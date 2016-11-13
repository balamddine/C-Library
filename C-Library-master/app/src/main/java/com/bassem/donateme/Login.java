package com.bassem.donateme;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.facebook.FacebookSdk;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class Login extends AppCompatActivity implements AsyncResponse,GoogleApiClient.OnConnectionFailedListener {
    EditText txtEmail,txtPassword;
Context context;
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
    private CallbackManager callbackManager;
    private LoginButton FbloginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        GetNotificationToken();
        getfaceboookkeyhash();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        setTitle("Login");

        SetElements();
        setFacebook();
        SignInWithGoogle();
        GetRegisterIntentValues();
    }

    private void getfaceboookkeyhash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bassem.donateme",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Facebook KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

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
    private void setFacebook() {
        FbloginButton = (LoginButton)findViewById(R.id.Fblogin_button);
        FbloginButton.setReadPermissions(Arrays.asList("public_profile","email"));

        callbackManager = CallbackManager.Factory.create();
        FbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userID = loginResult.getAccessToken().getUserId() ;
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String userid = AccessToken.getCurrentAccessToken().getUserId();
                                    String name = object.getString("name");
                                    JSONObject imageObj = object.getJSONObject("picture");
                                    JSONObject imageUrlobj = imageObj.getJSONObject("data");
                                    String imageURL =imageUrlobj.getString("url"); //"http://graph.facebook.com/" + userid + "/picture?type=large&redirect=false";
                                    users user = new users();
                                    user.setFBuserid(userid);
                                    user.setName(name);
                                    if (imageURL !=null)
                                    {
                                        user.setImage(imageURL.toString());
                                    }
                                    else{
                                        user.setImage("");
                                    }
                                    user.setPassword(Helper.generateUUID());
                                    user.setUserNotificationToken(Notitoken);
                                    user.SignIn(Login.this,Login.this,true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(context,"Error : "+e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GetRegisterIntentValues() {
        Intent Registerintent = getIntent();
        if(Registerintent!=null && Registerintent.hasExtra("email"))
        {
            Toast.makeText(this,"you have successfully registered",Toast.LENGTH_LONG).show();
            String email = Registerintent.getStringExtra("email");
            String password = Registerintent.getStringExtra("password");
            txtEmail.setText(email);
            txtPassword.setText(password);
            LogintoDB(email,password);

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
        else
        {
            Notitoken ="";
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

        // for facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
            LogintoDB(email,password);
        }
    }

    private void LogintoDB(String email, String password) {
        users user = new users();
        user.setEmail(email);
        user.setImage("");
        user.setName("");
        user.setPassword(password);
        user.setFBuserid("");
        user.setUserNotificationToken(Notitoken);
        user.SignIn(this,this,false);
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
                    case "GetUser": LoginToAppViaSSO(obj); break;
                    default:break;
                }
                this.finish();
            } catch (JSONException ex) {
                Toast.makeText(this,"An error has occured while trying to login ",Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
    }




    private void LoginToAppViaSSO(JSONObject obj) {

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
            Toast.makeText(this,"Signing in to google Failed",Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,obj.getString("message").toString(),Toast.LENGTH_LONG).show();

        }
        } catch (JSONException ex) {
            Toast.makeText(this,"error : "+ex.getMessage(),Toast.LENGTH_LONG).show();
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
        Toast.makeText(this,"Error : "+connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();

    }





}
