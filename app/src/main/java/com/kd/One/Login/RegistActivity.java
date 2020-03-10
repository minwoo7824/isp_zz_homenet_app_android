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
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

/**
 * Created by lwg on 2016-07-12.
 */
public class RegistActivity extends Activity {
    //**********************************************************************************************
    private Messenger                       mRegistRequest  = null;
    private Messenger                       mRegistResponse = null;
    //**********************************************************************************************

    //**********************************************************************************************
    private LocalConfig                     mLocalConfig;
    private Handler                         mTimeHandler;
    private CustomProgressDialog            mProgressDialog;
    private CustomPopupBasic                mCustomPopup;
    //**********************************************************************************************

    //**********************************************************************************************
    private EditText                        mRegistEditTextID;
    private EditText                        mRegistEditTextPW;
    private EditText                        mRegistEditTextPWConfirm;
    private EditText                        mRegistEditTextName;
    private EditText                        mRegistEditTextPhone;
    private EditText                        mRegistEditTextDong;
    private EditText                        mRegistEditTextHo;
    private LinearLayout                    mRegistLayout;

    //**********************************************************************************************

    //**********************************************************************************************
    private boolean                         mRegistIDDuplicationFlag;
    //**********************************************************************************************

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
        setContentView(R.layout.activity_login_regist);

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
        mRegistResponse = new Messenger(responseHandler);
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
        mRegistEditTextID           = (EditText)findViewById(R.id.Regist_EditText_ID);
        mRegistEditTextPW           = (EditText)findViewById(R.id.Regist_EditText_PW);
        mRegistEditTextPWConfirm    = (EditText)findViewById(R.id.Regist_EditText_PW_Confirm);
        mRegistEditTextName         = (EditText)findViewById(R.id.Regist_EditText_Name);
        mRegistEditTextPhone        = (EditText)findViewById(R.id.Regist_EditText_Phone);
        mRegistEditTextDong         = (EditText)findViewById(R.id.Regist_EditText_Dong);
        mRegistEditTextHo           = (EditText)findViewById(R.id.Regist_EditText_Ho);
        mRegistLayout               = (LinearLayout)findViewById(R.id.Regist_Layout);


        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity edittext filter
         */
        mRegistEditTextID.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        mRegistEditTextPW.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        mRegistEditTextPWConfirm.setFilters(new InputFilter[]{KDUtil.filterAlphaNum});
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity variable init
         */
        mRegistIDDuplicationFlag = false;
        //******************************************************************************************


    }
    //**********************************************************************************************

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
        mRegistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mRegistEditTextID.getWindowToken(), 0);
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
        tMsg.replyTo = mRegistResponse;
        sendMessage(tMsg);
        mRegistRequest = null;

        mProgressDialog.Dismiss();
        TimeHandlerRegistration(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(RegistRunner);
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
            mRegistRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mRegistResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mRegistResponse;
            sendMessage(tMsg);
            mRegistRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST:
                    DuplicationResponse((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_LOGIN_REGISTRATION_REQUEST:
                    RegistrationResponse((KDData)msg.obj);
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
            if(mRegistRequest != null) {
                mRegistRequest.send(tMsg);
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
    private void TimeHandlerRegistration(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(RegistRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable RegistRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    TimeHandlerRegistration(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerRegistration(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_request_fail),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerRegistration(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerRegistration(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif id duplication request
     */
    private void DuplicationRequest(){
        mProgressDialog.Show(getString(R.string.progress_request));
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerRegistration(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mRegistResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_ID, mRegistEditTextID.getText().toString().trim());

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif id duplication response
     * @param tKDData
     */
    private void DuplicationResponse(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        mProgressDialog.Dismiss();
        TimeHandlerRegistration(false, TIMER_NULL);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            // id duplication
            mRegistIDDuplicationFlag = false;

            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_title), getString(R.string.Regist_popup_id_use_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }else{
            // id null
            mRegistIDDuplicationFlag = true;

            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_title), getString(R.string.Regist_popup_id_use_success),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif registration response
     * @param tKDData
     */
    private void RegistrationResponse(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        mProgressDialog.Dismiss();
        TimeHandlerRegistration(false, TIMER_NULL);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mLocalConfig.setValue(Constants.SAVE_DATA_PW, mRegistEditTextPW.getText().toString());
            mLocalConfig.setValue(Constants.SAVE_DATA_DONG, mRegistEditTextDong.getText().toString());
            mLocalConfig.setValue(Constants.SAVE_DATA_HO, mRegistEditTextHo.getText().toString());

            Intent intent = new Intent(RegistActivity.this, CertifyActivity.class);
            intent.putExtra(Constants.INTENT_LOGIN_ID, mRegistEditTextID.getText().toString());
            startActivity(intent);
            finish();
        }else if(tKDData.Result.equals("5008")){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_max_member),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_request_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button select
     */
    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(RegistActivity.this, AgreeCheckActivity.class);
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
        }
    };


    //**********************************************************************************************

    //**********************************************************************************************
    public void OnClickBtnRegist(View v){
        switch(v.getId()){
            case    R.id.Regist_Lin_Back:
                onBackPressed();
                break;
            case    R.id.Regist_Btn_Duplication:
                if(mRegistEditTextID.length() != 0){
                    if(mRegistEditTextID.getText().toString().length() > 20){
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_id_length),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else {
                        DuplicationRequest();
                    }
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_id_input),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
                break;
            case    R.id.Regist_Btn_Success:
                RegistSuccess();
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif seccess button pressed fail & registration request
     */
    private void RegistSuccess(){
        if(mRegistEditTextID.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextPW.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextPWConfirm.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextName.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextPhone.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextDong.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_all_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextHo.getText().length() == 0){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_ho_input),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextDong.getText().length() > 4){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_dong_length_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextHo.getText().length() > 4){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_ho_length_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextID.getText().toString().length() > 20){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_id_length),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistEditTextPW.getText().toString().length() > 20){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_pw_length),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }else if(!mRegistEditTextPW.getText().toString().equals(mRegistEditTextPWConfirm.getText().toString())){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_pw_not_equal),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        } else if(mRegistIDDuplicationFlag == false){
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(RegistActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Regist_popup_reg_title), getString(R.string.Regist_popup_id_duplication_fail),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }else{
            if(mRegistEditTextDong.getText().length() < 4 && mRegistEditTextDong.getText().length() != 0) {
                String tText = "";
                for (int i = 0; i < (4 - mRegistEditTextDong.getText().length()); i++) {
                    tText += "0";
                }

                tText += mRegistEditTextDong.getText();
                mRegistEditTextDong.setText(tText);
            }

            if(mRegistEditTextHo.getText().length() < 4 && mRegistEditTextHo.getText().length() != 0){
                String tText = "";
                for(int i = 0; i < (4 - mRegistEditTextHo.getText().length());i++){
                    tText += "0";
                }

                tText += mRegistEditTextHo.getText();
                mRegistEditTextHo.setText(tText);
            }

            mProgressDialog.Show(getString(R.string.progress_request));
            mWaitCount    = 0;
            mRequestState = REQUEST_DATA_SEND_WAIT;
            TimeHandlerRegistration(true, TIMER_REQUEST);

            Message tMsg = Message.obtain();
            tMsg.replyTo = mRegistResponse;
            tMsg.what    = Constants.MSG_WHAT_LOGIN_REGISTRATION_REQUEST;

            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
            bundle.putString(Constants.KD_DATA_ID, mRegistEditTextID.getText().toString().trim());
            bundle.putString(Constants.KD_DATA_PW, mRegistEditTextPW.getText().toString().trim());
            bundle.putString(Constants.KD_DATA_NAME, mRegistEditTextName.getText().toString().trim());
            bundle.putString(Constants.KD_DATA_CELLPHONENUM, mRegistEditTextPhone.getText().toString().trim());
            bundle.putString(Constants.KD_DATA_DONG, mRegistEditTextDong.getText().toString().trim());
            bundle.putString(Constants.KD_DATA_HO, mRegistEditTextHo.getText().toString().trim());

            tMsg.setData(bundle);
            sendMessage(tMsg);
        }
    }
    //**********************************************************************************************
}
