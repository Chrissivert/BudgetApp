package no.ntnu.idata1002.g08.data;

import jakarta.persistence.PersistenceException;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.dao.AccountDAO;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry for all accounts in database.
 */
public class AccountRegistry {

    private static AccountRegistry instance = null;

  /**
   * Gets instance.
   *
   * @return the instance
   * @throws IllegalStateException the illegal state exception
   */
  public static synchronized AccountRegistry getInstance() throws IllegalStateException {
      if (GlobalData.getInstance().getUser() == null) {
        throw new IllegalStateException("User has to be initialized before accessing the registry");
      }
      if (instance == null) {
        instance = new AccountRegistry();
      }
      return instance;
    }

  /**
   * Is initialized boolean.
   *
   * @return the boolean
   */
  public static boolean isInitialized() {
      return instance != null;
    }

    private final Map<Long, Account> accountMap;

    private AccountRegistry() {
      accountMap = new ConcurrentHashMap<>();
      fillWithAccountsFromDB();
    }

  /**
   * Adds an account to the database and updates the registry.
   * Can also be used to update an account.
   * @param account Account to add
   */
    private void addAccountFromDB(Account account) {
      accountMap.put(account.getId(), account);
    }

  /**
   * Reset and refresh from db account registry.
   */
  public static void resetAndRefreshFromDB() {
      instance = null;
      getInstance();
  }

  /**
   * Destroy.
   */
  public static void destroy() {
      instance = null;
    }

  private void fillWithAccountsFromDB() {
    AccountDAO accountDAO = new AccountDAO();
    for (Account account : accountDAO.getAllAccounts()) {
      addAccountFromDB(account);
    }
  }

  /**
   * Add or update account.
   *
   * @param account the account
   * @throws PersistenceException the persistence exception
   */
  public void addOrUpdateAccount(Account account) throws PersistenceException {
    AccountDAO accountDAO = new AccountDAO();
    Account accountFromDB = accountDAO.addOrUpdateAccount(account);
    addAccountFromDB(accountFromDB);
  }

  /**
   * Gets all accounts.
   *
   * @return the all accounts
   */
  public List<Account> getAllAccounts() {
    return accountMap.values().stream().toList();
  }

  /**
   * Remove account.
   *
   * @param account the account
   * @throws PersistenceException the persistence exception
   */
  public void removeAccount(Account account) throws PersistenceException {
    AccountDAO accountDAO = new AccountDAO();
    accountDAO.removeAccount(account);
    removeAccountFromMap(account);
  }

  private void removeAccountFromMap(Account account) {
    accountMap.remove(account.getId());
  }

}

