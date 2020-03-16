package com.kd.One.Setup;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;

public class PushSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Messenger mPushSettingResponse = null;
    private Messenger mPushSettingRequest = null;
    LinearLayout mImgBack;
    TextView                mTxtSave;
    Switch                  mSwhEmergency;
    Switch                  mSwhDelivery;
    Switch                  mSwhParking;
    Switch                  mSwhCall;
    private                 LocalConfig mLocalConfig;

    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    private Handler                                 mTimeHandler;
    private int                                     mWaitCount              = 0;

    private int                                     mDataSendFlag           = 0;

    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1000;  // 1500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 10;   // 20 * 500msec = 10sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#fafafa"));
            }
        }
        setContentView(R.layout.activity_push_setting);

        mLocalConfig = new LocalConfig(getBaseContext());

        mProgressDialog = new CustomProgressDialog(this);

        FindViewById();

        registerReceiver();

        Intent intent_response = new Intent(this, HomeTokService.class);
        startService(intent_response);
        mPushSettingResponse = new Messenger(responseHandler);

    }

    void FindViewById(){
        mImgBack             = (LinearLayout) findViewById(R.id.Push_Setting_Lin_Back);
        mTxtSave             = (TextView) findViewById(R.id.Push_Setting_Txt_Save);
        mSwhEmergency        = (Switch)findViewById(R.id.Push_Setting_Swh_Emergency);
        mSwhDelivery         = (Switch)findViewById(R.id.Push_Setting_Swh_Delivery);
        mSwhParking          = (Switch)findViewById(R.id.Push_Setting_Swh_Parking);
        mSwhCall             = (Switch)findViewById(R.id.Push_Setting_Swh_Call);

        mImgBack.setOnClickListener(this);
        mTxtSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Push_Setting_Lin_Back:
                onBackPressed();
                break;
            case R.id.Push_Setting_Txt_Save:
                PushSettingEachControlRequest();
                break;
            default:
                break;
        }
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

        mProgressDialog.Show(getString(R.string.progress_request));
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
        tMsg.replyTo = mPushSettingResponse;
        sendMessage(tMsg);
        mDataSendFlag = 0;
        mPushSettingRequest = null;
        TimeHandlerPushSetting(false, TIMER_NULL);

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
        responseHandler = null;
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
            mPushSettingRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mPushSettingResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerPushSetting(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mPushSettingResponse;
            sendMessage(tMsg);
            mPushSettingRequest = null;
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
                case    Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST:
                    PushSettingStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST:
                    PushSettingControlResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mPushSettingRequest, mPushSettingResponse, PushSettingActivity.this);
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
            if(mPushSettingRequest != null) {
                mPushSettingRequest.send(tMsg);
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
    private void TimeHandlerPushSetting(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(PushSettingRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif delayrunner
     */
    private Runnable PushSettingRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    PushSettingStateRequest();
                    TimeHandlerPushSetting(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerPushSetting(false, TIMER_NULL);
                        PushSettingSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(PushSettingActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerPushSetting(true, TIMER_REQUEST);
                    }
                }

                if(mDataSendFlag == 1){
                    PushSettingStateRequest();
                    mDataSendFlag = 0;
                }
            }else{
                TimeHandlerPushSetting(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif ventilation socket close
     */
    private void PushSettingSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mPushSettingResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************


    //**********************************************************************************************
    /**
     *  @breif power state request
     */
    public void PushSettingStateRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mPushSettingResponse;
        tMsg.what    = Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST);
        bundle.putString(Constants.KD_DATA_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif ventilation each control request communication
     * @param tNum
     */
    public void PushSettingEachControlRequest(){
        mWaitCount              = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerPushSetting(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mPushSettingResponse;
        tMsg.what    = Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST);
        bundle.putString(Constants.KD_DATA_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
        if (mSwhEmergency.isChecked()){
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_EMERGENCY, "Y");
        }else{
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_EMERGENCY, "N");
        }
        if (mSwhParking.isChecked()){
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_CAR_INOUT, "Y");
        }else{
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_CAR_INOUT, "N");
        }
        if (mSwhDelivery.isChecked()){
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_DELIVERY, "Y");
        }else{
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_DELIVERY, "N");
        }
        if (mSwhCall.isChecked()){
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_SIP, "Y");
        }else{
            bundle.putString(Constants.KD_DATA_PUSH_SETTING_SIP, "N");
        }
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif VENTIALTION state request result
     * @param tKDData
     */
    private void PushSettingStateResult(KDData tKDData){
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserPushSetting(tKDData.ReceiveString);
                mDataSendFlag = 0;
                mWaitCount = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                mProgressDialog.Dismiss();

                TimeHandlerPushSetting(false, TIMER_NULL);
            } else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mProgressDialog.Dismiss();
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerPushSetting(false, TIMER_NULL);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(PushSettingActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mDataSendFlag = 0;
            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerPushSetting(false, TIMER_NULL);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Ventilation each & group control request result
     * @param tKDData
     */
    private void PushSettingControlResult(KDData tKDData){
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                mDataSendFlag = 1;
            } else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mProgressDialog.Dismiss();
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerPushSetting(false, TIMER_NULL);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(PushSettingActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mDataSendFlag = 0;
            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerPushSetting(false, TIMER_NULL);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button
     */
    @Override
    public void onBackPressed(){
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

//            Intent intent = new Intent(VentilationActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif hnml data parser Stand by power
     * @param String tContents
     */
    public void HNMLDataParserPushSetting(String tContents) {
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

                            if(tName.equals("emergency_push")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mSwhEmergency.setChecked(false);
                                }else{
                                    if (tConvert.equals("Y")){
                                        mSwhEmergency.setChecked(true);
                                    }else{
                                        mSwhEmergency.setChecked(false);
                                    }
                                }
                            }else if(tName.equals("car_inout_push")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mSwhParking.setChecked(false);
                                }else{
                                    if (tConvert.equals("Y")){
                                        mSwhParking.setChecked(true);
                                    }else{
                                        mSwhParking.setChecked(false);
                                    }
                                }
                            }else if(tName.equals("delivery_push")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mSwhDelivery.setChecked(false);
                                }else{
                                    if (tConvert.equals("Y")){
                                        mSwhDelivery.setChecked(true);
                                    }else{
                                        mSwhDelivery.setChecked(false);
                                    }
                                }
                            }else if(tName.equals("sip_push")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mSwhCall.setChecked(false);
                                }else{
                                    if (tConvert.equals("Y")){
                                        mSwhCall.setChecked(true);
                                    }else{
                                        mSwhCall.setChecked(false);
                                    }
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
