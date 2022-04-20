package no.ntnu.idata1002.g08.bank;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.dao.UserDAO;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.User;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.awt.Desktop;
import java.net.URI;
import java.rmi.AccessException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This Class handle the GUI when the user connect to
 * the Bank. It parses user input to the ApiRequest and
 * lets the user connect the Application to their Bank.
 * This class can be used on new users to connect to the
 * bank, refresh userAgreement, add more accounts from
 * database and even other banks.
 *
 * @author Daniel Nest
 * @version 27.04.2023
 */
public class APIGetBankController {
    ApiRequest apiRequest = new ApiRequest();
    AccountDAO accountDAO = new AccountDAO();
    Map<String, String> bankList = new HashMap<String, String>();
    int maxDays;
    String bankId = null;
    long userId;
    List<AccountClass> accountList = new ArrayList<AccountClass>();
    @FXML
    private HBox hBoxTest;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private Button getAccountsButton;
    @FXML
    private HBox hBox;
    @FXML
    private VBox vBox;
    @FXML
    private VBox lastStep;
    @FXML
    private Button NextButton;
    @FXML
    private TextField maxAmountOfDays;
    @FXML
    private TextField maxAmountOfValidDays;
    @FXML
    private Text amountOfDaysText;
    @FXML
    private VBox lastStepbank;

    /**
     * Initialize the GUI.
     */
    @FXML
    private void initialize() {
        userId = GlobalData.getInstance().getUser().getId();
        getBankList();
        nodeHide(lastStep);
        nodeHide(getAccountsButton);
        nodeHide(hBoxTest);
    }

    /**
     * Get the User BankId and update it to the database.
     *
     * @param instituteId The instituteId of the bank
     * @param agreement the agreement from the user
     * @throws Exception throws an exception if the sql could not be updated
     */
    private void getBankId(String instituteId, String agreement) throws Exception {
        String website = "https://www.danielneset.no/SystemProsjekt.html";

        String linkJson = apiRequest.getLink(website, instituteId, UUID.randomUUID().toString(), agreement, "NO");
        Object obj = JSONValue.parse(linkJson);
        JSONObject jsonObject = (JSONObject) obj;
        URI url = new URI(jsonObject.get("link").toString());

        openWebpage(url);

        nodeShow(getAccountsButton);
        bankId = jsonObject.get("id").toString();

        User user = GlobalData.getInstance().getUser();
        user.setBankId(bankId);
        UserDAO userDAO = new UserDAO();
        userDAO.addOrUpdateUser(user);
    }

    /**
     * The last step where its create all the accounts and transactions and
     * put them in the database. Afterwards proceeds to the Dashboard screen.
     *
     * @param event JavaFx event when the button done is pressed
     * @throws SQLException Throws SqlException if it cannot connect to database
     */
    @FXML
    private void done(ActionEvent event) throws SQLException {

        TransactionDAO transactionDAO = new TransactionDAO();
        for (AccountClass account: accountList) {
            String accountNumber = account.getAccountNumber();
            String name = account.getName();

            double amount = account.getAmount();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            AccountBalance accountBalance = new AccountBalance(date, amount);
            Account accountDatabase = new Account(name, accountNumber,accountBalance);
            accountDAO.importBankAccount(accountDatabase);

            Account x = accountDAO.getAccountByAccountNumber(accountDatabase.getAccountNumber());

            List<Transaction> transactions = apiRequest.getTransactionsAsObject(x);

            transactionDAO.addOrUpdateTransactions(transactions);
        }
        ScreenController.instance().changeScreen(Screens.DASHBOARD);
    }

    /**
     * InnerClass used to help when parsing accounts.
     */
    class AccountClass{
        private double amount;
        private String name;
        private String accountNumber;
        public AccountClass(String accountNumber, String Name, double amount){
            this.accountNumber = accountNumber;
            this.name = Name;
            this.amount = amount;
        }
        public void setName(String name){
            if(name.isBlank()||name.isEmpty()){
                //empty
            }else{
                this.name = name;
            }
        }
        public String getAccountNumber(){
            return accountNumber;
        }
        public double getAmount() {
            return amount;
        }
        public String getName(){
            return name;
        }
    }

    /**
     * Shows the user all the accounts they selected in the BankId
     * web setup. They have the ability to change their account name
     * before continuing.
     *
     * @param event JavaFx event when the button done is pressed
     * @throws Exception Throws Exception when somethings go wrong
     */
    @FXML
    private void getAccounts(ActionEvent event) throws Exception
    {
        List<Account> exsistingAccounts = accountDAO.getAllAccounts();

        nodeHide(lastStep);
        nodeHide(getAccountsButton);
        nodeShow(hBoxTest);
        nodeShow(lastStepbank);

        String accountsList = apiRequest.getAccount(bankId);

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(accountsList);
        jo.get("accounts");
        JsonArray list = new JsonArray();
        list = jo.get("accounts").getAsJsonArray();

        for (JsonElement jsonElement : list) {
            String accountBalance = apiRequest.getBalance(jsonElement.getAsString());
            String accountName = apiRequest.getDetails(jsonElement.getAsString());

            JsonParser jsonParser2 = new JsonParser();

            JsonObject details = (JsonObject)jsonParser2.parse(accountName);
            try{
                accountName = details.get("account").getAsJsonObject().get("name").getAsString();
            }catch (NullPointerException nullPointerException){
                accountName = "UserAccount";
            }

            JsonObject jsonObject = (JsonObject)jsonParser2.parse(accountBalance);
            JsonArray transactions2 = jsonObject.get("balances").getAsJsonArray();
            double accountBalanceInt = transactions2.get(0).getAsJsonObject().get("balanceAmount").getAsJsonObject().get("amount").getAsDouble();

            AccountClass accountclass = new AccountClass(jsonElement.getAsString(), accountName, accountBalanceInt);
            accountList.add(accountclass);
        }

        if(exsistingAccounts.size() == 0){
            //Run as normal
        }else {
            //Remove from list
            for (int i = 0; i < accountList.size(); i++) {
                for (Account account : exsistingAccounts){
                    if(accountList.get(i).getAccountNumber().equals(account.getAccountNumber())){
                        accountList.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        if(accountList.size() == 0){
            ScreenController.instance().changeScreen(Screens.DASHBOARD);
        }

        TextField[] textFields = new TextField[accountList.size()];
        for (int i = 0; i < textFields.length; i++) {
            VBox container = new VBox();
            Label content = new Label(accountList.get(i).getName());
            Label amount = new Label(accountList.get(i).getAmount() + " kr");
            textFields[i] = new TextField();
            textFields[i].setText(accountList.get(i).getName());
            Button button = new Button("Edit Account Name");

            container.getChildren().addAll(content, amount, textFields[i], button);
            //add button here
            int finalI = i;
            button.setOnAction(e -> {
                // Loop through the text fields and retrieve the text from each one
                for (TextField textField : textFields) {
                    String text = textField.getText();
                    if(text.isEmpty()||text.isBlank()){
                    }else{
                        content.setText(text);
                        accountList.get(finalI).setName(text);
                    }
                }
            });
            hBoxTest.getChildren().add(container);
        }
    }

    /**
     * Uses the Name from the bankList and find the Bank ID, that is used for generate the link and
     * let the user connect to BankID.
     *
     * @param event JavaFx event when the button done is pressed
     */
    @FXML
    private void selectBank(ActionEvent event) throws AccessException {
        Window owner = NextButton.getScene().getWindow();
        String errorTitle = "Bank Error!";

        if(comboBox.getValue() == null){
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please select an bank from the dropdown menu.");
            return;
        }
        if(maxAmountOfValidDays.getText().isEmpty()){
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter amount of days that we should be allowed to access you transactions data.");
            return;
        }
        if(Integer.parseInt(maxAmountOfValidDays.getText()) > 90){
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter an amount that is less or equal to the max amount.");
            return;
        }
        if(maxAmountOfDays.getText().isEmpty()){
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter amount of days that we should be allowed to access you transactions data.");
            return;
        }
        if(Integer.parseInt(maxAmountOfDays.getText()) > maxDays){
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter an amount that is less or equal to the max amount.");
            return;
        }

        String institudeId = null;
        boolean found = false;

        Iterator<Map.Entry<String, String>> it = bankList.entrySet().iterator();
        while (it.hasNext() && !found) {
            Map.Entry<String, String> pair = it.next();
            if (pair.getValue().equals(comboBox.getValue())) {
                institudeId = pair.getKey();
                found = true;
            }
        }

        nodeHide(comboBox);
        nodeHide(vBox);
        nodeShow(lastStep);

        hBox.setVisible(false);
        hBox.managedProperty().bind(hBox.visibleProperty());

        String endUserAgreement = apiRequest.getEndUserAgreement(institudeId, maxAmountOfDays.getText(), maxAmountOfValidDays.getText());
        try {
            getBankId(institudeId, endUserAgreement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return a string of all the banks
     *
     * @return return a string of all the banks names that is put into an comboBox.
     */
    private String getBankList() {
        String bankList;
        bankList = apiRequest.getBankList();
        String returnBankList = bankList;
        JsonArray obj = new JsonParser().parse(bankList).getAsJsonArray();
        ArrayList<String> id = new ArrayList<String>();
        for (int i = 0; i < obj.size(); i++) {
            JsonElement propertiesJson = obj.get(i);
            JsonObject obj3 = propertiesJson.getAsJsonObject();
            id.add(obj3.get("name").getAsString());
            this.bankList.put(obj3.get("id").getAsString(), obj3.get("name").getAsString());
        }

        comboBox.setItems(FXCollections.observableArrayList(id));

        comboBox.setOnAction((event) -> {
            for (int i = 0; i < obj.size(); i++) {
                JsonElement propertiesJson = obj.get(i);
                JsonObject obj3 = propertiesJson.getAsJsonObject();
                if(obj3.get("name").getAsString().equals(comboBox.getValue())){
                    amountOfDaysText.setText("Transaction History, max: " + obj3.get("transaction_total_days") + " days");
                    maxDays = obj3.get("transaction_total_days").getAsInt();
                }
            }
        });
        maxAmountOfDays.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    maxAmountOfDays.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        maxAmountOfValidDays.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    maxAmountOfValidDays.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        return returnBankList;
    }




    /**
     * Helper classes.
     */

    /**
     * Return true if the browser was open.
     *
     * @param uri the URL to the webpage in the browser
     * @return return true if the webpage was open
     */
    private static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        boolean hasExecuted = false;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                hasExecuted = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hasExecuted;
    }

    /**
     * Helper class to hide a node.
     *
     * @param node the node is an JavaFX element one wish to hide
     */
    private void nodeHide(Node node){
        node.setManaged(false);
        node.setVisible(false);
    }

    /**
     * Helper class to show a node.
     *
     * @param node the node is an JavaFX element one wish to show
     */
    private void nodeShow(Node node){
        node.setManaged(true);
        node.setVisible(true);
    }

    /**
     * Used to show an error message to the user.
     *
     * @param alertType what type of alert it is
     * @param owner the Window owner
     * @param title the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}



