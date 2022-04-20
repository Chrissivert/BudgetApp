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
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import java.util.Date;
import java.util.Set;

import static no.ntnu.idata1002.g08.data.BudgetPeriod.firstValidDate;

@Entity
@NamedQueries({
        @NamedQuery(name = "BudgetInterval.deleteByBudgetPeriodId", query = "DELETE FROM BudgetInterval b WHERE b.budget.budgetPeriod.budgetPeriodId = :budgetPeriodId"),
        @NamedQuery(name = "BudgetInterval.deleteAll", query = "DELETE FROM BudgetInterval b WHERE b.budget.budgetPeriod.user.userId = :userId"),
})
public class BudgetInterval {
    @Id
    @GeneratedValue
    @Column(name = "budgetIntervalId")
    private long budgetIntervalId;
    @JoinColumn(name = "budgetId", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Budget budget;
    @Column(name = "startDate", nullable = false)
    private Date startDate;
    @Column(name = "endDate", nullable = false)
    private Date endDate;
    @Column(name = "budgetedAmount", nullable = false)
    private double budgetedAmount;

    /**
     * Create a new BudgetPeriod Item Period.
     *
     * @param startDate The date the budget item period starts.
     * @param endDate The date the budget item period ends.
     * @param budgetedAmount The amount of money budgeted for this period.
     */
    public BudgetInterval(Budget budget, Date startDate, Date endDate, double budgetedAmount) {
        setStartDate(startDate);
        setEndDate(endDate);
        setBudgetedAmount(budgetedAmount);
        setBudget(budget);
    }

    public BudgetInterval() {
    }


    /**
     * Get the budget item periods id.
     * @return A unique identifier associated with this budget item period.
     */
    public long getId() {
        return this.budgetIntervalId;
    }

    /**
     * Set the budget linked to this budgetItemPeriod.
     * @param budget The BudgetWindow linked to this budgetItemPeriod.
     */
    public void setBudget(Budget budget) {
        if (budget == null) throw new IllegalArgumentException("Budget is invalid");
        this.budget = budget;
    }

    /**
     * Get the BudgetWindow linked to this budgetItemPeriod.
     * @return the budget linked to this budgetItemPeriod.
     */
    public Budget getBudget() {
        return this.budget;
    }

    /**
     * Set the start date of the budget item period.
     * @param startDate The date the budget item period starts.
     */
    public void setStartDate(Date startDate) {
        if (startDate == null || startDate.before(firstValidDate) || (endDate != null && startDate.after(endDate))) throw new IllegalArgumentException("Start date is invalid");
        this.startDate = startDate;
    }

    /**
     * Get the start date of this budget item period.
     * @return The date the budget item period starts.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Set the end date of the budget item period.
     * @param endDate The date the budget item period ends.
     */
    public void setEndDate(Date endDate) {
        if (endDate == null || endDate.before(firstValidDate) || (startDate != null && endDate.before(startDate))) throw new IllegalArgumentException("End date is invalid");
        this.endDate = endDate;
    }

    /**
     * Get the end date of this budget item period.
     * @return The date the budget item period ends.
     */
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     * Get the current amount used so far this period.
     * @return The total amount of money used so far this period.
     */
    public double getUsedAmount() {
        return calculateUsedAmount();
    }

    /**
     * Calculate the amount used so far this period.
     * @return The total amount of money used so far this period.
     */
    private double calculateUsedAmount() {
        double usedAmount = 0;
        TransactionDAO transactionDAO = new TransactionDAO();

        Set<Transaction> transactions = transactionDAO.getTransactionsByCategoriesAndDate(budget.getAllCategories(), startDate, endDate);
        for (Transaction transaction : transactions) {
            if (budget.getAllCategories().stream().anyMatch(category -> category.getId() == transaction.getCategory().getId())
                    && transaction.getDate().after(this.startDate) && transaction.getDate().before(this.endDate)) {
                usedAmount += transaction.getAmount();
            }
        }
        return usedAmount;
    }

    /**
     * Set an amount budgeted for this period.
     * @param budgetedAmount The amount of money budgeted for this period.
     */
    public void setBudgetedAmount(double budgetedAmount) {
        if (budgetedAmount < 1) throw new IllegalArgumentException("Budgeted amount cannot be negative");
        this.budgetedAmount = budgetedAmount;
    }

    /**
     * Get the budgeted amount for this period.
     * @return The amount of money budgeted for this period.
     */
    public double getBudgetedAmount() {
        return this.budgetedAmount;
    }
}
