package com.codeu.teamjacob.groups.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class Utility {

    public static final String PREFERENCE_FILE_NAME = "SETTINGS";

    public static String GROUP_ID = "GROUP_ID";

    public static void setGroupId(Context context, long id) {

        SharedPreferences mPrefs = context.getSharedPreferences(PREFERENCE_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putLong(GROUP_ID, id);
        ed.commit();
    }

    public static long getGroupId(Context context) {

        SharedPreferences mPrefs = context.getSharedPreferences(PREFERENCE_FILE_NAME, context.MODE_PRIVATE);
        return mPrefs.getLong(GROUP_ID, -1);

    }

    public static String readMyNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1Number();
    }

    public static void readContacts(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            while (cur.moveToNext()) {


                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Log.e("name", name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                        // I guess a code block must be added to add phoen number to db

                        Log.e("phone", " " + phone);
                    }
                    pCur.close();
                }
            }
        }
    }

    //Bitmap to String:
    public static String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }


    //String to Bitmap:
    public static Bitmap stringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
