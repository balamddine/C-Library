package com.bassem.donateme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class editprofile extends AppCompatActivity implements AsyncResponse {
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    JSONObject UserJson;
    users myuser ;
    ImageView ProfileImage;
    ListView lstUserInfo;
    ProgressDialog dialog;
    private Bitmap bitmap;
    private final static int RESULT_LOAD_IMG = 1;
    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getIntent().getStringExtra("refresh")=="1")
        {
            finish();
            startActivity(getIntent());
        }



        SetControls();
        initInstancesToolbar();
        SetUserContent();
        SetUserInfoListView();
    }

    private void SetUserInfoListView() {
        String[] values = new String[4];
        values[0] = "Name: " + myuser.getName();
        values[1] = "Email: " + myuser.getEmail();
        values[2] = "Password: ******";
        values[3] = "Profession: " + myuser.getProfession();

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this,R.layout.user_information_layout,R.id.txtinfovalue, values){
                        @Override
                        public View getView(int position, View convertView,ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            ViewGroup.LayoutParams params = view.getLayoutParams();
                            params.height = 150;
                            view.setLayoutParams(params);
                            return view;
                        }
                };
            lstUserInfo.setAdapter(itemsAdapter);
    }

    public void SetControls() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        ProfileImage = (ImageView) findViewById(R.id.imgProfile);
        lstUserInfo = (ListView) findViewById(R.id.lstUserInfo);
    }
    private void SetUserContent() {
        SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String userjson = myprefs.getString("user", null);

        try {
            UserJson = new JSONObject(userjson);
            myuser = new users();
            myuser.GetUserFromJson(UserJson);
            collapsingToolbarLayout.setTitle(UserJson.getString("Name"));

            GetUserImage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void GetUserImage() {
        String imageUrl = "";
        try {
            if (UserJson.has("Image")==true) {
                if (UserJson.getString("Image") != null && !UserJson.getString("Image").equals("")) {
                    imageUrl = Helper.getIfHttpUserImageUrl(UserJson.getString("Image"));
                    Picasso.with(getApplicationContext()).load(imageUrl).into(ProfileImage);
                }
            }
            else{
                Picasso.with(getApplicationContext()).load(R.mipmap.noimage).into(ProfileImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initInstancesToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_edit_user_profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.nav_editprofileImage:
                EditpRofileImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void EditpRofileImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                 selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
               String imgDecodableString = cursor.getString(columnIndex);

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String imgtoupload=Helper.getStringImage(bitmap);
                cursor.close();

                UploadImageToServer(imgtoupload);

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void UploadImageToServer(String imgtoupload) throws JSONException {
        HashMap PostData = new HashMap();
        PostData.put("call", "updateUserImage");
        PostData.put("Image", imgtoupload);
        PostData.put("ID", UserJson.getString("ID"));

        BackgroundWorker registerWorker= new BackgroundWorker(this,this,PostData);
        registerWorker.setLoadingMessage("uploading image...");
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    @Override
    public void processFinish(String result) {


        try {
            JSONObject jsonObj = null;
            jsonObj = new JSONObject(result.toString());
            JSONArray userJSON = jsonObj.getJSONArray("user");
            JSONObject obj = userJSON.getJSONObject(0);
            Picasso.with(getApplicationContext()).load(selectedImage).into(ProfileImage);
            myuser.setImage(obj.getString("ImageName"));
            UpdatePrefabs();
            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void UpdatePrefabs() {

        SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
        myprefs.edit().putString("user",myuser.toJSON()).apply();

    }

    public void imgEditInfo_Click(View view) {
       Intent EditInfoit = new Intent(this,editprofilepopup.class);
       startActivity(EditInfoit);

    }



}
