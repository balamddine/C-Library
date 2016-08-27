package com.bassem.donateme;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.*;
import com.bassem.donateme.classes.users;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class UserListing extends AppCompatActivity implements com.bassem.donateme.Helpers.AsyncResponse,SearchView.OnQueryTextListener {
    public ListView listview;
    public ArrayList<users> arlst=null;
    public userListAdapter MyuserListAdapter= null ;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    SharedPreferences myprefs;
    String userjson;
    JSONObject UserJson;
    ImageLoader imageLoader;
    LinearLayout usrlistinglayoutNoDataFound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Helper.CheckInternetConnection(this);
        setTitle("Discover people");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_listing);
        SetControls();
        GetUsersList(true);
    }

    private void SetControls() {
        listview =(ListView) findViewById(R.id.userlsting);
        usrlistinglayoutNoDataFound =(LinearLayout) findViewById(R.id.usrlistinglayoutNoDataFound);
    }

    private void GetUsersList(boolean showloading) {
        try {
            myprefs =this.getSharedPreferences("user", this.MODE_WORLD_READABLE);
            userjson = myprefs.getString("user",null);
            UserJson =new JSONObject(userjson);
            HashMap PostData = new HashMap();
            PostData.put("call", "GetAllUserExceptID");
            PostData.put("ID", UserJson.getString("ID"));

            BackgroundWorker Worker= new BackgroundWorker(this,this,PostData,showloading);
            Worker.execute(Helper.getPhpHelperUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.searchitem);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                GetUsersList(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void processFinish(String result) {

        if (result != null) {
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                // Getting JSON Array node
                JSONArray userJSON = jsonObj.getJSONArray("user");
                JSONObject obj = userJSON.getJSONObject(0);
                String CallFunction = obj.getString("call");
                switch(CallFunction) {
                    case "AddAsAFriend":ModifyFriendListProcessFnish(result);break;
                    case "CancelFriendRequest": ModifyFriendListProcessFnish(result);break;
                    case "GetAllUserExceptID": BindListViewAddapterProcessFinish(result); break;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void ModifyFriendListProcessFnish(String result) {
        GetUsersList(false);
    }

    private void BindListViewAddapterProcessFinish(String result) {

        String JsonStatus =Helper.GetJsonStatusResult(result,"user");
        if(JsonStatus.equals("0"))
        {
            usrlistinglayoutNoDataFound.setVisibility(View.VISIBLE);
            return;
        }
        usrlistinglayoutNoDataFound.setVisibility(View.INVISIBLE);

        listview.setAdapter(null);
        arlst = Helper.GetArrayListFromJsonString(result);
        final LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        MyuserListAdapter =new userListAdapter(this, R.layout.friendslayout,arlst );
        if(listview!=null)
        {
            listview.setAdapter(MyuserListAdapter);
            MyuserListAdapter.notifyDataSetChanged();
            listview.setTextFilterEnabled(true);
            registerForContextMenu(listview);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.userlsting) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle(arlst.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.userlisting);
            for (int i = 0; i < menuItems.length; i++)
                menu.add(Menu.NONE, i, i, menuItems[i]);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getTitle().toString()) {
            case "Share":
                // add stuff here
                return true;
            case "View Profile":
                Intent FriendProfileIntent = new Intent(this,editprofile.class);
                FriendProfileIntent.putExtra("FriendEmail",arlst.get(info.position).getEmail());
                startActivity(FriendProfileIntent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MyuserListAdapter.getFilter().filter(newText.toString().toLowerCase(Locale.getDefault()));
       return true;
    }
}
