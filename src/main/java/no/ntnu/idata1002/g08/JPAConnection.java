package no.ntnu.idata1002.g08;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Class for handling the connection to the database.
 *
 * @author Anders Lund
 * @version 20.04.2023
 */
public class JPAConnection {
    private static JPAConnection instance = null;
    private static EntityManagerFactory emf = null;


    /**
     * Exists only to defeat instantiation.
     */
    private JPAConnection(){}

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static synchronized JPAConnection getInstance() {
        if(instance == null) {
            instance = new JPAConnection();
        }
        return instance;
    }

    /**
     * Gets entity manager factory.
     *
     * @return the entity manager factory
     */
    public EntityManagerFactory getEntityManagerFactory() {
            if (emf == null) {
                PersistenceUnit selectedPersistenceUnit = GlobalData.getInstance().getPersistenceUnit();
                emf = getEntityManagerFactory(selectedPersistenceUnit == null ? PersistenceUnit.REMOTE_PERSISTENCE_UNIT : selectedPersistenceUnit);
            }
            if (emf == null) {
                throw new IllegalStateException("Could not connect to database");
            }
            return emf;
    }

    /**
     * Gets entity manager factory.
     *
     * @param persistenceUnit the persistence unit
     * @return the entity manager factory
     */
    public EntityManagerFactory getEntityManagerFactory(PersistenceUnit persistenceUnit) {
        emf = Persistence.createEntityManagerFactory(persistenceUnit.toString());
        return emf;
    }

    /**
     * Create entity manager.
     *
     * @return the entity manager
     */
    public EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
}