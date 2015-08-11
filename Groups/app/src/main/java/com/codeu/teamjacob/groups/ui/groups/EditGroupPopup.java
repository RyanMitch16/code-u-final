package com.codeu.teamjacob.groups.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codeu.teamjacob.groups.R;
import com.codeu.teamjacob.groups.database.GroupDatabase;
import com.codeu.teamjacob.groups.database.GroupEntry;
import com.codeu.teamjacob.groups.sync.GroupsSyncAccount;
import com.codeu.teamjacob.groups.sync.GroupsSyncAdapter;
import com.codeu.teamjacob.groups.ui.Utility;
import com.codeu.teamjacob.groups.ui.popups.PopupActivity;

public class EditGroupPopup extends PopupActivity {

    public static final String EXTRA_GROUP_ID = "EXTRA_GROUP_ID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_edit_group);

        int width = convertWidthPercentToPixels(0.8f);
        int height = convertHeightPercentToPixels(0.6f);

        final EditGroupPopup thisActivity = this;

        getWindow().setLayout(width, height);

        Intent launchIntent = getIntent();

        final long groupId;
        if (launchIntent.hasExtra(EXTRA_GROUP_ID)){
            groupId = launchIntent.getLongExtra(EXTRA_GROUP_ID, -1);
            Utility.setGroupId(this, groupId);
        } else {
            groupId = Utility.getGroupId(this);
        }

        final GroupEntry groupEntry = GroupDatabase.getById(this, groupId);

        //Get the views
        TextView groupName = (TextView) findViewById(R.id.popup_group_name);
        groupName.setText(groupEntry.groupName);

        LinearLayout addUsers = (LinearLayout) findViewById(R.id.add_user);
        addUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, AddGroupUserPopup.class);
                intent.putExtra(AddGroupUserPopup.EXTA_GROUP_ID, groupId);
                startActivity(intent);
                finish();

            }
        });

        LinearLayout leaveGroup = (LinearLayout) findViewById(R.id.leave_group);
        leaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupsSyncAdapter.syncGroupLeave(thisActivity,
                        GroupsSyncAccount.getUserKey(thisActivity), groupId);

                groupEntry.removed = true;
                GroupDatabase.put(thisActivity, groupEntry);

                finish();

            }
        });




        //btnCreate = (TextView) findViewById(R.id.create_button);

    }
}