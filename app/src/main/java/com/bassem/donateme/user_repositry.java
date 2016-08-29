package com.bassem.donateme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class user_repositry extends AppCompatActivity implements AsyncResponse {
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
        setContentView(R.layout.activity_user_repositry);
        setTitle("My Received files");
        listview =(ListView)findViewById(R.id.userrepository);
        GetRepository();
    }

    private void GetRepository() {
        users CurrentUser = users.GetCurrentuser(this);
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserRepository");
        PostData.put("UserID",""+CurrentUser.getID());
        BackgroundWorker Worker = new BackgroundWorker(this,this,PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }


    @Override
    public void processFinish(String result) {
        try{
          //  String call = Helper.GetJsonCallResult(result,"files");
           // String msg = Helper.GetJsonMessageResult(result,"files");
            //if(call.equals("RemoveFile"))
            //{
            //    Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            //    ReloadActivity();
            //    return;
            //}

            SharedPreferences myprefs = this.getSharedPreferences("userFiles",this.MODE_WORLD_READABLE);
            myprefs.edit().putString("userFiles",result).apply();
            arlst = Helper.GetFilesArrayListFromJsonString(result);
            final LayoutInflater mInflater = (LayoutInflater)this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            MyFilesListAdapter =new filesListAdapter(this, R.layout.filerepositorylayout,arlst,true,true);
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
}
