package com.bassem.donateme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bassem.donateme.Helpers.DownloadUpload;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.Notifications.GCMRegistrationIntentService;
import com.bassem.donateme.classes.DeviceToken;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class Default extends AppCompatActivity{

    LinearLayout logolayout;
    ImageView imglogo;
    private Boolean exit = false;
    private String NotificationToken = "";
    //Creating a broadcast receiver for gcm registration
    public  BroadcastReceiver  mRegistrationBroadcastReceiver ;
    public DeviceToken mydevicetoken = new DeviceToken();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
      Toast.makeText(this,mydevicetoken.getDeviceToken(),Toast.LENGTH_LONG).show();
       // Helper.CheckInternetConnection(this);
        DownloadUpload.verifyStoragePermissions(this);
        SetReceiver();
        SetControls();
        SetLogoAnimation();

        checkIfPreferencesExist();
        checkOrientation();
    }



    private void SetLogoAnimation() {
        Animation animBounce = AnimationUtils.loadAnimation(this, R.anim.animation);
        if(animBounce!=null){
            imglogo.startAnimation(animBounce);
        }

    }

    private void SetControls() {
        logolayout = (LinearLayout) findViewById(R.id.logolayout);
        imglogo = (ImageView) findViewById(R.id.Defaultimglogo);
    }

    public void SetReceiver() {
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    //Getting the registration token from the intent
                    NotificationToken = intent.getStringExtra("token");
                   // Toast.makeText(Default.this, "GCM registration Token : "+NotificationToken, Toast.LENGTH_LONG).show();
                    Log.d("Device Token",NotificationToken);
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                    Toast.makeText(Default.this, "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Default.this, "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Default.this);

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(Default.this, "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, Default.this);

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(Default.this, "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(Default.this, GCMRegistrationIntentService.class);
            Default.this.startService(itent);
        }
    }


    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(Default.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(Default.this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(Default.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {

        if (exit) {
            finish(); // finish activity
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }

    private void checkOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) logolayout.getLayoutParams();
// Changes the height and width to the specified *pixels*
                // params.height = 600;
                //logolayout.setLayoutParams(params);
                break;
            default:
                break;
        }
    }


    private void checkIfPreferencesExist() {
        SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String value = myprefs.getString("user", null);
        if (value != null) {
            if (value != "") {
                Intent myIntent = new Intent(this, UserProfile.class);
                this.startActivity(myIntent);
            }

        }

    }

    public void btnLogin_Click(View view) {
        Intent myintent = new Intent(this, Login.class);
        myintent.putExtra("token",NotificationToken);

        this.startActivity(myintent);
    }

    public void btn_Register_Click(View view) {
        Intent myintent = new Intent(this, register.class);
        myintent.putExtra("token",NotificationToken);
        this.startActivity(myintent);
    }


}
