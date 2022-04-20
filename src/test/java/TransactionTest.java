import no.ntnu.idata1002.g08.data.*;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    @Test
    public void testTransactionWithInvalidParameters(){
        boolean exceptionThrown = false;

        Date date = Calendar.getInstance().getTime();
        Account account = new Account();

        try {
            new Transaction(null, TransactionType.INCOME, 200, account, "NOK");
        } catch (IllegalArgumentException e) {
            if ("Date cannot be zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, null, 200, account, "NOK");
        } catch (IllegalArgumentException e) {
            if ("TransactionType cannot be zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, TransactionType.INCOME, -200, account, "NOK");
        } catch (IllegalArgumentException e) {
            if ("Amount cannot be less then zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, TransactionType.INCOME, 200, null, "NOK");
        } catch (IllegalArgumentException e) {
            if ("Account cannot be set to zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, TransactionType.INCOME, 200, account, "");
        } catch (IllegalArgumentException e) {
            if ("Currency cannot be empty or blank"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, TransactionType.INCOME, 200, account, "   ");
        } catch (IllegalArgumentException e) {
            if ("Currency cannot be empty or blank"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Transaction(date, TransactionType.INCOME, 200, account, null);
        } catch (IllegalArgumentException e) {
            if ("Currency cannot be zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
    }

    @Test
    public void testTransactionWithValidParameters(){
        Date date = Calendar.getInstance().getTime();
        Account account = new Account();

        Transaction transaction = new Transaction(date, TransactionType.INCOME, 200, account, "NOK");
        assertEquals(transaction.getDate(), date);
        assertEquals(transaction.getType(), TransactionType.INCOME);
        assertEquals(transaction.getAmount(), 200);
        assertEquals(transaction.getAccount(), account);
        assertEquals(transaction.getCurrency(), "NOK");
    }


}
