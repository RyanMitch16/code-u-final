package com.example.grocerycodeu.grocerycodeu;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class GroceryListFragment extends Fragment {

    ListView groceryListView;
    GroceryListAdapter delAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.grocery_list_fragment, container, false);

        ListView groceryListView = (ListView) rootView.findViewById(R.id.grocery_list_view);

        // Binder
        delAdapter = new GroceryListAdapter(getActivity(), null, 0);
        groceryListView.setAdapter(delAdapter);

        return rootView;
    }
}
