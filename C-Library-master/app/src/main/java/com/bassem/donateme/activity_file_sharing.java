package com.bassem.donateme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bassem.donateme.Helpers.DownloadUpload;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.classes.users;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;


public class activity_file_sharing extends AppCompatActivity  {
    Uri FPath= null;

    ProgressDialog progressDialog;
    Context context;
    File sourceFile;
    public long totalSize=0;
    DownloadUpload du = new DownloadUpload();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_file_sharing);
        context = this;
        setTitle("Sharing");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(extras.containsKey("Filepath"))
                FPath =  Uri.parse(getIntent().getStringExtra("Filepath"));
            if(extras.containsKey("Filesize"))
                totalSize=  getIntent().getLongExtra("Filesize",0);
        }
        String filePath = DownloadUpload.getPath(this,FPath);
        String Fname = getFileName(FPath);
        String FriendID = getIntent().getStringExtra("FriendID");
        users u = users.GetCurrentuser(this);
        Map<String, String> Postparams=new HashMap<String, String>();
        Postparams.put("call","ShareFile");
        Postparams.put("UserID", ""+u.getID());
        Postparams.put("FriendID", ""+FriendID);
        Postparams.put("GroupID", "-1");
        Postparams.put("Name", u.getName());
        new UploadFileToServer(this,FPath,Fname,filePath,Postparams).execute();

    }

   public class UploadFileToServer extends AsyncTask<String, String, String> {
        private ProgressDialog pd;
        private Context c;
        private Uri path;
        private String Fname;
       private String FilePath;
       private Map<String, String> Postparams;

        public UploadFileToServer(Context c, Uri path,String Fname,String FilePath,Map<String, String> Postparams) {
            this.c = c;
            this.path = path;
            this.Fname = Fname;
            this.FilePath = FilePath;
            this.Postparams=Postparams;
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(context);
            pd.setMessage("Uploading file. Please wait...");
            pd.setIndeterminate(false);
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(true);
            pd.show();
            pd = ProgressDialog.show(c, "Uploading", "Please Wait");
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {
            pd.dismiss();
            try {
               int InsertedID  = Integer.parseInt(result);
                Toast.makeText(context, "File shared successfully", Toast.LENGTH_LONG).show();
                Intent popUpintent = new Intent(context, activity_file_sharing_popup.class);
                popUpintent.putExtra("InsertedID", ""+InsertedID);
                startActivity(popUpintent);
            } catch (Exception e) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finish();
            super.onPostExecute(result);

        }

        @Override
        protected String doInBackground(String... params) {
            String result="";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis;
            try {
                File F = new File(FilePath);
                fis = new FileInputStream(F);
                result= du.Upload(fis,FilePath,Postparams);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }



    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }




}
