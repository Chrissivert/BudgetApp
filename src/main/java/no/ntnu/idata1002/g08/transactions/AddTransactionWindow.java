package no.ntnu.idata1002.g08.transactions;

import jakarta.persistence.PersistenceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.dashboard.DashboardController;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Controller class for the add transaction window
 * this class is used to add, edit or delete a transaction
 *
 * @author Brage Solem, Anders Lund, Daniel Neset
 * @version 27.04.2023
 */
public class AddTransactionWindow {
    /**
     * The transaction to be edited or deleted
     */
    private Transaction transaction;
    /**
     * The original transaction to be edited or deleted
     */
    private Transaction originalTransaction;
    /**
     * The root VBox of the window
     */
    private VBox rootVBox;
    /**
     * The in window title
     */
    @FXML
    private Label headerLabel;
    /**
     * The date picker for the transaction date
     */
    @FXML
    private DatePicker datePicker;
    /**
     * The choice box for the transaction type
     */
    @FXML
    private ChoiceBox<TransactionType> typeChoice;
    /**
     * The text field for the transaction description
     */
    @FXML
    private TextField descriptionField;
    /**
     * The text field for the transaction amount
     */
    @FXML
    private TextField amountField;
    /**
     * The choice box for the transaction category
     */
    @FXML
    private ChoiceBox<Category> categoryChoice;
    /**
     * The choice box for the transaction account
     */
    @FXML
    private ChoiceBox<Account> accountChoice;
    /**
     * The button for confirming the transaction addition,edit or deletion
     */
    @FXML
    private Button addButton;
    /**
     * Check box for no category
     */
    @FXML
    private CheckBox noCategoryCheckBox;
    /**
     * boolean for valid input
     */
    private boolean validInput;
    /**
     * String for invalid inputs
     */
    private String invalidInputs;

    /**
     * The transaction DAO to access the transaction in the database
     */
    TransactionDAO transactionDAO = new TransactionDAO();

   /**
    * Constructor for add transaction
    */
    public AddTransactionWindow() {
        this.init(null,false);
    }

    /**
     * Constructor for edit transaction and delete transaction
     * @param transaction the transaction to be edited or deleted
     */
    public AddTransactionWindow(Transaction transaction, boolean toDelete) {
      this.originalTransaction = transaction;
      this.transaction = transaction;
      this.init(this.transaction , toDelete);
    }


    /**
     * initializes the transactionAdd window
     * @param inputTransaction the transaction to be updated or deleted
     */
    private void init(Transaction inputTransaction, boolean toDelete) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transactions/AddTransactionWindow.fxml"));
        loader.setController(this);
        try {
            this.rootVBox = loader.load();
            this.amountField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));

            List<Category> categories = CategoryRegistry.getInstance().getAllCategories();
            List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();

            this.typeChoice.getItems().addAll(TransactionType.EXPENSE, TransactionType.INCOME);
            this.typeChoice.setValue(TransactionType.EXPENSE);

            this.typeChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                categoryChoice.getItems().clear();
                for (Category category : categories){
                    if(category.getType().equals(newValue)){
                        categoryChoice.getItems().add(category);
                        categoryChoice.setValue(category);
                    }
                }
                if(categoryChoice.getItems().size() == 0){
                    categoryChoice.setDisable(true);
                }else {
                    categoryChoice.setDisable(false);
                }
                this.categoryChoice.setDisable(this.noCategoryCheckBox.isSelected());
            });

           if(categories.size() == 0){
                this.categoryChoice.setDisable(true);
            }else{
               for (Category category : categories){
                   if(category.getType().equals(typeChoice.getValue())){
                       categoryChoice.getItems().add(category);
                       categoryChoice.setValue(category);
                   }
               }
                this.typeChoice.setValue(categories.get(0).getType());
            }

            this.accountChoice.getItems().addAll(accounts);
            this.accountChoice.setValue(accounts.get(0));

            this.noCategoryCheckBox.setOnAction(e -> this.categoryChoice.setDisable(this.noCategoryCheckBox.isSelected()));

            this.datePicker.setValue(LocalDate.now());

            this.addButton.setOnAction(this::addTransaction);
            if (inputTransaction != null) {
                this.addButton.setText("Update");
                this.headerLabel.setText("Edit transaction");
                this.updateForEditTransaction(inputTransaction);
                this.addButton.setOnAction(e -> this.updateTransaction(inputTransaction));

                if (toDelete) {
                    this.addButton.setText("Confirm delete");
                    this.headerLabel.setText("Delete transaction");
                    this.updateForDeleteTransaction(inputTransaction);
                    this.addButton.setOnAction(e -> this.deleteTransaction(inputTransaction));
                }
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(rootVBox));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the transaction to the database
     * @param event the event that triggered the method
     */
    private void addTransaction(ActionEvent event) {
        Transaction transaction = checkInput();
        if(transaction != null){
            transactionDAO.addOrUpdateTransaction(transaction);
//            if (this.dashboardController != null) {
//                this.dashboardController.initialize();
//            }
//            if (this.transactionsController != null) {
//                this.transactionsController.initialize();
//            }
            this.transaction = transaction;
        }
    }

    /**
     * Checks the user input for the transaction.
     */
    private Transaction checkInput(){
        Transaction transaction = null;
        Stage stage = (Stage) this.addButton.getScene().getWindow();
        try{
            TransactionType transactionType = typeChoice.getValue();
            Account account = accountChoice.getValue();
            LocalDate localDate = datePicker.getValue();
            String description = descriptionField.getText();
            String amountText = amountField.getText();
            Category category = categoryChoice.getValue();

            if(localDate == null){
                throw new IllegalArgumentException("You must enter a valid Date.\nThe date must be in the format: dd/mm/yyyy");
            } else if (transactionType == null) {
                throw new IllegalArgumentException("You need to select if the transaction is an expense or income");
            } else if (amountText.isBlank() | amountText.isEmpty()){
                throw new IllegalArgumentException("Please enter a positive amount");
            }else if(account == null){
                throw new IllegalArgumentException("You must choose a Account");
            }

            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date date = Date.from(instant);
            double amount = Double.parseDouble(amountText);

            if (amount < 1) {
                throw new IllegalArgumentException("Please enter a positive amount");
            }

            Transaction tempTransaction = new Transaction();
            tempTransaction.setDate(date);
            tempTransaction.setType(transactionType);
            tempTransaction.setAmount(amount);
            tempTransaction.setAccount(account);
            tempTransaction.setCurrency("NOK");
            tempTransaction.setDescription(description);
            if(category != null){
                tempTransaction.setCategory(category);
            }
            if(this.noCategoryCheckBox.isSelected()){
                tempTransaction.setCategory(null);
            }
            transaction = tempTransaction;

            stage.close();
        } catch (IllegalArgumentException illegalArgumentException){
            showAlert(Alert.AlertType.ERROR, this.addButton.getScene().getWindow(), "Invalid input", illegalArgumentException.getLocalizedMessage());
        } catch (PersistenceException persistenceException){
            showAlert(Alert.AlertType.ERROR, this.addButton.getScene().getWindow(), "An error occurred", "An error occurred while trying to add the transaction to the database. Please try again later.");
        } catch (Exception e){
            showAlert(Alert.AlertType.ERROR, this.addButton.getScene().getWindow(), "An unexpected error occurred", "An unexpected error occurred. Please try again later.");
        }
        return transaction;
    }

    /**
     * Deletes the transaction from the database.
     * @param inputTransaction the transaction to delete.
     */
    private void deleteTransaction(Transaction inputTransaction) {
        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.removeTransaction(inputTransaction);
        Stage stage = (Stage) this.addButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Updates the fields in the window to match the transaction that is being edited
     * @param transactionToEdit the transaction that is being edited
     */
    private void updateForEditTransaction(Transaction transactionToEdit){
        if(transactionToEdit.getDate() != null) {
            this.datePicker.setValue(transactionToEdit.getDate().toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate());
        }

        if(transactionToEdit.getType() != null) {
            this.typeChoice.setValue(transactionToEdit.getType());
        }

        if(transactionToEdit.getDescription()!= null) {
            this.descriptionField.setText(transactionToEdit.getDescription());
        }

        this.amountField.setText(String.valueOf(transactionToEdit.getAmount()));

      if ((transactionToEdit.getCategory()==null)||(transactionToEdit.getCategory().getName().equals("No category"))){
            this.noCategoryCheckBox.setSelected(true);
            this.categoryChoice.setDisable(true);
        }else {
            this.categoryChoice.setValue(transactionToEdit.getCategory());
        }

        this.accountChoice.setValue(transactionToEdit.getAccount());

    }

    /**
     * Updates the transaction in the database.
     * @param inputTransaction the transaction to update.
     */
    private void updateTransaction(Transaction inputTransaction) {
        if(checkInput() != null) {
            inputTransaction.setDate(Date.from(datePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC)));
            inputTransaction.setType(typeChoice.getValue());
            inputTransaction.setAccount(accountChoice.getValue());
            inputTransaction.setDescription(descriptionField.getText());
            inputTransaction.setAmount(Double.parseDouble(amountField.getText()));
            if(this.noCategoryCheckBox.isSelected()){
                inputTransaction.setCategory(null);
            }else {
                inputTransaction.setCategory(categoryChoice.getValue());
            }
            TransactionDAO transactionDAO = new TransactionDAO();
            transactionDAO.addOrUpdateTransaction(inputTransaction);
            Stage stage = (Stage) this.addButton.getScene().getWindow();
            stage.close();
        }
    }


    /**
     * Updates the fields in the window to match the transaction that is being deleted
     * @param inputTransaction the transaction that is being deleted
     */
    private void updateForDeleteTransaction(Transaction inputTransaction) {
        this.datePicker.setEditable(false);
        this.datePicker.setDisable(true);
        this.descriptionField.setEditable(false);
        this.amountField.setEditable(false);
        this.categoryChoice.setDisable(true);
        this.accountChoice.setDisable(true);

        if(inputTransaction.getDescription() == null){
            this.descriptionField.setText("No description");
        }else {
            this.descriptionField.setText(inputTransaction.getDescription());
        }
    }

    /**
     * Returns the transaction that was added, edited or deleted.
     * @return the transaction that was added, edited or deleted.
     */
    public Transaction getTransaction() {
        return transaction;
    }

}
