package com.alezhang.estore.service.impl;

import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.data.model.Discount;
import com.alezhang.estore.data.repository.CartRepository;
import com.alezhang.estore.data.repository.ProductRepository;
import com.alezhang.estore.service.IDiscountService;
import com.alezhang.estore.service.IProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Objects;

import static com.alezhang.estore.service.impl.constant.EStoreTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = EstoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class DiscountServiceTest {
    @Resource
    private IDiscountService discountService;
    @Resource
    private IProductService productService;
    @Resource
    private ProductRepository productRepository;
    @Resource
    private CartRepository cartRepository;

    @Mock
    private AddDiscountReq addDiscountReq;
    @Mock
    private AddProductReq addProductReq1;


    @BeforeEach
    public void init() {
        when(addDiscountReq.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(addDiscountReq.getDiscountStrategy()).thenReturn(TEST_DISCOUNT_STRATEGY);
        when(addDiscountReq.getDiscountPercentage()).thenReturn(TEST_DISCOUNT_PERCENTAGE);
        when(addDiscountReq.getTriggerThreshold()).thenReturn(TEST_TRIGGER_THRESHOLD);
        when(addDiscountReq.getUid()).thenReturn(ADMIN_ACCT_ID);

    }

    @AfterEach
    public void cleanup() {
        productRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    public void addDiscountWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> discountService.addDiscount(null),
                "Illegal argument while add discount");
        when(addDiscountReq.getDiscountPercentage()).thenReturn(101);
        assertThrows(IllegalArgumentException.class,
                () -> discountService.addDiscount(addDiscountReq),
                "Illegal argument while add discount");
    }

    @Test
    public void addDiscountWithNoPermission() {
        when(addDiscountReq.getUid()).thenReturn(RETAIL_ACCT_ID);
        assertThrows(RuntimeException.class,
                () -> discountService.addDiscount(addDiscountReq),
                "Only admin can perform this action");
    }

    @Test
    public void addDiscountWithProductNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> discountService.addDiscount(addDiscountReq)
                , "Illegal product id");
    }

    @Test
    public void addDiscount() {
        addProduct();
        boolean result = discountService.addDiscount(addDiscountReq);
        assertTrue(result);
        Discount discount = discountService.findDiscountByProductId(TEST_PRODUCT_ID_1);
        assertNotNull(discount);
        assertEquals(TEST_PRODUCT_ID_1, discount.getProductId());
        assertEquals(TEST_DISCOUNT_STRATEGY.name(), discount.getStrategy());
        assertEquals(TEST_TRIGGER_THRESHOLD, discount.getTriggerThreshold());
        assertEquals(TEST_DISCOUNT_PERCENTAGE, discount.getDiscountPercentage());
        assertNotNull(discount.getDbCreateTime());
        assertNotNull(discount.getDbModifyTime());
    }

    @Test
    public void addDuplicateDiscount() {
        addProduct();
        assertThrows(RuntimeException.class,
                () -> discountService.addDiscount(addDiscountReq),
                "Only one discount allowed for the same product");

    }

    @Test
    public void removeDiscountWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> discountService.removeDiscount("", ADMIN_ACCT_ID),
                "Product id required for removing discount");
    }

    @Test
    public void removeDiscountWithWrongPermission() {
        assertThrows(RuntimeException.class,
                () -> discountService.removeDiscount(TEST_PRODUCT_ID_1, RETAIL_ACCT_ID),
                "Only admin can perform this action");
    }

    @Test
    public void removeDiscountWithInValidProduct() {
        assertThrows(IllegalArgumentException.class,
                () -> discountService.removeDiscount(TEST_PRODUCT_ID_1, ADMIN_ACCT_ID),
                "Illegal product id");

    }

    @Test
    public void removeDiscount() {
        addProduct();
        discountService.addDiscount(addDiscountReq);
        discountService.removeDiscount(TEST_PRODUCT_ID_1, ADMIN_ACCT_ID);
        Discount discount = discountService.findDiscountByProductId(TEST_PRODUCT_ID_1);
        assertTrue(Objects.isNull(discount));

    }

    private void addProduct() {
        when(addProductReq1.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(addProductReq1.getPrice()).thenReturn(TEST_PRICE_1);
        when(addProductReq1.getProductName()).thenReturn(TEST_PRODUCT_NAME_1);
        when(addProductReq1.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);

        productService.addProduct(addProductReq1);
    }

}
