package com.example.grocerycodeu.grocerycloud;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by saryal on 8/5/2015.
 */
public class EmailAsyncTask extends AsyncTask<String, Void, Integer> {


    protected void onPreExecute() {

    }

    @Override
    protected Integer doInBackground(String... params) {
        final EmailSender sender = new EmailSender();
        try {
            if (sender.sendEmail(params[0], params[1])){
                Log.e("Status", "Emai send");
                return 1;
            } else{
                Log.e("Status", "Emai send");
                return 0;
            }
        } catch (RuntimeException e) {
            Log.e("SendMail", e.getMessage(), e);
            return 0;
        }
    }

    protected void onProgressUpdate() {

    }

}



