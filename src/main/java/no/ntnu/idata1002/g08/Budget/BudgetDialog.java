package no.ntnu.idata1002.g08.Budget;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.dao.BudgetDAO;
import no.ntnu.idata1002.g08.data.Budget;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.TransactionType;
import no.ntnu.idata1002.g08.screens.ScreenController;
import java.util.List;
import javafx.scene.control.Alert.AlertType;
import no.ntnu.idata1002.g08.screens.Screens;

import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * The bugdet dialog is a popup windows where the user
 * can enter required information needed.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class BudgetDialog {
  @FXML
  VBox rootVBox;
  @FXML
  Label headerLabel;
  @FXML
  TextField name;
  @FXML
  TextField amount;
  @FXML
  ListView<Category> categories;
  @FXML
  Button saveButton;
  @FXML
  Button cancelButton;
  private BudgetPeriod selectedBudgetPeriod;
  private Budget originalBudget;

  /**
   * Initialize the BudgetDialog and set required variables.
   *
   * @param initBudget The budget to use
   * @param budgetPeriod The budget periode
   * @param toDelete if it should be deleted
   */
  public BudgetDialog(Budget initBudget, BudgetPeriod budgetPeriod, boolean toDelete) {
    this.selectedBudgetPeriod = budgetPeriod;
    originalBudget = initBudget;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/budget/addBudgetItemDialog.fxml"));
    loader.setController(this);
    try {
      rootVBox = loader.load();

      if (originalBudget == null && toDelete) {
        throw new IllegalArgumentException("Cannot delete a budget that does not exist");
      }
      List<Category> categoriesList = CategoryRegistry.getInstance().getCategoriesByType(TransactionType.EXPENSE);

      categories.setItems(FXCollections.observableArrayList(categoriesList));
      categories.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      categories.setCellFactory(param -> new ListCell<>() {
        @Override
        protected void updateItem(Category item, boolean empty) {
          super.updateItem(item, empty);
          if (empty || item == null) {
            setText(null);
          } else {
            setText(item.toString());
          }
        }
      });

      cancelButton.setOnAction(event -> {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
      });

      if (originalBudget != null && toDelete) {
        saveButton.setText("Confirm delete");
        saveButton.getStyleClass().add("bg-danger");
        saveButton.setOnAction(this::deleteBudgetItem);
        headerLabel.setText("Are you sure you want to delete the budget?");

      } else if (originalBudget != null) {
        saveButton.setText("Save");
        saveButton.getStyleClass().add("bg-success");
        saveButton.setOnAction(this::addBudgetItem);
        headerLabel.setText("Edit budget " + originalBudget.getName());
      } else {
        saveButton.setText("Add");
        saveButton.getStyleClass().add("bg-success");
        saveButton.setOnAction(this::addBudgetItem);
        headerLabel.setText("Add a new budget");
      }

      if (originalBudget != null) {
        name.setText(originalBudget.getName());
        amount.setText(String.valueOf(originalBudget.getLatestInterval(true).getBudgetedAmount()));
        categories.getSelectionModel().select(originalBudget.getCategories().get(0));
        for (int i = 1; i < originalBudget.getCategories().size(); i++) {
          categories.getSelectionModel().select(originalBudget.getCategories().get(i));
        }
      }

      name.setEditable(!toDelete);
      name.setDisable(toDelete);
      amount.setEditable(!toDelete);
      amount.setDisable(toDelete);
      categories.setEditable(!toDelete);
      categories.setDisable(toDelete);

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
   * Add bugdet Item.
   *
   * @param actionEvent JavaFX action button on press
   */
  private void addBudgetItem(ActionEvent actionEvent) {
    try {

    if (name.getText().isEmpty()) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "BudgetPeriod name cannot be empty");
      return;
    }
    if (amount.getText().isEmpty() || !amount.getText().matches("[0-9]+(\\.[0-9]+)?")) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Budgeted amount cannot be empty");
      return;
    }

    if (Double.parseDouble(amount.getText()) < 1) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "Budgeted amount cannot be less than 1");
      return;
    }

    if (originalBudget == null) {
      originalBudget = new Budget(name.getText(), selectedBudgetPeriod, Double.parseDouble(amount.getText()), categories.getSelectionModel().getSelectedItems());
    } else {
      originalBudget.setName(name.getText());
      originalBudget.getLatestInterval(true).setBudgetedAmount(Double.parseDouble(amount.getText()));
      originalBudget.setCategories(categories.getSelectionModel().getSelectedItems());
      originalBudget.setBudgetedAmount(Double.parseDouble(amount.getText()));
    }

      BudgetDAO budgetDAO = new BudgetDAO();
      budgetDAO.addOrUpdateBudget(originalBudget);

      rootVBox.getScene().getWindow().hide();

    } catch (Exception e) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "An error occurred while adding the budget-item");
      e.printStackTrace();
    }
  }

  private void deleteBudgetItem(ActionEvent event) {
    try {
      BudgetDAO budgetDAO = new BudgetDAO();
      budgetDAO.deleteBudget(originalBudget);
      ScreenController.instance().setActiveBudgetPeriod(selectedBudgetPeriod);
      ScreenController.instance().changeScreen(Screens.BUDGET);
      rootVBox.getScene().getWindow().hide();
    } catch (Exception e) {
      showAlert(AlertType.ERROR, rootVBox.getScene().getWindow(), "Error", "An error occurred while deleting the budget-item");
      e.printStackTrace();
    }
  }
}
