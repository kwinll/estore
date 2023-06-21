package com.alezhang.estore.controller;

import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.controller.req.RemoveDiscountReq;
import com.alezhang.estore.service.IDiscountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class DiscountController {
    private static final String PATH = "/discount";
    @Resource
    private IDiscountService discountService;

    @PostMapping(PATH + "/add")
    public boolean addDiscount(AddDiscountReq addDiscountReq) {
        return discountService.addDiscount(addDiscountReq);
    }

    @PostMapping(PATH + "/remove")
    public boolean removeDiscount(RemoveDiscountReq removeDiscountReq) {
        return discountService.removeDiscount(removeDiscountReq.getProductId(), removeDiscountReq.getUid());
    }
}
