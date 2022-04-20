package no.ntnu.idata1002.g08.screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import no.ntnu.idata1002.g08.Budget.BudgetController;
import no.ntnu.idata1002.g08.Budget.BudgetHistoryController;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.data.Budget;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.layout.RootController;
import no.ntnu.idata1002.g08.settings.SettingsController;
import no.ntnu.idata1002.g08.settings.SettingsPage;
import no.ntnu.idata1002.g08.transactions.TransactionsController;
import no.ntnu.idata1002.g08.utils.HelpPage;
import java.io.IOException;
import java.net.URL;
import static no.ntnu.idata1002.g08.utils.Alert.showAlert;

/**
 * Class for controlling which screen is shown, and which data is passed to the screen.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class ScreenController {

    /**
     * The constant instance.
     */
    public static ScreenController instance = null;
    /**
     * The constant applicationName.
     */
    public static String applicationName = "Personal Economy";

    private Scene currentScene;
    private Stage currentStage;
    private SettingsPage settingsPage = SettingsPage.DEFAULT;
    private String categoryFilterTransactionPage;
    private Budget activeBudget;
    private BudgetPeriod activeBudgetPeriod;
    private boolean helpPage = false;

    private ScreenController() {
    }

    /**
     * Instance screen controller.
     *
     * @return the screen controller
     */
    public static synchronized ScreenController instance() {
        if (instance == null) {
            instance = new ScreenController();
        }
        return instance;
    }

    /**
     * Init.
     *
     * @param stage the stage
     */
    public void init(Stage stage) {
        currentStage = stage;
        currentStage.show();
      currentScene = new Scene(new Parent() {
      }, 800, 500);
    }

    /**
     * Change screen.
     *
     * @param screen the screen
     */
    public void changeScreen(Screens screen) {
        try {
            Parent root = null;
            RootController rootController = null;
            if (currentScene == null || currentStage == null) {
                throw new IllegalStateException("ScreenController has not been initialized");
            }
            String fxml = screen.getFxml();
            String stageTitle = applicationName + " - " + screen.getTitle();

            if (fxml == null || stageTitle == null) {
                throw new IllegalStateException("Screen is missing fxml or title");
            }

            URL fxmlUrl = getClass().getResource(fxml);

            if (fxmlUrl == null) {
                throw new IllegalStateException("FXML file not found");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            Parent screenRoot = loader.load();

            if (screen.getHeader()) {
                FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/fxml/layout/root.fxml"));
                BorderPane wrapper = rootLoader.load();
                rootController = rootLoader.getController();
                wrapper.setCenter(screenRoot);
                root = wrapper;
                root.getStylesheets().add(GlobalData.getInstance().getUser().getColorBlindSetting().getCss());
            } else {
                root = screenRoot;
            }

            double currentWidth = currentScene.getWidth();
            double currentHeight = currentScene.getHeight();

            currentScene = new Scene(root, currentWidth, currentHeight);
            currentStage.setTitle(stageTitle);
            currentStage.setScene(currentScene);

            initScreens(screen, loader, rootController);

        } catch (IOException e) {
            e.printStackTrace();
            Window currentWindow = currentStage.getScene().getWindow();
            showAlert(Alert.AlertType.ERROR, currentWindow, "Error", "Failed to change screen");
        }
    }

    /**
     * Set help page, for showing overview help.
     *
     * @param help true if help page is to be shown
     */
    public void setHelpPage(boolean help){
        this.helpPage = help;
    }

    /**
     * Sets settings page.
     *
     * @param page the page
     */
    public void setSettingsPage(SettingsPage page) {
        settingsPage = page;
    }

    /**
     * Sets category filter transaction page.
     *
     * @param category the category
     */
    public void setCategoryFilterTransactionPage(String category) {
        categoryFilterTransactionPage = category;
    }

    /**
     * Init screens and set active screen.
     *
     * @param screen the screen to be shown
     * @param loader the loader to load the screen
     * @param rootController the root controller to handle the interaction with header
     */
    private void initScreens(Screens screen, FXMLLoader loader, RootController rootController) {
        if (rootController != null) {
            rootController.setActiveScreen(screen);
        }
        switch (screen) {
            case BUDGET -> {
                BudgetController budgetController = loader.getController();
                budgetController.init(activeBudgetPeriod);
                activeBudgetPeriod = null;
            }
            case SETTINGS -> {
                SettingsController settingsController = loader.getController();
                settingsController.openSpesificSettings(settingsPage);
                settingsPage = SettingsPage.DEFAULT; // if null unknown link used error
            }
            case TRANSACTIONS -> {
                TransactionsController transactionsController = loader.getController();
                transactionsController.startWithCategoryFilter(categoryFilterTransactionPage);
            }
            case BUDGETHISTORY -> {
                BudgetHistoryController budgetHistoryController = loader.getController();
                budgetHistoryController.init(activeBudget);
//                activeBudget = null;
            }

        }
        if(helpPage){
            HelpPage.instance().showOverviewHelp();
            helpPage = false;
        }
    }

    /**
     * Gets current scene.
     *
     * @return the current scene
     */
    public Scene getCurrentScene() {
        return currentScene;
    }

    /**
     * Sets active budget.
     *
     * @param budget the budget
     */
    public void setActiveBudget(Budget budget) {
        this.activeBudget = budget;
  }

    /**
     * Sets active budget period.
     *
     * @param budgetPeriod the budget period
     */
    public void setActiveBudgetPeriod(BudgetPeriod budgetPeriod) {
        this.activeBudgetPeriod = budgetPeriod;
  }

}
