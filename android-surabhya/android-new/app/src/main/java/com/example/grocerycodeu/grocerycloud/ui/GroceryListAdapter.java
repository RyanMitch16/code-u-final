package com.example.grocerycodeu.grocerycloud.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grocerycodeu.grocerycloud.R;
import com.example.grocerycodeu.grocerycloud.database.GroceryContract;

public class GroceryListAdapter extends CursorAdapter {


    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            groceryListFragment.getLoaderManager().restartLoader(10, null, groceryListFragment);
        }
    };

            GroceryListFragment groceryListFragment;

    /**
     * Cache of the children views for a grocery list
     */
    public class ViewHolder{

        public final TextView listNameView;

        public ViewHolder(View view) {
            listNameView = (TextView) view.findViewById(R.id.grocery_list_name_text_view);
        }
    }

    public GroceryListAdapter(Context context, Cursor c, int flags, GroceryListFragment groceryListFragment) {
        super(context, c, flags);
        this.groceryListFragment = groceryListFragment;

            IntentFilter filter1=  new IntentFilter("com.example.grocerycodeu.grocerycloud.synced");
        groceryListFragment.getActivity().registerReceiver(myReceiver, filter1);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        Log.d("View", "New");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.d("Bind", "New");
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.listNameView.setText(cursor.getString(GroceryListFragment.COL_LIST_NAME));

    }

}

