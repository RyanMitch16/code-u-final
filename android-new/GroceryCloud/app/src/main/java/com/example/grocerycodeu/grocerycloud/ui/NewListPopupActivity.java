package com.example.grocerycodeu.grocerycloud.ui;

import android.accounts.Account;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.grocerycodeu.grocerycloud.R;
import com.example.grocerycodeu.grocerycloud.database.EntryDatabase;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract.UserEntry;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract.GroceryListEntry;
import com.example.grocerycodeu.grocerycloud.sync.GrocerySyncAccount;
import com.example.grocerycodeu.grocerycloud.sync.request.GroceryRequest;
import com.example.grocerycodeu.grocerycloud.sync.request.HttpRequest;
import com.example.grocerycodeu.grocerycloud.ui.base.PopupActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class NewListPopupActivity extends PopupActivity implements LoaderManager.LoaderCallbacks<HttpURLConnection>{

    EditText txtName;
    Button btnCreate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_list_create);

        int width = convertWidthPercentToPixels(1.0f);
        int height = convertHeightPercentToPixels(0.3f);

        final NewListPopupActivity thisActivity = this;

        getWindow().setLayout(width,height);

        txtName = (EditText) findViewById(R.id.list_name_text_view);

        btnCreate = (Button) findViewById(R.id.create_button);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLoaderManager().initLoader(GroceryRequest.OPCODE_LIST_CREATE, new Bundle(), thisActivity).forceLoad();

            }
        });
    }

    @Override
    public Loader<HttpURLConnection> onCreateLoader(int id, Bundle args) {

        Account account = GrocerySyncAccount.getSyncAccount(this);
        return GroceryRequest.listCreate(this,
                GrocerySyncAccount.getUserKey(this, account),
                txtName.getText().toString());
    }

    @Override
    public void onLoadFinished(Loader<HttpURLConnection> loader, HttpURLConnection data) {

        try {
            //Check if the response code is a success
            int id = data.getResponseCode();
            if (id >= 200 && id < 300) {

                //Get the user key from the request
                String listKey = HttpRequest.getContentString(data);

                //Add the list to the database
                EntryDatabase<GroceryListEntry> entryDatabase = GroceryListEntry.getDatabase();
                GroceryListEntry list = new GroceryListEntry(listKey,txtName.getText().toString(),null, 0, GroceryListEntry.UPDATED_FALSE);
                entryDatabase.put(this,list);

                Log.d("fuckkkkkkk","done");
            }
        }catch (Exception e){
            Log.d("XX",e.toString());
        }
    }

    @Override
    public void onLoaderReset(Loader<HttpURLConnection> loader) {

    }
}
