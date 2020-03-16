package com.kd.One.Info;

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
import android.widget.AdapterView;
import android.widget.ListView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Control.GasActivity;
import com.kd.One.Control.HeatActivity;
import com.kd.One.Control.LightActivity;
import com.kd.One.Control.StandbypowerActivity;
import com.kd.One.Control.VentilationActivity;
import com.kd.One.Custom.CustomAdapterVisitor;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.Main.InfoFragment;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HN_USER on 2016-07-29.
 */
public class VisitorActivity extends Activity{
    //**********************************************************************************************
    private Messenger                               mVisitorResponse        = null;
    private Messenger                               mVisitorRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig mLocalConfig;
    public MyGlobal mMyGlobal;
    private Handler                                 mTimeHandler;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;
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
    private ArrayList<String>                       mArrayVideoID;
    private ArrayList<String>                       mArrayDate;
    private ArrayList<String>                       mArrayTime;
    private ArrayList<String>                       mArrayVideoState;
    //**********************************************************************************************

    //**********************************************************************************************
    private CustomAdapterVisitor mListAdapter;
    private ListView                                mListViewVisitor;
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
    private static final int                        TIMER_WAIT_TIME         = 30;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String                     PARSER_VIDEO_ID             = "VideoId";
    private static final String                     PARSER_VIDEO_STATE          = "Status";
    private static final String                     PARSER_DATE_TIME            = "DateTime";
    private static final String                     PARSER_PAGE_ID              = "VideoId";
    private static final String                     PARSER_PAGE_FILE_SIZE       = "FileSize";
    private static final String                     PARSER_PAGE_FILE_DATA       = "FileData";
    private static final String                     PARSER_PAGE_TIME            = "VisitTime";
    private static final String                     PARSER_PAGE_STATE           = "Status";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate visitor activity
     * @param savedInstanceState
     */
    private SimpleDateFormat simpleDateFormatYMD = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatHms = new SimpleDateFormat("HH-mm-ss");
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
        setContentView(R.layout.activity_info_visitor);

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
        mVisitorResponse = new Messenger(responseHandler);
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
        mListViewVisitor     = (ListView)findViewById(R.id.VisitorList_ListView);
        //******************************************************************************************

        //******************************************************************************************

        mProgressDialog.Show(getString(R.string.progress_request));
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
        tMsg.replyTo = mVisitorResponse;
        sendMessage(tMsg);
        mVisitorRequest = null;
        TimeHandlerVisitor(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(VisitorRunner);
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
            mVisitorRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mVisitorResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerVisitor(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mVisitorResponse;
            sendMessage(tMsg);
            mVisitorRequest = null;
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
                case    Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST:
                    VisitorListResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mVisitorRequest, mVisitorResponse, VisitorActivity.this);
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
            if(mVisitorRequest != null) {
                mVisitorRequest.send(tMsg);
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
    private void TimeHandlerVisitor(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(VisitorRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable VisitorRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    VisitorListRequest();
                    TimeHandlerVisitor(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerVisitor(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(VisitorActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerVisitor(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerVisitor(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif visitor list request
     */
    public void VisitorListRequest(){
        mWaitCount          = 0;
        mRequestState       = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVisitor(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVisitorResponse;
        tMsg.what    = Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_END_DATE,simpleDateFormatYMD.format(new Date(System.currentTimeMillis()))+"T"+simpleDateFormatHms.format(new Date(System.currentTimeMillis())));
        tMsg.setData(bundle);
        sendMessage(tMsg);
        Log.e("Visit Activity", "Video List Request");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief visit list request result
     * @param tKDData
     */
    public void VisitorListResult(KDData tKDData){
        mWaitCount = 0;
        mProgressDialog.Dismiss();
        mRequestState = REQUEST_DATA_CLEAR;
        TimeHandlerVisitor(false, TIMER_NULL);
        if(tKDData != null) {
            if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                Log.e("Visit Activity", "Video List Response success");
                HNMLParserVisitList(tKDData.ReceiveString);
                if (mArrayVideoID.size() == 0) {
                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(VisitorActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_contents),
                                mPopupListenerNone);
                        mCustomPopup.show();
                    }
                }
            } else if (tKDData.Result.equals(Constants.HNML_RESULT_COMMUNICATION_ERROR)) {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VisitorActivity.this, R.layout.popup_basic_onebutton,
                            getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                            mPopupListenerOK);
                    mCustomPopup.show();
                }
            } else {
                if (mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(VisitorActivity.this, R.layout.popup_basic_onebutton,
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

/*
        Intent intent = new Intent(VisitorActivity.this, InfoFragment.class);
//        Intent intent = new Intent(getActivity(), InfoFragment.class);
//        intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_INFO);
//        getActivity().startService(intent);
        startActivity(intent);
        finish();*/
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

//            Intent intent = new Intent(VisitorActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
            onBackPressed();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif visitor image none
     */
    private View.OnClickListener mPopupListenerNone = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif list item click listener
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(VisitorActivity.this, VisitorEachActivity.class);
            intent.putExtra(Constants.INTENT_INFO_DATA_NUM, position);
            intent.putExtra(Constants.INTENT_INFO_DATA_ID, mArrayVideoID);
            startActivity(intent);
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif activity visitor button
     * @param v
     */
    public void OnClickBtnVisitorList(View v){
        switch(v.getId()){
            case    R.id.VisitorList_Lin_Home:
                onBackPressed();
                break;
            case    R.id.VisitorList_Img_Refresh:
                mWaitCount      = 0;
                mRequestState   = REQUEST_DATA_SEND_START;
                TimeHandlerVisitor(true, TIMER_REQUEST);
                mProgressDialog.Show(getString(R.string.progress_request));
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
    public void HNMLParserVisitList(String tContents) {
        mArrayVideoID       = new ArrayList<>();
        mArrayVideoState    = new ArrayList<>();
        mArrayDate          = new ArrayList<>();
        mArrayTime          = new ArrayList<>();

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

                            if(tName.equals(PARSER_VIDEO_ID)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayVideoID.add("");
                                }else{
                                    mArrayVideoID.add(tParser.getText());
                                }
                            }else if(tName.equals(PARSER_VIDEO_STATE)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayVideoState.add("");
                                }else{
                                    mArrayVideoState.add(tParser.getText());
                                }
                            }else if(tName.equals(PARSER_DATE_TIME)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mArrayTime.add("");
                                    mArrayDate.add("");
                                }else{
                                    String tDate;
                                    String tTime;
                                    String tTemp;
                                    int    tHour;

                                    tDate = tParser.getText();
                                    tDate = tDate.substring(0,4)+"."+tDate.substring(5,7)+"."+tDate.substring(8,10);
                                    mArrayDate.add(tDate);

                                    tTime = tParser.getText();
                                    tTemp = tTime.substring(11,13);
                                    tHour = Integer.parseInt(tTemp);
                                    /*if(tHour > 12){
                                        tTemp = "오후"+" "+String.valueOf(tHour-12)+tTime.substring(13,19);
                                    } else{
                                        tTemp = "오전"+" "+String.valueOf(tHour)+tTime.substring(13,19);
                                    }*/
                                    tTemp = String.valueOf(tHour) + "시 " + tTime.substring(14, 16) + "분 " +
                                            tTime.substring(17, 19) + "초";
                                    mArrayTime.add(tTemp);
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

            if(mArrayVideoID.size() != 0) {
                mListAdapter = new CustomAdapterVisitor(this, mArrayDate, mArrayTime, mArrayVideoState);
                mListViewVisitor.setOnItemClickListener(mItemClickListener);
                mListViewVisitor.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mListViewVisitor.setAdapter(mListAdapter);
            }
        }
    }
    //**********************************************************************************************
}
