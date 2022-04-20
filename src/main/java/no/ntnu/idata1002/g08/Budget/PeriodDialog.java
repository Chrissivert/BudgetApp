package no.ntnu.idata1002.g08.Budget;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import no.ntnu.idata1002.g08.dao.BudgetPeriodDAO;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.PeriodUnit;
import no.ntnu.idata1002.g08.screens.ScreenController;
import static no.ntnu.idata1002.g08.data.BudgetPeriod.*;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

/**
 * The dialog to add budget period.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class PeriodDialog {
  @FXML
  VBox rootVBox;
  @FXML
  Label headerLabel;
  @FXML
  DatePicker startDate;
  @FXML
  TextField name;
  @FXML
  TextField periodLength;
  @FXML
  ChoiceBox<PeriodUnit> periodLengthUnit;
  @FXML
  Button saveButton;
  @FXML
  Button cancelButton;
  private BudgetPeriod originalBudgetPeriod;

  /**
   * Create a Periode Dialog.
   *
   * @param initBudgetPeriod The budgetPeriode
   * @param toDelete         if to be deleted
   */
  public PeriodDialog(BudgetPeriod initBudgetPeriod, boolean toDelete) {
    originalBudgetPeriod = initBudgetPeriod;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget/addBudgetPeriod.fxml"));
    loader.setController(this);
    try {
      rootVBox = loader.load();

      if (originalBudgetPeriod == null && toDelete) {
        throw new IllegalArgumentException("Cannot delete a budget that does not exist");
      }

      periodLengthUnit.setItems(FXCollections.observableArrayList(Arrays.asList(PeriodUnit.values())));
      cancelButton.setOnAction(e -> ((Stage) rootVBox.getScene().getWindow()).close());

      if (originalBudgetPeriod != null && toDelete) {
        saveButton.setText("Confirm delete");
        saveButton.getStyleClass().add("bg-danger");
        saveButton.setOnAction(this::deleteBudget);
        headerLabel.setText("Are you sure you want to delete the period?");
      } else if (originalBudgetPeriod != null) {
        saveButton.setText("Save");
        saveButton.getStyleClass().add("bg-success");
        saveButton.setOnAction(this::checkAndAddBudget);
        headerLabel.setText("Edit budget " + originalBudgetPeriod.getName());
      } else {
        saveButton.setText("Add");
        saveButton.getStyleClass().add("bg-success");
        saveButton.setOnAction(this::checkAndAddBudget);
        headerLabel.setText("Add a new budget");
        periodLengthUnit.setValue(PeriodUnit.WEEK);
      }

      if (originalBudgetPeriod != null) {
        startDate.setValue(originalBudgetPeriod.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        name.setText(originalBudgetPeriod.getName());
        periodLength.setText(String.valueOf(originalBudgetPeriod.getPeriodLengthValue()));
        periodLengthUnit.setValue(originalBudgetPeriod.getPeriodLengthUnit());
      }

      name.setDisable(toDelete);
      startDate.setDisable(toDelete || originalBudgetPeriod != null);
      periodLength.setDisable(toDelete || originalBudgetPeriod != null);
      periodLengthUnit.setDisable(toDelete || originalBudgetPeriod != null);

      Stage stage = new Stage();
      stage.setScene(new Scene(rootVBox));
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.showAndWait();
    } catch (Exception e) {
      showAlert(AlertType.ERROR, ScreenController.instance().getCurrentScene().getWindow(), "Error", "An error occurred while loading the window");
      e.printStackTrace();
    }
  }

  /**
   * Check the budget and adds it.
   *
   * @param event JavaFX action button
   */
  public void checkAndAddBudget(ActionEvent event) {
    try {
      BudgetPeriod budgetPeriodToChange;
      BudgetPeriodDAO budgetPeriodDAO = new BudgetPeriodDAO();
      if (name.getText().isEmpty()) {
        showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Name cannot be empty");
        return;
      }
      if (startDate.getValue() == null || startDate.getValue().isBefore(firstValidDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) || startDate.getValue().isAfter(lastValidDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
        showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Start date is not a valid date.\nThe date has to be later than " + firstValidDateString + ", earlier than " + lastValidDateString + " and in the format dd/mm/yyyy");
        return;
      }
      if (periodLength.getText().isEmpty() || !periodLength.getText().matches("[0-9]+") || Integer.parseInt(periodLength.getText()) <= 0) {
        showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Period length has to be a number greater than 0");
        return;
      }
      if (periodLengthUnit.getValue() == null || (periodLengthUnit.getValue() != PeriodUnit.DAY && periodLengthUnit.getValue() != PeriodUnit.WEEK && periodLengthUnit.getValue() != PeriodUnit.MONTH && periodLengthUnit.getValue() != PeriodUnit.YEAR)) {
        showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Period length unit has to be one of the following: Days, Weeks, Months, Years");
        return;
      }

      Instant instant = Instant.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()));
      Date date = Date.from(instant);

      if (originalBudgetPeriod == null) {
        originalBudgetPeriod = new BudgetPeriod(name.getText(), Integer.parseInt(periodLength.getText()), periodLengthUnit.getValue(), date/*, recurring.isSelected()*/);
      } else {
        originalBudgetPeriod.setName(name.getText());
        originalBudgetPeriod.setStartDate(date);
        originalBudgetPeriod.setPeriodLengthValue(Integer.parseInt(periodLength.getText()));
        originalBudgetPeriod.setPeriodLengthUnit(periodLengthUnit.getValue());
      }

      budgetPeriodDAO.addOrUpdateBudgetPeriod(originalBudgetPeriod);

      rootVBox.getScene().getWindow().hide();
    } catch (Exception e) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Error adding budget");
      e.printStackTrace();
    }
  }

  /**
   * Delete the budget.
   *
   * @param event JavaFX action Event
   */
  public void deleteBudget(ActionEvent event) {
    try {
      BudgetPeriodDAO budgetPeriodDAO = new BudgetPeriodDAO();
      budgetPeriodDAO.removeBudgetPeriod(originalBudgetPeriod);
      rootVBox.getScene().getWindow().hide();
      originalBudgetPeriod = null;
    } catch (Exception e) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Error deleting budget");
      e.printStackTrace();
    }
  }

  public BudgetPeriod getBudgetPeriod() {
    return originalBudgetPeriod;
  }
}