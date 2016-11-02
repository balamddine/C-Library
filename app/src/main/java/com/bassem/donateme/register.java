package com.bassem.donateme;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class register extends AppCompatActivity implements AsyncResponse {
    EditText txtEmail,txtPassword,txtName;
    JSONArray userJSON= null;
    users user=null;
    String Notitoken;
    SharedPreferences notificatoinpref;
    AlertDialog alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
     //   Helper.CheckInternetConnection(this);
        setTitle("Register");
        SetElements();
        GetNotificationToken();


    }
    private void SetElements() {
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtName=(EditText)findViewById(R.id.txtName);
        alertdialog = new AlertDialog.Builder(this).create();

    }
    public void btnRegisterDBClick(View view)
    {
        if (!Validate())
        {
            Helper.Alert(alertdialog,"Error","All Fields are Required");
            return;
        }

        Intent myIntent=new Intent(this, UserProfile.class);

        user = new users();
        user.setName(txtName.getText().toString());
        user.setEmail(txtEmail.getText().toString());
        user.setPassword(txtPassword.getText().toString());
        user.setProfession("");
        user.setImage("");
        user.setUserNotificationToken(Notitoken);
        user.Register(this,this);

    }
    private void GetNotificationToken() {
        Intent Defaultintent = getIntent();
        if(Defaultintent!=null && Defaultintent.hasExtra("token"))
        {
            Notitoken =  Defaultintent.getStringExtra("token");
        }
    }
    private Boolean Validate() {
        if (txtName.getText().length()==0)
        {
            txtName.setError("Email required");
            return false;
        }
        if (txtEmail.getText().length()==0)
        {
            txtEmail.setError("Email required");
            return false;
        }else{
           if(!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText()).matches())
           {
               txtEmail.setError("Invalid Email address");
               return false;
           }

        }
        if (txtPassword.getText().length()==0)
        {
            txtPassword.setError("Password required");
            return false;
        }
        return true;
    }

    @Override
    public void processFinish(String result) {
        if (result != null) {
            try {
                JSONObject jsonObj = new JSONObject(result.toString());
                Intent myIntent=new Intent(this, Login.class);
                // Getting JSON Array node
                userJSON = jsonObj.getJSONArray("user");
                JSONObject obj = userJSON.getJSONObject(0);
                String status =obj.getString("status");
                if (status.equals("1")) {
                    myIntent.putExtra("email",txtEmail.getText().toString());
                    myIntent.putExtra("password",txtPassword.getText().toString());
                    this.startActivity(myIntent);
                }
                else{
                    Helper.Alert(alertdialog,"Error", obj.getString("message").toString());
                }

                } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
