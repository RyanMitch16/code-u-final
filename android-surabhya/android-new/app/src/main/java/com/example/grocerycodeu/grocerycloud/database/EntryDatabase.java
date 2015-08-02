package com.example.grocerycodeu.grocerycloud.database;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EntryDatabase<T extends Entry> {

    public Uri contentUri;

    public Class<T> classType;

    public List<String> projection = new ArrayList<String>();

    public EntryDatabase(Uri contentUri, Class<T> classType){
        this.contentUri = contentUri;
        this.classType = classType;
        projection.add(BaseColumns._ID);
    }

    /**
     * Adds a new projection to
     * @param columnName
     * @return
     */
    public int addProjection(String columnName){
        projection.add(columnName);
        return projection.size()-1;
    }

    public T[] query(Context context, @Nullable String selection,
                                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Retrieve the users following the selection
        Cursor cursor = context.getContentResolver().query(
                contentUri, projection.toArray(new String[projection.size()]), selection, selectionArgs, sortOrder);

        try {
            //Create an array to hold the selected users
            int count = cursor.getCount();
            cursor.moveToFirst();
            T[] users = (T[]) Array.newInstance(classType,count );

            for (int i = 0; i < count; i++) {
                //Create a new user entry to hold the properties
                T newUser = classType.newInstance();
                newUser.setValues(cursor);
                newUser.setId(cursor.getLong(0));

                users[i] = newUser;

                cursor.moveToNext();
            }
            cursor.close();
            return users;
        }catch (Exception e){
            Log.e("XXX",e.toString());
        }

        return null;
    }

    public T getById(Context context, long id){
        //Retrieve the users following the selection
        Cursor cursor = context.getContentResolver().query(
                contentUri, projection.toArray(new String[projection.size()]),
                T._ID + " =? ", new String[]{"" + id}, null);

        int count = cursor.getCount();
        if (count == 0) {
            cursor.close();
            return null;
        }
        try{
            //Create a new user entry
            cursor.moveToFirst();
            T newUser = classType.newInstance();
            newUser.setValues(cursor);
            newUser.setId(cursor.getLong(0));

            //Return the found user
            cursor.close();
            return newUser;
        }catch (Exception e){
            Log.e("XXX",e.toString());
        }

        return null;
    }


    /**
     * Inserts or updates the user entry in the databse
     * @param context   the context the user is being put from
     * @return          the id of the user
     */
    public long put(Context context, T entry){

        ContentValues values = entry.getValues();

        long _id = entry.getId();

        if (query(context, T._ID + " =? ", new String[]{"" + _id},null).length > 0){
            //Update if the user exists
            context.getContentResolver().update(
                    contentUri,values, BaseColumns._ID +" =? ", new String[]{""+ _id});
            return _id;
        }
        else{
            //Insert the user entry if it does not exist
            long id = getAppendedId(context.getContentResolver().insert(contentUri,values));
            entry.setId(id);
            return id;
        }
    }

    /**
     * Build the query path to get the list with the specified id
     * @param id        the user id
     * @return          the built content uri
     */
    public Uri buildEntryUri(long id) {
        return ContentUris.withAppendedId(contentUri, id);
    }

    /**
     * Retrieve the id from the uri
     * @param uri   the uri to retrieve the id from
     * @return      the key included in the uri if present
     */
    public long getAppendedId(Uri uri) {
        return ContentUris.parseId(uri);
    }
}
