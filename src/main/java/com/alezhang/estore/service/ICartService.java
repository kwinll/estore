package com.alezhang.estore.service;

import com.alezhang.estore.controller.req.ModifyCartReq;
import com.alezhang.estore.controller.resp.CheckoutResp;
import com.alezhang.estore.data.model.Cart;

import java.util.List;

public interface ICartService {
    boolean modify(ModifyCartReq modifyCartReq);

    List<Cart> findAllByUserId(long uid);

    CheckoutResp checkout(long uid);
}
