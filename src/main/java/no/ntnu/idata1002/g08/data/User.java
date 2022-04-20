package no.ntnu.idata1002.g08.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import no.ntnu.idata1002.g08.settings.ColorBlindMode;

/**
 * Represents a user in the Personal Economy App.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
})
@Table(uniqueConstraints= {
        @UniqueConstraint(columnNames = {"username"})
})
public class User {
    @Id
    @GeneratedValue
    @Column(name = "userId")
    private long userId;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "fullName", nullable = false)
    private String fullName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "salt", nullable = false)
    private byte[] salt;
    @Column(name = "colorBlindMode", nullable = false)
    private ColorBlindMode colorBlindMode;
    @Column(name = "bankId")
    private String bankId;

    /**
     * Create a new user.
     *
     * @param username Username.
     * @param fullName Full name.
     * @param password Password.
     * @param salt     Salt.
     */
    public User(String username, String fullName, String password, byte[] salt) {
        setUsername(username);
        setFullName(fullName);
        setPassword(password);
        setSalt(salt);
        setColorBlindMode(ColorBlindMode.NOT_COLORBLIND);
    }

    private void setSalt(byte[] salt) {
        if (salt == null) {
            throw new IllegalArgumentException("Salt cannot be null");
        }
        this.salt = salt;
    }

    private void setPassword(String password) {
        if (password == null || password.isEmpty() || password.isBlank() || password.length() < 6) {
            throw new IllegalArgumentException("Password has to be at least 6 characters long");
        }
        this.password = password;

    }

    private void setFullName(String fullName) {
        if (fullName == null || fullName.isEmpty() || fullName.isBlank() || fullName.trim().length() < 3) {
            throw new IllegalArgumentException("Full name has to be at least 3 characters long");
        }
        this.fullName = fullName;

    }

    private void setUsername(String username) {
        if (username == null || username.isEmpty() || username.isBlank() || username.length() < 4) {
            throw new IllegalArgumentException("Username has to be at least 4 characters long");
        }
        this.username = username;

    }

    /**
     * Instantiates a new User.
     */
    public User() {
    }

    /**
     * Change the colorblind setting for the user.
     *
     * @param colorBlindMode Colorblind mode to be changed to.
     */
    public void setColorBlindMode(ColorBlindMode colorBlindMode) {
        if (colorBlindMode == null) {
            throw new IllegalArgumentException("Colorblind mode cannot be null");
        }
        this.colorBlindMode = colorBlindMode;
    }

    /**
     * Get the colorblind setting value for this user.
     *
     * @return User colorblind settings.
     */
    public ColorBlindMode getColorBlindSetting() {
        return this.colorBlindMode;
    }

    /**
     * Get the user id.
     *
     * @return User id.
     */
    public long getId() {
        return userId;
    }

    /**
     * Get the username.
     *
     * @return Username. username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the full name of the user.
     *
     * @return Full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Get the salt used for hashing the password.
     *
     * @return Salt. byte [ ]
     */
    public byte[] getSalt() {
        return salt;
    }

    /**
     * Get the password.
     *
     * @return Password. password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the bank id.
     *
     * @return Bank id.
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * Set the bank id.
     *
     * @param bankId Bank id.
     */
    public void setBankId(String bankId) {
        if (bankId.isEmpty() || bankId.isBlank()) {
            throw new IllegalArgumentException("Bank id cannot be empty or blank");
        }
        this.bankId = bankId;
    }
}
