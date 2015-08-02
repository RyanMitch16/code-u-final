package com.example.grocerycodeu.grocerycloud.database;

import android.accounts.Account;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.grocerycodeu.grocerycloud.database.GroceryContract.UserEntry;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract.GroceryListEntry;
import com.example.grocerycodeu.grocerycloud.sync.GrocerySyncAccount;

/**
 * Just a dummy provider for now so we can use the sync adapter
 */
public class GroceryProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GroceryDatabaseHelper mOpenHelper;

    static final int USER = 100;
    static final int USER_GROCERY_LISTS = 101;
    static final int GROCERY_LIST = 200;

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = GroceryContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, GroceryContract.PATH_USER, USER);
        matcher.addURI(authority, GroceryContract.PATH_USER_LISTS, USER_GROCERY_LISTS);
        matcher.addURI(authority, GroceryContract.PATH_GROCERY, GROCERY_LIST);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new GroceryDatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case USER:
                return UserEntry.CONTENT_ITEM_TYPE;
            case USER_GROCERY_LISTS:
                return GroceryListEntry.CONTENT_TYPE;
            case GROCERY_LIST:
                return GroceryListEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor = null;

        //Match the uri to a scheme
        switch (sUriMatcher.match(uri)) {

            //Query the user entry values
            case USER:
                retCursor = db.query(UserEntry.TABLE_NAME,
                        projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case USER_GROCERY_LISTS:

                try {
                    //Get the user from the database
                    EntryDatabase<UserEntry> entryDatabase = UserEntry.getDatabase();
                    UserEntry[] userList = entryDatabase.query(getContext(),selection,selectionArgs,sortOrder);

                    Log.e("M",userList.length+"");
                    if (userList.length == 1) {

                        //Get the list of grocery lists available to the user
                        JSONObject groceryListJSON = userList[0].groceryLists;
                        JSONArray groceryLists = groceryListJSON.getJSONArray(
                                GroceryContract.UserEntry.GROCERY_LISTS_ARRAY);
                        Log.e("M", userList[0].groceryLists.toString());

                        //Set the selection arguments to selection all available lists
                        selection = GroceryContract.GroceryListEntry._ID + " = ? ";
                        selectionArgs = new String[Math.max(groceryLists.length(), 1)];
                        selectionArgs[0] = "-1";
                        for (int i = 0; i < groceryLists.length(); i++) {
                            if (i != 0){
                                selection += "OR " + GroceryContract.GroceryListEntry._ID + " = ? ";}
                            selectionArgs[i] = groceryLists.getString(i);
                        }

                        //Return the query
                        retCursor = db.query(GroceryContract.GroceryListEntry.TABLE_NAME,
                                projection, selection, selectionArgs, null, null, sortOrder);
                    }
                } catch (JSONException e){
                    Log.e("M",e.toString());
                }
                break;

            //Query the grocery list values
            case GROCERY_LIST:
                retCursor = db.query(GroceryContract.GroceryListEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            //Throw exception for unsupported uris
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (retCursor != null){
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);}
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        //Match the uri to a scheme
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //Insert the user entry values
            case USER: {
                long id = db.insert(GroceryContract.UserEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(),id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            //Insert the grocery list values
            case GROCERY_LIST:
                long id = 0;
                try{
                    id = db.insert(GroceryContract.GroceryListEntry.TABLE_NAME, null, values);

                    //Get the user account key
                    Account account = GrocerySyncAccount.getSyncAccount(getContext());
                    String userKey = GrocerySyncAccount.getUserKey(getContext(), account);

                    //Get the user from the database
                    EntryDatabase<UserEntry> userDatabase = UserEntry.getDatabase();
                    UserEntry[] userEntry = userDatabase.query(
                            getContext(),UserEntry.COLUMN_USER_KEY +" = ?", new String[]{userKey},null);

                    //Update the lists the user has access to
                    JSONArray newGroceryLists =
                            userEntry[0].groceryLists.getJSONArray(UserEntry.GROCERY_LISTS_ARRAY);
                    newGroceryLists.put(""+id);
                    userEntry[0].groceryLists.put(UserEntry.GROCERY_LISTS_ARRAY, newGroceryLists);

                    //Put the user back in the database
                    userDatabase.put(getContext(), userEntry[0]);
                } catch (JSONException e){
                    Log.e("XX",e.toString());
                }

                if (id > 0)
                    returnUri = ContentUris.appendId(uri.buildUpon(),id).build();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            //Throw exception for unsupported uris
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Log.d("Not","igy");
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Get the database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        //Match the uri to a scheme
        final int match = sUriMatcher.match(uri);
        switch (match) {

            //Update the user entry values
            case USER:
                long id = db.update(GroceryContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
                if (id <= 0){
                    throw new android.database.SQLException("Failed to insert row into " + uri);}
                break;

            //Throw exception for unsupported uris
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return 0;
    }
}