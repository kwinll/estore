package com.alezhang.estore.data.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uid", unique = true)
    private Long uid;
    @Column(name = "account_type")
    private String accountType;
    @Column(name = "db_create_time", insertable = false, updatable = false)
    private Date dbCreateTime;
    @Column(name = "db_modify_time", insertable = false, updatable = false)
    private Date dbModifyTime;
}
