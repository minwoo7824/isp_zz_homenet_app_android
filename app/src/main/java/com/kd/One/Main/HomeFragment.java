package com.kd.One.Main;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private String TAG = "HomeFragment";
    //**********************************************************************************************
    private Messenger                               mEmsResponse        = null;
    private Messenger                               mEmsRequest         = null;
    //**********************************************************************************************

    //**********************************************************************************************
    public LocalConfig                              mLocalConfig;
    public MyGlobal                                 mMyGlobal;
    private Handler                                 mTimeHandler;
    private CustomPopupBasic                        mCustomPopup;
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
//    private TextView                                mTxtElectronic;
//    private TextView                                mTxtGas;
//    private TextView                                mTxtWater;
    private TextView                                mTxtYTitle;
    private TextView                                mTxtXTitle;
    private ImageButton                             mBtnLeft;
    private TextView mTextViewDate;
    private ImageButton                             mBtnRight;
    private LinearLayout                            mLinDay;
    private LinearLayout                            mLinMonth;
    private ArrayAdapter<String> mAdapter;
    private FrameLayout                             mFrmCategory;
    private TextView                                mTxtCategory;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<Double>                       mDataDouble;
    private String                                  mUnit;
    private GraphicalView mChartView;

    private int                                     mEmsYear;
    private int                                     mEmsMonth;
    private int                                     mEmsDay;
    private int                                     mEmsDayMax;
    private String                                  mCategory;
    private String                                  mStartTime;
    private String                                  mEndTime;
    private String                                  mQueryType;
    private String                                  mCategoryType;

    private ArrayList<String>                       mDataEmsDate;
    private ArrayList<Double>                       mDataEmsValue;
    private ArrayList<String>                       mDataCategory;

    private ArrayList<String>                       emsUseListArray;
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
    private  String tEmsDay;
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate intro activity
     * @param savedInstanceState
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_home, container, false);
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
        mEmsResponse = new Messenger(responseHandler);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getActivity());
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
        mProgressDialog = new CustomProgressDialog(getActivity());
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif findByView create
         */
//        mTxtElectronic  = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Electronic);
//        mTxtGas         = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Gas);
//        mTxtWater       = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Water);
        mTextViewDate   = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Date);
        mBtnLeft        = (ImageButton)getActivity().findViewById(R.id.HomeFragment_Btn_Left);
        mBtnRight       = (ImageButton)getActivity().findViewById(R.id.HomeFragment_Btn_Right);
        mLinDay         = (LinearLayout)getActivity().findViewById(R.id.HomeFragment_Lin_Day);
        mLinMonth       = (LinearLayout)getActivity().findViewById(R.id.HomeFragment_Lin_Month);
        mFrmCategory    = (FrameLayout)getActivity().findViewById(R.id.HomeFragment_Frm_Use);
        mTxtCategory    = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Category);

        //******************************************************************************************

//        mTxtElectronic.setOnClickListener(this);
//        mTxtGas.setOnClickListener(this);
//        mTxtWater.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
        mLinDay.setOnClickListener(this);
        mLinMonth.setOnClickListener(this);
        mFrmCategory.setOnClickListener(this);
        //******************************************************************************************
        /**
         * @breif graph date setup
         */
        final Calendar c = Calendar.getInstance();
        mEmsYear        = c.get(Calendar.YEAR);
        mEmsMonth       = c.get(Calendar.MONTH) + 1;
        mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
        c.set(mEmsYear, mEmsMonth - 1, 1);
        mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        mQueryType      = Constants.EMS_CALENDAR_DAY;

    }

    void emsUsePopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.popup_ems_use_onebutton, null);
        LinearLayout linParent = (LinearLayout) dialogView.findViewById(R.id.Lin_Popup_Basic_List);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.Btn_Popup_Basic_Cancel);
        builder.setView(dialogView);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = display.getWidth();
        params.height = params.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.show();

        for (int i = 0; i < emsUseListArray.size(); i++){
            emsUseListView(linParent,emsUseListArray.get(i),alertDialog);
        }
    }

    void emsUseListView(LinearLayout linearLayout, final String title, final AlertDialog alertDialog){
        View listView = new View(getActivity());
        listView = getActivity().getLayoutInflater().inflate(R.layout.view_ems_list_item,null);
        final TextView txtTitle = (TextView)listView.findViewById(R.id.txt_ems_list_item_title);

        if (title.equals("Electricity")){
            txtTitle.setText("전기");
        }else if (title.equals("Gas")){
            txtTitle.setText("가스");
        }else if (title.equals("Water")){
            txtTitle.setText("수도");
        }else if (title.equals("Hotwater")){
            txtTitle.setText("온수");
        }else if (title.equals("Heating")){
            txtTitle.setText("난방");
        }else if (title.equals("Cooling")){
            txtTitle.setText("냉방");
        }

        linearLayout.addView(listView);

        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                Calendar c = Calendar.getInstance();
                int tEmsYear = c.get(Calendar.YEAR);
                int tEmsMonth = c.get(Calendar.MONTH) + 1;

                c = Calendar.getInstance();
                mEmsYear        = c.get(Calendar.YEAR);
                mEmsMonth       = c.get(Calendar.MONTH) + 1;
                mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
                c.set(mEmsYear, mEmsMonth - 1, 1);
                mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                if (title.equals("Electricity")){
                    mTxtCategory.setText("전기");
                    mCategoryType = Constants.EMS_CATEGORY_ELECTRIC;
                }else if (title.equals("Gas")){
                    mTxtCategory.setText("가스");
                    mCategoryType = Constants.EMS_CATEGORY_GAS;
                }else if (title.equals("Water")){
                    mTxtCategory.setText("수도");
                    mCategoryType = Constants.EMS_CATEGORY_WATER;
                }else if (title.equals("Hotwater")){
                    mTxtCategory.setText("온수");
                    mCategoryType = Constants.EMS_CATEGORY_HOTWATER;
                }else if (title.equals("Heating")){
                    mTxtCategory.setText("난방");
                    mCategoryType = Constants.EMS_CATEGORY_HEAT;
                }else if (title.equals("Cooling")){
                    mTxtCategory.setText("냉방");
                    mCategoryType = Constants.EMS_CATEGORY_COOLING;
                }

                EmsRequest();
            }
        });
    }
    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();

        emsUseListArray = new ArrayList<>();

        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_ELECTRICITY_USE) == 1){
            emsUseListArray.add("Electricity");
        }
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_GAS_USE) == 1){
            emsUseListArray.add("Gas");
        }
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_WATER_USE) == 1){
            emsUseListArray.add("Water");
        }
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_HOTWATER_USE) == 1){
            emsUseListArray.add("Hotwater");
        }
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_HEATING_USE) == 1){
            emsUseListArray.add("Heating");
        }
        if (mLocalConfig.getIntValue(Constants.SAVE_DATA_EMS_COOLING_USE) == 1){
            emsUseListArray.add("Cooling");
        }

        //MARK START : JMH - 2020-03-13 1010의 경우 리스트 조회기능이 없으므로, 해당 리스트가 없는 경우 전기, 가스, 수도 3종류 표시하도록 수정
        if (emsUseListArray.size() == 0) {
            emsUseListArray.add("Electricity");
            emsUseListArray.add("Gas");
            emsUseListArray.add("Water");
        }
        //MARK END

        if (emsUseListArray.size() > 0){
            if (emsUseListArray.get(0).equals("Electricity")){
                mTxtCategory.setText("전기");
                mCategoryType   = Constants.EMS_CATEGORY_ELECTRIC;
            }else if (emsUseListArray.get(0).equals("Gas")){
                mTxtCategory.setText("가스");
                mCategoryType   = Constants.EMS_CATEGORY_GAS;
            }else if (emsUseListArray.get(0).equals("Water")){
                mTxtCategory.setText("수도");
                mCategoryType   = Constants.EMS_CATEGORY_WATER;
            }else if (emsUseListArray.get(0).equals("Hotwater")){
                mTxtCategory.setText("온수");
                mCategoryType   = Constants.EMS_CATEGORY_HOTWATER;
            }else if (emsUseListArray.get(0).equals("Heating")){
                mTxtCategory.setText("난방");
                mCategoryType   = Constants.EMS_CATEGORY_HEAT;
            }else if (emsUseListArray.get(0).equals("Cooling")){
                mTxtCategory.setText("냉방");
                mCategoryType   = Constants.EMS_CATEGORY_COOLING;
            }
            Intent intent = new Intent(getActivity(), HomeTokService.class);
            getActivity().bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);

            mProgressDialog.Show(getString(R.string.progress_request));
        }else{
            mTxtCategory.setText("");
        }


    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief pause operating
     */
    public void onPause() {
        super.onPause();

        getActivity().unbindService(requestConnection);

        Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
        tMsg.replyTo = mEmsResponse;
        sendMessage(tMsg);
        mEmsRequest = null;
        TimeHandlerEms(false, TIMER_NULL);

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
            mTimeHandler.removeCallbacks(EmsRunner);
        }
        super.onDestroy();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mEmsRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mEmsResponse;
            sendMessage(tMsg);

            mWaitCount    = 0;
            mRequestState = REQUEST_DATA_SEND_START;
            TimeHandlerEms(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mEmsResponse;
            sendMessage(tMsg);
            mEmsRequest = null;
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
                case    Constants.MSG_WHAT_INFO_ENERGY_REQUEST:
                    EmsResponse((KDData)msg.obj);
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
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                TimeOutMoving.TimeOutMoving(mEmsRequest, mEmsResponse, getActivity());
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
            if(mEmsRequest != null) {
                mEmsRequest.send(tMsg);
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
    private void TimeHandlerEms(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(EmsRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable EmsRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    EmsRequest();
                    TimeHandlerEms(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mWaitEachGroupCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerEms(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerEms(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerEms(false, TIMER_NULL);
            }
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

    /**
     * @breif ems data request
     */
    private void EmsRequest(){
        String  tHomeID = "";
        String  tEmsMonth = "";
        tEmsDay   = "";
        String  tEmsYear  = "";

        final Calendar c = Calendar.getInstance();
        int tIntEmsYear = c.get(Calendar.YEAR);
        int tIntEmsMonth = c.get(Calendar.MONTH) + 1;

        tHomeID = "0000" + mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG) +
                mLocalConfig.getStringValue(Constants.SAVE_DATA_HO)+"00";
        tHomeID.trim();

        tEmsYear = String.valueOf(mEmsYear);

        if(mEmsMonth < 10){
            tEmsMonth = "0"+String.valueOf(mEmsMonth);
            tEmsMonth.trim();
        }else{
            tEmsMonth = String.valueOf(mEmsMonth);
        }

        if(tIntEmsYear == mEmsYear && tIntEmsMonth == mEmsMonth){
            if (mEmsDay < 10) {
                tEmsDay = "0" + String.valueOf(mEmsDay);
                tEmsDay.trim();
            } else {
                tEmsDay = String.valueOf(mEmsDay);
            }
        }else {
            if (mEmsDayMax < 10) {
                tEmsDay = "0" + String.valueOf(mEmsDayMax);
                tEmsDay.trim();
            } else {
                tEmsDay = String.valueOf(mEmsDayMax);
            }
        }

        if(mQueryType.equals(Constants.EMS_CALENDAR_MONTH)){
            // 년별
            mStartTime = tEmsYear + "-" + "01" + "-" + "01" + "T" + "00:00:00";
            mEndTime   = tEmsYear + "-" + tEmsMonth + "-" + tEmsDay + "T" + "23:59:59";

            mTextViewDate.setText(tEmsYear+"."+"01"+"."+"01"+ " " + "~" + " " + tEmsYear + "." + tEmsMonth + "." + tEmsDay);
        }else{
            // 월별
            mStartTime = tEmsYear + "-" + tEmsMonth + "-" + "01" + "T" + "00:00:00";
            mEndTime   = tEmsYear + "-" + tEmsMonth + "-" + tEmsDay + "T" + "23:59:59";

            mTextViewDate.setText(tEmsYear+"."+tEmsMonth+"."+"01"+ " " + "~" + " " + tEmsYear + "." + tEmsMonth + "." + tEmsDay);
        }

        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerEms(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mEmsResponse;
        tMsg.what    = Constants.MSG_WHAT_INFO_ENERGY_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_INFO_ENERGY_REQUEST);
        bundle.putString(Constants.KD_DATA_HOME_ID, tHomeID);
        bundle.putString(Constants.KD_DATA_ENERGY_START_TIME, mStartTime);
        bundle.putString(Constants.KD_DATA_ENERGY_END_TIME, mEndTime);
        bundle.putString(Constants.KD_DATA_ENERGY_CATEGORY_TYPE, mCategoryType);
        bundle.putString(Constants.KD_DATA_ENERGY_QUERY_TYPE, mQueryType);
        tMsg.setData(bundle);
        sendMessage(tMsg);
    }

    private void EmsResponse(KDData tKDData){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        TimeHandlerEms(false, TIMER_NULL);
        mProgressDialog.Dismiss();

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){
            HNMLParserEms(tKDData.ReceiveString);
        }else{
            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup none
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
     * @breif popup ok button
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;

//            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
//            startActivity(intent);
//            getActivity().finish();
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif graph setup
     * @param tTitle
     * @param tXtitle
     * @param tDataLength
     * @param tData
     */
    private void setGraph(String tTitle, String tXtitle, int tDataLength, double[] tData){
        DisplayMetrics tDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(tDisplayMetrics);

        List<double[]> tValue = new ArrayList<>();

        XYMultipleSeriesDataset tDataSet;
        XYMultipleSeriesRenderer tRenderer;

        RelativeLayout tLinearLayout;

        tDataSet        = new XYMultipleSeriesDataset();
        tRenderer       = new XYMultipleSeriesRenderer();

        tLinearLayout   = (RelativeLayout)getActivity().findViewById(R.id.HomeFragment_Layout_Graph);
        mTxtYTitle      = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_Y_Title);
        mTxtXTitle      = (TextView)getActivity().findViewById(R.id.HomeFragment_Txt_X_Title);
        tValue.add(tData);

        String[] title  = new String[]{tTitle};

        int[]    tColor = new int[]{getResources().getColor(R.color.colorPrimary)};

        if(tDisplayMetrics.density >= 3.0){
            tRenderer.setLegendTextSize(25);
        }else{
            tRenderer.setLegendTextSize(25);
        }

        int tLength = tColor.length;
        for(int i = 0; i < tLength; i++){
            SimpleSeriesRenderer tSimpleRenderer = new SimpleSeriesRenderer();
            tSimpleRenderer.setColor(tColor[i]);
            tRenderer.addSeriesRenderer(tSimpleRenderer);
        }

        double tMaxY = 0;
        for (int i = 0; i < mDataDouble.size(); i++){
            if(tMaxY < mDataDouble.get(i)){
                tMaxY = mDataDouble.get(i);
            }
        }

        if(tDisplayMetrics.density >= 3.0){
            tRenderer.setYLabelsPadding(28);
            tRenderer.setXLabelsPadding(3);
        }else{
            tRenderer.setYLabelsPadding(50); // y 축과 y 축 단위 padding 거리
            tRenderer.setXLabelsPadding(5); // x 축과 x 축 단위 padding 거리

        }

//        tRenderer.setXTitle(tXtitle);
//        tRenderer.setYTitle(String.format("사용량(%s)\n\n", mUnit));

        mTxtXTitle.setText(tXtitle);
        if(mUnit == null){ mUnit="";}
        mTxtYTitle.setText(String.format("%s", mUnit));

//        tRenderer.setShowLabels(false);


        if(tDisplayMetrics.density >= 3.0){
            tRenderer.setAxisTitleTextSize(30);
            tRenderer.setLabelsTextSize(25);
        }else{
            tRenderer.setAxisTitleTextSize(30);// 사용량, 일 글자크기
            tRenderer.setLabelsTextSize(25); //x 축과 y 축의 단위 글자크기 (0,20, 0,2)
        }

        tRenderer.setXAxisMin(0.5);
//        tRenderer.setXAxisMax(tDataLength+0.5);
        //그래프 X 축 최대값 수정
        if(tXtitle.equals("월별")) {
            tRenderer.setXAxisMax(mEmsMonth + 0.5);
        }else{
//            tRenderer.setXAxisMax( tEmsDay+ 0.5);
            tRenderer.setXAxisMax( Integer.parseInt(tEmsDay)+ 0.5);
        }

        tRenderer.setYAxisMin(0);
        if(tMaxY+(tMaxY*0.1) ==0){
            tMaxY = 1.0;
        }
        tRenderer.setYAxisMax(tMaxY+(tMaxY*0.1));


        tRenderer.setFitLegend(true);
        tRenderer.setShowGrid(true);
        tRenderer.setApplyBackgroundColor(true);

        tRenderer.setBackgroundColor(Color.TRANSPARENT);


        if(tDisplayMetrics.density >= 3.0){
            tRenderer.setMargins(new int[]{0,70,60,20});
        }else{
            tRenderer.setMargins(new int[]{0,70,60,10}); // ( , 왼쪽, 아래쪽, )
//            tRenderer.setMargins(new int[]{0,70,80,20});
        }

        tRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));

        tRenderer.setYAxisAlign(Paint.Align.LEFT, 0);

        tRenderer.setAxesColor(Color.argb(255, 50,50,50)); //축의 색상
//        tRenderer.setLabelsColor(Color.BLACK);
        tRenderer.setYLabelsColor(0,Color.BLACK); //축 밑에 단위 색상 검정색!!
        tRenderer.setXLabelsColor(Color.BLACK);

        int BarWidth=0;
        if(tXtitle.equals("월별")) {
            BarWidth = tDisplayMetrics.widthPixels / (mEmsMonth +5 );
        }else{
            BarWidth = tDisplayMetrics.widthPixels / (Integer.parseInt(tEmsDay) +10 );
        }
        Log.e("barwidth",BarWidth +"");

        if(tDisplayMetrics.density >= 3.0){
            tRenderer.setXLabels(5);
            tRenderer.setYLabels(5);
            tRenderer.setBarWidth(10);
        }else{
            tRenderer.setXLabels(10);
            tRenderer.setYLabels(6);
//            tRenderer.setBarWidth(25); // 바 넓이
            tRenderer.setBarWidth(BarWidth); // 바 넓이 수정
        }

        tRenderer.setXLabelsAlign(Paint.Align.LEFT);
        tRenderer.setYLabelsAlign(Paint.Align.LEFT);


        tRenderer.setPanEnabled(false, false);
        tRenderer.setZoomEnabled(false, false);

        /*tRenderer.setZoomInLimitX(tDataLength/2);
        tRenderer.setZoomRate(1.0f);*/

        tRenderer.setBarSpacing(0.5f);


        /**
         * @breif chart data setup
         */
        SimpleSeriesRenderer    tRender = tRenderer.getSeriesRendererAt(0);
        tRender.setDisplayChartValues(true);

        if(tDisplayMetrics.density >= 3.0){
            tRender.setChartValuesTextSize(20);
        }else{
            tRender.setChartValuesTextSize(20);
        }


        for( int i = 0; i < title.length; i++){
            CategorySeries tSeries = new CategorySeries(title[i]); //일별 사용량
            double[]    v = tValue.get(i);

            int tSeriesLength = v.length;
            Log.e("v","v : "+ v + "length : "+tSeriesLength);
            for(int j = 0; j < tSeriesLength; j++){ //tSeriesLength = 31
                tSeries.add(v[j]);
                Log.e("v","v : "+ v[j]);
            }
            tDataSet.addSeries(tSeries.toXYSeries());
        }

        tLinearLayout.removeView(mChartView);

        mChartView = ChartFactory.getBarChartView(getActivity(), tDataSet, tRenderer, BarChart.Type.STACKED);

        tLinearLayout.addView(mChartView);

    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif parser ems
     * @param tContents
     */
    private void HNMLParserEms(String tContents){
        mDataEmsDate    = new ArrayList<>();
        mDataDouble   = new ArrayList<>();
        mDataEmsDate.clear();
        mDataDouble.clear();

        if(tContents != null) {
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

                            if (tName.equals("EndTime")) {
                                tName = "";
                                if (tConvert.length() == 0) {
                                    mDataEmsDate.add("");
                                } else {
                                    if (mQueryType.equals(Constants.EMS_CALENDAR_DAY)) {
                                        mDataEmsDate.add(tParser.getText().substring(8, 10));
                                    } else {
                                        mDataEmsDate.add(tParser.getText().substring(5, 7));
                                    }
                                }
                            } else if (tName.equals("Amount")) {
                                tName = "";
                                if (tConvert.length() == 0) {
                                    mDataDouble.add(0.0);
                                } else {
                                    mDataDouble.add(Double.parseDouble(tParser.getText().trim()));
                                }
                            } else if(tName.equals("Unit")){
                                tName = "";
                                if(tConvert.length() == 0){
                                    mUnit = "";
                                }else{
                                    mUnit = tParser.getText().trim();
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
                Log.e("Ems info parser", "exception");
            }

            double[] tData = new double[mDataDouble.size()];
            int[]    tDate = new int[mDataEmsDate.size()];

            if(mDataDouble.size() > 0){

                for(int  i = 0; i < mDataDouble.size(); i++){
                    tData[i] = mDataDouble.get(i);
                    tDate[i] = Integer.parseInt(mDataEmsDate.get(i));
                }
                if(mQueryType.equals(Constants.EMS_CALENDAR_DAY)) {
                    double[] tDataNew = new double[mEmsDayMax];

                    for(int t = 0; t < mEmsDayMax; t++){
                        for(int j = 0; j < mDataEmsDate.size(); j++){
                            if(tDate[j] != t + 1){
                                tDataNew[t] = 0.0;
                            }else{
                                tDataNew[t] = tData[j];
                                break;
                            }
                        }
                    }

                    setGraph(getString(R.string.Ems_textview_day), getString(R.string.Ems_textview_info_day), mEmsDayMax, tDataNew);
                }else{
                    double[] tDataNew = new double[12];

                    for(int t = 0; t < 12; t++){
                        for(int j = 0; j < mDataEmsDate.size(); j++){
                            if(tDate[j] != t + 1){
                                tDataNew[t] = 0.0;
                            }else{
                                tDataNew[t] = tData[j];
                                break;
                            }
                        }
                    }
                    setGraph(getString(R.string.Ems_textview_month), getString(R.string.Ems_textview_info_month), 12, tDataNew);
                }
            }else{
                if(mQueryType.equals(Constants.EMS_CALENDAR_DAY)) {
                    setGraph(getString(R.string.Ems_textview_day), getString(R.string.Ems_textview_info_day), mEmsDayMax, tData);
                }else{
                    setGraph(getString(R.string.Ems_textview_month), getString(R.string.Ems_textview_info_month), 12, tData);
                }

                if(mCustomPopup == null) {
                    mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                            getString(R.string.Ems_popup_title), getString(R.string.Ems_popup_none_data),
                            mPopupListenerNone);
                    mCustomPopup.show();
                }
            }
        }
    }
    //**********************************************************************************************

    void ColorFilter(TextView textView1,TextView textView2,TextView textView3){
        textView1.setBackgroundResource(R.drawable.shape_stroke_05px);
        textView2.setBackgroundColor(getResources().getColor(R.color.colorfafafa));
        textView3.setBackgroundColor(getResources().getColor(R.color.colorfafafa));

        textView1.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView2.setTextColor(getResources().getColor(R.color.colorb8b8b8));
        textView3.setTextColor(getResources().getColor(R.color.colorb8b8b8));
    }
    @Override
    public void onClick(View v) {

        Calendar c = Calendar.getInstance();
        int tEmsYear = c.get(Calendar.YEAR);
        int tEmsMonth = c.get(Calendar.MONTH) + 1;

        switch (v.getId()){
//            case R.id.HomeFragment_Txt_Electronic :
//                ColorFilter(mTxtElectronic,mTxtGas,mTxtWater);
//                c = Calendar.getInstance();
//                mEmsYear        = c.get(Calendar.YEAR);
//                mEmsMonth       = c.get(Calendar.MONTH) + 1;
//                mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
//                c.set(mEmsYear, mEmsMonth - 1, 1);
//                mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//                mCategoryType = Constants.EMS_CATEGORY_ELECTRIC;
//                EmsRequest();
//                break;
//            case R.id.HomeFragment_Txt_Gas :
//                ColorFilter(mTxtGas,mTxtElectronic,mTxtWater);
//                c = Calendar.getInstance();
//                mEmsYear        = c.get(Calendar.YEAR);
//                mEmsMonth       = c.get(Calendar.MONTH) + 1;
//                mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
//                c.set(mEmsYear, mEmsMonth - 1, 1);
//                mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//                mCategoryType = Constants.EMS_CATEGORY_GAS;
//
//                EmsRequest();
//                break;
//            case R.id.HomeFragment_Txt_Water :
//                ColorFilter(mTxtWater,mTxtElectronic,mTxtGas);
//                c = Calendar.getInstance();
//                mEmsYear        = c.get(Calendar.YEAR);
//                mEmsMonth       = c.get(Calendar.MONTH) + 1;
//                mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
//                c.set(mEmsYear, mEmsMonth - 1, 1);
//                mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//                mCategoryType = Constants.EMS_CATEGORY_WATER;
//
//                EmsRequest();
//                break;
            case R.id.HomeFragment_Frm_Use :
                emsUsePopup();
                break;
            case R.id.HomeFragment_Btn_Left :
                if(mQueryType.equals(Constants.EMS_CALENDAR_DAY)){
                    if(mEmsMonth > 1) {
                        mEmsMonth -= 1;
                        c.set(mEmsYear, mEmsMonth - 1, 1);
                        mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);

                        EmsRequest();
                    }else{
                        mEmsMonth = 12;
                        mEmsYear -= 1;
                        c.set(mEmsYear, mEmsMonth - 1, 1);
                        mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        EmsRequest();
                    }
                }else{
                    if(tEmsYear >= mEmsYear){
                        mEmsYear -= 1;
                        mEmsMonth = 12;
                        c.set(mEmsYear, mEmsMonth - 1, 1);
                        mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        EmsRequest();
                    }
                }
                break;
            case R.id.HomeFragment_Btn_Right :
                if(mQueryType.equals(Constants.EMS_CALENDAR_DAY)){
                    if(mEmsMonth < 12) {
                        if((mEmsYear == tEmsYear) && (mEmsMonth == tEmsMonth)){
                            if(mCustomPopup == null) {
                                mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                        getString(R.string.Ems_popup_title), getString(R.string.Ems_popup_last),
                                        mPopupListenerNone);
                                mCustomPopup.show();
                            }
                        }else {
                            mEmsMonth += 1;
                            c.set(mEmsYear, mEmsMonth - 1, 1);
                            mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//                            mEmsDay = mEmsDayMax;
//                            mEmsDay = c.get(Calendar.DAY_OF_MONTH);
                            EmsRequest();
                        }
                    }else{
                        if(tEmsYear > mEmsYear) {
                            mEmsYear += 1;
                            mEmsMonth = 1;
                            if((mEmsYear == tEmsYear) && (mEmsMonth == tEmsMonth)){
                                if(mCustomPopup == null) {
                                    mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                            getString(R.string.Ems_popup_title), getString(R.string.Ems_popup_last),
                                            mPopupListenerNone);
                                    mCustomPopup.show();
                                }
                            }else {
                                c.set(mEmsYear, mEmsMonth - 1, 1);
                                mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                                EmsRequest();
                            }
                        }
                    }
                }else{
                    if(tEmsYear > mEmsYear){
                        mEmsYear += 1;
                        if(tEmsYear == mEmsYear){
                            mEmsMonth = c.get(Calendar.MONTH) + 1;
                        }else{
                            mEmsMonth = 12;
                        }
                        c.set(mEmsYear, mEmsMonth - 1, 1);
                        mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        EmsRequest();
                    }else{
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                    getString(R.string.Ems_popup_title), getString(R.string.Ems_popup_last),
                                    mPopupListenerNone);
                            mCustomPopup.show();
                        }
                    }
                }
                break;
            case R.id.HomeFragment_Lin_Day :
                if(mQueryType.equals(Constants.EMS_CALENDAR_MONTH)){
                    mEmsYear        = c.get(Calendar.YEAR);
                    mEmsMonth       = c.get(Calendar.MONTH) + 1;
                    mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
                    c.set(mEmsYear, mEmsMonth - 1, 1);
                    mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    mQueryType      = Constants.EMS_CALENDAR_DAY;

                    for (int i = 0; i < mLinDay.getChildCount(); i++){
                        View view = mLinDay.getChildAt(i);
                        View view1 = mLinMonth.getChildAt(i);
                        if (view instanceof TextView){
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                            ((TextView) view1).setTextColor(getResources().getColor(R.color.colorb8b8b8));
                        }else if (view instanceof LinearLayout){
                            view.setVisibility(View.VISIBLE);
                            view1.setVisibility(View.INVISIBLE);
                        }
                    }
                    EmsRequest();
                }
                break;
            case R.id.HomeFragment_Lin_Month :
                if(mQueryType.equals(Constants.EMS_CALENDAR_DAY)){
                    mEmsYear        = c.get(Calendar.YEAR);
                    mEmsMonth       = c.get(Calendar.MONTH) + 1;
                    mEmsDay         = c.get(Calendar.DAY_OF_MONTH);
                    c.set(mEmsYear, mEmsMonth - 1, 1);
                    mEmsDayMax = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    mQueryType      = Constants.EMS_CALENDAR_MONTH;

                    for (int i = 0; i < mLinDay.getChildCount(); i++){
                        View view1 = mLinDay.getChildAt(i);
                        View view = mLinMonth.getChildAt(i);
                        if (view instanceof TextView){
                            ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                            ((TextView) view1).setTextColor(getResources().getColor(R.color.colorb8b8b8));
                        }else if (view instanceof LinearLayout){
                            view.setVisibility(View.VISIBLE);
                            view1.setVisibility(View.INVISIBLE);
                        }
                    }
                EmsRequest();
            }
                break;
        }
    }
}
