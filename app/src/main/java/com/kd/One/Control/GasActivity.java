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
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kd.One.Common.TimeOutMoving;
import com.kd.One.R;
import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-25.
 */
public class GasActivity extends Activity{
    private String TAG = "GasActivity";
    //**********************************************************************************************
    private Messenger                               mGasResponse        = null;
    private Messenger                               mGasRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig mLocalConfig;
    public MyGlobal mMyGlobal;
    private Handler                                 mTimeHandler;
    private Handler                                 mTimeHandlerEachGroup;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mGasPosition            = 0;
    private int                                     mGasEachFlag            = 0;
    private int                                     mGasGroupFlag           = 0;
    //**********************************************************************************************

    //**********************************************************************************************

    private LinearLayout                            mGasLinListParent;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String>                       mArrayGroupID;
    private ArrayList<String>                       mArraySubID;
    private ArrayList<String>                       mArrayName;
    private ArrayList<String>                       mArrayState;
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
    private ViewGroup                               mViewGroupEMS;
    private ViewGroup                               mViewGroupVisitor;
    private ViewGroup                               mViewGroupNotice;
    private ViewGroup                               mViewGroupHomeView;
    private boolean                                 mIsSlideOpen;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitEachGroupCount     = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1000;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 40;   // 40 * 500msec = 20sec
    //**********************************************************************************************

    private int                                     mDataSendFlag = 0;

    //**********************************************************************************************
    private ArrayList<LinearLayout> parentLinViews = new ArrayList<>();
    private ArrayList<TextView> titleTxtViews = new ArrayList<>();
    private ArrayList<Switch> controlSwhViews = new ArrayList<>();
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
        setContentView(R.layout.activity_control_gas);

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
        mGasResponse = new Messenger(responseHandler);
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
        mGasLinListParent     = (LinearLayout)findViewById(R.id.Gas_Lin_List_Parent);
        //******************************************************************************************

        //******************************************************************************************

        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayState         = new ArrayList<>();
        mArrayImage         = new ArrayList<>();

        mGasPosition = 1;
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
        tMsg.replyTo = mGasResponse;
        sendMessage(tMsg);
        mGasRequest = null;
        mDataSendFlag = 0;
        TimeHandlerGas(false, TIMER_NULL);
        TimeHandlerGasEachGroup(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(GasRunner);
        }
        if (mTimeHandlerEachGroup != null){
            mTimeHandlerEachGroup.removeCallbacks(GasEachGroupRunner);
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
            mGasRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mGasResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerGas(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mGasResponse;
            sendMessage(tMsg);
            mGasRequest = null;
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
                case    Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST:
                    GasStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST:
                    GasControlResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST:
                    GasControlResult((KDData)msg.obj);
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
            Log.e("GasActivity", "BroadCast Receive");
            if (action.equals(Constants.ACTION_APP_FINISH)) {
                finish();
            } else if (action.equals(Constants.ACTION_APP_NETWORK_ERROR)) {
            } else if( action.equals(Constants.ACTION_APP_SOCKET_CLOSE)){
            } else if( action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)){
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                TimeOutMoving.TimeOutMoving(mGasRequest, mGasResponse, GasActivity.this);
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
            if(mGasRequest != null) {
                mGasRequest.send(tMsg);
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
    private void TimeHandlerGas(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(GasRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    private void TimeHandlerGasEachGroup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerEachGroup == null){
                mTimeHandlerEachGroup = new Handler();
            }
            mTimeHandlerEachGroup.postDelayed(GasEachGroupRunner, tTime);
        }else{
            mTimeHandlerEachGroup = null;
        }
    }

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable GasRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    GasStateRequest();
                    mTimeHandler.postDelayed(GasRunner, TIMER_REQUEST);
                }else{
                    Log.e("gas timer", String.valueOf(mWaitCount));
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitEachGroupCount = 0;
                        mGasEachFlag = 0;
                        mGasGroupFlag = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerGas(false, TIMER_NULL);
                        TimeHandlerGasEachGroup(false, TIMER_NULL);
                        GasSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        mTimeHandler.postDelayed(GasRunner, TIMER_REQUEST);
                    }

                    if(mDataSendFlag == 1){
                        GasStateRequest();
                        mDataSendFlag = 0;
//                        TimeHandlerGas(false, TIMER_NULL);
//                        TimeHandlerGasEachGroup(false,TIMER_NULL);
//                        mProgressDialog.Dismiss();
                    }
                }
            }else{
                TimeHandlerGas(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light group data timer
     */
    private Runnable GasEachGroupRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerEachGroup != null){
                Log.e("gas group timer", String.valueOf(mWaitEachGroupCount));
                mWaitEachGroupCount++;
                if(mWaitEachGroupCount > TIMER_WAIT_TIME){
                    mWaitCount = 0;
                    mWaitEachGroupCount = 0;
                    mGasEachFlag = 0;
                    mGasGroupFlag = 0;
                    mDataSendFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mProgressDialog.Dismiss();
                    TimeHandlerGas(false, TIMER_NULL);
                    TimeHandlerGasEachGroup(false, TIMER_NULL);
                    GasSocketClose();
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else{
                    mTimeHandlerEachGroup.postDelayed(GasEachGroupRunner, TIMER_REQUEST);
                }
            } else{
                mWaitEachGroupCount = 0;
                TimeHandlerGasEachGroup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif gas socket close
     */
    private void GasSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mGasResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Gas state request
     */
    private void GasStateRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerGas(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mGasResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST);
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
     * @breif gas each control request communication
     * @param tNum
     */
    private void GasEachControlRequest(int tNum){
        mWaitCount    = 0;
        mGasEachFlag  = 1;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerGas(true, TIMER_REQUEST);
        TimeHandlerGasEachGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mGasResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(tNum));
        bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(tNum));

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif gas all control on off
     * @param tState
     */
    private void GasGroupControlRequest(){
        mWaitCount      = 0;
        mGasGroupFlag   = 1;
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerGas(true, TIMER_REQUEST);
        TimeHandlerGasEachGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mGasResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif GAS state request result
     * @param tKDData
     */
    private void GasStateResult(KDData tKDData){
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            HNMLDataParserGas(tKDData.ReceiveString);
            if(mGasGroupFlag == 1){
                for(int i = 0; i < mArrayState.size(); i++){
                    if(!mArrayState.get(i).equals("Close")){
                        //mRequestState   = REQUEST_DATA_SEND_START;
                        mDataSendFlag = 1;
                        break;
                    }else{
                        if(mArrayState.size()-1 == i){
                            TimeHandlerGasEachGroup(false, TIMER_NULL);
                            mGasEachFlag = 0;
                            mGasGroupFlag = 0;
                            mDataSendFlag = 0;
                            mProgressDialog.Dismiss();
                            mWaitCount = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerGas(false, TIMER_NULL);
                        }
                    }
                }
            }else if(mGasEachFlag == 1){
                if(!mArrayState.get(mGasPosition).equals("Close")){
                    //mRequestState   = REQUEST_DATA_SEND_START;
                    mDataSendFlag = 1;
                }else{
                    TimeHandlerGasEachGroup(false, TIMER_NULL);
                    mGasEachFlag = 0;
                    mGasGroupFlag = 0;
                    mDataSendFlag = 0;
                    mProgressDialog.Dismiss();
                    mWaitCount = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerGas(false, TIMER_NULL);
                }
            }else {
                mProgressDialog.Dismiss();
                mWaitCount = 0;
                mDataSendFlag = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerGas(false, TIMER_NULL);
                TimeHandlerGasEachGroup(false, TIMER_NULL);
            }
        } else{
            mProgressDialog.Dismiss();
            mWaitCount = 0;
            mDataSendFlag = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerGas(false, TIMER_NULL);
            TimeHandlerGasEachGroup(false, TIMER_NULL);
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
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
    private void GasControlResult(KDData tKDData){
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mDataSendFlag = 1;
        }else{
            mWaitCount = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerGas(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
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
//        Intent intent = new Intent(GasActivity.this, MainFragment.class);
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
//            Intent intent = new Intent(GasActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif item click selected
     * @param parent
     * @param v
     * @param position
     * @param id
     */
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif light activity button
     * @param v
     */
    public void OnClickBtnGas(View v){
        switch (v.getId()){
            case    R.id.Gas_Lin_Home:
                onBackPressed();
                break;
            case    R.id.Gas_Btn_Menu:
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
     * @breif hnml data parser gas
     * @param String tContents
     */
    public void HNMLDataParserGas(String tContents) {
        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayState         = new ArrayList<>();
        mArrayImage         = new ArrayList<>();

        if (tContents != null) {
            mArrayGroupID.add("1");
            mArraySubID.add("1");
            mArrayName.add(getString(R.string.Gas_textview_all));
            mArrayState.add("");

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
                            }else if(tName.equals("GasValveStatus")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayState.add("");
                                }else{
                                    mArrayState.add(tParser.getText());
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
                mArrayName.set(0, getString(R.string.Gas_textview_all));
                mArrayState.set(0, "Close");

                for (int i = 0; i < mArrayName.size(); i++) {
                    if (mArrayState.get(i).equals("On")) {
                        mArrayImage.add(R.drawable.img_icon_light_select);
                    } else {
                        mArrayImage.add(R.drawable.img_icon_light);
                    }
                }

                for(int i = 1; i < mArrayState.size(); i++){
                    if(mArrayState.get(i).equals("Open")){
                        mArrayState.set(0, "Open");
                        break;
                    }
                }

                if (mGasLinListParent.getChildCount() == 1){
                    for (int i = 0; i < mArrayState.size(); i++){
                        GasListMake(true,i,mArrayState.get(i),mArrayName.get(i));
                    }
                }else{
                    for (int i = 0; i < mArrayState.size(); i++){
                        GasListMake(false,i,mArrayState.get(i),mArrayName.get(i));
                    }
                }
            }else{
                mArrayGroupID       = new ArrayList<>();
                mArraySubID         = new ArrayList<>();
                mArrayName          = new ArrayList<>();
                mArrayState         = new ArrayList<>();
                mArrayImage         = new ArrayList<>();

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    void GasListMake(boolean check, final int position, final String state, String name){
        LinearLayout linearParent = null;
        TextView txtTitle = null;
        Switch swhControl = null;

        if (check) {
            View listView = new View(this);
            listView = getLayoutInflater().inflate(R.layout.view_control_gas_layout, null);
            LinearLayout linearParent1 = (LinearLayout) listView.findViewById(R.id.Gas_Lin_List_Item_Parent);
            TextView txtTitle1 = (TextView) listView.findViewById(R.id.Gas_Txt_List_Item_Title);
            Switch swhControl1 = (Switch) listView.findViewById(R.id.Gas_Swh_List_Item);

            mGasLinListParent.addView(listView);

            linearParent = linearParent1;
            txtTitle = txtTitle1;
            swhControl = swhControl1;

            parentLinViews.add(linearParent);
            titleTxtViews.add(txtTitle);
            controlSwhViews.add(swhControl);
        }

        if (txtTitle == null){
            linearParent = parentLinViews.get(position);
            txtTitle = titleTxtViews.get(position);
            swhControl = controlSwhViews.get(position);
        }

        txtTitle.setText(name);

        Log.i(TAG,"state : " + state);

        if (state.equals("Close")) {
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
            swhControl.setChecked(false);
            swhControl.setEnabled(false);
        } else {
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
            swhControl.setChecked(true);
            swhControl.setEnabled(true);
        }

        final Switch finalSwhControl = swhControl;
        swhControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGasPosition = position;
                if (!finalSwhControl.isChecked()){
                    if(mArrayName.size() != 0) {
                        if (position != 0) {
                            if (!state.equals("Close")) {
                                Log.i(TAG,"aaaa");
                                GasEachControlRequest(position);
                            }
                            Log.i(TAG,"bbbb");
                        } else {
//                            for(int i = 0; i < mArrayState.size(); i++){
                                if(!state.equals("Close")){
                                    GasGroupControlRequest();
                                }
//                            }
                        }
                    }else{
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(GasActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }
                    finalSwhControl.setEnabled(false);
                }
            }
        });
    }
}
