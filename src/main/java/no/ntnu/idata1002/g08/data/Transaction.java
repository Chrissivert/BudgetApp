package no.ntnu.idata1002.g08.data;

import java.text.DateFormat;
import java.util.Date;
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
import jakarta.persistence.QueryHint;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;


/**
 * Represents a transaction in the system, which is a single expense or income.
 */
@Entity
@Table(uniqueConstraints= {
        @UniqueConstraint(columnNames = {"bankTransactionId"})
})
@NamedQueries(value = {
        @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t WHERE t.account.user.userId = :userId", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)),
        @NamedQuery(name = "Transaction.findByAccount", query = "SELECT t FROM Transaction t WHERE t.account.accountId = :accountId", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)),
        @NamedQuery(name = "Transaction.findByFilter", query = "" +
                "SELECT t FROM Transaction t " +
                "WHERE t.account.user.userId = :userId " +
                "AND (t.date >= :startDate OR :startDate IS NULL) " +
                "AND (t.date <= :endDate OR :endDate IS NULL)" +
                "AND (t.type = :type OR :type = -1) " +
                "AND (t.category.categoryId IN :categoryIds OR :categoryIdsSize = 0)" +
                "AND (t.account.accountId IN :accountIds OR :accountIdsSize = 0)" +
                "AND (t.amount >= :minAmount) " +
                "AND (t.amount <= :maxAmount)" +
                "ORDER BY t.date DESC", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)
        ),
        @NamedQuery(name = "Transaction.findByBankTransactionId", query = "SELECT t FROM Transaction t WHERE t.bankTransactionId = :bankTransactionId AND t.account.user.userId = :userId", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)),
        @NamedQuery(name = "Transaction.findById", query = "SELECT t FROM Transaction t WHERE t.transactionId = :transactionId AND t.account.user.userId = :userId", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)),
        @NamedQuery(name = "Transaction.findByCategoryAndDates", query = "SELECT t FROM Transaction t WHERE t.category.categoryId = :categoryId AND t.date BETWEEN :startDate AND :endDate", hints = @QueryHint(name = QueryHints.REFRESH, value = HintValues.TRUE)),
})

public class Transaction {
    @Id
    @GeneratedValue
    @Column(name = "transactionId", nullable = false)
    private long transactionId;
    @Column(name = "bankTransactionId", nullable = true)
    private String bankTransactionId;
    @Column(name = "date", nullable = false)
    private Date date;
    @Column(name = "expense", nullable = false)
    private TransactionType type;
    @Column(name = "description", nullable = true)
    private String description;
    @Column(name = "amount", nullable = false)
    private double amount;
    @JoinColumn(name = "categoryId")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Category category;
    @JoinColumn(name="accountId", nullable=false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Account account;
    @Column(name = "currency", nullable = false)
    private String currency = "NOK";

    /**
     * Instantiates a new Transaction.
     *
     * @param date     the date
     * @param type     the type
     * @param amount   the amount
     * @param account  the account
     * @param currency the currency
     */
    public Transaction(Date date, TransactionType type, double amount, Account account, String currency) {
        setDate(date);
        setType(type);
        setAmount(amount);
        setAccount(account);
        setCurrency(currency);
    }

    /**
     * Instantiates a new Transaction.
     *
     * @param transactionToClone the transaction to clone
     */
    public Transaction(Transaction transactionToClone) {
        if (transactionToClone == null) {
            return;
        }

        setBankTransactionId(transactionToClone.getBankTransactionId());
        setDate(transactionToClone.getDate());
        setType(transactionToClone.getType());
        setDescription(transactionToClone.getDescription());
        setAmount(transactionToClone.getAmount());
        setCategory(transactionToClone.getCategory());
        setAccount(transactionToClone.getAccount());
        setCurrency(transactionToClone.getCurrency());
    }

    /**
     * Instantiates a new Transaction.
     */
    public Transaction() {
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return transactionId;
    }

    /**
     * Sets bank transaction id.
     *
     * @param bankTransactionId the bank transaction id
     */
    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    /**
     * Gets bank transaction id.
     *
     * @return the bank transaction id
     */
    public String getBankTransactionId() {
        return bankTransactionId;
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
        this.date = date;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {return date;}

    /**
     * Gets formatted date.
     *
     * @return the formatted date
     */
    public String getFormattedDate() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
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
     * Gets type.
     *
     * @return the type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     */
    public void setAmount(double amount) {
        if(amount < 0){
            throw new IllegalArgumentException("Amount cannot be less then zero");
        }
        this.amount = amount;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(Category category) {
        if (category != null && type != category.getType()) {
            throw new IllegalArgumentException("Category type does not match transaction type");
        }
        this.category = category;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets account.
     *
     * @param account the account
     */
    public void setAccount(Account account) {
        if(account == null){
            throw new IllegalArgumentException("Account cannot be set to zero");
        }
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
     * Sets currency.
     *
     * @param currency the currency
     */
    public void setCurrency(String currency) {
        if(currency == null){
            throw new IllegalArgumentException("Currency cannot be zero");
        } else if (currency.isBlank() | currency.isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be empty or blank");
        }
        this.currency = currency;
    }

    /**
     * Gets currency.
     *
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }
}
