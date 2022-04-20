package no.ntnu.idata1002.g08.Budget;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import no.ntnu.idata1002.g08.data.Budget;
import no.ntnu.idata1002.g08.data.BudgetInterval;
import java.util.List;
import java.util.Set;

/**
 * The budget history controller.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class BudgetHistoryController {
  @FXML
  private Label budgetName;
  @FXML
  private AnchorPane currentBudgetIntervalWrapper;
  @FXML
  private FlowPane budgetHistoryWrapper;
  private Budget activeBudget;

  /**
   * Initialize the class.
   *
   * @param activeBudget the Budget to set active
   */
  public void init(Budget activeBudget) {
    setActiveBudget(activeBudget);
    refreshBudgetHistory();
  }

  /**
   * Create a current Budget Interval Pane.
   */
  private void createCurrentBudgetIntervalPane() {
    currentBudgetIntervalWrapper.getChildren().clear();
    BudgetPane budgetPane = new BudgetPane(activeBudget.getLatestInterval(true), true, false);
    Pane rootPane = budgetPane.getRootPane();
    AnchorPane.setTopAnchor(rootPane, 0.0);
    AnchorPane.setBottomAnchor(rootPane, 0.0);
    AnchorPane.setLeftAnchor(rootPane, 0.0);
    AnchorPane.setRightAnchor(rootPane, 0.0);
    currentBudgetIntervalWrapper.getChildren().add(budgetPane.getRootPane());
    currentBudgetIntervalWrapper.setPadding(new Insets(10, 10, 10, 10));

  }

  /**
   * Create a budget History pane.
   */
  private void createBudgetHistoryPane() {
    budgetHistoryWrapper.getChildren().clear();
    Set<BudgetInterval> budgetIntervals = activeBudget.getAllIntervals();
    List<BudgetInterval> sortedBudgetIntervals = budgetIntervals.stream().sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate())).toList();
    for (BudgetInterval budgetInterval : sortedBudgetIntervals) {
      BudgetPane budgetPane = new BudgetPane(budgetInterval, true, false);
      budgetHistoryWrapper.getChildren().add(budgetPane.getRootPane());
    }
  }

  /**
   * Edit the budget.
   *
   * @param actionEvent JavaFX action button.
   */
  public void editBudget(ActionEvent actionEvent) {
    new BudgetDialog(activeBudget, activeBudget.getBudgetPeriod(), false);
    refreshBudgetHistory();
    createCurrentBudgetIntervalPane();
    createBudgetHistoryPane();
  }

  /**
   * Set the active budget.
   *
   * @param activeBudget JavaFX action button.
   */
  public void setActiveBudget(Budget activeBudget) {
    this.activeBudget = activeBudget;
  }

  /**
   * Refresh the budget history.
   */
  public void refreshBudgetHistory() {
    createCurrentBudgetIntervalPane();
    createBudgetHistoryPane();
    budgetName.setText(activeBudget.getName());
  }

  /**
   * Delete the budget.
   * @param actionEvent JavaFX action button.
   */
  public void deleteBudget(ActionEvent actionEvent) {
    new BudgetDialog(activeBudget, activeBudget.getBudgetPeriod(), true);
    refreshBudgetHistory();
    createCurrentBudgetIntervalPane();
    createBudgetHistoryPane();
  }
}
