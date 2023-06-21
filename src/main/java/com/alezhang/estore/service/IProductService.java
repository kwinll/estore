package com.alezhang.estore.service;

import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.data.model.Product;

import java.util.List;

public interface IProductService {

    boolean addProduct(AddProductReq addProductReq);

    boolean removeProduct(String productId, long uid);

    Product queryProduct(String productId);

    List<Product> queryAllProducts();

    void checkProductExists(String productId);
}
