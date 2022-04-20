package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AccountDao is used to send and get data from the Database.
 *
 * @author Anders Lund
 * @version 23.04.2023
 */
public class AccountDAO {

  /**
   * Add or update the selected account.
   *
   * @param account The account to update or delete.
   * @return return the account.
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public Account addOrUpdateAccount(Account account) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();

    try {
      et.begin();
      User currentUser = em.find(User.class, GlobalData.getInstance().getUser().getId());
      account.setUser(currentUser);
      Map<Date, AccountBalance> accountBalances = account.getAccountBalances();
      accountBalances.forEach((date, accountBalance) -> {
        if (accountBalance.getId() == 0) {
          em.persist(accountBalance);
        } else {
          em.merge(accountBalance);
        }
      });

      if (account.getId() == 0) {
        em.persist(account);
      } else {
        em.merge(account);
      }
      et.commit();
    } catch (PersistenceException e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw e;
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return account;
  }

  /**
   * Import bank accounts.
   *
   * @param account the account to add.
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void importBankAccount(Account account) throws PersistenceException {
    List<Account> accounts = new ArrayList<>();
    accounts.add(account);
    importBankAccounts(accounts);
  }

  /**
   * Import all the accounts form a list.
   *
   * @param accounts the accounts list to be added to database
   * @return return the accounts from database
   * @throws PersistenceException
   */
  public Set<Account> importBankAccounts(List<Account> accounts) throws PersistenceException {
    Iterator<Account> iterator = accounts.iterator();
    Set<Account> accountsFromDB = new HashSet<>();

    if (accounts.size() == 0) {
      return null;
    }

    List<Account> existingAccounts = getAccountsByAccountNumbers(accounts.stream().map(Account::getAccountNumber).toArray(String[]::new));

    while (iterator.hasNext()) {
      Account account = iterator.next();

      if (existingAccounts.isEmpty()) {
        Account accountFromDB = addOrUpdateAccount(account);
        accountsFromDB.add(accountFromDB);
        continue;
      }
      existingAccounts.stream().forEach(existingAccount -> {
        if (existingAccount.getAccountNumber().equals(account.getAccountNumber())) {
          Account accountFromDB = addOrUpdateAccount(account);
          accountsFromDB.add(accountFromDB);
        }
      });
    }
    return accountsFromDB;
  }

  /**
   * Get account by its accountNumber.
   *
   * @param accountNumber The account Number.
   * @return return null
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public Account getAccountByAccountNumber(String accountNumber) throws PersistenceException {
    String[] accountNumbers = {accountNumber};
    List<Account> accounts = getAccountsByAccountNumbers(accountNumbers);
    if (accounts.size() > 0) {
      return accounts.get(0);
    }
    return null;
  }

  /**
   *Get accountList by accountList Numbers.
   *
   * @param accountNumbers get accounts by account numbers
   * @return return the accounts
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public List<Account> getAccountsByAccountNumbers(String[] accountNumbers) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    List<Account> accounts = new ArrayList<>();

    try {
      et.begin();
      Query query = em.createNamedQuery("Account.findByAccountNumbers", Account.class);
      query.setParameter("accountNumbers", Arrays.asList(accountNumbers));
      query.setParameter("userId", GlobalData.getInstance().getUser().getId());
      accounts = query.getResultList();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error getting accounts by accountNumbers", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return accounts;
  }

  /**
   * Return all accounts in List.
   *
   * @return return all accounts in list
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public List<Account> getAllAccounts() throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    List<Account> accounts = new ArrayList<>();

    try {
      et.begin();
      Query query = em.createNamedQuery("Account.findAll", Account.class);
      query.setParameter("userId", GlobalData.getInstance().getUser().getId());
      accounts = query.getResultList();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error getting accounts", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }

    return accounts;
  }

  /**
   * Return the selected account.
   *
   * @param account the account to remove
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void removeAccount(Account account) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();

    try {
      TransactionDAO transactionDAO = new TransactionDAO();
      et.begin();
      Account accountFromDB = em.find(Account.class, account.getId());
      List<Transaction> transactions = transactionDAO.getAllTransactionsByAccount(accountFromDB);
      transactionDAO.removeTransactions(transactions);
      em.remove(accountFromDB);
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error removing account", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  /**
   * Get account by its id.
   *
   * @param id the account id
   * @return return the account
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public Account getAccountById(long id) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    Account account = null;

    try {
      et.begin();
      Query query = em.createNamedQuery("Account.findById", Account.class)
              .setParameter("accountId", id)
              .setParameter("userId", GlobalData.getInstance().getUser().getId());
      account = (Account) query.getSingleResult();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error getting account by id", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return account;
  }

  /**
   * Delete all account Balance.
   *
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void deleteAllAccountBalances() throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();

    try {
      et.begin();
      Query query = em.createNamedQuery("AccountBalance.deleteAll", AccountBalance.class);
      query.setParameter("userId", GlobalData.getInstance().getUser().getId());
      query.executeUpdate();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error deleting all account balances", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  /**
   * Get all account Balance of the user.
   *
   * @return return all account Balance in a List
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public List<AccountBalance> getAllAccountBalances() throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    List<AccountBalance> accountBalances;

    try {
      et.begin();
      Query query = em.createNamedQuery("AccountBalance.findAll", AccountBalance.class);
      query.setParameter("userId", GlobalData.getInstance().getUser().getId());
      accountBalances = query.getResultList();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Error getting account balances", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return accountBalances;
  }
}
