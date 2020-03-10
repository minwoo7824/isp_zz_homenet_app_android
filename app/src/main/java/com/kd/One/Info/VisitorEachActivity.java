package com.kd.One.Info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Control.GasActivity;
import com.kd.One.Control.HeatActivity;
import com.kd.One.Control.LightActivity;
import com.kd.One.Control.StandbypowerActivity;
import com.kd.One.Control.VentilationActivity;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Custom.SimpleSideDrawer;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by lwg on 2016-09-01.
 */
public class VisitorEachActivity extends Activity {
    //**********************************************************************************************
    private Messenger                               mVisitorEachResponse        = null;
    private Messenger                               mVisitorEachRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public  LocalConfig                             mLocalConfig;
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
    private String                                  mStringVideoID;
    private String                                  mStringFileSize;
    private String                                  mStringFileData;
    private String                                  mStringVisitTime;
    private String                                  mStringVisitDate;
    private String                                  mStringVisitLocation;
    private String                                  mStringVisitState;
    private int                                     mPosition;
    private ArrayList<String>                       mArrayListVideoID;
    //**********************************************************************************************

    //**********************************************************************************************
    private TextView                                mTextViewDate;
    private TextView                                mTextViewTime;
    private TextView                                mTextViewLocation;
    private TextView                                mTextViewNum;
    private Bitmap                                  mBitmapImage;
    private ImageView                               mImageViewVisit;
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
    private static final int                        TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    //**********************************************************************************************

    //**********************************************************************************************
    private static final String         PARSER_VIDEO_ID             = "VideoId";
    private static final String         PARSER_VIDEO_STATE          = "Status";
    private static final String         PARSER_DATE_TIME            = "DateTime";
    private static final String         PARSER_PAGE_ID              = "VideoId";
    private static final String         PARSER_PAGE_FILE_SIZE       = "FileSize";
    private static final String         PARSER_PAGE_FILE_DATA       = "FileData";
    private static final String         PARSER_PAGE_TIME            = "VisitTime";
    private static final String         PARSER_PAGE_STATE           = "Status";
    private static final String         PARSER_PAGE_LOCATION        = "Location";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate visitor activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info_visitoreach);

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
        mVisitorEachResponse = new Messenger(responseHandler);
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
        mTextViewDate       = (TextView)findViewById(R.id.VisitorEach_TextView_Date);
        mTextViewTime       = (TextView)findViewById(R.id.VisitorEach_TextView_Time);
        mTextViewLocation   = (TextView)findViewById(R.id.VisitorEach_TextView_Location);
        mTextViewNum        = (TextView)findViewById(R.id.VisitorEach_TextView_Num);
        mImageViewVisit     = (ImageView)findViewById(R.id.VisitorEach_ImageView);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif intent data
         */
        mArrayListVideoID = new ArrayList<>();

        if(getIntent().getExtras().containsKey(Constants.INTENT_INFO_DATA_NUM)){
            mPosition           = getIntent().getExtras().getInt(Constants.INTENT_INFO_DATA_NUM);
            mArrayListVideoID   = getIntent().getExtras().getStringArrayList(Constants.INTENT_INFO_DATA_ID);
            mStringVideoID      = mArrayListVideoID.get(mPosition);
            mStringVisitState   = new String();
        }else{
            mPosition           = 0;
            mStringVideoID      = "";
            mStringVisitState   = new String();
            mArrayListVideoID.clear();
        }
        //******************************************************************************************
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
        tMsg.replyTo = mVisitorEachResponse;
        sendMessage(tMsg);
        mVisitorEachRequest = null;
        TimeHandlerVisitorEach(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(VisitorEachRunner);
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
            mVisitorEachRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mVisitorEachResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerVisitorEach(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mVisitorEachResponse;
            sendMessage(tMsg);
            mVisitorEachRequest = null;
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
                case    Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST:
                    VisitorEachResult((KDData)msg.obj);
                    break;
                case    Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST:
                    VisitorEachReadResult((KDData)msg.obj);
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
                TimeOutMoving.TimeOutMoving(mVisitorEachRequest, mVisitorEachResponse, VisitorEachActivity.this);
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
            if(mVisitorEachRequest != null) {
                mVisitorEachRequest.send(tMsg);
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
    private void TimeHandlerVisitorEach(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(VisitorEachRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable VisitorEachRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    VisitorEachRequest();
                    TimeHandlerVisitorEach(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerVisitorEach(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerVisitorEach(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerVisitorEach(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif visitor each request
     */
    private void VisitorEachRequest(){
        mWaitCount          = 0;
        mRequestState       = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVisitorEach(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVisitorEachResponse;
        tMsg.what    = Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST); //11070017
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_VIDEO_ID, mStringVideoID);
        tMsg.setData(bundle);
        sendMessage(tMsg);
        Log.e("Visit Each Activity", "Video data Request : "+tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif visit data read request
     */
    public void VisitorEachReadRequest(){
        mWaitCount = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerVisitorEach(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mVisitorEachResponse;
        tMsg.what    = Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST; //11070029

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));
        bundle.putString(Constants.KD_DATA_VIDEO_ID, mStringVideoID);
        bundle.putString(Constants.KD_DATA_VIDEO_STATE, "READ");
        tMsg.setData(bundle);
        Log.e("Visit read", "Video readnew Request : "+tMsg);
        sendMessage(tMsg);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif each video data result
     * @param tKDData
     */
    private void VisitorEachResult(KDData tKDData){
        mWaitCount = 0;
        TimeHandlerVisitorEach(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            HNMLParserVisitDataEachList(tKDData.ReceiveString);

            byte[] tImageData = KDUtil.StringToByte(mStringFileData);
            ByteArrayInputStream tStream = new ByteArrayInputStream(tImageData);

            mBitmapImage = null;
            mBitmapImage = BitmapFactory.decodeStream(tStream);
            mImageViewVisit.setImageBitmap(mBitmapImage);

            mTextViewDate.setText(mStringVisitDate);
            mTextViewTime.setText(mStringVisitTime);
            mTextViewLocation.setText(mStringVisitLocation);
            mTextViewNum.setText(String.valueOf(mPosition+1)+"/"+String.valueOf(mArrayListVideoID.size()));

            if(mStringVisitState.equals("NEW")){
                VisitorEachReadRequest();
            }else {
                mProgressDialog.Dismiss();
            }
        }else if (tKDData.Result.equals(Constants.HNML_RESULT_COMMUNICATION_ERROR)){
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_contents),
                        mPopupListenerNone);
                mCustomPopup.show();
            }
        }else{
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_contents),
                        mPopupListenerNone);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif each video data result
     * @param tKDData
     */
    public void VisitorEachReadResult(KDData tKDData){
        mWaitCount = 0;
        TimeHandlerVisitorEach(false, TIMER_NULL);
        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            mProgressDialog.Dismiss();
        }else{
            mProgressDialog.Dismiss();
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_contents),
                        mPopupListenerNone);
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
//        Intent intent = new Intent(VisitorEachActivity.this, VisitorActivity.class);
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

//            Intent intent = new Intent(VisitorEachActivity.this, MainMenuActivity.class);
//            startActivity(intent);
//            finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup visitor image none
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
     * @breif activity button
     * @param v
     */
    public void OnClickBtnVisitorEach(View v){
        Intent intent;
        switch (v.getId()){
            case    R.id.VisitorEach_Lin_Home:
                onBackPressed();
                break;
            case    R.id.VisitorEach_Btn_Menu:
                mSlideDrawer.toggleRightDrawer();
                mIsSlideOpen = mSlideDrawer.isRightSideOpened();
                break;
            case    R.id.VisitorEach_Btn_Control:
                onBackPressed();
                break;
            case    R.id.VisitorEach_Btn_Left:
                if(mPosition >= 1){
                    mPosition--;
                    mStringVideoID = mArrayListVideoID.get(mPosition);
                    VisitorEachRequest();
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_end),
                                mPopupListenerNone);
                        mCustomPopup.show();
                    }
                }
                break;
            case    R.id.VisitorEach_Btn_Right:
                if(mArrayListVideoID.size()-1 > mPosition){
                    mPosition++;
                    mStringVideoID = mArrayListVideoID.get(mPosition);
                    VisitorEachRequest();
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(VisitorEachActivity.this, R.layout.popup_basic_onebutton,
                                getString(R.string.Visitor_popup_title), getString(R.string.Visitor_popup_start),
                                mPopupListenerNone);
                        mCustomPopup.show();
                    }
                }
                break;
            default:
                break;
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif hnml data parser visit video data
     * @param String tContents
     */
    public void HNMLParserVisitDataEachList(String tContents) {

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

                            if(tName.equals(PARSER_PAGE_ID)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringVideoID = "";
                                }else{
                                    mStringVideoID = tParser.getText();
                                }
                            }else if(tName.equals(PARSER_PAGE_STATE)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringVisitState = "";
                                }else{
                                    mStringVisitState = tParser.getText();
                                }
                            } else if(tName.equals(PARSER_PAGE_FILE_SIZE)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringFileSize = "";
                                }else{
                                    mStringFileSize = tParser.getText();
                                }
                            }else if(tName.equals(PARSER_PAGE_FILE_DATA)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringFileData = "";
                                }else{
                                    mStringFileData = tParser.getText();
                                }
                            }else if(tName.equals(PARSER_PAGE_LOCATION)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringVisitLocation = "";
                                }else{
                                    mStringVisitLocation = tParser.getText();
                                }
                            }else if(tName.equals(PARSER_PAGE_TIME)){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mStringVisitTime = "";
                                    mStringVisitDate = "";
                                }else{
                                    String tDate;
                                    String tTime;
                                    String tTemp;
                                    int    tHour;

                                    tDate = tParser.getText();
                                    tDate = tDate.substring(0,4)+"년 "+tDate.substring(4,6)+"월 "+tDate.substring(6,8)+"일";
                                    mStringVisitDate = tDate;

                                    tTime = tParser.getText();
                                    tTemp = tTime.substring(8,10);
                                    tHour = Integer.parseInt(tTemp);
                                    if(tHour > 12){
                                        tTemp = String.valueOf(tHour)+"시 "+tTime.substring(10,12)+"분 "+tTime.substring(12,14)+"초";
                                    } else{
                                        tTemp = String.valueOf(tHour)+"시 "+tTime.substring(10,12)+"분 "+tTime.substring(12,14)+"초";
                                    }
                                    mStringVisitTime = tTemp;
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
        }
    }
    //**********************************************************************************************
}
