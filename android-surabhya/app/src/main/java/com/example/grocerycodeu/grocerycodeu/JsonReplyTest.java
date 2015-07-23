package com.example.grocerycodeu.grocerycodeu;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class JsonReplyTest extends ActionBarActivity {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://code-u-final.appspot.com/user/lists?user_key=ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAgICACgw";

    // JSON Node names
    private static final String TAG_ITEM_LIST = "itemLists";
    private static final String TAG_NAME = "name";
    private static final String TAG_KEY = "key";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_COST = "cost";
    private static final String TAG_QUANTITY = "quantity";

    // contacts JSONArray
    JSONArray itemListJsonArray = null;

    // Database helper method
    GroceryDBHelper dbHelper = new GroceryDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_reply_test);
        new GetContacts().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_json_reply_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(JsonReplyTest.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            Log.e("onPreExecute", "true");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    itemListJsonArray = jsonObj.getJSONArray(TAG_ITEM_LIST);

                    // looping through All Contacts
                    for (int i = 0; i < itemListJsonArray.length(); i++) {
                        JSONObject c = itemListJsonArray.getJSONObject(i);

                        String name = c.getString(TAG_NAME);
                        String key = c.getString(TAG_KEY);

                        // Add data to database
                        dbHelper.addDataFromCloud(name, key);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                Log.e("onPostExecute", "true");
            }
        }
    }
}
