package com.example.grocerycodeu.grocerycloud.ui;

import android.accounts.Account;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.content.Loader;
import android.content.CursorLoader;


import com.example.grocerycodeu.grocerycloud.R;
import com.example.grocerycodeu.grocerycloud.UserSignUpActivity;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract;
import com.example.grocerycodeu.grocerycloud.sync.GrocerySyncAccount;
import com.example.grocerycodeu.grocerycloud.sync.request.GroceryRequest;

public class GroceryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = GroceryListFragment.class.getSimpleName();

    private GroceryListAdapter mGroceryListAdapter;
    private ListView mListView;


    private static final String[] GROCERY_LIST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            GroceryContract.GroceryListEntry._ID,
            GroceryContract.GroceryListEntry.COLUMN_LIST_KEY,
            GroceryContract.GroceryListEntry.COLUMN_LIST_NAME,
            GroceryContract.GroceryListEntry.COLUMN_USER_KEYS,
            GroceryContract.GroceryListEntry.COLUMN_LIST_VERSION,
            GroceryContract.GroceryListEntry.COLUMN_LIST_UPDATED,
    };

    public static final int COL_LIST_ID = 0;
    static final int COL_LIST_KEY = 1;
    static final int COL_LIST_NAME = 2;
    static final int COL_USER_KEYS = 3;
    static final int COL_LIST_VERSION = 4;
    static final int COL_LIST_UPDATED = 5;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG,"Create" );

        //Get a reference to this fragment
        final GroceryListFragment thisFragment = this;

        //Get the root view
        View rootView = inflater.inflate(R.layout.fragment_grocery_lists, container, false);

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        mGroceryListAdapter = new GroceryListAdapter(getActivity(), null,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, this);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.grocery_list_view);
        mListView.setAdapter(mGroceryListAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(10, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getLoaderManager().restartLoader(10, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        //Get the user key
        Account account = GrocerySyncAccount.getSyncAccount(getActivity());
        String userKey = GrocerySyncAccount.getUserKey(getActivity(), account);

        Uri weatherForLocationUri = GroceryContract.UserEntry.CONTENT_URI_LISTS;

        Log.d(LOG_TAG, weatherForLocationUri.toString());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                GROCERY_LIST_COLUMNS,
                GroceryContract.UserEntry.COLUMN_USER_KEY + " = ?",
                new String[]{userKey},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGroceryListAdapter.swapCursor(data);

        Log.d(LOG_TAG,"Finished "+loader.getId() );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGroceryListAdapter.swapCursor(null);
    }
}
