package com.bassem.donateme.Helpers;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.app.AlertDialog;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bassem.donateme.activity_file_sharing;
import com.bassem.donateme.classes.Categories;
import com.bassem.donateme.classes.files;
import com.bassem.donateme.classes.users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by bassem on 6/11/2016.
 */
public class Helper extends AppCompatActivity {
    public static final String phpHelperClass="helper.php";
    public static final String HostURL ="http://192.168.137.1:80/Clibrary/";//"http://10.0.2.2:8080/Clibrary/";;//"http://leftovers.tabdab.me/Clibrary/";//// ;
    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }
    public static String SetActivityTitle(Context context,String title) {
       return getApplicationName(context) + title;
    }
    public static String getUrl() {
        return HostURL;
    }
    public static String getImageUrl() {
        return getUrl() + "/Library/images/";
    }
    public static String getFileUrl() {
        return getUrl() + "/Library/files/";
    }
    public static String getPhpHelperUrl() { return HostURL + phpHelperClass;}

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator var4 = params.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            if(first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String)entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static HttpURLConnection getConnection(String requestURL,HashMap<String, String> postDataParams) throws IOException {
        URL url = new URL(requestURL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setReadTimeout(30000);
        con.setConnectTimeout(30000);
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();
        return con;
    }

    public static void Alert(AlertDialog alertdialog , String Title, String message) {

        alertdialog.setTitle(Title);
        alertdialog.setMessage(message);
        alertdialog.show();
    }

    public static void ExitApp(Context applicationContext) {

        SharedPreferences settings = applicationContext.getSharedPreferences("user",applicationContext.MODE_WORLD_READABLE);
        settings.edit().clear().commit();
        System.exit(0);
    }

    public static ArrayList<users> GetArrayListFromJsonString(String result) {
        ArrayList<users> listdata = new ArrayList<users>();
        try {
            JSONObject jsonObj = new JSONObject(result);
           JSONArray jArray = jsonObj.getJSONArray("user");
            if (jArray != null) {
                users usr = null;
                for (int i=0;i<jArray.length();i++){
                    usr = new users();
                    usr.setID(Integer.parseInt(((JSONObject)jArray.get(i)).getString("ID")));
                    usr.setName(((JSONObject)jArray.get(i)).getString("Name"));
                    usr.setEmail(((JSONObject)jArray.get(i)).getString("Email"));
                    usr.setPassword(((JSONObject)jArray.get(i)).getString("Password"));
                    usr.setImage(((JSONObject)jArray.get(i)).getString("Image"));
                    if (((JSONObject)jArray.get(i)).getString("Accepted") !=null){
                        usr.setAccepted(((JSONObject)jArray.get(i)).getString("Accepted"));
                    }


                   //
                    listdata.add(usr);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return listdata;
    }

    public static String getIfHttpUserImageUrl(String image) {
        if (!image.equals(""))
        {
            if(image.contains("http")) {
                return image;
            }
            else{
                return getImageUrl() + image;
            }
        }
        return image;
    }

    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }
public static  String GetJsonStatusResult(String result, String Fortype) {
    JSONObject jsonObj = null;
    try {
        jsonObj = new JSONObject(result.toString());
        JSONArray userJSON = jsonObj.getJSONArray(Fortype);
        JSONObject obj = userJSON.getJSONObject(0);
        return obj.getString("status");
    } catch (JSONException e) {
        e.printStackTrace();
    }
    // Getting JSON Array node
    return "";
}
    public static  String GetJsonMessageResult(String result, String Fortype) {
        JSONObject jsonObj = null;
        String Rvalue="";
        try {
            jsonObj = new JSONObject(result.toString());
            JSONArray userJSON = jsonObj.getJSONArray(Fortype);
            JSONObject obj = userJSON.getJSONObject(0);
            if(obj.has("message"))
                Rvalue =obj.getString("message");

        } catch (JSONException e) {
            Rvalue ="";
            e.printStackTrace();
        }
        // Getting JSON Array node
        return Rvalue;
    }
    public static  String GetJsonCallResult(String result, String Fortype) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(result.toString());
            JSONArray userJSON = jsonObj.getJSONArray(Fortype);
            JSONObject obj = userJSON.getJSONObject(0);
            return obj.getString("call");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Getting JSON Array node
        return "";
    }
public static void SetFullScreen(AppCompatActivity activity) {
    activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

}
    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    public static ArrayList<Categories> GetCategoriesArrayListFromJsonString(String result) {
        ArrayList<Categories> listdata = new ArrayList<Categories>();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jArray = jsonObj.getJSONArray("categories");
            if (jArray != null) {
                Categories cat= null;
                for (int i=0;i<jArray.length();i++){
                    cat = new Categories();
                    cat.setID(Integer.parseInt(((JSONObject)jArray.get(i)).getString("ID")));
                    cat.setName(((JSONObject)jArray.get(i)).getString("Name"));
                    listdata.add(cat);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return listdata;
    }
    public static ArrayList<files> GetFilesArrayListFromJsonString(String result) {
        ArrayList<files> listdata = new ArrayList<files>();
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jArray = jsonObj.getJSONArray("files");
            if (jArray != null) {
                files fle= null;
                for (int i=0;i<jArray.length();i++){
                    fle = new files();
                    fle.setID(Integer.parseInt(((JSONObject)jArray.get(i)).getString("ID")));
                    fle.setName(((JSONObject)jArray.get(i)).getString("Name"));
                    fle.setSharedWithUserName(((JSONObject)jArray.get(i)).getString("SharedWithName"));
                    listdata.add(fle);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return listdata;
    }


    public static void CheckInternetConnection(Context context) {
        try
        {
            NetworkChangeReceiver networkreceiver= new NetworkChangeReceiver();
            context.registerReceiver(networkreceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        catch  (Exception ex)
        {
            Log.d("Brodcast Receiver error",ex.getMessage());
        }
    }
    public static String getFilePathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch(Exception ex)
        {
            return "";
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static long GetFileSize(Activity ativity , Uri fileUri) {

        Cursor cursor = ativity.getContentResolver().query(fileUri,
                null, null, null, null);
        cursor.moveToFirst();
        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        cursor.close();
        return size;
    }

    public static String getPathFromUri(Context context, Uri uri) throws URISyntaxException {
        if("content".equalsIgnoreCase(uri.getScheme()))
        {
            String[] projection ={"_data"};
            Cursor cursor=null;
            try{
                cursor = context.getContentResolver().query(uri,projection,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst()){
                    return cursor.getString(column_index);
                }

            }
            catch (Exception e){
                Log.d("exception cursor",e.getMessage());
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }
        return null;
    }

    public static String[] GetmimeTypes() {
        String [] mimetypes = {
                "application/pdf",
                "text/*",
                "image/*",
               // "video/*",
                "application/msword", //doc
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", //docx
                "application/vnd.ms-excel", //xls
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" //xlsx
        };
        return mimetypes;
    }

    public static void OpenFile(Context context,String pdfPath ) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(pdfPath); // a directory
        intent.setDataAndType(uri, "*/*");
        context.startActivity(Intent.createChooser(intent, "Open folder"));
    }

}



