package com.bassem.donateme.Notifications;

/**
 * Created by bassem on 7/3/2016.
 */

import com.bassem.donateme.R;
import com.bassem.donateme.UserProfile;
import com.google.android.gms.gcm.GcmListenerService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.google.android.gms.gcm.GcmListenerService;
import com.kosalgeek.asynctask.MainActivity;

//Class is extending GcmListenerService
public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        String Title = data.getString("title");
        //Displaying a notiffication with the message
        sendNotification(Title,message);
    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String title, String message) {
        Intent intent = new Intent(this, UserProfile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.notification_ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
                .setLights(0xff00ff00, 500, 1000)
                .setAutoCancel(true)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}