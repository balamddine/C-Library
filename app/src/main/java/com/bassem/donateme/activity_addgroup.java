package com.bassem.donateme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ListView;

import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class activity_addgroup extends AppCompatActivity implements AsyncResponse {
    SharedPreferences myprefs;
    String userjson;
    JSONObject UserJson;
    public ListView listview;
    ArrayList<users> arlst=null;
    public userListAdapter MyuserListAdapter= null ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addgroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listview =(ListView)findViewById(R.id.lstFriends);
        setTitle("Create new Group");
        GetAllFriends();

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
                listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listview.setItemsCanFocus(false);
                listview.setAdapter(MyuserListAdapter);

                MyuserListAdapter.notifyDataSetChanged();
                listview.setTextFilterEnabled(true);
                registerForContextMenu(listview);
            }

        }
    }
}
