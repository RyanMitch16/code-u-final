package com.example.grocerycodeu.grocerycloud.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import com.example.grocerycodeu.grocerycloud.R;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract;

public class GrocerySyncAccount {

    //The log tag of the class
    public static final String LOG_TAG = GrocerySyncAccount.class.getSimpleName();

    //The tag used to save the user key data to
    private static final String TAG_USER_KEY = "TAG_USER_KEY";

    /**
     * Gets the application account and creates it if it does not exist
     * @param context   the context used to access the account service
     * @return the account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount) ){

            //Create a new account
            final Bundle data = new Bundle();
            data.putString(TAG_USER_KEY, "");
            if (!accountManager.addAccountExplicitly(newAccount, "", data)) {
                return null;
            }
        }
        //Return the sync account
        return newAccount;
    }

    /**
     * Get the user key saved in the application account
     * @param context   the context used to access the account service
     * @param account   the application account
     * @return          the user key
     */
    public static String getUserKey(Context context, Account account){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        return  accountManager.getUserData(account, TAG_USER_KEY);
    }

    /**
     * Set the user key in the application account
     * @param context   the context used to access the account service
     * @param account   the application account
     * @param userKey   the user key
     */
    public static void setUserKey(Context context, Account account, String userKey) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        accountManager.setUserData(account, TAG_USER_KEY, userKey);
    }


}