package com.alezhang.estore.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "discount")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_id")
    private String productId;
    @Column(name = "strategy")
    private String strategy;
    @Column(name = "trigger_threshold")
    private int triggerThreshold;
    @Column(name = "discount_percentage")
    private int discountPercentage;
    @Column(name = "db_create_time", insertable = false, updatable = false)
    private Date dbCreateTime;
    @Column(name = "db_modify_time", insertable = false, updatable = false)
    private Date dbModifyTime;
}
