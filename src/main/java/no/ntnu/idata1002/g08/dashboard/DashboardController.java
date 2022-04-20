package no.ntnu.idata1002.g08.dashboard;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.TilePane;
import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.dao.CategoryDAO;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import no.ntnu.idata1002.g08.settings.SettingsPage;
import no.ntnu.idata1002.g08.transactions.AddTransactionWindow;
import no.ntnu.idata1002.g08.transactions.TransactionTableView;
import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Objects;

/**
 * The type Dashboard controller.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class DashboardController {
    @FXML
    private TableView<Transaction> tableView;
    @FXML
    private LineChart lineChart;
    @FXML
    private NumberAxis amountAxis;
    @FXML
    private CategoryAxis dateAxis;
    @FXML
    private PieChart pieChart;
    @FXML
    private TableView<Category> categoryTableView;

    @FXML
    private TilePane accountGrid;

    private SettingsPage settingsPage;
    private List<Transaction> transactionList;
    private int noCategoryAmount;


    /**
     * Initialize Dashboard view with data.
     */
    @FXML
    public void initialize() {
        this.intializeTable();
        this.intializeLineChart();
        this.initPieChart();
        this.updateShownData();
    }


    /**
     * Initialize line chart.
     */
    private void intializeLineChart() {
        this.dateAxis.setAutoRanging(true);
        this.lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.X_AXIS);
    }

    /**
     * Initialize acounts.
     */
    private void initAccounts(){
        accountGrid.getChildren().clear();
//        List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();
        AccountDAO accountDAO = new AccountDAO();
        List<Account> accounts = accountDAO.getAllAccounts();
        try {

            accountGrid.setHgap(10);
            accountGrid.setVgap(10);
            for (Account account : accounts) {
                String name = account.getName();
                double amount = account.getCurrentBalance();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard/gridAccount.fxml"));
                Button child = loader.load();
                accountGrid.getChildren().add(child);
                GridAccountController gridAccountController = loader.getController();
                gridAccountController.setName(name);

                gridAccountController.setAmount(amount);
            }
            accountGrid.setAlignment(Pos.TOP_LEFT);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize pie chart and category table.
     */
    private void initPieChart() {


        TableColumn<Category, String> nameColumn = new TableColumn<>("Name");
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setCellValueFactory(param ->{
            if(param.getValue() != null) {
                if(param.getValue().getName().equals("No category")){
                    return new SimpleStringProperty("No category");
                }
                return new SimpleStringProperty(param.getValue().getName());
            }else {
                return new SimpleStringProperty("No category");
            }

        });

        TableColumn<Category, String> typeColumn = new TableColumn<>("Type");

        typeColumn.setCellValueFactory(param ->{
            if(param.getValue() != null) {
                if(param.getValue().getType() == null){
                    return new SimpleStringProperty("No type");
                }else {
                    return new SimpleStringProperty(param.getValue().getType().toString());
                }
            }else {
                return new SimpleStringProperty("No type");
            }

        });
        TableColumn<Category, String> connectionAmountColumn = new TableColumn<>("Connection amount");
        connectionAmountColumn.setCellValueFactory(param ->{
            if(param.getValue() != null) {
                if(param.getValue().getName().equals("No category")){
                    return new SimpleStringProperty(Integer.toString(noCategoryAmount));
                }
               return new SimpleStringProperty(Integer.toString(param.getValue().getTransactions().size()));
            }else {
                return new SimpleStringProperty(Integer.toString(noCategoryAmount));
            }

        });

        categoryTableView.setRowFactory(tv -> {
            TableRow<Category> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem toTransaction= new MenuItem("Go to transactions with this category");
            toTransaction.setOnAction(event -> {
                Category selectedCategory = categoryTableView.getSelectionModel().getSelectedItem();
                this.goToTransaction(selectedCategory);
            });
            contextMenu.getItems().add(toTransaction);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });

        this.categoryTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.categoryTableView.getColumns().addAll(nameColumn, typeColumn, connectionAmountColumn);
    }

    /**
     * Update shown data for category table and pie chart.
     */
    private void updatePieChartAndTableData(){
        pieChart.getData().clear();
        CategoryDAO categoryDAO = new CategoryDAO();
//        CategoryRegistry.getInstance().resetAndRefreshFromDB();
        noCategoryAmount = (int) this.transactionList.stream().filter(Objects::nonNull).filter(transaction -> transaction.getCategory() == null).count();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        ObservableList<Category> categoryList = FXCollections.observableArrayList();
//        for (Category category : CategoryRegistry.getInstance().getAllCategories()) {
        for (Category category : categoryDAO.getAllCategories()) {
            pieChartData.add(new PieChart.Data(category.getName(), category.getTransactions().size()));
            categoryList.add(category);
        }
        pieChartData.add(new PieChart.Data("No category", noCategoryAmount));
        categoryList.add(null);

        this.pieChart.setData(pieChartData);
        this.categoryTableView.setItems(categoryList);
    }

    /**
     * Update shown data for line chart.
     */
    private void prepListForLineChart() {
        AccountDAO accountDAO = new AccountDAO();
        List<Account> accounts = accountDAO.getAllAccounts();

        for (Account account : accounts) {
            XYChart.Series<String, Double> series = new XYChart.Series<>();
            for (AccountBalance accountBalance : account.getAccountBalances().values()) {
                series.getData().add(new XYChart.Data<>(DateFormat.getDateInstance(DateFormat.SHORT).format(accountBalance.getDate()), account.calculateBalanceByDate(accountBalance.getDate())));
            }
            series.setName(account.getName());
            this.lineChart.getData().add(series);
        }
    }

    /**
     * Initialize transaction table.
     */
    private void intializeTable() {

        TransactionTableView transactionTableView = new TransactionTableView();
        this.tableView.getColumns().addAll(transactionTableView.getColumns()); // add all columns

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


        this.transactionList = transactionTableView.getItems(); // get all items
        this.tableView.getItems().addAll(this.transactionList); // add all item form database
        this.tableView.refresh();
        this.tableView.sort();
        this.tableView.getSortOrder().add(transactionTableView.getColumns().get(0));
    }

    /**
     * Edit transaction.
     * @param selectedTransaction
     */
    private void editTransaction(Transaction selectedTransaction) {
        AddTransactionWindow a = new AddTransactionWindow(selectedTransaction,false);
        this.tableView.refresh();
        this.updateShownData();

    }

    /**
     * Delete given transaction.
     * @param selectedTransaction
     */
    private void deleteTransaction(Transaction selectedTransaction) {
        AddTransactionWindow a = new AddTransactionWindow(selectedTransaction, true);
//        this.tableView.getItems().remove(selectedTransaction);
//        this.transactionList.remove(selectedTransaction);
        this.tableView.refresh();
        this.updateShownData();

    }

    /**
     * Update all shown data on dashboard.
     */
    private void updateShownData() {
        this.tableView.getItems().clear();
        this.lineChart.getData().clear();

        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        this.transactionList = FXCollections.observableArrayList(transactions);

        this.tableView.getItems().addAll(this.transactionList);
        this.prepListForLineChart();
        this.updatePieChartAndTableData();
        this.initAccounts();
    }

    /**
     * Add transaction to table and database.
     */
    @FXML
    private void addTransaction() {
        AddTransactionWindow addTransactionWindow = new AddTransactionWindow();
//        this.tableView.getItems().add(addTransactionWindow.getTransaction());
//        this.transactionList.add(addTransactionWindow.getTransaction());
//        CategoryRegistry.getInstance().resetAndRefreshFromDB();
        this.updateShownData();
    }

    /**
     * Takes you to the account settings page.
     *
     * @param event ActionEvent
     */
    @FXML
    private void AddAccount(ActionEvent event) {
        settingsPage = SettingsPage.ACCOUNT;
        ScreenController.instance().setSettingsPage(settingsPage);
        ScreenController.instance.changeScreen(Screens.SETTINGS);
        ScreenController.instance().setSettingsPage(SettingsPage.DEFAULT);
    }

    /**
     * Takes you to the category settings page.
     */
    @FXML
    private void addCategory() {
        settingsPage = SettingsPage.CATEGORIES;
        ScreenController.instance().setSettingsPage(settingsPage);
        ScreenController.instance.changeScreen(Screens.SETTINGS);
        ScreenController.instance().setSettingsPage(SettingsPage.DEFAULT);
    }

    /**
     * Takes you to the transaction page with the selected category as filter.
     * @param selectedCategory Category selected in the table.
     */
    private void goToTransaction(Category selectedCategory) {
        ScreenController.instance().setCategoryFilterTransactionPage(selectedCategory.getName());
        ScreenController.instance().changeScreen(Screens.TRANSACTIONS);
        ScreenController.instance().setCategoryFilterTransactionPage(null);
    }
}
