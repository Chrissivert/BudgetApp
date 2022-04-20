package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.*;

import java.util.List;
import java.util.Set;

/**
 * BudgetDao is used to send and get data from the Database.
 *
 * @author Anders Lund
 * @version 23.04.2023
 */
public class BudgetDAO {

  /**
   * Add or update the budget.
   *
   * @param budget the budget to update
   * @return return the budget
   */
  public Budget addOrUpdateBudget(Budget budget) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();

      BudgetPeriod currentBudgetPeriodFromDB = em.find(BudgetPeriod.class, budget.getBudgetPeriod().getId());
      budget.setBudgetPeriod(currentBudgetPeriodFromDB);

      Set<BudgetInterval> intervals = budget.getAllIntervals();

      for (BudgetInterval interval : intervals) {
        if (interval.getId() == 0) {
          em.persist(interval);
        } else {
          em.merge(interval);
        }
      }

      if (budget.getId() == 0) {
        em.persist(budget);
      } else {
        em.merge(budget);
      }
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not add or update budget", e);
    } finally {
      if (em != null) {
          em.close();
      }
    }
    return budget;
  }

  /**
   * Delete budgets by budgets periode ID.
   *
   * @param id The budget periode id
   */
  public void deleteBudgetsByBudgetPeriodId(long id) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      deleteBudgetItervalsByBudgetPeriodId(id);
      Query query = em.createNamedQuery("Budget.deleteByBudgetPeriodId")
              .setParameter("budgetPeriodId", id);
      query.executeUpdate();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not delete budgets", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  /**
   * Delete budgets Iterval by BudgetPeriodeId.
   *
   * @param id Budget Periode Id used to delete budgets Itervals
   */
  private void deleteBudgetItervalsByBudgetPeriodId(long id) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      Query query = em.createNamedQuery("BudgetInterval.deleteByBudgetPeriodId")
              .setParameter("budgetPeriodId", id);
      query.executeUpdate();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not delete budget intervals", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

  public void deleteBudget(Budget originalBudget) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();

    try {
      et.begin();
      Budget budgetFromDB = em.find(Budget.class, originalBudget.getId());
      em.remove(budgetFromDB);
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
}
