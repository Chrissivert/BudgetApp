package no.ntnu.idata1002.g08.auth;

import jakarta.persistence.PersistenceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Handle the registration of new users.
 *
 * @author Anders Lund
 * @version 23.07.2023
 */
public class RegistrationController {
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitButton;

    /**
     * Create a new user, and let them login or go a scene
     * where they can add Bank ID.
     *
     * @param event JavaFX event on press
     */
    @FXML
    public void register(ActionEvent event) {
        Window owner = submitButton.getScene().getWindow();
        String errorTitle = "Registration Error!";

        if (fullNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, errorTitle,
                    "Please enter your full name");
            return;
        }
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
        String fullName = fullNameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            AuthService authService = new AuthService();
            String result = authService.register(username, password, fullName);
            if (result == "success") {
                Node eventNode = (Node) event.getSource();
                String nodeId = eventNode.getId();

                if (nodeId.equals("submitButton")) {
                    ScreenController.instance().changeScreen(Screens.DASHBOARD);
                } else if (nodeId.equals("submitBankButton")) {
                    ScreenController.instance().changeScreen(Screens.CHOOSEBANK);
                }
//                showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration Successful!",
//                        "Welcome " + fullName);
            } else if (result == "Username already exists") {
                showAlert(Alert.AlertType.ERROR, owner, "Registration Failed!",
                        "Username already exists. Please try again.");
            } else if (result == "Password is too short") {
                showAlert(Alert.AlertType.ERROR, owner, "Registration Failed!",
                        "The password has to be at least 6 characters long. Please try again.");
            } else {
                showAlert(Alert.AlertType.ERROR, owner, "Registration Failed!",
                        "Something went wrong. Please try again.");
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, owner, "Registration Failed!",
                    "Something went wrong when accessing the database. Please try again.\nRemember that to use the remote database, you need to be connected to the NTNU-network or their VPN.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, owner, "Registration Failed!",
                    "Something went wrong. Please try again.");
        }
    }

    /**
     * When press on Login, go to Login scene.
     *
     * @param event JavaFX event on press
     */
    @FXML
    private void goToLogin(ActionEvent event) {
        ScreenController.instance().changeScreen(Screens.LOGIN);
    }
}
