package com.alezhang.estore.service.impl;

import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.data.enumeration.ProductCurrency;
import com.alezhang.estore.data.model.Product;
import com.alezhang.estore.data.repository.CartRepository;
import com.alezhang.estore.data.repository.DiscountRepository;
import com.alezhang.estore.data.repository.ProductRepository;
import com.alezhang.estore.service.AbstractBaseService;
import com.alezhang.estore.service.IAccountService;
import com.alezhang.estore.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


/**
 * The product service implementation
 */
@Slf4j
@Service
public class ProductServiceImpl extends AbstractBaseService implements IProductService {
    @Resource
    private ProductRepository productRepository;
    @Resource
    private CartRepository cartRepository;
    @Resource
    private IAccountService accountService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private DiscountRepository discountRepository;


    /**
     * Add a new product per {@link AddProductReq}, could only be invoked by admin
     * <tt>Attention</tt>, admin user must provide product id(unique identifier), product name and amount
     * At present, the currency of the product would be default to {@link ProductCurrency#HKD}
     *
     * @param addProductReq, see {@link AddProductReq}
     * @return true if adding product successfully, otherwise an exception would be thrown accordingly
     */
    @Override
    public boolean addProduct(AddProductReq addProductReq) {
        if (Objects.isNull(addProductReq) || StringUtils.isAnyBlank(addProductReq.getProductId(), addProductReq.getProductName())
                || Objects.isNull(addProductReq.getPrice()) || addProductReq.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Illegal parameters: {} while adding product", addProductReq);
            throw new IllegalArgumentException("Illegal parameter while adding a product");
        }
        checkPermission(addProductReq.getOperatorUid());
        Product product = assembleProduct(addProductReq);
        log.info("Going to save product: {}", product);
        productRepository.save(product);
        return true;
    }


    /**
     * Assemble product based on {@link AddProductReq}
     */
    private Product assembleProduct(AddProductReq addProductReq) {
        Product product = new Product();
        product.setProductId(addProductReq.getProductId());
        product.setProductName(addProductReq.getProductName());
        product.setDescription(addProductReq.getDescription());
        product.setCurrency(ProductCurrency.defaultCCY().name());
        product.setPrice(addProductReq.getPrice());
        product.setOperatorUid(addProductReq.getOperatorUid());
        return product;
    }

    /**
     * Remove a product from current inventory, this action could only be triggered by admin
     * If in any carts of retail user, the product could not be removed
     *
     * @param productId the unique product identifier
     * @return true if removing product successfully, otherwise an exception would be thrown
     */
    @Override
    public boolean removeProduct(String productId, long uid) {
        if (StringUtils.isBlank(productId)) {
            log.warn("Empty product id while removing product");
            throw new IllegalArgumentException("Illegal product id while removing product");
        }
        checkPermission(uid);
        long count = cartRepository.countAllByProductId(productId);
        if (count > 0L) {
            log.warn("The product still exists in carts, could not be deleted");
            throw new RuntimeException("Cannot remove product as it's still in carts");
        }
        //will delete both product and its discount
        transactionTemplate.execute(status -> {
            productRepository.deleteByProductId(productId);
            discountRepository.deleteByProductId(productId);
            return true;
        });

        return true;
    }


    /**
     * Find product by product id
     *
     * @param productId the product id
     * @return an instance of {@link Product}
     */
    @Override
    public Product queryProduct(String productId) {
        return productRepository.findByProductId(productId);
    }

    /**
     * Query all products
     *
     * @return a list of {@link Product}
     */
    @Override
    public List<Product> queryAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Check whether the product exist or not, otherwise an exception would be thrown
     *
     * @param productId the product id
     */
    @Override
    public void checkProductExists(String productId) {
        if (Objects.isNull(queryProduct(productId))) {
            throw new IllegalArgumentException("Illegal product id");
        }
    }


    /**
     * Check a user is admin or not to perform any actions on product
     *
     * @param uid the user id
     */
    public void checkPermission(long uid) {
        boolean isAdmin = accountService.isAdmin(uid);
        if (!isAdmin) {
            log.error("User [{}] is not the admin for product operations", uid);
            throw new RuntimeException("Only admin can perform this action");
        }
    }
}
