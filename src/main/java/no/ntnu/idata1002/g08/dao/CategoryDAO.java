package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.TransactionType;
import no.ntnu.idata1002.g08.data.User;
import java.util.List;

/**
 * CategoryDao is used to send and get data from the Database.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class CategoryDAO {

  /**
   * Add or update category.
   *
   * @param category The category to be added to the database
   * @return return the category
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public Category addOrUpdateCategory(Category category) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      User currentUser = em.find(User.class, GlobalData.getInstance().getUser().getId());
      category.setUser(currentUser);
      if (category.getId() == 0) {
        em.persist(category);
      } else {
        em.merge(category);
      }
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not add category", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return category;
  }

  /**
   * Return all the categories in a List.
   *
   * @return return all the categories
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public List<Category> getAllCategories() throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    List<Category> categories;
    try {
      et.begin();
      categories = em.createNamedQuery("Category.findAll")
              .setParameter("userId", GlobalData.getInstance().getUser().getId())
              .getResultList();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not find categories", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return categories;
  }

  /**
   * Get category by category Id.
   *
   * @param id The category id
   * @return return the category
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public Category getCategoryById(long id) throws PersistenceException{
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    Category category;
    try {
      et.begin();
      Query query = em.createNamedQuery("Category.findById")
              .setParameter("userId", GlobalData.getInstance().getUser().getId())
              .setParameter("categoryId", id);
      category = (Category) query.getSingleResult();
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not find category", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
    return category;
  }

  /**
   * Remove category by category Id.
   *
   * @param category The category to be deleted
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void removeCategory(Category category) throws PersistenceException{
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      Long id = category.getId();
      Category categoryFromDB = em.find(Category.class, id);
      category.getTransactions().forEach(transaction -> {
        transaction.setCategory(null);
        em.merge(transaction);
      });
      em.remove(categoryFromDB);
      et.commit();
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
}
