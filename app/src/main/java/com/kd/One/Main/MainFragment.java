package com.kd.One.Main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fingerpush.android.FingerPushManager;
import com.fingerpush.android.NetworkUtility;
import com.kd.One.Common.Constants;
import com.kd.One.Common.FingerPushUtil;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by lwg on 2016-07-15.
 */
public class MainFragment extends FragmentActivity{
    private String TAG = "MainFragment";
    //**********************************************************************************************
    private Messenger                               mFragmentResponse        = null;
    private Messenger                               mFragmentRequest         = null;
    //**********************************************************************************************

    private LocalConfig mLocalConfig;

    //**********************************************************************************************
    public MyGlobal mMyGlobal;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                         mCurrentFragmentIndex;
    //**********************************************************************************************

    //**********************************************************************************************
    private LinearLayout                mMainFragmentBtnControl;
    private LinearLayout                mMainFragmentBtnHome;
    private LinearLayout                mMainFragmentBtnInfo;
    private LinearLayout                mMainFragmentBtnSetup;
    private TextView                    mMainFragmentTxtTitle;
    private ImageView                   mMainFragmentImgMode;
    //**********************************************************************************************

    //**********************************************************************************************
    private Handler mTimeHandler;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;

    private String mSecurityState;

    private int mControlFlag = 0;
    private int mDataSendFlag= 0;
    private int mWaitCount = 0;
    private int mRequestState = 0;
    private static final int REQUEST_DATA_CLEAR = 0;
    private static final int REQUEST_DATA_SEND_START = 1;
    private static final int REQUEST_DATA_SEND_WAIT = 2;

    private static final int TIMER_REQUEST = 1000;  // 500msec
    private static final int TIMER_NULL = 0;
    private static final int TIMER_WAIT_TIME = 20;   // 40 * 500msec = 20sec
    //**********************************************************************************************
    private AlertDialog alertDialog;
    //**********************************************************************************************
    String dong = "";
    String ho = "";

    private long backKeyPressedTime = 0;

    Comparator<? super File> filecomparator = new Comparator<File>(){
        public int compare(File file1, File file2) {
            return String.valueOf(file1.getName()).compareTo(file2.getName());
        }
    };

    //**********************************************************************************************
    /**
     * @breif oncreate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
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
        setContentView(R.layout.activity_main_fragment);

        mLocalConfig = new LocalConfig(getBaseContext());

        dong = "";
        ho = "";

        if (mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG).charAt(0) == '0'){
            dong = mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG).substring(1,4);
        }else{
            dong = mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG);
        }
        if (mLocalConfig.getStringValue(Constants.SAVE_DATA_HO).charAt(0) == '0'){
            ho = mLocalConfig.getStringValue(Constants.SAVE_DATA_HO).substring(1,4);
        }else{
            ho = mLocalConfig.getStringValue(Constants.SAVE_DATA_HO);
        }


        //setIdentity
        FingerPushUtil.setIdentityFingerPush(MainFragment.this,mLocalConfig.getStringValue(Constants.SAVE_DATA_PUBLIC_IP)+"_"+mLocalConfig.getStringValue(Constants.SAVE_DATA_ID) );

      /*  FingerPushManager.getInstance(this).setDevice(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String s, String s1, JSONObject jsonObject) {
                if(s.equals("200") || s.equals("201")) {
                    // 디바이스 최초 등록시 해당 코드 리턴 후 태그 등록
                    FingerPushManager.getInstance(MainFragment.this).setIdentity(Constants.SERVER_LOCAL_IP+"_"+mLocalConfig.getStringValue(Constants.SAVE_DATA_ID), new NetworkUtility.ObjectListener() {
                        @Override
                        public void onComplete(String code, String message, JSONObject jsonObject) {

                        }

                        @Override
                        public void onError(String code, String message) {

                        }
                    });
                }
            }

            @Override
            public void onError(String s, String s1) {
                if(s.equals("504")) {
                    Log.e("FingerPushKey",mLocalConfig.getStringValue(Constants.SAVE_DATA_PUBLIC_IP)+"_"+mLocalConfig.getStringValue(Constants.SAVE_DATA_ID));
                    // 디바이스가 이미 등록된 경우 해당 코드 리턴 후 태그 등록
                    FingerPushManager.getInstance(MainFragment.this).setIdentity(mLocalConfig.getStringValue(Constants.SAVE_DATA_PUBLIC_IP)+"_"+mLocalConfig.getStringValue(Constants.SAVE_DATA_ID), new NetworkUtility.ObjectListener() {
                        @Override
                        public void onComplete(String code, String message, JSONObject jsonObject) {
                            Log.e("Fingerpush!!!!","SUCCESS");
                        }

                        @Override
                        public void onError(String code, String message) {
                            Log.e("Fingerpush!!!!","FAIL");
                        }
                    });

                }
            }
        }) ;
*/
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
        mFragmentResponse = new Messenger(responseHandler);
        //******************************************************************************************

        /**
         * @breif progress dialog create
         */
        mProgressDialog = new CustomProgressDialog(this);

        //******************************************************************************************
        /**
         * @breif global data setup
         */
        //******************************************************************************************
        mMyGlobal = mMyGlobal.getInstance();
        //******************************************************************************************

        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey(Constants.INTENT_FRAGMENT_STATE)){
                mCurrentFragmentIndex = getIntent().getExtras().getInt(Constants.INTENT_FRAGMENT_STATE);
            }else if(getIntent().getExtras().containsKey(Constants.INTENT_TIMEOUT)){
                Message tMsg = Message.obtain();
                tMsg.replyTo = mFragmentResponse;
                tMsg.what    = Constants.MSG_WHAT_TIMER_END;

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TIMER_END);
                tMsg.setData(bundle);
                sendMessage(tMsg);

//              mTimeHandler

                unregisterReceiver(appReceiver);
                SocketRefreshDialog();
            }else{
                mCurrentFragmentIndex = Constants.FRAGMENT_CONTROL;
            }
        }else {
            mCurrentFragmentIndex = Constants.FRAGMENT_CONTROL;
        }

        mMainFragmentBtnControl = (LinearLayout) findViewById(R.id.MainFragment_Lin_Control);
        mMainFragmentBtnInfo    = (LinearLayout)findViewById(R.id.MainFragment_Lin_Info);
        mMainFragmentBtnSetup   = (LinearLayout)findViewById(R.id.MainFragment_Lin_Setup);
        mMainFragmentTxtTitle   = (TextView)findViewById(R.id.MainFragment_Txt_Title);
        mMainFragmentBtnHome    = (LinearLayout)findViewById(R.id.MainFragment_Lin_Home);
        mMainFragmentImgMode    = (ImageView)findViewById(R.id.MainFragment_Img_Mode);

        mMainFragmentTxtTitle.setText("제어");

        fragmentReplace(mCurrentFragmentIndex);

        mSecurityState = "0";
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
        if(mProgressDialog==null){
            mProgressDialog = new CustomProgressDialog(this);
        }
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
        tMsg.replyTo = mFragmentResponse;
        sendMessage(tMsg);
        mFragmentRequest = null;
        mControlFlag = 0;
        mDataSendFlag = 0;
        TimeHandlerSecurity(false, TIMER_NULL);

        if (mCustomPopup != null) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }

        if (mProgressDialog != null) {
            Log.e("mProgressDialog","!! : "+ mProgressDialog);
            mProgressDialog.Dismiss();
        }
        try{
            unregisterReceiver(appReceiver);
        }catch (IllegalArgumentException e){

        }catch (Exception e){

        }

        Log.e("onPause","Mainfragment!! : onPause");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif destroy operating
     */
    public void onDestroy() {
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(SecurityRunner);
        }
        super.onDestroy();

        if(alertDialog !=null){
            alertDialog.dismiss();
        }
        Log.e("onDestroy","Mainfragment!! : onDestroy");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mFragmentRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mFragmentResponse;
            sendMessage(tMsg);

            mWaitCount = 0;
            mRequestState = REQUEST_DATA_SEND_START;
            TimeHandlerSecurity(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mFragmentResponse;
            sendMessage(tMsg);
            mFragmentRequest = null;
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
                case Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST:
                    SecurityStateResult((KDData) msg.obj);
                    break;
                case Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST:
                    SecurityControlResult((KDData) msg.obj);
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
            Log.e("FragmentActivity", "BroadCast Receive");
            if (action.equals(Constants.ACTION_APP_FINISH)) {
                finish();
            } else if (action.equals(Constants.ACTION_APP_NETWORK_ERROR)) {
            } else if( action.equals(Constants.ACTION_APP_SOCKET_CLOSE)){
            } else if( action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)){
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                Log.i(TAG,"timeOut");
                TimeOutMoving.TimeOutMoving(mFragmentRequest, mFragmentResponse, MainFragment.this);
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
            if(mFragmentRequest != null) {
                mFragmentRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tState
     * @param tTime
     * @breif time handler setup function
     */
    private void TimeHandlerSecurity(boolean tState, int tTime) {
        if (tState == true) {
            if (mTimeHandler == null) {
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(SecurityRunner, tTime);
        } else {
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable SecurityRunner = new Runnable() {
        @Override
        public void run() {
            if (mTimeHandler != null) {
                if (mRequestState == REQUEST_DATA_SEND_START) {
                    SecurityStateRequest();
                    mTimeHandler.postDelayed(SecurityRunner, TIMER_REQUEST);
                } else {
                    mWaitCount++;
                    Log.e("security timer", String.valueOf(mWaitCount));
                    if (mWaitCount > TIMER_WAIT_TIME) {
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerSecurity(false, TIMER_NULL);
                        SecuritySocketClose();
                        mControlFlag = 0;
                        mDataSendFlag = 0;
                        if (mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(MainFragment.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Control_popup_error_title), getString(R.string.Control_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    } else {
                        mTimeHandler.postDelayed(SecurityRunner, TIMER_REQUEST);
                    }

                    if(mDataSendFlag == 1){
                        SecurityStateRequest();
                        mDataSendFlag = 0;
                    }

                }
            } else {
                TimeHandlerSecurity(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif security socket close
     */
    private void SecuritySocketClose() {
        Message tMsg = Message.obtain();
        tMsg.replyTo = mFragmentResponse;
        tMsg.what = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif Security state request
     */
    private void SecurityStateRequest() {
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mFragmentResponse;
        tMsg.what = Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tNum
     * @breif Security control request communication
     */
    private void SecurityControlRequest() {
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerSecurity(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mFragmentResponse;
        tMsg.what = Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_SECURITY_STATE, mSecurityState);

        tMsg.setData(bundle);
        sendMessage(tMsg);

        mControlFlag = 1;
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param tKDData
     * @breif Security state request result
     */
    private void SecurityStateResult(KDData tKDData) {
        if (tKDData != null) {
            if (null != tKDData.Result) {
                if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                    HNMLDataParserSecurity(tKDData.ReceiveString);
                    if (mControlFlag == 0) {
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        TimeHandlerSecurity(false, TIMER_NULL);
                        mProgressDialog.Dismiss();
                        mControlFlag = 0;
                        mDataSendFlag = 0;
                        Log.e("security state result", "success");
                    } else {
                        if (mSecurityState.equals("1")) {
                            mWaitCount = 0;
                            mRequestState = REQUEST_DATA_CLEAR;
                            TimeHandlerSecurity(false, TIMER_NULL);
                            mProgressDialog.Dismiss();
                            mControlFlag = 0;
                            mDataSendFlag = 0;
                            Log.e("security state result", "control success");
                        } else {
                            //mRequestState = REQUEST_DATA_SEND_START;
                            //SecurityStateRequest();
                            mDataSendFlag = 1;
                            Log.e("security state result", "control fail");
                        }
                    }

                } else {
                    mWaitCount = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerSecurity(false, TIMER_NULL);
                    mProgressDialog.Dismiss();
                    mControlFlag = 0;
                    mDataSendFlag = 0;
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(MainFragment.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_control_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        } else {
            Log.e("security state result", "tKDData null fail");

            mWaitCount = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerSecurity(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            mControlFlag = 0;
            mDataSendFlag = 0;
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
//            Intent intent = new Intent(MainFragment.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @param tKDData
     * @breif Security control request result
     */
    private void SecurityControlResult(KDData tKDData) {
        if (tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                HNMLDataParserSecurity(tKDData.ReceiveString);
                if (mSecurityState.equals("1")) {
                    mWaitCount = 0;
                    mRequestState = REQUEST_DATA_CLEAR;
                    TimeHandlerSecurity(false, TIMER_NULL);
                    //mProgressDialog.Dismiss();
                    mControlFlag = 0;
                    mDataSendFlag = 0;

                    mRequestState = REQUEST_DATA_SEND_START;
                    SecurityStateRequest();

                    Log.e("security control result", "control result success");
                } else {
                    //mRequestState = REQUEST_DATA_SEND_START;
                    //SecurityStateRequest();
                    mDataSendFlag = 1;
                    Log.e("security control result", "fail");
                }

            } else {
                mWaitCount = 0;
                mRequestState = REQUEST_DATA_CLEAR;
                TimeHandlerSecurity(false, TIMER_NULL);
                mProgressDialog.Dismiss();
                mControlFlag = 0;
                mDataSendFlag = 0;
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(MainFragment.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Security_popup_out_fail_title), getString(R.string.Security_popup_out_fail_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        } else {
            mWaitCount = 0;
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerSecurity(false, TIMER_NULL);
            mProgressDialog.Dismiss();
            mControlFlag = 0;
            mDataSendFlag = 0;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif fragment replace
     * @param tFragmentIndex
     */
    public void fragmentReplace(int tFragmentIndex){
        Fragment newFragment = null;

        newFragment = getFragment(tFragmentIndex);

        //replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.MainFragment_Layout, newFragment);

        transaction.commit();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif get fragment
     * @param tIndex
     * @return
     */
    private Fragment getFragment(int tIndex){
        Fragment tFragment = null;

        switch(tIndex){
            case    Constants.FRAGMENT_CONTROL:
                tFragment = new ControlFragment();
                mMainFragmentBtnControl.setSelected(true);
                mMainFragmentBtnInfo.setSelected(false);
                mMainFragmentBtnSetup.setSelected(false);
                mMainFragmentBtnHome.setSelected(false);
                break;
            case    Constants.FRAGMENT_HOME:
                tFragment = new HomeFragment();
                mMainFragmentBtnControl.setSelected(false);
                mMainFragmentBtnInfo.setSelected(false);
                mMainFragmentBtnSetup.setSelected(false);
                mMainFragmentBtnHome.setSelected(true);
                break;
            case    Constants.FRAGMENT_INFO:
                tFragment = new InfoFragment();
                mMainFragmentBtnControl.setSelected(false);
                mMainFragmentBtnInfo.setSelected(true);
                mMainFragmentBtnSetup.setSelected(false);
                mMainFragmentBtnHome.setSelected(false);
                break;
            case    Constants.FRAGMENT_SETUP:
                tFragment = new SetupFragment();
                mMainFragmentBtnControl.setSelected(false);
                mMainFragmentBtnInfo.setSelected(false);
                mMainFragmentBtnSetup.setSelected(true);
                mMainFragmentBtnHome.setSelected(false);
                break;
        }

        return tFragment;
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief back button selected
     */
    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************


    void ColorFilter(LinearLayout linearLayout1,LinearLayout linearLayout2,LinearLayout linearLayout3,LinearLayout linearLayout4){

        for (int i = 0; i < linearLayout1.getChildCount(); i++){
            View textView1 = linearLayout1.getChildAt(i);
            View imageView1 = linearLayout1.getChildAt(i);
            View textView2 = linearLayout2.getChildAt(i);
            View imageView2 = linearLayout2.getChildAt(i);
            View textView3 = linearLayout3.getChildAt(i);
            View imageView3 = linearLayout3.getChildAt(i);
            View textView4 = linearLayout4.getChildAt(i);
            View imageView4 = linearLayout4.getChildAt(i);

            if (textView1 instanceof TextView){
                ((TextView) textView1).setTextColor(getResources().getColor(R.color.colorPrimary));
            }else if (imageView1 instanceof ImageView){
                if (linearLayout1.getId() == R.id.MainFragment_Lin_Control){
                    ((ImageView) imageView1).setImageResource(R.drawable.ic_main_tab02);
                }else{
                    ((ImageView) imageView1).setColorFilter(getResources().getColor(R.color.colorPrimary));
                }
            }
            if (textView2 instanceof TextView){
                ((TextView) textView2).setTextColor(getResources().getColor(R.color.colorb8b8b8));
            }else if (imageView2 instanceof ImageView){
                if (linearLayout2.getId() == R.id.MainFragment_Lin_Control){
                    ((ImageView) imageView2).setImageResource(R.drawable.ic_main_tab_control_off);
                }else{
                    ((ImageView) imageView2).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                }
            }
            if (textView3 instanceof TextView){
                ((TextView) textView3).setTextColor(getResources().getColor(R.color.colorb8b8b8));
            }else if (imageView3 instanceof ImageView){
                if (linearLayout3.getId() == R.id.MainFragment_Lin_Control){
                    ((ImageView) imageView3).setImageResource(R.drawable.ic_main_tab_control_off);
                }else{
                    ((ImageView) imageView3).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                }
            }
            if (textView4 instanceof TextView){
                ((TextView) textView4).setTextColor(getResources().getColor(R.color.colorb8b8b8));
            }else if (imageView4 instanceof ImageView){
                if (linearLayout4.getId() == R.id.MainFragment_Lin_Control){
                    ((ImageView) imageView4).setImageResource(R.drawable.ic_main_tab_control_off);
                }else{
                    ((ImageView) imageView4).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                }
            }
        }
    }

    void ModeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.pop_up_mode_select, null);

        Button btnCancel = (Button)dialogView.findViewById(R.id.btn_mode_popup_cancel);
        Button btnDone = (Button)dialogView.findViewById(R.id.btn_mode_popup_done);

        builder.setView(dialogView);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        Display display = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = display.getWidth();
        params.height = display.getHeight()/3*2;
        alertDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSecurityState = "1";
                SecurityControlRequest();
                alertDialog.dismiss();
            }
        });
    }

    void SocketRefreshDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainFragment.this);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.pop_up_socket_refresh, null);
        ImageView imgRefresh = (ImageView)dialogView.findViewById(R.id.img_socket_connect_wait_refresh);
        builder.setView(dialogView);
        builder.setCancelable(true);

        alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(false);
        alertDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        window.setAttributes(layoutParams);

        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message tMsg = Message.obtain();
                tMsg.replyTo = mFragmentResponse;
                tMsg.what    = Constants.MSG_WHAT_TIMER_START;

                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
                tMsg.setData(bundle);
                sendMessage(tMsg);

                registerReceiver();

                SecurityStateRequest();

                alertDialog.dismiss();

            }
        });
    }

    private class SocketConnectAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            SocketAddress socketAddress = new InetSocketAddress(Constants.SERVER_LOCAL_COMPLEX_IP, Constants.SERVER_LOCAL_COMPLEX_PORT);
            Socket mSocket = new Socket();
            try {
                mSocket.connect(socketAddress, 3000);
                Log.i(TAG,"socket reconnect success");
                registerReceiver();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG,"socket reconnect failed");
            }
            return null;
        }
    }
    /**
     * @breif fragment button
     * @param v
     */
    public void OnClickBtnMainFragment(View v){
        switch(v.getId()){
//            case    R.id.MainFragment_Btn_Back:
//                Intent intent = new Intent(MainFragment.this, MainMenuActivity.class);
//                startActivity(intent);
//                finish();
//                break;
            case    R.id.MainFragment_Lin_Control:
                mMainFragmentTxtTitle.setText("제어");
                ColorFilter(mMainFragmentBtnControl,mMainFragmentBtnHome,mMainFragmentBtnInfo,mMainFragmentBtnSetup);
                if(mCurrentFragmentIndex != Constants.FRAGMENT_CONTROL) {
                    SecurityStateRequest();
                    mCurrentFragmentIndex           = Constants.FRAGMENT_CONTROL;
                    mMyGlobal.GlobalInfoIndex       = 0;
                    mMyGlobal.GlobalControlIndex    = 1;
                    fragmentReplace(Constants.FRAGMENT_CONTROL);
                    mMainFragmentBtnControl.setSelected(true);
                    mMainFragmentBtnInfo.setSelected(false);
                    mMainFragmentBtnSetup.setSelected(false);
                    mMainFragmentBtnHome.setSelected(false);
                }
                break;
            case    R.id.MainFragment_Lin_Info:
                mMainFragmentTxtTitle.setText("정보");
                ColorFilter(mMainFragmentBtnInfo,mMainFragmentBtnHome,mMainFragmentBtnControl,mMainFragmentBtnSetup);
                if(mCurrentFragmentIndex != Constants.FRAGMENT_INFO) {
                    SecurityStateRequest();
                    mCurrentFragmentIndex           = Constants.FRAGMENT_INFO;
                    mMyGlobal.GlobalInfoIndex       = 1;
                    mMyGlobal.GlobalControlIndex    = 0;
                    fragmentReplace(Constants.FRAGMENT_INFO);
                    mMainFragmentBtnControl.setSelected(false);
                    mMainFragmentBtnInfo.setSelected(true);
                    mMainFragmentBtnSetup.setSelected(false);
                    mMainFragmentBtnHome.setSelected(false);
                }
                break;
            case    R.id.MainFragment_Lin_Setup:
                mMainFragmentTxtTitle.setText("설정");
                ColorFilter(mMainFragmentBtnSetup,mMainFragmentBtnHome,mMainFragmentBtnInfo,mMainFragmentBtnControl);
                if(mCurrentFragmentIndex != Constants.FRAGMENT_SETUP) {
                    SecurityStateRequest();
                    mCurrentFragmentIndex = Constants.FRAGMENT_SETUP;
                    fragmentReplace(Constants.FRAGMENT_SETUP);
                    mMainFragmentBtnControl.setSelected(false);
                    mMainFragmentBtnInfo.setSelected(false);
                    mMainFragmentBtnSetup.setSelected(true);
                    mMainFragmentBtnHome.setSelected(false);
                }
                break;
            case R.id.MainFragment_Lin_Home:
                mMainFragmentTxtTitle.setText(dong + "동 " + ho + "호");
                ColorFilter(mMainFragmentBtnHome,mMainFragmentBtnControl,mMainFragmentBtnInfo,mMainFragmentBtnSetup);
                if(mCurrentFragmentIndex != Constants.FRAGMENT_HOME) {
                    SecurityStateRequest();
                    mCurrentFragmentIndex = Constants.FRAGMENT_HOME;
                    fragmentReplace(Constants.FRAGMENT_HOME);
                    mMainFragmentBtnControl.setSelected(false);
                    mMainFragmentBtnInfo.setSelected(false);
                    mMainFragmentBtnSetup.setSelected(false);
                    mMainFragmentBtnHome.setSelected(true);
                }
                break;
            case R.id.MainFragment_Img_Mode:
                ModeDialog();
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param String tContents
     * @breif hnml data parser security
     */
    public void HNMLDataParserSecurity(String tContents) {

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

                            if (tName.equals("State")) {
                                tName = "";
                                if (tConvert.length() == 0) {
                                    mSecurityState = "0";
                                } else {
                                    mSecurityState = tParser.getText();
                                    Log.i(TAG,"mSecurityState : " + tParser.getText());
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (tName.equals("State")) {
                                mSecurityState = "0";
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

            if (mSecurityState.equals("0")) {       // 방범해제
                mMainFragmentImgMode.setImageResource(R.drawable.ic_mode_security);
                mMainFragmentImgMode.setEnabled(true);
            }else if (mSecurityState.equals("1")){  // 외출방범
                mMainFragmentImgMode.setImageResource(R.drawable.ic_mode_outing);
                mMainFragmentImgMode.setEnabled(false);
            }else if (mSecurityState.equals("2")){  // 재실
                mMainFragmentImgMode.setImageResource(R.drawable.ic_mode_room);
                mMainFragmentImgMode.setEnabled(false);     //
            }else{
                mMainFragmentImgMode.setImageResource(R.drawable.ic_mode_security);
                mMainFragmentImgMode.setEnabled(true);
            }
        }
    }
    //**********************************************************************************************
}
