package com.alezhang.estore.controller;

import com.alezhang.estore.controller.req.AddProductReq;
import com.alezhang.estore.controller.req.RemoveProductReq;
import com.alezhang.estore.data.model.Product;
import com.alezhang.estore.service.IProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ProductController {
    private static final String PATH = "/product";
    @Resource
    private IProductService productService;

    @PostMapping(PATH + "/add")
    public boolean addProduct(AddProductReq addProductReq) {
        return productService.addProduct(addProductReq);
    }

    @PostMapping(PATH + "/remove")
    public boolean removeProduct(RemoveProductReq removeProductReq) {
        return productService.removeProduct(removeProductReq.getProductId(), removeProductReq.getUid());
    }

    @GetMapping(PATH + "/all")
    public List<Product> allProducts() {
        return productService.queryAllProducts();
    }
}
