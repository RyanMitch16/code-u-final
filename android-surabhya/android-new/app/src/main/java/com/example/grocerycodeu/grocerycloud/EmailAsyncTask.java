package com.example.grocerycodeu.grocerycloud;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by saryal on 8/5/2015.
 */
public class EmailAsyncTask extends AsyncTask<String, Void, Void> {


    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {
        final EmailSender sender = new EmailSender();
        try {
            sender.sendEmail(params[0], params[1],params[2]);
        } catch (RuntimeException e) {
            Log.e("RunTimeException", e.getMessage(), e);
        }
        return null;
    }

    protected void onProgressUpdate() {

    }

}



