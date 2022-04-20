import no.ntnu.idata1002.g08.auth.AuthService;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.User;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountBalanceTest {

    @Test
    public void AccountBalanceTest(){
        GlobalTestData.getInstance();
        AuthService authService = new AuthService();
    }
    @Test
    public void testAccountBalanceWithInvalidParameters(){
        boolean exceptionThrown = false;
        Date date = Calendar.getInstance().getTime();
        try {
            new AccountBalance(date, -200);
        } catch (IllegalArgumentException e) {
            if ("Balance cannot be less then zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new AccountBalance(null, 200);
        } catch (IllegalArgumentException e) {
            if ("Date cannot be zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
    }

    @Test
    public void testAccountBalanceWithValidParameters(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        Date date = calendar.getTime();
        AccountBalance accountBalance = new AccountBalance(date, 200);
        assertEquals(accountBalance.getBalance(), 200);
        assertEquals(accountBalance.getDate(), date);
    }
}
