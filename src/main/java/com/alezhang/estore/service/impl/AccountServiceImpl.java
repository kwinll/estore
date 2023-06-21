package com.alezhang.estore.service.impl;

import com.alezhang.estore.data.enumeration.AccountType;
import com.alezhang.estore.data.model.Account;
import com.alezhang.estore.data.repository.AccountRepository;
import com.alezhang.estore.service.IAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@Service
public class AccountServiceImpl implements IAccountService {
    @Resource
    private AccountRepository accountRepository;
    private static final long INVALID_ACCT_ID_BOUND = 0L;

    @Override
    public Optional<Account> findAccountByUid(long uid) {
        //uid cannot be empty and always a positive number
        if (uid <= INVALID_ACCT_ID_BOUND) {
            return Optional.empty();
        }
        return Optional.ofNullable(accountRepository.findAccountByUid(uid));
    }

    @Override
    public boolean isAdmin(long uid) {
        Optional<Account> acctOptional = findAccountByUid(uid);
        return acctOptional.filter(account -> AccountType.ADMIN.name().equals(account.getAccountType())).isPresent();
    }

    @Override
    public boolean isRetailUser(long uid) {
        Optional<Account> acctOptional = findAccountByUid(uid);
        return acctOptional.filter(account -> AccountType.RETAIL.name().equals(account.getAccountType())).isPresent();
    }
}
