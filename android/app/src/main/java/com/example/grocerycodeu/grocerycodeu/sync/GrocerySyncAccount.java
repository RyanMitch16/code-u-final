package com.example.grocerycodeu.grocerycodeu.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;

import com.example.grocerycodeu.grocerycodeu.R;

public class GrocerySyncAccount {

    public final String LOG_TAG = GrocerySyncAccount.class.getSimpleName();

    /**
     * Gets the application account or creates the account if it does not exist.
     * @param context The context used to access the account service
     * @return the account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            //Set android:syncable="true" in your <provider> element in the manifest later
            ContentResolver.setIsSyncable(newAccount,
                    context.getString(R.string.content_authority), 1);

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Finally, let's do a sync to get things started
         */
        //syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}