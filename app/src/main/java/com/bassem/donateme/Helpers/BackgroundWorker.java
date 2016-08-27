package com.bassem.donateme.Helpers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
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
                response = "{\"user\":[{\"status\":\"0\", \"message\":\"Response code : "+responseCode+" \"}]}";
                Log.d("PostResponseAsyncTask", responseCode + "");
            }
        } catch (Exception ex) {
            response = "{\"user\":[{\"status\":\"0\", \"message\":\"Exception : "+ex.getMessage()+" \"}]}";;
            ex.printStackTrace();
            Log.d("exception",ex.toString());
        }

        return response;
    }

    protected void onPreExecute() {
        if(this.showLoadingMessage) {
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setMessage(this.loadingMessage);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        if(this.showLoadingMessage && this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }

        result = result.trim();
        if(this.asyncResponse != null) {

           // JSONObject jsonObj = null;
            try {
             //   jsonObj = new JSONObject(result.toString());
             //   JSONArray userJSON = jsonObj.getJSONArray("user");
             //   JSONObject obj = userJSON.getJSONObject(0);
                //if (obj.getString("status").equals("1")) {
                    this.asyncResponse.processFinish(result);
               // }
               // else{
                   // Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
               //     this.ProcessFinishErrorResult = obj.getString("message")
               //     Alert("Error",obj.getString("message"));
               // }
            } catch (Exception e) {
                Alert("Error",e.getMessage());
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
