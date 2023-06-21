package com.alezhang.estore.controller.req;

import lombok.Data;

/**
 * Remove product request
 */
@Data
public class RemoveProductReq {
    private String productId;
    private long uid;
}
