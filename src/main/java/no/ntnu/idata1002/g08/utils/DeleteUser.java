package no.ntnu.idata1002.g08.utils;

import no.ntnu.idata1002.g08.dao.AccountDAO;
import no.ntnu.idata1002.g08.dao.BudgetPeriodDAO;
import no.ntnu.idata1002.g08.dao.TransactionDAO;
import no.ntnu.idata1002.g08.dao.UserDAO;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.data.BudgetPeriod;
import no.ntnu.idata1002.g08.data.Category;
import no.ntnu.idata1002.g08.data.CategoryRegistry;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;

import java.util.List;

/**
 * This class is used to delete all the userInformation of the user
 * that is logged in. This is to comply with the GDPR data protection.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class DeleteUser {

    /**
     * This method delete the user that is logged in.
     */
    public DeleteUser(){
        TransactionDAO transactionDAO = new TransactionDAO();
        BudgetPeriodDAO budgetPeriodDao = new BudgetPeriodDAO();
        List<BudgetPeriod> bugetPeriodes = budgetPeriodDao.getAllBudgetPeriods();

        for (BudgetPeriod budgetPeriod : bugetPeriodes){
            budgetPeriodDao.removeBudgetPeriod(budgetPeriod);
            //budgetDAO.deleteBudgetsByBudgetPeriodId(budgetPeriod.getId());
        }
        transactionDAO.removeTransactions(transactionDAO.getAllTransactions());

        List<Category> categorys = CategoryRegistry.getInstance().getAllCategories();
        for (Category category : categorys){
            CategoryRegistry.getInstance().removeCategory(category);
        }

        AccountDAO accountDAO = new AccountDAO();
        accountDAO.deleteAllAccountBalances();
        List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();

        for (Account account : accounts){
            AccountRegistry.getInstance().removeAccount(account);
        }

        UserDAO userDao = new UserDAO();
        userDao.deleteUser();
        ScreenController.instance().changeScreen(Screens.LOGIN);
    }
}
