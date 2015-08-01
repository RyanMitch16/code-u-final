package com.example.grocerycodeu.grocerycloud.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Map;

public class Entry<T> implements BaseColumns {

    static Map<Class, EntryDatabase> databaseMap;

    //The id of the entry
    private long _id = -1;

    /**
     * Default constructor
     */
    Entry() { }

    /**
     * Put the values of the entry into a content values object
     * @return  the content values object with the entry information
     */
    public ContentValues getValues(){
        return null;
    }

    /**
     * Override and set the values of the entry using the cursor
     * @param cursor   the cursor to get the values from
     */
    public void setValues(Cursor cursor) {

    }

    /**
     * Retrieve the id of the entry
     * @return  the entry id
     */
    public final long getId() {
        return _id;
    }

    /**
     * Retrieve the id of the entry
     * @return  the entry id
     */
    protected final void setId(long id) {
        _id = id;
    }

}