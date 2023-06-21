import com.alezhang.estore.EstoreApplication;
import com.alezhang.estore.data.enumeration.AccountType;
import com.alezhang.estore.data.model.Account;
import com.alezhang.estore.service.impl.AccountServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

import static org.junit.Assert.*;

@SpringBootTest(classes = EstoreApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @Resource
    private AccountServiceImpl accountService;
    private static final Long ADMIN_ACCT_ID = 100001L;
    private static final Long RETAIL_ACCT_ID = 100002L;

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
