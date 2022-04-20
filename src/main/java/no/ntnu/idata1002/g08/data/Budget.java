package no.ntnu.idata1002.g08.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.QueryHint;
import no.ntnu.idata1002.g08.dao.BudgetDAO;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a BudgetPeriod Item.
 *
 * @author Anders Lund
 * @version 27.04.2023
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Budget.findAll", query = "SELECT b FROM Budget b WHERE b.budgetPeriod.user.userId = :userId", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Budget.findAllByBudgetPeriodId", query = "SELECT b FROM Budget b WHERE b.budgetPeriod.budgetPeriodId = :budgetPeriodId", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Budget.findByName", query = "SELECT b FROM Budget b WHERE b.name = :name", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Budget.findById", query = "SELECT b FROM Budget b WHERE b.budgetId = :budgetId", hints = @QueryHint(name= QueryHints.REFRESH, value= HintValues.TRUE)),
        @NamedQuery(name = "Budget.deleteByBudgetPeriodId", query = "DELETE FROM Budget b WHERE b.budgetPeriod.budgetPeriodId = :budgetPeriodId"),
        @NamedQuery(name = "Budget.deleteAll", query = "DELETE FROM Budget b WHERE b.budgetPeriod.user.userId = :userId"),
})
public class Budget {
    @Id
    @GeneratedValue
    @Column(name = "budgetId")
    private long budgetId;
    @Column(name = "name", nullable = false)
    private String name;
    @JoinColumn(name = "budgetPeriodId", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private BudgetPeriod budgetPeriod;

    @JoinColumn
    @OneToMany(mappedBy = "budget", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Map<Date, BudgetInterval> intervals = new HashMap<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "categoryId")
    private Map<Long, Category> categories = new HashMap<>();

    private double initialBudgetedAmount;


    /**
     * Creates a new BudgetWindow.
     *
     * @param name         the name
     * @param budgetPeriod the budget period
     * @param amount       the amount
     * @param categories   the categories
     */
    public Budget(String name, BudgetPeriod budgetPeriod, double amount, List<Category> categories) {
        setName(name);
        setBudgetPeriod(budgetPeriod);
        setBudgetedAmount(amount);
        setCategories(categories);
        generateIntervals();
    }

    /**
     * Sets categories.
     *
     * @param categories the categories
     */
    public void setCategories(List<Category> categories) {
        setCategories(categories.stream().collect(Collectors.toMap(Category::getId, category -> category)));
    }

    /**
     * Instantiates a new Budget.
     */
    public Budget() {
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
        if (name == null || name.isEmpty() || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    /**
     * Gets categories.
     *
     * @return the categories
     */
    public Map<Long, Category> getCategories() {
        return categories;
    }

    /**
     * Gets all categories.
     *
     * @return the all categories
     */
    public Set<Category> getAllCategories() {
        return Set.copyOf(categories.values());
    }

    /**
     * Sets categories.
     *
     * @param categories the categories
     */
    public void setCategories(Map<Long, Category> categories) {
        this.categories = categories;
    }

    /**
     * Gets all intervals.
     *
     * @return the all intervals
     */
    public Set<BudgetInterval> getAllIntervals() {
        return Set.copyOf(intervals.values());
    }

    /**
     * Gets latest interval.
     *
     * @param generate the generate
     * @return the latest interval
     */
    public BudgetInterval getLatestInterval(boolean generate) {
        if (generate) generateIntervals();
        if (intervals == null || intervals.isEmpty()) return null;
        return intervals.get(intervals.keySet().stream().max(Date::compareTo).orElse(null));
    }

    /**
     * Add interval.
     *
     * @param interval the interval
     */
    public void addInterval(BudgetInterval interval) {
        this.intervals.put(interval.getStartDate(), interval);
    }

    private void generateIntervals() {
        Calendar calendar = Calendar.getInstance();
        BudgetInterval latestInterval = getLatestInterval(false);
        Date startDate = latestInterval == null ? budgetPeriod.getStartDate() : latestInterval.getStartDate();
        Date dateNow = calendar.getTime();
        double budgetedAmount = latestInterval == null ? getBudgetedAmount() : latestInterval.getBudgetedAmount();
        int amountOfPeriods = PeriodUnit.amountOfPeriods(startDate, dateNow, budgetPeriod.getPeriodLengthUnit()) / budgetPeriod.getPeriodLengthValue();
        int existingPeriods = intervals.keySet().stream().filter(date -> date.after(startDate)).toList().size();
        for (int i = 0; i <= amountOfPeriods - existingPeriods; i++) {
            if (amountOfPeriods == 0 && intervals.size() != 0) continue;
            calendar.setTime(startDate);
            calendar.add(budgetPeriod.getPeriodLengthUnit().getCalendarUnit(), i * budgetPeriod.getPeriodLengthValue());
            Date periodStartDate = calendar.getTime();
            calendar.add(budgetPeriod.getPeriodLengthUnit().getCalendarUnit(), budgetPeriod.getPeriodLengthValue());
            Date periodEndDate = calendar.getTime();
            if (periodStartDate.after(dateNow)) continue;
            BudgetInterval period = new BudgetInterval(this, periodStartDate, periodEndDate, budgetedAmount);
            addInterval(period);
        }
    }

    /**
     * Gets budget period.
     *
     * @return the budget period
     */
    public BudgetPeriod getBudgetPeriod() {
        return budgetPeriod;
    }

    /**
     * Sets budget period.
     *
     * @param budgetPeriod the budget period
     */
    public void setBudgetPeriod(BudgetPeriod budgetPeriod) {
        if (budgetPeriod == null) throw new IllegalArgumentException("Budget period cannot be null");
        this.budgetPeriod = budgetPeriod;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return budgetId;
    }

    /**
     * Gets budgeted amount.
     *
     * @return the budgeted amount
     */
    public double getBudgetedAmount() {
        BudgetInterval latestInterval = getLatestInterval(false);
        if (latestInterval != null) {
            double intervalAmount = latestInterval.getBudgetedAmount();
            if (intervalAmount != 0) {
                return intervalAmount;
            }
        }
        return initialBudgetedAmount;
    }

    /**
     * Sets budgeted amount.
     *
     * @param budgetedAmount the budgeted amount
     */
    public void setBudgetedAmount(double budgetedAmount) {
        if (budgetedAmount < 1) throw new IllegalArgumentException("Budgeted amount cannot be below 1");
        BudgetInterval latestPeriod = getLatestInterval(false);
        if (latestPeriod != null) {
            latestPeriod.setBudgetedAmount(budgetedAmount);
        }
        if (this.initialBudgetedAmount == 0) {
            this.initialBudgetedAmount = budgetedAmount;
        }
    }

}
