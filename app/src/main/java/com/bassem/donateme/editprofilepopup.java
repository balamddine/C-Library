package com.bassem.donateme;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class editprofilepopup extends AppCompatActivity implements AsyncResponse {
    EditText textPersonName;
    EditText txtProfession;
    JSONObject UserJson;
    users user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofilepopup);

        SetContent();
        FillInputs();
    }

    private void SetContent() {
        textPersonName=(EditText) findViewById(R.id.txtName);
        txtProfession=(EditText)findViewById(R.id.txtProfession);
    }

    private void FillInputs() {
        SharedPreferences myprefs =this.getSharedPreferences("user", MODE_WORLD_READABLE);
        String userjson = myprefs.getString("user",null);
        // Intent intent = getIntent();
        //String userjson = intent.getStringExtra("user").toString();
        try {
            UserJson = new JSONObject(userjson);
            textPersonName.setText(UserJson.getString("Name"));
            txtProfession.setText(UserJson.getString("Profession"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnEditProfile(View view)  {

        try {
            user = new users();
            user.setID(Integer.parseInt(UserJson.getString("ID").toString()) );
            user.setName(textPersonName.getText().toString());
            user.setEmail(UserJson.getString("Email").toString());

            user.setPassword(UserJson.getString("Password").toString());
            String profession = txtProfession.getText().toString().equals("")?"-":txtProfession.getText().toString();
            user.setProfession(profession);
            user.setImage(UserJson.getString("Image").toString());
            user.setUserNotificationToken(UserJson.getString("UserNotificationToken").toString());
            user.Modify(this,this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void btnChangePassword(View view) {

    }

    public void btnCancel(View view) {
        finish();
    }

    @Override
    public void processFinish(String result) {
        try {
            JSONObject jsonObj = null;
            jsonObj = new JSONObject(result.toString());
            JSONArray userJSON = jsonObj.getJSONArray("user");
            JSONObject obj = userJSON.getJSONObject(0);
            SharedPreferences myprefs = this.getSharedPreferences("user", MODE_WORLD_READABLE);
            myprefs.edit().putString("user",user.toJSON()).apply();
            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_LONG).show();
            finish();
            Intent intent = new Intent(this, editprofile.class);
            intent.putExtra("Refresh", "1");
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
