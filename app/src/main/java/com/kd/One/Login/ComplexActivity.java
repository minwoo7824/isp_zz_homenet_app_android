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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomAdapterComplexList;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-07.
 */
public class ComplexActivity extends Activity {
    private String TAG = "ComplexActivity";
    //**********************************************************************************************
    private Messenger                           mComplexResponse        = null;
    private Messenger                           mComplexRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig mLocalConfig;
    private Handler                             mTimeHandler;
    private Handler                             mTimeHandlerOut;
    private CustomAdapterComplexList mCustomAdapterComplex;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
    //**********************************************************************************************

    //**********************************************************************************************
    private EditText                            mEditTextComplexSearch;
    private ListView                            mListViewComplex;
    private Button                              mBtnComplete;
    private LinearLayout                        mComplexLayout;
    private ImageView                           mImageViewReset;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String>                   mComplexCompanyName;
    private ArrayList<String>                   mComplexCompany;
    private ArrayList<String>                   mComplexName;
    private ArrayList<String>                   mComplexIP;
    private ArrayList<String>                   mComplexPort;
    private ArrayList<String>                   mComplex;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                 mPosition;
    private int                                 mSelectFlag;
    private int                                 mListFlag;

    private boolean                             mComplexOutFlag;
    private int                                 mComplexOutTimer;

    private static final int                    LIST_FIRST              = 0;
    private static final int                    LIST_SEARCH             = 1;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                 mWaitCount              = 0;
    private int                                 mRequestState           = 0;
    private static final int                    REQUEST_DATA_CLEAR      = 0;
    private static final int                    REQUEST_DATA_SEND_START = 1;
    private static final int                    REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                    TIMER_REQUEST           = 500;  // 500msec
    private static final int                    TIMER_NULL              = 0;
    private static final int                    TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    private static final int                    TIMER_OUT_TIME          = 10;   // 10 * 500msec = 5sec
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                 COMPLEX_CC              = "CC";
    private static final String                 COMPLEX_NAME            = "ServiceName";
    private static final String                 COMPLEX_IP              = "Address";
    private static final String                 COMPLEX_PORT            = "Port";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @param savedInstanceState
     * @brief oncreate
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
        setContentView(R.layout.activity_login_complex);

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
        mComplexResponse = new Messenger(responseHandler);
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
        mEditTextComplexSearch      = (EditText)findViewById(R.id.Complex_EditText_Name);
        mListViewComplex            = (ListView)findViewById(R.id.Complex_ListView);
        mBtnComplete                = (Button)findViewById(R.id.Complex_Btn_SelectComplete);
        mComplexLayout              = (LinearLayout)findViewById(R.id.Complex_Layout);
        mImageViewReset             = (ImageView)findViewById(R.id.Complex_img_reset);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity event registration
         */
        mEditTextComplexSearch.addTextChangedListener(mEditTextComplexListener);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif activity data registration
         */
        mComplex = new ArrayList<>();
        //******************************************************************************************

        int tComplexCheck = mLocalConfig.getIntValue(Constants.SAVE_DATA_COMPLEX_CHECK);

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

        //******************************************************************************************
        /**
         * @breif keyboard on status -> keyboard outside touch keyboard off
         */
        mComplexLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mEditTextComplexSearch.getWindowToken(), 0);
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
        tMsg.replyTo = mComplexResponse;
        sendMessage(tMsg);
        mComplexRequest = null;
        mComplexOutFlag = false;
        mComplexOutTimer = 0;
        TimeHandlerComplex(false, TIMER_NULL);
        TimeHandlerComplexOut(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(ComplexRunner);
        }
        if (mTimeHandlerOut != null){
            mTimeHandlerOut.removeCallbacks(ComplexOutRunner);
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
            mComplexRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mComplexResponse;
            sendMessage(tMsg);

            ComplexRequestStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mComplexResponse;
            sendMessage(tMsg);
            mComplexRequest = null;
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
                case    Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST:
                    ComplexRequestResult((KDData)msg.obj);
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
                mProgressDialog.Dismiss();
                TimeHandlerComplex(false, TIMER_NULL);
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            } else if( action.equals(Constants.ACTION_APP_SOCKET_CLOSE)){
                mProgressDialog.Dismiss();
                TimeHandlerComplex(false, TIMER_NULL);
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            } else if( action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)){
                mProgressDialog.Dismiss();
                TimeHandlerComplex(false, TIMER_NULL);
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
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
            if(mComplexRequest != null) {
                mComplexRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif timer out
     * @param tState
     * @param tTime
     */
    private void TimeHandlerComplexOut(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandlerOut == null){
                mTimeHandlerOut = new Handler();
            }

            mTimeHandlerOut.postDelayed(ComplexOutRunner, tTime);
        }else{
            mTimeHandlerOut = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler setup function
     * @param tState
     * @param tTime
     */
    private void TimeHandlerComplex(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(ComplexRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable ComplexRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    ComplexRequest();
                    TimeHandlerComplex(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerComplex(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerComplex(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerComplex(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif complex out timer runner
     */
    private Runnable ComplexOutRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandlerOut != null){
                if(mComplexOutFlag == true){
                    mComplexOutTimer++;
                    if(mComplexOutTimer >= TIMER_OUT_TIME){
                        mComplexOutFlag = false;
                        mComplexOutTimer = 0;
                        TimeHandlerComplexOut(false, TIMER_NULL);
                    }else {
                        TimeHandlerComplexOut(true, TIMER_REQUEST);
                    }
                }else{
                    TimeHandlerComplexOut(false, TIMER_NULL);
                }
            }else{
                TimeHandlerComplexOut(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif complex request start
     */
    private void ComplexRequestStart(){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_START;
        TimeHandlerComplex(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif complex request
     */
    private void ComplexRequest(){
        mRequestState = REQUEST_DATA_SEND_WAIT;

        Message tMsg = Message.obtain();
        tMsg.replyTo = mComplexResponse;
        tMsg.what    = Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif complex request result
     * @param tKDData
     */
    private void ComplexRequestResult(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = 0;
        mProgressDialog.Dismiss();
        TimeHandlerComplex(false, TIMER_NULL);

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            if(mCustomPopup != null) {
                mCustomPopup.dismiss();
                mCustomPopup = null;
            }
            ComplexParser(tKDData.ReceiveString);
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif edit text change event
     */
    private TextWatcher mEditTextComplexListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() != 0){
                mComplex = new ArrayList<>();
                int size = mComplexCompanyName.size();

                for (int i = 0; i < size; i++){
                    String tSearchData = mComplexCompanyName.get(i).toString();
                    String tKeyWord   = s.toString();

                    boolean isAdd = false;
                    String iniName = HangulUtils.getHangulInitialSound(tSearchData, tKeyWord);
                    if (iniName.indexOf(tKeyWord) >= 0) {
                        isAdd = true;
                    }
                    if(isAdd) {
                        mComplex.add(tSearchData);
                    }
                }

                mCustomAdapterComplex = new CustomAdapterComplexList(ComplexActivity.this, mComplex);
                mListViewComplex.setAdapter(mCustomAdapterComplex);
                mCustomAdapterComplex.notifyDataSetChanged();
                mPosition = 0;
                mListFlag = LIST_SEARCH;

            }else {
                mCustomAdapterComplex = new CustomAdapterComplexList(ComplexActivity.this, mComplexCompanyName);
                mListViewComplex.setAdapter(mCustomAdapterComplex);
                mCustomAdapterComplex.notifyDataSetChanged();
                mPosition = 0;
                mListFlag = LIST_FIRST;
            }
            mSelectFlag = 0;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif check box checked listener
     */
    private CompoundButton.OnCheckedChangeListener mCheckBoxComplexSaveListener = new CompoundButton.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_CHECK, 1);
            }else{
                mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_CHECK, 0);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif complex list view on item click listener
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                TextView textView = (TextView)linearLayout.getChildAt(0);
                textView.setTextColor(getResources().getColor(R.color.color585858));
            }

            TextView textView = (TextView) view.findViewById(R.id.TextView_Complex_List);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            mPosition   = position;
            mSelectFlag = 1;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief back button selected
     */
    public void onBackPressed() {
        if(mComplexOutFlag == false){
            mComplexOutFlag = true;
            mComplexOutTimer = 0;
            TimeHandlerComplexOut(true, TIMER_REQUEST);
            Toast.makeText(this, getString(R.string.Complex_toast_out), Toast.LENGTH_SHORT).show();
        }else{
            finish();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif activity button
     * @param v
     */
    public void OnClickBtnComplex(View v){
        switch (v.getId()){
            case    R.id.Complex_img_Refresh:
                ComplexRequestStart();
                break;
            case R.id.Complex_img_reset:
                mEditTextComplexSearch.setText("");
                break;
            case    R.id.Complex_Btn_SelectComplete:
                if(mSelectFlag == 0){
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Complex_popup_title), getString(R.string.Complex_popup_contents),
                                mPopupListenerComplex);
                        mCustomPopup.show();
                    }
                }else {
                    mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_CHECK, 1);
                    // data save activity change
                    if(mListFlag == LIST_FIRST) {
                        mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_NAME, mComplexCompanyName.get(mPosition));
                        mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_IP, mComplexIP.get(mPosition));
                        mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_PORT, Integer.parseInt(mComplexPort.get(mPosition)));
                    }else{
                        for(int i = 0; i < mComplexCompanyName.size(); i++){
                            if(mComplexCompanyName.get(i).equals(mComplex.get(mPosition))){
                                mLocalConfig.setValue(Constants.SAVE_DATA_COMPLEX_NAME, mComplexCompanyName.get(i));
                                mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_IP, mComplexIP.get(i));
                                mLocalConfig.setValue(Constants.SAVE_DATA_LOCAL_PORT, Integer.parseInt(mComplexPort.get(i)));
                                break;
                            }
                        }
                    }
                    Intent intent = new Intent(ComplexActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok button listener
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            int tLocalPort = 0;
            //ComplexRequestStart();

            mComplexCompanyName = new ArrayList<>();
            mComplexCompany     = new ArrayList<>();
            mComplexName        = new ArrayList<>();
            mComplexIP          = new ArrayList<>();
            mComplexPort        = new ArrayList<>();

            mComplexIP.add(mLocalConfig.getStringValue(Constants.SAVE_DATA_LOCAL_IP));
            tLocalPort = mLocalConfig.getIntValue(Constants.SAVE_DATA_LOCAL_PORT);
            mComplexPort.add(String.valueOf(tLocalPort));
            mComplexCompanyName.add(mLocalConfig.getStringValue(Constants.SAVE_DATA_COMPLEX_NAME));
            mCustomAdapterComplex = new CustomAdapterComplexList(ComplexActivity.this, mComplexCompanyName);
            mListViewComplex.setAdapter(mCustomAdapterComplex);
            mListViewComplex.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListViewComplex.setOnItemClickListener(mItemClickListener);
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok button listener
     */
    private View.OnClickListener mPopupListenerComplex = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    };
    //**********************************************************************************************

    private void ComplexParser(String tContents){
        mComplexCompanyName = new ArrayList<>();
        mComplexCompany     = new ArrayList<>();
        mComplexName        = new ArrayList<>();
        mComplexIP          = new ArrayList<>();
        mComplexPort        = new ArrayList<>();

        if(tContents != null){
            XmlPullParser tParser = Xml.newPullParser();

            try{
                tParser.setInput(new StringReader(tContents));
                int tEventType  = tParser.getEventType();
                String tName    = null;

                while(tEventType != XmlPullParser.END_DOCUMENT){
                    String name = tParser.getName();

                    switch(tEventType){
                        case    XmlPullParser.START_TAG:
                            tName = name;
                            if(name.equals("Data")){
                                tName = tParser.getAttributeValue(null, "name");
                            }
                            break;
                        case    XmlPullParser.TEXT:
                            String tConvert = tParser.getText().trim();

                            if(tName.equals(COMPLEX_CC)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mComplexCompany.add("");
                                }else{
                                    mComplexCompany.add(tParser.getText());
                                }
                            } else if(tName.equals(COMPLEX_NAME)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mComplexName.add("");
                                }else{
                                    mComplexName.add(tParser.getText());
                                }
                            } else if(tName.equals(COMPLEX_IP)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mComplexIP.add("");
                                }else{
                                    mComplexIP.add(tParser.getText());
                                }
                            } else if(tName.equals(COMPLEX_PORT)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mComplexPort.add("");
                                }else{
                                    mComplexPort.add(tParser.getText());
                                }
                            }
                            break;
                        case    XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    tEventType = tParser.next();
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.e("HNML", "Complex Parser Error");
            }

            mSelectFlag = 0;

            if(mComplexCompany.size() != 0) {
                mListFlag = LIST_FIRST;

                for (int i = 0; i < mComplexCompany.size(); i++) {
                    mComplexCompanyName.add(mComplexCompany.get(i) + " " + mComplexName.get(i));
                }

                mCustomAdapterComplex = new CustomAdapterComplexList(this, mComplexCompanyName);
                mListViewComplex.setAdapter(mCustomAdapterComplex);
                mListViewComplex.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mListViewComplex.setOnItemClickListener(mItemClickListener);
            }else{
                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(ComplexActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Complex_popup_error_title), getString(R.string.Complex_popup_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif list hangul search
     */
    public static class ListSearcher{
        private static final char HANGUL_BEGIN_UNICODE  = 44032;    //가
        private static final char HANGUL_LAST_UNICODE   = 55203;    // 힣
        private static final char HANGUL_BASE_UNIT      = 580;      // 각 자음마다 가지는 글자수

        private static final char[] INITIAL_HANGUL = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

        /**
         * @brief 해당 문자가 initial hangul 인지 검사
         */
        private static boolean isInitialList(char searchar){
            for(char c:INITIAL_HANGUL){
                if(c == searchar){
                    return true;
                }
            }
            return false;
        }

        /**
         * @breif 해당 문자의 자음을 얻는다.
         * @param c
         * @return
         */
        private static char getInitialList(char c){
            int hanBegin = (c - HANGUL_BEGIN_UNICODE);
            int index    = hanBegin / HANGUL_BASE_UNIT;
            return INITIAL_HANGUL[index];

        }

        /**
         * @breif 해당 문자가 한글인지 검사
         * @param c
         * @return
         */
        private static boolean isHangul(char c){
            return HANGUL_BEGIN_UNICODE <= c && c <= HANGUL_LAST_UNICODE;
        }

        /**
         * @breif 생성자
         */
        private ListSearcher(){}

        private static boolean matchString(String value, String search){
            String TAG = "matchString";
            int t = 0;
            int seof = value.length() - search.length();

            int slen = search.length();

            if(seof < 0){
                return false;
            }

            for(int i = 0; i <= seof; i++){
                t = 0;
                while(t < slen){
                    Log.i(TAG,"list : " + search.charAt(t));
                    if(isInitialList(search.charAt(t)) == true && isHangul(value.charAt(i+t))){
                        if(getInitialList(value.charAt(i+t)) == search.charAt(t)){
                            t++;
                        }else {
                            break;
                        }
                    }else{
                        if(value.charAt(i+t) == search.charAt(t)){
                            t++;
                        }else{
                            break;
                        }
                    }
                }

                if(t == slen){
                    return true;
                }
            }
            return false;
        }
    }
    //**********************************************************************************************

    public static class HangulUtils {

        private static String toHexString(int decimal) {
            Long intDec = Long.valueOf(decimal);
            return Long.toHexString(intDec);
        }

        public static final int HANGUL_BEGIN_UNICODE = 44032; // 가
        public static final int HANGUL_END_UNICODE = 55203; // 힣
        public static final int HANGUL_BASE_UNIT = 588;

        public static final int[] INITIAL_SOUND_UNICODE = { 12593, 12594, 12596,
                12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615,
                12616, 12617, 12618, 12619, 12620, 12621, 12622 };

        public static final char[] INITIAL_SOUND = { 'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
                'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ' };

        /**
         * 문자를 유니코드(10진수)로 변환 후 반환 한다.
         *
         * @param ch
         * @return
         */
        public static int convertCharToUnicode(char ch) {
            return (int) ch;
        }

        /**
         * 문자열을 유니코드(10진수)로 변환 후 반환 한다.
         *
         * @param str
         * @return
         */
        public static int[] convertStringToUnicode(String str) {
            int[] unicodeList = null;
            if (str != null) {
                unicodeList = new int[str.length()];
                for (int i = 0; i < str.length(); i++) {
                    unicodeList[i] = convertCharToUnicode(str.charAt(i));
                }
            }

            return unicodeList;
        }

        /**
         * 유니코드(16진수)를 문자로 변환 후 반환 한다.
         *
         * @param hexUnicode
         * @return
         */
        public static char convertUnicodeToChar(String hexUnicode) {
            return (char) Integer.parseInt(hexUnicode, 16);
        }

        /**
         * 유니코드(10진수)를 문자로 변환 후 반환 한다.
         *
         * @param unicode
         * @return
         */
        public static char convertUnicodeToChar(int unicode) {
            return convertUnicodeToChar(toHexString(unicode));
        }

        /**
         *
         * @param value
         * @return
         */
        public static String getHangulInitialSound(String value) {
            StringBuffer result = new StringBuffer();
            int[] unicodeList = convertStringToUnicode(value);
            for (int unicode : unicodeList) {

                if (HANGUL_BEGIN_UNICODE <= unicode
                        && unicode <= HANGUL_END_UNICODE) {
                    int tmp = (unicode - HANGUL_BEGIN_UNICODE);
                    int index = tmp / HANGUL_BASE_UNIT;
                    result.append(INITIAL_SOUND[index]);
                } else {
                    result.append(convertUnicodeToChar(unicode));

                }
            }
            return result.toString();
        }

        public static boolean[] getIsChoSungList(String name) {
            if (name == null) {
                return null;
            }
            boolean[] choList = new boolean[name.length()];
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);

                boolean isCho = false;
                for (char cho : INITIAL_SOUND) {
                    if (c == cho) {
                        isCho = true;
                        break;
                    }
                }
                choList[i] = isCho;
            }
            return choList;
        }

        public static String getHangulInitialSound(String value,
                                                   String searchKeyword) {
            return getHangulInitialSound(value, getIsChoSungList(searchKeyword));
        }

        public static String getHangulInitialSound(String value, boolean[] isChoList) {
            StringBuffer result = new StringBuffer();
            int[] unicodeList = convertStringToUnicode(value);
            for (int idx = 0; idx < unicodeList.length; idx++) {
                int unicode = unicodeList[idx];

                if (isChoList != null && idx <= (isChoList.length - 1)) {
                    if (isChoList[idx]) {
                        if (HANGUL_BEGIN_UNICODE <= unicode
                                && unicode <= HANGUL_END_UNICODE) {
                            int tmp = (unicode - HANGUL_BEGIN_UNICODE);
                            int index = tmp / HANGUL_BASE_UNIT;
                            result.append(INITIAL_SOUND[index]);
                        } else {
                            result.append(convertUnicodeToChar(unicode));
                        }
                    } else {
                        result.append(convertUnicodeToChar(unicode));
                    }
                } else {
                    result.append(convertUnicodeToChar(unicode));
                }
            }

            return result.toString();
        }

        public static void main(String[] args) {
            for (char ch : HangulUtils.INITIAL_SOUND) {
                System.out.print(HangulUtils.convertCharToUnicode(ch) + ",");
            }
        }
    }
}
