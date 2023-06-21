package com.alezhang.estore.data;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Long id;
    private String productId;
    private String productName;
    private String description;
    private String currency;
    private BigDecimal amount;
    private Long operatorUid;
    private Date dbCreateTime;
    private Date dbModifyTime;
}
