package com.alezhang.estore.controller.resp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutResp {
    private long uid;
    List<CheckoutDetail> checkoutDetails;
    private BigDecimal originalTotalPrice;
    private BigDecimal discount;
    private BigDecimal realTotalPrice;
}
