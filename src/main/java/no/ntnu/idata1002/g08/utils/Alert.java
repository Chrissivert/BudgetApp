package no.ntnu.idata1002.g08.utils;

import javafx.stage.Window;

/**
 * Util clas that is used to show the user a AlertWindow.
 * This can be used when there is an error we want to show
 * the user.
 *
 * @author Anders Lund
 * @version 26.03.2023
 */
public class Alert {
  /**
   * Shows the user a popup windows that can display error or other
   * usefully information for the user.
   *
   * @param alertType The type off alert to show the user
   * @param owner The window of a node from the scene
   * @param title The title of the window
   * @param message The message of the window
   */
  public static void showAlert(javafx.scene.control.Alert.AlertType alertType, Window owner, String title, String message) {
    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.initOwner(owner);
    alert.show();
  }
}
