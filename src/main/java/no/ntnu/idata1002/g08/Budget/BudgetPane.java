package no.ntnu.idata1002.g08.Budget;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import no.ntnu.idata1002.g08.data.BudgetInterval;
import no.ntnu.idata1002.g08.screens.ScreenController;
import java.text.DateFormat;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * The BugdetPane creates the one Pane.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class BudgetPane {
  @FXML
  Label dates;
  @FXML
  Label name;
  @FXML
  Label amount;
  @FXML
  ProgressBar progressBar;
  private BudgetInterval budgetInterval;
  private Pane rootPane;

  /**
   * Initilize the budget pane with values.
   *
   * @param budgetInterval The budgetInterval
   * @param includeDates If it includes Dates
   * @param includeTitle If it includes Titles
   */
  public BudgetPane(BudgetInterval budgetInterval, boolean includeDates, boolean includeTitle) {
    try {
      this.budgetInterval = budgetInterval;

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget/budgetPane.fxml"));
      loader.setController(this);
      rootPane = loader.load();

      setFields(includeDates, includeTitle);
    } catch (Exception e) {
      e.printStackTrace();
      showAlert(Alert.AlertType.ERROR, ScreenController.instance().getCurrentScene().getWindow(), "Error", "Error loading budget item pane");
    }
  }

  /**
   * Set the fields.
   *
   * @param includeDates if includes dates
   * @param includeTitle if includes title
   */
  private void setFields(boolean includeDates, boolean includeTitle) {
    if (budgetInterval != null) {
      String startDateShort = DateFormat.getDateInstance(DateFormat.SHORT).format(budgetInterval.getStartDate());
      String endDateShort = DateFormat.getDateInstance(DateFormat.SHORT).format(budgetInterval.getEndDate());
      dates.setText(includeDates ? startDateShort + " - " + endDateShort : "");

      String budgetName = budgetInterval.getBudget().getName();
      name.setText(includeTitle ? budgetName : "");
      amount.setText(String.format("%.2f / %.2f", budgetInterval.getUsedAmount(), budgetInterval.getBudgetedAmount()));
      progressBar.setProgress(budgetInterval.getUsedAmount() / budgetInterval.getBudgetedAmount());
      if (budgetInterval.getUsedAmount() > budgetInterval.getBudgetedAmount()) {
        progressBar.setStyle("-fx-accent: red;");
      } else if (budgetInterval.getUsedAmount() > budgetInterval.getBudgetedAmount() * 0.75) {
        progressBar.setStyle("-fx-accent: orange;");
      } else {
        progressBar.setStyle("-fx-accent: green;");
      }
    } else {
      dates.setText("");
      name.setText("");
      amount.setText("0.0 / 0.0");
      progressBar.setProgress(0);
    }
  }

  /**
   * Get the root Pane.
   *
   * @return the rootPane
   */
  public Pane getRootPane() {
    return rootPane;
  }

}