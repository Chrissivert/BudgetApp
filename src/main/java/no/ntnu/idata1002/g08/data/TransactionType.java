package no.ntnu.idata1002.g08.data;

/**
 * The enum Transaction type.
 */
public enum TransactionType {
    /**
     * Expense transaction type.
     */
    EXPENSE("Expense"),
    /**
     * Income transaction type.
     */
    INCOME("Income");

    private final String name;

    TransactionType(String s) {
        name = s;
    }

    /**
     * Returns the name of the transaction type
     * @return the name of the transaction type
     */
    @Override
    public String toString() {
        return this.name;
    }
}
