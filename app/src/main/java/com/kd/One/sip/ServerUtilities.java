package com.kd.One.sip;

import android.content.Context;
import android.util.Log;

import com.kd.One.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId, final String phone) {
        //System.out.println("GCM>>>registering device (regId = " + regId + ")"+"  idsize:"+regId.length());
        Log.i(CommonUtilities.TAG, "++++  registering device (regId = " + regId + ")"+"  idsize:"+regId.length());
        String serverUrl = CommonUtilities.SERVER_URL + "/register";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(CommonUtilities.TAG, "Attempt #" + i + " to register");
            try {
                if( CommonUtilities.SERVER_IP!=null
                        && CommonUtilities.SERVER_IP.length()>0 && CommonUtilities.SERVER_PORT>0)
                {
                    SIPStack.deviceInfo.id=phone;
                    SIPStack.deviceInfo.pushKey=SIPStack.ANDROID_API_KEY;
                    SIPStack.deviceInfo.deviceId=regId;
                    SIPStack.deviceInfo.T0=new Date();
                    SIPStack.deviceInfo.flag=true;
                    System.out.println("Devie Info set["+SIPStack.deviceInfo.toString()+"]");

                }
                //GCMRegistrar.setRegisteredOnServer(context, true);    2018/09/13  여기에 들어올 일이 없음
                String message = context.getString(R.string.server_registered);
                CommonUtilities.displayMessage(context, message);
                return true;
            }
            //catch (IOException e) { original
            catch (Exception e) {
                Log.e(CommonUtilities.TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(CommonUtilities.TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(CommonUtilities.TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        //String message = context.getString(R.string.server_register_error,
        //        MAX_ATTEMPTS);
        String message = "server register error";//2012 09 21
        CommonUtilities.displayMessage(context, message);
        return false;
    }
    static boolean register(final String regId,final String phone) {
        Log.i(CommonUtilities.TAG, "+++++   registering device (regId = " + regId + ")"+"  idsize:"+regId.length());
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(CommonUtilities.TAG, "Attempt #" + i + " to register");
            try {
                //displayMessage(context, context.getString(
                //        R.string.server_registering, i, MAX_ATTEMPTS));
                //post(serverUrl, params); original
                if(CommonUtilities.SERVER_IP!=null
                        && CommonUtilities.SERVER_IP.length()>0 && CommonUtilities.SERVER_PORT>0)
                {
                    SIPStack.deviceInfo.id=phone;
                    SIPStack.deviceInfo.pushKey=SIPStack.ANDROID_API_KEY;
                    SIPStack.deviceInfo.deviceId=regId;
                    SIPStack.deviceInfo.T0=new Date();
                    SIPStack.deviceInfo.flag=true;
                    System.out.println("Devie Info set["+SIPStack.deviceInfo.toString()+"]");

                }
                return true;
            }
            //catch (IOException e) { original
            catch (Exception e) {
                Log.e(CommonUtilities.TAG, "Failed to register on attempt " + i, e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(CommonUtilities.TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(CommonUtilities.TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return false;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static void unregister(final Context context, final String regId,final String phone) {
        Log.i(CommonUtilities.TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = CommonUtilities.SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            SIPStack.deviceInfo.flag=false;

            //GCMRegistrar.setRegisteredOnServer(context, false); 사용 안하므로 일단 주석 2018/09/13
            String message = context.getString(R.string.server_unregistered);
            CommonUtilities.displayMessage(context, message);
        }
        //catch (IOException e) { original
        catch (Exception e) {
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(CommonUtilities.TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
