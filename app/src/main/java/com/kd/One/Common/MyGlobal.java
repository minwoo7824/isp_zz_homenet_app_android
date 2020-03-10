package com.kd.One.Common;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-15.
 */
public class MyGlobal extends Application {
    public ArrayList<String>    GlobalDeviceList = new ArrayList<>();

    public int                  GlobalControlIndex;
    public int                  GlobalInfoIndex;

    private static MyGlobal instance = null;

    public static synchronized MyGlobal getInstance(){
        if(null == instance){
            instance = new MyGlobal();
        }
        return instance;
    }
}
