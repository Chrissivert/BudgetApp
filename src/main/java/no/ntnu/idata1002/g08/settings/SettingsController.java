package no.ntnu.idata1002.g08.settings;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.User;
import no.ntnu.idata1002.g08.utils.DeleteUser;
import no.ntnu.idata1002.g08.utils.WriteToFile;
import no.ntnu.idata1002.g08.bank.TransactionSyncronice;
import no.ntnu.idata1002.g08.dao.UserDAO;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import java.io.File;
import java.util.List;


/**
 * Represents the settings controller, controls the settings menu.
 * This class is responsible for the settings menu and the different
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class SettingsController {
    @FXML
    private BorderPane settingsStartingPage;
    @FXML
    private GridPane bankConnection;
    @FXML
    private GridPane notifications;
    @FXML
    private GridPane rules;
    @FXML
    private GridPane accessibility;
    @FXML
    private GridPane categories;
    @FXML
    private GridPane settingsDashboard;
    @FXML
    private GridPane settingsGDPR;
    @FXML
    private GridPane settingsAccount;
    @FXML
    private Button buttonBankConnection;
    @FXML
    private Button buttonAccessibility;
    @FXML
    private Button buttonCategories;
    @FXML
    private Button buttonGDPR;
    @FXML
    private Button buttonAccounts;
    @FXML
    private Button removeAccount;
    @FXML
    private Button addAccount;
    @FXML
    private Button manageRules;
    @FXML
    private Button mangeDashboard;
    @FXML
    private MenuButton colorblindChoice;
    @FXML
    private ChoiceBox<String> textFontChoice;
    @FXML
    private ToggleButton exceedingOfBudget;
    @FXML
    private ToggleButton budgetExcessSavings;
    @FXML
    private ToggleButton budgetExceeded;
    @FXML
    private ToggleButton savingsGoal;
    @FXML
    private ToggleButton notificationWhenGettingA;
    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TextField accountNameFXML;

    @FXML
    private ChoiceBox<Account> accountChoice;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize() {

        try {
            settingsStartingPage.managedProperty().bind(settingsStartingPage.visibleProperty());
            bankConnection.managedProperty().bind(bankConnection.visibleProperty());
            notifications.managedProperty().bind(notifications.visibleProperty());
            rules.managedProperty().bind(rules.visibleProperty());
            accessibility.managedProperty().bind(accessibility.visibleProperty());
            categories.managedProperty().bind(categories.visibleProperty());
            settingsDashboard.managedProperty().bind(settingsDashboard.visibleProperty());
            settingsGDPR.managedProperty().bind(settingsGDPR.visibleProperty());
            settingsAccount.managedProperty().bind(settingsAccount.visibleProperty());

            for (ColorBlindMode colorBlindMode:ColorBlindMode.values()) {
                MenuItem menuItem = new MenuItem(colorBlindMode.getTitle());
                menuItem.setOnAction(event -> {
                        UserDAO userDAO = new UserDAO();
                        User user = GlobalData.getInstance().getUser();
                        user.setColorBlindMode(colorBlindMode);
                        userDAO.addOrUpdateUser(user);
                        this.colorblindChoice.setText(colorBlindMode.getTitle());
                });
                this.colorblindChoice.getItems().add(menuItem);
            }
            this.colorblindChoice.setText(GlobalData.getInstance().getUser().getColorBlindSetting().getTitle());

            this.textFontChoice.getItems().addAll("Arial","Times New Roman","Verdana","Calibri","Comic Sans MS");
            this.textFontChoice.setValue("Arial");

            this.initTable();
            this.hideAll();
            settingsStartingPage.setVisible(true);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the table with categories.
     */
    private void initTable() {
        TableColumn<Category, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Category, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Category, String> connectionAmountColumn = new TableColumn<>("Connection amount");
        connectionAmountColumn.setCellValueFactory(param ->{
            if(param.getValue() != null) {
                if(param.getValue().getTransactions() != null) {
                    return new SimpleStringProperty(Integer.toString(param.getValue().getTransactions().size()));
                }else {
                    return new SimpleStringProperty(null);
                }
            }else {
                return new SimpleStringProperty(null);
            }

        });

        List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();
        accountChoice.getItems().addAll(accounts);
        if(accounts.size() != 0){
            accountChoice.setValue(accounts.get(0));
        }


        categoryTable.setRowFactory(tv -> {
            TableRow<Category> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem edit = new MenuItem("Edit");
            MenuItem delete = new MenuItem("Delete");
            edit.setOnAction(event -> {
                Category category = categoryTable.getSelectionModel().getSelectedItem();
                this.editCategories(category);
            });
            delete.setOnAction(event -> {
                Category category = categoryTable.getSelectionModel().getSelectedItem();
                this.deleteCategory(category);
            });
            contextMenu.getItems().addAll(edit, delete);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );
            return row;
        });

        this.categoryTable.getColumns().addAll(nameColumn, typeColumn, connectionAmountColumn);
        this.categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        this.categoryTable.getItems().addAll(CategoryRegistry.getInstance().getAllCategories());
    }

    /**
     * Open a specific settings page.
     * @param settings
     */
    public void openSpesificSettings(SettingsPage settings) {
        switch (settings) {
            case BANKCONNECTION:
                this.showBankConnection();
                break;
            case ACCESSIBILITY:
                this.showAccessibility();
                break;
            case CATEGORIES:
                this.showCategories();
                break;
            case GDPR:
                this.showGDPR();
                break;
            case ACCOUNT:
                this.showAccounts();
                break;
            case DEFAULT:
                //null gives unknown link used error
                break;
            default:
                break;
        }
    }

    /**
     * Hide all settings pages, and disable all buttons.
     */
    private void hideAll() {
        this.settingsStartingPage.setVisible(false);
        this.bankConnection.setVisible(false);
        this.notifications.setVisible(false);
        this.rules.setVisible(false);
        this.accessibility.setVisible(false);
        this.categories.setVisible(false);
        this.settingsDashboard.setVisible(false);
        this.settingsGDPR.setVisible(false);
        this.settingsAccount.setVisible(false);
        this.buttonBankConnection.setDisable(false);
        this.buttonAccessibility.setDisable(false);
        this.buttonCategories.setDisable(false);
        this.buttonGDPR.setDisable(false);
        this.buttonAccounts.setDisable(false);
    }
   /**
     * Show accessibility settings page.
     */
    @FXML
    public void showAccessibility() {
        this.hideAll();
        this.accessibility.setVisible(true);
        this.buttonAccessibility.setDisable(true);
    }

    /**
     * Show bank connection settings page.
     */
    @FXML
    public void showBankConnection() {
        this.hideAll();
        this.bankConnection.setVisible(true);
        this.buttonBankConnection.setDisable(true);
    }

    /**
     * Show categories settings page.
     */
    @FXML
    public void showCategories() {
        this.hideAll();
        this.categories.setVisible(true);
        this.buttonCategories.setDisable(true);
    }
    /**
     * Show GDPR settings page.
     */
    @FXML
    public void showGDPR(){
        this.hideAll();
        this.settingsGDPR.setVisible(true);
        this.buttonGDPR.setDisable(true);
    }
    /**
     * Show accounts settings page.
     */
    @FXML
    public void showAccounts(){
        this.hideAll();
        this.settingsAccount.setVisible(true);
        this.buttonAccounts.setDisable(true);
    }

    /**
     * Adds a new Account to the AccountRegistry.
     */
    @FXML
    public void addAccount(){
        AddAccountWindow addAccountWindow = new AddAccountWindow();
        refreshAccountChoice();
    }

    /**
     * Edits an existing Account in the AccountRegistry.
     */
    @FXML
    public void editAccount(){
        EditAccountWindows editAccountWindows = new EditAccountWindows(accountChoice.getValue());
        refreshAccountChoice();
    }

    /**
     * Deletes an existing Account in the AccountRegistry.
     */
    @FXML
    public void deleteAccount(){

        DeleteAccount deleteAccount = new DeleteAccount("Delete account", "Are you sure you want to delete this account, and all its Transactions?");
        if(deleteAccount.isYes){
            AccountRegistry.getInstance().removeAccount(accountChoice.getValue());
        }

        refreshAccountChoice();
    }

    /**
     * Deletes a user.
     */
    @FXML
    public void deleteUser(){
        DeleteAccount deleteAccount = new DeleteAccount("Delete User?", "This action will delete all off your data in our systems!");
        if(deleteAccount.isYes){
            DeleteUser deleteUser = new DeleteUser();
        }
    }

    @FXML
    public void removeAccountBankAPI() {}

    /**
     * Connects a bank account to the API.
     */
    @FXML
    public void addAccountBankAPI() {
        ScreenController.instance().changeScreen(Screens.CHOOSEBANK);
    }

    /**
     * Refreshes the syncronization of the accounts to the API.
     */
    @FXML
    public void syncAccountApi(){
        new TransactionSyncronice();
    }

    /**
     * Adds a new category to the category registry.
     */
    @FXML
    public void addCategory() {
        AddCategoryController addCategoryController = new AddCategoryController();
        this.categoryTable.getItems().add(addCategoryController.getCategory());
        this.categoryTable.refresh();
    }

    /**
     * Edits an existing category in the category registry.
     */
    private void editCategories(Category category) {
        new AddCategoryController(category);
        this.categoryTable.refresh();

    }

    /**
     * Deletes an existing category in the category registry.
     */
    private void deleteCategory(Category category) {
        new AddCategoryController(category,true);
        this.categoryTable.getItems().remove(category);
    }

    /**
     * Exports all user data to a CSV file.
     * @param event an event
     */
    @FXML
    private void getCSVFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName("UserData.csv");
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            WriteToFile writeToFile = new WriteToFile(filePath);
            writeToFile.writeAllUserData();
        }
    }


    @FXML
    public String textFontChoiceBox() {return this.textFontChoice.getValue();}

    /**
     * Refreshes the account choice box.
     */
    private void refreshAccountChoice(){
        List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();
        accountChoice.getItems().clear();
        accountChoice.getItems().addAll(accounts);
        if(accounts.size() != 0){
            accountChoice.setValue(accounts.get(0));
        }
    }

}
