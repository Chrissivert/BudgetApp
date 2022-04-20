package no.ntnu.idata1002.g08.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import java.util.Set;

/**
 * Represents a category for transactions, with a name and a type, and a list of transactions connected to it.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c WHERE c.user.userId = :userId", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Category.findByType", query = "SELECT c FROM Category c WHERE c.type = :type AND c.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
        @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE c.name = :name AND c.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
        @NamedQuery(name = "Category.findById", query = "SELECT c FROM Category c WHERE c.categoryId = :categoryId AND c.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
})
public class Category {
  @Id
  @GeneratedValue
  @Column(name = "categoryId", nullable = false)
  private long categoryId;
  @Column(name = "name", nullable = false)
  private String name;
  @JoinColumn(name = "userId",nullable = false)
  @ManyToOne
  private User user;
  @Column(name = "type", nullable = false)
  private TransactionType type;

  @OneToMany(mappedBy = "category", fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
  private Set<Transaction> transactions;

  /**
   * Instantiates a new Category.
   *
   * @param name the name
   * @param type the type
   */
  public Category(String name, TransactionType type) {
    setName(name);
    setType(type);
  }

  /**
   * Instantiates a new Category.
   */
  public Category() {
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return categoryId;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    if(name == null){
      throw new IllegalArgumentException("Name cannot be null");
    }else if(name.isEmpty() | name.isBlank()){
      throw new IllegalArgumentException("Name cannot be blank or empty");
    }
    this.name = name;
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
   * Gets type.
   *
   * @return the type
   */
  public TransactionType getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(TransactionType type) {
    if(type == null){
      throw new IllegalArgumentException("TransactionType cannot be zero");
    }
    this.type = type;
  }

  /**
   * Gets transactions.
   *
   * @return the transactions
   */
  public Set<Transaction> getTransactions() {
    return transactions;
  }

  /**
   * Gets category name.
   * @return the category name
   */
  public String toString() {
    return getName();
  }
}
