package com.bassem.donateme.classes;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bassem.donateme.Helpers.AsyncResponse;
import com.bassem.donateme.Helpers.BackgroundWorker;
import com.bassem.donateme.Helpers.DownloadUpload;
import com.bassem.donateme.Helpers.Helper;
import com.bassem.donateme.R;
import com.bassem.donateme.activity_file_sharing_popup;
import com.bassem.donateme.user_repositry;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DownloadService extends IntentService implements AsyncResponse {

    private NotificationManager nm;
    DownloadUpload du = new DownloadUpload();
    NotificationCompat.Builder mBuilder;
    String dwnload_file_path = "";
    String file_ID = "";
    String FileName = "";
    public String DownloadFolderPath ="";
    String FolderName = "Clibrary";
    ResultReceiver rec;
    public DownloadService() {
        super("");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("FileID"))
                file_ID = intent.getStringExtra("FileID");
            if (extras.containsKey("FileName"))
            {
                FileName = intent.getStringExtra("FileName");
            }
            dwnload_file_path = Helper.getFileUrl() +FileName ;
        }
       DownloadFile();
        UpdateDBRecord();
       rec = intent.getParcelableExtra("DownloadReceiver");
    }
    private void DownloadFile() {
        CreateDownloadFolder();
        du.Download(dwnload_file_path,DownloadFolderPath,FileName);

    }
    private void CreateDownloadFolder() {
        DownloadFolderPath = Environment.getExternalStorageDirectory().getPath() + "/" + FolderName;
        File file = new File(DownloadFolderPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Downloading",Toast.LENGTH_LONG).show();
        SetProgressNotification();
       // UpdateDBRecord();
    }

    private void SetProgressNotification() {
        final int id = 0;
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.notification_ic_launcher)
                .setContentTitle("Downloading...")
                .setContentText("your file is being Downloaded !");
// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 100; incr += 5) {
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
                                .setProgress(0, 0, false);
                        nm.notify(id, mBuilder.build());
                        nm.cancel(0);

                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();


    }

    private void UpdateDBRecord() {
        HashMap<String, String>  PostData=new HashMap<String, String>();
        PostData.put("call","UpdateFile");
        PostData.put("ID", ""+file_ID);
        BackgroundWorker registerWorker= new BackgroundWorker(this,this,PostData);
        registerWorker.setShowLoadingMessage(false);
        registerWorker.execute(Helper.getPhpHelperUrl());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Toast.makeText(DownloadService.this,"Download is completed",Toast.LENGTH_SHORT).show();
        // Cancel the persistent notification.
      //  nm.cancel(0);
    }

    @Override
    public void processFinish(String results) {
        Log.d("Download","Download DB updated");
        Bundle bundle = new Bundle();
        //bundle.putString("resultValue", "My Result Value. Passed in: " );
        // Here we call send passing a resultCode and the bundle of extras
        rec.send(Activity.RESULT_OK, bundle);
    }

}