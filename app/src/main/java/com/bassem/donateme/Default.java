package com.bassem.donateme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Default extends AppCompatActivity {

    LinearLayout logolayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        logolayout =(LinearLayout) findViewById(R.id.logolayout);
        checkIfPreferencesExist();
        checkOrientation();
    }

    private void checkOrientation() {
        int orientation = getResources().getConfiguration().orientation;
        switch(orientation) {
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
        SharedPreferences myprefs =this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String value = myprefs.getString("user",null);
        if(value!=null)
        {
            Intent myIntent=new Intent(this, UserProfile.class);
            this.startActivity(myIntent);
        }

    }
   public void btnLogin_Click(View view)
    {
        Intent myintent = new Intent(this,Login.class);
        this.startActivity(myintent);
    }
    public void btn_Register_Click(View view)
    {
        Intent myintent = new Intent(this,register.class);
        this.startActivity(myintent);
    }



}
