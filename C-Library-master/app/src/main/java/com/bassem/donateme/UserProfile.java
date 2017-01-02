package com.bassem.donateme;


import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.donateme.Helpers.CircleTransform;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.Helpers.NetworkChangeReceiver;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProfile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,UserGallery.OnFragmentInteractionListener
    ,UserFriends.OnFragmentInteractionListener
{
    TextView litEmail ;
    TextView litName ;
    ImageView imguser;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View HeaderView;
    JSONObject UserJson;
    FragmentManager fragmentManager;
    Fragment fragment = null;
    int FabResources=0;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("Profile");

      //  Helper.CheckInternetConnection(this);
        SetControls();
        GetIntentKeys();
        SetFragments(savedInstanceState);
        SetDrawerActivitySettings();


    }


    private void SetFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {

            Class fragmentClass = null;
            fragmentClass = UserGallery.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"Gallery").commit();
        }
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

             GetUserImage();
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
        String imageUrl = "";
        try {
            if (UserJson.has("Image")==true) {
                if (UserJson.getString("Image") != null && !UserJson.getString("Image").equals("")) {
                    imageUrl = Helper.getIfHttpUserImageUrl(UserJson.getString("Image"));
                    Picasso.with(getApplicationContext()).load(imageUrl).transform(new CircleTransform()).into(imguser);
                }
            }
            else{
                Picasso.with(getApplicationContext()).load(R.mipmap.noimage).transform(new CircleTransform()).into(imguser);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void SetDrawerActivitySettings() {
        setSupportActionBar(toolbar);
       fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.mipmap.gallery);
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fManager = getSupportFragmentManager();
                if (fManager!=null) {
                    Fragment myFragment = fManager.findFragmentByTag("Gallery");

                    if (myFragment != null && myFragment.isVisible()) {

                        startActivity(new Intent(getApplicationContext(), activity_add_category.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), UserListing.class));
                    }
                }

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent myIntent =null;
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = UserGallery.class;
        String fragmentTag ="Gallery";
        FabResources = R.mipmap.gallery;
        fab.setImageResource(FabResources);
        if (id == R.id.nav_users) {
            fragmentClass = UserFriends.class;
            fragmentTag ="Friends";
            FabResources = R.mipmap.adduser;
        }
        else if(id==R.id.nav_discover)
        {
            myIntent = new Intent(this, UserListing.class);
            this.startActivity(myIntent);
        }
        else if (id == R.id.nav_gallery) {
            fragmentClass = UserGallery.class;
            fragmentTag = "Gallery";
            FabResources = R.mipmap.gallery;
        }
        else if(id==R.id.nav_repositry) {
            myIntent = new Intent(this, user_repositry.class);
            this.startActivity(myIntent);
        }
        else if (id == R.id.nav_usersGroup) {
            myIntent = new Intent(this, groups.class);
            this.startActivity(myIntent);
        }
       /* else if (id == R.id.nav_track) {
            myIntent = new Intent(this, TrackActivity.class);
            this.startActivity(myIntent);
        }*/
        else if(id==R.id.nav_editprofile)
        {
            myIntent = new Intent(this, editprofile.class);
            this.startActivity(myIntent);
        }
        else if (id == R.id.nav_Logout) {
            SharedPreferences settings = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            settings.edit().clear().commit();
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();
            this.finish();
            myIntent = new Intent(this, Default.class);
            this.startActivity(myIntent);
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment!=null){
            fab.setImageResource(FabResources);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment,fragmentTag).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
