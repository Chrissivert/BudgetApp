import no.ntnu.idata1002.g08.auth.AuthService;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.TransactionType;
import no.ntnu.idata1002.g08.data.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CategoryTest {

    @Test
    public void testCategoryWithInvalidParameters(){
        boolean exceptionThrown = false;
        AuthService authService = new AuthService();

        try {
            new Category("", TransactionType.INCOME);
        } catch (IllegalArgumentException e) {
            if ("Name cannot be blank or empty"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Category("   ", TransactionType.INCOME);
        } catch (IllegalArgumentException e) {
            if ("Name cannot be blank or empty"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
        try {
            new Category(null, TransactionType.INCOME);
        } catch (IllegalArgumentException e) {
            if ("Name cannot be null"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            new Category("Good Food", null);
        } catch (IllegalArgumentException e) {
            if ("TransactionType cannot be zero"== e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
    }

    @Test
    public void testCategoryWithValidParameters(){
        Category category = new Category("Good Food", TransactionType.INCOME);
        assertEquals(category.getType(), TransactionType.INCOME);
        assertEquals(category.getName(), "Good Food");
    }


}
