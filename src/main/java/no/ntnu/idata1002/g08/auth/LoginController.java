package no.ntnu.idata1002.g08.auth;

import jakarta.persistence.PersistenceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.dialog.AccountCheck;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * The controller to the LoginPage.
 * Get the input data and action event form
 * the user, and utilize the data.
 *
 * @author Anders Lund
 * @version 23.04.2023
 */
public class LoginController {
    AccountCheck accountCheck = new AccountCheck();
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitButton;

    /**
     * On press get the user data and check it with the database.
     * If correct, login else error message.
     */
    @FXML
    public void login(/*ActionEvent event*/) {
        Window owner = submitButton.getScene().getWindow();
        String errorTitle = "Login Error!";
        if (usernameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter your username");
            return;
        }
        if (passwordField.getText().isEmpty() || passwordField.getText().length() < 6) {
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter a password that is at least 6 characters long");
            return;
        }
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            AuthService authService = new AuthService();
            String result = authService.login(username, password);
            if (result == "success") {
                ScreenController.instance().changeScreen(Screens.DASHBOARD);
                accountCheck.changeScene();
            } else if (result == "Username does not exist" || result == "Wrong password" ) {
                showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                        result);
            } else {
                showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                        "Something went wrong. Please try again.");
            }
        } catch (PersistenceException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                        "Something went wrong when accessing the database. Please try again.\nRemember that to use the remote database, you need to be connected to the NTNU-network or their VPN.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,"Something went wrong. Please try again.");
        }

    }

    /**
     * when user press enter, login.
     *
     * @param event JavaFx event on button press
     */
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            login();
        }
    }

    /**
     * On registration press, change scene.
     *
     * @param event JavaFX event on button press
     */
    @FXML
    private void goToRegistration(ActionEvent event) {
        ScreenController.instance().changeScreen(Screens.REGISTRATION);
    }
}
