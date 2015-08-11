package com.codeu.teamjacob.groups.ui.items;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ItemEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.ui.EntryLoader;

public class ItemsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ItemEntry[]> {

    /**
     * The callback when items are clicked on
     */
    public interface Callback {
        void onItemClick(GroupEntry entry);

        void onItemLongClick(GroupEntry entry);
    }

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";

    //The group entry loader id
    public static final int GROUPS_LOADER = 3;

    ListView groupList;
    ItemEntryAdapter itemEntryAdapter;

    public ListEntry listEntry;

    public boolean editMode;
    public boolean deleteMode;

    Menu menuOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        editMode = false;
        deleteMode = false;

        //Get the root view and the list view
        final View rootView = inflater.inflate(R.layout.fragment_item, container, false);


        listEntry = ListDatabase.getById(getActivity(), getArguments().getLong(EXTRA_LIST_ID));

        groupList = (ListView) rootView.findViewById(R.id.item_list_view);

        //Create the group adapter
        itemEntryAdapter = new ItemEntryAdapter(getActivity(), R.layout.fragment_item, editMode);
        groupList.setAdapter(itemEntryAdapter);

        //Set the callback of a click to the activity
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //((Callback) getActivity()).onItemClick((GroupEntry) parent.getItemAtPosition(position));
            }
        });

        //Set the callback of a log click to the activity
        groupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //((Callback) getActivity()).onItemLongClick((GroupEntry) parent.getItemAtPosition(position));
                return false;
            }
        });

        return rootView;
    }

    /**
     * Reload the data in the adapter
     */
    public void reloadData() {
        getLoaderManager().restartLoader(GROUPS_LOADER, null, this);
    }

    /**
     * Initiate the loader to load the entries
     *
     * @param savedInstanceState the saved bundle
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(GROUPS_LOADER, null, this);
    }

    /**
     * Reloads the apater when the activity resumes
     */
    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    /**
     * Create the loader to load the entries into the adapter
     *
     * @param id   the id of the loader
     * @param args the extra loader arguments
     * @return the loader to load the entries
     */
    @Override
    public Loader<ItemEntry[]> onCreateLoader(int id, Bundle args) {

        return new EntryLoader<ItemEntry[]>(getActivity()) {
            @Override
            public ItemEntry[] loadInBackground() {

                //Get the groups the user has access to
                ItemEntry[] groups = listEntry.getItems(getActivity());
                Log.d("Items","Id: "+listEntry.getId()+" , "+groups.length+"");
                return groups;
            }
        };
    }


    public boolean onBackPressed(){
        if (deleteMode){
            deleteMode = false;
            menuOptions.findItem(R.id.action_trash_items).setVisible(true);
            menuOptions.findItem(R.id.action_edit_mode).setVisible(true);
            menuOptions.findItem(R.id.action_select_all_items).setVisible(false);
            itemEntryAdapter.toggleDeleteMode();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.item, menu);
        menuOptions = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.action_edit_mode);
        checkable.setChecked(editMode);

        menu.findItem(R.id.action_trash_items).setVisible(false);
        menuOptions.findItem(R.id.action_select_all_items).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_mode:
                editMode = !item.isChecked();
                if (editMode){
                    item.setIcon(R.mipmap.ic_action_accept_holo_dark);
                    //item.setTitle()
                } else{
                    item.setIcon(R.mipmap.ic_action_edit_holo_dark);
                }
                menuOptions.findItem(R.id.action_trash_items).setVisible(editMode);
                itemEntryAdapter.toggleEditMode();
                item.setChecked(editMode);
                return true;
            case R.id.action_trash_items:
                item.setVisible(false);
                menuOptions.findItem(R.id.action_edit_mode).setVisible(false);

                menuOptions.findItem(R.id.action_select_all_items).setVisible(true);
                deleteMode = true;
                itemEntryAdapter.toggleDeleteMode();
                return true;
            case R.id.action_select_all_items:
                itemEntryAdapter.selectAll(true);
                return true;
            default:
                return false;
        }
    }

    /**
     * Set the adapter to the data returned from the loader
     *
     * @param loader       the entry loader
     * @param groupEntries the entry data from the loader
     */
    @Override
    public void onLoadFinished(Loader<ItemEntry[]> loader, ItemEntry[] groupEntries) {
        itemEntryAdapter.set(groupEntries);
        itemEntryAdapter.notifyDataSetChanged();
    }

    /**
     * Reset the adapter when the loader resets
     *
     * @param loader the entry loader
     */
    @Override
    public void onLoaderReset(Loader<ItemEntry[]> loader) {
        itemEntryAdapter.set(null);
    }

}