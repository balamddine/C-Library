package com.bassem.donateme;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements AsyncResponse,GoogleApiClient.OnConnectionFailedListener {
    EditText txtEmail,txtPassword;

    AlertDialog alertdialog;
    LinearLayout LoginBox ;
    GoogleSignInOptions gso;
    SignInButton GsignInButton;
    GoogleApiClient mGoogleApiClient;
    JSONArray userJSON= null;
    private final int RC_SIGN_IN = 9002;
    public static GoogleSignInAccount acct;
    String Notitoken;
    SharedPreferences notificatoinpref;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        GetNotificationToken();
        SetElements();
        SignInWithGoogle();
        GetRegisterIntentValues();
    }

    private void GetRegisterIntentValues() {
        Intent Registerintent = getIntent();
        if(Registerintent!=null && Registerintent.hasExtra("email"))
        {
            Toast.makeText(this,"you have successfully registered",Toast.LENGTH_LONG).show();
            txtEmail.setText(Registerintent.getStringExtra("email"));
            txtPassword.setText(Registerintent.getStringExtra("password"));
        }
    }

    private void GetNotificationToken() {
       /*  notificatoinpref =this.getSharedPreferences("notificatointoken", MODE_WORLD_READABLE);
        Notitoken = notificatoinpref.getString("notificatointoken",null);*/
        Intent Defaultintent = getIntent();
        if(Defaultintent!=null && Defaultintent.hasExtra("token"))
        {
            Notitoken =  Defaultintent.getStringExtra("token");
        }
    }



    public void ShowProgressBar(String message)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
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
              //  ShowProgressBar("Signing in to google");
                //Alert("Signing in ","Signing in to google");
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
                user.setName(acct.getDisplayName());
                if (acct.getPhotoUrl()!=null)
                {
                    user.setImage(acct.getPhotoUrl().toString());
                }
                else{
                    user.setImage("");
                }
                user.setPassword(Helper.generateUUID());
                user.setUserNotificationToken(Notitoken);
                user.SignIn(this,this,true);

                mGoogleApiClient.disconnect();
            } else {
                Toast.makeText(getApplicationContext(),"Can't sign in with google Reason \n" + result.getStatus(), Toast.LENGTH_LONG).show();
              //  Alert("Error","Can't sign in with google Reason \n" + result.getStatus());
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
            user.setUserNotificationToken(Notitoken);
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
                JSONObject jsonObj = null;
                jsonObj = new JSONObject(result.toString());
                JSONArray userJSON = jsonObj.getJSONArray("user");
                JSONObject obj = userJSON.getJSONObject(0);
              String CallFunction =obj.getString("call");
                 switch(CallFunction) {
                    case "login": LoginToApp(obj);break;
                    case "GetUser": LoginToAppViaGoogle(obj); break;
                    default:break;
                }
                this.finish();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
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
                String image =obj.getString("Image");// acct.getPhotoUrl().toString();
                int ID =Integer.parseInt(obj.getString("ID"));
                user = new users(ID,name,email,password,image,"");
                user.setUserNotificationToken(Notitoken);
            }

            SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            myprefs.edit().putString("user",user.toJSON()).apply();
            this.startActivity(myIntent);
        } catch (JSONException ex) {
            Alert("Error ","Signing in to google Failed");
            Log.d("Google sign in error",ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void LoginToApp(JSONObject obj) {
        try {
        if (obj.getString("status").equals("1")) {
            Intent myIntent = new Intent(this, UserProfile.class);
            int ID =Integer.parseInt(obj.getString("ID"));
            String name = obj.getString("Name");
            String email = obj.getString("Email");
            String password = obj.getString("Password");
            String image = obj.getString("Image");
            String Profession = obj.getString("Profession");
            users user = new users(ID,name,email,password,image,Profession);
            user.setUserNotificationToken(Notitoken);
            SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            myprefs.edit().putString("user",user.toJSON()).apply();

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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Google Error",connectionResult.getErrorMessage());
        Alert("Error", connectionResult.toString());
    }
}
