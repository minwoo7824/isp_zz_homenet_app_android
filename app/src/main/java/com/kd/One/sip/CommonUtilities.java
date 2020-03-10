package com.kd.One.sip;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    static final String SERVER_URL = "http://192.168.10.2:8080/";
//    static final String SERVER_URL = "http://192.168.27.195:8080/";


    static  String SERVER_IP = "124.111.208.78";
    static  int SERVER_PORT	= 21010;
    /**
     * Google API project id registered to use GCM.
     */
    static final String SENDER_ID = "518070849451";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "BSSGCM>>> ";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.bssoft.homemanagerv2.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
