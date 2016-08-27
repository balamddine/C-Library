package com.bassem.donateme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.users;

public class activity_add_category extends AppCompatActivity implements AsyncResponse {

    EditText txtCatName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add_category);

        txtCatName= (EditText)findViewById(R.id.txtCatName);
    }

    public void BtnaddCategory_Click(View view) {
        if(Validate())
        {
            Categories C = new Categories();
            C.setUserID("" +users.GetCurrentuser(this).getID());
            C.setName(txtCatName.getText().toString());
            C.AddNewCategory(this,this);

        }
    }
    private Boolean Validate() {
        if (txtCatName.getText().length() == 0) {
            txtCatName.setError("Name required");
            return false;
        }
        return true;
    }

    @Override
    public void processFinish(String result) {
        String JsonStatus = Helper.GetJsonStatusResult(result,"categories");
        String JsonMessage = Helper.GetJsonMessageResult(result,"categories");
        Toast.makeText(this,JsonMessage,Toast.LENGTH_LONG).show();
        finish();
        startActivity(new Intent(this,UserProfile.class));
    }
}
