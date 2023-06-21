package com.alezhang.estore.service;

import com.alezhang.estore.data.model.Account;

import java.util.Optional;

public interface IAccountService {

    /**
     * Find account by user id
     *
     * @param uid the user id
     * @return an optional instance of {@link Account}
     */
    Optional<Account> findAccountByUid(long uid);

    /**
     * Check whether a user is admin or not
     *
     * @param uid the user id
     * @return true if the user is admin, otherwise returning false
     */
    boolean isAdmin(long uid);

    /**
     * Check whether a user is retail user or not
     *
     * @param uid the user id
     * @return true if the user is retail user, otherwise returning false
     */
    boolean isRetailUser(long uid);
}
