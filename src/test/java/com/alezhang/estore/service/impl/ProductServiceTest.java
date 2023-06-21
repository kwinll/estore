package com.alezhang.estore.service.impl;

import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.data.enumeration.ProductCurrency;
import com.alezhang.estore.data.model.Cart;
import com.alezhang.estore.data.model.Product;
import com.alezhang.estore.data.repository.CartRepository;
import com.alezhang.estore.data.repository.ProductRepository;
import com.alezhang.estore.service.IProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

import static com.alezhang.estore.service.impl.constant.EStoreTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = EstoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ProductServiceTest {
    @Resource
    private IProductService productService;
    @Mock
    private AddProductReq addProductReq1, addProductReq2;
    @Resource
    private ProductRepository productRepository;
    @Resource
    private CartRepository cartRepository;

    @BeforeEach
    public void init() {
        when(addProductReq1.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(addProductReq1.getPrice()).thenReturn(TEST_PRICE_1);
        when(addProductReq1.getProductName()).thenReturn(TEST_PRODUCT_NAME_1);
        when(addProductReq1.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);

        when(addProductReq2.getProductId()).thenReturn(TEST_PRODUCT_ID_2);
        when(addProductReq2.getPrice()).thenReturn(TEST_PRICE_2);
        when(addProductReq2.getProductName()).thenReturn(TEST_PRODUCT_NAME_2);
        when(addProductReq2.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);
    }

    @AfterEach
    public void cleanup() {
        productRepository.deleteAll();
    }


    @Test
    public void addProductWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(null),
                "Illegal parameter while adding a product");

    }

    @Test
    public void addProductWithNoPermission() {
        when(addProductReq1.getOperatorUid()).thenReturn(0L);
        assertThrows(RuntimeException.class,
                () -> productService.addProduct(addProductReq1),
                "Only admin can perform this action");
    }

    @Test
    public void addProduct() {
        productService.addProduct(addProductReq1);
        Product product = productService.queryProduct(TEST_PRODUCT_ID_1);
        assertNotNull(product.getId());
        assertEquals(TEST_PRODUCT_ID_1, product.getProductId());
        assertEquals(TEST_PRODUCT_NAME_1, product.getProductName());
        assertEquals(0, TEST_PRICE_1.compareTo(product.getPrice()));
        assertEquals(ProductCurrency.HKD.name(), product.getCurrency());
        assertEquals(ADMIN_ACCT_ID, product.getOperatorUid());
        assertNotNull(product.getDbCreateTime());
        assertNotNull(product.getDbModifyTime());
    }


    @Test
    public void queryAllProducts() {
        productService.addProduct(addProductReq1);
        List<Product> products = productService.queryAllProducts();
        assertEquals(1, products.size());

        productService.addProduct(addProductReq2);
        products = productService.queryAllProducts();
        assertEquals(2, products.size());
    }

    @Test
    public void removeProductWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.removeProduct(null, ADMIN_ACCT_ID),
                "Illegal product id while removing product");
    }

    @Test
    public void removeProductWithNoPermission() {
        assertThrows(RuntimeException.class,
                () -> productService.removeProduct(TEST_PRODUCT_ID_1, 0L),
                "Only admin can perform this action");
    }

    @Test
    public void removeProductWithCartItem() {
        productService.addProduct(addProductReq1);

        Cart cart = new Cart();
        cart.setProductId(TEST_PRODUCT_ID_1);
        cart.setUid(11111L);
        cart.setCount(2);
        cartRepository.save(cart);
        try {
            productService.removeProduct(TEST_PRODUCT_ID_1, ADMIN_ACCT_ID);
        } catch (Exception ex) {
            assertEquals("Cannot remove product as it's still in carts", ex.getMessage());
        }
        cartRepository.deleteAll();
    }

    @Test
    public void removeProduct() {
        productService.addProduct(addProductReq1);
        productService.addProduct(addProductReq2);
        productService.removeProduct(TEST_PRODUCT_ID_1, ADMIN_ACCT_ID);
        List<Product> products = productService.queryAllProducts();
        assertEquals(1, products.size());
        Product theProduct = products.get(0);
        assertEquals(TEST_PRODUCT_ID_2, theProduct.getProductId());
        productService.removeProduct(TEST_PRODUCT_ID_2, ADMIN_ACCT_ID);
        products = productService.queryAllProducts();
        assertEquals(0, products.size());
        productService.removeProduct(TEST_PRODUCT_ID_2, ADMIN_ACCT_ID);
    }
}
