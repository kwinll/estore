package com.alezhang.estore.controller.resp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutDetail {
    private String productId;
    private String productName;
    private Integer count;
    private BigDecimal originalTotalPrice;
    private BigDecimal discount;
    private BigDecimal realTotalPrice;
}
