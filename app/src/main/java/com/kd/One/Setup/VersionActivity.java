package com.kd.One.Setup;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kd.One.BuildConfig;
import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Control.GasActivity;
import com.kd.One.Control.HeatActivity;
import com.kd.One.Control.LightActivity;
import com.kd.One.Control.StandbypowerActivity;
import com.kd.One.Control.VentilationActivity;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.Info.NoticeActivity;
import com.kd.One.Info.VisitorActivity;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutionException;

/**
 * Created by lwg on 2016-09-06.
 */
public class VersionActivity extends Activity {

    private String TAG = "VersionActivity";
    //**********************************************************************************************
    private Messenger mVersionResponse = null;
    private Messenger mVersionRequest = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public MyGlobal mMyGlobal;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    private Handler mTimeHandler;
    //**********************************************************************************************

    //**********************************************************************************************
    private TextView mTextViewNow;
    private TextView mTextViewNew;
    private String mStringNowVersion;
    private String mStringNewVersion;
    //**********************************************************************************************

    //**********************************************************************************************
    private SimpleSideDrawer mSlideDrawer;
    private ViewGroup mViewGroupHeat;
    private ViewGroup mViewGroupGas;
    private ViewGroup mViewGroupLight;
    private ViewGroup mViewGroupVentilation;
    private ViewGroup mViewGroupPower;
    private ViewGroup mViewGroupShutdown;
    private ViewGroup mViewGroupHome;
    private ViewGroup mViewGroupHomeView;
    private ViewGroup mViewGroupEMS;
    private ViewGroup mViewGroupVisitor;
    private ViewGroup mViewGroupNotice;
    private boolean mIsSlideOpen;
    //**********************************************************************************************

    //**********************************************************************************************
    private int mWaitGroupCount = 0;
    private int mWaitCount = 0;
    private int mRequestState = 0;
    private static final int REQUEST_DATA_CLEAR = 0;
    private static final int REQUEST_DATA_SEND_START = 1;
    private static final int REQUEST_DATA_SEND_WAIT = 2;

    private static final int TIMER_REQUEST = 500;  // 1500msec
    private static final int TIMER_NULL = 0;
    private static final int TIMER_WAIT_TIME = 20;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param savedInstanceState
     * @breif oncreate help activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#fafafa"));
            }
        }
        setContentView(R.layout.activity_setup_version);

        //******************************************************************************************
        /**
         * @breif broadcast intent filter
         */
        registerReceiver();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif service registration
         */
        Intent intent_response = new Intent(this, HomeTokService.class);
        startService(intent_response);
        mVersionResponse = new Messenger(responseHandler);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif global data setup
         */
        //******************************************************************************************
        mMyGlobal = mMyGlobal.getInstance();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif progress dialog create
         */
        mProgressDialog = new CustomProgressDialog(this);

        //******************************************************************************************

        //******************************************************************************************
        mTextViewNew = (TextView) findViewById(R.id.Version_TextView_NewVersion);
        mTextViewNow = (TextView) findViewById(R.id.Version_TextView_NowVersion);
        //******************************************************************************************

        String nowVersion = " Ver. " + getVersionInfo(this);
        mTextViewNow.setText(nowVersion);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getBaseContext(), HomeTokService.class);
        bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @brief pause operating
     */
    public void onPause() {
        super.onPause();

        unbindService(requestConnection);

        Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
        tMsg.replyTo = mVersionResponse;
        sendMessage(tMsg);
        mVersionRequest = null;
        TimeHandlerVersion(false, TIMER_NULL);

        if (mCustomPopup != null) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif destroy operating
     */
    public void onDestroy() {
        responseHandler = null;
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacks(VersionRunner);
        }
        super.onDestroy();
        unregisterReceiver(appReceiver);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    public String getVersionInfo(Context context) {
        String version = "Unknown";
        PackageInfo packageInfo;

        if (context == null) {
            return version;
        }
        try {
            packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getApplicationContext().getPackageName(), 0 );
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getVersionInfo :" + e.getMessage());
        }
        return version;
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*
            mVersionRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mVersionResponse;
            sendMessage(tMsg);

            mWaitCount = 0;
            mRequestState = REQUEST_DATA_SEND_START;
            TimeHandlerVersion(true, TIMER_REQUEST);
            mProgressDialog.Show(getString(R.string.progress_request));

            */
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mVersionResponse;
            sendMessage(tMsg);
            mVersionRequest = null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif broadcast receiver registration
     */
    public void registerReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_APP_FINISH);
        intentFilter.addAction(Constants.ACTION_APP_NETWORK_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_SERVER_CONNECT_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_OP_TIMEOUT);
        registerReceiver(appReceiver, new IntentFilter(intentFilter));
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief HomeTok service callback message result
     */
    private Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_WHAT_LOGIN_VERSION_REQUEST:
                    VersionResult((KDData) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif broadcast receiver operating
     */
    public final BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.ACTION_APP_FINISH)) {
                finish();
            } else if (action.equals(Constants.ACTION_APP_NETWORK_ERROR)) {
            } else if (action.equals(Constants.ACTION_APP_SOCKET_CLOSE)) {
            } else if (action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)) {
            } else if (action.equals(Constants.ACTION_APP_OP_TIMEOUT)) {
                TimeOutMoving.TimeOutMoving(mVersionRequest, mVersionResponse, VersionActivity.this);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tFind
     * @return
     * @breif slide menu bar find menu
     */
    private boolean DeviceFind(String tFind) {
        for (int i = 0; i < mMyGlobal.GlobalDeviceList.size(); i++) {
            if (mMyGlobal.GlobalDeviceList.get(i).equals(tFind)) {
                return true;
            }
        }
        return false;
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tMsg
     * @breif send message
     * @brief message send function
     */
    private void sendMessage(Message tMsg) {
        try {
            if (mVersionRequest != null) {
                mVersionRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tState
     * @param tTime
     * @breif time handler setup function
     */
    private void TimeHandlerVersion(boolean tState, int tTime) {
        if (tState == true) {
            if (mTimeHandler == null) {
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(VersionRunner, tTime);
        } else {
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable VersionRunner = new Runnable() {
        @Override
        public void run() {
            /*
            if (mTimeHandler != null) {
                if (mRequestState == REQUEST_DATA_SEND_START) {
                    VersionRequest();
                    TimeHandlerVersion(true, TIMER_REQUEST);
                } else {
                    mWaitCount++;
                    if (mWaitCount > TIMER_WAIT_TIME) {
                        mWaitCount = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerVersion(false, TIMER_NULL);
                        if (mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(VersionActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    } else {
                        TimeHandlerVersion(true, TIMER_REQUEST);
                    }
                }
            } else {
                TimeHandlerVersion(false, TIMER_NULL);
            }

             */
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif version request
     */
    private void VersionRequest() {
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVersion(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVersionResponse;
        tMsg.what = Constants.MSG_WHAT_LOGIN_VERSION_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_LOGIN_VERSION_REQUEST);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tKDData
     * @breif Version result
     */
    public void VersionResult(KDData tKDData) {
        mWaitCount = 0;
        TimeHandlerVersion(false, TIMER_NULL);

        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserVersion(tKDData.ReceiveString);
                mProgressDialog.Dismiss();
            } else {
                mProgressDialog.Dismiss();
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VersionActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();

                    mStringNowVersion = " Ver" + String.valueOf(2.2);
                    mTextViewNow.setText(mStringNowVersion);
                }
            }
        } else {
            mProgressDialog.Dismiss();
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif back button
     */
    @Override
    public void onBackPressed() {
//        Intent intent = new Intent(VersionActivity.this, MainFragment.class);
//        intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_SETUP);
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok button
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;

//            Intent intent = new Intent(VersionActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param v
     * @breif onclick version button
     */
    public void OnClickBtnVersion(View v) {
        switch (v.getId()) {
            case R.id.Version_Lin_Back:
                onBackPressed();
                break;
            case R.id.Version_Btn_Menu:
                mSlideDrawer.toggleRightDrawer();
                mIsSlideOpen = mSlideDrawer.isRightSideOpened();
                break;
            case R.id.Version_Btn_Version:
                Uri uri = Uri.parse("market://details?id=com.kyungdong.kdhomenet");
                Intent tIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(tIntent);
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    public void HNMLDataParserVersion(String tContents) {
        mStringNewVersion = new String();
        mStringNowVersion = new String();

        if (tContents != null) {
            XmlPullParser tParser = Xml.newPullParser();

            try {
                tParser.setInput(new StringReader(tContents));
                int tEventType = tParser.getEventType();
                String tName = null;

                while (tEventType != XmlPullParser.END_DOCUMENT) {

                    String name = tParser.getName();
                    switch (tEventType) {
                        case XmlPullParser.START_TAG:
                            tName = name;
                            if (name.equals("Data")) {
                                tName = tParser.getAttributeValue(null, "name");
                            }
                            break;
                        case XmlPullParser.TEXT:
                            String tConvert = tParser.getText().trim();

                            if (tName.equals("Android")) {
                                tName = "";
                                if (tConvert.length() == 0) {
                                    mStringNewVersion = "";
                                } else {
                                    mStringNewVersion = tParser.getText();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    tEventType = tParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HNML", "Exception");
            }
            mStringNowVersion = " : Ver." + " " + BuildConfig.VERSION_NAME;

            mTextViewNow.setText(mStringNowVersion);
            mTextViewNew.setText(" : Ver. " + mStringNewVersion);
        }
    }

    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;

        @Override
        protected String doInBackground(String... params) {

            try {
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.i(TAG,"VERSION : " + newVersion);

            return newVersion;
        }
    }
}