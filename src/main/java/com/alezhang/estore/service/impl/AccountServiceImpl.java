package com.alezhang.estore.service.impl;

import com.alezhang.estore.data.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AccountService {
    @Resource
    private AccountRepository accountRepository;

}
