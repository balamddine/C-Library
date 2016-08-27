package com.bassem.donateme.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by bassem on 8/15/2016.
 */
public class DownloadUpload {

    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;
    String urlServer = Helper.getUrl() +"helper.php";//"uploadfile.php";// "http://192.168.1.1/handle_upload.php";
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary =  "*****";
    byte[] buffer;
    int bytesRead, bytesAvailable, bufferSize;
    int maxBufferSize = 1*1024*1024;
    public static final int SERVER_CODE_OK=200;
public String Upload(FileInputStream fileInputStream,String pathToOurFile,Map<String, String> params) {
    String reply="";
    try {

        URL url = new URL(urlServer);
        connection = (HttpURLConnection) url.openConnection();

        // Allow Inputs &amp; Outputs.
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        // Set HTTP method to POST.
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(twoHyphens + boundary + lineEnd);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile  +"\"" + lineEnd);
        outputStream.writeBytes(lineEnd);
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];

        // Read file
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while (bytesRead > 0) {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        outputStream.writeBytes(lineEnd);

        // Set Additioinal POST Data
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = params.get(key);

            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(value);
            outputStream.writeBytes(lineEnd);
        }


        outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

        // Responses from the server (code and message)
        int serverResponseCode = connection.getResponseCode();
        String serverResponseMessage = connection.getResponseMessage();

        /**************************Read Server response *****************/

        if(serverResponseCode==SERVER_CODE_OK)
        {
            InputStream in = connection.getInputStream();
            StringBuffer sb = new StringBuffer();
            try {
                int chr;
                while ((chr = in.read()) != -1) {
                    sb.append((char) chr);
                }
                reply = sb.toString();

            } finally {
                in.close();
            }
        }

        fileInputStream.close();
        outputStream.flush();
        outputStream.close();
    } catch (Exception ex) {
        Log.d("DownloadUpload",ex.getMessage());       //Exception handling
    }
    return reply;
}
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    /** * Get a file path from a Uri. This will get the the path for Storage Access *
     * Framework Documents, as well as the _data field for the MediaStore and * other file-based ContentProviders.
     * * * @param context The context. * @param uri The Uri to query. * @author paulburke */
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
         if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
             // ExternalStorageProvider
             if (isExternalStorageDocument(uri)) {
                 final String docId = DocumentsContract.getDocumentId(uri);
                 final String[] split = docId.split(":");
                 final String type = split[0];
                 if ("primary".equalsIgnoreCase(type)) {
                     return Environment.getExternalStorageDirectory() + "/" + split[1];
                 }
                 // TODO handle non-primary volumes
             } // DownloadsProvider
              else if (isDownloadsDocument(uri)) {
                 final String id = DocumentsContract.getDocumentId(uri);
                 final Uri contentUri = ContentUris.withAppendedId( Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                 return getDataColumn(context, contentUri, null, null);
             }
             // MediaProvider
             else if (isMediaDocument(uri)) {
                 final String docId = DocumentsContract.getDocumentId(uri);
                 final String[] split = docId.split(":");
                 final String type = split[0];
                 Uri contentUri = null; if ("image".equals(type)) {
                     contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                 } else if ("video".equals(type)) {
                     contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                 } else if ("audio".equals(type)) {
                     contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                 }
                 final String selection = "_id=?";
                 final String[] selectionArgs = new String[] { split[1] };
                 return getDataColumn(context, contentUri, selection, selectionArgs);
             }
         }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
             return getDataColumn(context, uri, null, null);
         }
     // File
     else if ("file".equalsIgnoreCase(uri.getScheme())) {
        return uri.getPath();
    }
    return null;
}
    /** * Get the value of the data column for this Uri. This is useful for * MediaStore Uris, and other file-based ContentProviders.
     *  * * @param context The context. * @param uri The Uri to query. * @param selection (Optional) Filter used in the query.
     *  * @param selectionArgs (Optional) Selection arguments used in the query. * @return The value of the _data column,
     *  which is typically a file path. */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }
    /** * @param uri The Uri to check. * @return Whether the Uri authority is ExternalStorageProvider. */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /** * @param uri The Uri to check. * @return Whether the Uri authority is DownloadsProvider. */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /** * @param uri The Uri to check. * @return Whether the Uri authority is MediaProvider. */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
