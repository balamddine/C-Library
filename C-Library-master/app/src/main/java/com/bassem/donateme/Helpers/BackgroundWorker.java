package com.bassem.donateme.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by bassem on 5/29/2016.
 */
public class BackgroundWorker extends AsyncTask<String,String,String> {

    private Context context;
    private HashMap<String, String> postData = new HashMap();
    private String loadingMessage = "Loading...";
    private boolean showLoadingMessage = true;
    private ProgressDialog progressDialog;
    private AlertDialog alertdialog;
    private AsyncResponse asyncResponse;
    private  String ProcessFinishErrorResult = "";

    public boolean isShowLoadingMessage() {
        return showLoadingMessage;
    }
    public void setShowLoadingMessage(boolean showLoadingMessage) {
        this.showLoadingMessage = showLoadingMessage;
    }
    public BackgroundWorker(Context context,AsyncResponse asyncResponse, HashMap<String, String> postData ) {
        this.context = context;
        this.postData = postData;
        this.asyncResponse = asyncResponse;
    }
    public BackgroundWorker(Context context, HashMap<String, String> postData,AsyncResponse asyncResponse,String LoadingMessage) {
        this.context = context;
        this.postData = postData;
        this.loadingMessage=LoadingMessage;
        this.asyncResponse = asyncResponse;
    }
    public BackgroundWorker(Context context,AsyncResponse asyncResponse, HashMap<String, String> postData,boolean showLoadingMessage) {
        this.context = context;
        this.postData = postData;
        this.showLoadingMessage=showLoadingMessage;
        this.asyncResponse = asyncResponse;
    }

    public String getProcessFinishErrorResult() {
        return ProcessFinishErrorResult;
    }

    public void setProcessFinishErrorResult(String processFinishErrorResult) {
        ProcessFinishErrorResult = processFinishErrorResult;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }
    public String getLoadingMessage() {
        return this.loadingMessage;
    }

    public Context getContext() {
        return this.context;
    }

    public HashMap<String, String> getPostData() {
        return this.postData;
    }
    public AsyncResponse getAsyncResponse() {
        return this.asyncResponse;
    }

    public void setPostData(HashMap<String, String> postData) {
        this.postData = postData;
    }


    protected String doInBackground(String... urls) {
        String result = "";

        for(int i = 0; i <= 0; ++i) {
            result = this.invokePost(urls[i], this.postData);
        }
        return result;
    }

    private String invokePost(String requestURL, HashMap<String, String> postDataParams) {
        String response = "";

        try {
            HttpURLConnection con = Helper.getConnection(requestURL,postDataParams);
            con.setConnectTimeout(5000);
            int responseCode = con.getResponseCode();
            String line;
            if(responseCode == 200) {
                for(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())); (line = br.readLine()) != null; response = response + line) {
                    ;
                }
            } else {
                response = "responsecode: "+responseCode;

                Log.d("PostResponseAsyncTask", responseCode + "");
            }
        } catch (Exception ex) {
            response = "error: "+ex.getMessage();
            Log.d("exception",ex.toString());
        }

        return response;
    }

    protected void onPreExecute() {
        if(this.showLoadingMessage) {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //this.progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            this.progressDialog.setMessage(this.loadingMessage);
            this.progressDialog.setIndeterminate(true);

            this.progressDialog.show();
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if(this.showLoadingMessage && !this.progressDialog.equals(null) && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }

        result = result.trim();
        Log.d("Background workor result",result);
        if(this.asyncResponse != null) {
            try {

                if(result.toLowerCase().contains("error") || result.toLowerCase().contains("responsecode") )
                    Alert("Error from server",result);
                else
                    this.asyncResponse.processFinish(result);
            } catch (Exception e) {
               // Toast.makeText(context,"Error : "+e.getMessage().toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
    public void Alert(String Title, String message) {
        alertdialog = new AlertDialog.Builder(context).create();
        alertdialog.setTitle(Title);
        alertdialog.setMessage(message);
        alertdialog.show();
    }
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}
