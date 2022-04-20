package no.ntnu.idata1002.g08.settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.data.Account;
import java.io.IOException;

/**
 * The type Delete account.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class DeleteAccount {

    private VBox rootVBox;
    @FXML
    private Button deleteAccountYes;
    @FXML
    private Button deleteAccountNo;
    @FXML
    private Label content;
    @FXML
    private Label title;
    private String newContent;
    private String newTitle;
    private Account account;

    /**
     * The Is yes.
     */
    boolean isYes = false;

    /**
     * Instantiates a new Delete account.
     *
     * @param titel   the titel
     * @param content the content
     */
    public DeleteAccount(String titel, String content){
        this.account = account;
        newTitle = titel;
        newContent = content;
        init();
    }

    /**
     * Initialize the delete account window.
     */
    private void init(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/settings/deleteAccount.fxml"));
        loader.setController(this);
        try {
            this.rootVBox = loader.load();
            this.deleteAccountYes.setOnAction(this::delete);
            this.deleteAccountNo.setOnAction(this::cancel);
            this.title.setText(newTitle);
            this.content.setText(newContent);
            Stage stage = new Stage();
            stage.setScene(new Scene(this.rootVBox));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /**
     * Delete the account.
     * @param event the event that triggers the method.
     */
    private void delete(ActionEvent event){
        Stage stage = (Stage) this.deleteAccountYes.getScene().getWindow();
        isYes = true;
        stage.close();
    }

    /**
     * Cancel the delete account.
     * @param event
     */
    private void cancel(ActionEvent event){
        Stage stage = (Stage) this.deleteAccountNo.getScene().getWindow();
        stage.close();
    }

    /**
     * Get the boolean value.
     * @return the boolean value.
     */
    private boolean getBoolean(){
        return this.isYes;
    }

}
