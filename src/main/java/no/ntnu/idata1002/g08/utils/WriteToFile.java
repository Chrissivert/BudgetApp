package no.ntnu.idata1002.g08.utils;

import no.ntnu.idata1002.g08.GlobalData;
import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.dao.BudgetPeriodDAO;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.dao.UserDAO;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.data.PeriodUnit;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import no.ntnu.idata1002.g08.data.User;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * This class is used to write all the data from the database to a
 * csv file. This is to comply with the GDPR data protection.
 *
 * @author Daniel Neset
 * @version 25.04.2023
 */
public class WriteToFile {
    String path;
    TransactionDAO transactionDAO = new TransactionDAO();
    AccountDAO accountDAO = new AccountDAO();

    /**
     * Constructor that sets the path of where the csv file should be saved.
     *
     * @param path The path that the file should be saved.
     */
    public WriteToFile(String path){
        this.path = path;
    }

    /**
     * Creates a csv file with all the transactions data.
     */
    public void writeToCsvFile(){
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        try {
            FileWriter writer = new FileWriter(path);
            writer.append("Date, Amount, Type, Category, Description\n");
            for (Transaction transaction : transactions){
                Date date = transaction.getDate();
                double amount = transaction.getAmount();
                TransactionType transactionType = transaction.getType();
                String category = null;
                if(transaction.getCategory() != null){
                    category = transaction.getCategory().getName();
                }
                String description = transaction.getDescription();
                writer.append(date + ","
                        + amount + ","
                        + transactionType.toString() + ","
                        + category + ","
                        + description + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a csv file with ALl the database data.
     */
    public void writeAllUserData(){
        //Accounts contains a MAP of all AccountBalance
        List<Account> accounts = new AccountDAO().getAllAccounts();
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        List<AccountBalance> accountBalances = accountDAO.getAllAccountBalances();
        List<Category> categorys = CategoryRegistry.getInstance().getAllCategories();
        UserDAO userDAO = new UserDAO();

        try {
            FileWriter writer = new FileWriter(path);

            //User
            writer.append("User\n");
            writer.append("UserId, BankId, ColorBlindMode, FullName, UserName\n");
            User user = GlobalData.getInstance().getUser();
            Long userId = user.getId();
            String bankId = user.getBankId();
            String colorBlindMode = user.getColorBlindSetting().getTitle();
            String fullName = user.getFullName();
            String userName = user.getUsername();
            writer.append(userId+","+bankId+","+colorBlindMode+","+fullName+","+userName+"\n");

            //All accounts
            writer.append("Account\n");
            writer.append("AccountId, UserId, AccountNumber, AccountName\n");
            for (Account account : accounts) {
                String accountId = account.getId().toString();
                Long accountUserId = account.getId();
                String accountNumber = account.getAccountNumber();
                String accountName = account.getName();
                writer.append(accountId+","+accountUserId+","+accountNumber+","+accountName+"\n");
            }

            //All AccountBalance
            writer.append("AccountBalance\n");
            writer.append("AccountBalanceId, Balance, Date, AccountId, ACCOUNTBALANCES_KEY\n");
            for (AccountBalance accountBalance : accountBalances) {
                Long accountBalanceId = accountBalance.getId();
                double accountBalanceAmount = accountBalance.getBalance();
                Date accountBalanceDate = accountBalance.getDate();
                Long accountBalanceAccountId = accountBalance.getAccount().getId();
                writer.append(accountBalanceId+","+accountBalanceAmount+","+accountBalanceDate+","+accountBalanceAccountId+"\n");
            }

            //ALL BudgetPeriode
            BudgetPeriodDAO budgetPeriodDAO = new BudgetPeriodDAO();
            List<BudgetPeriod> budgetPeriods = budgetPeriodDAO.getAllBudgetPeriods();
            writer.append("BudgetPeriod\n");
            writer.append("BudgetPeriodId, Name, PeriodLengthUnit, PeriodLengthValue, StartDate, UserId\n");
            for (BudgetPeriod budgetPeriod : budgetPeriods){
                Long budgetId = budgetPeriod.getId();
                String budgetName = budgetPeriod.getName();
                PeriodUnit periodeLengthUnit = budgetPeriod.getPeriodLengthUnit();
                int periodeLengthValue = budgetPeriod.getPeriodLengthValue();
                Date budgetDate = budgetPeriod.getStartDate();
                Long budgetUserId = budgetPeriod.getUser().getId();
                writer.append(budgetId+","+budgetName+","+periodeLengthUnit.getCalendarUnit()+","+periodeLengthValue+","+budgetDate+","+budgetUserId);
            }

            //ALL CATEGORY
            writer.append("Category \n");
            writer.append("CategoryId, Name, Type, UserId \n");
            for (Category category: categorys) {
                Long categoryId = category.getId();
                String name = category.getName();
                TransactionType transactionType = category.getType();
                User userIdCategory = category.getUser();
                writer.append(categoryId+","+name+","+transactionType.toString()+","+userIdCategory.getId());
            }

            //All Transactions
            writer.append("Transactions \n");
            writer.append("TransactionId, Amount, BankTransactionId, Currency, Date, Deleted, Description, Expense, AccountId, CategoryId \n");
            for (Transaction transaction : transactions){
                Long transactionId = transaction.getId();
                double amount = transaction.getAmount();
                String bankTransactionId = transaction.getBankTransactionId();
                String currency = transaction.getCurrency();
                Date date = transaction.getDate();
                String description = transaction.getDescription();
                String expense = transaction.getType().toString();
                Long accountId = transaction.getAccount().getId();
                Category category = transaction.getCategory();
                Long categoryId = null;
                if(category != null){
                    categoryId = category.getId();
                }
                writer.append(transactionId+","+amount+","+bankTransactionId+","+currency+","+date+","+description+","+expense+","+accountId+","+categoryId+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
