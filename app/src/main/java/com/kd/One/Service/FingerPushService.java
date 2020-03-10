package com.kd.One.Service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fingerpush.android.FingerPushFcmListener;
import com.kd.One.Common.Constants;
import com.kd.One.Common.LocalConfig;
import com.kd.One.sip.SmartHomeviewActivity;
import com.kd.One.Login.IntroActivity;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class FingerPushService extends FingerPushFcmListener {
    private String TAG = "FingerPushService";
    boolean isBackground = false;
    private LocalConfig mLocalConfig;

    @Override
    public void onMessage(Context context, Bundle bundle) {
        mLocalConfig = new LocalConfig(getBaseContext());
        createNotificationChannel(bundle);
    }

    private void createNotificationChannel(Bundle data) {

        isBackground = isAppIsInBackground(this);

        try {
            Log.i(TAG,"date11 : " + URLDecoder.decode(data.getString("data.message"),"UTF-8"));
            String message = URLDecoder.decode(data.getString("data.message"),"UTF-8");
            JSONObject jsonObject = new JSONObject(message.replace("[TEST]",""));
            int seviceCode = jsonObject.getInt("ServiceCode");
            int msgCode = jsonObject.getInt("MsgCode");
            String arg = jsonObject.getString("Args");
            String messages = jsonObject.getString("Message");

            if (msgCode == 13011){//통화
                Intent intent = null;
                JSONObject object = new JSONObject(arg);
                if (!isBackground){
                    intent = new Intent(FingerPushService.this, SmartHomeviewActivity.class);
                }else{
                    intent = new Intent(FingerPushService.this, IntroActivity.class);
                }
                intent.putExtra("pushType",object.getString("DeviceType"));
                intent.putExtra("pushPassword",object.getString("AuthCode"));
                intent.putExtra(Constants.INTENT_TYPE_HOME_VIEW,Constants.INTENT_TYPE_HOME_VIEW);
                sendNotification(intent,messages);
            }else if (msgCode == 12011){//택배
                Intent intent = null;
                if (!isBackground){
                    intent = new Intent(FingerPushService.this, MainFragment.class);
                }else{
                    intent = new Intent(FingerPushService.this, IntroActivity.class);
                }
                sendNotification(intent,messages);
            }else if (msgCode == 12021){//입출차
                Intent intent = null;
                if (!isBackground){
                    intent = new Intent(FingerPushService.this, MainFragment.class);
                }else{
                    intent = new Intent(FingerPushService.this, IntroActivity.class);
                }
                sendNotification(intent,messages);
            }else{//나머지
                Intent intent = null;
                if (!isBackground){
                    intent = new Intent(FingerPushService.this, MainFragment.class);
                }else{
                    intent = new Intent(FingerPushService.this, IntroActivity.class);
                }
                sendNotification(intent,messages);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void sendNotification(Intent intent,String message){
//        PendingIntent pi = PendingIntent.getActivity(FingerPushService.this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//        FingerNotification fingerNotification = new FingerNotification(this);
//        fingerNotification.setNofiticaionIdentifier((int) System.currentTimeMillis());
//        fingerNotification.setIcon(R.mipmap.ic_launcher); // Notification small icon
////        fingerNotification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            fingerNotification.setColor(Color.rgb(0, 114, 162));
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            fingerNotification.createChannel("channel_id", "channel_name");
//        }
//        fingerNotification.setVibrate(new long[]{0, 500, 600, 1000});
//        fingerNotification.setLights(Color.parseColor("#ffff00ff"), 500, 500);
//        fingerNotification.showNotification(data, pi);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Uri notification = null;

        NotificationChannel mChannel;
        NotificationCompat.Builder mBuilder;

        Intent pendingIntent = intent;
        pendingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pi = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        notification = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mChannel = new NotificationChannel(getPackageName(), "KDOne", NotificationManager.IMPORTANCE_HIGH);
//            mChannel.setDescription("KDOne push");
//            mNotificationManager.createNotificationChannel(mChannel);

            mChannel = new NotificationChannel(getPackageName(), "HomeTok", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("HomeTok push");
            mNotificationManager.createNotificationChannel(mChannel);

            mBuilder = new NotificationCompat.Builder(this, mChannel.getId())
                    .setSmallIcon(R.mipmap.ic_app_logo)
//                    .setContentTitle("HomeTok")
                    .setContentText(message)
                    .setTicker(message)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(notification);
        }else{
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_app_logo)
//                    .setContentTitle("HomeTok")
                    .setContentText(message)
                    .setTicker(message)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(notification);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setCategory(Notification.CATEGORY_MESSAGE)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVisibility(Notification.VISIBILITY_PUBLIC);
            }
        }

        mNotificationManager.notify(1, mBuilder.build());
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}
