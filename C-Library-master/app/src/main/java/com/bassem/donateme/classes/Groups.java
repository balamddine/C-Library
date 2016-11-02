package com.bassem.donateme.classes;

import android.content.Context;

import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;

import java.util.HashMap;

/**
 * Created by bassem on 7/30/2016.
 */
public class Groups {

    private int ID;
    private String AdminID;
    private String AdminName;
    private String usersIDs;

    public String getAdminName() {
        return AdminName;
    }

    public void setAdminName(String adminName) {
        AdminName = adminName;
    }

    private String Name;

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

    public String getUsersIDs() {
        return usersIDs;
    }

    public void setUsersIDs(String usersIDs) {
        this.usersIDs = usersIDs;
    }

    public String getAdminID() {
        return AdminID;
    }

    public void setAdminID(String adminID) {
        AdminID = adminID;
    }

    @Override
    public String toString() {
        return Name;
    }
}
