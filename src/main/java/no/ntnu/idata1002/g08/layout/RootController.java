package no.ntnu.idata1002.g08.layout;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.auth.AuthService;
import no.ntnu.idata1002.g08.bank.TransactionSyncronice;
import no.ntnu.idata1002.g08.dialog.AccountCheck;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import no.ntnu.idata1002.g08.utils.HelpPage;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Represents the root controller for the application. This controller is used to control the header
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class RootController {


  /**
   * The Account and category check.
   */
  AccountCheck accountCheck = new AccountCheck();
  /**
   * The Top nav.
   */
  @FXML
  GridPane topNav;

  /**
   * The Logo wrapper.
   */
  @FXML
  HBox logoWrapper;

  /**
   * The Links.
   */
  @FXML
  HBox links;

  /**
   * The Right links.
   */
  @FXML
  HBox rightLinks;

  /**
   * The Dashboard link.
   */
  @FXML
  Label dashboardLink;
  /**
   * The Transaction link.
   */
  @FXML
  Label transactionLink;
  /**
   * The Budget link.
   */
  @FXML
  Label budgetLink;
  /**
   * The Sync account.
   */
  @FXML
  ImageView syncAccount;

  /**
   * Initialize.
   */
  @FXML
  public void initialize(){
    syncAccount.setVisible(false);
    if(GlobalData.getInstance().getUser().getBankId() != null){
      syncAccount.setVisible(true);
    }
  }

  /**
   * Goes to the screen that is clicked on
   *
   * @param event the event
   */
  @FXML
  public void goTo(MouseEvent event) {
    Node eventNode = null;
    try {
      eventNode = (Node) event.getSource();
      String id = eventNode.getId();
      switch (id) {
        case "dashboard", "logo":
          ScreenController.instance().changeScreen(Screens.DASHBOARD);
          accountCheck.changeScene();
          HelpPage.instance().setCurrentScreen(Screens.DASHBOARD);
          break;
        case "transactions":
          ScreenController.instance().changeScreen(Screens.TRANSACTIONS);
          accountCheck.changeScene();
          HelpPage.instance().setCurrentScreen(Screens.TRANSACTIONS);
          break;
        case "budget":
          ScreenController.instance().changeScreen(Screens.BUDGET);
          accountCheck.changeScene();
          HelpPage.instance().setCurrentScreen(Screens.BUDGET);
          break;
        case "settings":
          ScreenController.instance().changeScreen(Screens.SETTINGS);
          accountCheck.changeScene();
          HelpPage.instance().setCurrentScreen(Screens.SETTINGS);
          break;
        case "logout":
          break;
        case "help":
          HelpPage.instance().showHelpPage();
          break;
        default:
          throw new Exception("Unknown button");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Window currentWindow = eventNode.getScene().getWindow();
      showAlert(Alert.AlertType.ERROR, currentWindow, "Error", "Unknown link used");
    }
  }

  /**
   * Sync account.
   */
  @FXML
  public void syncAccount(){
    new TransactionSyncronice();
  }

  /**
   * Logs a user out of the application.
   *
   * @param event the event
   */
  @FXML
  public void logOut(MouseEvent event) {
    AuthService.logout();
    ScreenController.instance().changeScreen(Screens.LOGIN);
  }

  /**
   * Sets active screen.
   *
   * @param screen the screen
   */
  public void setActiveScreen(Screens screen) {
    switch (screen) {
      case DASHBOARD:
        dashboardLink.getStyleClass().add("selectedLabel");
        break;
      case TRANSACTIONS:
        transactionLink.getStyleClass().add("selectedLabel");
        break;
      case BUDGET:
        budgetLink.getStyleClass().add("selectedLabel");
        break;
    }
  }



}
