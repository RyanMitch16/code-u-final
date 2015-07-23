package com.example.grocerycodeu.grocerycodeu.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.grocerycodeu.grocerycodeu.R;

public class UserSyncAdapterTest extends AndroidTestCase {

    public void testSyn() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(UserSyncAdapter.TAG_OPCODE, UserSyncAdapter.OPCODE_USER_CREATE);
        bundle.putString(UserSyncAdapter.TAG_EMAIL, "ryanmitch200@gmail.com");


        Account account = UserSyncAccount.getSyncAccount(getContext());

        ContentResolver.setIsSyncable(account,
                getContext().getString(R.string.content_authority), 1);

        if (ContentResolver.isSyncPending(account, getContext().getString(R.string.content_authority)) ||
                ContentResolver.isSyncActive(account, getContext().getString(R.string.content_authority))) {
            Log.i("TestXXX", "SyncPending, canceling");
            ContentResolver.cancelSync(account, getContext().getString(R.string.content_authority));
        }
        ContentResolver.requestSync(account,
                getContext().getString(R.string.content_authority), bundle);

        assertTrue(1==1);
    }


}
