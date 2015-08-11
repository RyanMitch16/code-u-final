package com.codeu.teamjacob.groups.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.database.ItemDatabase;
import com.codeu.teamjacob.groups.database.ItemEntry;
import com.codeu.teamjacob.groups.database.ListDatabase;
import com.codeu.teamjacob.groups.database.ListEntry;
import com.codeu.teamjacob.groups.database.UserDatabase;
import com.codeu.teamjacob.groups.database.UserEntry;
import com.codeu.teamjacob.groups.sync.request.GroupsRequest;
import com.codeu.teamjacob.groups.sync.request.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GroupsSyncAdapter extends AbstractThreadedSyncAdapter {

    //The log tag for the class
    private static final String LOG_TAG = GroupsSyncAdapter.class.getSimpleName();

    ContentResolver mContentResolver;

    public static final String EXTRA_ACTION = "EXTRA_ACTION";
    public static final String EXTRA_USER_KEY = "EXTRA_USER_KEY";

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";
    public static final String EXTRA_GROUP_NAME = "EXTRA_GROUP_NAME";
    public static final String EXTRA_GROUP_ADD_USERNAMES = "EXTRA_GROUP_ADD_USERNAMES";
    public static final String EXTRA_GROUP_CONFIRM = "EXTRA_GROUP_CONFIRM";

    public static final String EXTRA_LIST_ID = "EXTRA_LIST_ID";
    public static final String EXTRA_LIST_NAME = "EXTRA_LIST_NAME";

    public static final String EXTRA_ITEM_ID = "EXTRA_ITEM_ID";

    public static final int ACTION_GROUP_CREATE = 0;
    public static final int ACTION_GROUP_ADD_USER = 1;
    public static final int ACTION_GROUP_CONFIRM = 2;
    public static final int ACTION_USER_GET_GROUPS = 3;
    public static final int ACTION_LIST_CREATE = 4;
    public static final int ACTION_ITEM_ADD = 5;


    //Default constructor
    public GroupsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {
            switch (extras.getInt(EXTRA_ACTION)) {
                case ACTION_GROUP_CREATE:{
                    HttpURLConnection con = groupCreate(extras.getString(EXTRA_USER_KEY),
                            extras.getString(EXTRA_GROUP_NAME));
                    int responseCode = con.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 300) {
                        String contentString = HttpRequest.getContentString(con);

                        GroupEntry group = GroupDatabase.getById(getContext(), extras.getLong(EXTRA_GROUP_ID));
                        group.groupKey = contentString;
                        GroupDatabase.put(getContext(), group);

                        Log.d(LOG_TAG,contentString);
                    }
                    else{
                        Log.e(LOG_TAG, HttpRequest.getContentString(con));
                    }}
                break;

                case ACTION_GROUP_ADD_USER:{
                    HttpURLConnection con = groupUserAdd(
                            extras.getLong(EXTRA_GROUP_ID),
                            extras.getString(EXTRA_GROUP_ADD_USERNAMES));

                    int responseCode = con.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 300) {
                        String contentString = HttpRequest.getContentString(con);

                        GroupEntry group = GroupDatabase.getById(getContext(), extras.getLong(EXTRA_GROUP_ID));

                        JSONArray pendingUsers = new JSONArray(contentString);

                        for (int i=0;i<pendingUsers.length();i++){

                            UserEntry user = UserDatabase.getByUsername(getContext(),pendingUsers.getString(i));
                            if (user == null){
                                user = new UserEntry("",pendingUsers.getString(i),null, 0);
                                UserDatabase.put(getContext(), user);

                                group.addPendingUser(user.username);
                                Log.d(LOG_TAG, "USER NOT FOUND");
                            }
                        }

                        GroupDatabase.put(getContext(),group);


                        Log.d(LOG_TAG,contentString);
                    }
                    else{
                        Log.e(LOG_TAG, HttpRequest.getContentString(con));
                    }}
                break;
                case ACTION_USER_GET_GROUPS: {

                    HttpURLConnection con = userGetGroups(extras.getString(EXTRA_USER_KEY));

                    int responseCode = con.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 300) {

                        String contentString = HttpRequest.getContentString(con);

                        //GroupEntry group = GroupDatabase.getById(getContext(), extras.getLong(EXTRA_GROUP_ID));
                        UserEntry userEntry = UserDatabase.getByKey(getContext(),extras.getString(EXTRA_USER_KEY));

                        JSONObject responseJSON = new JSONObject(contentString);

                        JSONArray groups = responseJSON.getJSONArray("groups");

                        for (int i=0;i<groups.length();i++){

                            JSONObject group = groups.getJSONObject(i);

                            GroupEntry groupEntry = GroupDatabase.getByKey(getContext(),group.getString("group_key"));
                            if (groupEntry == null){
                                groupEntry = new GroupEntry(group.getString("group_key"),
                                        group.getString("group_name"),
                                        group.getString("group_usernames"),
                                        group.getString("group_pending_usernames"),
                                        group.getLong("group_version"));

                                Log.e(LOG_TAG+"ZZZZZZZZZZZ",groupEntry.pendingUsers.toString());
                                Log.e(LOG_TAG+"ZZZZZZZZZZZ",groupEntry.users.toString());
                            } else {
                                groupEntry.groupName = group.getString("group_name");
                                groupEntry.users = new JSONArray(group.getString("group_usernames"));
                                groupEntry.pendingUsers = new JSONArray(group.getString("group_pending_usernames"));
                                groupEntry.version = group.getLong("group_version");
                            }
                            GroupDatabase.put(getContext(), groupEntry);

                            userEntry.addGroup(groupEntry.getId());
                            UserDatabase.put(getContext(), userEntry);

                            Log.e(LOG_TAG+"ZZZZZZZZZZZ",userEntry.groupIds.toString());

                            ListEntry[] listsToDelete = groupEntry.getLists(getContext(), true);

                            JSONArray lists = group.getJSONArray("group_lists");
                            for (int j=0;j<lists.length();j++){

                                JSONObject list = lists.getJSONObject(j);

                                ListEntry listEntry = ListDatabase.getByKey(getContext(), list.getString("list_key"));
                                if (listEntry == null){
                                    listEntry = new ListEntry(list.getString("list_key"),
                                            list.getString("list_name"),
                                            groupEntry.getId(),
                                            list.getLong("list_version"));

                                    listEntry.setItems(getContext(),new JSONArray(list.getString("list_contents")));
                                    Log.e(LOG_TAG, (new JSONArray(list.getString("list_contents")).toString()));
                                } else {
                                    listEntry.listKey = list.getString("list_key");
                                    listEntry.listName = list.getString("list_name");
                                    listEntry.version = list.getLong("list_version");
                                    listEntry.setItems(getContext(),new JSONArray(list.getString("list_contents")));
                                    Log.e(LOG_TAG, (new JSONArray(list.getString("list_contents")).toString()));
                                }

                                Log.d(LOG_TAG+"VVVV",list.getString("list_key"));

                                ListDatabase.put(getContext(),listEntry);

                                for (int k=0;k<listsToDelete.length;k++){
                                    if (listsToDelete[k] != null){
                                        if (listsToDelete[k].listKey.equals(listEntry.listKey)){
                                            listsToDelete[k] = null;
                                        }
                                    }
                                }

                            }

                            for (int j=0;j<listsToDelete.length;j++){
                                if (listsToDelete[j] != null){
                                    ListDatabase.delete(getContext(),listsToDelete[j]);

                                }
                            }
                        }

                        Intent newIntent = new Intent(GroupsPeriodicSyncService.BROADCAST_ACTION);
                        getContext().sendBroadcast(newIntent);

                    } else{
                        Log.e(LOG_TAG, HttpRequest.getContentString(con));
                    }
                }
                break;

                case ACTION_GROUP_CONFIRM:{

                    HttpURLConnection con = groupConfirm(extras.getString(EXTRA_USER_KEY),
                            extras.getLong(EXTRA_GROUP_ID),
                            extras.getBoolean(EXTRA_GROUP_CONFIRM));

                    //Intent newIntent = new Intent(GroupsPeriodicSyncService.BROADCAST_ACTION);
                    //getContext().sendBroadcast(newIntent);
                    break;
                }
                
                case ACTION_LIST_CREATE :{

                    HttpURLConnection con = listCreate(extras.getLong(EXTRA_GROUP_ID),
                            extras.getString(EXTRA_LIST_NAME));


                    int responseCode = con.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 300) {

                        String contentString = HttpRequest.getContentString(con);

                        ListEntry listEntry = ListDatabase.getById(getContext(), extras.getLong(EXTRA_LIST_ID));

                        listEntry.listKey = contentString;
                        ListDatabase.put(getContext(),listEntry);
                    }

                }

                break;

                case ACTION_ITEM_ADD : {

                    HttpURLConnection con = itemAdd(extras.getLong(EXTRA_LIST_ID),
                            extras.getLong(EXTRA_ITEM_ID));

                    int responseCode = con.getResponseCode();
                    if (responseCode >= 200 && responseCode <= 300) {

                        String contentString = HttpRequest.getContentString(con);

                        ItemEntry itemEntry = ItemDatabase.getById(getContext(), extras.getLong(EXTRA_ITEM_ID));

                        itemEntry.itemPut = true;
                        ItemDatabase.put(getContext(), itemEntry);
                    }
                }


                break;
            }
        } catch (IOException e){
            Log.e(LOG_TAG, e.toString());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.toString());
        }
    }

    private HttpURLConnection groupCreate(String userKey, String name) {

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("group")
                .appendPath("create")
                .appendQueryParameter("user_key", userKey)
                .appendQueryParameter("name", name)
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;
    }

    private HttpURLConnection groupUserAdd(long groupId, String userNames) {

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("group")
                .appendPath("user")
                .appendPath("add")
                .appendQueryParameter("group_key", GroupDatabase.getById(getContext(), groupId).groupKey)
                .appendQueryParameter("usernames", userNames)
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;
    }

    private HttpURLConnection userGetGroups(String userKey){

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("user")
                .appendPath("get")
                .appendPath("groups")
                .appendQueryParameter("user_key", userKey)
                .appendQueryParameter("versions","[]")
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;

    }

    private HttpURLConnection groupConfirm(String userKey, long groupId, boolean confirm){

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("group")
                .appendPath("user")
                .appendPath("confirm")
                .appendQueryParameter("user_key", userKey)
                .appendQueryParameter("group_key", GroupDatabase.getById(getContext(),groupId).groupKey)
                .appendQueryParameter("confirmation", confirm ? "ACCEPT" : "DENY")
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;

    }

    private HttpURLConnection listCreate(long groupId, String listName){

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("list")
                .appendPath("create")
                .appendQueryParameter("group_key", GroupDatabase.getById(getContext(),groupId).groupKey)
                .appendQueryParameter("name", listName)
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;

    }

    private HttpURLConnection itemAdd(long listId, long itemId){

        ItemEntry itemEntry = ItemDatabase.getById(getContext(), itemId);

        JSONArray item = new JSONArray();

        JSONObject itemValues = new JSONObject();
        try {
            itemValues.put("_id", itemEntry.itemAppEngineId);
            itemValues.put("_op", "add");
            itemValues.put("name", itemEntry.itemName);
            itemValues.put("checked", itemEntry.isChecked ? ItemDatabase.CHECKED : ItemDatabase.UNCHECKED);

            item.put(itemValues);
        } catch (Exception e){
            Log.d(LOG_TAG,e.toString());
        }

        //Build the url for logging in as the user
        Uri url = Uri.parse(GroupsRequest.BASE_URL).buildUpon()
                .appendPath("list")
                .appendPath("edit")
                .appendQueryParameter("list_key", ListDatabase.getById(getContext(), listId).listKey)
                .appendQueryParameter("changed_content", item.toString())
                .build();
        Log.d(LOG_TAG, url.toString());

        //Attempt to login as the user
        HttpURLConnection connection = null;
        try {
            //Make the request
            connection = HttpRequest.get(url);
            connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
        }
        //Return the connection to the url
        return connection;

    }


    public static void syncCreateGroup(Context context, long id, String groupName){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putInt(EXTRA_ACTION,ACTION_GROUP_CREATE);
        bundle.putString(EXTRA_USER_KEY, GroupsSyncAccount.getUserKey(context));
        bundle.putLong(EXTRA_GROUP_ID, id);
        bundle.putString(EXTRA_GROUP_NAME, groupName);

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);
    }

    public static void syncGroupAddUsers(Context context, long groupId, String[] userNames){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        JSONArray userNamesJSON = new JSONArray();
        for (String userName : userNames) {
            userNamesJSON.put(userName);
        }


        bundle.putInt(EXTRA_ACTION, ACTION_GROUP_ADD_USER);
        bundle.putLong(EXTRA_GROUP_ID, groupId);
        bundle.putString(EXTRA_GROUP_ADD_USERNAMES, userNamesJSON.toString());

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);

    }

    public static void syncGetGroups(Context context, String userKey){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putInt(EXTRA_ACTION, ACTION_USER_GET_GROUPS);
        bundle.putString(EXTRA_USER_KEY, userKey);

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);

    }

    public static void syncGroupConfirm(Context context, String userKey, long groupId, boolean confirm){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putInt(EXTRA_ACTION, ACTION_GROUP_CONFIRM);
        bundle.putString(EXTRA_USER_KEY, userKey);
        bundle.putLong(EXTRA_GROUP_ID, groupId);
        bundle.putBoolean(EXTRA_GROUP_CONFIRM, confirm);

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);


    }

    public static void syncCreateList(Context context, long groupId, long listId, String listName){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putInt(EXTRA_ACTION,ACTION_LIST_CREATE);
        bundle.putLong(EXTRA_GROUP_ID, groupId);
        bundle.putString(EXTRA_LIST_NAME, listName);
        bundle.putLong(EXTRA_LIST_ID, listId);

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);

    }

    public static void syncAddItem(Context context, long listId, long itemId){

        //Get the user account key
        Account account = GroupsSyncAccount.getSyncAccount(context);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putInt(EXTRA_ACTION, ACTION_ITEM_ADD);
        bundle.putLong(EXTRA_ITEM_ID, itemId);
        bundle.putLong(EXTRA_LIST_ID, listId);

        //Request to sync the lists
        ContentResolver.requestSync(account, context.getString(R.string.content_authority), bundle);

    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {



        Account account = GroupsSyncAccount.getSyncAccount(context);


        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);


        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ACTION,ACTION_USER_GET_GROUPS);
        bundle.putString(EXTRA_USER_KEY, GroupsSyncAccount.getUserKey(context));

        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(bundle).build();


            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, bundle, syncInterval);
        }
    }


}
