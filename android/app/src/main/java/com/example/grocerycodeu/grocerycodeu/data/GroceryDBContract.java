package com.example.grocerycodeu.grocerycodeu.data;

import android.provider.BaseColumns;

/**
 * Created by saryal on 7/19/15.
 */
public class GroceryDBContract {

    public GroceryDBContract(){

    }

    public static abstract class GroceryList implements BaseColumns {
        public static final String TABLE_NAME = "grocerylist";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_ENTRY_KEY = "entrykey";
    }

}
