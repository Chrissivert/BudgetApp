package no.ntnu.idata1002.g08.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an account, with a name, account number, balance and a list of transactions.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a WHERE a.user.userId = :userId", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Account.findByAccountNumbers", query = "SELECT a FROM Account a WHERE a.accountNumber IN :accountNumbers AND a.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
        @NamedQuery(name = "Account.findById", query = "SELECT a FROM Account a WHERE a.accountId = :accountId AND a.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
        @NamedQuery(name = "Account.deleteAll", query = "DELETE FROM Account a WHERE a.user.userId = :userId"),
})
@Table(uniqueConstraints= {
        @UniqueConstraint(columnNames = {"accountNumber"})
})
public class Account {
  @Id
  @GeneratedValue
  @Column(name = "accountId", nullable = false)
  private long accountId;
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "accountNumber")
  private String accountNumber;
  @JoinColumn(name = "userId", nullable = false)
  @ManyToOne
  private User user;
  @JoinColumn(name = "accountBalance", nullable = false)
  @OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH})
  @MapKeyTemporal(TemporalType.DATE)
  private Map<Date, AccountBalance> accountBalances = new HashMap<>();

  /**
   * Creates an account with the given name, account number, and account balance.
   *
   * @param name           the name of the account
   * @param accountNumber  the account number of the account
   * @param accountBalance the account balance of the account
   */
  public Account(String name, String accountNumber, AccountBalance accountBalance) {
    setName(name);
    setAccountNumber(accountNumber);
    addAccountBalance(accountBalance);
    accountBalance.setAccount(this);
  }

  /**
   * Creates an account with the given name and account balance.
   *
   * @param name           the name of the account
   * @param accountBalance the account balance of the account
   */
  public Account(String name, AccountBalance accountBalance) {
    setName(name);
    addAccountBalance(accountBalance);
  }

  /**
   * Creates an empty account.
   */
  public Account() {
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null");
    }
    if(name.isEmpty() | name.isBlank()){
      throw new IllegalArgumentException("Name cannot be blank");
    }
    this.name = name;
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
   * Sets account number.
   *
   * @param accountNumber the account number
   */
  public void setAccountNumber(String accountNumber) {
    if(accountNumber == null){
      this.accountNumber = accountNumber;
    }else if(accountNumber.isBlank() | accountNumber.isEmpty()){
      throw new IllegalArgumentException("accountNumber cannot be blank");
    }
    this.accountNumber = accountNumber;
  }

  /**
   * Gets account number.
   *
   * @return the account number
   */
  public String getAccountNumber() {
    return accountNumber;
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
   * Gets user.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return accountId;
  }

  /**
   * Gets account balances.
   *
   * @return the account balances
   */
  public Map<Date, AccountBalance> getAccountBalances() {
    return accountBalances;
  }

  /**
   * Gets account balance by date.
   *
   * @param date the date
   * @return the account balance by date
   */
  public AccountBalance getAccountBalanceByDate(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND,0);
    cal.set(Calendar.MILLISECOND, 0);
    date = cal.getTime();
    return accountBalances.get(date);
  }

  /**
   * Add account balance.
   *
   * @param accountBalance the account balance
   */
  public void addAccountBalance(AccountBalance accountBalance) {
    if(accountBalance.getBalance() < 0){
      throw new IllegalArgumentException("Balance cannot be less than zero");
    }
    if(accountBalance.getDate() == null){
      throw new IllegalArgumentException("You need to set a date");
    }
    this.accountBalances.put(accountBalance.getDate(), accountBalance);
    accountBalance.setAccount(this);
  }

  /**
   * Gets current balance.
   *
   * @return the current balance
   */
  public double getCurrentBalance() {
    return calculateCurrentBalance();
  }

  private double calculateCurrentBalance() {

    double currentBalance = 0;
    if (accountBalances.size() > 0) {
      AccountBalance lastAccountBalance = accountBalances.get(accountBalances.keySet().toArray()[accountBalances.size() - 1]);
      AccountBalance tempAccountBalance = new AccountBalance(lastAccountBalance.getDate(), lastAccountBalance.getBalance());
      currentBalance = tempAccountBalance.getBalance();
      Date lastAccountBalanceDate = tempAccountBalance.getDate();
      TransactionDAO transactionDAO = new TransactionDAO();
      List<Transaction> transactions = transactionDAO.getAllTransactions();

      List<Transaction> transactionsFiltered = transactions.stream()
              .filter(transaction -> transaction.getAccount().getId().equals(accountId))
              .filter(transaction -> transaction.getDate().getTime() >= lastAccountBalanceDate.getTime())
              .toList();

    for (Transaction transaction : transactionsFiltered) {
        if (transaction.getType() == TransactionType.EXPENSE) {
          currentBalance -= transaction.getAmount();
        } else if (transaction.getType() == TransactionType.INCOME) {
          currentBalance += transaction.getAmount();
        }
      }
    }
    return currentBalance;
  }

  /**
   * Calculate balance by date double.
   *
   * @param date the date
   * @return the double
   */
  public double calculateBalanceByDate(Date date) {
    double balance = 0;
    if (accountBalances.size() > 0) {
      AccountBalance lastAccountBalance = accountBalances.get(date);
      AccountBalance tempAccountBalance = new AccountBalance(lastAccountBalance.getDate(), lastAccountBalance.getBalance());
      balance = tempAccountBalance.getBalance();
      TransactionDAO transactionDAO = new TransactionDAO();
      List<Transaction> transactions = transactionDAO.getAllTransactions();

      List<Transaction> transactionsFiltered = transactions.stream()
              .filter(transaction -> transaction.getAccount().getId().equals(accountId))
              .filter(transaction -> transaction.getDate().getTime() > date.getTime())
              .toList();

      for (Transaction transaction : transactionsFiltered) {
        if (transaction.getType() == TransactionType.EXPENSE) {
          balance -= transaction.getAmount();
        } else if (transaction.getType() == TransactionType.INCOME) {
          balance += transaction.getAmount();
        }
      }
    }
    return balance;
  }

  /**
   * Gets account Name.
   * @return the account Name
   */
  @Override
  public String toString() {
    return getName();
  }
}
