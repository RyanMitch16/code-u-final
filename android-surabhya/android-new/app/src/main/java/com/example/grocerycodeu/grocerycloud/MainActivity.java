package com.example.grocerycodeu.grocerycloud;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.grocerycodeu.grocerycloud.sync.GrocerySyncAccount;
import com.example.grocerycodeu.grocerycloud.sync.GrocerySyncAdapter;
import com.example.grocerycodeu.grocerycloud.ui.NewListPopupActivity;
import com.example.grocerycodeu.grocerycloud.ui.base.FloatingButtonView;


public class MainActivity extends Activity{

    public static final String EXTRA_USER_KEY = "EXTRA_USER_KEY";

    public Account applicationAccount;

    public String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve the logged in user's key
        userKey = getIntent().getStringExtra(EXTRA_USER_KEY);
        applicationAccount = GrocerySyncAccount.getSyncAccount(this);

        //Set the user key in the application account if the user key extra has been passed in
        if (userKey != null) {
            GrocerySyncAccount.setUserKey(this, applicationAccount, userKey);
        }
        else{
            //Go to the log in activity if the user key is not present in the account
            userKey = GrocerySyncAccount.getUserKey(this, applicationAccount);
            if (userKey.equals("")){
                Intent intent = new Intent(this, UserLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return;
            }
        }

        final Activity thisActivity = this;

        setContentView(R.layout.activity_main);

        refreshGroceryLists();

        FloatingButtonView addListButton = (FloatingButtonView) findViewById(R.id.new_list_button);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, NewListPopupActivity.class);
                startActivity(intent);
            }
        });

    }

    public void refreshGroceryLists(){
        GrocerySyncAdapter.syncImmediatelyUserGetLists(this, applicationAccount);
    }
}
