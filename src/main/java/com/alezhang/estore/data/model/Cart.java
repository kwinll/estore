package com.alezhang.estore.data;

import lombok.Data;

import java.util.Date;

@Data
public class Cart {
    private Long id;
    private Long uid;
    private String productId;
    private Integer count;
    private Date dbCreateTime;
    private Date dbModifyTime;
}
