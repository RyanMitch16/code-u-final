package com.example.grocerycodeu.grocerycodeu;

import android.provider.BaseColumns;

/**
 * Created by saryal on 7/19/15.
 */
public class GroceryDBContract {

    public GroceryDBContract(){

    }

    public static abstract class GroceryList implements BaseColumns {
        public static final String TABLE_NAME = "grocerylist";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_TOTAL_ORDER= "totalorder";
    }

}