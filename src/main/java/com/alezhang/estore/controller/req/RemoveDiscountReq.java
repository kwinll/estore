package com.alezhang.estore.controller.req;

import lombok.Data;

/**
 * Remove discount request
 */
@Data
public class RemoveDiscountReq {
    private String productId;
    private long uid;
}
