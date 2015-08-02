package com.example.grocerycodeu.grocerycodeu;

import android.provider.BaseColumns;

/**
 * Created by saryal on 7/19/15.
 */
public class GroceryDBContract {

    public GroceryDBContract() {

    }

    public static abstract class GroceryList implements BaseColumns {
        public static final String TABLE_NAME = "grocerylist";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_ENTRY_KEY = "entrykey";
    }

    public static abstract class ItemList implements BaseColumns {
        public static final String TABLE_NAME = "itemlist";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ENTRY_KEY = "entrykey";
        public static final String COLUMN_NAME_ITEM_NAME = "itemname";
        public static final String COLUMN_NAME_ITEM_COST = "itemcost";
        public static final String COLUMN_NAME_ITEM_QUANTITY = "itemquantity";
    }
}
