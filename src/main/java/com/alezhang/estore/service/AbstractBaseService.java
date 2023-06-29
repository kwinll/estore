package com.alezhang.estore.service;

import com.alezhang.estore.data.model.Discount;

import java.util.List;

public abstract class AbstractBaseService {
    public abstract void checkPermission(long uid);

}
