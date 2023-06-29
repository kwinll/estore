package com.alezhang.estore.service;

import com.alezhang.estore.controller.req.AddDiscountReq;
import com.alezhang.estore.data.model.Discount;

import java.util.List;

public interface IDiscountService {
    boolean addDiscount(AddDiscountReq addDiscountReq);

    boolean removeDiscount(String productId, long uid);

    Discount findDiscountByProductId(String productId);

    Discount findBuyNGetSthFree();


    List<Discount> findHybrids();
}
