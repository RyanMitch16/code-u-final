package com.codeu.teamjacob.groups.ui.lists;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.ui.EntryAdapter;

public class ListsEntryAdapter extends EntryAdapter<ListEntry> {

    public class ViewHolder{

        public final TextView listNameView;

        public ViewHolder(View view) {
            listNameView = (TextView) view.findViewById(R.id.list_name_text_view);
        }
    }

    ListsActivity activityContext;

    public ListsEntryAdapter(ListsActivity context, @LayoutRes int resource){
        super(context, resource);
        activityContext = context;
    }

    @Override
    public View newView(Context context, final int position, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public View bindView(Context context, final int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.listNameView.setText(getItem(position).listName);

        return view;
    }

}