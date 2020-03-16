package com.kd.One.Login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

/**
 * Created by lwg on 2016-07-12.
 */
public class LoginActivity extends Activity {
    //**********************************************************************************************
    private Messenger                                   mLoginResponse        = null;
    private Messenger                                   mLoginRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    private LocalConfig mLocalConfig;
    private CustomProgressDialog mProgressDialog;
    private CustomPopupBasic mCustomPopup;
    private Handler                                     mTimeHandler;
    private Handler                                     mTimeHandlerBack;
    //**********************************************************************************************

    //**********************************************************************************************
    private EditText                                    mLoginEditTextID;
    private EditText                                    mLoginEditTextPW;
    private LinearLayout                                mLoginLayout;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                         mLoginAutoFlag;
    private int                                         mLoginSuccessFlag;
    private String                                      mLoginLevelCode;
    private int                                         mLoginBackTime;
    private boolean                                     mLoginBackFlag;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                         mWaitCount              = 0;
    private int                                         mRequestState           = 0;
    private static final int                            REQUEST_DATA_CLEAR      = 0;
    private static final int                            REQUEST_DATA_SEND_START = 1;
    private static final int                            REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                            TIMER_REQUEST           = 500;  // 500msec
    private static final int                            TIMER_NULL              = 0;
    private static final int                            TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    private static final int                            TIMER_OUT_TIME          = 10;   // 10 * 500msec = 5sec
    //**********************************************************************************************

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
        setContentView(R.layout.activity_login_login);

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
        mLoginResponse = new Messenger(responseHandler);
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
        mLoginEditTextID        = (EditText)findViewById(R.id.Login_EditText_ID);
        mLoginEditTextPW        = (EditText)findViewById(R.id.Login_EditText_PW);
        mLoginLayout            = (LinearLayout)findViewById(R.id.Login_Layout);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif intent data receive check
         */
        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey(Constants.INTENT_TYPE_CERTIFY)){
                mLoginAutoFlag = getIntent().getExtras().getInt(Constants.INTENT_TYPE_CERTIFY);
                mLocalConfig.setValue(Constants.SAVE_DATA_AUTO_ID, 1);
                mLoginEditTextID.setText(mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
                mLoginEditTextPW.setText(mLocalConfig.getStringValue(Constants.SAVE_DATA_PW));
                mProgressDialog.Show(getString(R.string.progress_request));
            } else if(getIntent().getExtras().containsKey(Constants.INTENT_TIMEOUT)){
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_timeout),
                            mPopupListenerOKTimeOut);
                    mCustomPopup.show();
                }
            }else{
                mLoginAutoFlag = 0;
            }
        }

        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity edittext filter
         */
        mLoginEditTextID.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        mLoginEditTextPW.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        //******************************************************************************************


        /**
         * @breif login data setting
         * */
        //MARK - BYS START - 2020-01-03
        /**
         * 로그인시 회원가입화면에서 넘어왔을 경우 아이디 비번 세팅 추가
         * */
        if (getIntent().getExtras() != null){
            boolean doRegister = getIntent().getExtras().getBoolean(Constants.SAVE_SATUS_DOING_REGISTER);
            if (doRegister){
                String loginId = mLocalConfig.getStringValue(Constants.SAVE_DATA_ID);
                String loginPw = mLocalConfig.getStringValue(Constants.SAVE_DATA_PW);
                if (!loginId.isEmpty() && !loginPw.isEmpty()){
                    mLoginEditTextID.setText(loginId);
                    mLoginEditTextPW.setText(loginPw);
//                    Log.e("!!!!!!!!!!","LoginActivity loginId : " + loginId);
//                    Log.e("!!!!!!!!!!","LoginActivity loginPw : " + loginPw);
                }
            }
        }
        //MARK - BYS END


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

        //******************************************************************************************
        /**
         * @breif keyboard on status -> keyboard outside touch keyboard off
         */
        mLoginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mLoginEditTextID.getWindowToken(), 0);
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

        Log.e("LoginAcivity", "Pause");
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
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif destroy operating
     */
    public void onDestroy() {
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

            if(mLoginAutoFlag == 1){
                mWaitCount      = 0;
                mRequestState   = REQUEST_DATA_SEND_START;
                TimeHandlerLogin(true, TIMER_REQUEST);
            }
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
                            mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
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

        Message tMsg = Message.obtain();
        tMsg.replyTo = mLoginResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_ID, mLoginEditTextID.getText().toString().trim());
        bundle.putString(Constants.KD_DATA_PW, mLoginEditTextPW.getText().toString().trim());
        bundle.putString(Constants.KD_DATA_CERTIFY, mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY));

        tMsg.setData(bundle);
        sendMessage(tMsg);

        mLocalConfig.setValue(Constants.SAVE_DATA_AUTO_ID, 1);
        mLocalConfig.setValue(Constants.SAVE_DATA_ID, mLoginEditTextID.getText().toString().trim());
        mLocalConfig.setValue(Constants.SAVE_DATA_PW, mLoginEditTextPW.getText().toString().trim());
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

        if(tKDData != null) {
            LoginParser(tKDData.ReceiveString);

            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {

                Log.e("LoginActivity", "LoginRequestResult : " + tKDData.Result);

                if (mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY) == "") {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                mPopupListenerOKCertify);
                        mCustomPopup.show();
                    }
                } else if (mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID) == "") {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                mPopupListenerOKCertify);
                        mCustomPopup.show();
                    }
                } else if (mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID) != "") {
                    String tStringID = mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY_ID).substring(4);
                    String tStringCertify = mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY).substring(0, 4);

                    if (tStringID.equals(mLocalConfig.getStringValue(Constants.SAVE_DATA_ID))
                            && tStringCertify.equals(mLocalConfig.getStringValue(Constants.SAVE_DATA_CERTIFY))) {
                        if (mLoginSuccessFlag == 1) {
                            // 인증이 정상적으로 되어 있음. 성공
                            if (mLoginLevelCode.equals("11")) {
                                // 관리자 허가 성공
                                Message tMsg = Message.obtain();
                                tMsg.replyTo = mLoginResponse;
                                tMsg.what = Constants.MSG_WHAT_TIMER_START;

                                Bundle bundle = new Bundle();
                                bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
                                tMsg.setData(bundle);
                                sendMessage(tMsg);

                                mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tKDData.Dong);
                                mLocalConfig.setValue(Constants.SAVE_DATA_HO, tKDData.Ho);
                                mLocalConfig.setValue(Constants.SAVE_DATA_LOGIN_STATUS, 1);

                                Intent intent = new Intent(LoginActivity.this, MainFragment.class);

                                startActivity(intent);
                                finish();
                            } else {
                                // 관리자 허가 실패
                                mLocalConfig.setValue(Constants.SAVE_DATA_DONG, tKDData.Dong);
                                mLocalConfig.setValue(Constants.SAVE_DATA_HO, tKDData.Ho);
                                mLocalConfig.setValue(Constants.SAVE_DATA_LOGIN_STATUS, -1);

                                if (mCustomPopup == null) {
                                    mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                            getString(R.string.Login_popup_title_admin), getString(R.string.Login_popup_contents_admin),
                                            mPopupListenerOKAdmin);
                                    mCustomPopup.show();
                                }
                            }
                        } else {
                            // 인증이 정상적으로 이루어지지 않았음.
                            if (mCustomPopup == null) {
                                mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                        getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                        mPopupListenerOKCertify);
                                mCustomPopup.show();
                            }
                        }
                    } else {
                        if (mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                    mPopupListenerOKCertify);
                            mCustomPopup.show();
                        }
                    }
                } else {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_fail),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            } else {

                Log.e("LoginActivity", "LoginRequestResult : " + tKDData.Result);

                if (tKDData.Result.equals(Constants.HNML_RESULT_ID_ERROR)) {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_id_fail),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else if (tKDData.Result.equals(Constants.HNML_RESULT_PW_ERROR)) {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_pw_fail),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else if (tKDData.Result.equals(Constants.HNML_RESULT_CERTIFY_CHAR_ERROR)) {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_certify), getString(R.string.Login_popup_contents_certify),
                                mPopupListenerOKCertify);
                        mCustomPopup.show();
                    }
                } else {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_fail),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup timeout ok button
     */
    private View.OnClickListener mPopupListenerOKTimeOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
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
     * @breif custom popup certify fail
     */
    private View.OnClickListener mPopupListenerOKCertify = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            Intent intent = new Intent(LoginActivity.this, CertifyActivity.class);
            intent.putExtra(Constants.INTENT_LOGIN_ID, mLoginEditTextID.getText().toString());
            startActivity(intent);
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
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button
     */
    public void onBackPressed(){
        if(mLoginBackFlag == false){
            mLoginBackFlag = true;
            mLoginBackTime = 0;
            TimeHandlerBack(true, TIMER_REQUEST);
            Toast.makeText(this, getString(R.string.Login_toast_out), Toast.LENGTH_SHORT).show();
        }else{
            finish();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif login activity button
     * @param v
     */
    public void OnClickBtnLogin(View v){
        switch (v.getId()){
            case    R.id.Login_Txt_Personal_Info:
                Uri uri = Uri.parse("https://privacy.naviensmartcontrol.com/docs/view?serviceCode=20&docType=P");

                Intent intentUri = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intentUri);
                break;
            case    R.id.Login_Lin_Back:
                mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_CHECK, 0);
                Intent intent = new Intent(LoginActivity.this, ComplexActivity.class);
                startActivity(intent);
                finish();
                break;
            case    R.id.Login_Txt_IDFind:
                Intent intentfind = new Intent(LoginActivity.this, FindActivity.class);
                startActivity(intentfind);
                break;
            case    R.id.Login_Btn_Registration:
                Intent intentAgree = new Intent(LoginActivity.this, AgreeCheckActivity.class);
                startActivity(intentAgree);
                break;
            case    R.id.Login_Btn_Login:
                if(mLoginEditTextID.getText().length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_id_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else if(mLoginEditTextPW.getText().length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LoginActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Login_popup_title_fail), getString(R.string.Login_popup_pw_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else {
                    //******************************************************************************************
                    /**
                     * @breif keyboard on status -> keyboard outside touch keyboard off
                     */
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mLoginEditTextID.getWindowToken(), 0);
                    //******************************************************************************************
                    LoginRequest();
                }
                break;
            default:
                break;
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
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_STUN_IP, tParser.getText().trim());
                                }
                            } else if(tName.equals("local_stun_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "local stun port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_LOCAL_STUN_PORT, tParser.getText().trim());
                                }
                            } else if(tName.equals("call_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "call ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_CALL_IP, tParser.getText().trim());
                                }
                            } else if(tName.equals("call_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "call port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_CALL_PORT, tParser.getText().trim());
                                }
                            } else if(tName.equals("stun_ip")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "stun ip : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_STUN_IP, tParser.getText().trim());
                                }
                            } else if(tName.equals("stun_port")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "stun port : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_STUN_PORT, tParser.getText().trim());
                                }
                            } else if(tName.equals("domain_name")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    Log.e("Login activity", "domain name : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_DOMAIN, tParser.getText().trim());
                                }
                            } else if(tName.equals("call_password")){
                                tName = "";
                                if(tConvert.length() == 0){

                                }else{
                                    tNableInfoFlag = true;
                                    Log.e("Login activity", "call_password : " + tParser.getText().toString());
                                    mLocalConfig.setValue(Constants.SAVE_DATA_NABLE_PASSWORD, tParser.getText().trim());
                                }
                            }else if(tName.equals("HomeID")){
                                tName = "";
                                tHomeID = tParser.getText();
                            } else if (tName.equals("UsePush")){
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
}
