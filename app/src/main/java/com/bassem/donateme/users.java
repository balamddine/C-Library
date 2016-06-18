package com.bassem.donateme;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kosalgeek.asynctask.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by bassem on 6/12/2016.
 */
public class users {
    private int ID;
    private String Name;
    private String Email;
    private String Password;
    private String Image;
    private String Profession;
    public users(){}
    public users(int ID, String name,String Email, String password, String image,String Profession ) {
        this.ID = ID;
        this.Name = name;
        this.Email = Email;
        this.Password = password;
        this.Image = image;
        this.Profession=Profession;
    }

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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getProfession() {
        return Profession;
    }

    public void setProfession(String profession) {
        Profession = profession;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", getID());
            jsonObject.put("Name", getName());
            jsonObject.put("Email", getEmail());
            jsonObject.put("Password", getPassword());
            jsonObject.put("Image", getImage());
            jsonObject.put("Profession", getProfession());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    public  void SignIn(Context ctx, AsyncResponse asR,boolean viaGoogle){
        HashMap PostData = new HashMap();
        PostData.put("call", (viaGoogle==false?"login":"GetUser"));
        PostData.put("email", this.Email);
        PostData.put("password", this.Password);
        BackgroundWorker LoginWorker= new BackgroundWorker(ctx,asR,PostData);
        LoginWorker.execute(Helper.getPhpHelperUrl());
    }

    public  void Register(Context ctx, AsyncResponse asR) {
        HashMap PostData = new HashMap();
        PostData.put("call", "register");
        PostData.put("name", this.Name);
        PostData.put("email", this.Email);
        PostData.put("password", this.Password);
        PostData.put("image", this.Image);
        PostData.put("profession", this.Profession);

        BackgroundWorker registerWorker= new BackgroundWorker(ctx,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    public  void Logout() {

    }
}
