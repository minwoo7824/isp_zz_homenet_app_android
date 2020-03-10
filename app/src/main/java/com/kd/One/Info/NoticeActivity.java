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
import android.widget.AbsListView;
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
import com.kd.One.Custom.CustomAdapterNotice;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by HN_USER on 2016-07-29.
 */
public class NoticeActivity extends Activity implements AbsListView.OnScrollListener{
    private String TAG = "NoticeActivity";
    //**********************************************************************************************
    private Messenger                               mNoticeResponse        = null;
    private Messenger                               mNoticeRequest         = null;
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
    private CustomAdapterNotice mListAdapter;
    private ListView                                mListViewNotice;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String>                       mArrayListDate;
    private ArrayList<String>                       mArrayListTime;
    private ArrayList<String>                       mArrayListTitle;
    private ArrayList<String>                       mArrayListContents;
    private String                                  mStringInfoContents;
    private int                                     mTotalCount;
    private int                                     mPageNum;
    private boolean                                 mDataTry;
    private int                                     mListPosition;
    private int                                     mListFirstData;
    private int                                     mListFirstDataOld;
    private boolean lastItemVisibleFlag = false;
    private boolean mLockListView = false;
    //**********************************************************************************************

    //**********************************************************************************************
    private int                                     mWaitGroupCount         = 0;
    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1000;  // 1500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 10;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate notice activity
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
        setContentView(R.layout.activity_info_notice);

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
        mNoticeResponse = new Messenger(responseHandler);
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
        mStringInfoContents = "";
        mListViewNotice     = (ListView)findViewById(R.id.NoticeList_ListView);
        mListViewNotice.setOnScrollListener(this);
        //******************************************************************************************

        //******************************************************************************************

        mArrayListDate      = new ArrayList<>();
        mArrayListTime      = new ArrayList<>();
        mArrayListTitle     = new ArrayList<>();
        mArrayListContents  = new ArrayList<>();

        mPageNum        = 0;
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_SEND_START;
        TimeHandlerNotice(true, TIMER_REQUEST);
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
        tMsg.replyTo = mNoticeResponse;
        sendMessage(tMsg);
        mNoticeRequest  = null;
        mDataTry        = false;
        TimeHandlerNotice(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(NoticeRunner);
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
            mNoticeRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mNoticeResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mNoticeResponse;
            sendMessage(tMsg);
            mNoticeRequest = null;
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
                case    Constants.MSG_WHAT_INFO_NOTICE_REQUEST:
                    NoticeResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mNoticeRequest, mNoticeResponse, NoticeActivity.this);
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
            if(mNoticeRequest != null) {
                mNoticeRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
        int count = totalItemCount - visibleItemCount;

        Log.e("on scroll total count", String.valueOf(totalItemCount) + "first : " + String.valueOf(firstVisibleItem));
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);

    }

    //**********************************************************************************************

    //**********************************************************************************************
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState){
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            if (mTotalCount >= 20){
                mLockListView = true;
                mListPosition = mListViewNotice.getFirstVisiblePosition();
                mDataTry = true;
                mPageNum += 1;
                Log.i(TAG,"paging");
                NoticeRequest();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif time handler setup function
     * @param tState
     * @param tTime
     */
    private void TimeHandlerNotice(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(NoticeRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable NoticeRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    NoticeRequest();
                    TimeHandlerNotice(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerNotice(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(NoticeActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerNotice(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerNotice(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif NOTICE state request
     */
    private void NoticeRequest(){
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerNotice(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mNoticeResponse;
        tMsg.what    = Constants.MSG_WHAT_INFO_NOTICE_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_INFO_NOTICE_REQUEST);
        bundle.putString(Constants.KD_DATA_LIST_NUM, Integer.toString(mPageNum * 20));
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif notice result
     * @param tKDData
     */
    public void NoticeResult(KDData tKDData){
        mWaitCount  = 0;
        mDataTry    = false;
        TimeHandlerNotice(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mStringInfoContents = tKDData.ReceiveString;
            HNMLDataParserNotice(tKDData.ReceiveString);
            mLockListView = false;
            mProgressDialog.Dismiss();
        }else{
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(NoticeActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
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
//        Intent intent = new Intent(NoticeActivity.this, MainFragment.class);
//        intent.putExtra(Constants.INTENT_FRAGMENT_STATE, Constants.FRAGMENT_INFO);
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
            mDataTry     = false;

//            Intent intent = new Intent(NoticeActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
            onBackPressed();
        }
    };

    private View.OnClickListener mPopupListenerFinalOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
            mDataTry     = false;

//            Intent intent = new Intent(NoticeActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif activity notice button
     * @param v
     */
    public void OnClickBtnNoticeList(View v){
        switch(v.getId()){
            case    R.id.NoticeList_Lin_Home:
                onBackPressed();
                break;
            case    R.id.NoticeList_Img_Refresh:
                mWaitCount      = 0;
                mPageNum        = 0;
                mListPosition   = 0;
                mArrayListTitle.clear();
                mArrayListDate.clear();
                mArrayListTime.clear();
                mArrayListContents.clear();
                NoticeRequest();
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
    public void HNMLDataParserNotice(String tContents) {

        ArrayList<String> mStartTag = new ArrayList<>();

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
                                mStartTag.add(tParser.getAttributeValue(null, "name"));
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (tName.equals("reg_time") && tParser.getText() != "") {
                                tName = "";
                                String tDate;
                                String tTemp;

                                tDate = tParser.getText();
                                tTemp = tDate.substring(0,4)+"."+tDate.substring(4,6) +"."+tDate.substring(6,8);
                                mArrayListDate.add(tTemp);
                            }else if(tName.equals("title") && tParser.getText() != ""){
                                tName = "";
                                mArrayListTitle.add(tParser.getText());
                            }else if(tName.equals("contents") && tParser.getText() != ""){
                                tName = "";
                                mArrayListContents.add(tParser.getText());
                            }else if(tName.equals("totalcount") && tParser.getText() != ""){
                                tName = "";
                                mTotalCount = Integer.valueOf(tParser.getText());
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

            if(mArrayListTitle.size() != 0) {
                mListAdapter = new CustomAdapterNotice(this, mArrayListDate, mArrayListTitle,mArrayListContents);
                mListViewNotice.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                mListViewNotice.setSelector(R.color.colorOrange);
                mListViewNotice.setAdapter(mListAdapter);
                mListViewNotice.setSelection(mListPosition);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }
    //**********************************************************************************************
}
