package com.bassem.donateme.classes;

import android.content.Context;

import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;

import java.util.HashMap;

/**
 * Created by bassem on 7/30/2016.
 */
public class Categories {

    private int ID;
    private String UserID;
    private String Name;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Categories(int ID, String name) {
        this.ID = ID;
        Name = name;
    }

    public Categories(){}

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


    public void AddNewCategory(Context context, com.bassem.donateme.Helpers.AsyncResponse asR)
    {
        HashMap PostData = new HashMap();
        PostData.put("call", "AddNewCategory");
        PostData.put("Name", this.getName());
        PostData.put("UserID", this.getUserID());

        BackgroundWorker registerWorker= new BackgroundWorker(context,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());

    }
    @Override
    public String toString() {
        return Name;
    }
}
