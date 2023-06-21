package com.alezhang.estore.controller.req;

import lombok.Data;

import java.math.BigDecimal;


/**
 * The add product request
 * No need to specify currency as HKD is set as default
 */
@Data
public class AddProductReq {
    private String productId;
    private String productName;
    private BigDecimal price;
    private long operatorUid;
    private String description;
}
