import no.ntnu.idata1002.g08.auth.AuthService;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.User;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {


    @Test
    public void AccountTest(){
        GlobalTestData.getInstance();
        AuthService authService = new AuthService();
    }

    @Test
    public void testAccountWithInvalidParameters(){
        boolean exceptionThrown = false;
        //Test invalid names
        try{
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), 400);
            Account account = new Account("", "Big Bank Account", accountBalance);
        }catch (IllegalArgumentException illegalArgumentException){
            if ("Name cannot be blank"== illegalArgumentException.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try{
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), 400);
            Account account = new Account("   ", "Big Bank Account", accountBalance);
        }catch (IllegalArgumentException illegalArgumentException){
            if ("Name cannot be blank"== illegalArgumentException.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try{
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), 400);
            Account account = new Account(null, "Big Bank Account", accountBalance);
        }catch (IllegalArgumentException illegalArgumentException){
            if ("Name cannot be null"== illegalArgumentException.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        //Test invalid account
        try{
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), 400);
            Account account = new Account("Amazing Name", "", accountBalance);
        }catch (IllegalArgumentException illegalArgumentException){
            if ("accountNumber cannot be blank"== illegalArgumentException.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try{
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), 400);
            Account account = new Account("Amazing Name", "  ", accountBalance);
        }catch (IllegalArgumentException illegalArgumentException){
            if ("accountNumber cannot be blank"== illegalArgumentException.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
    }

    @Test
    public void testAccountWithValidParameters(){
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        AccountBalance accountBalance = new AccountBalance(date, 400);
        Account account = new Account("Amazing Name", null, accountBalance);
        assertEquals(account.getName(), "Amazing Name");
        assertEquals(account.getAccountNumber(), null);

        account = new Account("Amazing Name", "Big Bank Account", accountBalance);
        assertEquals(account.getAccountNumber(), "Big Bank Account");
        //date = cal.getTime();
        //date = calendar.getTime()
        //assertEquals(account.getAccountBalanceByDate(date), accountBalance);
    }
}
