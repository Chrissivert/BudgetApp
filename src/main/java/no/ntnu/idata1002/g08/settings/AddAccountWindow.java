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
 * Represents the add account window in the settings menu.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class AddAccountWindow {

    @FXML
    private Button addButton;
    private VBox rootVBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField balanceField;

    private Account account;

    /**
     * Instantiates a new Add account window.
     */
    public AddAccountWindow(){
        init();
    }

    /**
     * Initialize the add account window.
     */
    private void init(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings/addAccount.fxml"));
        loader.setController(this);
        try {
            this.rootVBox = loader.load();
            this.balanceField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));
            this.addButton.setOnAction(this::addButton);
            Stage stage = new Stage();
            stage.setScene(new Scene(this.rootVBox));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * Adds account.
     * @param event the event
     */
    private void addButton(ActionEvent event){
        Account account = checkAccount();
        Stage stage = (Stage) this.addButton.getScene().getWindow();
        if(account != null){
            AccountRegistry.getInstance().addOrUpdateAccount(account);
            this.account = account;
            stage.close();
        }
    }

    /**
     * Check if account is valid.
     * @return the account
     */
    private Account checkAccount(){

        Account account = null;

        try{
            if(nameField.getText().isBlank() | nameField.getText().isEmpty()){
                throw new IllegalArgumentException("You need to give your account a name.");
            } else if (balanceField.getText().isBlank() | balanceField.getText().isEmpty()) {
                throw new IllegalArgumentException("You need to set your account balance.");
            }
            double amount = Double.parseDouble(balanceField.getText());
            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), amount);
            account = new Account(nameField.getText(), accountBalance);
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
