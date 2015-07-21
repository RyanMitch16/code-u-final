package com.example.grocerycodeu.grocerycodeu;

import java.util.ArrayList;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private ListView numberList;
    MyThumbnailAdapter delAdapter = null;
    ArrayList<String> dummyData = new ArrayList<>();

    // Creates fake items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        numberList = (ListView) findViewById(R.id.listview);

        for (int i = 0; i < 30; ++i) {
            dummyData.add("Foo " + i + " Bar");
        }

        // Binder
        delAdapter = new MyThumbnailAdapter(this, R.layout.list_row, dummyData);
        numberList.setAdapter(delAdapter);
    }

    public class MyThumbnailAdapter extends ArrayAdapter<String> {

        ArrayList<String> data;

        public MyThumbnailAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {

            super(context, textViewResourceId, objects);
            data = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.list_row, parent, false);
            TextView textNumber = (TextView) view.findViewById(R.id.text_content);
            Button delButton = (Button) view.findViewById(R.id.btn_del);

            delButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    data.remove(position);
                    // data.add(position, "oink");
                    delAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            });

            textNumber.setText(data.get(position));
            return view;
        }
    }
}
