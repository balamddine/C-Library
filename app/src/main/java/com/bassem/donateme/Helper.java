package com.bassem.donateme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bassem on 6/11/2016.
 */
public class Helper {
    public static final String phpHelperClass="helper.php";
    public static final String HostURL ="http://192.168.137.1:8080/Clibrary/";// "http://10.0.2.2:8080/Clibrary/";
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
        return getUrl() + "images/";
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
}



