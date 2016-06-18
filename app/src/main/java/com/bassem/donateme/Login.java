package com.bassem.donateme;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.google.android.gms.*;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Login extends AppCompatActivity implements AsyncResponse {
    EditText txtEmail,txtPassword;

    AlertDialog alertdialog;
    LinearLayout LoginBox ;
    GoogleSignInOptions gso;
    SignInButton GsignInButton;
    GoogleApiClient mGoogleApiClient;
    JSONArray userJSON= null;
    private final int RC_SIGN_IN = 9002;
    public static GoogleSignInAccount acct;
   ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(Helper.getApplicationName(this)+" - Login ");
        checkIfPreferencesExist();
        SetElements();
        SignInWithGoogle();
    }

    @Override
    protected void onResume() {
        checkIfPreferencesExist();
        super.onResume();
    }

    private void checkIfPreferencesExist() {
        SharedPreferences   myprefs =this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String value = myprefs.getString("user",null);
        if(value!=null)
        {
            Intent myIntent=new Intent(this, UserProfile.class);
            this.startActivity(myIntent);
        }

    }


    private void SetElements() {
        txtEmail= (EditText)findViewById(R.id.txtEmail);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        alertdialog = new AlertDialog.Builder(this).create();
        LoginBox =(LinearLayout)findViewById(R.id.LoginBox);
        GsignInButton = (SignInButton) findViewById(R.id.btnGoogleSignIn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    private void SignInWithGoogle() {
        GsignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient != null) {
                    mGoogleApiClient.disconnect();
                }
                Alert("Signing in ","Signing in to google");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                mGoogleApiClient.connect();
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                acct = result.getSignInAccount();
                users user = new users();
                user.setEmail(acct.getEmail());
                user.setPassword("");
                user.SignIn(this,this,true);
                alertdialog.hide();
                mGoogleApiClient.disconnect();
            } else {
                Alert("Error","Can't sign in with google");
            }
        }
    }



    public void Login_Click(View view)
    {
        Boolean valid = Validate();
        String email =txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        if (valid) {
            users user = new users();
            user.setEmail(email);
            user.setPassword(password);
            user.SignIn(this,this,false);
        }
    }

    private Boolean Validate() {
        if (txtEmail.getText().length()==0)
        {
            txtEmail.setError("Email required");
            return false;
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
                // Getting JSON Array node
                userJSON = jsonObj.getJSONArray("user");
                JSONObject obj = userJSON.getJSONObject(0);
                String CallFunction = obj.getString("call");
                 switch(CallFunction) {
                    case "login": LoginToApp(obj);break;
                    case "GetUser": LoginToAppViaGoogle(obj); break;
                     case "register" :registeruser(obj);break;
                    default:break;
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void registeruser(JSONObject obj) {
       /* try {
            if (obj.getString("status").equals("1")) {

            }
            else {
                Alert("Error", obj.getString("message").toString());
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }*/
    }

    private void LoginToAppViaGoogle(JSONObject obj) {

        try {

            String status =obj.getString("status");
            Intent myIntent=new Intent(this, UserProfile.class);
            users user=null;
            if (status.equals("1")) {

                String name = obj.getString("Name");
                String email = obj.getString("Email");
                String password = obj.getString("Password");
                String image = acct.getPhotoUrl().toString();
                int ID =Integer.parseInt(obj.getString("ID"));

                user = new users(ID,name,email,password,image,"");
                myIntent.putExtra("user",user.toJSON());

            }
            else{
                user = new users();
                user.setName(acct.getDisplayName());
                user.setEmail(acct.getEmail());
                user.setPassword("123123");
                user.setImage(acct.getPhotoUrl().toString());
                user.Register(this,this);
            }
            SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            myprefs.edit().putString("user",obj.toString()).apply();
            this.startActivity(myIntent);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void LoginToApp(JSONObject obj) {
        try {
        if (obj.getString("status").equals("1")) {
            Intent myIntent = new Intent(this, UserProfile.class);
            SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            myprefs.edit().putString("user",obj.toString()).apply();
            int ID =Integer.parseInt(obj.getString("ID"));
            String name = obj.getString("Name");
            String email = obj.getString("Email");
            String password = obj.getString("Password");
            String image = obj.getString("Image");
            String Profession = obj.getString("Profession");
            users user = new users(ID,name,email,password,image,Profession);
            myIntent.putExtra("user",user.toJSON());
            this.startActivity(myIntent);
        } else {
            Alert("Error", obj.getString("message").toString());
        }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void Alert(String Title, String message) {
            alertdialog.setTitle(Title);
            alertdialog.setMessage(message);
            alertdialog.show();
    }

    public void btnRegister_Click(View view) {
        Intent RegisterIntent = new Intent(this, register.class);
        this.startActivity(RegisterIntent);
    }
    @Override
    protected  void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

}
