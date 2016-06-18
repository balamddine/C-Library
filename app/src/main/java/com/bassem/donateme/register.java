package com.bassem.donateme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       setTitle(Helper.getApplicationName(this) + " - Register");
    }
}
