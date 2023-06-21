package com.alezhang.estore.service;

import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.data.model.Discount;

public interface IDiscountService {
    boolean addDiscount(AddDiscountReq addDiscountReq);

    boolean removeDiscount(String productId, long uid);

    Discount findDiscountByProductId(String productId);
}
