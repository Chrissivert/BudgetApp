package util;

import no.ntnu.idata1002.g08.dao.BudgetPeriodDAO;
import no.ntnu.idata1002.g08.dao.CategoryDAO;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.PeriodUnit;
import no.ntnu.idata1002.g08.data.TransactionType;

import java.util.*;

public class GenerateTestObjects {

  public static List<BudgetPeriod> generateTestBudgetPeriods(int count) {
    List<BudgetPeriod> budgetPeriods = new ArrayList<>();
    BudgetPeriodDAO budgetPeriodDAO = new BudgetPeriodDAO();

    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.WEEK_OF_YEAR, -3);
    Date startDate = cal.getTime();
    cal.add(Calendar.WEEK_OF_YEAR, 2);
    for (int i = 0; i < count; i++) {
      BudgetPeriod budgetPeriod = new BudgetPeriod("Test" + i, 2, PeriodUnit.WEEK, startDate);
      budgetPeriods.add(budgetPeriod);
      budgetPeriodDAO.addOrUpdateBudgetPeriod(budgetPeriod);
    }

    return budgetPeriods;
  }

  public static List<Category> generateTestExpenseCategories(int count) {
    List<Category> categories = new ArrayList<>();
    CategoryDAO categoryDAO = new CategoryDAO();
    for (int i = 0; i < count; i++) {
      Category category = new Category("TestExpenseCategory" + i, TransactionType.EXPENSE);
      categories.add(category);
      categoryDAO.addOrUpdateCategory(category);
    }
    return categories;
  }

  public static List<Category> generateTestIncomeCategories(int count) {
    List<Category> categories = new ArrayList<>();
    CategoryDAO categoryDAO = new CategoryDAO();
    for (int i = 0; i < count; i++) {
      Category category = new Category("TestIncomeCategory" + i, TransactionType.INCOME);
      categories.add(category);
      categoryDAO.addOrUpdateCategory(category);
    }
    return categories;
  }
}
