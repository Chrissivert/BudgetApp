package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.Transaction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for handling transactions in the database.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class TransactionDAO {
    /**
     * Remove transactions.
     *
     * @param transactions the transactions
     * @throws PersistenceException the persistence exception
     */
    public void removeTransactions(List<Transaction> transactions) throws PersistenceException {
        if (transactions == null) {
            return;
        }
        for (Transaction transaction : transactions) {
            removeTransaction(transaction);
        }
    }

    /**
     * Remove transaction.
     *
     * @param transaction the transaction
     * @throws PersistenceException the persistence exception
     */
    public void removeTransaction(Transaction transaction) throws PersistenceException{
        if (transaction == null) {
            return;
        }
        EntityManager em = JPAConnection.getInstance().createEntityManager();
        EntityTransaction et = em.getTransaction();
        try {
            et.begin();
            Transaction transactionFromDB = em.find(Transaction.class, transaction.getId());
            transactionFromDB.setCategory(null);
            Transaction transactionWitoutCategory = addOrUpdateTransaction(transactionFromDB);
            em.remove(transactionWitoutCategory);
            et.commit();

            CategoryRegistry.getInstance().resetAndRefreshFromDB();
            AccountRegistry.getInstance().resetAndRefreshFromDB();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw new PersistenceException("Could not remove category", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Add or update transaction transaction.
     *
     * @param transaction the transaction
     * @return the transaction
     * @throws PersistenceException the persistence exception
     */
    public Transaction addOrUpdateTransaction(Transaction transaction) throws PersistenceException {
        EntityManager em = null;
        EntityTransaction et = null;
        CategoryDAO categoryDAO = new CategoryDAO();
        AccountDAO accountDAO = new AccountDAO();
        try {
            em = JPAConnection.getInstance().createEntityManager();
            et = em.getTransaction();
            et.begin();
            if (transaction.getAccount() != null) {
                Long accountId = transaction.getAccount().getId();
                Account accountFromDB = accountDAO.getAccountById(accountId);
                Account attachedAccount = em.find(Account.class, accountFromDB.getId());
                transaction.setAccount(attachedAccount);
            }
            if (transaction.getCategory() != null) {
                Long categoryId = transaction.getCategory().getId();
                Category categoryFromDB = categoryDAO.getCategoryById(categoryId);
                Category attachedCategory = em.find(Category.class, categoryFromDB.getId());
                transaction.setCategory(attachedCategory);
            }

            if (transaction.getId() == 0) {
                em.persist(transaction);
            } else {
                em.merge(transaction);
            }

            et.commit();

            CategoryRegistry.getInstance().resetAndRefreshFromDB();
            AccountRegistry.getInstance().resetAndRefreshFromDB();
        } catch (Exception e) {
            if (et != null  && et.isActive()) {
                et.rollback();
            }
            throw new PersistenceException("Error adding transaction", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return transaction;
    }

    /**
     * Add or update transactions list.
     *
     * @param transactions the transactions
     * @return the list
     * @throws PersistenceException the persistence exception
     */
    public List<Transaction> addOrUpdateTransactions(List<Transaction> transactions) throws PersistenceException {
        List<Transaction> transactionsToReturn = new ArrayList<>();
        try {
            for (Transaction transaction : transactions) {
                Transaction transactionFromDB = addOrUpdateTransaction(transaction);
                transactionsToReturn.add(transactionFromDB);
            }
        } catch (Exception e) {
            throw new PersistenceException("Error adding transaction", e);
        }
        return transactionsToReturn;
    }

    /**
     * Gets all transactions by account.
     *
     * @param account the account
     * @return the all transactions by account
     * @throws PersistenceException the persistence exception
     */
    public List<Transaction> getAllTransactionsByAccount(Account account) throws PersistenceException {
        EntityManager em = null;
        EntityTransaction et = null;
        List<Transaction> transactions;
        try {
            em = JPAConnection.getInstance().createEntityManager();
            et = em.getTransaction();
            et.begin();
            transactions = em.createNamedQuery("Transaction.findByAccount")
                    .setParameter("accountId", account.getId())
                    .getResultList();
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw new PersistenceException("Error getting transactions", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return transactions;
    }

    /**
     * Gets all transactions.
     *
     * @return the all transactions
     * @throws PersistenceException the persistence exception
     */
    public List<Transaction> getAllTransactions() throws PersistenceException {
        EntityManager em = null;
        EntityTransaction et = null;
        List<Transaction> transactions;
        try {
            em = JPAConnection.getInstance().createEntityManager();
            et = em.getTransaction();
            et.begin();
            transactions = em.createNamedQuery("Transaction.findAll")
                    .setParameter("userId", GlobalData.getInstance().getUser().getId())
                    .getResultList();
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw new PersistenceException("Error getting transactions", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return transactions;
    }

    /**
     * Gets transactions by categories and date.
     *
     * @param categories the categories
     * @param startDate  the start date
     * @param endDate    the end date
     * @return the transactions by categories and date
     * @throws PersistenceException the persistence exception
     */
    public Set<Transaction> getTransactionsByCategoriesAndDate(Set<Category> categories, java.util.Date startDate, java.util.Date endDate) throws PersistenceException {
        EntityManager em = null;
        EntityTransaction et = null;
        Set<Transaction> transactions = new HashSet<>();
        try {
            em = JPAConnection.getInstance().createEntityManager();
            et = em.getTransaction();
            et.begin();

            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

            for (Category category : categories) {
                List<Transaction> transactionsByCategory = em.createNamedQuery("Transaction.findByCategoryAndDates")
                        .setParameter("categoryId", category.getId())
                        .setParameter("startDate", sqlStartDate)
                        .setParameter("endDate", sqlEndDate)
                        .getResultList();
                if (transactionsByCategory != null && !transactionsByCategory.isEmpty()) {
                    transactions.addAll(transactionsByCategory);
                }
            }
            et.commit();
        } catch (Exception e) {
            if (et != null && et.isActive()) {
                et.rollback();
            }
            throw new PersistenceException("Error getting transactions", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return transactions;
    }
}
