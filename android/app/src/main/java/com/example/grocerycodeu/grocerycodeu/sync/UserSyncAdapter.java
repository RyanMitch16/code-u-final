package com.example.grocerycodeu.grocerycodeu.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UserSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String BASE_URL = "http://code-u-final.appspot.com/";

    public static final String TAG_OPCODE = "op";

    public static final int OPCODE_USER_CREATE = 0;

    public static final String TAG_EMAIL = "EMAIL";

    public UserSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Log.d("TESTXXX","Perform");
        try {
            switch (extras.getInt(TAG_OPCODE)) {
                case OPCODE_USER_CREATE:
                    Log.d("TESTXXX","Creating");
                    Log.d("TESTXXX", createUser(extras));
                    break;
            }
        }
        catch (Exception e){

        }

    }

    public String createUser(Bundle extras) throws IOException{

        Uri url = Uri.parse(BASE_URL).buildUpon()
                .appendPath("user")
                .appendPath("create")
                .appendQueryParameter("email", extras.getString(TAG_EMAIL))
                .build();

        Log.d("TestXXX",url.toString());

        //Retrieve the user key
        HttpURLConnection connection = HttpRequest.get(url);
        return HttpRequest.getContentString(connection);
    }



}
