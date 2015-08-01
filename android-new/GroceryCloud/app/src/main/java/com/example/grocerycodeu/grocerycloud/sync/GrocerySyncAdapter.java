package com.example.grocerycodeu.grocerycloud.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.grocerycodeu.grocerycloud.R;
import com.example.grocerycodeu.grocerycloud.database.EntryDatabase;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract;
import com.example.grocerycodeu.grocerycloud.sync.request.HttpRequest;
import com.example.grocerycodeu.grocerycloud.ui.GroceryListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GrocerySyncAdapter extends AbstractThreadedSyncAdapter {

    //Get the log tag of the class
    public static final String LOG_TAG = GrocerySyncAdapter.class.getSimpleName();

    public static final String BASE_URL = "http://code-u-final.appspot.com/";

    public static final String TAG_OPCODE = "op";
    public static final int OPCODE_USER_GET_LISTS = 0;

    public static final String TAG_USER_KEY = "USER_KEY";

    ContentResolver mContentResolver;

    //Default constructor
    public GrocerySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {
            switch (extras.getInt(TAG_OPCODE)) {
                case OPCODE_USER_GET_LISTS:
                    userGetGroceryLists(extras);
                    break;
            }
        }
        catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }

        Intent intent1 = new Intent();
        intent1.setAction("com.example.grocerycodeu.grocerycloud.synced");
        getContext().sendBroadcast(intent1);

    }

    /**
     * Retrieves the json of lists the user has access to
     * @param extras
     * @return
     * @throws IOException
     */
    public void userGetGroceryLists(Bundle extras) throws IOException, JSONException{

        //Get the user account key
        Account account = GrocerySyncAccount.getSyncAccount(getContext());
        String userKey = GrocerySyncAccount.getUserKey(getContext(), account);

        //Get the user from the database
        EntryDatabase<GroceryContract.UserEntry> userDatabase = GroceryContract.UserEntry.getDatabase();
        EntryDatabase<GroceryContract.GroceryListEntry> groceryDatabase = GroceryContract.GroceryListEntry.getDatabase();
        GroceryContract.UserEntry[] userEntry = userDatabase.query(
                getContext(), GroceryContract.UserEntry.COLUMN_USER_KEY +" = ?", new String[]{userKey},null);

        if (userEntry.length == 1){

            //Get the lists the user has access to
            JSONArray localGroceryListsArray = userEntry[0].groceryLists.getJSONArray("grocery_lists");
            JSONObject versions = new JSONObject();

            for (int i=0;i<localGroceryListsArray.length();i++){

                //Build the json of list keys paired with their version numbers
                String listKey = localGroceryListsArray.getString(i);
                GroceryContract.GroceryListEntry[] lists = groceryDatabase.query(getContext(),
                        GroceryContract.UserEntry.COLUMN_USER_KEY + " = ?", new String[]{userKey}, null);

                if (lists.length == 1){
                    Log.d(LOG_TAG, lists[0].listName);
                }
            }

            Log.d(LOG_TAG, versions.toString());

            Uri url = Uri.parse(BASE_URL).buildUpon()
                    .appendPath("user")
                    .appendPath("get")
                    .appendPath("lists")
                    .appendQueryParameter("user_key", extras.getString(TAG_USER_KEY))
                    .appendQueryParameter("versions", versions.toString())
                    .build();

            Log.d(LOG_TAG, url.toString());

            //Retrieve the user key
            HttpURLConnection connection = HttpRequest.get(url);

            JSONObject groceryListsResponse = new JSONObject(HttpRequest.getContentString(connection));
            JSONArray groceryLists = groceryListsResponse.getJSONArray(GroceryContract.UserEntry.GROCERY_LISTS_ARRAY);

            for (int i=0;i<groceryLists.length();i++) {

                JSONObject list = groceryLists.getJSONObject(i);

                GroceryContract.GroceryListEntry groceryList = new GroceryContract.GroceryListEntry(
                        list.getString("list_key"),
                        list.getString("list_name"),
                        null,
                        0, GroceryContract.GroceryListEntry.UPDATED_NEW);

                //Check if the list exists in the database
                if (!versions.has(list.getString("list_key"))) {
                    groceryDatabase.put(getContext(), groceryList);
                }

                localGroceryListsArray.put(list.getString("list_key"));
                Log.d(LOG_TAG, list.getString("list_key") + "," + list.getString("list_name") + "," + list.getString("list_version"));
            }
        }

    }

    public void userGetGroceryItems(Bundle extras) throws IOException{

    }

    public static void syncImmediatelyUserGetLists(Context context, Account account){
        //Create the expedited bundle
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        //Add the query parameters
        bundle.putInt(TAG_OPCODE, OPCODE_USER_GET_LISTS);
        bundle.putString(TAG_USER_KEY, GrocerySyncAccount.getUserKey(context, account));

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);
    }


}
