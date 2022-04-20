package no.ntnu.idata1002.g08.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a balance for an account at a given date.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "AccountBalance.deleteAll", query = "DELETE FROM AccountBalance ab WHERE ab.account.user.userId = :userId"),
    @NamedQuery(name = "AccountBalance.findAll", query = "SELECT ab FROM AccountBalance ab WHERE ab.account.user.userId = :userId"/*, hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)*/),
})
public class AccountBalance {
  @Id
  @GeneratedValue
  @Column(name = "accountBalanceId")
  private long AccountBalanceId;
  @JoinColumn(name = "accountId", nullable = false)
  @ManyToOne
  private Account account;
  @Temporal(TemporalType.DATE)
  @Column(name = "date")
  private Date date;
  @Column(name = "balance")
  private double balance;

  /**
   * Instantiates a new Account balance.
   *
   * @param date    the date
   * @param balance the balance
   */
  public AccountBalance(Date date, double balance) {
    setDate(date);
    setBalance(balance);
  }

  /**
   * Instantiates a new Account balance.
   */
  public AccountBalance() {
  }

  /**
   * Sets date.
   *
   * @param date the date
   */
  public void setDate(Date date) {
    if(date == null){
      throw new IllegalArgumentException("Date cannot be zero");
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    this.date = calendar.getTime();
  }

  /**
   * Gets date.
   *
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Sets balance.
   *
   * @param balance the balance
   */
  public void setBalance(double balance) {
    if(balance < 0){
      throw new IllegalArgumentException("Balance cannot be less then zero");
    }
    this.balance = balance;
  }

  /**
   * Gets balance.
   *
   * @return the balance
   */
  public double getBalance() {
    return balance;
  }

  /**
   * Sets account.
   *
   * @param account the account
   */
  public void setAccount(Account account) {
    this.account = account;
  }

  /**
   * Gets account.
   *
   * @return the account
   */
  public Account getAccount() {
    return account;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return AccountBalanceId;
  }
}
