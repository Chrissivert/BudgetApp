package no.ntnu.idata1002.g08.dialog;

import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.AccountRegistry;
import no.ntnu.idata1002.g08.screens.ScreenController;
import no.ntnu.idata1002.g08.screens.Screens;
import java.util.List;

/**
 * This class is used for when a user signup
 * or login to check if there got an account.
 * If they don´t they get a popup dialog where
 * they can create on. Can also be used when
 * a user delete their last account, and we
 * need to create a new one.
 *
 * @version 27.04.2023
 * @author Daniel Neset
 */
public class AccountCheck {

    /**
     * Change screens if there are no accounts.
     */
    public void changeScene(){
        try{
            List<Account> accounts = AccountRegistry.getInstance().getAllAccounts();
            if(accounts.size() == 0){
                ScreenController.instance().changeScreen(Screens.ADDACCOUNT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
