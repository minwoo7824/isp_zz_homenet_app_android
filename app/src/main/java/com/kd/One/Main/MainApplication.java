package com.kd.One.Main;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.view.View;
import android.widget.TabHost;

import java.util.HashMap;
import java.util.Map;

public class MainApplication extends Application {
    public int currentTabIndex = 0;
    public boolean nextTab = true;
    public class MSG_WHAT {
        public static final int INIT = 0;
        public static final int DISAPPEAR = 1;
    }

    public TabHost mTabHost = null;
    private static final String TAG = "MainApplication";

    public static class TAB {
        public TAB(String tag, Class<?> cls, View obj) {
            TabTAG = tag;
            clsName = cls;
            view = obj;
        }

        public String TabTAG = null;
        public Class<?> clsName = null;
        public View view = null;
        public Handler tabHandler = null;
    }

    public Map<String, TAB> m_enumCls = new HashMap<String, TAB>();

    public String m_sSelectedTabTag = "tab1";

    public Map<String, Object> m_pSelectedItem = null;

//	public Map<String, Object> loginInfo = null;


    public WifiManager.WifiLock m_wifiLock = null; // 대기모드에서도 WIFI를 끄지 않도록...

    public void closeTitleBtn(boolean leftBtn) {

    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
