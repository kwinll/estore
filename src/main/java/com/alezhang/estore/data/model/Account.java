package com.alezhang.estore.data;

import lombok.Data;

import java.util.Date;

@Data
public class Account {
    private Long id;
    private Long uid;
    private String accountType;
    private Date dbCreateTime;
    private Date dbModifyTime;
}
