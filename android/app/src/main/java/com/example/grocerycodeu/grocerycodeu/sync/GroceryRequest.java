package com.example.grocerycodeu.grocerycodeu.sync;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.net.HttpURLConnection;

public class GroceryRequest extends AsyncTaskLoader<HttpURLConnection>{

    private final String LOG_TAG = GroceryRequest.class.getSimpleName();

    public static final String BASE_URL = "http://code-u-final.appspot.com/";

    public static final String TAG_OPCODE = "op";

    public static final int OPCODE_USER_CREATE = 0;

    public static final String TAG_EMAIL = "EMAIL";

    private Bundle params;

    public GroceryRequest(Context context, Bundle params) {
        super(context);
        this.params = params;
    }

    @Override
    public HttpURLConnection loadInBackground() {

        HttpURLConnection connection = null;

        switch (params.getInt(TAG_OPCODE)) {

            case OPCODE_USER_CREATE:
                Uri url = Uri.parse(BASE_URL).buildUpon()
                        .appendPath("user")
                        .appendPath("create")
                        .appendQueryParameter("email", params.getString(TAG_EMAIL))
                        .build();

                Log.d(LOG_TAG, url.toString());

                //Retrieve the user key
                try {
                    connection = HttpRequest.get(url);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "OP_CREATE : "+e.toString());
                }
            break;
        }

        return connection;
    }
}
