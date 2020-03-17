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
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
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
 * @description: Created by lwg on 2016-07-25.
 */
public class HeatActivity extends Activity{

    private String TAG = "HeatActivity";
    //**********************************************************************************************
    private Messenger                               mHeatResponse        = null;
    private Messenger                               mHeatRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig                              mLocalConfig;
    public MyGlobal mMyGlobal;
    private Handler                                 mTimeHandler;
    private Handler                                 mTimeHandlerGroup;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog                    mProgressDialog;

    private boolean mTimeHandlerCheck = false;
    private boolean mTimeHandlerGroupCheck = false;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String>                       mArrayGroupID;
    private ArrayList<String>                       mArraySubID;
    private ArrayList<String>                       mArrayName;
    private ArrayList<String>                       mArrayReservation;
    private ArrayList<String>                       mArrayHotwater;
    private ArrayList<String>                       mArrayMode;
    private ArrayList<String>                       mArrayHeating;
    private ArrayList<String>                       mArraySettingTemp;
    private ArrayList<String>                       mArrayCurrentTemp;
    private ArrayList<String>                       mArrayFloat;
    private ArrayList<String>                       mArraySetMax;
    private ArrayList<String>                       mArraySetMin;
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
    private ViewGroup                               mViewGroupEMS;
    private ViewGroup                               mViewGroupVisitor;
    private ViewGroup                               mViewGroupNotice;
    private ViewGroup                               mViewGroupHomeView;
    private boolean                                 mIsSlideOpen;
    //**********************************************************************************************

    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mHeatPosition;
    private int                                     mHeatGroupFlag;
    private String                                  mHeatGroupState;
    private String                                  mHeatGroupData;
    private float                                   mHeatSetTempFloat;
    private float                                   mHeatCurrentTempFloat;
    private String                                  mHeatSetTemp;
    //**********************************************************************************************

    private LinearLayout                            mHeatLinListParent;

    //**********************************************************************************************
    private int                                     mWaitGroupCount         = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1500;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 16;   // 10 * 500msec = 20sec
    //**********************************************************************************************

    private int                                     mDataSendFlag           = 0;

    //**********************************************************************************************
    private ArrayList<LinearLayout> parentLinViews = new ArrayList<>();
    private ArrayList<TextView> titleTxtViews = new ArrayList<>();
    private ArrayList<Switch> controlSwhViews = new ArrayList<>();
    private ArrayList<LinearLayout> visibleLinViews = new ArrayList<>();
    private ArrayList<TextView> heatTxtViews = new ArrayList<>();
    private ArrayList<TextView> hotWaterTxtViews = new ArrayList<>();
    private ArrayList<TextView> modeTxtViews = new ArrayList<>();
    private ArrayList<TextView> settingTempTxtViews = new ArrayList<>();
    private ArrayList<TextView> currentTempTxtViews = new ArrayList<>();
    private ArrayList<SeekBar> seekbarViews = new ArrayList<>();
    private ArrayList<ImageButton> minusBtnViews = new ArrayList<>();
    private ArrayList<ImageButton> plusBtnViews = new ArrayList<>();
    private ArrayList<Button> saveBtnViews = new ArrayList<>();
    private ArrayList<LinearLayout> currentVisibleLinViews = new ArrayList<>();
    private ArrayList<RelativeLayout> settingVisibleRelaViews = new ArrayList<>();
    private ArrayList<LinearLayout> groupVisibleLinViews = new ArrayList<>();
    private ArrayList<TextView> goOutTxtViews = new ArrayList<>();

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
        setContentView(R.layout.activity_control_heat);

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
        mHeatResponse = new Messenger(responseHandler);
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

        mHeatLinListParent      = (LinearLayout)findViewById(R.id.Heat_Lin_List_Parent);
        //******************************************************************************************

        //******************************************************************************************

        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayReservation   = new ArrayList<>();
        mArrayHotwater      = new ArrayList<>();
        mArrayMode          = new ArrayList<>();
        mArrayHeating       = new ArrayList<>();
        mArraySettingTemp   = new ArrayList<>();
        mArrayCurrentTemp   = new ArrayList<>();
        mArrayFloat         = new ArrayList<>();
        mArraySetMax        = new ArrayList<>();
        mArraySetMin        = new ArrayList<>();
        mHeatGroupState     = "";
        //******************************************************************************************
        mHeatPosition   = 0;
        mHeatGroupFlag  = 0;
        //******************************************************************************************

        mDataSendFlag = 0;
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
        tMsg.replyTo = mHeatResponse;
        sendMessage(tMsg);
        mHeatRequest = null;
        mDataSendFlag = 0;
        TimeHandlerHeat(false, TIMER_NULL);
        TimeHandlerHeatGroup(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(HeatRunner);
        }
        if (mTimeHandlerGroup != null){
            mTimeHandlerGroup.removeCallbacks(HeatGroupRunner);
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
            mHeatRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mHeatResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerHeat(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mHeatResponse;
            sendMessage(tMsg);
            mHeatRequest = null;
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
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case    Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST:
                    HeatStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST:
                case    Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST:
                case    Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST:
                case    Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST:
                case    Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST:
                    HeatControlResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mHeatRequest, mHeatResponse, HeatActivity.this);
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
            if(mHeatRequest != null) {
                mHeatRequest.send(tMsg);
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
    private void TimeHandlerHeat(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }
            mTimeHandler.postDelayed(HeatRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler heat group control
     * @param tState
     * @param tTime
     */
    private void TimeHandlerHeatGroup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerGroup == null){
                mTimeHandlerGroup = new Handler();
            }

            mTimeHandlerGroup.postDelayed(HeatGroupRunner, tTime);
        }else{
            mTimeHandlerGroup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable HeatRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    HeatStateRequest();
                    mTimeHandler.postDelayed(HeatRunner, TIMER_REQUEST);
                }
                else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitGroupCount = 0;
                        mHeatGroupFlag = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerHeat(false, TIMER_NULL);
                        TimeHandlerHeatGroup(false, TIMER_NULL);
                        HeatSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        mTimeHandler.postDelayed(HeatRunner, TIMER_REQUEST);
                    }

                    if(mDataSendFlag == 1){
                        mDataSendFlag = 0;
                        HeatStateRequest();
                        Log.e("heat activity", "data send flag send");
                    }
                }
            }else{
                TimeHandlerHeat(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light group data timer
     */
    private Runnable HeatGroupRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerGroup != null){
                mWaitGroupCount++;
                Log.e("group count1111", String.valueOf(mWaitCount));
                if(mWaitGroupCount > TIMER_WAIT_TIME){
                    mWaitCount = 0;
                    mWaitGroupCount = 0;
                    mHeatGroupFlag = 0;
                    mDataSendFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mProgressDialog.Dismiss();
                    TimeHandlerHeat(false, TIMER_NULL);
                    TimeHandlerHeatGroup(false, TIMER_NULL);
                    HeatSocketClose();
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else{
                    TimeHandlerHeatGroup(true, TIMER_REQUEST);
                }
            } else{
                mWaitGroupCount = 0;
                TimeHandlerHeatGroup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif heat socket close
     */
    private void HeatSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     *  @breif heat state request
     */
    private void HeatStateRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All'");
        bundle.putString(Constants.KD_DATA_SUB_ID, "All");
        tMsg.setData(bundle);
        sendMessage(tMsg);

        Log.e("Heat state", "request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif boiler each control request communication
     * @param mode -> on, off
     */

    private void HeatEachControlRequest(int mode){
        mWaitCount      = 0;
        mHeatGroupState = "Heating";
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerHeat(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(mHeatPosition));
        bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(mHeatPosition));

        if (mode == 1){
            mHeatGroupData = "On";
            bundle.putString(Constants.KD_DATA_HEATING, "On");
        }else if (mode == 2){
            mHeatGroupData = "Off";
            bundle.putString(Constants.KD_DATA_HEATING, "Off");
        }

//        if(mArrayHeating.get(mHeatPosition).equals("On")) {
//            mHeatGroupData = "Off";
//            bundle.putString(Constants.KD_DATA_HEATING, "Off");
//        }else{
//            mHeatGroupData = "On";
//            bundle.putString(Constants.KD_DATA_HEATING, "On");
//        }
        tMsg.setData(bundle);
        sendMessage(tMsg);

        Log.e("Heat each control", "request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif boiler group control request communication
     * @param mode -> on, off
     */
    private void HeatGroupControlRequest(int mode){
        mWaitCount      = 0;
        mWaitGroupCount = 0;
        mHeatGroupFlag  = 1;
        mHeatGroupState = "Heating";
        mRequestState   = REQUEST_DATA_SEND_WAIT;

        TimeHandlerHeat(true, TIMER_REQUEST);
        TimeHandlerHeatGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");

        if (mode == 1){
            mHeatGroupData = "On";
            bundle.putString(Constants.KD_DATA_HEATING, "On");
        }else if (mode == 2){
            mHeatGroupData = "Off";
            bundle.putString(Constants.KD_DATA_HEATING, "Off");
        }

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif temp control
     * @param tEachGroup
     * @param tTemp
     */
    private void HeatTempControlRequest(String tEachGroup, String tTemp){
        mWaitCount      = 0;
        mHeatGroupState = "Temp";
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerHeat(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));

        if(tEachGroup.equals("All")){
            mWaitGroupCount = 0;
            mHeatGroupFlag  = 1;
            TimeHandlerHeatGroup(true, TIMER_REQUEST);
            bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
            bundle.putString(Constants.KD_DATA_SUB_ID, "All");
        }else {
            bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(mHeatPosition));
            bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(mHeatPosition));
        }
        mHeatGroupData  = tTemp;
        bundle.putString(Constants.KD_DATA_TEMP, tTemp);

        tMsg.setData(bundle);
        sendMessage(tMsg);

        Log.e("Heat temp control", "request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif heat mode setup
     * @param tEachGroup
     */
    private void HeatModeControlRequest(String tEachGroup,int mode){
        mWaitCount      = 0;
        mHeatGroupState = "Mode";
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerHeat(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));

        if(tEachGroup.equals("All")){
            mWaitGroupCount = 0;
            mHeatGroupFlag  = 1;
            TimeHandlerHeatGroup(true, TIMER_REQUEST);
            bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
            bundle.putString(Constants.KD_DATA_SUB_ID, "All");
        }else {
            bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(mHeatPosition));
            bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(mHeatPosition));
        }

        if (mode == 1){
            mHeatGroupData = "GoOut";
            bundle.putString(Constants.KD_DATA_MODE, "GoOut");
        }else if (mode == 2){
            mHeatGroupData = "Normal";
            bundle.putString(Constants.KD_DATA_MODE, "Normal");
        }


        tMsg.setData(bundle);
        sendMessage(tMsg);

        Log.e("Heat mode control", "request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif hotwater heat
     * @param tEachGroup
     */
    private void HeatHotWaterControlRequest(String tEachGroup){
        mWaitCount      = 0;
        mHeatGroupState = "HotWater";
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerHeat(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mHeatResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));

        if(tEachGroup.equals("All")){
            mWaitGroupCount = 0;
            mHeatGroupFlag  = 1;
            TimeHandlerHeatGroup(true, TIMER_REQUEST);
            bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
            bundle.putString(Constants.KD_DATA_SUB_ID, "All");
        }else {
            bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(mHeatPosition));
            bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(mHeatPosition));
        }

        if(mArrayHotwater.get(mHeatPosition).equals("On")){
            mHeatGroupData  = "Off";
            bundle.putString(Constants.KD_DATA_HOTWATER, "Off");
        }else{
            mHeatGroupData  = "On";
            bundle.putString(Constants.KD_DATA_HOTWATER, "On");
        }

        tMsg.setData(bundle);
        sendMessage(tMsg);

        Log.e("Heat hot water", "request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif heat state result
     * @param tKDData
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void HeatStateResult(KDData tKDData){
//        Log.e("Heat kd data", tKDData.ReceiveString);
        if (tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserHeat(tKDData.ReceiveString);
                if (mHeatGroupFlag == 1) {
                    if (mHeatGroupState.equals("Heating")) {
                        for (int i = 0; i < mArrayHeating.size(); i++) {
                            if (!mArrayHeating.get(i).equals(mHeatGroupData)) {
                                mDataSendFlag = 1;
                                Log.e("heat activity", "group heat");
                                break;
                            } else {
                                if (mArrayHeating.size() - 1 == i) {
                                    mDataSendFlag = 0;
                                    mHeatGroupFlag = 0;
                                    mHeatGroupState = "";
                                    mHeatGroupData = "";
                                    mRequestState = REQUEST_DATA_CLEAR;
                                    TimeHandlerHeat(false, TIMER_NULL);
                                    TimeHandlerHeatGroup(false, TIMER_NULL);
                                    mProgressDialog.Dismiss();
                                }
                            }
                        }
                    } else if (mHeatGroupState.equals("Mode")) {
                        for (int i = 0; i < mArrayMode.size(); i++) {
                            if (!mArrayMode.get(i).equals(mHeatGroupData)) {
                                mDataSendFlag = 1;
                                Log.e("heat activity", "group mode");
                                break;
                            } else {
                                if (mArrayMode.size() - 1 == i) {
                                    mDataSendFlag = 0;
                                    mHeatGroupFlag = 0;
                                    mHeatGroupState = "";
                                    mHeatGroupData = "";
                                    mRequestState = REQUEST_DATA_CLEAR;
                                    TimeHandlerHeat(false, TIMER_NULL);
                                    TimeHandlerHeatGroup(false, TIMER_NULL);
                                    mProgressDialog.Dismiss();
                                }
                            }
                        }
                    } else if (mHeatGroupState.equals("Temp")) {
                        for (int i = 0; i < mArraySettingTemp.size(); i++) {
                            if (!mArraySettingTemp.get(i).equals(mHeatGroupData)) {
                                mDataSendFlag = 1;
                                Log.e("heat activity", "group temp");
                                break;
                            } else {
                                if (mArraySettingTemp.size() - 1 == i) {
                                    mDataSendFlag = 0;
                                    mHeatGroupFlag = 0;
                                    mHeatGroupState = "";
                                    mHeatGroupData = "";
                                    mRequestState = REQUEST_DATA_CLEAR;
                                    TimeHandlerHeat(false, TIMER_NULL);
                                    TimeHandlerHeatGroup(false, TIMER_NULL);
                                    mProgressDialog.Dismiss();
                                }
                            }
                        }
                    } else if (mHeatGroupState.equals("HotWater")) {
                        for (int i = 0; i < mArrayHotwater.size(); i++) {
                            if (!mArrayHotwater.get(i).equals(mHeatGroupData)) {
                                mDataSendFlag = 1;
                                Log.e("heat activity", "group hotwater");
                                break;
                            } else {
                                if (mArrayHotwater.size() - 1 == i) {
                                    mDataSendFlag = 0;
                                    mHeatGroupFlag = 0;
                                    mHeatGroupState = "";
                                    mHeatGroupData = "";
                                    mRequestState = REQUEST_DATA_CLEAR;
                                    TimeHandlerHeat(false, TIMER_NULL);
                                    TimeHandlerHeatGroup(false, TIMER_NULL);
                                    mProgressDialog.Dismiss();
                                }
                            }
                        }
                    } else {
                        mDataSendFlag = 0;
                        mHeatGroupFlag = 0;
                        mHeatGroupState = "";
                        mHeatGroupData = "";
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerHeat(false, TIMER_NULL);
                        TimeHandlerHeatGroup(false, TIMER_NULL);
                        mProgressDialog.Dismiss();
                    }
                } else {
                    if (mHeatGroupState.equals("Heating")) {
                        if (!mArrayHeating.get(mHeatPosition).equals(mHeatGroupData)) {
                            mDataSendFlag = 1;
                            Log.e("heat activity", "each heating");
                        } else {
                            mDataSendFlag = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerHeat(false, TIMER_NULL);
                            mProgressDialog.Dismiss();
                        }
                    } else if (mHeatGroupState.equals("Mode")) {
                        if (!mArrayMode.get(mHeatPosition).equals(mHeatGroupData)) {
                            mDataSendFlag = 1;
                            Log.e("heat activity", "each mode");
                        } else {
                            mDataSendFlag = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerHeat(false, TIMER_NULL);
                            mProgressDialog.Dismiss();
                        }
                    } else if (mHeatGroupState.equals("Temp")) {
                        if (!mArraySettingTemp.get(mHeatPosition).equals(mHeatGroupData)) {
                            mDataSendFlag = 1;
                            Log.e("heat activity", "each temp");
                        } else {
                            mDataSendFlag = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerHeat(false, TIMER_NULL);
                            mProgressDialog.Dismiss();
                        }
                    } else if (mHeatGroupState.equals("HotWater")) {
                        if (!mArrayHotwater.get(mHeatPosition).equals(mHeatGroupData)) {
                            mDataSendFlag = 1;
                            Log.e("heat activity", "each hot water");
                        } else {
                            mDataSendFlag = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerHeat(false, TIMER_NULL);
                            mProgressDialog.Dismiss();
                        }
                    } else {
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerHeat(false, TIMER_NULL);
                        mProgressDialog.Dismiss();
                    }
                }
            } else {
                mDataSendFlag = 0;
                mHeatGroupFlag = 0;
                mHeatGroupState = "";
                mHeatGroupData = "";
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerHeat(false, TIMER_NULL);
                TimeHandlerHeatGroup(false, TIMER_NULL);
                mProgressDialog.Dismiss();
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mDataSendFlag = 0;
            mHeatGroupFlag = 0;
            mHeatGroupState = "";
            mHeatGroupData = "";
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerHeat(false, TIMER_NULL);
            TimeHandlerHeatGroup(false, TIMER_NULL);
            mProgressDialog.Dismiss();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif heat each control request result
     * @param tKDData
     */
    private void HeatControlResult(KDData tKDData){
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                /*
                mWaitCount = 0;
                mDataSendFlag = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerHeat(false, TIMER_NULL);
                mProgressDialog.Dismiss();
                TimeHandlerHeatGroup(false, TIMER_NULL);
                 */
                mDataSendFlag = 1;
            } else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerHeat(false, TIMER_NULL);
                mProgressDialog.Dismiss();
                TimeHandlerHeatGroup(false, TIMER_NULL);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerHeat(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            TimeHandlerHeatGroup(false, TIMER_NULL);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif slide menu bar find menu
     * @param tFind
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
//        Intent intent = new Intent(HeatActivity.this, MainFragment.class);
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
            mCustomPopup    = null;
            mRequestState   = REQUEST_DATA_CLEAR;
            TimeHandlerHeat(false, TIMER_NULL);

//            Intent intent = new Intent(HeatActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
            onBackPressed();
        }
    };
    //**********************************************************************************************


    //**********************************************************************************************
    /**
     * @breif onclick btn heat activity
     * @param v
     */
    public void OnClickBtnHeat(View v){
        switch(v.getId()){
            case    R.id.Heat_Lin_Back:
                onBackPressed();
                break;
            case    R.id.Heat_Btn_Menu:
                mSlideDrawer.toggleRightDrawer();
                mIsSlideOpen = mSlideDrawer.isRightSideOpened();
                break;
            default:
                break;
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif hnml data parser heat
     * @param tContents String
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void HNMLDataParserHeat(String tContents) {
        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayReservation   = new ArrayList<>();
        mArrayHotwater      = new ArrayList<>();
        mArrayMode          = new ArrayList<>();
        mArrayHeating       = new ArrayList<>();
        mArraySettingTemp   = new ArrayList<>();
        mArrayCurrentTemp   = new ArrayList<>();
        mArrayFloat         = new ArrayList<>();
        mArraySetMax        = new ArrayList<>();
        mArraySetMin        = new ArrayList<>();

        if (tContents != null) {
            mArrayGroupID.add("1");
            mArraySubID.add("1");
            mArrayName.add(getString(R.string.Heat_textview_all));
            mArrayReservation.add("");
            mArrayHotwater.add("");
            mArrayMode.add("");
            mArrayHeating.add("");
            mArraySettingTemp.add("");
            mArrayCurrentTemp.add("");
            mArrayFloat.add("");
            mArraySetMax.add("");
            mArraySetMin.add("");

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
                            }else if(tName.equals("DeviceName")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayName.add("");
                                }else{
                                    mArrayName.add(tParser.getText());
                                }
                            }else if(tName.equals("Reservation")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayReservation.add("");
                                }else{
                                    mArrayReservation.add(tParser.getText());
                                }
                            }else if(tName.equals("HotWater")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayHotwater.add("");
                                }else{
                                    mArrayHotwater.add(tParser.getText());
                                }
                            }else if(tName.equals("Mode")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayMode.add("");
                                }else{
                                    mArrayMode.add(tParser.getText());
                                }
                            }else if(tName.equals("Heating")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayHeating.add("");
                                }else{
                                    mArrayHeating.add(tParser.getText());
                                }
                            }else if(tName.equals("SettingTemp")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArraySettingTemp.add("");
                                }else{
                                    mArraySettingTemp.add(tParser.getText());
                                }
                            }else if(tName.equals("CurrentTemp")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayCurrentTemp.add("");
                                }else{
                                    mArrayCurrentTemp.add(tParser.getText());
                                }
                            }else if(tName.equals("Float")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayFloat.add("");
                                }else{
                                    mArrayFloat.add(tParser.getText());
                                }
                            }else if(tName.equals("SettingMax")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArraySetMax.add("");
                                }else{
                                    mArraySetMax.add(tParser.getText());
                                }
                            }else if(tName.equals("SettingMin")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArraySetMin.add("");
                                }else{
                                    mArraySetMin.add(tParser.getText());
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

            if(mArrayGroupID.size() > 1) {
                mArrayGroupID.set(0, mArrayGroupID.get(1));
                mArraySubID.set(0, mArraySubID.get(1));
                mArrayName.set(0, getString(R.string.Heat_textview_all));
                mArrayReservation.set(0, mArrayReservation.get(1));
                mArrayHotwater.set(0, mArrayHotwater.get(1));
                mArrayMode.set(0, mArrayMode.get(1));
                mArrayHeating.set(0, mArrayHeating.get(1));
                mArraySettingTemp.set(0, mArraySettingTemp.get(1));
                mArrayCurrentTemp.set(0, mArrayCurrentTemp.get(1));
                mArrayFloat.set(0, mArrayFloat.get(1));
                mArraySetMax.set(0, mArraySetMax.get(1));
                mArraySetMin.set(0, mArraySetMin.get(1));

                String tCompare = "";

                for(int i = 1; i < mArrayReservation.size(); i++){
                    if(i == 1){
                        tCompare = mArrayReservation.get(i);
                        mArrayReservation.set(0, mArrayReservation.get(i));
                    }else if(!tCompare.equals(mArrayReservation.get(i))){
                        mArrayReservation.set(0, "");
                        break;
                    }
                }

                for(int i = 1; i < mArrayHotwater.size(); i++){
                    if(i == 1){
                        tCompare = mArrayHotwater.get(i);
                        mArrayHotwater.set(0, mArrayHotwater.get(i));
                    }else if(!tCompare.equals(mArrayHotwater.get(i))){
                        mArrayHotwater.set(0, "");
                        break;
                    }
                }

                for(int i = 1; i < mArrayMode.size(); i++){
                    if(i == 1){
                        tCompare = mArrayMode.get(i);
                        mArrayMode.set(0, mArrayMode.get(i));
                    }else if(!tCompare.equals(mArrayMode.get(i))){
                        mArrayMode.set(0, "");
                        break;
                    }
                }

                for(int i = 1; i < mArrayHeating.size(); i++){
                    if(i == 1){
                        tCompare = mArrayHeating.get(i);
                        mArrayHeating.set(0, mArrayHeating.get(i));
                    }else if(!tCompare.equals(mArrayHeating.get(i))){
                        mArrayHeating.set(0, "");
                        break;
                    }
                }
                if (mHeatLinListParent.getChildCount() == 1){
                    for (int i = 0; i < mArrayHeating.size(); i++){
                        HeatListMake(true,i,mArrayHeating.get(i),mArrayName.get(i),mArrayCurrentTemp.get(i),mArraySetMax.get(i),mArraySetMin.get(i),
                                mArrayHotwater.get(i),mArrayMode.get(i),mArrayReservation.get(i),mArrayFloat.get(i),mArraySettingTemp.get(i));
                    }
                }else{
                    for (int i = 0; i < mArrayHeating.size(); i++){
                        HeatListMake(false,i,mArrayHeating.get(i),mArrayName.get(i),mArrayCurrentTemp.get(i),mArraySetMax.get(i),mArraySetMin.get(i),
                                mArrayHotwater.get(i),mArrayMode.get(i),mArrayReservation.get(i),mArrayFloat.get(i),mArraySettingTemp.get(i));
                    }
                }
            }else{
                mArrayGroupID       = new ArrayList<>();
                mArraySubID         = new ArrayList<>();
                mArrayName          = new ArrayList<>();
                mArrayReservation   = new ArrayList<>();
                mArrayHotwater      = new ArrayList<>();
                mArrayMode          = new ArrayList<>();
                mArrayHeating       = new ArrayList<>();
                mArraySettingTemp   = new ArrayList<>();
                mArrayCurrentTemp   = new ArrayList<>();
                mArrayFloat         = new ArrayList<>();
                mArraySetMax        = new ArrayList<>();
                mArraySetMin        = new ArrayList<>();

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //add list
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void HeatListMake(boolean check, final int position , final String heating, String name, String currentTemp, final String max, final String min, final String hotWater,
                      final String mode, String reservation, final String floatMode, String settingTemp){

        LinearLayout linearParent = null;
        TextView txtTitle = null;
        Switch swhControl = null;
        LinearLayout linVisible = null;
        TextView txtHeat = null;
        TextView txtHotWater = null;
        TextView txtMode = null;
        TextView txtSettingTemp = null;
        TextView txtCurrentTemp = null;
        SeekBar seekBar = null;
        ImageButton btnMinus = null;
        ImageButton btnPlus = null;
        Button btnSave = null;
        LinearLayout linearCurrentVisible = null;
        RelativeLayout relaSettingVisible = null;
        LinearLayout linearGroupVisible = null;
        TextView txtGoOutMode = null;

        if (check){
            View listView = new View(this);
            listView = getLayoutInflater().inflate(R.layout.view_control_heat_layout,null);
            LinearLayout linearParent1 = (LinearLayout)listView.findViewById(R.id.Heat_Lin_List_Item_Parent);
            TextView txtTitle1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_Title);
            Switch swhControl1 = (Switch)listView.findViewById(R.id.Heat_Swh_List_Item);
            final LinearLayout linVisible1 = (LinearLayout)listView.findViewById(R.id.Heat_Lin_List_Item_Visible);
            TextView txtHeat1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_heat);
            TextView txtHotWater1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_hot_water);
            TextView txtMode1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_mode);
            final TextView txtSettingTemp1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_Setting_Temp);
            final TextView txtCurrentTemp1 = (TextView)listView.findViewById(R.id.Heat_Txt_List_Item_Current_Temp);
            final SeekBar seekBar1 = (SeekBar)listView.findViewById(R.id.Heat_Seek_List_Item);
            ImageButton btnMinus1 = (ImageButton)listView.findViewById(R.id.Heat_Btn_List_Item_Minus);
            ImageButton btnPlus1 = (ImageButton)listView.findViewById(R.id.Heat_Btn_List_Item_Plus);
            Button btnSave1 = (Button)listView.findViewById(R.id.Heat_Btn_List_Item_Save);
            LinearLayout linearCurrentVisible1 = (LinearLayout)listView.findViewById(R.id.Heat_Lin_List_Item_Current_Parent_Visible);
            RelativeLayout relaSettingVisible1 = (RelativeLayout)listView.findViewById(R.id.Heat_Rela_List_Item_Setting_Parent_Visible);
            LinearLayout linearGroupVisible1 = (LinearLayout)listView.findViewById(R.id.Heat_Lin_List_Item_Control_Group_Visible);
            TextView txtGoOutMode1 = (TextView)listView.findViewById(R.id.Heat_Txt_ListItem_Go_Out_Mode);

            mHeatLinListParent.addView(listView);

            linearParent = linearParent1;
            txtTitle = txtTitle1;
            swhControl = swhControl1;
            linVisible = linVisible1;
            txtHeat = txtHeat1;
            txtHotWater = txtHotWater1;
            txtMode = txtMode1;
            txtSettingTemp = txtSettingTemp1;
            txtCurrentTemp = txtCurrentTemp1;
            seekBar = seekBar1;
            btnMinus = btnMinus1;
            btnPlus = btnPlus1;
            btnSave = btnSave1;
            linearCurrentVisible = linearCurrentVisible1;
            relaSettingVisible = relaSettingVisible1;
            linearGroupVisible = linearGroupVisible1;
            txtGoOutMode = txtGoOutMode1;

            parentLinViews.add(linearParent);
            titleTxtViews.add(txtTitle);
            controlSwhViews.add(swhControl);
            visibleLinViews.add(linVisible);
            heatTxtViews.add(txtHeat);
            hotWaterTxtViews.add(txtHotWater);
            modeTxtViews.add(txtMode);
            settingTempTxtViews.add(txtSettingTemp);
            currentTempTxtViews.add(txtCurrentTemp);
            seekbarViews.add(seekBar);
            minusBtnViews.add(btnMinus);
            plusBtnViews.add(btnPlus);
            saveBtnViews.add(btnSave);
            currentVisibleLinViews.add(linearCurrentVisible);
            settingVisibleRelaViews.add(relaSettingVisible);
            groupVisibleLinViews.add(linearGroupVisible);
            goOutTxtViews.add(txtGoOutMode);
        }

        if (txtTitle == null){
            linearParent = parentLinViews.get(position);
            txtTitle = titleTxtViews.get(position);
            swhControl = controlSwhViews.get(position);
            linVisible = visibleLinViews.get(position);
            txtHeat = heatTxtViews.get(position);
            txtHotWater = hotWaterTxtViews.get(position);
            txtMode = modeTxtViews.get(position);
            txtSettingTemp = settingTempTxtViews.get(position);
            txtCurrentTemp = currentTempTxtViews.get(position);
            seekBar = seekbarViews.get(position);
            btnMinus = minusBtnViews.get(position);
            btnPlus = plusBtnViews.get(position);
            btnSave = saveBtnViews.get(position);
            linearCurrentVisible = currentVisibleLinViews.get(position);
            relaSettingVisible = settingVisibleRelaViews.get(position);
            linearGroupVisible = groupVisibleLinViews.get(position);
            txtGoOutMode = goOutTxtViews.get(position);
        }

        txtTitle.setText(name);
        txtSettingTemp.setText(settingTemp);
        txtCurrentTemp.setText(currentTemp);

        if (position == 0){
            linearCurrentVisible.setVisibility(View.INVISIBLE);
            relaSettingVisible.setVisibility(View.INVISIBLE);
            seekBar.setEnabled(false);
            btnMinus.setEnabled(false);
            btnPlus.setEnabled(false);
            btnSave.setEnabled(false);
            linearGroupVisible.setVisibility(View.GONE);
        }else{
            linearGroupVisible.setVisibility(View.VISIBLE);
        }

        linVisible.setVisibility(View.VISIBLE);
        linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);

        //MARK START : JMH   2020-03-17 버튼 색상 변경
        //온수 NONE 상태일 경우 버튼 색상 구분되지 않아 색상 변경함
        //기존 : background(fafafa), text(b8b8b8) -> 변경 : 활성화 background(ededed), text(b8b8b8), 비활성화 background(fafafa), text(e8e8e8)

        if(heating.equals("On")){
            txtHeat.setBackgroundResource(R.drawable.shape_stroke_corner_50dp);
            txtHeat.setBackgroundTintList(null);
            txtHeat.setTextColor(getResources().getColor(R.color.colorPrimary));
            swhControl.setChecked(true);

            if (Float.parseFloat(settingTemp) > Float.parseFloat(currentTemp)){
                txtGoOutMode.setVisibility(View.VISIBLE);
                txtGoOutMode.setText("난방중");
            }else{
                txtGoOutMode.setVisibility(View.INVISIBLE);
            }
        }else{
            txtHeat.setBackgroundTintList(getResources().getColorStateList(R.color.colorededed));
            txtHeat.setTextColor(getResources().getColor(R.color.colorb8b8b8));
            swhControl.setChecked(false);
            txtGoOutMode.setVisibility(View.INVISIBLE);
//            linVisible.setVisibility(View.GONE);
//            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
        }

        if(hotWater.equals("None")){
            txtHotWater.setBackgroundTintList(getResources().getColorStateList(R.color.colorfafafa));
            txtHotWater.setTextColor(getResources().getColor(R.color.colore8e8e8));
            txtHotWater.setSelected(false);
        }else if(hotWater.equals("On")){
            txtHotWater.setBackgroundResource(R.drawable.shape_stroke_corner_50dp);
            txtHotWater.setBackgroundTintList(null);
            txtHotWater.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else{
            txtHotWater.setBackgroundTintList(getResources().getColorStateList(R.color.colorededed));
            txtHotWater.setTextColor(getResources().getColor(R.color.colorb8b8b8));
        }

        if(mode.equals("GoOut")){
            txtMode.setBackgroundResource(R.drawable.shape_stroke_corner_50dp);
            txtMode.setBackgroundTintList(null);
            txtMode.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtGoOutMode.setVisibility(View.VISIBLE);
            txtGoOutMode.setText("외출중");
            swhControl.setChecked(true);
        }else{
            txtMode.setBackgroundTintList(getResources().getColorStateList(R.color.colorededed));
            txtMode.setTextColor(getResources().getColor(R.color.colorb8b8b8));
        }

        //MARK END

        final LinearLayout finalLinVisible = linVisible;
        final LinearLayout finalLinearParent = linearParent;

        final Switch finalSwhControl = swhControl;

        swhControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeatPosition = position;
                if (finalSwhControl.isChecked()){
//                    finalLinVisible.setVisibility(View.VISIBLE);
//                    finalLinearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);

                    if(mArrayGroupID.size() > 1){
                        if(position == 0){
//                            if(!heating.equals("On")) {
                                HeatGroupControlRequest(1);
//                            }
                        }else{
//                            if(!heating.equals("On")) {
                                HeatEachControlRequest(1);
//                            }
                        }
                    }else{
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }
                }else{
//                    finalLinVisible.setVisibility(View.GONE);
//                    finalLinearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);

                    if(mArrayGroupID.size() > 1){
                        if(position == 0){
//                            if(!mode.equals("GoOut")) {
                                HeatGroupControlRequest(2);
//                            }
                        }else{
//                            if(!mode.equals("GoOut")) {
                                HeatEachControlRequest(2);
//                            }
                        }
                    }else{
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }
                }
            }
        });

        final float[] progressValue = new float[1];

        if (floatMode.equals("Y")){
            seekBar.setMax((Integer.parseInt(max) - Integer.parseInt(min)) * 2);
            seekBar.setProgress((int) ((Float.parseFloat(settingTemp) - Float.parseFloat(min)) * 2));
            progressValue[0] = (int) ((Float.parseFloat(settingTemp) - Float.parseFloat(min)) * 2);
            Log.i(TAG,"1111 : " + progressValue[0]);
        }else{
            seekBar.setMax(Integer.parseInt(max) - Integer.parseInt(min));
            seekBar.setProgress((int) Float.parseFloat(settingTemp) - Integer.parseInt(min));
            progressValue[0] = (int) Float.parseFloat(settingTemp) - Integer.parseInt(min);
        }


        final TextView finalTxtSettingTemp = txtSettingTemp;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
                progressValue[0] = progress;
                if (floatMode.equals("Y")){
                    finalTxtSettingTemp.setText(""+(float)((float)progress/2 + Integer.parseInt(min)));
                }else{
                    finalTxtSettingTemp.setText(""+(float)((float)progress + Integer.parseInt(min)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final SeekBar finalSeekBar = seekBar;
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (progressValue[0] != 0){
                    if (floatMode.equals("Y")){
                        progressValue[0] = progressValue[0] - 1f;
                        finalSeekBar.setProgress((int)progressValue[0]);
                        finalTxtSettingTemp.setText(""+(progressValue[0]/2 + Integer.parseInt(min)));
                    }else{
                        progressValue[0]--;
                        finalSeekBar.setProgress((int)progressValue[0]);
                        finalTxtSettingTemp.setText(""+(float)(finalSeekBar.getProgress() + Integer.parseInt(min)));
                    }

                }
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatMode.equals("Y")){
                    if (progressValue[0] != (Float.parseFloat(max)*2 - Float.parseFloat((min))*2)){
                        progressValue[0] = progressValue[0] + 1f;
                        finalSeekBar.setProgress((int)progressValue[0]);
                        finalTxtSettingTemp.setText(""+((float)progressValue[0]/2 + Float.parseFloat(min)));
                    }
                }else{
                    if (progressValue[0] != Integer.parseInt(max)){
                        progressValue[0]++;
                        finalSeekBar.setProgress((int)progressValue[0]);
                        finalTxtSettingTemp.setText(""+(float)(finalSeekBar.getProgress() + Integer.parseInt(min)));
                    }
                }
            }
        });

        txtHeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeatPosition = position;
                if(mArrayGroupID.size() > 1){
                    if(position == 0){
                        if(!heating.equals("On")) {
                            HeatGroupControlRequest(1);
                        }else{
                            HeatEachControlRequest(2);
                        }
                    }else{
                        if(!heating.equals("On")) {
                            HeatEachControlRequest(1);
                        }else{
                            HeatEachControlRequest(2);
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });

        txtHotWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeatPosition = position;
                if(mArrayGroupID.size() > 1){
                    if(!hotWater.equals("None")){
                        HeatHotWaterControlRequest("All");
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });

        txtMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeatPosition = position;
                if(mArrayGroupID.size() > 1){
                    if(position == 0){
                        if(!mode.equals("GoOut")) {
                            HeatModeControlRequest("All",1);
                        }
                    }else{
                        if(!mode.equals("GoOut")) {
                            HeatModeControlRequest("",1);
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeatPosition = position;
                if (mArrayGroupID.size() > 1) {
                    if (position == 0) {
                        //HeatTempControlRequest("All", mHeatSetTemp);
                    } else {
                        HeatTempControlRequest("", finalTxtSettingTemp.getText().toString());
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(HeatActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });
    }
    //**********************************************************************************************
}
