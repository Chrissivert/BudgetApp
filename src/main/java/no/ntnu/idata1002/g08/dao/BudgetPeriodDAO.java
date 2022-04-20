package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.User;
import java.util.List;

/**
 * BudgetPeriodDAO is used to send and get data from the Database.
 *
 * @author Anders Lund
 * @version 26.04.2023
 */
public class BudgetPeriodDAO {

  /**
   * Add or update budget period.
   *
   * @param budgetPeriod The budgetPeriod to be added or updated
   * @return return the budgetPeriod
   */
  public BudgetPeriod addOrUpdateBudgetPeriod(BudgetPeriod budgetPeriod) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      User currentUser = em.find(User.class, GlobalData.getInstance().getUser().getId());
      budgetPeriod.setUser(currentUser);
      if (budgetPeriod.getId() == 0) {
        em.persist(budgetPeriod);
      } else {
        em.merge(budgetPeriod);
      }
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not add or update budgetPeriod", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return budgetPeriod;
  }

  /**
   * Get all budgetPeriods as a List.
   *
   * @return Return all budgetPeriods as a List
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public List<BudgetPeriod> getAllBudgetPeriods() throws PersistenceException{
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    List<BudgetPeriod> budgetPeriods;
    try {
      et.begin();
      budgetPeriods = em.createNamedQuery("BudgetPeriod.findAll")
              .setParameter("userId", GlobalData.getInstance().getUser().getId())
              .getResultList();
      et.commit();
    }
    catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not find budgetPeriods", e);
    }
    finally {
      if (em != null) {
        em.close();
      }
    }
    return budgetPeriods;
  }

  /**
   * Delete budgetPeriod by Id.
   *
   * @param id The budget period Id
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void deleteBudgetPeriodById(long id) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      Query query = em.createNamedQuery("BudgetPeriod.findById")
              .setParameter("userId", GlobalData.getInstance().getUser().getId())
              .setParameter("budgetPeriodId", id);
      BudgetPeriod budgetPeriodFromDB = (BudgetPeriod) query.getSingleResult();
      BudgetDAO budgetDAO = new BudgetDAO();
      budgetDAO.deleteBudgetsByBudgetPeriodId(id);
      em.remove(budgetPeriodFromDB);
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not delete budget", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  /**
   * Remove the budgetPeriod.
   *
   * @param budgetPeriod Remove the budgetPeriod by budgetPeriod.
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void removeBudgetPeriod(BudgetPeriod budgetPeriod) throws PersistenceException {
    long id = budgetPeriod.getId();
    deleteBudgetPeriodById(id);
  }
}
