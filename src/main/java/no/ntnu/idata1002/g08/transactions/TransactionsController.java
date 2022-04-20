package no.ntnu.idata1002.g08.transactions;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import no.ntnu.idata1002.g08.utils.WriteToFile;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import java.io.File;
import java.text.DateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the controller for the transactions view.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class TransactionsController {


    /**
     * The constant ALLA for selecting all accounts.
     */
    private static final String ALLA = "All accounts";

    /**
     * The constant ALLC for selecting all categories.
     */
    private static final String ALLC = "All categories";

    /**
     * The constant NOC for selecting no category.
     */
    private static final String NOC = "No category";

    /**
     * The constant ALLT for selecting all types.
     */
    private static final String ALLT = "All types";

    /**
     * The constant FFA for selecting from amount.
     */
    private static final String FFA = "Filter from amount";

    /**
     * The constant FTA for selecting to amount.
     */
    private static final String FTA = "Filter to amount";

    /**
     * Table view for displaying transactions.
     */
    @FXML
    private TableView<Transaction> tableView;
    /**
     * The Filter for filtering transactions.
     */
    private Filter filter;
    /**
     * The Filter date picker.
     */
    @FXML
    private DatePicker filterDatePicker;
    /**
     * Filter button for Account.
     */
    @FXML
    private MenuButton accountMenuButton;
    /**
     * Filter button for Category.
     */
    @FXML
    private MenuButton categoryMenuButton;
    /**
     * Filter button for Type.
     */
    @FXML
    private MenuButton typeMenuButton;
    /**
     * Filter button for Amount.
     */
    @FXML
    private MenuButton toFromMenuButton;
    /**
     * Filter slider for Amount.
     */
    @FXML
    private Slider amountSlider;
    /**
     * Flow pane for displaying the file chooser.
     */
    @FXML
    private FlowPane fileChooser;

    /**
     * The Transaction list containing all transactions to be displayed.
     */
    private List<Transaction> transactionList;

    /**
     * The Base transaction list containing all transactions.
     */
    private List<Transaction> baseTransactionList;

    /**
     * The Selected category in filter option.
     */
    private String selectedCategory;
    /**
     * The Selected account in filter option.
     */
    private String selectedAccount;
    /**
     * The Selected type in filter option.
     */
    private TransactionType selectedType;
    /**
     * The Selected date in filter option.
     */
    private String selectedDate;
    /**
     * The Transaction dao.
     */
    TransactionDAO transactionDAO = new TransactionDAO();


    /**
     * Initializes the transactions view window and sets up the table view.
     */
    public void initialize() {

        TransactionTableView transactionTableView = new TransactionTableView();
        this.tableView.getColumns().addAll(transactionTableView.getColumns());
        this.transactionList = transactionTableView.getItems();
        this.tableView.getItems().addAll(this.transactionList);
        this.baseTransactionList = this.transactionList;
        this.filter = new Filter();


        tableView.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");
            edit.setOnAction(event -> {
                Transaction selectedTransaction = tableView.getSelectionModel().getSelectedItem();
                this.editTransaction(selectedTransaction);
            });
            delete.setOnAction(event -> {
                Transaction selectedTransaction = tableView.getSelectionModel().getSelectedItem();
                this.deleteTransaction(selectedTransaction);
            });
            contextMenu.getItems().addAll(edit, delete);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });


        MenuItem allCategories = new MenuItem(ALLC);
        allCategories.setOnAction(event -> this.allCategories());
        MenuItem noCategory = new MenuItem(NOC);
        noCategory.setOnAction(event -> this.noCategory());

        MenuItem allAccounts = new MenuItem(ALLA);
        allAccounts.setOnAction(event -> this.allAccounts());

        MenuItem allTypes = new MenuItem(ALLT);
        allTypes.setOnAction(event -> this.allTypes());
        MenuItem income = new MenuItem(TransactionType.INCOME.toString());
        income.setOnAction(event -> this.income());
        MenuItem expense = new MenuItem(TransactionType.EXPENSE.toString());
        expense.setOnAction(event -> this.expense());

        MenuItem fromAmount = new MenuItem(FFA);
        fromAmount.setOnAction(event -> this.fromAmount());
        MenuItem toAmount = new MenuItem(FTA);
        toAmount.setOnAction(event -> this.toAmount());

        this.selectedCategory = ALLC;
        this.categoryMenuButton.setText(ALLC);
        this.categoryMenuButton.getItems().addAll(allCategories, noCategory);

        this.selectedAccount = ALLA;
        this.accountMenuButton.setText(ALLA);
        this.accountMenuButton.getItems().add(allAccounts);

        this.selectedType = null;
        this.typeMenuButton.setText(ALLT);
        this.typeMenuButton.getItems().addAll(allTypes, income, expense);

        this.toFromMenuButton.setText(FFA);
        this.toFromMenuButton.getItems().addAll(fromAmount, toAmount);

        this.filterDatePicker.setOnAction(event -> this.filterDate());

        this.amountSlider.valueProperty().addListener((observable, oldValue, newValue) -> this.setTransactionList(this.filterList(this.baseTransactionList)));


        for (Category category: CategoryRegistry.getInstance().getAllCategories()) {
            MenuItem menuItem = new MenuItem(category.getName());
            menuItem.setOnAction(event ->{
                categoryMenuButton.setText(category.getName());
                this.selectedCategory = category.getName();
                this.transactionList = this.baseTransactionList;
                this.setTransactionList(this.filterList(this.transactionList));
            });
            categoryMenuButton.getItems().add(menuItem);
        }

        for (Account account:AccountRegistry.getInstance().getAllAccounts()) {
            MenuItem menuItem = new MenuItem(account.getName());
            menuItem.setOnAction(event -> {
                accountMenuButton.setText(account.getName());
                this.selectedAccount = account.getName();
                this.transactionList = this.baseTransactionList;
                this.setTransactionList(this.filterList(this.transactionList));
            });
            accountMenuButton.getItems().add(menuItem);
        }
        this.tableView.getSortOrder().add(this.tableView.getColumns().get(0));
        this.tableView.sort();

    }

    /**
     * Start with category filter.
     *
     * @param categoryName the category name
     */
    public void startWithCategoryFilter(String categoryName){
        if(categoryName == null ||categoryName.isEmpty()||categoryName.isBlank()){
            this.categoryMenuButton.setText(ALLC);
        }else {
            this.categoryMenuButton.setText(categoryName);
        }
        this.selectedCategory = categoryName;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Edit a given transaction.
     * @param selectedTransaction transaction to be edited.
     */
    private void editTransaction(Transaction selectedTransaction) {
        AddTransactionWindow a = new AddTransactionWindow(selectedTransaction,false);
        this.tableView.refresh();
    }

    /**
     * Delete a given transaction.
     * @param selectedTransaction transaction to be deleted.
     */
    private void deleteTransaction(Transaction selectedTransaction) {
        AddTransactionWindow a = new AddTransactionWindow(selectedTransaction, true);
        this.transactionList.remove(selectedTransaction);
        this.baseTransactionList.remove(selectedTransaction);
        this.tableView.getItems().remove(selectedTransaction);
    }

    /**
     * Sets transaction list.
     *
     * @param inputList the input list
     */
    public void setTransactionList(List<Transaction> inputList) {
        tableView.getItems().clear();
        this.transactionList = new ArrayList<>();
        this.transactionList.addAll(inputList);
        tableView.getItems().addAll(this.transactionList);
    }

    /**
     * Set account filter to allAccounts.
     */
    public void allAccounts() {
        this.accountMenuButton.setText(ALLA);
        this.selectedAccount = ALLA;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set category filter to All categories.
     */
    public void allCategories() {
        this.categoryMenuButton.setText(ALLC);
        this.selectedCategory = ALLC;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set category filter to no category.
     */
    private void noCategory() {
        this.categoryMenuButton.setText(NOC);
        this.selectedCategory = NOC;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set type filter to all types.
     */
    public void allTypes() {
        this.typeMenuButton.setText(ALLT);
        this.selectedType = null;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set type filter to income.
     */
    public void income() {
        this.typeMenuButton.setText(TransactionType.INCOME.toString());
        this.selectedType = TransactionType.INCOME;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set type filter to expense.
     */
    public void expense() {
        this.typeMenuButton.setText(TransactionType.EXPENSE.toString());
        this.selectedType = TransactionType.EXPENSE;
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Filter date.
     */
    public void filterDate() {
        Date tempDate = Date.from(this.filterDatePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC));
        this.selectedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(tempDate);
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Reset date.
     */
    public void resetDate(){
        this.selectedDate = null;
        this.filterDatePicker.setValue(null);
        this.filterDatePicker.getEditor().clear();
        this.filterDatePicker.setPromptText("Filter by date");
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set amount filter to from amount.
     */
    public void fromAmount() {
        this.toFromMenuButton.setText(FFA);
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Set amount filter to "to amount".
     */
    public void toAmount() {
        this.toFromMenuButton.setText(FTA);
        this.transactionList = this.baseTransactionList;
        this.setTransactionList(this.filterList(this.transactionList));
    }

    /**
     * Filter list with selected filters.
     * @param inputList list to be filtered.
     */
    private List<Transaction> filterList(List<Transaction> inputList) {
        List<Transaction> tempList = inputList;
        tempList = this.filter.filterByAccount(tempList, this.selectedAccount);
        tempList = this.filter.filterByCategory(tempList, this.selectedCategory);
        tempList = this.filter.filterByType(tempList, this.selectedType);
        tempList = this.filter.filterByFormattedDate(tempList, this.selectedDate);
        tempList = this.filter.filterByAmount(tempList, this.amountSlider.getValue(), this.toFromMenuButton.getText());
        return tempList;
    }

    /**
     * Reset filters
     */
    public void resetFilterButton() {
        this.selectedAccount = ALLA;
        this.selectedCategory = ALLC;
        this.selectedType = null;
        this.selectedDate = null;
        this.toFromMenuButton.setText(FFA);
        this.amountSlider.setValue(0);
        this.resetDate();
    }

    /**
     * Add transaction.
     */
    public void addTransaction() {
        AddTransactionWindow addTransactionWindow = new AddTransactionWindow();
        this.transactionList.add(addTransactionWindow.getTransaction());
        this.setTransactionList(this.transactionList);
    }

    /**
     * Export transaction list to CSV file.
     * @param event event that triggers the method.
     */
    @FXML
    private void SaveEXCEL(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName("Transactions.csv");
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            WriteToFile writeToFile = new WriteToFile(filePath);
            writeToFile.writeToCsvFile();
        }
    }

}