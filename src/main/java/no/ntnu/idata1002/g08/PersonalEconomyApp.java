package no.ntnu.idata1002.g08;

import javafx.application.Application;
import javafx.stage.Stage;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;

/**
 * Launcher for the PersonalEconomyApp.
 *
 * @author  Anders Lund
 * @version 24.04.2023
 */
public class PersonalEconomyApp extends Application {
    @Override
    public void start(Stage stage) {
        ScreenController.instance().init(stage);
        ScreenController.instance().changeScreen(Screens.STARTUP);
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}