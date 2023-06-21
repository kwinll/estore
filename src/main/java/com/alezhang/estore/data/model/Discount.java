package com.alezhang.estore.data;

import lombok.Data;

import java.util.Date;

@Data
public class Discount {
    private Long id;
    private String productId;
    private String strategy;
    private Integer triggerThreshold;
    private Integer discountPercentage;
    private Date dbCreateTime;
    private Date dbModifyTime;
}
