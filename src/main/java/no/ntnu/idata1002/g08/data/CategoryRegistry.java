package no.ntnu.idata1002.g08.data;

import jakarta.persistence.PersistenceException;
import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.dao.CategoryDAO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for categories, to get and add categories to the database.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
public class CategoryRegistry {

  private static CategoryRegistry instance = null;

  /**
   * Gets instance.
   *
   * @return the instance
   * @throws IllegalStateException the illegal state exception
   */
  public static synchronized CategoryRegistry getInstance() throws IllegalStateException {
    if (GlobalData.getInstance().getUser() == null) {
      throw new IllegalStateException("User has to be initialized before accessing the registry");
    }
    if (instance == null) {
      instance = new CategoryRegistry();
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

  private final Map<Long, Category> categoryMap;

  /**
   * Instantiates a new Category registry.
   */
  private CategoryRegistry() {
    categoryMap = new ConcurrentHashMap<>();
    fillWithCategoriesFromDB();
  }

  /**
   * Fill category map with categories from db.
   */
  private void fillWithCategoriesFromDB() {
    CategoryDAO categoryDAO = new CategoryDAO();
    for (Category category : categoryDAO.getAllCategories()) {
      addCategoryFromDB(category);
    }
  }

  /**
   * Add or update category.
   *
   * @param category the category
   * @throws PersistenceException the persistence exception
   */
  public void addOrUpdateCategory(Category category) throws PersistenceException {
    CategoryDAO categoryDAO = new CategoryDAO();
    Category categoryFromDB = categoryDAO.addOrUpdateCategory(category);
    addCategoryFromDB(categoryFromDB);
  }

  /**
   * Add category to category map.
   * @param category the category to be added
   */
  private void addCategoryFromDB(Category category) {
    categoryMap.put(category.getId(), category);
  }

  /**
   * Gets category by name.
   *
   * @param name the name
   * @return the category by name
   */
  public Category getCategoryByName(String name) {
    for (Category category : categoryMap.values()) {
      if (category.getName().equals(name)) {
        return category;
      }
    }
    return null;
  }

  /**
   * Gets categories by type.
   *
   * @param type the type
   * @return the categories by type
   */
  public List<Category> getCategoriesByType(TransactionType type) {
    return categoryMap.values().stream().filter(category -> category.getType() == type).toList();
  }

  /**
   * Reset and refresh from db category registry.
   */
  public static void resetAndRefreshFromDB() {
    instance = null;
    getInstance();
  }

  /**
   * Gets all categories.
   *
   * @return the all categories
   */
  public List<Category> getAllCategories() {
    return categoryMap.values().stream().toList();
  }

  /**
   * Remove category.
   *
   * @param category the category
   * @throws PersistenceException the persistence exception
   */
  public void removeCategory(Category category) throws PersistenceException {
    CategoryDAO categoryDAO = new CategoryDAO();
    categoryDAO.removeCategory(category);
    removeCategoryFromDB(category);
  }

  /**
   * remove category from the DB
   *
   * @param category the category to be removed
   */
  private void removeCategoryFromDB(Category category) {
    categoryMap.remove(category.getId());
  }

  /**
   * Destroys the instance of the registry
   */
  public static void destroy() {
    instance = null;
  }
}

