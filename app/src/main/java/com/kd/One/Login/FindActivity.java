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
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

/**
 * Created by lwg on 2016-09-07.
 */
public class FindActivity extends Activity {
    //**********************************************************************************************
    private Messenger                       mFindRequest  = null;
    private Messenger                       mFindResponse = null;
    //**********************************************************************************************

    //**********************************************************************************************
    private LocalConfig                     mLocalConfig;
    private Handler                         mTimeHandler;
    private CustomProgressDialog            mProgressDialog;
    private CustomPopupBasic                mCustomPopup;
    //**********************************************************************************************

    private EditText                        mEditTextName;
    private EditText                        mEditTextPhoneNumber;
    private LinearLayout                    mLinearLayoutFind;

    private String                          mName;
    private String                          mPhoneNum;
    private String                          mID;

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
        setContentView(R.layout.activity_login_findid);

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
        mFindResponse = new Messenger(responseHandler);
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
        mEditTextName           = (EditText)findViewById(R.id.Find_EditText_Name);
        mEditTextPhoneNumber    = (EditText)findViewById(R.id.Find_EditText_Phone);
        mLinearLayoutFind       = (LinearLayout)findViewById(R.id.Find_LinearLayout);
        //******************************************************************************************

        //******************************************************************************************
        mName = "";
        mPhoneNum = "";
        mID = "";
        //******************************************************************************************
    }

    //**********************************************************************************************
    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();

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
        mLinearLayoutFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextName.getWindowToken(), 0);
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
        tMsg.replyTo = mFindResponse;
        sendMessage(tMsg);
        mFindRequest = null;

        mProgressDialog.Dismiss();
        TimeHandlerFind(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(FindRunner);
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
            mFindRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mFindResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mFindResponse;
            sendMessage(tMsg);
            mFindRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_FIND_ID:
                    FindResult((KDData)msg.obj);
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
            if(mFindRequest != null) {
                mFindRequest.send(tMsg);
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
    private void TimeHandlerFind(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(FindRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable FindRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    TimeHandlerFind(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerFind(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(FindActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerFind(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerFind(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Find id number request
     */
    private void FindRequest(){
        mProgressDialog.Show(getString(R.string.progress_request));
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerFind(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mFindResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_FIND_ID;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_LOGIN_FIND_ID);
        bundle.putString(Constants.KD_DATA_NAME, mName);
        bundle.putString(Constants.KD_DATA_CELLPHONENUM, mPhoneNum);

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif 인증 요청
     * @param tKDData
     */
    private void FindResult(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        mProgressDialog.Dismiss();
        TimeHandlerFind(false, TIMER_NULL);

        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserFindID(tKDData.ReceiveString);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(FindActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Find_popup_title), getString(R.string.Find_popup_success) + "\n" + mID +
                            getString(R.string.Find_popup_success_1),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            } else {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(FindActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Find_popup_title), getString(R.string.Find_popup_fail),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
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
            if (mID.length() != 0){
                onBackPressed();
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button select
     */
    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(FindActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif btn find
     * @param v
     */
    public void OnClickBtnFind(View v){
        switch (v.getId()){
            case    R.id.Find_Lin_Back:
                onBackPressed();
                break;
            case    R.id.Find_Btn_Complete:
                mPhoneNum = mEditTextPhoneNumber.getText().toString().trim();
                mName = mEditTextName.getText().toString().trim();

                if(mName.contains("ip:") || mName.contains("IP:")){
                    if(mName.contains("ip:")) {
                        String tIP = mName.replace("ip:", "");
                        mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_IP, tIP);
                        Toast.makeText(this, "숨은 기능 : 아이피 변경 되었습니다", Toast.LENGTH_SHORT).show();
                    }else if(mName.contains("IP:")){
                        String tIP = mName.replace("IP:", "");
                        mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_IP, tIP);
                        Toast.makeText(this, "숨은 기능 : 아이피 변경 되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(this, "숨은 기능 : 잘못 입력했습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(mName.length() < 1){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(FindActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Find_popup_title), getString(R.string.Find_popup_id_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else if(mPhoneNum.length() < 1){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(FindActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Find_popup_title), getString(R.string.Find_popup_phone_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else{
                    FindRequest();
                }
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    public void HNMLDataParserFindID(String tContents){

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

                            if (tName.equals("UserID")) {
                                tName = "";
                                if (tConvert.length() == 0) {
                                    mID = "";
                                } else {
                                    mID = tParser.getText();
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
        }
    }
}
