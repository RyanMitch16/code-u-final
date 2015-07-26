package com.example.grocerycodeu.grocerycodeu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by saryal on 7/19/15.
 */
public class GroceryDBHelper extends SQLiteOpenHelper{


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 7;
    static final String DATABASE_NAME = "groceryapp.db";


    /**
     * Constructor for GroceryDBHelper
     * @param context of the activity
     */
    public GroceryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    /**
     * SQL statement to create a table to store data reterived from the cloud
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_GROCERY_LIST_TABLE = "CREATE TABLE " + GroceryDBContract.GroceryList.TABLE_NAME + " (" +
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY + " TEXT PRIMARY KEY  " +
                " )";

        final String SQL_CREATE_ITEM_LIST_TABLE = "CREATE TABLE " + GroceryDBContract.ItemList.TABLE_NAME + " (" +
                GroceryDBContract.ItemList.COLUMN_NAME_ID + " TEXT PRIMARY KEY , " +
                GroceryDBContract.ItemList.COLUMN_NAME_ENTRY_KEY + " TEXT NOT NULL, " +
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_NAME + " TEXT NOT NULL, " +
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_COST + " REAL NULL, " +
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_QUANTITY + " INTEGER NOT NULL " +
                " )";

        sqLiteDatabase.execSQL(SQL_CREATE_GROCERY_LIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_LIST_TABLE);
    }

    /**
     * Constructor for GroceryDBHelper
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroceryDBContract.GroceryList.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GroceryDBContract.ItemList.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

     /**
      * * add a new grocery list item to the database
      * @param  title title of the list
      * @param  key unique key for the list
      * @return unique long ID
      */
    public long addDataFromCloud(String title, String key){

        long newRowId;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_TITLE, title);
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY, key);
        newRowId = database.insert(
                GroceryDBContract.GroceryList.TABLE_NAME,
                null,
                values);
        database.close();
        return newRowId;
    }

    /**
     * * add a new grocery list item to the database
     * @param  id title of the item
     * @param  key unique key for the list;used as foregin key
     * @param  cost cost fo the item
     * @param  quantity total amount of item ordered
     * @return unique long ID
     */
    public long addDataFromCloud(String id, String key,String name,double cost, int quantity){

        long newRowId;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GroceryDBContract.ItemList.COLUMN_NAME_ID, id);
        values.put(GroceryDBContract.ItemList.COLUMN_NAME_ENTRY_KEY, key);
        values.put(GroceryDBContract.ItemList.COLUMN_NAME_ITEM_NAME, name);
        values.put(GroceryDBContract.ItemList.COLUMN_NAME_ITEM_COST, cost);
        values.put(GroceryDBContract.ItemList.COLUMN_NAME_ITEM_QUANTITY, quantity);
        newRowId = database.insert(
                GroceryDBContract.GroceryList.TABLE_NAME,
                null,
                values);
        database.close();
        return newRowId;
    }


    /**
     * get all data from grocery list stored in android DB
     * @return ArrayList of array with name and key for a specific list stored in the DB
     */

    public ArrayList<String[]> getAllListFromAndroidDB(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String[]> data = new ArrayList<String[]>();

        String[] projection = {
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE,
                GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY
        };

        String sortOrder =
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE + " ASC";

        Cursor c = db.query(
                GroceryDBContract.GroceryList.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(c.moveToNext()){
            String[] item = {c.getString(0), c.getString(1)};
            data.add(item);
        };
        c.close();
        db.close();
        return data;
    }

    /**
     * get all item from a specific grocery list stored in android DB
     * @params list key
     * @return ArrayList of array with item id, name, cost and quantity for a specific item stored in the list
     */

    public ArrayList<String[]> getItemFromAndroidDB(String key){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String[]> data = new ArrayList<String[]>();

        String[] projection = {
                GroceryDBContract.ItemList.COLUMN_NAME_ID,
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_NAME,
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_COST,
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_QUANTITY
        };

        String sortOrder =
                GroceryDBContract.ItemList.COLUMN_NAME_ITEM_NAME + " ASC";

        Cursor c = db.query(
                GroceryDBContract.ItemList.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        while(c.moveToNext()){
            String[] item = {c.getString(0), c.getString(1), c.getString(2), c.getString(3)};
            data.add(item);
        };
        c.close();
        db.close();
        return data;
    }


    /**
     * delete grocery list stored in android DB
     * @param key unique ID that represents a particular row/grocery item
     * @return boolean value representing if delete was succesfull or not
     */
    // Need to check this mehod once UI is ready as String itemID compared rather than INT.
    public boolean deleteList(String key) {

        boolean result = false;
        String query = "Select * FROM " + GroceryDBContract.GroceryList.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        String itemKey = "" + key;

        if (c.moveToNext()) {
            String[] value = {itemKey};
            db.delete(GroceryDBContract.GroceryList.TABLE_NAME, GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY+ " = ?" ,value);
            cleatListItem(itemKey);
            c.close();
            result = true;
        }
        c.close();
        db.close();
        return result;
    }

    /**
     * delete  item stored in grocery list
     * @param id unique ID that represents a particular row/grocery item
     * @return boolean value representing if delete was succesfull or not
     */
    // Need to check this mehod once UI is ready as String itemID compared rather than INT.
    public boolean deleteItem(String id) {

        boolean result = false;
        String query = "Select * FROM " + GroceryDBContract.ItemList.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        String itemKey = "" + id;

        if (c.moveToNext()) {
            String[] value = {itemKey};
            db.delete(GroceryDBContract.ItemList.TABLE_NAME, GroceryDBContract.ItemList.COLUMN_NAME_ID + " = ?", value);
            c.close();
            result = true;
        }
        c.close();
        db.close();
        return result;
    }

    /**
     * delete  item stored in grocery list
     * @param key unique key that represents list it belongs to.
     * @return boolean value representing if delete was succesfull or not
     */
    // Need to check this mehod once UI is ready as String itemID compared rather than INT.
    public boolean cleatListItem(String key) {

        boolean result = false;
        String query = "Select * FROM " + GroceryDBContract.ItemList.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        String itemKey = "" + key;

        while (c.moveToNext()) {
            String[] value = {itemKey};
            db.delete(GroceryDBContract.ItemList.TABLE_NAME, GroceryDBContract.ItemList.COLUMN_NAME_ID+ " = ?" ,value);
            c.close();
            result = true;
        }
        c.close();
        db.close();
        return result;
    }


    /**
     * empty/clear android DB
     */
    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(GroceryDBContract.GroceryList.TABLE_NAME, null, null);
        db.delete(GroceryDBContract.ItemList.TABLE_NAME, null, null);
        db.close();
    }

    /**
     * delete grocery list item stored in android DB
     * @param itemKey unique ID that represents a particular row/grocery item
     * @return String[] with listTitle and listKey
     */
    public String[] findListByKey(String itemKey){
        String query = "Select * FROM " + GroceryDBContract.GroceryList.TABLE_NAME + " WHERE " + GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY + " =  \"" + itemKey + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        String[] item = new String[2];
        if (c.moveToFirst()) {
            c.moveToFirst();
            item[0]= c.getString(0);
            item[1]= c.getString(1);
            c.close();
        } else {
            item = null;
        }
        db.close();
        return item;
    }

    /**
     * delete grocery list item stored in android DB
     * @param itemid unique ID that represents a particular item in the list
     * @return String[] item id, name, cost and quantity for a specific item stored in the list
     */
    public String[] findItemByID(String itemid){
        String query = "Select * FROM " + GroceryDBContract.ItemList.TABLE_NAME + " WHERE " + GroceryDBContract.ItemList.COLUMN_NAME_ID + " =  \"" + itemid + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        String[] item = new String[4];
        if (c.moveToFirst()) {
            c.moveToFirst();
            item[0]= c.getString(0);
            item[1]= c.getString(1);
            item[2]= c.getString(2);
            item[3]= c.getString(3);
            c.close();
        } else {
            item = null;
        }
        db.close();
        return item;
    }
}
