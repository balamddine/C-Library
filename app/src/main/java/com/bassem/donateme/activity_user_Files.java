package com.bassem.donateme;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.bassem.donateme.Adapters.filesListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.files;
import com.bassem.donateme.classes.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class activity_user_Files extends AppCompatActivity implements AsyncResponse,SearchView.OnQueryTextListener {
    public ListView listview;
    ArrayList<files> arlst=null;
    public filesListAdapter MyFilesListAdapter= null ;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    String CategoryID ="";
    String CategoryName ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_activity_user__files);
        CategoryName = getIntent().getStringExtra("CatName");
        CategoryID = getIntent().getStringExtra("CatID");
        setTitle(CategoryName + "-Files");
        listview =(ListView)findViewById(R.id.lstFiles);
        GetUserFiles();
    }

    private void GetUserFiles() {
        users CurrentUser = users.GetCurrentuser(this);
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserFiles");
        PostData.put("UserID",""+CurrentUser.getID());
        PostData.put("CatID",""+CategoryID);
        BackgroundWorker Worker = new BackgroundWorker(this,this,PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
       // super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_gallery_fragment_menu, menu);
        SetSearchMenuItem(menu);
    }
    private void SetSearchMenuItem(Menu menu) {
        SearchManager searchManager = (SearchManager)this.getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.searchusr);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        searchView.setOnQueryTextListener(this);
    }
    @Override
    public void processFinish(String result) {
        try{
            String call = Helper.GetJsonCallResult(result,"files");
            String msg = Helper.GetJsonMessageResult(result,"files");
            if(call.equals("RemoveFile"))
            {
                Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
                ReloadActivity();
                return;
            }

            SharedPreferences myprefs = this.getSharedPreferences("userFiles",this.MODE_WORLD_READABLE);
            myprefs.edit().putString("userFiles",result).apply();
            arlst = Helper.GetFilesArrayListFromJsonString(result);
            final LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            MyFilesListAdapter =new filesListAdapter(this, R.layout.fileslayout,arlst,false,false);
            if(listview!=null)
            {
                listview.setAdapter(null);
                if (MyFilesListAdapter!=null)
                {
                    listview.setAdapter(MyFilesListAdapter);
                    MyFilesListAdapter.notifyDataSetChanged();
                    listview.setTextFilterEnabled(true);
                    registerForContextMenu(listview);
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private void ReloadActivity() {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        MyFilesListAdapter.getFilter().filter(newText.toString().toLowerCase(Locale.getDefault()));
        return true;
    }
}
