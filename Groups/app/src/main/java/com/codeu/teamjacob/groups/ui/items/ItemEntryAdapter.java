package com.codeu.teamjacob.groups.ui.items;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.ItemEntry;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.ui.EntryAdapter;
import com.codeu.teamjacob.groups.ui.lists.ListsActivity;

import java.util.ArrayList;

public class ItemEntryAdapter extends EntryAdapter<ItemEntry> {

    public boolean editMode;

    public ArrayList<CheckBox> deleteCheckBoxes;
    public ArrayList<CheckBox> completedCheckBoxes;


    public class ViewHolder{

        public final TextView itemNameView;
        public final CheckBox completedBox;
        public final CheckBox deleteBox;

        public ViewHolder(View view) {
            itemNameView = (TextView) view.findViewById(R.id.item_name_text_view);
            completedBox = (CheckBox) view.findViewById(R.id.item_completed_check_box);
            deleteBox = (CheckBox) view.findViewById(R.id.item_delete_check_box);
        }
    }


    public ItemEntryAdapter(Activity context, @LayoutRes int resource){
        super(context, resource);
        this.editMode = false;

        deleteCheckBoxes = new ArrayList<CheckBox>();
        completedCheckBoxes = new ArrayList<CheckBox>();
    }

    @Override
    public View newView(Context context, final int position, ViewGroup parent) {
        View view =  LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public View bindView(Context context, final int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.itemNameView.setText(getItem(position).itemName);
        holder.deleteBox.setVisibility(View.INVISIBLE);
        holder.completedBox.setVisibility((editMode) ? View.INVISIBLE : View.VISIBLE);

        deleteCheckBoxes.add(holder.deleteBox);
        completedCheckBoxes.add(holder.completedBox);

        return view;
    }

    public void toggleEditMode(){
        editMode = !editMode;
        if (!editMode){
            for (CheckBox checkBox : completedCheckBoxes) {
                checkBox.setVisibility(View.VISIBLE);
            }
            for (CheckBox checkBox : deleteCheckBoxes) {
                checkBox.setVisibility(View.INVISIBLE);
            }
        } else {
            for (CheckBox checkBox : completedCheckBoxes) {
                checkBox.setVisibility(View.INVISIBLE);
            }
            for (CheckBox checkBox : deleteCheckBoxes) {
                checkBox.setVisibility(View.VISIBLE);
            }
        }

    }

    public void selectAll(boolean select){
        for (CheckBox checkBox : deleteCheckBoxes) {
            checkBox.setChecked(true);
        }
    }

    public void delete(){
        for (int i=0;i<deleteCheckBoxes.size();i++){
            CheckBox checkBox = deleteCheckBoxes.get(i);
            if (checkBox.isChecked()){
                remove(getItem(i));
            }
        }
    }

}