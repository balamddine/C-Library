package com.bassem.donateme;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bassem.donateme.Adapters.categoriesListAdapter;
import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_file_sharing_popup extends AppCompatActivity implements AsyncResponse {

    Spinner ddCategories;
    ArrayList<Categories> arlst=null;
    public categoriesListAdapter MyCategoriesListAdapter= null ;
    String InsertedID ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing_popup);
        InsertedID = getIntent().getStringExtra("InsertedID");
        SetControls();
        SetDropDown();
    }

    private void SetControls() {
        ddCategories =(Spinner) findViewById(R.id.ddCategories);
    }

    private void SetDropDown() {
        users CurrentUser = users.GetCurrentuser(this);
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserCategories");
        PostData.put("UserID",""+CurrentUser.getID());
        BackgroundWorker Worker = new BackgroundWorker(this, this, PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }

    @Override
    public void processFinish(String result) {
        String call = Helper.GetJsonCallResult(result,"categories");
        if(call.equals("SaveFileToCategory"))
        {
            String message = Helper.GetJsonMessageResult(result,"categories");
            Toast.makeText(this,message,Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            arlst = Helper.GetCategoriesArrayListFromJsonString(result);
            MyCategoriesListAdapter =new categoriesListAdapter(this, R.layout.simplecategorieslayout,arlst);
            if(ddCategories!=null)
            {
                ddCategories.setAdapter(null);
                if (MyCategoriesListAdapter!=null)
                {
                    MyCategoriesListAdapter.setDropDownViewResource(R.layout.simplecategorieslayout);
                    ddCategories.setAdapter(MyCategoriesListAdapter);

                }
            }
        }

    }


    public void btn_saveToCategoryClick(View view) {
        Categories Ct = (Categories)ddCategories.getSelectedItem();
        HashMap PostData = new HashMap();
        PostData.put("call", "SaveFileToCategory");
        PostData.put("ID",""+InsertedID);
        PostData.put("CategoryID",""+Ct.getID());
        BackgroundWorker Worker = new BackgroundWorker(this, this, PostData);
        Worker.execute(Helper.getPhpHelperUrl());

    }

    public void btn_cancelCategoryClick(View view) {
        finish();
    }
}
