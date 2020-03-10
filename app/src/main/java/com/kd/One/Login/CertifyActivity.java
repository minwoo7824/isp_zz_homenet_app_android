package com.kd.One.Login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.InputFilter;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

/**
 * Created by lwg on 2016-07-12.
 */
public class CertifyActivity extends Activity {
    //**********************************************************************************************
    private Messenger                       mCertifyRequest  = null;
    private Messenger                       mCertifyResponse = null;
    //**********************************************************************************************

    //**********************************************************************************************
    private LocalConfig                     mLocalConfig;
    private Handler                         mTimeHandler;
    private CustomProgressDialog            mProgressDialog;
    private CustomPopupBasic                mCustomPopup;
    //**********************************************************************************************

    //**********************************************************************************************
    private LinearLayout                    mCertifyLayout;
    private EditText                        mCertifyEditText;
    private Button                          mCertifyBtnWithdrawal;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                             mWaitCount              = 0;
    private int                             mRequestState           = 0;
    private static final int                REQUEST_DATA_CLEAR      = 0;
    private static final int                REQUEST_DATA_SEND_START = 1;
    private static final int                REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                TIMER_REQUEST           = 500;  // 500msec
    private static final int                TIMER_NULL              = 0;
    private static final int                TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************
    private String                          mCertifyID              = "";
    private String                          mCertifyNable           = "";
    private static final String             NABLE_TRUE              = "true";
    private static final String             NABLE_FALSE             = "false";
    //**********************************************************************************************
//    private boolean                         mIsSignUpFlag           = false;
    //**********************************************************************************************
    /**
     * @breif oncreate intro activity
     * @param savedInstanceState
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
        setContentView(R.layout.activity_login_certify);

        //******************************************************************************************
        /**
         * @breif broadcast intent filter
         */
        registerReceiver();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif service bind
         */
        Intent intent_response = new Intent(this, HomeTokService.class);
        startService(intent_response);
        mCertifyResponse = new Messenger(responseHandler);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getBaseContext());
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif progress dialog create
         */
        mProgressDialog = new CustomProgressDialog(this);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity find action
         */
        mCertifyLayout               = (LinearLayout)findViewById(R.id.Certify_Layout);
        mCertifyEditText             = (EditText)findViewById(R.id.Certify_EditText_Certify);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity edittext filter
         */
        //mCertifyEditText.setFocusableInTouchMode(false);
        mCertifyEditText.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        /*mCertifyBtnWithdrawal.setText(Html.fromHtml("<u>"+getString(R.string.Certify_btn_withdrawal)+"</u>"));*/
        //******************************************************************************************

        if(getIntent().getExtras().containsKey(Constants.INTENT_LOGIN_ID)){
            mCertifyID = getIntent().getExtras().getString(Constants.INTENT_LOGIN_ID);
        }

    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();

        //mProgressDialog.Show(getString(R.string.Certify_progress_certify));

        //******************************************************************************************
        /**
         * @breif service bind
         */
        Intent intent = new Intent(getBaseContext(), HomeTokService.class);
        bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif keyboard on status -> keyboard outside touch keyboard off
         */
        mCertifyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mCertifyEditText.getWindowToken(), 0);
            }
        });
        //******************************************************************************************
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
        tMsg.replyTo = mCertifyResponse;
        sendMessage(tMsg);
        mCertifyRequest = null;

        mProgressDialog.Dismiss();
        TimeHandlerCertify(false, TIMER_NULL);

        if(mCustomPopup != null){
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
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(CertifyRunner);
        }
        super.onDestroy();
        unregisterReceiver(appReceiver);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCertifyRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mCertifyResponse;
            sendMessage(tMsg);

            //mWaitCount      = 0;
            //mRequestState   = REQUEST_DATA_SEND_START;
            //TimeHandlerCertify(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mCertifyResponse;
            sendMessage(tMsg);
            mCertifyRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_CERTIFY_REQUEST:
                    CertifyResponse((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION:
                    AuthenticationResponse((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO:
                    DeviceInfoResponse((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_LOGIN_WITHDRAWAL:
                    WithdrawalResult((KDData)msg.obj);
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
            if(mCertifyRequest != null) {
                mCertifyRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler setup function
     * @param tState
     * @param tTime
     */
    private void TimeHandlerCertify(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(CertifyRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable CertifyRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    CertifyRequest();
                    TimeHandlerCertify(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerCertify(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_request_fail),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerCertify(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerCertify(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif certify number request
     */
    private void CertifyRequest(){
        mProgressDialog.Show(getString(R.string.progress_request));
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerCertify(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mCertifyResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_CERTIFY_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_ID, mCertifyID);

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif authentication request
     */
    private void AuthenticationRequest(){
        mProgressDialog.Show(getString(R.string.progress_request));
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerCertify(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mCertifyResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_CERTIFY, mCertifyEditText.getText().toString().trim().toUpperCase());
        bundle.putString(Constants.KD_DATA_ID, mCertifyID);

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif certify response
      * @param tKDData
     */
    private void CertifyResponse(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        mProgressDialog.Dismiss();
        TimeHandlerCertify(false, TIMER_NULL);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            //mCertifyEditText.setFocusableInTouchMode(true);
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_request_success),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }else{
            //mCertifyEditText.setFocusableInTouchMode(false);
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_request_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif 인증 요청
     * @param tKDData
     */
    private void AuthenticationResponse(KDData tKDData){
        CertifyResponseParser(tKDData.ReceiveString);
        if(mCertifyNable.equals(NABLE_TRUE)){
            mWaitCount      = 0;
            DeviceInfoRequest();
        }else {
            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_CLEAR;
            mProgressDialog.Dismiss();
            TimeHandlerCertify(false, TIMER_NULL);

            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                mLocalConfig.setValue(Constants.SAVE_DATA_ID, mCertifyID);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_success),
                            mPopupListenerSuccess);
                    mCustomPopup.show();
                }
            } else {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @berif device info push key & information request
     */
    private void DeviceInfoRequest(){
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerCertify(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mCertifyResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_ID, mCertifyID);
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_ID, getDeviceID());
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_TYPE, "1");  // 1 : mobile, 2 : pc, 3 : pad, 4 : ip phone, 5 : etc
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_OS, "1");    // 1 : Adnroid, 2 : ios, 3 : windows, 4 : etc
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_OSVER, getSDKVersion());
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_APPVER, getAppVersion());
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_MODEL, getDeviceName());
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_PUSH_TYPE, "gcm");   // apns : ios, gcm : android
        bundle.putString(Constants.KD_DATA_DEVICE_INFO_PUSH_KEY, mLocalConfig.getStringValue(Constants.SAVE_DATA_TOKEN));

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif device info response
     * @param tKDData
     */
    private void DeviceInfoResponse(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        mProgressDialog.Dismiss();
        TimeHandlerCertify(false, TIMER_NULL);

        if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
            mLocalConfig.setValue(Constants.SAVE_DATA_ID, mCertifyID);
            if (mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_success),
                        mPopupListenerSuccess);
                mCustomPopup.show();
            }
        } else {
            if (mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok button
     */
    private View.OnClickListener mPopupListenerSuccess = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            mLocalConfig.setValue(Constants.SAVE_DATA_CERTIFY_ID, mCertifyEditText.getText().toString().trim().toUpperCase()+mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
            mLocalConfig.setValue(Constants.SAVE_DATA_CERTIFY, mCertifyEditText.getText().toString().trim().toUpperCase());
//            onBackPressed();

            Intent intent = new Intent(CertifyActivity.this, LoginActivity.class);
            //MARK BYS START - 2020-01-03
            intent.putExtra(Constants.SAVE_SATUS_DOING_REGISTER, true);
            //MARK BYS END
            startActivity(intent);
            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup withdrawal ok button
     */
    private View.OnClickListener mPopupListenerWithdrawalOK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mCustomPopup != null){
                mCustomPopup.dismiss();
                mCustomPopup = null;
            }

            WithdrawalRequest();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok mPopupListenerWithdrawalSuccess
     */
    private View.OnClickListener mPopupListenerWithdrawalSuccess = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mLocalConfig.setValue(Constants.SAVE_DATA_ID, "");
            mLocalConfig.setValue(Constants.SAVE_DATA_AUTO_ID, 0);
            mLocalConfig.setValue(Constants.KD_DATA_CERTIFY, "");
            Intent intent = new Intent(CertifyActivity.this, LoginActivity.class);
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
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button select
     */
    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(CertifyActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif certify button
     * @param v
     */
    public void OnClickBtnCertify(View v){
        switch(v.getId()){
            case    R.id.Certify_Lin_Back:
                Intent intent = new Intent(CertifyActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case    R.id.Certify_Btn_Retry:
                CertifyRequest();
                break;
            case    R.id.Certify_Btn_Complete:
                if(mCertifyEditText.getText().length() == 4){
                    AuthenticationRequest();
                }else{
                    if(mCertifyEditText.getText().length() == 0){
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_number_fail),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else {
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Certify_popup_title), getString(R.string.Certify_popup_certify_number_over_fail),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif certify response parser
     */
    private void CertifyResponseParser(String tContents){
        if(tContents != null){
            XmlPullParser tParser = Xml.newPullParser();

            try{
                tParser.setInput(new StringReader(tContents));
                int     tEvent  = tParser.getEventType();
                String  tName   = null;

                while (tEvent != XmlPullParser.END_DOCUMENT){

                    String name = tParser.getName();

                    switch(tEvent){
                        case    XmlPullParser.START_TAG:
                            tName = name;
                            break;
                        case    XmlPullParser.TEXT:
                            if(tName.equals("Nable")){
                                tName = "";
                                mCertifyNable = tParser.getText().toString();
                            }
                            break;
                        case    XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }

                    tEvent = tParser.next();
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("Certify", "Certify Response Parser : " + e.getMessage());
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif withdrawal request
     */
    private void WithdrawalRequest(){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerCertify(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mCertifyResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_WITHDRAWAL;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_LOGIN_WITHDRAWAL);
        bundle.putString(Constants.KD_DATA_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif withdrawal result
     * @param tKDData
     */
    private void WithdrawalResult(KDData tKDData){
        mWaitCount = 0;
        mProgressDialog.Dismiss();
        TimeHandlerCertify(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_withdrawal_title), getString(R.string.Certify_popup_withdrawal_success),
                        mPopupListenerWithdrawalSuccess);
                mCustomPopup.show();
            }
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(CertifyActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Certify_popup_withdrawal_title), getString(R.string.Certify_popup_withdrawal_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif serial key device id
     * @return
     */
    private String getDeviceID(){
        String tDeviceID = "";
        try{
            tDeviceID = (String) Build.class.getField("SERIAL").get(null);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Device ID", "Exception : " + e.getMessage());
        }

        return tDeviceID;
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif get skd version
     * @return
     */
    private String getSDKVersion(){
        int    tSdkVersion = 0;

        tSdkVersion = Build.VERSION.SDK_INT;

        return String.valueOf(tSdkVersion);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * get application version
     * @return
     */
    private String getAppVersion(){
        String myVersion = "";

        myVersion = Build.VERSION.RELEASE;

        return myVersion;
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif getdevice name
     * @return
     */
    private String  getDeviceName(){
        String tDeviceName = "";

        tDeviceName = Build.MODEL;

        return tDeviceName;
    }
    //**********************************************************************************************
}
