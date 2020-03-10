package com.kd.One.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by lwg on 2016-07-07.
 */
public class NerworkUtil {
    public static int TYPE_WIFI         = 1;
    public static int TYPE_MOBILE       = 2;
    public static int TYPE_NOT_CONNECT  = 0;

    public static int getConnectivityStatus(Context context){
        ConnectivityManager tCM = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo tActiveNetwork = tCM.getActiveNetworkInfo();
        if(tActiveNetwork != null){
            if(tActiveNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                return TYPE_WIFI;
            }else if(tActiveNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                return TYPE_MOBILE;
            }
        }

        return TYPE_NOT_CONNECT;
    }

    public static String getLocalIPAddr(Context context){
        WifiManager wm          = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
        DhcpInfo dpcpInfo    = wm.getDhcpInfo();
        int         serverip    = dpcpInfo.gateway;
        int         ip          = dpcpInfo.ipAddress;

        String ipAddr = String.format("%d.%d.%d.%d",
                ip & 0xff,
                ip >> 8 & 0xff,
                ip >> 16 & 0xff,
                ip >> 24 & 0xff);

        return ipAddr;
    }

    public static String getMacAddr(Context context){
        WifiManager tManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo tInfo = tManager.getConnectionInfo();
        String tMac = tInfo.getMacAddress();

        return tMac;
    }
}
