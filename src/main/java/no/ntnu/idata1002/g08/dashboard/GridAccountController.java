package no.ntnu.idata1002.g08.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Adds accounts to the Dashboard.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class GridAccountController {
    @FXML
    private Label accountNameFXML;
    @FXML
    private Label accountAmountFXML;

    /**
     * Set the name of the Account grid.
     *
     * @param name The name of the Account
     */
    public void setName(String name) {
        accountNameFXML.setText(name);
    }

    /**
     * Set the amount of the Account grid.
     *
     * @param amount The amount of the Account
     */
    public void setAmount(double amount) {
        accountAmountFXML.setText(amount + " kr");
    }
}
