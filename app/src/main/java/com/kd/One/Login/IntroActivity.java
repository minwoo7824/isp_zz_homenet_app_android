package com.kd.One.Login;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.kd.One.Common.Constants;
import com.kd.One.Common.FingerPushUtil;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.sip.SmartHomeviewActivity;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

public class IntroActivity extends Activity {

    //**********************************************************************************************
    private Messenger mLoginResponse        = null;
    private Messenger                                   mLoginRequest         = null;

    public static final int         MY_PERMISSION_RECORD_AUDIO = 1;
    public static final int         MY_PERMISSION_READ_EXTERNAL_STORAGE = 4;
    public static final int         MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 5;
    public static final int         MY_PERMISSION_CAMERA = 6;
    public static final int         MY_PERMISSION_READ_PHONE_STATE = 7;

    private LocalConfig mLocalConfig;

    private CustomProgressDialog mProgressDialog;
    private CustomPopupBasic mCustomPopup;

    private int                                         mLoginBackTime;
    private boolean                                     mLoginBackFlag;

    private Handler                                     mTimeHandler;
    private Handler                                     mTimeHandlerBack;

    private int                                         mWaitCount              = 0;
    private int                                         mRequestState           = 0;
    private static final int                            REQUEST_DATA_CLEAR      = 0;
    private static final int                            REQUEST_DATA_SEND_START = 1;
    private static final int                            REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                            TIMER_REQUEST           = 500;  // 500msec
    private static final int                            TIMER_NULL              = 0;
    private static final int                            TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    private static final int                            TIMER_OUT_TIME          = 10;   // 10 * 500msec = 5sec

    private int                                         mLoginSuccessFlag;
    private String                                      mLoginLevelCode;
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param savedInstanceState
     * @breif oncreate intro activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_intro);
        /**
         * @breif broadcast intent filter
         */
        registerReceiver();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif service registration
         */
        mLocalConfig = new LocalConfig(getBaseContext());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            check();
        }else{
            NextActivity();
        }

        FingerPushUtil.setFingerPush(this);

    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif onresume
     */
    @Override
    protected void onResume() {
        super.onResume();

        //******************************************************************************************
        /**
         * @breif activity init
         */
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_LOGIN_STATUS) == 1) {
            Intent intent = new Intent(getBaseContext(), HomeTokService.class);
            bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);
        }
        //******************************************************************************************
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif on pause
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_LOGIN_STATUS) == 1) {
            unbindService(requestConnection);

            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mLoginResponse;
            sendMessage(tMsg);
            mLoginRequest = null;
            mLoginBackFlag = false;
            mLoginBackTime = 0;
            TimeHandlerLogin(false, TIMER_NULL);
            TimeHandlerBack(false, TIMER_NULL);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif on distroy
     */
    @Override
    protected void onDestroy() {
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(LoginRunner);
        }

        if (mTimeHandlerBack != null){
            mTimeHandlerBack.removeCallbacks(LoginBackRunner);
        }
        super.onDestroy();
        unregisterReceiver(appReceiver);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLoginRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mLoginResponse;
            sendMessage(tMsg);

            tMsg = Message.obtain();
            tMsg.replyTo = mLoginResponse;
            tMsg.what    = Constants.MSG_WHAT_TIMER_END;

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TIMER_END);
            tMsg.setData(bundle);
            sendMessage(tMsg);

            mRequestState   = REQUEST_DATA_SEND_START;

            TimeHandlerLogin(true, TIMER_REQUEST);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mLoginResponse;
            sendMessage(tMsg);
            mLoginRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST:
                    LoginRequestResult((KDData)msg.obj);
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
            } else if( action.equals(Constants.ACTION_APP_SOCKET_CLOSE)){
            } else if( action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)){
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                Message tMsg = Message.obtain();
                tMsg.replyTo = mLoginResponse;
                tMsg.what    = Constants.MSG_WHAT_TIMER_END;

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TIMER_END);
                tMsg.setData(bundle);
                sendMessage(tMsg);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif send message
     * @param tMsg
     * @brief message send function
     */
    private void sendMessage(Message tMsg) {
        try {
            if(mLoginRequest != null) {
                mLoginRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler back
     * @param tState
     * @param tTime
     */
    private void TimeHandlerBack(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerBack == null){
                mTimeHandlerBack = new Handler();
            }
            mTimeHandlerBack.postDelayed(LoginBackRunner, tTime);
        }else{
            mTimeHandlerBack = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler setup function
     * @param tState
     * @param tTime
     */
    private void TimeHandlerLogin(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(LoginRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif delayrunner
     */
    private Runnable LoginRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    LoginRequest();
                    TimeHandlerLogin(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerLogin(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOKAdmin);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerLogin(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerLogin(false, TIMER_NULL);
            }
        }
    };

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif custom popup admain fail
     */
    private View.OnClickListener mPopupListenerOKAdmin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(intent);
            onBackPressed();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif custom popup certify fail
     */
    private View.OnClickListener mPopupListenerOKCertify = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            Intent intent = new Intent(IntroActivity.this, CertifyActivity.class);
            intent.putExtra(Constants.INTENT_LOGIN_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
            startActivity(intent);
            finish();
        }
    };
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
            Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(intent);
            onBackPressed();
        }
    };

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler back button runner
     */
    private Runnable LoginBackRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerBack != null){
                if(mLoginBackFlag == true){
                    mLoginBackTime++;
                    if(mLoginBackTime >= TIMER_OUT_TIME){
                        mLoginBackFlag = false;
                        mLoginBackTime = 0;
                        TimeHandlerBack(false, TIMER_NULL);
                    }else {
                        TimeHandlerBack(true, TIMER_REQUEST);
                    }
                }else{
                    TimeHandlerBack(false, TIMER_NULL);
                }
            }else{
                TimeHandlerBack(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif login request
     */
    private void LoginRequest(){
        mWaitCount          = 0;
        mLoginSuccessFlag   = 0;
        mRequestState       = REQUEST_DATA_SEND_WAIT;
        TimeHandlerLogin(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        String tdata = "";

        tdata = mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY);

        final Message tMsg = Message.obtain();
        tMsg.replyTo = mLoginResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
                bundle.putString(Constants.KD_DATA_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID).trim());
                bundle.putString(Constants.KD_DATA_PW, mLocalConfig.getStringValue(Constants.SAVE_DATA_PW).trim());
                bundle.putString(Constants.KD_DATA_CERTIFY, mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY));

                tMsg.setData(bundle);
                sendMessage(tMsg);
                mLocalConfig.setValue(Constants.SAVE_DATA_AUTO_ID, 1);
            }
        },1500);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif LOGIN request result
     * @param tKDData
     */
    private void LoginRequestResult(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = 0;
        mProgressDialog.Dismiss();
        TimeHandlerLogin(false, TIMER_NULL);

        LoginParser(tKDData.ReceiveString);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){

            Log.e("LoginActivity","LoginRequestResult : " + tKDData.Result);

            if(mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY) == ""){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                            mPopupListenerOKCertify);
                    mCustomPopup.show();
                }
            } else if(mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID) == ""){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                            mPopupListenerOKCertify);
                    mCustomPopup.show();
                }
            } else if(mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID) != ""){
                String tStringID = mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID).substring(4);
                String tStringCertify = mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY).substring(0, 4);

                if(tStringID.equals(mLocalConfig.getStringValue(Constants.SAVE_DATA_ID))
                        && tStringCertify.equals(mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY))){
                    if(mLoginSuccessFlag == 1){
                        // 인증이 정상적으로 되어 있음. 성공
                        if(mLoginLevelCode.equals("11")){
                            // 관리자 허가 성공
                            Message tMsg = Message.obtain();
                            tMsg.replyTo = mLoginResponse;
                            tMsg.what    = Constants.MSG_WHAT_TIMER_START;

                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
                            tMsg.setData(bundle);
                            sendMessage(tMsg);

                            mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tKDData.Dong);
                            mLocalConfig.setValue(Constants.SAVE_DATA_HO, tKDData.Ho);
                            mLocalConfig.setValue(Constants.SAVE_DATA_LOGIN_STATUS,1);

                            if (getIntent().getExtras() != null){
                                if (getIntent().getExtras().containsKey(Constants.INTENT_TYPE_HOME_VIEW)){
                                    Intent intent = new Intent(IntroActivity.this, SmartHomeviewActivity.class);
                                    intent.putExtra("pushType",getIntent().getStringExtra("pushType"));
                                    intent.putExtra("pushPassword",getIntent().getStringExtra("pushPassword"));
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent = new Intent(IntroActivity.this, MainFragment.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }else{
                                Intent intent = new Intent(IntroActivity.this, MainFragment.class);
                                startActivity(intent);
                                finish();
                            }
                        } else{
                            // 관리자 허가 실패
                            mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tKDData.Dong);
                            mLocalConfig.setValue(Constants.SAVE_DATA_HO, tKDData.Ho);
                            mLocalConfig.setValue(Constants.SAVE_DATA_LOGIN_STATUS,-1);

                            if(mCustomPopup == null) {
                                mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                                        getString(R.string.Login_popup_title_admin), getString(R.string.Login_popup_contents_admin),
                                        mPopupListenerOKAdmin);
                                mCustomPopup.show();
                            }
                        }
                    }else{
                        // 인증이 정상적으로 이루어지지 않았음.
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                    mPopupListenerOKCertify);
                            mCustomPopup.show();
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                mPopupListenerOKCertify);
                        mCustomPopup.show();
                    }
                }
            } else {
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else{

            Log.e("LoginActivity","LoginRequestResult : " + tKDData.Result);

            if(tKDData.Result.equals(Constants.HNML_RESULT_ID_ERROR)){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_id_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }else if(tKDData.Result.equals(Constants.HNML_RESULT_PW_ERROR)){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_pw_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }else if(tKDData.Result.equals(Constants.HNML_RESULT_CERTIFY_CHAR_ERROR)){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                            mPopupListenerOKCertify);
                    mCustomPopup.show();
                }
            }else {
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(IntroActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif login parser
     * @param tContents
     */
    private void LoginParser(String tContents){
        mLoginLevelCode         = new String();
        String  tHomeID         = "";
        boolean tNableInfoFlag  = false;

        if(tContents != null){
            XmlPullParser tParser = Xml.newPullParser();

            try{
                tParser.setInput(new StringReader(tContents));
                int tEventType  = tParser.getEventType();
                String tName    = null;
                String test     = null;

                while(tEventType != XmlPullParser.END_DOCUMENT){
                    String name = tParser.getName();

                    switch(tEventType){
                        case    XmlPullParser.START_TAG:
                            tName = name;

                            if(name.equals("Data")){
                                tName = tParser.getAttributeValue(null, "name");
                                test = tParser.getAttributeValue(null,"use");
                            }else if(name.equals("HomeID")){
                                tName = "HomeID";
                            }
                            break;
                        case    XmlPullParser.TEXT:
                            String tConvert = tParser.getText().trim();

//                            if (test != null && test.equals("Yes")){
//                                Log.i("tag","test : " + tConvert);
//                            }

                            if(tName.equals("PhoneCertify")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mLoginSuccessFlag = 0;
                                }else{
                                    mLoginSuccessFlag = 1;
                                }
                            } else if(tName.equals("LevelCode")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mLoginLevelCode = "";
                                }else{
                                    mLoginLevelCode = tParser.getText();
                                }
                            } else if(tName.equals("Dong")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tParser.getText().trim());
                                }
                            } else if(tName.equals("Ho")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_HO, tParser.getText().trim());
                                }
                            } else if(tName.equals("Smartphone")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_PHONE_NUM, tParser.getText().trim());
                                }
                            } else if(tName.equals("Name")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NAME, tParser.getText().trim());
                                }
                            } else if(tName.equals("local_call_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "local call ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_CALL_IP, tParser.getText().toString());
                                }
                            } else if(tName.equals("local_call_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "local call port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_CALL_PORT, tParser.getText().toString());
                                }
                            } else if(tName.equals("local_stun_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "local stun ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_STUN_IP, tParser.getText().toString());
                                }
                            } else if(tName.equals("local_stun_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "local stun port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_STUN_PORT, tParser.getText().toString());
                                }
                            } else if(tName.equals("call_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "call ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_CALL_IP, tParser.getText().toString());
                                }
                            } else if(tName.equals("call_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "call port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_CALL_PORT, tParser.getText().toString());
                                }
                            } else if(tName.equals("stun_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "stun ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_STUN_IP, tParser.getText().toString());
                                }
                            } else if(tName.equals("stun_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "stun port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_STUN_PORT, tParser.getText().toString());
                                }
                            } else if(tName.equals("domain_name")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "domain name : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_DOMAIN, tParser.getText().toString());
                                }
                            } else if(tName.equals("call_password")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    tNableInfoFlag = true;
                                    Log.e("Login activity", "password : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_PASSWORD, tParser.getText().trim());
                                }
                            }else if(tName.equals("HomeID")){
                                tName = "";
                                tHomeID = tParser.getText();
                            }else if (tName.equals("UsePush")){
                                tName = "";
                                if (tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "UsePush : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_USE_PUSH, tParser.getText().trim());
                                }
                            }else if (tName.equals("PublicIP")){
                                tName = "";
                                if (tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "PublicIP : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_PUBLIC_IP, tParser.getText().trim());
                                }
                            }
                            break;
                        case    XmlPullParser.END_TAG:

                            if (tName.equals("electricity")){
                                Log.e("Login activity", "electricity : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_ELECTRICITY_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_ELECTRICITY_USE, 0);
                                }
                            }else if (tName.equals("gas")){
                                Log.e("Login activity", "gas : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_GAS_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_GAS_USE, 0);
                                }
                            }else if (tName.equals("water")){
                                Log.e("Login activity", "water : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_WATER_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_WATER_USE, 0);
                                }
                            }else if (tName.equals("hotwater")){
                                Log.e("Login activity", "hotwater : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_HOTWATER_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_HOTWATER_USE, 0);
                                }
                            }else if (tName.equals("heating")){
                                Log.e("Login activity", "heating : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_HEATING_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_HEATING_USE, 0);
                                }
                            }else if (tName.equals("cooling")){
                                Log.e("Login activity", "cooling : " + test);
                                if (test.equals("Yes")){
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_COOLING_USE, 1);
                                }else{
                                    mLocalConfig.setValue(Constants.SAVE_DATA_EMS_COOLING_USE, 0);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    tEventType = tParser.next();
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("HNML", "Login Parser Error");
            }

            if(tNableInfoFlag == true){
                mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_USE, "true");
            }else{
                mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_USE, "false");
            }

            if(!tHomeID.equals("")){
                String tData = "";
                tData = tHomeID.substring(4,8);
                mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tData);
                tData = tHomeID.substring(8,12);
                mLocalConfig.setValue(Constants.SAVE_DATA_HO, tData);
            }
        }
    }
    //**********************************************************************************************

    private void NextActivity(){
        mProgressDialog = new CustomProgressDialog(this);

        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_LOGIN_STATUS) == 1){
            Intent intent_response = new Intent(this, HomeTokService.class);
            //            startService(intent_response);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent_response);
            } else {
                startService(intent_response);
            }

            mLoginResponse = new Messenger(responseHandler);
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int tComplexCheck = mLocalConfig.getIntValue(Constants.SAVE_DATA_COMPLEX_CHECK);
                    if (tComplexCheck == 1) {
                        Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_CHECK, 0);
                        Intent intent = new Intent(IntroActivity.this, ComplexActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            },2000);
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif
     */
    public void FinishActivity(){
        Toast.makeText(IntroActivity.this, "권한 사용에 동의하지 않아 APP을 종료 합니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    NextActivity();
                } else {
                    FinishActivity();
                }
                break;
            default:
                break;
        }
    }

    //**********************************************************************************************
    /**
     * @breif permission check
     */
    private void check() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE}, 1234);

        }else{
            NextActivity();
        }
    }
}
