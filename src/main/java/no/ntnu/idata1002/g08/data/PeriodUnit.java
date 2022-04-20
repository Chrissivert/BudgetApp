package no.ntnu.idata1002.g08.data;

import java.util.Calendar;
import java.util.Date;

/**
 * Represents a period of time to be used in the budget.
 */
public enum PeriodUnit {
    /**
     * Day period unit.
     */
    DAY("Day", "Days", 1, Calendar.DAY_OF_YEAR),
    /**
     * Week period unit.
     */
    WEEK("Week", "Weeks", 7, Calendar.WEEK_OF_YEAR),
    /**
     * Month period unit.
     */
    MONTH("Month", "Months", 30, Calendar.MONTH),
    /**
     * Year period unit.
     */
    YEAR("Year", "Years", 365, Calendar.YEAR);

    private final String singular;
    private final String plural;
    private final int days;
    private final int calendarUnit;

    PeriodUnit(String singular, String plural, int days, int calendarUnit) {
        this.singular = singular;
        this.plural = plural;
        this.days = days;
        this.calendarUnit = calendarUnit;
    }


    /**
     * Amount of periods int.
     *
     * @param start the start
     * @param end   the end
     * @param unit  the unit
     * @return the int
     */
    public static int amountOfPeriods(Date start, Date end, PeriodUnit unit) {
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        return switch (unit) {
            case DAY -> calEnd.get(Calendar.DAY_OF_YEAR) - calStart.get(Calendar.DAY_OF_YEAR);
            case WEEK -> calEnd.get(Calendar.WEEK_OF_YEAR) - calStart.get(Calendar.WEEK_OF_YEAR);
            case MONTH -> calEnd.get(Calendar.MONTH) - calStart.get(Calendar.MONTH);
            case YEAR -> calEnd.get(Calendar.YEAR) - calStart.get(Calendar.YEAR);
        };
    }

    /**
     * Gets calendar unit.
     *
     * @return the calendar unit
     */
    public int getCalendarUnit() {
        return calendarUnit;
    }

    @Override
    public String toString() {
        return plural;
    }
}
