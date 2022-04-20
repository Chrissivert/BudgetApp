package no.ntnu.idata1002.g08.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.data.Account;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import java.sql.SQLException;

/**
 * A dialog used to add an account.
 */
public class AddAccountDialog {
    @FXML
    private HBox chooseAccountType;
    @FXML
    private VBox defaultAccount;
    @FXML
    private TextField defaultAccountName;
    private Stage dialogStage;

    /**
     * Set the dialog Stage.
     *
     * @param dialogStage The dialog Stage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Change scene so the user can create a normal Account.
     */
    @FXML
    private void addAccount(){
        chooseAccountType.setVisible(false);
        defaultAccount.setVisible(true);
    }

    /**
     *Create a new default account and add it to the database.
     *
     * @throws SQLException Throws SQLException if they are an error getting data from the SQL server
     */
    @FXML
    private void createNewDefaultAccount() throws SQLException {
        dialogStage.close();

        AccountDAO accountDAO = new AccountDAO();

        if(defaultAccountName.getText().isBlank()){
        }else{
             Account account = new Account();
             account.setName(defaultAccountName.getText());
             account.setAccountNumber(null);
             accountDAO.importBankAccount(account);
             dialogStage.close();
        }
    }

    /**
     * On press change screen to let the user connect to the API.
     *
     * @param Event JavaFX button event
     */
    @FXML
    private void OnNewBankId(ActionEvent Event) {
        ScreenController.instance().changeScreen(Screens.CHOOSEBANK);
        dialogStage.close();
    }
}
