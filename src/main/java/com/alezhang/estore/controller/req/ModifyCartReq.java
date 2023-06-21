package com.alezhang.estore.controller.req;

import lombok.Data;


/**
 * Modify cart request
 * */
@Data
public class ModifyCartReq {
    private Long uid;
    private String productId;
    private int count; // if count is 0 means delete, otherwise upsert the cart item
}
