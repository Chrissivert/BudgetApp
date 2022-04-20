package no.ntnu.idata1002.g08.utils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.screens.Screens;

/**
 * This class is used to help the user to get a better understanding of the
 * application. It is a popup window that give the user information about different
 * screens and how to use them.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class HelpPage {
    /**
     * Singleton pattern
     */
    private static HelpPage instance = null;
    /**
     * Overview help page
     */
    @FXML
    private VBox helpPageOverview;
    /**
     * Dashboard help page
     */
    @FXML
    private VBox helpPageDashboard;
    /**
     * Transactions help page
     */
    @FXML
    private VBox helpPageTransactions;
    /**
     * Budget help page
     */
    @FXML
    private VBox helpPageBudget;
    /**
     * Settings help page
     */
    @FXML
    private VBox helpPageSettings;
    /**
     * Button for switching to OverView
     */
    @FXML
    private Button buttonOverview;
    /**
     * Button for switching to Dashboard
     */
    @FXML
    private Button buttonDashBoard;
    /**
     * Button for switching to Transactions
     */
    @FXML
    private Button buttonTransactions;
    /**
     * Button for switching to Budget
     */
    @FXML
    private Button buttonBudget;
    /**
     * Button for switching to Settings
     */
    @FXML
    private Button buttonSettings;
    /**
     * Root HBox
     */
    private HBox rootHBox;
    /**
     * Current main screen
     */
    private Screens currentScreen = Screens.DASHBOARD;
    /**
     * Stage
     */
    private final Stage stage = new Stage();
    /**
     * Constructor, empty and hidden
     */
    private HelpPage() {}

    /**
     * Singleton pattern
     *
     * @return instance of HelpPage
     */
    public static synchronized HelpPage instance() {
        if (instance == null) {
            instance = new HelpPage();}
        return instance;
    }

    /**
     * Set current screen
     *
     * @param screen current screen
     */
    public void setCurrentScreen(Screens screen){
        this.currentScreen = screen;
    }

    /**
     * Show help page
     */
    public void showHelpPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/helpPage.fxml"));
        loader.setController(this);
        try{
            this.rootHBox = loader.load();

            this.helpPageOverview.managedProperty().bind(this.helpPageOverview.visibleProperty());
            this.helpPageDashboard.managedProperty().bind(this.helpPageDashboard.visibleProperty());
            this.helpPageTransactions.managedProperty().bind(this.helpPageTransactions.visibleProperty());
            this.helpPageBudget.managedProperty().bind(this.helpPageBudget.visibleProperty());
            this.helpPageSettings.managedProperty().bind(this.helpPageSettings.visibleProperty());

            this.buttonOverview.setOnAction(e->showOverviewHelp());
            this.buttonDashBoard.setOnAction(e -> showDashboardHelp());
            this.buttonTransactions.setOnAction(e -> showTransactionsHelp());
            this.buttonBudget.setOnAction(e -> showBudgetHelp());
            this.buttonSettings.setOnAction(e -> showSettingsHelp());

            switch (this.currentScreen) {
                case DASHBOARD -> showDashboardHelp();
                case TRANSACTIONS -> showTransactionsHelp();
                case BUDGET -> showBudgetHelp();
                case SETTINGS -> showSettingsHelp();
                default -> showDashboardHelp();
            }

            stage.setScene(new Scene(rootHBox));
            stage.setTitle("Help page");
            if(stage.isShowing()){
                stage.toFront();
                stage.setIconified(false);
            }else {
                stage.showAndWait();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * show help page for overview
     */
    public void showOverviewHelp(){
        this.hideAllHelpPages();
        this.enableAllButtons();
        this.helpPageOverview.setVisible(true);
        this.buttonOverview.setDisable(true);
    }

    /**
     * show help page for dashboard
     */
    private void showDashboardHelp(){
        this.hideAllHelpPages();
        this.enableAllButtons();
        this.helpPageDashboard.setVisible(true);
        this.buttonDashBoard.setDisable(true);
    }

    /**
     * show help page for transactions
     */
    private void showTransactionsHelp(){
        this.hideAllHelpPages();
        this.enableAllButtons();
        this.helpPageTransactions.setVisible(true);
        this.buttonTransactions.setDisable(true);
    }

    /**
     * show help page for budget
     */
    private void showBudgetHelp(){
        this.hideAllHelpPages();
        this.enableAllButtons();
        this.helpPageBudget.setVisible(true);
        this.buttonBudget.setDisable(true);
    }

    /**
     * show help page for settings
     */
   private void showSettingsHelp(){
        this.hideAllHelpPages();
        this.enableAllButtons();
        this.helpPageSettings.setVisible(true);
        this.buttonSettings.setDisable(true);
    }

    /**
     * enable all buttons
     */
    private void enableAllButtons(){
        this.buttonOverview.setDisable(false);
        this.buttonDashBoard.setDisable(false);
        this.buttonTransactions.setDisable(false);
        this.buttonBudget.setDisable(false);
        this.buttonSettings.setDisable(false);
    }

    /**
     * hide all help pages
     */
    private void hideAllHelpPages(){
        this.helpPageOverview.setVisible(false);
        this.helpPageDashboard.setVisible(false);
        this.helpPageTransactions.setVisible(false);
        this.helpPageBudget.setVisible(false);
        this.helpPageSettings.setVisible(false);
    }
}
