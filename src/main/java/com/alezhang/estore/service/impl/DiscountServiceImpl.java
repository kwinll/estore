package com.alezhang.estore.service.impl;

import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.data.model.Discount;
import com.alezhang.estore.data.repository.DiscountRepository;
import com.alezhang.estore.service.AbstractBaseService;
import com.alezhang.estore.service.IAccountService;
import com.alezhang.estore.service.IDiscountService;
import com.alezhang.estore.service.IProductService;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Objects;


/**
 * The discount service implementation
 */
@Slf4j
@Service
public class DiscountServiceImpl extends AbstractBaseService implements IDiscountService {
    @Resource
    private DiscountRepository discountRepository;
    @Resource
    private IProductService productService;
    @Resource
    private IAccountService accountService;
    @Resource
    private TransactionTemplate transactionTemplate;

    private static final Range<Integer> VALID_DISCOUNT_PERCENTAGE = Range.openClosed(0, 100);


    /**
     * Add discount for product
     * At present, for each product, only one discount could be applied to avoid discount conflicts on the same product
     * <tt>Attention</tt>, only the admin is able to add discount
     * For any adding request, product, and strategy related info must be provided
     *
     * @param addDiscountReq see {@link AddDiscountReq}
     * @return true if add discount event successfully, otherwise an exception would be thrown accordingly
     */
    @Override
    public boolean addDiscount(AddDiscountReq addDiscountReq) {
        if (Objects.isNull(addDiscountReq) || StringUtils.isBlank(addDiscountReq.getProductId())
                || Objects.isNull(addDiscountReq.getDiscountStrategy()) || addDiscountReq.getTriggerThreshold() <= 0
                || !VALID_DISCOUNT_PERCENTAGE.contains(addDiscountReq.getDiscountPercentage())) {
            log.warn("Illegal parameter for add discount request: {}", addDiscountReq);
            throw new IllegalArgumentException("Illegal argument while add discount");
        }
        checkPermission(addDiscountReq.getUid());
        productService.checkProductExists(addDiscountReq.getProductId());

        if (Objects.nonNull(discountRepository.findByProductId(addDiscountReq.getProductId()))) {
            throw new RuntimeException("Only one discount allowed for the same product");
        }
        Discount discount = assembleDiscount(addDiscountReq);
        log.info("Going to save discount: {}", discount);
        discountRepository.save(discount);
        return true;
    }

    /**
     * Assemble {@link Discount} based on {@link AddDiscountReq}
     */
    private Discount assembleDiscount(AddDiscountReq addDiscountReq) {
        Discount discount = new Discount();
        discount.setProductId(addDiscountReq.getProductId());
        discount.setStrategy(addDiscountReq.getDiscountStrategy().name());
        discount.setDiscountPercentage(addDiscountReq.getDiscountPercentage());
        discount.setTriggerThreshold(addDiscountReq.getTriggerThreshold());
        return discount;
    }

    /**
     * Remove discount on the product
     */
    @Override
    public boolean removeDiscount(String productId, long uid) {
        if (StringUtils.isBlank(productId)) {
            log.warn("Blank product id");
            throw new IllegalArgumentException("Product id required for removing discount");
        }
        checkPermission(uid);
        productService.checkProductExists(productId);
        transactionTemplate.execute(status -> {
            discountRepository.deleteByProductId(productId);
            return true;
        });
        return true;
    }


    /**
     * Find discount by product id
     *
     * @param productId the product id
     * @return discount object if exists
     */
    @Override
    public Discount findDiscountByProductId(String productId) {
        return discountRepository.findByProductId(productId);
    }

    /**
     * Check a user is admin or not to perform any actions on product
     *
     * @param uid the user id
     */
    public void checkPermission(long uid) {
        boolean isAdmin = accountService.isAdmin(uid);
        if (!isAdmin) {
            log.error("User [{}] is not the admin for discount operations", uid);
            throw new RuntimeException("Only admin can perform this action");
        }
    }
}
