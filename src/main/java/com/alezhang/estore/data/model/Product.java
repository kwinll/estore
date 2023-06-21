package com.alezhang.estore.data.model;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id")
    private String productId;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "description")
    private String description;
    @Column(name = "currency")
    private String currency;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "operator_uid")
    private long operatorUid;
    @Column(name = "db_create_time", insertable = false, updatable = false)
    private Date dbCreateTime;
    @Column(name = "db_modify_time", insertable = false, updatable = false)
    private Date dbModifyTime;
}
