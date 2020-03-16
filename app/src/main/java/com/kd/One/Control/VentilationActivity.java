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
public class VentilationActivity extends Activity{

    private String TAG = "VentilationActivity";
    //**********************************************************************************************
    private Messenger                               mVentilationResponse        = null;
    private Messenger                               mVentilationRequest         = null;
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
    private ArrayList<String>                       mArrayState;
    private ArrayList<String>                       mArrayWind;
    private ArrayList<String>                       mArrayMode;
    private ArrayList<String>                       mArrayFilter;
    private ArrayList<String>                       mArrayPollution;
    private ArrayList<String>                       mArrayError;
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
    private LinearLayout                            mVentilationLinListParent;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mVentilationPosition;
    private int                                     mVentilationGroupFlag;
    private String                                  mVentilationGroupState;
    private String                                  mVentilationSendData;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitGroupCount         = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 500;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 40;   // 40 * 500msec = 20sec
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     VENTILATION_POWER       = "PowerStatus";
    private static final String                     VENTILATION_WIND        = "WindPower";
    private static final String                     VENTILATION_MODE        = "Mode";
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     VENTILATION_ON          = "On";
    private static final String                     VENTILATION_OFF         = "Off";
    //**********************************************************************************************

    private int                                     mDataSendFlag           = 0;

    //**********************************************************************************************

    private ArrayList<LinearLayout> parentLinViews = new ArrayList<>();
    private ArrayList<TextView> titleTxtViews = new ArrayList<>();
    private ArrayList<Switch> controlSwhViews = new ArrayList<>();
    private ArrayList<LinearLayout> visibleLinViews = new ArrayList<>();
    private ArrayList<TextView> filterTxtViews = new ArrayList<>();
    private ArrayList<TextView> windTxtViews = new ArrayList<>();
    private ArrayList<TextView> modeTxtViews = new ArrayList<>();
    /**
     * @breif oncreate ventilation activity
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
        setContentView(R.layout.activity_control_ventilation);

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
        mVentilationResponse = new Messenger(responseHandler);
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
        mVentilationLinListParent       = (LinearLayout)findViewById(R.id.Ventilation_Lin_List_Parent);
        //******************************************************************************************

        //******************************************************************************************

        mArrayGroupID       = new ArrayList<>();
        mArraySubID         = new ArrayList<>();
        mArrayName          = new ArrayList<>();
        mArrayState         = new ArrayList<>();
        mArrayWind          = new ArrayList<>();
        mArrayMode          = new ArrayList<>();
        mArrayFilter        = new ArrayList<>();
        mArrayPollution     = new ArrayList<>();
        mArrayError         = new ArrayList<>();

        mVentilationPosition = 1;
        mVentilationGroupFlag = 0;
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
        tMsg.replyTo = mVentilationResponse;
        sendMessage(tMsg);
        mDataSendFlag = 0;
        mVentilationRequest = null;
        TimeHandlerVentilation(false, TIMER_NULL);
        TimeHandlerVentilationGroup(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(VentilationRunner);
        }
        if (mTimeHandlerGroup != null){
            mTimeHandlerGroup.removeCallbacks(VentilationGroupRunner);
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
            mVentilationRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mVentilationResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerVentilation(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mVentilationResponse;
            sendMessage(tMsg);
            mVentilationRequest = null;
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
                case    Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST:
                    VentilationStateResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST:
                    VentilationControlResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST:
                    VentilationControlResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mVentilationRequest, mVentilationResponse, VentilationActivity.this);
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
            if(mVentilationRequest != null) {
                mVentilationRequest.send(tMsg);
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
    private void TimeHandlerVentilation(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(VentilationRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler ventilation group control
     * @param tState
     * @param tTime
     */
    private void TimeHandlerVentilationGroup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerGroup == null){
                mTimeHandlerGroup = new Handler();
            }

            mTimeHandlerGroup.postDelayed(VentilationGroupRunner, tTime);
        }else{
            mTimeHandlerGroup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable VentilationRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    VentilationStateRequest();
                    TimeHandlerVentilation(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mWaitGroupCount = 0;
                        mVentilationGroupFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerVentilation(false, TIMER_NULL);
                        TimeHandlerVentilationGroup(false, TIMER_NULL);
                        VentilationSocketClose();
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerVentilation(true, TIMER_REQUEST);
                    }
                }

                if(mDataSendFlag == 1){
                    VentilationStateRequest();
                    mDataSendFlag = 0;
                }
            }else{
                TimeHandlerVentilation(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Ventilation group data timer
     */
    private Runnable VentilationGroupRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerGroup != null){
                mWaitGroupCount++;
                if(mWaitGroupCount > TIMER_WAIT_TIME){
                    mWaitCount = 0;
                    mDataSendFlag = 0;
                    mWaitGroupCount = 0;
                    mVentilationGroupFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mProgressDialog.Dismiss();
                    TimeHandlerVentilation(false, TIMER_NULL);
                    TimeHandlerVentilationGroup(false, TIMER_NULL);
                    VentilationSocketClose();
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else{
                    TimeHandlerVentilationGroup(true, TIMER_REQUEST);
                }
            } else{
                mWaitGroupCount = 0;
                TimeHandlerVentilationGroup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif ventilation socket close
     */
    private void VentilationSocketClose(){
        Message tMsg = Message.obtain();
        tMsg.replyTo = mVentilationResponse;
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
    public void VentilationStateRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVentilationResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST);
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
     * @breif ventilation each control request communication
     * @param tNum
     */
    public void VentilationEachControlRequest(String status){
        mWaitCount              = 0;
        mVentilationGroupFlag   = 1;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVentilation(true, TIMER_REQUEST);
        TimeHandlerVentilationGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVentilationResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, mArrayGroupID.get(mVentilationPosition));
        bundle.putString(Constants.KD_DATA_SUB_ID, mArraySubID.get(mVentilationPosition));

        bundle.putString(Constants.KD_DATA_VENTILATION_EACH_STATE, VENTILATION_POWER);
        if (status.equals(VENTILATION_ON)) {
            mVentilationSendData = VENTILATION_OFF;
            bundle.putString(Constants.KD_DATA_VENTILATION_POWER, VENTILATION_OFF);
        } else {
            mVentilationSendData = VENTILATION_ON;
            bundle.putString(Constants.KD_DATA_VENTILATION_POWER, VENTILATION_ON);
        }

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif ventilation group control
     * @param tMode
     */
    public void VentilationGroupModeControlRequest(String status){
        mWaitCount              = 0;
        mVentilationGroupFlag   = 2;
        mRequestState           = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVentilation(true, TIMER_REQUEST);
        TimeHandlerVentilationGroup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVentilationResponse;
        tMsg.what    = Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_GROUP_ID, "All");
        bundle.putString(Constants.KD_DATA_VENTILATION_EACH_STATE, VENTILATION_POWER);

        if (status.equals(VENTILATION_ON)) {
            mVentilationSendData = VENTILATION_OFF;
            bundle.putString(Constants.KD_DATA_VENTILATION_POWER, VENTILATION_OFF);
        } else {
            mVentilationSendData = VENTILATION_ON;
            bundle.putString(Constants.KD_DATA_VENTILATION_POWER, VENTILATION_ON);
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
    private void VentilationStateResult(KDData tKDData){
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserVentilation(tKDData.ReceiveString);
                if (mVentilationGroupFlag == 1) {
                    // Each control
                    if (mVentilationSendData.equals(mArrayState.get(mVentilationPosition))) {
                        mWaitGroupCount = 0;
                        mVentilationGroupFlag = 0;
                        mProgressDialog.Dismiss();
                        mWaitCount = 0;
                        mDataSendFlag = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerVentilation(false, TIMER_NULL);
                        TimeHandlerVentilationGroup(false, TIMER_NULL);
                    } else {
                        mDataSendFlag = 1;
                        Log.e("ventilation", "fail");
                    }
                } else if (mVentilationGroupFlag == 2) {
                    // Group control
                    for (int i = 0; i < mArrayState.size(); i++) {
                        if (!mArrayState.get(i).equals(mVentilationSendData)) {
                            mDataSendFlag = 1;
                            Log.e("ventilation", "group fail");
                            break;
                        } else {
                            if (mArrayState.size() - 1 == i) {
                                mDataSendFlag = 0;
                                mWaitGroupCount = 0;
                                mVentilationGroupFlag = 0;
                                mProgressDialog.Dismiss();

                                mWaitCount = 0;
                                mRequestState = REQUEST_DATA_CLEAR;
                                TimeHandlerVentilation(false, TIMER_NULL);
                                TimeHandlerVentilationGroup(false, TIMER_NULL);
                            }
                        }
                    }
                } else {
                    mDataSendFlag = 0;
                    mWaitCount = 0;
                    mWaitGroupCount = 0;
                    mVentilationGroupFlag = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    mProgressDialog.Dismiss();

                    TimeHandlerVentilation(false, TIMER_NULL);
                    TimeHandlerVentilationGroup(false, TIMER_NULL);
                }
            } else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mWaitGroupCount = 0;
                mVentilationGroupFlag = 0;
                mProgressDialog.Dismiss();
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerVentilation(false, TIMER_NULL);
                TimeHandlerVentilationGroup(false, TIMER_NULL);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mDataSendFlag = 0;
            mWaitGroupCount = 0;
            mVentilationGroupFlag = 0;
            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerVentilation(false, TIMER_NULL);
            TimeHandlerVentilationGroup(false, TIMER_NULL);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif Ventilation each & group control request result
     * @param tKDData
     */
    private void VentilationControlResult(KDData tKDData){
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                mDataSendFlag = 1;
            } else {
                mWaitCount = 0;
                mDataSendFlag = 0;
                mWaitGroupCount = 0;
                mVentilationGroupFlag = 0;
                mProgressDialog.Dismiss();
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerVentilation(false, TIMER_NULL);
                TimeHandlerVentilationGroup(false, TIMER_NULL);
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mDataSendFlag = 0;
            mWaitGroupCount = 0;
            mVentilationGroupFlag = 0;
            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerVentilation(false, TIMER_NULL);
            TimeHandlerVentilationGroup(false, TIMER_NULL);
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
//        Intent intent = new Intent(VentilationActivity.this, MainFragment.class);
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
//            Intent intent = new Intent(VentilationActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif onclick ventilation
     * @param v
     */
    public void OnClickBtnVentilation(View v){
        switch(v.getId()){
            case    R.id.Ventilation_Lin_Home:
                onBackPressed();
                break;
            case    R.id.Ventilation_Btn_Menu:
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
     * @breif hnml data parser Stand by power
     * @param String tContents
     */
    public void HNMLDataParserVentilation(String tContents) {
        mArrayGroupID               = new ArrayList<>();
        mArraySubID                 = new ArrayList<>();
        mArrayName                  = new ArrayList<>();
        mArrayState                 = new ArrayList<>();
        mArrayWind                  = new ArrayList<>();
        mArrayMode                  = new ArrayList<>();
        mArrayFilter                = new ArrayList<>();
        mArrayPollution             = new ArrayList<>();
        mArrayError                 = new ArrayList<>();

        if (tContents != null) {
            mArrayGroupID.add("1");
            mArraySubID.add("1");
            mArrayName.add(getString(R.string.Standbypower_textview_all));
            mArrayState.add("");
            mArrayWind.add("");
            mArrayMode.add("");
            mArrayFilter.add("");
            mArrayPollution.add("");
            mArrayError.add("");

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
                                Log.i(TAG,"tName : " + tName);
                            }
                            break;
                        case XmlPullParser.TEXT:
                            String tConvert = tParser.getText().trim();
                            Log.i(TAG,"tConvert : " + tConvert);
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
                            }else if(tName.equals("PowerStatus")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayState.add("");
                                }else{
                                    mArrayState.add(tParser.getText());
                                }
                            }else if(tName.equals("WindPower")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayWind.add("");
                                }else{
                                    mArrayWind.add(tParser.getText());
                                }
                            }else if(tName.equals("Mode")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayMode.add("");
                                }else{
                                    mArrayMode.add(tParser.getText());
                                }
                            }else if(tName.equals("Filter")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayFilter.add("");
                                }else{
                                    mArrayFilter.add(tParser.getText());
                                }
                            }else if(tName.equals("Pollution")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayPollution.add("");
                                }else{
                                    mArrayPollution.add(tParser.getText());
                                }
                            }else if(tName.equals("Error")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayError.add("");
                                }else{
                                    mArrayError.add(tParser.getText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if(tName.equals("DeviceName")){
                                mArrayName.add("");
                            }else if (tName.equals("Mode")){
                                mArrayMode.add("");
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

            if(mArrayGroupID.size() > 1){
                mArrayGroupID.set(0, mArrayGroupID.get(1));
                mArraySubID.set(0, mArraySubID.get(1));
                mArrayState.set(0, "Off");
                mArrayWind.set(0, mArrayWind.get(1));
                mArrayMode.set(0, mArrayMode.get(1));
                mArrayFilter.set(0, mArrayFilter.get(1));
                mArrayPollution.set(0, mArrayPollution.get(1));
                mArrayError.set(0, mArrayError.get(1));

                for(int i = 1; i < mArrayState.size(); i++){
                    if(mArrayState.get(i).equals("On")){
                        mArrayState.set(0, "On");
                        break;
                    }
                }

                if (mVentilationLinListParent.getChildCount() == 1){
                    for (int i = 0; i < mArrayState.size(); i++){
                        VentilationListMake(true,i,mArrayState.get(i),mArrayName.get(i),mArrayWind.get(i),mArrayMode.get(i),mArrayFilter.get(i));
                    }
                }else{
                    for (int i = 0; i < mArrayState.size(); i++){
                        VentilationListMake(false,i,mArrayState.get(i),mArrayName.get(i),mArrayWind.get(i),mArrayMode.get(i),mArrayFilter.get(i));
                    }
                }

            }else{
                mArrayGroupID               = new ArrayList<>();
                mArraySubID                 = new ArrayList<>();
                mArrayName                  = new ArrayList<>();
                mArrayState                 = new ArrayList<>();
                mArrayWind                  = new ArrayList<>();
                mArrayMode                  = new ArrayList<>();
                mArrayFilter                = new ArrayList<>();
                mArrayPollution             = new ArrayList<>();
                mArrayError                 = new ArrayList<>();

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    void VentilationListMake(final boolean check, final int position, final String state, String name, String wind, String mode, String filter) {
        LinearLayout linearParent = null;
        TextView txtTitle = null;
        Switch swhControl = null;
        LinearLayout linearVisible = null;
        TextView txtFilter = null;
        TextView txtWind = null;
        TextView txtMode = null;

        if (check) {
            View listView = new View(this);
            listView = getLayoutInflater().inflate(R.layout.view_control_ventilation_layout, null);
            LinearLayout linearParent1 = (LinearLayout) listView.findViewById(R.id.Ventilation_Lin_List_Item_Parent);
            TextView txtTitle1 = (TextView) listView.findViewById(R.id.Ventilation_Txt_List_Item_Title);
            Switch swhControl1 = (Switch) listView.findViewById(R.id.Ventilation_Swh_List_Item);
            LinearLayout linearVisible1 = (LinearLayout)listView.findViewById(R.id.Ventilation_Lin_List_Item_Visible);
            TextView txtFilter1 = (TextView)listView.findViewById(R.id.txt_ventilation_status);
            TextView txtWind1 = (TextView)listView.findViewById(R.id.txt_ventilation_wind);
            TextView txtMode1 = (TextView)listView.findViewById(R.id.txt_ventilation_mode);

            mVentilationLinListParent.addView(listView);

            linearParent = linearParent1;
            txtTitle = txtTitle1;
            swhControl = swhControl1;
            linearVisible = linearVisible1;
            txtFilter = txtFilter1;
            txtWind = txtWind1;
            txtMode = txtMode1;

            parentLinViews.add(linearParent);
            titleTxtViews.add(txtTitle);
            controlSwhViews.add(swhControl);
            visibleLinViews.add(linearVisible);
            filterTxtViews.add(txtFilter);
            windTxtViews.add(txtWind);
            modeTxtViews.add(txtMode);
        }

        if (txtTitle == null){
            linearParent = parentLinViews.get(position);
            txtTitle = titleTxtViews.get(position);
            swhControl = controlSwhViews.get(position);
            linearVisible = visibleLinViews.get(position);
            txtFilter = filterTxtViews.get(position);
            txtWind = windTxtViews.get(position);
            txtMode = modeTxtViews.get(position);
        }

        txtTitle.setText(name);

        if(position != 0) {
            if (wind.equals("3")) {
                txtWind.setText(getString(R.string.Ventilation_textview_wind_3));
            } else if (wind.equals("2")) {
                txtWind.setText(getString(R.string.Ventilation_textview_wind_2));
            } else if (wind.equals("1")) {
                txtWind.setText(getString(R.string.Ventilation_textview_wind_1));
            } else {
                txtWind.setText(wind);
            }
        }else{
            txtWind.setText(" - ");
        }
//
        if(position != 0) {
            if (mode.equals("Auto")) {
                txtMode.setText(getString(R.string.Ventilation_textview_mode_auto));
            } else if (mode.equals("Normal")) {
                txtMode.setText(getString(R.string.Ventilation_textview_mode_normal));
            } else if (mode.equals("Sleep")) {
                txtMode.setText(getString(R.string.Ventilation_textview_mode_sleep));
            } else if (mode.equals("Pleasant")) {
                txtMode.setText(getString(R.string.Ventilation_textview_mode_pleasant));
            } else {
                txtMode.setText(getString(R.string.Ventilation_textview_mode_powersave));
            }
        }else{
            txtMode.setText(" - ");
        }
//
        if(position != 0) {
            if (filter.equals("Normal")) {
                txtFilter.setText(getString(R.string.Ventilation_textview_filter_normal));
            } else {
                txtFilter.setText(getString(R.string.Ventilation_textview_filter_change));
            }
            txtFilter.setVisibility(View.VISIBLE);
        }else{
            txtFilter.setText(" - ");
            txtFilter.setVisibility(View.VISIBLE);
        }

        if(state.equals("On")){
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
            swhControl.setChecked(true);
            linearVisible.setVisibility(View.VISIBLE);
        }else{
            linearParent.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
            swhControl.setChecked(false);
            linearVisible.setVisibility(View.GONE);
        }

        swhControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVentilationPosition = position;
                if(mArrayGroupID.size() > 1) {
                    if (position != 0) {
                        VentilationEachControlRequest(state);
                    } else {
                        VentilationGroupModeControlRequest(state);
                    }
                } else {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(VentilationActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        });
    }
}
