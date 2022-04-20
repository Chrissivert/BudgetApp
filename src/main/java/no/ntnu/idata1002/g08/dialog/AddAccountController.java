package no.ntnu.idata1002.g08.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Window;
import javafx.util.converter.DoubleStringConverter;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import no.ntnu.idata1002.g08.utils.Alert;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is used for when a user open the application without an account.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class AddAccountController {
    @FXML
    private TextField accountName;
    @FXML
    private TextField accountBalance;

    /**
     * This method is used to initialize the controller.
     */
    @FXML
    private void initialize(){
        this.accountBalance.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
    }

    /**
     * This method is used to create an account.
     *
     * @param event the event that is triggered when the user clicks the button.
     */
    @FXML
    private void createAccount(ActionEvent event){
        Window owner = accountName.getScene().getWindow();
        try{
            if (accountBalance.getText().isEmpty() | accountBalance.getText().isBlank()) {
                throw new IllegalArgumentException("Account Balance need to be a number!");
            } else if (accountName.getText().isEmpty() | accountName.getText().isBlank()) {
                throw new IllegalArgumentException("Account Name cannot be empty or just space!");
            }
            addAccount();

        }catch (IllegalArgumentException illegalArgumentException){
            Alert alert = new Alert();
            alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, owner, "Fault", illegalArgumentException.getMessage());
        }

        ScreenController.instance().changeScreen(Screens.DASHBOARD);
    }

    /**
     * This method is used to add an account.
     */
    private void addAccount(){
        double amount = Double.parseDouble(accountBalance.getText().toString());
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        AccountBalance accountBalanceObject = new AccountBalance(date, amount);
        Account account = new Account(accountName.getText(), accountBalanceObject);
        AccountRegistry.getInstance().addOrUpdateAccount(account);
    }
}
