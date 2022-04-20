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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;


/**
 * Represents a budget in the Personal Economy application.
 * <p>
 * Id - a unique identifier for the budget.
 * Name - the name of the budget.
 * userID - a link to the user that created the budget.
 * periodLengthValue -
 * startDate - which day of the month the budget should start on.
 * recurring - Whether the budget should be recurring when the periodLengthValue is reached.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "BudgetPeriod.findAll", query = "SELECT b FROM BudgetPeriod b WHERE b.user.userId = :userId", hints = @QueryHint(name=QueryHints.REFRESH, value=HintValues.TRUE)),
        @NamedQuery(name = "BudgetPeriod.findById", query = "SELECT b FROM BudgetPeriod b WHERE b.budgetPeriodId = :budgetPeriodId AND b.user.userId = :userId", hints = @QueryHint(name=QueryHints.REFRESH, value=HintValues.TRUE)),
        @NamedQuery(name = "BudgetPeriod.deleteAll", query = "DELETE FROM BudgetPeriod b WHERE b.user.userId = :userId"),
})
public class BudgetPeriod {
    @Id
    @GeneratedValue
    @Column(name = "budgetPeriodId")
    private long budgetPeriodId;
    @Column(name = "name", nullable = false)
    private String name;
    @JoinColumn(name = "userId", nullable = false)
    @ManyToOne
    private User user;
    @Column(name = "periodLengthValue", nullable = false)
    private int periodLengthValue;
    @Column(name = "periodLengthUnit", nullable = false)
    private PeriodUnit periodLengthUnit;
    @Column(name = "startDate", nullable = false)
    private Date startDate;
    @JoinColumn(name = "budgets", nullable = false)
    @OneToMany(mappedBy = "budgetPeriod", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Budget> budgets;

    /**
     * The constant firstValidDate.
     */
    public static final Date firstValidDate = new GregorianCalendar(2020, 1, 1).getTime();
    /**
     * The constant firstValidDateString.
     */
    public static final String firstValidDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(firstValidDate);

    /**
     * The constant lastValidDate.
     */
    public static Date lastValidDate = getLastValidDate();
    /**
     * The constant lastValidDateString.
     */
    public static String lastValidDateString = DateFormat.getDateInstance(DateFormat.SHORT).format(lastValidDate);

    /**
     * Instantiates a new Budget period.
     *
     * @param name              the name
     * @param periodLengthValue the period length value
     * @param periodLengthUnit  the period length unit
     * @param startDate         the start date
     */
    public BudgetPeriod(String name, int periodLengthValue, PeriodUnit periodLengthUnit, Date startDate/*, boolean recurring*/) {
        setStartDate(startDate);
        setPeriodLengthValue(periodLengthValue);
        setPeriodLengthUnit(periodLengthUnit);
        setName(name);
    }

    /**
     * Creates a new BudgetPeriod.
     */
    public BudgetPeriod() {
    }


    /**
     * Accessor method for the budget's id.
     *
     * @return The budgetId.
     */
    public long getId() {
        return this.budgetPeriodId;
    }

    /**
     * Sets the name of the budget.
     *
     * @param name Cannot be null or empty. Throws IllegalArgumentException.
     */
    public void setName(String name) {
        if (name == null || name.isEmpty() || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        this.name = name;
    }

    /**
     * Accessor method for the budgets name.
     *
     * @return Name of the budget.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set how many days the budget should last.
     *
     * @param periodLengthValue The amount of days the budget should last.
     */
    public void setPeriodLengthValue(int periodLengthValue) {
        if (periodLengthValue < 1) {
            throw new IllegalArgumentException("Period length value must be greater than 0");
        }
        this.periodLengthValue = periodLengthValue;
    }

    /**
     * Get the total amount of days the budget will last.
     *
     * @return The amount of days the budget will last.
     */
    public int getPeriodLengthValue() {
        return this.periodLengthValue;
    }

    /**
     * Change the start date of the budget.
     *
     * @param startDate The new start date of the budget.
     */
    public void setStartDate(Date startDate) {
        if (startDate == null || startDate.before(firstValidDate)) {
            throw new IllegalArgumentException("Date startDate is invalid.");
        }
        this.startDate = startDate;
    }

    /**
     * Get the start date of the budget.
     *
     * @return The start date of the budget.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets period length unit.
     *
     * @param periodLengthUnit the period length unit
     */
    public void setPeriodLengthUnit(PeriodUnit periodLengthUnit) {
        if (periodLengthUnit == null) {
            throw new IllegalArgumentException("Period length unit is null.");
        }
        this.periodLengthUnit = periodLengthUnit;
    }

    /**
     * Gets period length unit.
     *
     * @return the period length unit
     */
    public PeriodUnit getPeriodLengthUnit() {
        return this.periodLengthUnit;
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
        if (user == null) {
            throw new IllegalArgumentException("User user is null.");
        }
        this.user = user;
    }

    /**
     * Gets budgets.
     *
     * @return the budgets
     */
    public Set<Budget> getBudgets() {
        return budgets;
    }

    /**
     * Gets the last valid date
     *
     * @return the last valid date
     */
    private static Date getLastValidDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

}
