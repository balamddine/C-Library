package com.bassem.donateme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class UserProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    TextView litEmail ;
    TextView litName ;
    ImageView imguser;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View HeaderView;
    JSONObject UserJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle(Helper.getApplicationName(this) + " - Profile");
        SetControls();
        SetDrawerActivitySettings();
        GetIntentKeys();
        GetUserImage();
    }
    private void GetIntentKeys() {
        SharedPreferences myprefs =this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String userjson = myprefs.getString("user",null);
       // Intent intent = getIntent();
        //String userjson = intent.getStringExtra("user").toString();
        try {
            UserJson =new JSONObject(userjson);
            litEmail.setText(UserJson.getString("Email"));
            litName.setText(UserJson.getString("Name") );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SetControls() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        HeaderView = navigationView.getHeaderView(0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer= (DrawerLayout) findViewById(R.id.drawer_layout);

        litEmail = (TextView) HeaderView.findViewById(R.id.litEmail);
        litName= (TextView) HeaderView.findViewById(R.id.litName);
        imguser=(ImageView) HeaderView.findViewById(R.id.imguser);


    }

    private void GetUserImage() {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap bitmap = null;
                try {
                    String imageUrl = "";
                    if(UserJson.getString("Image").contains("http"))
                    {
                        imageUrl = UserJson.getString("Image");
                    }
                    else{
                        imageUrl =Helper.getImageUrl() + UserJson.getString("Image");
                    }

                    bitmap= BitmapFactory.decodeStream((InputStream) new URL(imageUrl).getContent());


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

              //  imguser.setImageBitmap(bitmap);
                imguser.setImageBitmap(RoundImage.getCircleBitmap(bitmap));

            }
        }.execute();
    }


    private void SetDrawerActivitySettings() {
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add File", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_track) {

        }
        else if (id == R.id.nav_Logout) {
            SharedPreferences settings = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            settings.edit().clear().commit();
            Intent myIntent = new Intent(this, Login.class);
            this.startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
