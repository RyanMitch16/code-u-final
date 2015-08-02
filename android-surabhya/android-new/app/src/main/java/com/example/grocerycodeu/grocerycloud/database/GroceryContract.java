package com.example.grocerycodeu.grocerycloud.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class GroceryContract {

    //The content authority of the content provider
    public static final String CONTENT_AUTHORITY = "com.example.geocerycodeu.grocerycloud";

    //The base uri to access the content
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible content uri paths
    public static final String PATH_USER = "user";
    public static final String PATH_USER_LISTS = "user/lists";
    public static final String PATH_GROCERY = "grocery";
    public static final String PATH_ITEM = "item";
    public static final String PATH_REQUEST = "request";

    //Default constructor
    public GroceryContract() { }

    /**
     * Specifies the columns and entry database for the users
     */
    public static class UserEntry extends Entry {

        //Set the log tag
        public static String LOG_TAG = UserEntry.class.getSimpleName();

        //Build the uri for accessing the grocery list content
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        //Build the uri for accessing the grocery list content
        public static final Uri CONTENT_URI_LISTS =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).appendPath("lists").build();

        //The constants for if one or multiple entries are selected
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USER;

        //The table name and column values
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USER_KEY = "user_key";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_GROCERY_LIST_KEYS = "grocery_lists";

        //Set the column number for each property in the projection
        public static final int COL_USER_KEY;
        public static final int COL_USERNAME;
        public static final int COL_GROCERY_LIST_KEYS;

        //Create the entry database for the user
        public static EntryDatabase<UserEntry> entryDatabase = new EntryDatabase<UserEntry>(CONTENT_URI, UserEntry.class);
        static {
            COL_USER_KEY = entryDatabase.addProjection(COLUMN_USER_KEY);
            COL_USERNAME = entryDatabase.addProjection(COLUMN_USERNAME);
            COL_GROCERY_LIST_KEYS = entryDatabase.addProjection(COLUMN_GROCERY_LIST_KEYS);;
        }

        //The name of the array of grocery list ids the user has access to
        public static final String GROCERY_LISTS_ARRAY = "grocery_lists";

        //The user properties
        public String userKey;
        public String username;
        public JSONObject groceryLists;

        //Default constructor
        public UserEntry(){ }

        /**
         * Create a new user entry object to be manipulated
         * @param userKey       the key of the user
         * @param username      the username of the user
         * @param groceryLists  the grocery lists in the
         */
        public UserEntry(String userKey, String username, @Nullable String groceryLists){
            super();
            this.userKey = userKey;
            this.username = username;
            try {
                if (groceryLists == null) {
                    this.groceryLists = new JSONObject("{ " + GROCERY_LISTS_ARRAY + " : [] }");
                } else {
                    this.groceryLists = new JSONObject(groceryLists);
                }
            } catch (JSONException e){
                Log.e(LOG_TAG,e.toString());
            }
        }

        /**
         * Get the user entry database that
         * @return  tthe user entry database
         */
        public static EntryDatabase<UserEntry>  getDatabase(){
            return entryDatabase;
        }

        /**
         * Set the user properties from the given ursor
         * @param values    the cursor to take the values from
         */
        @Override
        public void setValues(Cursor values){
            userKey = values.getString(COL_USER_KEY);
            username = values.getString(COL_USERNAME);
            try{
                groceryLists = new JSONObject(values.getString(COL_GROCERY_LIST_KEYS));}
            catch (JSONException e){
                Log.e(LOG_TAG,e.toString());
            }
        }

        /**
         * Get the values of the user properties
         * @return  the content values object that holds the user properties
         */
        @Override
        public ContentValues getValues() {

            //Set the values to put into the table
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USER_KEY, userKey);
            userValues.put(COLUMN_USERNAME, username);
            userValues.put(COLUMN_GROCERY_LIST_KEYS, groceryLists.toString());
            return userValues;
        }
    }

    /**
     * Specifies the columns and entry database for the lists
     */
    public static class GroceryListEntry extends Entry {

        //Set the log tag
        public static String LOG_TAG = UserEntry.class.getSimpleName();

        //Build the uri for accessing the grocery list content
        public static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GROCERY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROCERY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GROCERY;

        //The table name
        public static final String TABLE_NAME = "grocery_lists";
        //The list key of the grocery list
        public static final String COLUMN_LIST_KEY = "list_key";
        //The json in string form of the users that have access to the grocery list
        public static final String COLUMN_USER_KEYS = "user_key";
        //The name of the grocery list
        public static final String COLUMN_LIST_NAME = "list_name";
        //The current version of the list
        public static final String COLUMN_LIST_VERSION = "list_version";
        //Whether the list has been updated since last viewed
        public static final String COLUMN_LIST_UPDATED = "list_updated";

        //Set the column number for each property in the projection
        public static final int COL_LIST_KEY;
        public static final int COL_LIST_NAME;
        public static final int COL_USER_KEYS;
        public static final int COL_LIST_VERSION;
        public static final int COL_LIST_UPDATED;

        //Create the entry database for the user
        public static EntryDatabase<GroceryListEntry> entryDatabase = new EntryDatabase<GroceryListEntry>(CONTENT_URI, GroceryListEntry.class);
        static {
            COL_LIST_KEY = entryDatabase.addProjection(COLUMN_LIST_KEY);
            COL_LIST_NAME = entryDatabase.addProjection(COLUMN_LIST_KEY);
            COL_USER_KEYS = entryDatabase.addProjection(COLUMN_USER_KEYS);
            COL_LIST_VERSION = entryDatabase.addProjection(COLUMN_LIST_VERSION);
            COL_LIST_UPDATED = entryDatabase.addProjection(COLUMN_LIST_UPDATED);
        }

        public static final String USER_LISTS_ARRAY = "grocery_lists";

        public static final int UPDATED_FALSE = 0;
        public static final int UPDATED_TRUE = 1;
        public static final int UPDATED_NEW = 2;

        public String listKey;
        public String listName;
        public JSONObject userIds;
        public long version;
        public int updated;

        public GroceryListEntry(){ }

        /**
         * Create a new grocery list entry object to be manipulated
         * @param listKey   the app engine generated list key
         * @param listName  the name of the list
         * @param userIds  the json of users that have access to this list
         * @param version   the version of the list
         * @param updated   the update state of the list
         */
        public GroceryListEntry(String listKey, String listName, @Nullable String userIds, long version, int updated){
            super();
            this.listKey = listKey;
            this.listName = listName;
            try {
                if (userIds == null) {
                    this.userIds = new JSONObject("{ " + USER_LISTS_ARRAY + " : [] }");
                } else {
                    this.userIds = new JSONObject(userIds);
                }
            } catch (JSONException e){
                Log.e(LOG_TAG,e.toString());
            }
            this.version = version;
            this.updated = updated;
        }

        /**
         * Get the list entry database that
         * @return  the list entry database
         */
        public static EntryDatabase<GroceryListEntry>  getDatabase(){
            return entryDatabase;
        }

        /**
         * Set the user properties from the given ursor
         * @param values    the cursor to take the values from
         */
        @Override
        public void setValues(Cursor values){
            listKey = values.getString(COL_LIST_KEY);
            listName = values.getString(COL_LIST_NAME);
            try {
                this.userIds = new JSONObject(values.getString(COL_USER_KEYS));
            } catch (JSONException e){
                Log.e(LOG_TAG,e.toString());
            }
            version = values.getLong(COL_LIST_VERSION);
            updated = values.getInt(COL_LIST_UPDATED);
        }

        /**
         * Get the values of the user properties
         * @return  the content values object that holds the user properties
         */
        @Override
        public ContentValues getValues() {

            //Set the values to put into the table
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_LIST_KEY, listKey);
            userValues.put(COLUMN_LIST_NAME, listName);
            userValues.put(COLUMN_USER_KEYS, userIds.toString());
            userValues.put(COLUMN_LIST_VERSION, version);
            userValues.put(COLUMN_LIST_UPDATED, updated);
            return userValues;
        }

    }

    /**
     * 
     */
    public static class ItemEntry implements BaseColumns {

        //Build the uri for accessing the grocery list item content
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        //The table name
        public static final String TABLE_NAME = "grocery_list_items";
        //The key of the list the item belongs to
        public static final String COLUMN_LIST_KEY = "list_key";
        //The id of the item in the list
        public static final String COLUMN_ITEM_ID = "item_id";
        //The name of the item
        public static final String COLUMN_ITEM_NAME = "item_name";
        //The number of items needed
        public static final String COLUMN_ITEM_QUANTITY = "item_quantity";
        //Whether or not this item has been deleted or not
        public static final String COLUMN_ITEM_TRASHED = "item_trashed";
    }

    /**
     *
     */
    public static class HttpRequestEntry implements BaseColumns {

        //Build the uri for accessing the grocery list item content
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REQUEST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REQUEST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REQUEST;

        //The table name
        public static final String TABLE_NAME = "http_requests";
        //The key of the list the item belongs to
        public static final String COLUMN_URL = "url";
    }
}