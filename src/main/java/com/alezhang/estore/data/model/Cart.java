package com.alezhang.estore.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uid")
    private Long uid;
    @Column(name = "product_id")
    private String productId;
    @Column(name = "count")
    private int count;
    @Column(name = "db_create_time", insertable = false, updatable = false)
    private Date dbCreateTime;
    @Column(name = "db_modify_time", insertable = false, updatable = false)
    private Date dbModifyTime;
}
