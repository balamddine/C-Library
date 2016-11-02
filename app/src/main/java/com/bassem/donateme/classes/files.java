package com.bassem.donateme.classes;

import android.content.Context;

import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;

import java.util.HashMap;

/**
 * Created by bassem on 7/30/2016.
 */
public class files {

    private int ID;

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    private String UserID;
    private String CategoryID;
    private String Name;

    public String getSharedWithUserName() {
        return SharedWithUserName;
    }

    public void setSharedWithUserName(String sharedWithUserName) {
        SharedWithUserName = sharedWithUserName;
    }

    private String SharedWithUserName;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public files(int ID, String name) {
        this.ID = ID;
        Name = name;
    }

    public files(){}

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public void AddNewFile(Context context, com.bassem.donateme.Helpers.AsyncResponse asR)
    {
        HashMap PostData = new HashMap();
        PostData.put("call", "AddNewFile");
        PostData.put("Name", this.getName());
        PostData.put("UserID", this.getUserID());
        PostData.put("CatID", this.getCategoryID());

        BackgroundWorker registerWorker= new BackgroundWorker(context,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());

    }
}
