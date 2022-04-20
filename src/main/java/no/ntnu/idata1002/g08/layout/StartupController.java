package no.ntnu.idata1002.g08.layout;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.PersistenceUnit;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Controller for the startup screen before login.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class StartupController {

    /**
     * Initialize the local login window.
     */
    @FXML
  public void initLocalMode() {
    try {
      GlobalData.getInstance().setPersistenceUnit(PersistenceUnit.LOCAL_PERSISTENCE_UNIT);
      JPAConnection.getInstance().getEntityManagerFactory(PersistenceUnit.LOCAL_PERSISTENCE_UNIT);
      startApplication();
    } catch (Exception e) {
      Window currentWindow = ScreenController.instance().getCurrentScene().getWindow();
      showAlert(Alert.AlertType.ERROR, currentWindow, "Error", "Could not connect to database. Please try again or choose a different connection mode.");
    }
  }

    /**
     * Initialize the remote login window.
     */
    @FXML
  public void initRemoteMode() {
    try {
      GlobalData.getInstance().setPersistenceUnit(PersistenceUnit.REMOTE_PERSISTENCE_UNIT);
      JPAConnection.getInstance().getEntityManagerFactory(PersistenceUnit.REMOTE_PERSISTENCE_UNIT);
      startApplication();
    } catch (Exception e) {
      e.printStackTrace();
      Window currentWindow = ScreenController.instance().getCurrentScene().getWindow();
      showAlert(Alert.AlertType.ERROR, currentWindow, "Error", "Could not connect to database. Please check your internet connection and try again.");
    }
  }

  /**
   * Starts the application.
   */
  private void startApplication() {
    ScreenController.instance().changeScreen(Screens.LOGIN);
  }
}
