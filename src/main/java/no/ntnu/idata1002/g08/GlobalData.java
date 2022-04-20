package no.ntnu.idata1002.g08;

import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.User;

/**
 * Represents a singleton class that can be used to store global data in the application.
 *
 * @author Anders Lund
 * @version 24.04.2023
 */
public class GlobalData {
    private static GlobalData instance = null;
    private User user;
    private PersistenceUnit persistenceUnit;

    /**
     * Exists only to defeat instantiation.
     */
    private GlobalData() {}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized GlobalData getInstance() {
            if(instance == null) {
                instance = new GlobalData();
            }
            return instance;
        }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
            return user;
        }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(User user) {
            this.user = user;
        }

    /**
     * Reset global data.
     *
     * @return the global data
     */
    public static synchronized GlobalData reset() {
            if (AccountRegistry.isInitialized()) {
                AccountRegistry.getInstance().destroy();
            }
            if (CategoryRegistry.isInitialized()) {
                CategoryRegistry.getInstance().destroy();
            }
            instance = null;
            return getInstance();
        }

    /**
     * Gets persistence unit.
     *
     * @return the persistence unit
     */
    public PersistenceUnit getPersistenceUnit() {
        return persistenceUnit;
    }

    /**
     * Sets persistence unit.
     *
     * @param persistenceUnit the persistence unit
     */
    public void setPersistenceUnit(PersistenceUnit persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }
}