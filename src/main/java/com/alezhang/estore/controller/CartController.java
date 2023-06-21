package com.alezhang.estore.controller;

import com.alezhang.estore.controller.req.ModifyCartReq;
import com.alezhang.estore.controller.resp.CheckoutResp;
import com.alezhang.estore.data.model.Cart;
import com.alezhang.estore.service.ICartService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CartController {
    private final static String PATH = "/cart";
    @Resource
    private ICartService cartService;

    @PostMapping(PATH + "/modify")
    public boolean modify(ModifyCartReq modifyCartReq) {
        return cartService.modify(modifyCartReq);
    }

    @GetMapping(PATH + "/checkout")
    public CheckoutResp checkout(@Param("uid") long uid) {
        return cartService.checkout(uid);
    }

    @GetMapping(PATH + "/list")
    public List<Cart> listAllItem(@Param("uid") long uid) {
        return cartService.findAllByUserId(uid);
    }
}
