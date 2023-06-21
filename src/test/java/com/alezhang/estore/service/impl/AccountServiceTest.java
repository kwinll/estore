package com.alezhang.estore.service.impl;

import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.data.enumeration.AccountType;
import com.alezhang.estore.data.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.Optional;

import static com.alezhang.estore.service.impl.constant.EStoreTestConstants.ADMIN_ACCT_ID;
import static com.alezhang.estore.service.impl.constant.EStoreTestConstants.RETAIL_ACCT_ID;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = EstoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class AccountServiceTest {
    @Resource
    private AccountServiceImpl accountService;


    @Test
    public void findAccountByUid() {

        Optional<Account> adminAcct = accountService.findAccountByUid(ADMIN_ACCT_ID);
        assertTrue(adminAcct.isPresent());
        assertEquals(ADMIN_ACCT_ID, adminAcct.get().getUid());
        assertEquals(AccountType.ADMIN.name(), adminAcct.get().getAccountType());

        Optional<Account> retailAcct = accountService.findAccountByUid(RETAIL_ACCT_ID);
        assertTrue(retailAcct.isPresent());
        assertEquals(RETAIL_ACCT_ID, retailAcct.get().getUid());
        assertEquals(AccountType.RETAIL.name(), retailAcct.get().getAccountType());

        Optional<Account> invalidAcct = accountService.findAccountByUid(1L);
        assertTrue(invalidAcct.isEmpty());
    }

    @Test
    public void isAdmin() {
        assertTrue(accountService.isAdmin(ADMIN_ACCT_ID));
        assertFalse(accountService.isAdmin(RETAIL_ACCT_ID));
        assertFalse(accountService.isAdmin(1L));

    }

    @Test
    public void isRetailUser() {
        assertTrue(accountService.isRetailUser(RETAIL_ACCT_ID));
        assertFalse(accountService.isRetailUser(ADMIN_ACCT_ID));
        assertFalse(accountService.isAdmin(1L));
    }

}
