package no.ntnu.idata1002.g08.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import java.io.IOException;
import java.util.Calendar;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Represents the window for editing an account
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class EditAccountWindows {
    @FXML
    private Button addButton;
    private VBox rootVBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;

    private Account account;

    /**
     * Instantiates a new Edit account windows.
     *
     * @param account the account to be edited
     */
    public EditAccountWindows(Account account){
        this.account = account;
        init();
    }

    /**
     * intialize the window
     */
    private void init(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings/editAccount.fxml"));
        loader.setController(this);
        try {
            this.rootVBox = loader.load();
            this.balanceField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
            this.addButton.setOnAction(this::addButton);
            nameField.setText(account.getName());
            balanceField.setText(account.getCurrentBalance() + "");
            Stage stage = new Stage();
            stage.setScene(new Scene(this.rootVBox));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * confirm the edit
     * @param event the event that triggered the method
     */
    private void addButton(ActionEvent event){
        Account account = checkAccount();
        Stage stage = (Stage) this.addButton.getScene().getWindow();
        if(account != null){
            AccountRegistry.getInstance().addOrUpdateAccount(account);
            stage.close();
        }
    }

    /**
     * check if the account is valid
     * @return the account if it is valid
     */
    private Account checkAccount(){

        Account account = this.account;
        try{
            if(nameField.getText().isBlank() | nameField.getText().isEmpty()){
                throw new IllegalArgumentException("You need to give your account a name.");
            } else if (balanceField.getText().isBlank() | balanceField.getText().isEmpty()) {
                throw new IllegalArgumentException("You need to set your account balance.");
            }
            double amount = Double.parseDouble(balanceField.getText());

            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), amount);

            account.setName(nameField.getText());
            account.addAccountBalance(accountBalance);

        }catch (IllegalArgumentException illegalArgumentException){
            showAlert(Alert.AlertType.ERROR, this.rootVBox.getScene().getWindow(), "Invalid input", illegalArgumentException.getLocalizedMessage());
        }

        return account;
    }

    /**
     * Get account account.
     *
     * @return the account
     */
    public Account getAccount(){
        return this.account;
    }
}
