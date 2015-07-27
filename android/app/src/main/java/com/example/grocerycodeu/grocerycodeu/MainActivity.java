package com.example.grocerycodeu.grocerycodeu;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grocerycodeu.grocerycodeu.ui.FloatingButtonView;

public class MainActivity extends ActionBarActivity {


    // Creates fake items
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_list_activity);

        /*ListView numberList = (ListView) findViewById(R.id.grocery_list_view);

        for (int i = 0; i < 5; ++i) {
            dummyData.add("Foo " + i + " Bar");
        }

        FloatingButtonView addButtonView = new FloatingButtonView(this,200,
                getResources().getColor(R.color.material_accent),
                getResources().getDrawable(R.mipmap.ic_launcher));*/

    }
}
