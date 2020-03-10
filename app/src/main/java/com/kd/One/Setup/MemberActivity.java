package com.kd.One.Setup;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fingerpush.android.FingerPushManager;
import com.fingerpush.android.NetworkUtility;
import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.Login.LoginActivity;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.json.JSONObject;

/**
 * Created by lwg on 2016-09-06.
 */
public class MemberActivity extends Activity {
    private String TAG ="MemberActivity";
    //**********************************************************************************************
    private Messenger                               mMemberResponse        = null;
    private Messenger                               mMemberRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public  LocalConfig                             mLocalConfig;
    public  MyGlobal                                mMyGlobal;
    private Handler                                 mTimeHandler;
    private CustomPopupBasic                        mCustomPopup;
    private CustomProgressDialog                    mProgressDialog;
    //**********************************************************************************************

    //**********************************************************************************************
    private SimpleSideDrawer                        mSlideDrawer;
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
    private int                                     mPopupStatus;
    private static final int                        POPUP_NULL          = 0;
    private static final int                        POPUP_ERROR         = 1;
    private static final int                        POPUP_LOGOUT        = 2;
    private static final int                        POPUP_WITHDRAWAL    = 3;
    //**********************************************************************************************
    private TextView                                mTextViewID;
    private TextView                                mTextViewName;
    private TextView                                mTextViewDong;
    private EditText                                mEditTextPassword;
    private EditText                                mEditTextPasswordNew;
    private EditText                                mEditTextPasswordConfirm;
    private EditText                                mEditTextCellPhone;
    private LinearLayout                            mLinearLayoutMember;
    //**********************************************************************************************

    //**********************************************************************************************
    private String                                  mCellPhoneNum;
    private String                                  mPassword;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitEachGroupCount     = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 500;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate member activity
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
        setContentView(R.layout.activity_setup_mamberinfochange);

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
        mMemberResponse = new Messenger(responseHandler);
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
        mTextViewDong               = (TextView)findViewById(R.id.Member_TextView_Dong);
        mTextViewID                 = (TextView)findViewById(R.id.Member_TextView_ID);
        mTextViewName               = (TextView)findViewById(R.id.Member_TextView_Name);
        mEditTextCellPhone          = (EditText)findViewById(R.id.Member_EditText_PhoneNumber);
        mEditTextPassword           = (EditText)findViewById(R.id.Member_EditText_Password);
        mEditTextPasswordNew        = (EditText)findViewById(R.id.Member_EditText_Password_New);
        mEditTextPasswordConfirm    = (EditText)findViewById(R.id.Member_EditText_Password_New_Confirm);
        mLinearLayoutMember         = (LinearLayout)findViewById(R.id.Member_LinearLayout);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity edittext filter
         */
        mEditTextCellPhone.setFilters(new InputFilter[]{KDUtil.filterNum});
        mEditTextPassword.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        mEditTextPasswordNew.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        mEditTextPasswordConfirm.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        //******************************************************************************************

        mTextViewName.setText(" " + mLocalConfig.getStringValue(Constants.SAVE_DATA_NAME));
        mTextViewID.setText(" " + mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
        mTextViewDong.setText(" "+mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG) + "동" + " "
        + mLocalConfig.getStringValue(Constants.SAVE_DATA_HO) + "호");
        mEditTextCellPhone.setText(mLocalConfig.getStringValue(Constants.SAVE_DATA_PHONE_NUM));
    }

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
        mLinearLayoutMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextPassword.getWindowToken(), 0);
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
        tMsg.replyTo = mMemberResponse;
        sendMessage(tMsg);
        mMemberRequest = null;
        TimeHandlerMember(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(MemberRunner);
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
            mMemberRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mMemberResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mMemberResponse;
            sendMessage(tMsg);
            mMemberRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST:
                    MemberResult((KDData)msg.obj);
                    break;
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
                finish();
            } else if (action.equals(Constants.ACTION_APP_NETWORK_ERROR)) {
            } else if( action.equals(Constants.ACTION_APP_SOCKET_CLOSE)){
            } else if( action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)){
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                TimeOutMoving.TimeOutMoving(mMemberRequest, mMemberResponse, MemberActivity.this);
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
            if(mMemberRequest != null) {
                mMemberRequest.send(tMsg);
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
    private void TimeHandlerMember(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(MemberRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable MemberRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    MemberRequest();
                    TimeHandlerMember(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerMember(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerError);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerMember(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerMember(false, TIMER_NULL);
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
        TimeHandlerMember(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mMemberResponse;
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
        TimeHandlerMember(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mMemberResponse;
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
        TimeHandlerMember(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mLocalConfig.setValue(Constants.SAVE_DATA_LOGIN_STATUS,-1);
            Intent intent = new Intent(MemberActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
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
        TimeHandlerMember(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Setup_popup_withdrawal_title), getString(R.string.Setup_popup_withdrawal_contents_success),
                        mPopupListenerWithdrawalSuccess);
                mCustomPopup.show();
            }
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    /**
     * @breif member request
     */
    private void MemberRequest(){
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerMember(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mMemberResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST);
        bundle.putString(Constants.KD_DATA_ID, mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
        bundle.putString(Constants.KD_DATA_CELLPHONENUM, mCellPhoneNum);
        bundle.putString(Constants.KD_DATA_PW, mPassword);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif MEMBER request result
     * @param tKDData
     */
    private void MemberResult(KDData tKDData){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_CLEAR;
        TimeHandlerMember(false, TIMER_NULL);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Member_popup_title), getString(R.string.Member_popup_chnage_success_contents),
                        mPopupListenerSuccess);
                mCustomPopup.show();
            }
        }else{
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                        mPopupListenerError);
                mCustomPopup.show();
            }
        }
    }

    private View.OnClickListener mPopupListenerWithdrawalSuccess = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mLocalConfig.setValue(Constants.SAVE_DATA_ID, "");
            mLocalConfig.setValue(Constants.SAVE_DATA_AUTO_ID, 0);
            mLocalConfig.setValue(Constants.KD_DATA_CERTIFY, "");
            Intent intent = new Intent(MemberActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

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
//        Intent intent = new Intent(MemberActivity.this, MainFragment.class);
//        intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_SETUP);
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup error main page
     */
    private View.OnClickListener mPopupListenerError = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCustomPopup.dismiss();
            mCustomPopup = null;

//            Intent intent = new Intent(MemberActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
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
            if(mPopupStatus == POPUP_LOGOUT){

                FingerPushManager.getInstance(MemberActivity.this).removeIdentity(new NetworkUtility.ObjectListener() {
                    @Override
                    public void onComplete(String code, String message, JSONObject jsonObject) {
                        Log.e("FingerPush", "removeIdentity onComplete : code : " + code + ", message : " + message );
                    }

                    @Override
                    public void onError(String code, String message) {
                        Log.e("FingerPush", "removeIdentity onError : code : " + code + ", message : " + message);
                    }
                });

                mCustomPopup.dismiss();
                mCustomPopup = null;
                mPopupStatus = POPUP_NULL;
                LogoutRequest();
            }else if(mPopupStatus == POPUP_WITHDRAWAL){

                FingerPushManager.getInstance(MemberActivity.this).removeIdentity(new NetworkUtility.ObjectListener() {
                    @Override
                    public void onComplete(String code, String message, JSONObject jsonObject) {
                        Log.d("FingerPush", "removeIdentity onComplete : code : " + code + ", message : " + message );
                    }

                    @Override
                    public void onError(String code, String message) {
                        Log.d("FingerPush", "removeIdentity onError : code : " + code + ", message : " + message);
                    }
                });

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
     * @breif popup cancel button
     */
    private View.OnClickListener mPopupListenerCancel = new View.OnClickListener() {
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
    private View.OnClickListener mPopupListenerRequest = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup    = null;
            mCellPhoneNum   = mEditTextCellPhone.getText().toString().trim();
            mPassword       = mEditTextPasswordNew.getText().toString().trim();
            MemberRequest();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup success button
     */
    private View.OnClickListener mPopupListenerSuccess = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
//            Intent intent = new Intent(MemberActivity.this, MainFragment.class);
//            intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_SETUP);
//            startActivity(intent);
//            finish();
            onBackPressed();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    public void OnClickBtnMember(View v){
        switch (v.getId()){
            case    R.id.Member_Lin_Back:
                onBackPressed();
                break;
            case    R.id.Member_Btn_Change:
                String  tNew = "";
                String  tNewConfirm = "";
                String  tPass = "";
                String  tPhone = "";
                String  tSavePass = "";

                tSavePass = mLocalConfig.getStringValue(Constants.SAVE_DATA_PW);

                tPhone = mEditTextCellPhone.getText().toString().trim();
                tPass = mEditTextPassword.getText().toString().trim();
                tNew = mEditTextPasswordNew.getText().toString().trim();
                tNewConfirm = mEditTextPasswordConfirm.getText().toString().trim();

                if(tPass.length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_all_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else if(tNew.length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_all_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else if(tNewConfirm.length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_all_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else if(tPhone.length() == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_all_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                } else if(!tPass.equals(tSavePass)){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_pw_equal),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else if(!tNew.equals(tNewConfirm)){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_pw_fail),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }else {
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_twobutton,
                                getString(R.string.Member_popup_title), getString(R.string.Member_popup_change_contents),
                                mPopupListenerCancel, mPopupListenerRequest);
                        mCustomPopup.show();
                    }
                }
                break;
            case    R.id.Member_Btn_Logout:
                mPopupStatus = POPUP_LOGOUT;
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_twobutton,
                        getString(R.string.Setup_textview_logout), getString(R.string.Setup_popup_logout_contents),
                        mPopupListenerCancel, mPopupListenerOK);
                mCustomPopup.show();
                break;
            case    R.id.Member_Btn_Withdrawal:
                mPopupStatus = POPUP_WITHDRAWAL;
                mCustomPopup = new CustomPopupBasic(MemberActivity.this, R.layout.popup_basic_twobutton,
                        getString(R.string.Setup_textview_withdrawal), getString(R.string.Setup_popup_withdrawal_contents),
                        mPopupListenerCancel, mPopupListenerOK);
                mCustomPopup.show();
                break;
            default:
                break;
        }
    }
}
