package com.alezhang.estore.data.enumeration;


/**
 * The discount strategy
 */
public enum DiscountStrategy {
    BUY_N_GET_LAST_DISCOUNT(DiscountStrategyScope.SINGLE, "BUY_N_GET_LAST_DISCOUNT"),
    BUY_N_GET_TOTAL_DISCOUNT(DiscountStrategyScope.SINGLE, "BUY_N_GET_TOTAL_DISCOUNT"),
    BUY_N_GET_STH_FREE(DiscountStrategyScope.ALL, "BUY_N_GET_STH_FREE"),

    BUY_SINGLE_N_GET_STH_FREE(DiscountStrategyScope.HYBRID, "BUY_SINGLE_N_GET_STH_FREE")
    ;
    private DiscountStrategyScope discountStrategyScope;
    private String strategyName;


    DiscountStrategy(DiscountStrategyScope discountStrategyScope, String strategyName) {
        this.discountStrategyScope = discountStrategyScope;
    }

}
