package com.bassem.donateme.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.bassem.donateme.Helpers.*;
import com.bassem.donateme.Login;

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
    private String Accepted;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFBuserid() {
        return FBuserid;
    }

    public void setFBuserid(String FBuserid) {
        this.FBuserid = FBuserid;
    }

    private String FBuserid;
    private String UserNotificationToken;
    public users(){}
    public users(int ID, String name,String Email, String password, String image,String Profession ) {
        this.ID = ID;
        this.Name = name;
        this.Email = Email;
        this.Password = password;
        this.Image = image;
        this.Profession=Profession;
    }

    public String getUserNotificationToken() {
        return UserNotificationToken;
    }

    public void setUserNotificationToken(String userNotificationToken) {
        UserNotificationToken = userNotificationToken;
    }

    public void setAccepted(String accepted) { Accepted = accepted; }

    public String getAccepted() { return Accepted; }

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
            jsonObject.put("UserNotificationToken", getUserNotificationToken());
            jsonObject.put("Accepted", getAccepted());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    public  void SignIn(Context ctx, com.bassem.donateme.Helpers.AsyncResponse asR, boolean SSO){
        HashMap PostData = new HashMap();
        PostData.put("call", (SSO==false?"login":"GetUser"));
        PostData.put("email", (this.FBuserid!=null && !this.FBuserid.equals("")?"":this.Email));
        PostData.put("password", this.Password);
        PostData.put("notificationtocken", this.UserNotificationToken);
        if(SSO)
        {
            if(this.FBuserid!=null && !this.FBuserid.equals(""))
            {
                PostData.put("FBuserID", this.FBuserid);
            }
            else{
                PostData.put("FBuserID", "");
            }
        }
        PostData.put("name", this.Name);
        PostData.put("image", this.Image);
        PostData.put("profession", "");
        BackgroundWorker LoginWorker= new BackgroundWorker(ctx,asR,PostData);
        LoginWorker.setLoadingMessage("Signing in ");
        LoginWorker.execute(Helper.getPhpHelperUrl());
    }
    public static void GetFriendList(Context ctx, com.bassem.donateme.Helpers.AsyncResponse asR, String UserID ){
        HashMap PostData = new HashMap();
        PostData.put("call", "GetUserFriend");
        PostData.put("ID", UserID);
        BackgroundWorker Worker= new BackgroundWorker(ctx,asR,PostData);
        Worker.execute(Helper.getPhpHelperUrl());
    }

    public  void Register(Context ctx, com.bassem.donateme.Helpers.AsyncResponse asR) {
        HashMap PostData = new HashMap();
        PostData.put("call", "register");
        PostData.put("name", this.Name);
        PostData.put("email", this.Email);
        PostData.put("password", this.Password);
        PostData.put("notificationtocken", this.UserNotificationToken);

        BackgroundWorker registerWorker= new BackgroundWorker(ctx,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    @Override
    public String toString() {
      return super.toString();
    }

    public  void Logout() {

    }

    public void GetUserFromJson(JSONObject userjsonStr ) throws JSONException {
        this.ID = userjsonStr.getInt("ID");
        this.Name = userjsonStr.getString("Name");
        this.Email = userjsonStr.getString("Email");
        this.Password = userjsonStr.getString("Password");
        this.Profession = userjsonStr.getString("Profession");
        if (userjsonStr.has("Image")==true) {
            this.Image = userjsonStr.getString("Image");
        }
        if (userjsonStr.has("Accepted")==true) {
            this.Accepted = userjsonStr.getString("Accepted");
        }
        if (userjsonStr.has("UserNotificationToken")==true) {
            this.UserNotificationToken = userjsonStr.getString("UserNotificationToken");
        }
    }

    public void Modify(Context ctx, com.bassem.donateme.Helpers.AsyncResponse asR) {
        HashMap PostData = new HashMap();
        PostData.put("call", "Editprofile");
        PostData.put("name", this.Name);
        PostData.put("email", this.Email);
        PostData.put("profession", this.Profession);
        BackgroundWorker registerWorker= new BackgroundWorker(ctx,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    public static void GetAllFriendRequestsNotifications(Context context,com.bassem.donateme.Helpers.AsyncResponse asR, String id) {
        HashMap PostData = new HashMap();
        PostData.put("call", "GetFriendsRequest");
        PostData.put("ID", id);

        BackgroundWorker registerWorker= new BackgroundWorker(context,asR,PostData);
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    public static users GetCurrentuser(Context context) {
        users u = new users();
        try {
            String Cuserjson;
            JSONObject CUserJson;
            SharedPreferences myCprefs;

            myCprefs = context.getSharedPreferences("user", context.MODE_WORLD_READABLE);
            if(myCprefs!=null)
            {
                Cuserjson = myCprefs.getString("user", null);
                if(Cuserjson !=null) {
                    CUserJson = new JSONObject(Cuserjson);
                    if (CUserJson != null) {
                        u.setID(CUserJson.getInt("ID"));
                        u.setName(CUserJson.getString("Name"));
                        u.setEmail(CUserJson.getString("Email"));
                        u.setPassword(CUserJson.getString("Password"));
                        u.setProfession(CUserJson.getString("Profession"));
                        if (CUserJson.has("Image") == true) {
                            u.setImage(CUserJson.getString("Image"));
                        }
                        if (CUserJson.has("Accepted") == true) {
                            u.setAccepted(CUserJson.getString("Accepted"));
                        }
                        if (CUserJson.has("UserNotificationToken") == true) {
                            u.setUserNotificationToken(CUserJson.getString("UserNotificationToken"));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }


}
