package com.bassem.donateme.classes;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bassem.donateme.Helpers.DownloadUpload;
import com.bassem.donateme.R;
import com.bassem.donateme.activity_file_sharing_popup;

import org.apache.commons.io.output.ByteArrayOutputStream;

public class UploadService extends IntentService {

    private NotificationManager nm;
    private final Calendar time = Calendar.getInstance();
    Uri FPath= null;
    File sourceFile;
    public long totalSize=0;
    String filePath ="";
    String Fname ="";
    String FriendID ="";
    DownloadUpload du = new DownloadUpload();
    Map<String, String> Postparams =null;
    NotificationCompat.Builder mBuilder;
    String DBResult="";
    public UploadService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if(extras.containsKey("Filepath"))
                FPath =  Uri.parse(intent.getStringExtra("Filepath"));
            if(extras.containsKey("Filesize"))
                totalSize=  intent.getLongExtra("Filesize",0);
        }
        filePath = DownloadUpload.getPath(this,FPath);
        Fname = getFileName(FPath);
        FriendID  = intent.getStringExtra("FriendID");
        users u = users.GetCurrentuser(this);
        Postparams=new HashMap<String, String>();
        Postparams.put("call","ShareFile");
        Postparams.put("UserID", ""+u.getID());
        Postparams.put("FriendID", ""+FriendID);
        Postparams.put("Name", u.getName());
       String res = UploadFile();
        ShowCategorySelection();
    }

    private String UploadFile() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileInputStream fis;
        try {
            File F = new File(filePath);
            fis = new FileInputStream(F);
            DBResult= du.Upload(fis,filePath,Postparams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DBResult;
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
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Uploading",Toast.LENGTH_LONG).show();
        SetProgressNotification();
    }

    private void SetProgressNotification() {
        final int id = 0;
        nm =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.notification_ic_launcher)
                .setContentTitle("Uploading...")
                .setContentText("your file is being uploaded!");
// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr+=5) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(100, incr, false);
                            // Displays the progress bar for the first time.
                            nm.notify(id, mBuilder.build());
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for 5 seconds
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Log.d("notification", "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        mBuilder.setContentText("Download complete")
                                // Removes the progress bar
                                .setProgress(0,0,false);
                        nm.notify(id, mBuilder.build());
                        nm.cancel(0);


                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();
    }

    private void ShowCategorySelection() {
        Toast.makeText(UploadService.this,"Your upload is complete",Toast.LENGTH_LONG).show();
        int InsertedID  = Integer.parseInt(DBResult);
        Intent popUpintent = new Intent(UploadService.this, activity_file_sharing_popup.class);
        popUpintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        popUpintent.putExtra("InsertedID", ""+InsertedID);
        startActivity(popUpintent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancel the persistent notification.
      //  nm.cancel(0);

    }



}