import no.ntnu.idata1002.g08.data.*;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.GenerateTestObjects.generateTestExpenseCategories;

class BudgetIntervalTest {
  @Test
  void budgetIntervalPositive() {
    GlobalTestData.getInstance();
    Calendar cal = Calendar.getInstance();
    Date startDate = cal.getTime();
    cal.add(Calendar.MONTH, 1);
    Date endDate = cal.getTime();
    List<Category> categories = generateTestExpenseCategories(2);
    BudgetPeriod budgetPeriod = new BudgetPeriod("testBudgetPeriod", 1, PeriodUnit.MONTH, startDate);
    Budget budget = new Budget("testBudget", budgetPeriod, 1000, categories);
    BudgetInterval budgetInterval = new BudgetInterval(budget, startDate, endDate, 500);

    assertEquals(budget, budgetInterval.getBudget());
    assertEquals(startDate, budgetInterval.getStartDate());
    assertEquals(endDate, budgetInterval.getEndDate());
    assertEquals(500, budgetInterval.getBudgetedAmount());
  }

  @Test
  void budgetIntervalNegative() {
    boolean exceptionThrown = false;
    GlobalTestData.getInstance();
    Calendar cal = Calendar.getInstance();
    BudgetInterval budgetInterval = new BudgetInterval();

    try {
      budgetInterval.setBudget(null);
    } catch (IllegalArgumentException e) {
      if ("Budget is invalid".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
    exceptionThrown = false;

    try {
      budgetInterval.setStartDate(null);
    } catch (IllegalArgumentException e) {
      if ("Start date is invalid".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
    exceptionThrown = false;

    try {
      budgetInterval.setEndDate(null);
    } catch (IllegalArgumentException e) {
      if ("End date is invalid".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
    exceptionThrown = false;

    try {
      Date endDate = cal.getTime();
      cal.add(Calendar.MONTH, 1);
      Date startDate = cal.getTime();
      budgetInterval.setStartDate(startDate);
      budgetInterval.setEndDate(endDate);
    } catch (IllegalArgumentException e) {
      if ("End date is invalid".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
    exceptionThrown = false;

    try {
      Date endDate = cal.getTime();
      cal.add(Calendar.MONTH, 1);
      Date startDate = cal.getTime();
      budgetInterval.setEndDate(endDate);
      budgetInterval.setStartDate(startDate);
    } catch (IllegalArgumentException e) {
      if ("Start date is invalid".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
    exceptionThrown = false;

    try {
      budgetInterval.setBudgetedAmount(-1);
    } catch (IllegalArgumentException e) {
      if ("Budgeted amount cannot be negative".equals(e.getMessage())) {exceptionThrown = true;}
    }
    assertTrue(exceptionThrown);
  }
}
