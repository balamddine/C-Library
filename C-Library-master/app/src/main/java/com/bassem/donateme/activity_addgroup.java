package com.bassem.donateme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.kosalgeek.asynctask.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_addgroup extends AppCompatActivity implements AsyncResponse {
    SharedPreferences myprefs;
    String userjson;
    JSONObject UserJson;
    public ListView listview;
    public EditText txtGroupName;
    ArrayList<users> arlst=null;
    ArrayList<users> CheckedUsers=new ArrayList<users>();
    public userListAdapter MyuserListAdapter= null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listview =(ListView)findViewById(R.id.lstFriends);
        txtGroupName =(EditText)findViewById(R.id.txtGroupName);
        setTitle("Create new Group");
        GetAllFriends();
        CreateGroup();
    }

    private void CreateGroup() {
        Button BtnGroupCreate =(Button) findViewById(R.id.BtnGroupCreate);
        BtnGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str="";
               for(int i=0;i<CheckedUsers.size();i++)
               {
                   str+=CheckedUsers.get(i).getID()+",";
               }
              str =str.substring(0,str.length()-1);
                users us = users.GetCurrentuser(activity_addgroup.this);
                HashMap PostData = new HashMap();
                PostData.put("call", "AddNewGroup");
                PostData.put("AdminID", ""+us.getID());
                PostData.put("AdminName", us.getName());
                PostData.put("usersIDs", str+","+us.getID());
                PostData.put("Name", txtGroupName.getText().toString());
                BackgroundWorker Worker= new BackgroundWorker(activity_addgroup.this,activity_addgroup.this,PostData);
                Worker.execute(Helper.getPhpHelperUrl());
            }
        });
    }

    private void GetAllFriends() {
        myprefs =this.getSharedPreferences("user", this.MODE_WORLD_READABLE);
        userjson = myprefs.getString("user",null);
        try {
            UserJson=new JSONObject(userjson);
            users.GetFriendList(this,this,UserJson.getString("ID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void processFinish(String result) {
        String call = Helper.GetJsonCallResult(result,"groups");
        if(call.equals("AddNewGroup"))
        {
            Intent intentt =  new Intent(this,groups.class);
            startActivity(intentt);
        }
        SharedPreferences myprefs = this.getSharedPreferences("friends",this.MODE_WORLD_READABLE);
        myprefs.edit().putString("friends",result).apply();

        arlst = Helper.GetArrayListFromJsonString(result);
        final LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyuserListAdapter =new userListAdapter(this, R.layout.friendslayout,arlst);
        MyuserListAdapter.setFriendFragmentView(true);
        MyuserListAdapter.ShowCheckBoxes=true;
        if(listview!=null)
        {
            listview.setAdapter(null);
            if (MyuserListAdapter!=null)
            {
                listview.setItemsCanFocus(false);
                listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listview.setAdapter(MyuserListAdapter);

                MyuserListAdapter.notifyDataSetChanged();
                listview.setTextFilterEnabled(true);
                registerForContextMenu(listview);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                        CheckBox ctv = (CheckBox)v.findViewById(R.id.chkuser);
                        if(ctv.isChecked()){
                            ctv.setChecked(false);
                        }else{
                            ctv.setChecked(true);
                            CheckedUsers.add(arlst.get(position));
                        }
                    }
                });
            }

        }
    }
}
