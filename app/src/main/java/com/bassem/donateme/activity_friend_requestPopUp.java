package com.bassem.donateme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;

import java.util.ArrayList;

public class activity_friend_requestPopUp extends AppCompatActivity implements AsyncResponse {
    ArrayList<users> arlst=null;
    ListView lstPopUpFriendsRequest;
    public userListAdapter MyuserListAdapter= null ;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_friend_request_pop_up);
        setTitle("Friends requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetControls();
        ShowProgressDialog();
        SetNotificationsList();
    }

    private void ShowProgressDialog() {
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Loading...");
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    private void GetControls() {
        lstPopUpFriendsRequest=(ListView)findViewById(R.id.lstPopUpFriendsRequest);
    }

    private void SetNotificationsList() {
        Intent myPopUpRequestIntent = getIntent();
        String result=  myPopUpRequestIntent.getStringExtra("jsonResult");
        arlst = Helper.GetArrayListFromJsonString(result);
        if(arlst!=null && arlst.size() >0)
        {
            MyuserListAdapter =new userListAdapter(this, R.layout.friendslayout,arlst);
            MyuserListAdapter.setRequestFragmentView(true);
            if(lstPopUpFriendsRequest!=null)
            {
                lstPopUpFriendsRequest.setAdapter(null);
                lstPopUpFriendsRequest.setAdapter(MyuserListAdapter);
                this.progressDialog.hide();
               // MyuserListAdapter.notifyDataSetChanged();
               // lstPopUpFriendsRequest.setTextFilterEnabled(accept);
            }
        }
        else{
            this.progressDialog.hide();
        }
    }

    @Override
    public void processFinish(String result) {
        String call = Helper.GetJsonCallResult(result,"user");
        String JsonStatus =Helper.GetJsonStatusResult(result,"user");

        if (call.equals("AcceptFriendRequest")) {
            Intent myuserFriendIntent=new Intent(this,UserProfile.class);
          startActivity(myuserFriendIntent);
        }
        //else  if (call.equals("DeclineFriendRequest")) {
        //}

    }
}
