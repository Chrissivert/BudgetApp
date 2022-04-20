package no.ntnu.idata1002.g08.screens;

/**
 * The enum Screens.
 * This enum is used to define all the screens in the application.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public enum Screens {
    /**
     * The Startup.
     */
    STARTUP("Select database", "/fxml/layout/startup.fxml", false),
    /**
     * Login screens.
     */
    LOGIN("Login", "/fxml/auth/login.fxml", false),
    /**
     * Registration screens.
     */
    REGISTRATION("Registration", "/fxml/auth/registration.fxml", false),
    /**
     * Dashboard screens.
     */
    DASHBOARD("Dashboard", "/fxml/dashboard/Dashboard.fxml", true),
    /**
     * Transactions screens.
     */
    TRANSACTIONS("Transactions", "/fxml/transactions/TransactionsWindow.fxml", true),
    /**
     * Budget screens.
     */
    BUDGET("BudgetPeriod", "/fxml/budget/budget.fxml", true),
    /**
     * Choosebank screens.
     */
    CHOOSEBANK("ChooseBank", "/fxml/bank/bankInit.fxml", false),
    /**
     * Settings screens.
     */
    SETTINGS("Settings", "/fxml/settings/settings.fxml", true),
    /**
     * Addaccount screens.
     */
    ADDACCOUNT("AddAccount", "/fxml/dialog/addAccount.fxml", false),

    /**
     * Budgethistory screens.
     */
    BUDGETHISTORY("BudgetHistory", "/fxml/budget/budgetHistory.fxml", true);

    private final String title;
    private final String fxml;
    private final boolean header;

    /**
     * Instantiates a new Screens.
     *
     * @param title the title
     * @param fxml the fxml path
     * @param header is in header
     */
    Screens(String title, String fxml, boolean header) {
        this.title = title;
        this.fxml = fxml;
        this.header = header;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets fxml.
     *
     * @return the fxml
     */
    public String getFxml() {
        return fxml;
    }

    /**
     * Gets header.
     *
     * @return the header
     */
    public boolean getHeader() {
        return header;
    }
}
