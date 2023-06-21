package com.alezhang.estore.service;

import com.alezhang.estore.data.model.Account;

import java.util.Optional;

public interface IAccountService {

    Optional<Account> findAccountByUid(long uid);

    boolean isAdmin(long uid);

    boolean isRetailUser(long uid);
}
