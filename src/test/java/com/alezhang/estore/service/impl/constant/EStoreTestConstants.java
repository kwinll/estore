package com.alezhang.estore.service.impl.constant;

import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.data.enumeration.DiscountStrategy;

import java.math.BigDecimal;

public final class EStoreTestConstants {
    private EStoreTestConstants(){

    }

    public static final Long ADMIN_ACCT_ID = 100001L;
    public static final Long RETAIL_ACCT_ID = 100002L;

    public static final String TEST_PRODUCT_ID_1 = "ALEX12345678";
    public static final String TEST_PRODUCT_ID_2 = "ALEX12345679";

    public static final BigDecimal TEST_PRICE_1 = BigDecimal.TEN;
    public static final BigDecimal TEST_PRICE_2 = BigDecimal.ONE;
    public static final String TEST_PRODUCT_NAME_1 = "Alex's Coin";
    public static final String TEST_PRODUCT_NAME_2 = "Zhang's Coin";

    public static final DiscountStrategy TEST_DISCOUNT_STRATEGY = DiscountStrategy.BUY_N_GET_LAST_DISCOUNT;
    public static final int TEST_TRIGGER_THRESHOLD = 2;
    public static final int TEST_DISCOUNT_PERCENTAGE = 45;
}
