package com.kd.One.Main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Login.AgreeActivity;
import com.kd.One.Login.LoginActivity;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import com.kd.One.Setup.HelpActivity;
import com.kd.One.Setup.MemberActivity;
import com.kd.One.Setup.PushSettingActivity;
import com.kd.One.Setup.VersionActivity;


/**
 * Created by lwg on 2016-07-15.
 */
public class SetupFragment extends Fragment implements View.OnClickListener{
    //**********************************************************************************************
    private Messenger                               mSetupResponse        = null;
    private Messenger                               mSetupRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    private MyGlobal mMyGlobal;
    private LocalConfig mLocalConfig;
    private Handler                                 mTimeHandler;
    //**********************************************************************************************

    //**********************************************************************************************
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    //**********************************************************************************************

    //**********************************************************************************************

    private ViewGroup                               mLayoutPush;
    private ViewGroup                               mLayoutUser;
    private ViewGroup                               mLayoutVersion;
    private ViewGroup                               mLayoutHelp;
    private ViewGroup                               mLayoutAgree;

    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mPopupStatus;
    private static final int                        POPUP_NULL          = 0;
    private static final int                        POPUP_ERROR         = 1;
    private static final int                        POPUP_LOGOUT        = 2;
    private static final int                        POPUP_WITHDRAWAL    = 3;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitGroupCount         = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 500;  // 1500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //******************************************************************************************
        /**
         * @breif broadcast intent filter
         */

        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif service registration
         */
        Intent intent_response = new Intent(getActivity(), HomeTokService.class);
        getActivity().startService(intent_response);
        mSetupResponse = new Messenger(responseHandler);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getContext());
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif global data setup
         */
        //******************************************************************************************
        mMyGlobal = mMyGlobal.getInstance();
        //**************************************************************************************

        //******************************************************************************************
        /**
         * @breif progress dialog create
         */
        mProgressDialog = new CustomProgressDialog(getActivity());
        //******************************************************************************************

        //**************************************************************************************
        mLayoutPush             = (ViewGroup)getActivity().findViewById(R.id.Setup_LinearLayout_Push);
        mLayoutHelp             = (ViewGroup)getActivity().findViewById(R.id.Setup_LinearLayout_help);
        mLayoutVersion          = (ViewGroup)getActivity().findViewById(R.id.Setup_LinearLayout_Version);
        mLayoutUser             = (ViewGroup)getActivity().findViewById(R.id.Setup_LinearLayout_User);
        mLayoutAgree             = (ViewGroup)getActivity().findViewById(R.id.Setup_LinearLayout_Agree);

        // MARK : JMH -2020-03-16 default 푸시 표시하지 않음
        mLayoutPush.setVisibility(View.GONE);

        if (null != mLocalConfig.getStringValue(Constants.SAVE_DATA_USE_PUSH)) {
            if (mLocalConfig.getStringValue(Constants.SAVE_DATA_USE_PUSH).equals("Y")) {
                mLayoutPush.setVisibility(View.VISIBLE);
            } else {
                mLayoutPush.setVisibility(View.GONE);
            }
        }

        mLayoutHelp.setOnClickListener(this);
        mLayoutVersion.setOnClickListener(this);
        mLayoutUser.setOnClickListener(this);
        mLayoutPush.setOnClickListener(this);
        mLayoutAgree.setOnClickListener(this);
        //**************************************************************************************
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = new Intent(getContext(), HomeTokService.class);
        getActivity().bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onPause(){
        super.onPause();

        getActivity().unbindService(requestConnection);

        Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
        tMsg.replyTo = mSetupResponse;
        sendMessage(tMsg);
        mSetupRequest = null;
        TimeHandlerSetup(false, TIMER_NULL);

        if(mCustomPopup != null){
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(SetupRunner);
        }
        super.onDestroy();
    }

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSetupRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mSetupResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mSetupResponse;
            sendMessage(tMsg);
            mSetupRequest = null;
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
        getActivity().registerReceiver(appReceiver, new IntentFilter(intentFilter));
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief HomeTok service callback message result
     */
    private Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case    Constants.MSG_WHAT_LOGIN_LOGOUT:
                    LogoutResult((KDData)msg.obj);
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
                getActivity().finish();
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
            if(mSetupRequest != null) {
                mSetupRequest.send(tMsg);
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
    private void TimeHandlerSetup(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(SetupRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable SetupRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    TimeHandlerSetup(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerSetup(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mPopupStatus = POPUP_ERROR;
                            mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerSetup(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerSetup(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif logout request
     */
    private void LogoutRequest(){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerSetup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mSetupResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_LOGOUT;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_LOGIN_LOGOUT);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif withdrawal request
     */
    private void WithdrawalRequest(){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerSetup(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mSetupResponse;
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
     * @breif Loginout result
     * @param tKDData
     */
    private void LogoutResult(KDData tKDData){
        mWaitCount = 0;
        mProgressDialog.Dismiss();
        TimeHandlerSetup(false, TIMER_NULL);

        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
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
        TimeHandlerSetup(false, TIMER_NULL);

        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                            getString(R.string.Setup_popup_withdrawal_title), getString(R.string.Setup_popup_withdrawal_contents_success),
                            mPopupListenerWithdrawalSuccess);
                    mCustomPopup.show();
                }
            } else {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button
     */

    public void onBackPressed(){
//        Intent intent = new Intent(getActivity(), MainMenuActivity.class);
//        startActivity(intent);
//        getActivity().finish();

    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup cancel
     */
    private View.OnClickListener mPopupListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            mPopupStatus = POPUP_NULL;
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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mPopupStatus == POPUP_LOGOUT){
                mCustomPopup.dismiss();
                mCustomPopup = null;
                mPopupStatus = POPUP_NULL;
                LogoutRequest();
            }else if(mPopupStatus == POPUP_WITHDRAWAL){
                mCustomPopup.dismiss();
                mCustomPopup = null;
                mPopupStatus = POPUP_NULL;
                WithdrawalRequest();
            }else if(mPopupStatus == POPUP_ERROR){
                mCustomPopup.dismiss();
                mCustomPopup = null;
                mPopupStatus = POPUP_NULL;
            }else{
                mCustomPopup.dismiss();
                mCustomPopup = null;
                mPopupStatus = POPUP_NULL;
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif button
     * @param v
     */
    @Override
    public void onClick(View v){
        Intent intent;
        switch (v.getId()){
            case    R.id.Setup_LinearLayout_help:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
                break;
            case    R.id.Setup_LinearLayout_Version:
                intent = new Intent(getActivity(), VersionActivity.class);
                startActivity(intent);
                break;
            case    R.id.Setup_LinearLayout_User:
                intent = new Intent(getActivity(), MemberActivity.class);
                startActivity(intent);
                break;
            case R.id.Setup_LinearLayout_Push:
                intent = new Intent(getActivity(), PushSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.Setup_LinearLayout_Agree:
                intent = new Intent(getActivity(), AgreeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************
}
