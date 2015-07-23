package com.example.grocerycodeu.grocerycodeu;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroceryListAdapter extends CursorAdapter {

    ArrayList<String> data;

    public GroceryListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, final Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_row, viewGroup, false);

        TextView textNumber = (TextView) view.findViewById(R.id.text_content);
        Button delButton = (Button) view.findViewById(R.id.btn_del);

        textNumber.setText("HelloWold");

        delButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //data.remove(cursor.getPosition());
                notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
