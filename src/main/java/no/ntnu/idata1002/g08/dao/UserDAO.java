package no.ntnu.idata1002.g08.dao;

import jakarta.persistence.*;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.JPAConnection;
import no.ntnu.idata1002.g08.data.User;

/**
 * UserDao is used to send and get data from the Database.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class UserDAO {

  /**
   * Add or update the user.
   *
   * @param user The user to be added or updated
   * @return Return the user
   */
  public User addOrUpdateUser(User user) {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      if (user.getId() == 0) {
        em.persist(user);
      } else {
        em.merge(user);
      }
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not add or update user", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }

    return user;
  }

  /**
   * Get user by UserName.
   *
   * @param username The userName used to get user
   * @return Return the user
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public User getUserByUsername(String username) throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    User user = null;
    try {
      Query query = em.createNamedQuery("User.findByUsername", User.class);
      query.setParameter("username", username);
      user = (User) query.getSingleResult();
    } catch(NoResultException e) {
      return null;
    } catch (Exception e) {
      throw new PersistenceException("Could not find user", e);
    } finally {
      em.close();
    }
    return user;
  }

  /**
   * Delete the user.
   *
   * @throws PersistenceException Throws PersistenceException if something goes wrong
   */
  public void deleteUser() throws PersistenceException {
    EntityManager em = JPAConnection.getInstance().createEntityManager();
    EntityTransaction et = em.getTransaction();
    try {
      et.begin();
      em.remove(em.find(User.class, GlobalData.getInstance().getUser().getId()));
      et.commit();
    } catch (Exception e) {
      if (et != null && et.isActive()) {
        et.rollback();
      }
      throw new PersistenceException("Could not delete user", e);
    } finally {
      if (em != null) {
        em.close();
      }
    }
  }

}
