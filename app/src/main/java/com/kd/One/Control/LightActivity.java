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
import android.widget.CompoundButton;
import android.widget.ImageView;
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
 * Created by lwg on 2016-07-18.
 */
public class LightActivity extends Activity{
    private String TAG = "LightActivity";
    //**********************************************************************************************
    private Messenger                               mLightResponse        = null;
    private Messenger                               mLightRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public  LocalConfig                             mLocalConfig;
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
    private ArrayList<String>                       mArrayState;
    private ArrayList<String>                       mArrayDimming;
    private ArrayList<String>                       mArrayDimmingState;
    private ArrayList<Integer>                      mArrayImage;
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
    private LinearLayout                            mLightLinListParent;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mLightPosition;
    private int                                     mLightGroupFlag;
    private String                                  mLightGroupState;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitGroupCount         = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1000;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 20;   // 40 * 500msec = 20sec
    //**********************************************************************************************

    private int                                     mDataSendFlag           = 0;

    //**********************************************************************************************

    private ArrayList<LinearLayout> parentLinViews = new ArrayList<>();
    private ArrayList<TextView> titleTxtViews = new ArrayList<>();
    private ArrayList<Switch> controlSwhViews = new ArrayList<>();
    private ArrayList<LinearLayout> visibleLinViews = new ArrayList<>();
    private ArrayList<ImageView> minusImgViews = new ArrayList<>();
    private ArrayList<ImageView> plusImgViews = new ArrayList<>();
    private ArrayList<LinearLayout> lightLinViews01 = new ArrayList<>();
    private ArrayList<LinearLayout> lightLinViews02 = new ArrayList<>();
    private ArrayList<LinearLayout> lightLinViews03 = new ArrayList<>();
    private ArrayList<LinearLayout> lightLinViews04 = new ArrayList<>();
    private ArrayList<LinearLayout> lightLinViews05 = new ArrayList<>();
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
        setContentView(R.layout.activity_control_light);

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
        mLightResponse = new Messenger(responseHandler);
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
        mLightLinListParent     = (LinearLayout)findViewById(R.id.Light_Lin_List_Parent);
        //******************************************************************************************

        //******************************************************************************************

        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayState         = new ArrayList<>();
        mArrayDimming       = new ArrayList<>();
        mArrayDimmingState  = new ArrayList<>();
        mArrayImage         = new ArrayList<>();

        mLightPosition = 1;
        mLightGroupFlag = 0;
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
        tMsg.replyTo = mLightResponse;
        sendMessage(tMsg);
        mLightRequest = null;
        mDataSendFlag = 0;
        TimeHandlerLight(false, TIMER_NULL);
        TimeHandlerLightGroup(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(LightRunner);
        }
        if (mTimeHandlerGroup != null){
            mTimeHandlerGroup.removeCallbacks(LightGroupRunner);
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
            mLightRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mLightResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerLight(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mLightResponse;
            sendMessage(tMsg);
            mLightRequest = null;
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
                case    Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST:
                    LightStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST:
                    LightControlResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST:
                    LightControlResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mLightRequest, mLightResponse, LightActivity.this);
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
            if(mLightRequest != null) {
                mLightRequest.send(tMsg);
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
    private void TimeHandlerLight(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(LightRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler light group control
     * @param tState
     * @param tTime
     */
    private void TimeHandlerLightGroup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerGroup == null){
                mTimeHandlerGroup = new Handler();
            }

            mTimeHandlerGroup.postDelayed(LightGroupRunner, tTime);
        }else{
            mTimeHandlerGroup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable LightRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                Log.i(TAG,"runner aaaa : " + mRequestState);
                if(mRequestState == REQUEST_DATA_SEND_START){
                    Log.i(TAG,"runner aaaa");
                    LightStateRequest();
                    mTimeHandler.postDelayed(LightRunner, TIMER_REQUEST);
                    Log.i(TAG,"runner bbbb");
                }else{
                    mWaitCount++;
                    Log.e("light timer", String.valueOf(mWaitCount));
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mWaitGroupCount = 0;
                        mLightGroupFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerLight(false, TIMER_NULL);
                        TimeHandlerLightGroup(false, TIMER_NULL);
                        LightSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        mTimeHandler.postDelayed(LightRunner, TIMER_REQUEST);
                    }
                }

                if(mDataSendFlag == 1){
                    LightStateRequest();
                    mDataSendFlag = 0;
                }
            }else{
                TimeHandlerLight(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light group data timer
     */
    private Runnable LightGroupRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerGroup != null){
                Log.e("light group timer", String.valueOf(mWaitGroupCount));
                mWaitGroupCount++;
                if(mWaitGroupCount > TIMER_WAIT_TIME){
                    mWaitCount = 0;
                    mDataSendFlag = 0;
                    mWaitGroupCount = 0;
                    mLightGroupFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mProgressDialog.Dismiss();
                    TimeHandlerLight(false, TIMER_NULL);
                    TimeHandlerLightGroup(false, TIMER_NULL);
                    LightSocketClose();
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else{
                    mTimeHandlerGroup.postDelayed(LightGroupRunner, TIMER_REQUEST);
                }
            } else{
                mWaitGroupCount = 0;
                TimeHandlerLightGroup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light socket close
     */
    private void LightSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mLightResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light state request
     */
    private void LightStateRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mLightResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST);
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
     * @breif Light each control request communication
     * @param tNum
     */
    private void LightEachControlRequest(int tNum){
        mWaitCount      = 0;
        mLightGroupFlag = 2;
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerLight(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mLightResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(tNum));
        bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(tNum));

        if(mArrayState.get(tNum).equals("On")){
            mLightGroupState = "Off";
            bundle.putString(Constants.KD_DATA_ONOFF, "Off");
        }else{
            mLightGroupState = "On";
            bundle.putString(Constants.KD_DATA_ONOFF, "On");
        }
        bundle.putString(Constants.KD_DATA_DIMMINGLEVEL, mArrayDimming.get(tNum));

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light all control on off
     * @param tState
     */
    private void LightGroupControlRequest(String tState,String level){
        String tDimmingLevel = level;
        int    tDimmingLevelInt = 0;
        int    tDimmingLevelCompare = 0;

        for(int i = 0; i < mArrayDimming.size(); i++){
            tDimmingLevelInt = Integer.parseInt(mArrayDimming.get(i));
            if(tDimmingLevelCompare <= tDimmingLevelInt){
                tDimmingLevelCompare = tDimmingLevelInt;
                tDimmingLevel = String.valueOf(tDimmingLevelCompare);
            }
        }

        mWaitCount      = 0;
        mWaitGroupCount = 0;
        mLightGroupFlag = 1;
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerLight(true, TIMER_REQUEST);
        TimeHandlerLightGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mLightResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
        bundle.putString(Constants.KD_DATA_ONOFF, tState);
        bundle.putString(Constants.KD_DATA_DIMMINGLEVEL, tDimmingLevel);

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light state request result
     * @param tKDData
     */
    private void LightStateResult(KDData tKDData){

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            HNMLDataParserLight(tKDData.ReceiveString);
            if(mLightGroupFlag == 1){
                for(int i = 0; i < mArrayState.size(); i++){
                    if(!mArrayState.get(i).equals(mLightGroupState)){
                        mDataSendFlag = 1;
                        Log.e("light group control", "request start");
                        break;
                    }else{
                        if(mArrayState.size()-1 == i){
                            TimeHandlerLightGroup(false, TIMER_NULL);
                            mDataSendFlag = 0;
                            mLightGroupFlag = 0;
                            mProgressDialog.Dismiss();
                            mWaitCount = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerLight(false, TIMER_NULL);
                        }
                    }
                }
            }else if(mLightGroupFlag == 2){
                if(!mArrayState.get(mLightPosition).equals(mLightGroupState)){
                    mDataSendFlag = 1;
                    Log.e("light each control", "request start");
                }else{
                    mDataSendFlag = 0;
                    mLightGroupFlag = 0;
                    mProgressDialog.Dismiss();
                    mWaitCount = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerLight(false, TIMER_NULL);
                }
            }else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerLight(false, TIMER_NULL);
                TimeHandlerLightGroup(false, TIMER_NULL);
                mProgressDialog.Dismiss();
            }
        } else{
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerLight(false, TIMER_NULL);
            TimeHandlerLightGroup(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light each control request result
     * @param tKDData
     */
    private void LightControlResult(KDData tKDData){
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            //mRequestState = REQUEST_DATA_SEND_START;
            mDataSendFlag = 1;
        }else{
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerLight(false, TIMER_NULL);
            TimeHandlerLightGroup(false, TIMER_NULL);
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
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
//        Intent intent = new Intent(LightActivity.this, MainFragment.class);
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
//            Intent intent = new Intent(LightActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif spinner click item
     * @param parent
     * @param v
     * @param position
     * @param id
     */
    //**********************************************************************************************
    /**
     * @breif light activity button
     * @param v
     */
    public void OnClickBtnLight(View v){
        switch (v.getId()){
            case    R.id.Light_Lin_Home:
                onBackPressed();
                break;
            case    R.id.Light_Btn_Menu:
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
     * @breif hnml data parser login init 1
     * @param String tContents
     */
    public void HNMLDataParserLight(String tContents) {
        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayState         = new ArrayList<>();
        mArrayDimming       = new ArrayList<>();
        mArrayDimmingState  = new ArrayList<>();
        mArrayImage         = new ArrayList<>();

        if (tContents != null) {
            mArrayGroupID.add("1");
            mArraySubID.add("1");
            mArrayName.add(getString(R.string.Light_textview_all));
            mArrayState.add("");
            mArrayDimming.add("");
            mArrayDimmingState.add("");

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
                            }else if(tName.equals("OnOffStatus")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayState.add("");
                                }else{
                                    mArrayState.add(tParser.getText());
                                }
                            }else if(tName.equals("DimmingLevel")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayDimming.add("");
                                }else{
                                    mArrayDimming.add(tParser.getText());
                                }
                            }else if(tName.equals("DimmingFunction")){
                                // 1 - 디밍기능있음, 0 - 디밍기능없음
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayDimmingState.add("");
                                }else{
                                    mArrayDimmingState.add(tParser.getText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if(tName.equals("DeviceName")){
                                mArrayName.add("");
                            }else if(tName.equals("DimmingLevel")){
                                mArrayDimming.add("");
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
                mArrayState.set(0, "Off");
                mArrayDimming.set(0, mArrayDimming.get(1));
                mArrayDimmingState.set(0, mArrayDimmingState.get(1));

                for (int i = 0; i < mArrayName.size(); i++) {
                    if (mArrayState.get(i).equals("On")) {
                        mArrayImage.add(R.drawable.img_icon_light_select);
                    } else {
                        mArrayImage.add(R.drawable.img_icon_light);
                    }
                }

                for(int i = 1; i < mArrayState.size(); i++){
                    if(mArrayState.get(i).equals("On")){
                        mArrayState.set(0, "On");
                        break;
                    }
                }

                if (mLightLinListParent.getChildCount() == 1){
                    for (int i = 0; i < mArrayState.size(); i++){
                        LightListMake(true,i,mArrayState.get(i),mArrayName.get(i),mArrayDimmingState.get(i),mArrayDimming.get(i));
                    }
                }else{
                    for (int i = 0; i < mArrayState.size(); i++){
                        LightListMake(false,i,mArrayState.get(i),mArrayName.get(i),mArrayDimmingState.get(i),mArrayDimming.get(i));
                    }
                }

            }else{
                mArrayGroupID       = new ArrayList<>();
                mArraySubID         = new ArrayList<>();
                mArrayName          = new ArrayList<>();
                mArrayState         = new ArrayList<>();
                mArrayDimming       = new ArrayList<>();
                mArrayDimmingState  = new ArrayList<>();
                mArrayImage         = new ArrayList<>();

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    void LightListMake(boolean check, final int position, final String state, String name, final String dimmingFunction, final String dimmingLevel){
        LinearLayout linearParent = null;
        TextView txtTitle = null;
        Switch swhControl = null;
        LinearLayout linearVisible = null;
        ImageView imgMinus = null;
        ImageView imgPlus = null;
        LinearLayout linearLight01 = null;
        LinearLayout linearLight02 = null;
        LinearLayout linearLight03 = null;
        LinearLayout linearLight04 = null;
        LinearLayout linearLight05 = null;

        if (check) {
            View listView = new View(this);
            listView = getLayoutInflater().inflate(R.layout.view_control_light_layout, null);
            LinearLayout linearParent1 = (LinearLayout) listView.findViewById(R.id.Light_Lin_List_Item_Parent);
            TextView txtTitle1 = (TextView) listView.findViewById(R.id.Light_Txt_List_Item_Title);
            Switch swhControl1 = (Switch) listView.findViewById(R.id.Light_Swh_List_Item);
            linearVisible = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Visible);
            imgMinus = (ImageView)listView.findViewById(R.id.Light_Img_List_Item_Minus);
            imgPlus = (ImageView)listView.findViewById(R.id.Light_Img_List_Item_Plus);
            linearLight01 = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Light01);
            linearLight02 = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Light02);
            linearLight03 = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Light03);
            linearLight04 = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Light04);
            linearLight05 = (LinearLayout)listView.findViewById(R.id.Light_Lin_List_Item_Light05);

            mLightLinListParent.addView(listView);

            linearParent = linearParent1;
            txtTitle = txtTitle1;
            swhControl = swhControl1;

            parentLinViews.add(linearParent);
            titleTxtViews.add(txtTitle);
            controlSwhViews.add(swhControl);
            visibleLinViews.add(linearVisible);
            minusImgViews.add(imgMinus);
            plusImgViews.add(imgPlus);
            lightLinViews01.add(linearLight01);
            lightLinViews02.add(linearLight02);
            lightLinViews03.add(linearLight03);
            lightLinViews04.add(linearLight04);
            lightLinViews05.add(linearLight05);
        }

        if (txtTitle == null){
            linearParent = parentLinViews.get(position);
            txtTitle = titleTxtViews.get(position);
            swhControl = controlSwhViews.get(position);
            linearVisible = visibleLinViews.get(position);
            imgMinus = minusImgViews.get(position);
            imgPlus = plusImgViews.get(position);
            linearLight01 = lightLinViews01.get(position);
            linearLight02 = lightLinViews01.get(position);
            linearLight03 = lightLinViews01.get(position);
            linearLight04 = lightLinViews01.get(position);
            linearLight05 = lightLinViews01.get(position);
        }

        txtTitle.setText(name);

        if (state.equals("On")) {
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
            swhControl.setChecked(true);

            if (position != 0 && dimmingFunction.equals("1")){
                linearVisible.setVisibility(View.VISIBLE);
                if (dimmingLevel.equals("1") || dimmingLevel.equals("2")){
                    linearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }else if (dimmingLevel.equals("3") || dimmingLevel.equals("4")){
                    linearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }else if (dimmingLevel.equals("5") || dimmingLevel.equals("6")){
                    linearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight03.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }else if (dimmingLevel.equals("7") || dimmingLevel.equals("8")){
                    linearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight03.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight04.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }else if (dimmingLevel.equals("9") || dimmingLevel.equals("10")){
                    linearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight03.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight04.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    linearLight05.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                }
            }
        } else {
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
            swhControl.setChecked(false);
        }

        Log.i(TAG,"state : " + state);

        final Switch finalSwhControl = swhControl;
        swhControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mArrayName.size() != 0) {
                    if (position != 0) {
                        mLightPosition = position;
                        LightEachControlRequest(position);
                    } else {
                        if (mArrayState.get(1).equals("On")) {
                            mLightGroupState = "Off";
                            LightGroupControlRequest("Off",dimmingLevel);
                        } else {
                            mLightGroupState = "On";
                            LightGroupControlRequest("On",dimmingLevel);
                        }
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(LightActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });

        final LinearLayout finalLinearVisible = linearVisible;
        swhControl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    if (position != 0 && dimmingFunction.equals("1")){
                        finalLinearVisible.setVisibility(View.VISIBLE);
                    }
                }else{
                    finalLinearVisible.setVisibility(View.GONE);
                }
            }
        });

        final LinearLayout finalLinearLight01 = linearLight01;
        final LinearLayout finalLinearLight02 = linearLight02;
        final LinearLayout finalLinearLight03 = linearLight03;
        final LinearLayout finalLinearLight04 = linearLight04;
        final LinearLayout finalLinearLight05 = linearLight05;
        final String[] finalDimmingLevel = {dimmingLevel};
        imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(dimmingLevel) < 3){

                }else if (Integer.parseInt(dimmingLevel) < 5){
                    finalLinearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorfafafa));
                    finalDimmingLevel[0] = "1";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) < 7){
                    finalLinearLight03.setBackgroundTintList(getResources().getColorStateList(R.color.colorfafafa));
                    finalDimmingLevel[0] = "3";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) < 9){
                    finalLinearLight04.setBackgroundTintList(getResources().getColorStateList(R.color.colorfafafa));
                    finalDimmingLevel[0] = "5";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) < 11){
                    finalLinearLight04.setBackgroundTintList(getResources().getColorStateList(R.color.colorfafafa));
                    finalDimmingLevel[0] = "7";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }
            }
        });

        imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(dimmingLevel) >8){

                }else if (Integer.parseInt(dimmingLevel) >6){
                    finalLinearLight05.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    finalDimmingLevel[0] = "9";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) >4){
                    finalLinearLight04.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    finalDimmingLevel[0] = "7";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) >2){
                    finalLinearLight03.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    finalDimmingLevel[0] = "5";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else if (Integer.parseInt(dimmingLevel) >0){
                    finalLinearLight02.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    finalDimmingLevel[0] = "3";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }else{
                    finalLinearLight01.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                    finalDimmingLevel[0] = "1";
                    LightGroupControlRequest("On",finalDimmingLevel[0]);
                }
            }
        });
    }
}
