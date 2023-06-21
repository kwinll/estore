package com.alezhang.estore.controller.req;


import com.alezhang.estore.data.enumeration.DiscountStrategy;
import lombok.Data;

/**
 * The request for add discount
 */
@Data
public class AddDiscountReq {
    private String productId;
    private DiscountStrategy discountStrategy;
    private int triggerThreshold;
    private int discountPercentage;
    private long uid;
}
