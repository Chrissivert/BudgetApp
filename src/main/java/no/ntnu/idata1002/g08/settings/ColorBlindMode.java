package no.ntnu.idata1002.g08.settings;

/**
 * The enum Color blind mode.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public enum ColorBlindMode {
    /**
     * The Not colorblind.
     */
    NOT_COLORBLIND ("Trichromacy(Not colorblind)","/fxml/settings/trichromacy.css"),
    /**
     * The Protanopia.
     */
    PROTANOPIA ("Protanopia(Red colorblind)","/fxml/settings/protanopia.css"),
    /**
     * The Deuteranopia.
     */
    DEUTERANOPIA ("Deuteranopia(Green colorblind)","/fxml/settings/deuteranopia.css"), //også rød-grønn fargeblind, men forskjellig
    /**
     * The Tritanopia.
     */
    TRITANOPIA ("Tritanopia(Blue colorblind)","/fxml/settings/tritanopia.css");


    private final String title;
    private final String css;

    /**
     * Instantiates a new Color blind mode.
     * @param title the title.
     * @param css  the css path.
     */
    ColorBlindMode(String title,String css){
        this.title = title;
        this.css = css;
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
     * Gets css.
     *
     * @return the css
     */
    public String getCss() {
        return css;
    }
}
