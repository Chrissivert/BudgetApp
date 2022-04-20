import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.PeriodUnit;
import org.junit.jupiter.api.Test;
import util.GlobalTestData;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetPeriodTest {

  @Test
  public void testBudgetPeriodPositive() {
    GlobalTestData.getInstance();
    Calendar cal = Calendar.getInstance();
    Date startDate = cal.getTime();
    BudgetPeriod budgetPeriod = new BudgetPeriod("testBudgetPeriod", 1, PeriodUnit.MONTH, startDate);
    assertEquals("testBudgetPeriod", budgetPeriod.getName());
    assertEquals(1, budgetPeriod.getPeriodLengthValue());
    assertEquals(PeriodUnit.MONTH, budgetPeriod.getPeriodLengthUnit());
    assertEquals(startDate, budgetPeriod.getStartDate());
  }

  @Test
  public void testBudgetPeriodNegative() {
    GlobalTestData.getInstance();
    boolean exceptionThrown = false;
    Calendar cal = Calendar.getInstance();
    cal.set(2018, Calendar.JANUARY, 1);
    Date startDate = cal.getTime();
    BudgetPeriod budgetPeriod = new BudgetPeriod();
    try {
      budgetPeriod.setName(null);
    } catch (IllegalArgumentException e) {
      if ("Name cannot be empty" == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setName("");
    } catch (IllegalArgumentException e) {
      if ("Name cannot be empty" == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setPeriodLengthValue(0);
    } catch (IllegalArgumentException e) {
      if ("Period length value must be greater than 0" == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setPeriodLengthValue(-1);
    } catch (IllegalArgumentException e) {
      if ("Period length value must be greater than 0" == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setPeriodLengthUnit(null);
    } catch (IllegalArgumentException e) {
      if ("Period length unit is null." == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setStartDate(null);
    } catch (IllegalArgumentException e) {
      if ("Date startDate is invalid." == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
    exceptionThrown = false;
    try {
      budgetPeriod.setStartDate(startDate);
    } catch (IllegalArgumentException e) {
      if ("Date startDate is invalid." == e.getMessage()) {exceptionThrown = true;}
    }
    assertEquals(true, exceptionThrown);
  }
}
