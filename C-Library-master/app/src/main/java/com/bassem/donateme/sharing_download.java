package com.bassem.donateme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bassem.donateme.Helpers.Helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class sharing_download extends AppCompatActivity {
    String dwnload_file_path = "";
    String file_ID = "";
    String FileName = "";
    Dialog dialog;
    private ProgressDialog pDialog;
   public String DownloadFolderPath ="";
  //  private String DownloadedFilePath ="/sdcard/Download/";
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    String FolderName = "Clibrary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_download);
        CreateDownloadFolder();
        setPageAttributes();
        DownloadF();

    }

    private void CreateDownloadFolder() {
       DownloadFolderPath = Environment.getExternalStorageDirectory().getPath() + "/" + FolderName;
        File file = new File(DownloadFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void DownloadF() {
        new DownloadFileFromURL().execute();
    }

    private void setPageAttributes() {
        setTitle("Downloading");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("FileID"))
                file_ID = getIntent().getStringExtra("FileID");
            if (extras.containsKey("FileName"))
            {
                FileName = getIntent().getStringExtra("FileName");
            }
                dwnload_file_path = Helper.getFileUrl() +FileName ;
        }
    }


    /**
     * Showing Dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

   public class DownloadFileFromURL extends AsyncTask<String,Integer,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                    URL url = new URL(dwnload_file_path);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    Log.d("DownloadedData","Data::" + dwnload_file_path);
                    // Output stream to write file

                OutputStream output =   new FileOutputStream(DownloadFolderPath + "/" + "test.pdf");;

                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) ((total * 100) / lenghtOfFile));
                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            pDialog.setProgress(progress[0]);
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            Toast.makeText(sharing_download.this,"Completed",Toast.LENGTH_LONG);
            showAlertDialog("","Would you like to navigate to Download folder ?");
        }

    }
    public void showAlertDialog(String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Helper.OpenFile(sharing_download.this,DownloadedFilePath + "test.pdf");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + FolderName);
                intent.setDataAndType(uri, "*/*");
                startActivity(Intent.createChooser(intent, "Open folder"));
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

}
