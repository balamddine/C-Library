package com.bassem.donateme;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bassem.donateme.Adapters.categoriesListAdapter;
import com.bassem.donateme.Adapters.groupsListAdapter;
import com.bassem.donateme.Adapters.userListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.Groups;
import com.bassem.donateme.classes.users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.security.AccessController.getContext;

public class groups extends AppCompatActivity implements AsyncResponse, SearchView.OnQueryTextListener {
    public groupsListAdapter MyGroupsListAdapter= null ;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private ListView listview;
    ArrayList<Groups> arlst=null;
    Context context;
    users CurrentUser = new users();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Groups");

        setContentView(R.layout.activity_groups);
        SetElements();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               OpenAddNewGroupIntent();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetUserGroups();

    }

    private void SetElements() {

        listview =(ListView)findViewById(R.id.lstGroups);

    }

    private void GetUserGroups() {
        CurrentUser = users.GetCurrentuser(this);
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserGroups");
        PostData.put("UserID",""+CurrentUser.getID());
        BackgroundWorker Worker = new BackgroundWorker(this, this, PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                GetUserGroups();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    private void OpenAddNewGroupIntent() {
        Intent groupIntent = new Intent(this,activity_addgroup.class);
        startActivity(groupIntent);
    }


    @Override
    public void processFinish(String result) {
        try{
            String call = Helper.GetJsonCallResult(result,"groups");
            String msg = Helper.GetJsonMessageResult(result,"groups");
           /* if(call.equals("RemoveCategory"))
            {
                Toast.makeText(getContext(),msg,Toast.LENGTH_LONG).show();
                ReloadFragment();
                return;
            }*/


            arlst = Helper.GetGroupsArrayListFromJsonString(result);
            final LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            MyGroupsListAdapter =new groupsListAdapter(this, R.layout.grouplayout,arlst);
            if(listview!=null)
            {
                listview.setAdapter(null);
                if (MyGroupsListAdapter!=null)
                {
                    listview.setAdapter(MyGroupsListAdapter);
                    MyGroupsListAdapter.notifyDataSetChanged();
                    listview.setTextFilterEnabled(true);
                    registerForContextMenu(listview);
                    SetListViewClickEvent();

                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //if (v.getId() == R.id.lstGallery) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(arlst.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.grouplisting);
            for (int i = 0; i < menuItems.length; i++) {
                if (menuItems[i].toLowerCase().equals("delete group")) {
                    if (Integer.parseInt(arlst.get(info.position).getAdminID()) == CurrentUser.getID())// default category
                    {
                        menu.add(Menu.NONE, i, i, menuItems[i]);
                    }
                } else {
                    menu.add(Menu.NONE, i, i, menuItems[i]);
                }
            }
        //}
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getTitle().toString()) {
            case "Remove":
                try {
                    HashMap PostData = new HashMap();
                    PostData.put("call", "RemoveGroup");
                    PostData.put("CatID", "" + arlst.get(info.position).getID());
                    PostData.put("UserID", "" + users.GetCurrentuser(this).getID());
                    BackgroundWorker Worker = new BackgroundWorker(this, this, PostData);
                    Worker.execute(Helper.getPhpHelperUrl());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            case "Share":

                return true;
        }
        return true;
    }

    private void SetListViewClickEvent() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent intent = new Intent();
                intent.setClass(context,UserListing.class);
                String GroupID = ""+arlst.get(position).getID();
                String GroupName = ""+arlst.get(position).getName();
                intent.putExtra("GroupID", GroupID);
                intent.putExtra("GroupName", GroupName);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
