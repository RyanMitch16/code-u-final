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

    // URL to get JSON
    private static String urlGetUserList = "http://code-u-final.appspot.com/user/lists?user_key=ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAgICACgw";
    private static String urlGetUserItem = "http://code-u-final.appspot.com/list/get?user_key=ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw&list_key=ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgICAgAsM";

    // JSON Node names
    private static final String TAG_GROCERY_LIST = "itemLists";
    private static final String TAG_LIST_NAME = "name";
    private static final String TAG_LIST_KEY = "key";

    private static final String TAG_ITEM_LIST = "items";
    private static final String TAG_ITEM_ID = "_id";
    private static final String TAG_ITEM_NAME = "name";
    private static final String TAG_ITEM_COST = "cost";
    private static final String TAG_ITEM_QUANTITY = "item-quantity";

    // contacts JSONArray
    JSONArray groceryListJsonArray = null;
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
            String jsonStrGroceryList = sh.makeServiceCall(urlGetUserList, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStrGroceryList);

            if (jsonStrGroceryList != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStrGroceryList);

                    // Getting JSON Array node
                    groceryListJsonArray = jsonObj.getJSONArray(TAG_GROCERY_LIST);

                    // looping through All Contacts
                    for (int i = 0; i < groceryListJsonArray.length(); i++) {
                        JSONObject c = groceryListJsonArray.getJSONObject(i);

                        String listName = c.getString(TAG_LIST_NAME);
                        String listKey = c.getString(TAG_LIST_KEY);

                        // Add data to database
                        dbHelper.addDataFromCloud(listName, listKey);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the grocery list url");
            }

            String jsonStrItemList = sh.makeServiceCall(urlGetUserItem, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStrItemList);

            if (jsonStrItemList != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStrItemList);

                    // Getting JSON Array node
                    itemListJsonArray = jsonObj.getJSONArray(TAG_ITEM_LIST);
                    Log.e("ServiceHandler", itemListJsonArray.length()+"");
                    // looping through All Contacts
                    for (int i = 0; i < itemListJsonArray.length(); i++) {
                        JSONObject c = itemListJsonArray.getJSONObject(i);

                        String itemID = c.getString(TAG_ITEM_ID);
                        String itemName = c.getString(TAG_ITEM_NAME);
                        int itemQuantity = c.getInt(TAG_ITEM_QUANTITY);

                        // Add data to database
                        Log.e("OUTPUT", itemID + " " + itemName + " " + itemQuantity);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the item list url");
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
                ArrayList<String[]> data = dbHelper.getAllDataFromAndroidDB();
                for (String[] x: data){
                    Log.e("NAME:",x[0]);
                    Log.e("KEY:",x[1]);
                }
                Log.e("onPostExecute", "true");
            }
        }
    }
}
