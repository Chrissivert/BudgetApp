package no.ntnu.idata1002.g08.settings;

/**
 * The enum Settings page.
 * This enum is use to open the correct settings page on settings screen change.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public enum SettingsPage {
     /**
      * Default settings page.
      */
     DEFAULT(""),
     /**
      * Bankconnection settings page.
      */
     BANKCONNECTION ("bankConnection"),
     /**
      * Notifications settings page.
      */
     NOTIFICATIONS("notifications"),
     /**
      * Rules settings page.
      */
     RULES("rules"),
     /**
      * Accessibility settings page.
      */
     ACCESSIBILITY("accessibility"),
     /**
      * Categories settings page.
      */
     CATEGORIES ("categories"),
     /**
      * Account settings page.
      */
     ACCOUNT("account"),
     /**
      * Settingsdashboard settings page.
      */
     SETTINGSDASHBOARD ("settingsDashboard"),
     /**
      * Gdpr settings page.
      */
     GDPR("GDPR");

     private final String settingsPage;

     /**
      * Instantiates a new Settings page.
      * @param settingsPage the settings page
      */
     SettingsPage(String settingsPage) {
         this.settingsPage = settingsPage;
     }

     /**
      * Gets settings page.
      *
      * @return the settings page
      */
     public String getSettingsPage() {
         return settingsPage;
     }
}
