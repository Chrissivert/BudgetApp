package no.ntnu.idata1002.g08.bank;

import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountBalance;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.Transaction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class is used to synchronise between the Api and the
 * Database. It gets all the bankId transactions and their latest date
 * from the Database. Then it gets all the new transactions from the APi.
 * It sorts the API transaction with the date from the Database transactions.
 * So from that date and forwards. Then it quickly checks if there are any
 * duplicate and then send the uniq new API transactions to the Database.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class TransactionSyncronice
{
    List<Account> accounts;
    TransactionDAO transactionDAO = new TransactionDAO();
    ApiRequest apiRequest = new ApiRequest();
    AccountDAO accountDAO = new AccountDAO();

    /**
     * This class synchronise all the Data from the API transactions to the Database.
     * Also updates the Accounts Balances.
     */
    public TransactionSyncronice()  {
        try{
        accounts = accountDAO.getAllAccounts();
        List<Account> filteredAccounts = new ArrayList<>();
        for (Account account : accounts){
            if(account.getAccountNumber() != null){
                filteredAccounts.add(account);
            }
        }
        for (Account account: filteredAccounts) {
            List<Transaction> transactionsApi = getAllDataFromAPI(account);
            List<Transaction> transactionsDao = getAllFromDatabase();
            Date earliestDate = null;
            for (Transaction obj : transactionsApi) {
                if (earliestDate == null || obj.getDate().before(earliestDate)) {
                    earliestDate = obj.getDate();
                }
            }

            // Get the latest date from list2
            Date latestDate = null;
            String bankTransactionId = null;
            for (Transaction obj : transactionsDao) {
                if (latestDate == null || obj.getDate().after(latestDate)) {
                    latestDate = obj.getDate();
                    bankTransactionId = obj.getBankTransactionId();
                }
            }

            //All the transactions from the database with the latest Date
            List<Transaction> transactionsDaoWithLateDate = new ArrayList<>();
            for (Transaction transaction : transactionsDao){
                if(latestDate.equals(transaction.getDate())){
                    transactionsDaoWithLateDate.add(transaction);
                }
            }

            //Get all the objects from the API that contains the same date and after.
            List<Transaction> transactionsApiWithTheSameDateAsDao = new ArrayList<>();
            for (Transaction transaction : transactionsApi){
                if(transaction.getDate().equals(latestDate) || transaction.getDate().after(latestDate)){
                    transactionsApiWithTheSameDateAsDao.add(transaction);
                }
            }

            Set<String> transactionIds = new HashSet<String>();
            for (Transaction obj : transactionsDaoWithLateDate) {
                transactionIds.add(obj.getBankTransactionId());
            }

            // Remove objects from list2 that have the same transaction ID as in the set
            Iterator<Transaction> iter = transactionsApiWithTheSameDateAsDao.iterator();
            while (iter.hasNext()) {
                Transaction obj = iter.next();
                if (transactionIds.contains(obj.getBankTransactionId())) {
                    iter.remove();
                }
            }

            if(transactionsApiWithTheSameDateAsDao.size() >= 1){
                transactionDAO.addOrUpdateTransactions(transactionsApiWithTheSameDateAsDao);
            }

            AccountBalance accountBalance = new AccountBalance(Calendar.getInstance().getTime(), apiRequest.getBalanceAsDouble(account));
            account.addAccountBalance(accountBalance);
            AccountRegistry.getInstance().addOrUpdateAccount(account);

        }
        }catch (SQLException sqlException){
            sqlException.printStackTrace();
        }
    }

    /**
     * Get all data from the API that contains the selected Account.
     *
     * @param account the selected account
     * @return return all API transactions from that account
     */
    private List<Transaction> getAllDataFromAPI(Account account){
        return apiRequest.getTransactionsAsObject(account);
    }

    /**
     * This class is used to get all Transaction data from the Database.
     *
     * @return Return all transactions from the Database.
     * @throws SQLException Throws SQLException if it cannot connect to the database.
     */
    private List<Transaction> getAllFromDatabase() throws SQLException {
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        List<Transaction> filterTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if(transaction.getBankTransactionId() != null){
                filterTransactions.add(transaction);
            }
        }
        return filterTransactions;
    }
}
