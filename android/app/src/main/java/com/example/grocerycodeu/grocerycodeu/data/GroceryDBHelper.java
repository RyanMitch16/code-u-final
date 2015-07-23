package com.example.grocerycodeu.grocerycodeu.data;

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
    private static final int DATABASE_VERSION = 4;
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
        final String SQL_CREATE_MENU_TABLE = "CREATE TABLE " + GroceryDBContract.GroceryList.TABLE_NAME + " (" +
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY + " TEXT NOT NULL " +
                " )";
        sqLiteDatabase.execSQL(SQL_CREATE_MENU_TABLE);
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
     * get all data from grocery list stored in android DB
     * @return ArrayList of array with name and key for a specific list stored in the DB
     */

    public ArrayList<String[]> getAllDataFromAndroidDB(){

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
     * delete grocery list item stored in android DB
     * @param key unique ID that represents a particular row/grocery item
     * @return boolean value representing if delete was succesfull or not
     */
    // Need to check this mehod once UI is ready as String itemID compared rather than INT.
    public boolean deleteItem(String key) {

        boolean result = false;
        String query = "Select * FROM " + GroceryDBContract.GroceryList.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        String itemKey = "" + key;

        if (c.moveToNext()) {
            String[] value = {itemKey};
            db.delete(GroceryDBContract.GroceryList.TABLE_NAME, GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_KEY+ " = ?" ,value);
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
        db.close();
    }

    /**
     * delete grocery list item stored in android DB
     * @param itemKey unique ID that represents a particular row/grocery item
     * @return String[] with listTitle and listKey
     */
    public String[] findByID(String itemKey){
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
}
