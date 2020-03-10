package com.kd.One.Control;

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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-25.
 */
public class StandbypowerActivity extends Activity{
    private String TAG = "StandbypowerActivity";
    //**********************************************************************************************
    private Messenger                               mStandbypowerResponse               = null;
    private Messenger                               mStandbypowerRequest                = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig mLocalConfig;
    public MyGlobal mMyGlobal;
    private Handler                                 mTimeHandler;
    private Handler                                 mTimeHandlerGroup;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String>                       mArrayGroupID;
    private ArrayList<String>                       mArraySubID;
    private ArrayList<String>                       mArrayName;
    private ArrayList<String>                       mArrayAutoBlockSettingValue;
    private ArrayList<String>                       mArrayCurrentUsage;
    private ArrayList<String>                       mArrayAutoBlock;
    private ArrayList<String>                       mArrayBlockCriteria;
    private ArrayList<String>                       mArrayOverload;
    private ArrayList<String>                       mArrayPowerSupply;
    //**********************************************************************************************

    //**********************************************************************************************
    private SimpleSideDrawer mSlideDrawer;
    private ViewGroup                               mViewGroupHeat;
    private ViewGroup                               mViewGroupGas;
    private ViewGroup                               mViewGroupLight;
    private ViewGroup                               mViewGroupVentilation;
    private ViewGroup                               mViewGroupPower;
    private ViewGroup                               mViewGroupShutdown;
    private ViewGroup                               mViewGroupHome;
    private ViewGroup                               mViewGroupHomeView;
    private ViewGroup                               mViewGroupEMS;
    private ViewGroup                               mViewGroupVisitor;
    private ViewGroup                               mViewGroupNotice;
    private boolean                                 mIsSlideOpen;
    //**********************************************************************************************

    //**********************************************************************************************
    private LinearLayout                            mStandByPowerListParent;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mStandbypowerPosition;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitGroupCount                     = 0;
    private int                                     mWaitCount                          = 0;
    private int                                     mRequestState                       = 0;
    private static final int                        REQUEST_DATA_CLEAR                  = 0;
    private static final int                        REQUEST_DATA_SEND_START             = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT              = 2;

    private static final int                        TIMER_REQUEST                       = 1000;  // 500msec
    private static final int                        TIMER_NULL                          = 0;
    private static final int                        TIMER_WAIT_TIME                     = 20;   // 40 * 500msec = 22.5sec
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     STANDBYPOWER_DATA_NAME              = "DeviceName";
    private static final String                     STANDBYPOWER_DATA_AUTOBLOCKSTTING   = "AutoBlockSetting";
    private static final String                     STANDBYPOWER_DATA_CURRENTUSAGE      = "CurrentUsage";
    private static final String                     STANDBYPOWER_DATA_AUTOBLOCK         = "AutoBlock";
    private static final String                     STANDBYPOWER_DATA_BLOCKCRITERIA     = "BlockCriteria";
    private static final String                     STANDBYPOWER_DATA_OVERLOAD          = "Overload";
    private static final String                     STANDBYPOWER_DATA_POWRSUPPLY        = "PowerSupply";
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     STANDBYPOWER_AUTO_BLOCK             = "AUTOBLOCK";
    private static final String                     STANDBYPOWER_STATE                  = "STATE";
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     STANDBYPOWER_ON                     = "On";
    private static final String                     STANDBYPOWER_OFF                    = "Off";
    //**********************************************************************************************

    private int                                     mStandbypowerEachGroupFlag          = 0;    // 1 : each 2 : group
    private String                                  mStandbypowerDataSend;
    private String                                  mStandbypowerData;

    private int                                     mDataSendFlag = 0;

    private ArrayList<LinearLayout> parentLinViews = new ArrayList<>();
    private ArrayList<TextView> titleTxtViews = new ArrayList<>();
    private ArrayList<Switch> controlSwhViews = new ArrayList<>();
    private ArrayList<LinearLayout> visibleLinViews = new ArrayList<>();
    private ArrayList<TextView> blockTxtViews  = new ArrayList<>();
    private ArrayList<TextView> usageTxtViews  = new ArrayList<>();
    private ArrayList<TextView> statusTxtViews = new ArrayList<>();
    private ArrayList<Button>   autoBtnViews   = new ArrayList<>();

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
        setContentView(R.layout.activity_control_standbypower);

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
        mStandbypowerResponse = new Messenger(responseHandler);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getBaseContext());
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
        /**
         * @breif activity item find
         */
        mStandByPowerListParent             = (LinearLayout)findViewById(R.id.Standbypower_Lin_List_Parent);
        //******************************************************************************************

        //******************************************************************************************

        mArrayGroupID               = new ArrayList<>();
        mArraySubID                 = new ArrayList<>();
        mArrayName                  = new ArrayList<>();
        mArrayAutoBlock             = new ArrayList<>();
        mArrayCurrentUsage          = new ArrayList<>();
        mArrayAutoBlockSettingValue = new ArrayList<>();
        mArrayBlockCriteria         = new ArrayList<>();
        mArrayOverload              = new ArrayList<>();
        mArrayPowerSupply           = new ArrayList<>();

        mStandbypowerPosition = 0;
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
        tMsg.replyTo = mStandbypowerResponse;
        sendMessage(tMsg);
        mDataSendFlag = 0;
        mStandbypowerRequest = null;
        TimeHandlerStandbypower(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(StandbypowerRunner);
        }
        if (mTimeHandlerGroup != null){
            mTimeHandlerGroup.removeCallbacks(StandbypowerGroupRunner);
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
            mStandbypowerRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mStandbypowerResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerStandbypower(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mStandbypowerResponse;
            sendMessage(tMsg);
            mStandbypowerRequest = null;
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
                case    Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST:
                    StandbypowerStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST:
                    StandbypowerControlResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST:
                    StandbypowerControlResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mStandbypowerRequest, mStandbypowerResponse, StandbypowerActivity.this);
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
            if(mStandbypowerRequest != null) {
                mStandbypowerRequest.send(tMsg);
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
    private void TimeHandlerStandbypower(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(StandbypowerRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler setup function
     * @param tState
     * @param tTime
     */
    private void TimeHandlerStandbypowerEachGroup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerGroup == null){
                mTimeHandlerGroup = new Handler();
            }

            mTimeHandlerGroup.postDelayed(StandbypowerGroupRunner, tTime);
        }else{
            mTimeHandlerGroup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable StandbypowerRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    StandbypowerStateRequest();
                    mTimeHandler.postDelayed(StandbypowerRunner, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mStandbypowerEachGroupFlag = 0;
                        mProgressDialog.Dismiss();
                        TimeHandlerStandbypower(false, TIMER_NULL);
                        TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                        StranbypowerSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        mTimeHandler.postDelayed(StandbypowerRunner, TIMER_REQUEST);
                    }
                }

                if(mDataSendFlag == 1){
                    StandbypowerStateRequest();
                    mDataSendFlag = 0;
                }
            }else{
                TimeHandlerStandbypower(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable StandbypowerGroupRunner = new Runnable() {
        @Override
        public void run() {
            if (mTimeHandlerGroup != null) {
                mWaitGroupCount++;
                if (mWaitGroupCount > TIMER_WAIT_TIME) {
                    mWaitCount = 0;
                    mDataSendFlag = 0;
                    mWaitGroupCount = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mStandbypowerEachGroupFlag = 0;
                    mProgressDialog.Dismiss();
                    TimeHandlerStandbypower(false, TIMER_NULL);
                    TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                    StranbypowerSocketClose();
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else {
                    mTimeHandlerGroup.postDelayed(StandbypowerGroupRunner, TIMER_REQUEST);
                }
            }else{
                TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Standby socket close
     */
    private void StranbypowerSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mStandbypowerResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Standbypower state request
     */
    private void StandbypowerStateRequest(){
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerStandbypower(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mStandbypowerResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
        bundle.putString(Constants.KD_DATA_SUB_ID, "All");
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif standbypower each control request communication
     * @param tNum
     */
    private void StandbypowerEachControlRequest(int tNum, String tState, String tValue, String settingValue){
        mWaitCount                  = 0;
        mWaitGroupCount             = 0;
        mRequestState               = REQUEST_DATA_SEND_WAIT;
        mStandbypowerEachGroupFlag  = 1;
        mStandbypowerDataSend       = tState;
        mStandbypowerData           = tValue;
        TimeHandlerStandbypower(true, TIMER_REQUEST);
        TimeHandlerStandbypowerEachGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mStandbypowerResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(tNum));
        bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(tNum));
        bundle.putString(Constants.KD_DATA_AUTOBLOCKSETTING, settingValue);

        // AUTO BLOCK SETTING

        Log.i(TAG,"tState : " + tState + " tValue : " + tValue);

        if(tState.equals(STANDBYPOWER_AUTO_BLOCK)){
            bundle.putString(Constants.KD_DATA_AUTOBLOCK, tValue);
            bundle.putString(Constants.KD_DATA_STANDBYPOWER_STATE, "");
        }else{
            bundle.putString(Constants.KD_DATA_AUTOBLOCK, "");
            bundle.putString(Constants.KD_DATA_STANDBYPOWER_STATE, tValue);
        }

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Stand by power all control on off
     * @param tState
     */
    private void StandbypowerGroupControlRequest(String tState, String tValue){
        mWaitCount                  = 0;
        mWaitGroupCount             = 0;
        mStandbypowerEachGroupFlag  = 2;
        mStandbypowerDataSend       = tState;
        mStandbypowerData           = tValue;
        mRequestState               = REQUEST_DATA_SEND_WAIT;
        TimeHandlerStandbypower(true, TIMER_REQUEST);
        TimeHandlerStandbypowerEachGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mStandbypowerResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
        bundle.putString(Constants.KD_DATA_AUTOBLOCKSETTING, mArrayAutoBlockSettingValue.get(0));


        if(tState.equals(STANDBYPOWER_AUTO_BLOCK)){
            bundle.putString(Constants.KD_DATA_AUTOBLOCK, tValue);
            bundle.putString(Constants.KD_DATA_STANDBYPOWER_STATE, "");
        }else{
            bundle.putString(Constants.KD_DATA_AUTOBLOCK, "");
            bundle.putString(Constants.KD_DATA_STANDBYPOWER_STATE, tValue);
        }

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif stand by power state request result
     * @param tKDData
     */
    private void StandbypowerStateResult(KDData tKDData){
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            HNMLDataParserStandbypower(tKDData.ReceiveString);
            if(mStandbypowerEachGroupFlag == 1){
                // EACH SELECT
                if(mStandbypowerDataSend.equals(STANDBYPOWER_STATE)){
                    if(mArrayPowerSupply.get(mStandbypowerPosition).equals(mStandbypowerData)){
                        mProgressDialog.Dismiss();
                        mWaitGroupCount = 0;
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerStandbypower(false, TIMER_NULL);
                        TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                    }else{
                        mDataSendFlag = 1;
                    }
                }else if(mStandbypowerDataSend.equals(STANDBYPOWER_AUTO_BLOCK)){
                    if(mArrayAutoBlock.get(mStandbypowerPosition).equals(mStandbypowerData)){
                        mProgressDialog.Dismiss();
                        mWaitGroupCount = 0;
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerStandbypower(false, TIMER_NULL);
                        TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                    }else{
                        mDataSendFlag = 1;
                    }
                }else{
                    mWaitCount = 0;
                    mDataSendFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerStandbypower(false, TIMER_NULL);
                    mWaitGroupCount = 0;
                    mStandbypowerEachGroupFlag = 0;
                    TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                    mProgressDialog.Dismiss();
                }
            } else if(mStandbypowerEachGroupFlag == 2){
                // GROUP SELECT
                if(mStandbypowerDataSend.equals(STANDBYPOWER_STATE)){
                    for(int i = 0; i < mArrayPowerSupply.size(); i++){
                        if(!mArrayPowerSupply.get(i).equals(mStandbypowerData)){
                            mDataSendFlag = 1;
                            break;
                        }else{
                            if(mArrayPowerSupply.size() - 1 == i){
                                mProgressDialog.Dismiss();
                                mWaitCount = 0;
                                mRequestState = REQUEST_DATA_CLEAR;
                                TimeHandlerStandbypower(false, TIMER_NULL);
                                mWaitGroupCount = 0;
                                mDataSendFlag = 0;
                                mStandbypowerEachGroupFlag = 0;
                                TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                            }
                        }
                    }
                }else if(mStandbypowerDataSend.equals(STANDBYPOWER_AUTO_BLOCK)){
                    for(int i = 0; i < mArrayPowerSupply.size(); i++){
                        if(!mArrayAutoBlock.get(i).equals(mStandbypowerData)){
                            mDataSendFlag = 1;
                            break;
                        }else{
                            if(mArrayAutoBlock.size() - 1 == i){
                                mProgressDialog.Dismiss();
                                mWaitCount = 0;
                                mDataSendFlag = 0;
                                mRequestState = REQUEST_DATA_CLEAR;
                                TimeHandlerStandbypower(false, TIMER_NULL);
                                mWaitGroupCount = 0;
                                mStandbypowerEachGroupFlag = 0;
                                TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                            }
                        }
                    }
                }else{
                    mWaitCount = 0;
                    mDataSendFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerStandbypower(false, TIMER_NULL);
                    mProgressDialog.Dismiss();
                    mWaitGroupCount = 0;
                    mStandbypowerEachGroupFlag = 0;
                    TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                }
            }else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerStandbypower(false, TIMER_NULL);
                mWaitGroupCount = 0;
                TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
                mProgressDialog.Dismiss();
            }
        } else{
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerStandbypower(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            mWaitGroupCount = 0;
            TimeHandlerStandbypowerEachGroup(false, TIMER_NULL);
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Standbypower each & group control request result
     * @param tKDData
     */
    private void StandbypowerControlResult(KDData tKDData){

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mRequestState = REQUEST_DATA_SEND_START;
        }else{
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerStandbypower(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            mWaitGroupCount = 0;
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif slide menu bar find menu
     * @param tFind
     * @return
     */
    private boolean DeviceFind(String tFind){
        for(int i = 0; i < mMyGlobal.GlobalDeviceList.size(); i++){
            if(mMyGlobal.GlobalDeviceList.get(i).equals(tFind)){
                return true;
            }
        }
        return false;
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button
     */

    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(StandbypowerActivity.this, MainFragment.class);
//        intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_CONTROL);
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
            onBackPressed();
//            Intent intent = new Intent(StandbypowerActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Stand by power activity button
     * @param v
     */
    public void OnClickBtnStandbypower(View v){
        switch (v.getId()){
            case    R.id.Standbypower_Lin_Home:
//                Intent intent = new Intent(StandbypowerActivity.this, MainMenuActivity.class);
//                startActivity(intent);
//                finish();
                onBackPressed();
                break;
            case    R.id.Standbypower_Btn_Menu:
                mSlideDrawer.toggleRightDrawer();
                mIsSlideOpen = mSlideDrawer.isRightSideOpened();
                break;
            case    R.id.Standbypower_Btn_State:
                if(mArrayName.size() != 0) {
                    if (mStandbypowerPosition == 0) {
                        if (mArrayPowerSupply.get(mStandbypowerPosition).equals(STANDBYPOWER_ON)) {
                            StandbypowerGroupControlRequest(STANDBYPOWER_STATE, STANDBYPOWER_OFF);
                        } else {
                            StandbypowerGroupControlRequest(STANDBYPOWER_STATE, STANDBYPOWER_ON);
                        }
                    } else {
                        if (mArrayPowerSupply.get(mStandbypowerPosition).equals(STANDBYPOWER_ON)) {
                            StandbypowerEachControlRequest(mStandbypowerPosition, STANDBYPOWER_STATE, STANDBYPOWER_OFF,mArrayAutoBlockSettingValue.get(mStandbypowerPosition));
                        } else {
                            StandbypowerEachControlRequest(mStandbypowerPosition, STANDBYPOWER_STATE, STANDBYPOWER_ON,mArrayAutoBlockSettingValue.get(mStandbypowerPosition));
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
                break;
            case    R.id.Standbypower_Btn_AutoBlock:
                if(mArrayName.size() != 0) {
                    if (mStandbypowerPosition == 0) {
                        if (mArrayAutoBlock.get(mStandbypowerPosition).equals(STANDBYPOWER_ON)) {
                            StandbypowerGroupControlRequest(STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_OFF);
                        } else {
                            StandbypowerGroupControlRequest(STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_ON);
                        }
                    } else {
                        if (mArrayAutoBlock.get(mStandbypowerPosition).equals(STANDBYPOWER_ON)) {
                            StandbypowerEachControlRequest(mStandbypowerPosition, STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_OFF,mArrayAutoBlockSettingValue.get(mStandbypowerPosition));
                        } else {
                            StandbypowerEachControlRequest(mStandbypowerPosition, STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_ON,mArrayAutoBlockSettingValue.get(mStandbypowerPosition));
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
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
     * @breif hnml data parser Stand by power
     * @param String tContents
     */
    public void HNMLDataParserStandbypower(String tContents) {
        mArrayGroupID               = new ArrayList<>();
        mArraySubID                 = new ArrayList<>();
        mArrayName                  = new ArrayList<>();
        mArrayAutoBlock             = new ArrayList<>();
        mArrayCurrentUsage          = new ArrayList<>();
        mArrayAutoBlockSettingValue = new ArrayList<>();
        mArrayBlockCriteria         = new ArrayList<>();
        mArrayOverload              = new ArrayList<>();
        mArrayPowerSupply           = new ArrayList<>();

        if (tContents != null) {
            mArrayGroupID.add("1");
            mArraySubID.add("1");
            mArrayName.add(getString(R.string.Standbypower_textview_all));
            mArrayAutoBlock.add("");
            mArrayCurrentUsage.add("");
            mArrayAutoBlockSettingValue.add("");
            mArrayBlockCriteria.add("");
            mArrayOverload.add("");
            mArrayPowerSupply.add("");

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

                            if(tName.equals("GroupID")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayGroupID.add("");
                                }else{
                                    mArrayGroupID.add(tParser.getText());
                                }
                            }else if(tName.equals("SubID")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArraySubID.add("");
                                }else{
                                    mArraySubID.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_NAME)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayName.add("");
                                }else{
                                    mArrayName.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_AUTOBLOCK)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayAutoBlock.add("");
                                }else{
                                    mArrayAutoBlock.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_CURRENTUSAGE)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayCurrentUsage.add("");
                                }else{
                                    mArrayCurrentUsage.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_AUTOBLOCKSTTING)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayAutoBlockSettingValue.add("");
                                }else{
                                    mArrayAutoBlockSettingValue.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_BLOCKCRITERIA)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayBlockCriteria.add("");
                                }else{
                                    mArrayBlockCriteria.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_OVERLOAD)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayOverload.add("");
                                }else{
                                    mArrayOverload.add(tParser.getText());
                                }
                            }else if(tName.equals(STANDBYPOWER_DATA_POWRSUPPLY)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayPowerSupply.add("");
                                }else{
                                    mArrayPowerSupply.add(tParser.getText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if(tName.equals("DeviceName")){
                                mArrayName.add("");
                            }
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

            if(mArrayGroupID.size() > 1) {
                mArrayGroupID.set(0, mArrayGroupID.get(1));
                mArraySubID.set(0, mArraySubID.get(1));
                mArrayName.set(0, getString(R.string.Light_textview_all));
                mArrayAutoBlock.set(0, STANDBYPOWER_ON);
                mArrayCurrentUsage.set(0, mArrayCurrentUsage.get(1));
                mArrayAutoBlockSettingValue.set(0, mArrayAutoBlockSettingValue.get(1));
                mArrayBlockCriteria.set(0, mArrayBlockCriteria.get(1));
                mArrayOverload.set(0, mArrayOverload.get(1));
                mArrayPowerSupply.set(0, STANDBYPOWER_OFF);

                for(int i = 1; i < mArrayPowerSupply.size(); i++){
                    if(mArrayPowerSupply.get(i).equals(STANDBYPOWER_ON)){
                        mArrayPowerSupply.set(0, STANDBYPOWER_ON);
                        break;
                    }
                }

                for(int i = 1; i < mArrayAutoBlock.size(); i++){
                    if(mArrayAutoBlock.get(i).equals(STANDBYPOWER_ON)){
                        mArrayAutoBlock.set(0, STANDBYPOWER_ON);
                        break;
                    }else{
                        mArrayAutoBlock.set(0, STANDBYPOWER_OFF);
                    }
                }

                if (mStandByPowerListParent.getChildCount() == 1){
                    for (int i = 0; i < mArrayPowerSupply.size(); i++){
                        StandPowerListMake(true,i,mArrayPowerSupply.get(i),mArrayName.get(i),mArrayOverload.get(i),mArrayBlockCriteria.get(i),
                                mArrayAutoBlockSettingValue.get(i),mArrayCurrentUsage.get(i),mArrayAutoBlock.get(i));
                    }
                }else{
                    for (int i = 0; i < mArrayPowerSupply.size(); i++){
                        StandPowerListMake(false,i,mArrayPowerSupply.get(i),mArrayName.get(i),mArrayOverload.get(i),mArrayBlockCriteria.get(i),
                                mArrayAutoBlockSettingValue.get(i),mArrayCurrentUsage.get(i),mArrayAutoBlock.get(i));
                    }
                }
            }else{
                mArrayGroupID               = new ArrayList<>();
                mArraySubID                 = new ArrayList<>();
                mArrayName                  = new ArrayList<>();
                mArrayAutoBlock             = new ArrayList<>();
                mArrayCurrentUsage          = new ArrayList<>();
                mArrayAutoBlockSettingValue = new ArrayList<>();
                mArrayBlockCriteria         = new ArrayList<>();
                mArrayOverload              = new ArrayList<>();
                mArrayPowerSupply           = new ArrayList<>();

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    void StandPowerListMake(final boolean check, final int position, final String supply, String name, String overload, String blockCriteria,
                            final String autoBlockSettingValue, String currentUsage, final String autoBlock) {
        LinearLayout linearParent = null;
        TextView txtTitle = null;
        Switch swhControl = null;
        LinearLayout linearVisible = null;
        TextView txtBlock = null;
        TextView txtUsage = null;
        TextView txtStatus = null;
        Button btnAuto = null;

        if (check) {
            View listView = new View(this);
            listView = getLayoutInflater().inflate(R.layout.view_control_stand_by_power_layout, null);
            LinearLayout linearParent1 = (LinearLayout) listView.findViewById(R.id.Stand_Lin_List_Item_Parent);
            TextView txtTitle1 = (TextView) listView.findViewById(R.id.Stand_Txt_List_Item_Title);
            Switch swhControl1 = (Switch) listView.findViewById(R.id.Stand_Swh_List_Item);
            LinearLayout linearVisible1 = (LinearLayout)listView.findViewById(R.id.Stand_Lin_List_Item_Visible);
            txtBlock = (TextView)listView.findViewById(R.id.Stand_Txt_List_Item_Block);
            txtUsage = (TextView)listView.findViewById(R.id.Stand_Txt_List_Item_Usage);
            txtStatus = (TextView)listView.findViewById(R.id.Stand_Txt_List_Item_Status);
            btnAuto = (Button)listView.findViewById(R.id.Stand_Btn_List_Item_Auto);

            mStandByPowerListParent.addView(listView);

            linearParent = linearParent1;
            txtTitle = txtTitle1;
            swhControl = swhControl1;
            linearVisible = linearVisible1;

            parentLinViews.add(linearParent);
            titleTxtViews.add(txtTitle);
            controlSwhViews.add(swhControl);
            visibleLinViews.add(linearVisible);
            blockTxtViews.add(txtBlock);
            usageTxtViews.add(txtUsage);
            statusTxtViews.add(txtStatus);
            autoBtnViews.add(btnAuto);
        }

        if (txtTitle == null){
            linearParent = parentLinViews.get(position);
            txtTitle = titleTxtViews.get(position);
            swhControl = controlSwhViews.get(position);
            linearVisible = visibleLinViews.get(position);
            txtBlock = blockTxtViews.get(position);
            txtUsage = usageTxtViews.get(position);
            txtStatus = statusTxtViews.get(position);
            btnAuto = autoBtnViews.get(position);
        }

        txtTitle.setText(name);

        if (supply.equals(STANDBYPOWER_ON)) {
            swhControl.setChecked(true);

        } else {
            swhControl.setChecked(false);
        }

        if(position != 0) {
            if (overload.equals(STANDBYPOWER_ON)) {
                txtStatus.setText(getText(R.string.Standbypower_textview_overload));
            } else {
                if (blockCriteria.equals(STANDBYPOWER_ON)) {
                    txtStatus.setText(getText(R.string.Standbypower_textview_block_out)); //차단값 초과하여 사용중
                } else {
                    txtStatus.setText(getText(R.string.Standbypower_textview_block_in)); //차단값 이내에서 사용중
                }
            }
            txtStatus.setVisibility(View.VISIBLE);
            linearVisible.setVisibility(View.VISIBLE);
        }else{
            txtStatus.setVisibility(View.INVISIBLE);
            linearVisible.setVisibility(View.GONE);
        }

        if(position != 0) {
            txtBlock.setText(autoBlockSettingValue);
            txtUsage.setText(currentUsage);
        }else{
            txtBlock.setText(" - ");
            txtUsage.setText(" - ");
        }

        if (autoBlock.equals(STANDBYPOWER_ON)) {
            btnAuto.setText(getText(R.string.Standbypower_btn_block_cancle));
            btnAuto.setBackgroundResource(R.drawable.shape_circle_main);
            btnAuto.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            btnAuto.setText(getText(R.string.Standbypower_btn_block));
            btnAuto.setBackgroundResource(R.drawable.shape_stroke_corner_50dp);
            btnAuto.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        swhControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStandbypowerPosition = position;
                if(mArrayName.size() != 0) {
                    if (position == 0) {
                        if (supply.equals(STANDBYPOWER_ON)) {
                            StandbypowerGroupControlRequest(STANDBYPOWER_STATE, STANDBYPOWER_OFF);
                        } else {
                            StandbypowerGroupControlRequest(STANDBYPOWER_STATE, STANDBYPOWER_ON);
                        }
                    } else {
                        if (supply.equals(STANDBYPOWER_ON)) {
                            StandbypowerEachControlRequest(position, STANDBYPOWER_STATE, STANDBYPOWER_OFF,autoBlockSettingValue);
                        } else {
                            StandbypowerEachControlRequest(position, STANDBYPOWER_STATE, STANDBYPOWER_ON,autoBlockSettingValue);
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStandbypowerPosition = position;
                if(mArrayName.size() != 0) {
                    if (position == 0) {
                        if (autoBlock.equals(STANDBYPOWER_ON)) {
                            StandbypowerGroupControlRequest(STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_OFF);
                        } else {
                            StandbypowerGroupControlRequest(STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_ON);
                        }
                    } else {
                        if (autoBlock.equals(STANDBYPOWER_ON)) {
                            StandbypowerEachControlRequest(position, STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_OFF,autoBlockSettingValue);
                        } else {
                            StandbypowerEachControlRequest(position, STANDBYPOWER_AUTO_BLOCK, STANDBYPOWER_ON,autoBlockSettingValue);
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(StandbypowerActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });
    }
}
