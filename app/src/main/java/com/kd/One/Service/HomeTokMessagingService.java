package com.kd.One.Service;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kd.One.R;

public class HomeTokMessagingService extends FirebaseMessagingService {
    private static final String TAG = "HomeTokMessagingService";

    String imgsrc = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.e("on Message Received", remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.e("HomeTokMessagingService", "Message data payload : "+remoteMessage.getData().toString());
        }

        if(remoteMessage.getNotification() != null){
            Log.e("HomeTokMessagingService", "Message Notification body : " + remoteMessage.getNotification().getBody());
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle("FCM Message")
                .setContentText(remoteMessage.getData().toString())
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
        //.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        //sendNotification(remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String messageBody){
        /*Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);*/

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle("FCM Message")
                //.setContentText(messageBody)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
                //.setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
