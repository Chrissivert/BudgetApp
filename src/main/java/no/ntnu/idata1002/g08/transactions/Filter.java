package no.ntnu.idata1002.g08.transactions;

import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import java.util.List;
import java.util.Objects;

/**
 * Represents a filter for transactions.
 *
 * @author Brage Solem
 * @version 27.04.2023
 */
public class Filter {

    /**
     * Instantiates a new Filter.
     */
    public Filter() { /* Does not hold any fields */ }

    /**
     * Filter by type list.
     *
     * @param inputList the input list
     * @param type      the type
     * @return the list
     */
    public List<Transaction> filterByType(List<Transaction> inputList, TransactionType type) {
        if ((type == TransactionType.INCOME) || (type == TransactionType.EXPENSE)) {
            return inputList.stream().filter(transaction -> transaction.getType() == type).toList();
        } else {
            return inputList;
        }
    }

    /**
     * Filter by amount list.
     *
     * @param inputList the input list
     * @param cost      the cost
     * @param formTo    the form to
     * @return the list
     */
    public List<Transaction> filterByAmount(List<Transaction> inputList, double cost, String formTo) {
        if (formTo.equals("Filter from amount")) {
            return inputList.stream().filter(transaction -> transaction.getAmount() >= cost).toList();
        } else if (formTo.equals("Filter to amount")) {
            return inputList.stream().filter(transaction -> transaction.getAmount() <= cost).toList();
        } else {
            return inputList;
        }
    }

    /**
     * Filter by formatted date list.
     *
     * @param inputList     the input list
     * @param formattedDate the formatted date
     * @return the list
     */
    public List<Transaction> filterByFormattedDate(List<Transaction> inputList, String formattedDate) {
        if ((formattedDate == null) || (formattedDate.isEmpty())) {
            return inputList;
        } else {
            return inputList.stream().filter(transaction -> transaction.getFormattedDate().equals(formattedDate)).toList();
        }
    }

    /**
     * Filter by account list.
     *
     * @param inputList     the input list
     * @param accountNumber the account number
     * @return the list
     */
    public List<Transaction> filterByAccount(List<Transaction> inputList, String accountNumber) {
        if ((Objects.equals(accountNumber, "All accounts")) || (accountNumber == null)) {
            return inputList;
        } else {
            return inputList.stream().filter(transaction -> transaction.getAccount().getName().equals(accountNumber)).toList();
        }
    }

    /**
     * Filter by category list.
     *
     * @param transactionList  the transaction list
     * @param selectedCategory the selected category
     * @return the list
     */
    public List<Transaction> filterByCategory(List<Transaction> transactionList, String selectedCategory) {
        if (Objects.equals(selectedCategory, "All categories") || selectedCategory == null) {
            return transactionList;
        } else if (Objects.equals(selectedCategory, "No category")) {
            return transactionList.stream().filter(transaction -> transaction.getCategory() == null).toList();
        } else {
            return transactionList.stream().filter(transaction -> Objects.nonNull(transaction.getCategory())).filter(transaction -> transaction.getCategory().getName().equals(selectedCategory)).toList();
        }
    }
}

