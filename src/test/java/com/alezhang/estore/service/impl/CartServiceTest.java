package com.alezhang.estore.service.impl;

import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.controller.req.ModifyCartReq;
import com.alezhang.estore.controller.resp.CheckoutResp;
import com.alezhang.estore.data.enumeration.DiscountStrategy;
import com.alezhang.estore.data.model.Cart;
import com.alezhang.estore.data.repository.CartRepository;
import com.alezhang.estore.data.repository.DiscountRepository;
import com.alezhang.estore.data.repository.ProductRepository;
import com.alezhang.estore.service.ICartService;
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
import java.math.BigDecimal;
import java.util.List;

import static com.alezhang.estore.service.impl.constant.EStoreTestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = EstoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CartServiceTest {
    @Resource
    private ICartService cartService;
    @Resource
    private IProductService productService;
    @Resource
    private IDiscountService discountService;
    @Resource
    private CartRepository cartRepository;
    @Resource
    private ProductRepository productRepository;
    @Resource
    private DiscountRepository discountRepository;
    @Mock
    private ModifyCartReq modifyCartReq1, modifyCartReq2;
    @Mock
    private AddProductReq addProductReq1, addProductReq2, addProductReq3;
    @Mock
    private AddDiscountReq addDiscountReq1, addDiscountReq2, addDiscountReq3;


    @BeforeEach
    public void init() {
        when(modifyCartReq1.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(modifyCartReq1.getCount()).thenReturn(2);
        when(modifyCartReq1.getUid()).thenReturn(RETAIL_ACCT_ID);

        when(modifyCartReq2.getProductId()).thenReturn(TEST_PRODUCT_ID_2);
        when(modifyCartReq2.getCount()).thenReturn(3);
        when(modifyCartReq2.getUid()).thenReturn(RETAIL_ACCT_ID);
    }

    @AfterEach
    public void cleanup() {
        cartRepository.deleteAll();
        productRepository.deleteAll();
        discountRepository.deleteAll();
    }

    @Test
    public void addCartWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.modify(null),
                "Illegal argument while add/remove products into cart");
    }

    @Test
    public void addCartWithWrongPermission() {
        when(modifyCartReq1.getUid()).thenReturn(ADMIN_ACCT_ID);
        assertThrows(RuntimeException.class,
                () -> cartService.modify(modifyCartReq1), "Only retail user can perform this action");
    }

    @Test
    public void addCartWithInvalidCount() {
        addProduct();
        when(modifyCartReq1.getCount()).thenReturn(0);
        assertThrows(RuntimeException.class,
                () -> cartService.modify(modifyCartReq1),
                "Cannot remove product from cart since it's not in cart");

    }


    @Test
    public void addCart() {
        addProduct();
        cartService.modify(modifyCartReq1);
        cartService.modify(modifyCartReq2);
        List<Cart> cartList = cartService.findAllByUserId(RETAIL_ACCT_ID);
        assertEquals(2, cartList.size());
        cartList.forEach(cart -> {
            if (cart.getProductId().equals(TEST_PRODUCT_ID_1)) {
                assertEquals(2, cart.getCount());

            } else if (cart.getProductId().equals(TEST_PRODUCT_ID_2)) {
                assertEquals(3, cart.getCount());
            }
        });

        when(modifyCartReq1.getCount()).thenReturn(5);
        cartService.modify(modifyCartReq1);
        cartList = cartService.findAllByUserId(RETAIL_ACCT_ID);
        cartList.forEach(cart -> {
            if (cart.getProductId().equals(TEST_PRODUCT_ID_1)) {
                assertEquals(5, cart.getCount());

            } else if (cart.getProductId().equals(TEST_PRODUCT_ID_2)) {
                assertEquals(3, cart.getCount());
            }
        });
        when(modifyCartReq2.getCount()).thenReturn(0);
        cartService.modify(modifyCartReq2);
        cartList = cartService.findAllByUserId(RETAIL_ACCT_ID);
        assertEquals(1, cartList.size());
        Cart cartToBeTested = cartList.get(0);
        assertEquals(TEST_PRODUCT_ID_1, cartToBeTested.getProductId());
        assertEquals(5, cartToBeTested.getCount());
    }

    @Test
    public void checkoutWithIllegalParam() {
        assertThrows(IllegalArgumentException.class,
                () -> cartService.checkout(-1),
                "Illegal user id while checking out");
    }

    @Test
    public void checkoutWithWrongPermission() {
        assertThrows(RuntimeException.class,
                () -> cartService.checkout(ADMIN_ACCT_ID),
                "Only retail user can perform this action");
    }

    @Test
    public void checkout1() {
        when(modifyCartReq1.getCount()).thenReturn(2);
        when(modifyCartReq2.getCount()).thenReturn(3);
        addProduct();
        addDiscount();
        cartService.modify(modifyCartReq1);
        cartService.modify(modifyCartReq2);
        CheckoutResp checkoutResp = cartService.checkout(RETAIL_ACCT_ID);
        assertNotNull(checkoutResp);
        assertEquals(0, checkoutResp.getDiscount().compareTo(new BigDecimal("6.5")));
        assertEquals(0, checkoutResp.getRealTotalPrice().compareTo(new BigDecimal("16.5")));
        checkoutResp.getCheckoutDetails().forEach(checkoutDetail ->
        {
            if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_1)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("5")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("15")));

            } else if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_2)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("1.5")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("1.5")));
            }
        });
    }

    @Test
    public void checkoutNew() {
        when(modifyCartReq1.getCount()).thenReturn(2);
        when(modifyCartReq2.getCount()).thenReturn(3);
        addProduct();
        addDiscount();
        cartService.modify(modifyCartReq1);
        cartService.modify(modifyCartReq2);
        CheckoutResp checkoutResp = cartService.checkout(RETAIL_ACCT_ID);
        assertNotNull(checkoutResp);
        assertEquals(0, checkoutResp.getDiscount().compareTo(new BigDecimal("6.5")));
        assertEquals(0, checkoutResp.getRealTotalPrice().compareTo(new BigDecimal("16.5")));
        checkoutResp.getCheckoutDetails().forEach(checkoutDetail ->
        {
            if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_1)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("5")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("15")));

            } else if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_2)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("1.5")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("1.5")));
            }
        });
    }


    @Test
    public void checkout2() {
        when(modifyCartReq1.getCount()).thenReturn(2);
        when(modifyCartReq2.getCount()).thenReturn(1);
        addProduct();
        addDiscount();
        cartService.modify(modifyCartReq1);
        cartService.modify(modifyCartReq2);
        CheckoutResp checkoutResp = cartService.checkout(RETAIL_ACCT_ID);
        assertNotNull(checkoutResp);
        assertEquals(0, checkoutResp.getDiscount().compareTo(new BigDecimal("5")));
        assertEquals(0, checkoutResp.getRealTotalPrice().compareTo(new BigDecimal("16")));
        checkoutResp.getCheckoutDetails().forEach(checkoutDetail ->
        {
            if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_1)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("5")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("15")));

            } else if (checkoutDetail.getProductId().equals(TEST_PRODUCT_ID_2)) {
                assertEquals(0, checkoutDetail.getDiscount().compareTo(new BigDecimal("0")));
                assertEquals(0, checkoutDetail.getRealTotalPrice().compareTo(new BigDecimal("1")));
            }
        });
    }

    private void addProduct() {
        when(addProductReq1.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(addProductReq1.getPrice()).thenReturn(TEST_PRICE_1);
        when(addProductReq1.getProductName()).thenReturn(TEST_PRODUCT_NAME_1);
        when(addProductReq1.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);

        productService.addProduct(addProductReq1);

        when(addProductReq2.getProductId()).thenReturn(TEST_PRODUCT_ID_2);
        when(addProductReq2.getPrice()).thenReturn(TEST_PRICE_2);
        when(addProductReq2.getProductName()).thenReturn(TEST_PRODUCT_NAME_2);
        when(addProductReq2.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);

        productService.addProduct(addProductReq2);

        when(addProductReq3.getProductId()).thenReturn(TEST_PRODUCT_ID_3);
        when(addProductReq3.getPrice()).thenReturn(TEST_PRICE_3);
        when(addProductReq3.getProductName()).thenReturn(TEST_PRODUCT_NAME_3);
        when(addProductReq3.getOperatorUid()).thenReturn(ADMIN_ACCT_ID);

        productService.addProduct(addProductReq3);
    }

    public void addDiscount() {
        when(addDiscountReq1.getProductId()).thenReturn(TEST_PRODUCT_ID_1);
        when(addDiscountReq1.getDiscountStrategy()).thenReturn(DiscountStrategy.BUY_N_GET_LAST_DISCOUNT);
        when(addDiscountReq1.getDiscountPercentage()).thenReturn(50);
        when(addDiscountReq1.getTriggerThreshold()).thenReturn(2);
        when(addDiscountReq1.getUid()).thenReturn(ADMIN_ACCT_ID);

        discountService.addDiscount(addDiscountReq1);

        when(addDiscountReq2.getProductId()).thenReturn(TEST_PRODUCT_ID_2);
        when(addDiscountReq2.getDiscountStrategy()).thenReturn(DiscountStrategy.BUY_N_GET_TOTAL_DISCOUNT);
        when(addDiscountReq2.getDiscountPercentage()).thenReturn(50);
        when(addDiscountReq2.getTriggerThreshold()).thenReturn(3);
        when(addDiscountReq2.getUid()).thenReturn(ADMIN_ACCT_ID);

        discountService.addDiscount(addDiscountReq2);

        when(addDiscountReq3.getProductId()).thenReturn(TEST_PRODUCT_ID_3);
        when(addDiscountReq3.getDiscountStrategy()).thenReturn(DiscountStrategy.BUY_N_GET_STH_FREE);
        when(addDiscountReq3.getDiscountPercentage()).thenReturn(50);
        when(addDiscountReq3.getTriggerThreshold()).thenReturn(TEST_BUY_N_GET_STH_FREE_THRESHOLD);
        when(addDiscountReq3.getUid()).thenReturn(ADMIN_ACCT_ID);

        discountService.addDiscount(addDiscountReq3);
    }

}
