import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.PersistenceUnit;
import no.ntnu.idata1002.g08.dao.CategoryDAO;
import no.ntnu.idata1002.g08.data.Budget;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.Category;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.GenerateTestObjects.generateTestBudgetPeriods;
import static util.GenerateTestObjects.generateTestExpenseCategories;

public class BudgetTest {
    @Test
    public void testBudgetPostive() {
        GlobalTestData.getInstance();
        CategoryDAO categoryDAO = new CategoryDAO();

        List<BudgetPeriod> budgetPeriods = generateTestBudgetPeriods(1);
        BudgetPeriod budgetPeriod = budgetPeriods.get(0);
        List<Category> testCategories = generateTestExpenseCategories(2);

        for (Category category : testCategories) {
            categoryDAO.addOrUpdateCategory(category);
        }
        Budget budget = new Budget("testBudget", budgetPeriod, 500, testCategories);
        assertEquals("testBudget", budget.getName());
        assertEquals(budgetPeriod, budget.getBudgetPeriod());
        assertEquals(500, budget.getBudgetedAmount());
        assertEquals(testCategories, budget.getCategories().values().stream().toList());
    }

    @Test
    public void testBudgetNegative() {
        GlobalTestData.getInstance();
        boolean exceptionThrown = false;

        Calendar cal = Calendar.getInstance();
        Date startDate = cal.getTime();

        BudgetPeriod budgetPeriod = generateTestBudgetPeriods(1).get(0);
        List<Category> testCategories = generateTestExpenseCategories(2);

        Budget budget = new Budget();
        try {
            budget.setName(null);
        } catch (IllegalArgumentException e) {
            if ("Name cannot be empty" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            budget.setName("");
        } catch (IllegalArgumentException e) {
            if ("Name cannot be empty" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            budget.setName("     ");
        } catch (IllegalArgumentException e) {
            if ("Name cannot be empty" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            budget.setBudgetPeriod(null);
        } catch (IllegalArgumentException e) {
            if ("Budget period cannot be null" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            budget.setBudgetedAmount(-1);
        } catch (IllegalArgumentException e) {
            if ("Budgeted amount cannot be below 1" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;

        try {
            budget.setBudgetedAmount(0);
        } catch (IllegalArgumentException e) {
            if ("Budgeted amount cannot be below 1" == e.getMessage()) {exceptionThrown = true;}
        }
        assertEquals(true, exceptionThrown);
        exceptionThrown = false;
    }
}
