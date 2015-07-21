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
    private static final int DATABASE_VERSION =2;
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
                GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_COST + " REAL NOT NULL, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_IMAGE + " TEXT NOT NULL, " +
                GroceryDBContract.GroceryList.COLUMN_NAME_TOTAL_ORDER + " INTEGER NOT NULL " +
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
      * @param  item object for the class walmartItemObject
      * @return unique long ID
      */
    public long addDataFromCloud(walmartItemObject item){

        long newRowId;
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_ID, item.getItemID());
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_TITLE, item.getitemTitle());
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_COST, item.getItemCost());
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_IMAGE, item.getItemImage());
            values.put(GroceryDBContract.GroceryList.COLUMN_NAME_TOTAL_ORDER, item.getTotalOrder());
        newRowId = database.insert(
                GroceryDBContract.GroceryList.TABLE_NAME,
                null,
                values);
        database.close();
        return newRowId;
    }

    /**
     * get all data from grocery list stored in android DB
     * @return ArrayList of walmartItemObject stored in the DB
     */

    public ArrayList<walmartItemObject> getAllDataFromAndroidDB(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<walmartItemObject> data = new ArrayList<walmartItemObject>();

        String[] projection = {
                GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_ID,
                GroceryDBContract.GroceryList.COLUMN_NAME_TITLE,
                GroceryDBContract.GroceryList.COLUMN_NAME_COST,
                GroceryDBContract.GroceryList.COLUMN_NAME_IMAGE,
                GroceryDBContract.GroceryList.COLUMN_NAME_TOTAL_ORDER
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
            walmartItemObject item = new walmartItemObject(c.getInt(0),c.getString(1),c.getDouble(2),c.getString(3),c.getInt(4));
            data.add(item);
        };
        c.close();
        db.close();
        return data;
    }

    /**
     * delete grocery list item stored in android DB
     * @param itemID unique ID that represents a particular row/grocery item
     * @return boolean value representing if delete was succesfull or not
     */
    // Need to check this mehod once UI is ready as String itemID compared rather than INT.
    public boolean deleteItem(int itemID) {

        boolean result = false;
        String query = "Select * FROM " + GroceryDBContract.GroceryList.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);
        String StringItemID = "" + itemID;

        if (c.moveToNext()) {
            String[] value = {StringItemID};
            db.delete(GroceryDBContract.GroceryList.TABLE_NAME, GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_ID+ " = ?" ,value);
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
     * @param itemID unique ID that represents a particular row/grocery item
     * @return walmartItemObject object
     */
    public walmartItemObject findByID(int itemID){
        String query = "Select * FROM " + GroceryDBContract.GroceryList.TABLE_NAME + " WHERE " + GroceryDBContract.GroceryList.COLUMN_NAME_ENTRY_ID + " =  \"" + itemID + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        walmartItemObject item;

        if (c.moveToFirst()) {
            c.moveToFirst();
            item = new walmartItemObject(c.getInt(0),c.getString(1),c.getDouble(2),c.getString(3),c.getInt(4));
            c.close();
        } else {
            item = null;
        }
        db.close();
        return item;
    }
}
