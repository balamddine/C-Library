package com.bassem.donateme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kosalgeek.asynctask.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.id.list;

public class activity_addgroup extends AppCompatActivity implements AsyncResponse {
    SharedPreferences myprefs;
    String userjson;
    JSONObject UserJson;
    public ListView listview;
    public EditText txtGroupName;
    ArrayList<users> arlst = null;
    ArrayList<String> CheckedUsers = new ArrayList<String>();
    public userListAdapter MyuserListAdapter = null;
    public Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);
        context =this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listview = (ListView) findViewById(R.id.lstFriends);
        txtGroupName = (EditText) findViewById(R.id.txtGroupName);
        setTitle("Create new Group");
        GetAllFriends();
        CreateGroup();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void CreateGroup() {
        Button BtnGroupCreate = (Button) findViewById(R.id.BtnGroupCreate);
        BtnGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String str=  TextUtils.join(",", CheckedUsers);
                users us = users.GetCurrentuser(activity_addgroup.this);
                HashMap PostData = new HashMap();
                PostData.put("call", "AddNewGroup");
                PostData.put("AdminID", "" + us.getID());
                PostData.put("AdminName", us.getName());
                PostData.put("usersIDs", str + "," + us.getID());
                PostData.put("Name", txtGroupName.getText().toString());
                BackgroundWorker Worker = new BackgroundWorker(activity_addgroup.this, activity_addgroup.this, PostData);
                Worker.execute(Helper.getPhpHelperUrl());
            }
        });
    }

    private void GetAllFriends() {
        myprefs = this.getSharedPreferences("user", this.MODE_WORLD_READABLE);
        userjson = myprefs.getString("user", null);
        try {
            UserJson = new JSONObject(userjson);
            users.GetFriendList(this, this, UserJson.getString("ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void processFinish(String result) {
        String call = Helper.GetJsonCallResult(result, "groups");
        if (call.equals("AddNewGroup")) {
            Intent intentt = new Intent(this, groups.class);
            startActivity(intentt);
        }
        SharedPreferences myprefs = this.getSharedPreferences("friends", this.MODE_WORLD_READABLE);
        myprefs.edit().putString("friends", result).apply();

        arlst = Helper.GetArrayListFromJsonString(result);
        final LayoutInflater mInflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyuserListAdapter = new userListAdapter(this, R.layout.friendslayout, arlst);
        MyuserListAdapter.setFriendFragmentView(true);
        MyuserListAdapter.ShowCheckBoxes = true;
        if (listview != null) {
            listview.setAdapter(null);
            if (MyuserListAdapter != null) {
                listview.setItemsCanFocus(false);
                listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listview.setAdapter(MyuserListAdapter);

                MyuserListAdapter.notifyDataSetChanged();
                listview.setTextFilterEnabled(true);
                registerForContextMenu(listview);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                        CheckBox ctv = (CheckBox) v.findViewById(R.id.chkuser);
                        if (!ctv.isChecked()) {
                            ctv.setChecked(true);
                            users checkeduser = (users)adapter.getItemAtPosition(position);
                            if(checkeduser!=null){
                                CheckedUsers.add(checkeduser.getID()+"");
                                Toast.makeText(context,"Added "+checkeduser.getName()+" to group.",Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            ctv.setChecked(false);
                        }
                    }
                });
            }

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("activity_addgroup Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
