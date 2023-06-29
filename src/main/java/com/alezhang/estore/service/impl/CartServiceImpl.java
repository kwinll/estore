package com.alezhang.estore.service.impl;

import com.alezhang.estore.controller.req.ModifyCartReq;
import com.alezhang.estore.controller.resp.CheckoutDetail;
import com.alezhang.estore.controller.resp.CheckoutResp;
import com.alezhang.estore.data.enumeration.DiscountStrategy;
import com.alezhang.estore.data.model.Cart;
import com.alezhang.estore.data.model.Discount;
import com.alezhang.estore.data.model.Product;
import com.alezhang.estore.data.repository.CartRepository;
import com.alezhang.estore.service.*;
import com.alezhang.estore.util.EStoreLockUtils;
import com.alezhang.estore.util.IEStoreLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl extends AbstractBaseService implements ICartService {
    @Resource
    private CartRepository cartRepository;
    @Resource
    private IAccountService accountService;
    @Resource
    private IProductService productService;
    @Resource
    private IDiscountService discountService;

    private static final IEStoreLock<String> CART_LOCK = new EStoreLockUtils<>();

    private static final BigDecimal PERCENTAGE_DENOMINATOR = new BigDecimal("100");


    /**
     * Add/remove product into the cart
     * While modify the cart, a lock would be added at uid+product id level to avoid race condition
     * If the count == 0, means delete the product from users cart. Otherwise, will perform an upsert
     *
     * @param modifyCartReq see{@link ModifyCartReq}
     * @return true if modify cart successfully, otherwise an exception would be thrown
     */
    @Override
    public boolean modify(ModifyCartReq modifyCartReq) {
        if (Objects.isNull(modifyCartReq) || modifyCartReq.getUid() <= 0L
                || modifyCartReq.getCount() < 0 || StringUtils.isBlank(modifyCartReq.getProductId())) {
            log.warn("Illegal argument: {} while add/remove products into cart", modifyCartReq);
            throw new IllegalArgumentException("Illegal argument while add/remove products into cart");
        }
        checkPermission(modifyCartReq.getUid());
        productService.checkProductExists(modifyCartReq.getProductId());
        String lockKey = modifyCartReq.getUid() + modifyCartReq.getProductId();
        CART_LOCK.lock(lockKey);
        try {
            Optional<Cart> cartOptional = findByUidAndProductId(modifyCartReq.getUid(), modifyCartReq.getProductId());
            if (cartOptional.isPresent() && modifyCartReq.getCount() == 0) {
                log.info("Perform cart deletion for request: {}", modifyCartReq);
                cartRepository.deleteById(cartOptional.get().getId());
            } else if (modifyCartReq.getCount() != 0) {
                log.info("Perform add or update for cart request: {}", modifyCartReq);
                Cart cart = assembleCart(modifyCartReq, cartOptional);
                cartRepository.save(cart);
            } else {
                throw new RuntimeException("Cannot remove product from cart since it's not in cart");
            }

        } finally {
            CART_LOCK.unlock(lockKey);
        }
        return true;
    }


    /**
     * Assemble cart based on {@link ModifyCartReq}
     */
    private Cart assembleCart(ModifyCartReq modifyCartReq, Optional<Cart> cartOptional) {
        Cart cart = new Cart();
        cart.setUid(modifyCartReq.getUid());
        cart.setProductId(modifyCartReq.getProductId());
        cart.setCount(modifyCartReq.getCount());
        cartOptional.ifPresent(entry -> cart.setId(entry.getId()));
        return cart;
    }


    /**
     * Find cart by user id and product id
     *
     * @param uid       the user id
     * @param productId the product id
     * @return an optional object of cart
     */
    public Optional<Cart> findByUidAndProductId(long uid, String productId) {
        if (uid <= 0L || StringUtils.isBlank(productId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(cartRepository.findByUidAndProductId(uid, productId));
    }

    /**
     * Find all cart items by user id
     *
     * @param uid the user id
     * @return a collection of {@link Cart}
     */
    @Override
    public List<Cart> findAllByUserId(long uid) {
        return cartRepository.findAllByUid(uid);
    }


    /**
     * Check out all cart items and calculate price for every product(discount is also taken into consideration)
     * Total number would be summed up based on all cart item.
     * Only retail user can use the interface
     *
     * @param uid the user id
     * @return see {@link CheckoutResp}. Otherwise, an exception would be thrown if any error happens
     */
    @Override
    public CheckoutResp checkout(long uid) {
        if (uid <= 0L) {
            throw new IllegalArgumentException("Illegal user id while checking out");
        }
        checkPermission(uid);
        List<Cart> cartList = cartRepository.findAllByUid(uid);
        List<CheckoutDetail> checkoutDetails = cartList.stream().map(this::mapToCheckoutDetail)
                .filter(Objects::nonNull).collect(Collectors.toList());

        CheckoutResp checkoutResp = new CheckoutResp();
        checkoutResp.setUid(uid);
        if (!CollectionUtils.isEmpty(checkoutDetails)) {
            BigDecimal originalTotalPrice = checkoutDetails.stream().map(CheckoutDetail::getOriginalTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discount = checkoutDetails.stream().map(CheckoutDetail::getDiscount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal realTotalPrice = checkoutDetails.stream().map(CheckoutDetail::getRealTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            checkoutResp.setCheckoutDetails(checkoutDetails);
            checkoutResp.setOriginalTotalPrice(originalTotalPrice);
            checkoutResp.setRealTotalPrice(realTotalPrice);
            checkoutResp.setDiscount(discount);
        }
        return checkoutResp;
    }


    /**
     * Map and calculate cart item into {@link CheckoutDetail}
     */
    private CheckoutDetail mapToCheckoutDetail(Cart cart) {

        Product product = productService.queryProduct(cart.getProductId());
        if (Objects.isNull(product)) {
            return null;
        }

        CheckoutDetail checkoutDetail = new CheckoutDetail();
        checkoutDetail.setProductId(product.getProductId());
        checkoutDetail.setProductName(product.getProductName());
        checkoutDetail.setCount(cart.getCount());
        BigDecimal originalTotalPrice = product.getPrice().multiply(new BigDecimal(cart.getCount()));
        BigDecimal discountAmount = BigDecimal.ZERO;

        Discount discount = discountService.findDiscountByProductId(checkoutDetail.getProductId());
        if (Objects.nonNull(discount) && cart.getCount() >= discount.getTriggerThreshold()) {
            if (DiscountStrategy.BUY_N_GET_LAST_DISCOUNT.name().equals(discount.getStrategy())) {
                // if 100 percent off for the last one -> product price * 100.100
                // if 70* percent off for the last one -> product price * 70/100
                discountAmount = product.getPrice().multiply(
                        BigDecimal.valueOf(discount.getDiscountPercentage()).divide(PERCENTAGE_DENOMINATOR, 8,
                                RoundingMode.HALF_UP));
            } else {
                discountAmount = originalTotalPrice.multiply(
                        BigDecimal.valueOf(discount.getDiscountPercentage()).divide(PERCENTAGE_DENOMINATOR, 8,
                                RoundingMode.HALF_UP));

            }

        }
        BigDecimal realTotalPrice = originalTotalPrice.subtract(discountAmount);
        checkoutDetail.setOriginalTotalPrice(originalTotalPrice);
        checkoutDetail.setDiscount(discountAmount);
        checkoutDetail.setRealTotalPrice(realTotalPrice);
        return checkoutDetail;
    }


    public void checkPermission(long uid) {
        boolean isRetailUser = accountService.isRetailUser(uid);
        if (!isRetailUser) {
            log.error("User [{}] is not the retail user for cart operations", uid);
            throw new RuntimeException("Only retail user can perform this action");
        }
    }
}
