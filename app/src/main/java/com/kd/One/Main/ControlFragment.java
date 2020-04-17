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
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-15.
 */
public class ControlFragment extends Fragment implements View.OnClickListener {
    private String TAG = "ControlFragment";
    //**********************************************************************************************
    private Messenger                           mMainResponse        = null;
    private Messenger                           mMainRequest         = null;
    private Handler                             mTimeHandler;
    //**********************************************************************************************
    private MyGlobal mMyGlobal;
    private LocalConfig mLocalConfig;
    //**********************************************************************************************

    //**********************************************************************************************
    private LinearLayout                    mControlLinHeat;
    private LinearLayout                    mControlLinGas;
    private LinearLayout                    mControlLinLight;
    private LinearLayout                    mControlLinPower;
    private LinearLayout                    mControlLinVentilation;
    //**********************************************************************************************
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog                mProgressDialog;

    //**********************************************************************************************
    private int                                 mWaitCount              = 0;
    private int                                 mRequestState           = 0;
    private static final int                    REQUEST_DATA_CLEAR      = 0;
    private static final int                    REQUEST_DATA_SEND_START = 1;
    private static final int                    REQUEST_DATA_SEND_DISP  = 2;
    private static final int                    REQUEST_DATA_SEND_WAIT  = 3;

    private static final int                    TIMER_REQUEST           = 1000;  // 500msec
    private static final int                    TIMER_NULL              = 0;
    private static final int                    TIMER_WAIT_TIME         = 20;   // 20 * 500msec = 10sec
    /**
     * @breif control fragment create view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_control, container, false);
    }
    //**********************************************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //registerReceiver();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif service registration
         */
        //MARK START : JMH - 2020-03-13 제어기기 리스트 요청을 위한 메신저 등록
        Intent intent_response = new Intent(getActivity(), HomeTokService.class);
        getActivity().startService(intent_response);
        mMainResponse = new Messenger(responseHandler);
        //MARK END

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getActivity().getBaseContext());
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif global data setup
         */
        //******************************************************************************************
        mMyGlobal = mMyGlobal.getInstance();
        //******************************************************************************************

        //******************************************************************************************
        mProgressDialog = new CustomProgressDialog(getActivity());
        /**
         * @breif dot find activity
         */
        //******************************************************************************************
        mControlLinHeat              = (LinearLayout)getActivity().findViewById(R.id.ControlFrag_Lin_Heat);
        mControlLinGas               = (LinearLayout)getActivity().findViewById(R.id.ControlFrag_Lin_Gas);
        mControlLinLight             = (LinearLayout)getActivity().findViewById(R.id.ControlFrag_Lin_Light);
        mControlLinPower             = (LinearLayout)getActivity().findViewById(R.id.ControlFrag_Lin_Power);
        mControlLinVentilation       = (LinearLayout)getActivity().findViewById(R.id.ControlFrag_Lin_Ventilation);
        //**********************************************************************************************

        mControlLinHeat.setVisibility(View.VISIBLE);
        mControlLinGas.setVisibility(View.VISIBLE);
        mControlLinLight.setVisibility(View.VISIBLE);

        //MARK START : JMH - 2020-03-13 제어기기 모두 표시에서 현재 연동가능한 기기만 표시하도록 수정 (가스, 조명, 난방은 디폴트로 표시)
        mControlLinPower.setVisibility(View.GONE);
        mControlLinVentilation.setVisibility(View.GONE);
        //MARK END

        mControlLinHeat.setOnClickListener(this);
        mControlLinGas.setOnClickListener(this);
        mControlLinLight.setOnClickListener(this);
        mControlLinPower.setOnClickListener(this);
        mControlLinVentilation.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ControlFrag_Lin_Heat : {
                Intent intent = new Intent(getActivity(),HeatActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ControlFrag_Lin_Gas : {
                Intent intent = new Intent(getContext(),GasActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ControlFrag_Lin_Light : {
                Intent intent = new Intent(getContext(),LightActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ControlFrag_Lin_Power : {
                Intent intent = new Intent(getContext(),StandbypowerActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ControlFrag_Lin_Ventilation : {
                Intent intent = new Intent(getContext(),VentilationActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();

        //MARK START : JMH - 2020-03-13 제어기기 리스트 요청을 위한 Service Bind
        Intent intent = new Intent(getActivity(), HomeTokService.class);
        getActivity().bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);
        //MARK END

        mProgressDialog.Show(getString(R.string.progress_request));
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief pause operating
     */
    public void onPause() {
        super.onPause();

        //MARK START : JMH - 2020-03-13 제어기기 리스트 요청을 위한 메시지 요청
        getActivity().unbindService(requestConnection);

        Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
        tMsg.replyTo = mMainResponse;
        sendMessage(tMsg);
        mMainRequest = null;
        TimeHandlerMain(false, TIMER_NULL);

        if(mCustomPopup != null){
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
        //MARK END
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif destroy operating
     */
    public void onDestroy() {
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(MainRunner);
        }
        super.onDestroy();

        //getActivity().unregisterReceiver(appReceiver);
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif send message
     * @param tMsg
     * @brief message send function
     */
    private void sendMessage(Message tMsg) {
        try {
            if(mMainRequest != null) {
                mMainRequest.send(tMsg);
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
    private void TimeHandlerMain(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }

            mTimeHandler.postDelayed(MainRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }
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
                TimeOutMoving.TimeOutMoving(mMainRequest, mMainResponse, getActivity());
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable MainRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    TimeHandlerMain(true, TIMER_REQUEST);
                    MainInformationRequest();
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerMain(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerMain(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerMain(false, TIMER_NULL);
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif main information request
     */
    private void MainInformationRequest(){
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_SEND_WAIT;
        TimeHandlerMain(true, TIMER_REQUEST);

        Message tMsg = Message.obtain();
        tMsg.replyTo = mMainResponse;
        tMsg.what    = Constants.MSG_WHAT_MAIN_INFORMATION_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, tMsg.what);
        bundle.putString(Constants.KD_DATA_DONG, mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG));
        bundle.putString(Constants.KD_DATA_HO, mLocalConfig.getStringValue(Constants.SAVE_DATA_HO));

        tMsg.setData(bundle);
        sendMessage(tMsg);
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief service request connection setup
     */
    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMainRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mMainResponse;
            sendMessage(tMsg);

            mWaitCount      = 0;
            mRequestState   = REQUEST_DATA_SEND_START;
            TimeHandlerMain(true, TIMER_REQUEST);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mMainResponse;
            sendMessage(tMsg);
            mMainRequest = null;
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
                case    Constants.MSG_WHAT_MAIN_INFORMATION_REQUEST:
                    MainInformationResponse((KDData)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif main information response
     * @param tKDData
     */
    private void MainInformationResponse(KDData tKDData){
        Log.e("info receive", tKDData.ReceiveString);
        mProgressDialog.Dismiss();
        mWaitCount      = 0;
        mRequestState   = REQUEST_DATA_CLEAR;
        TimeHandlerMain(false, TIMER_NULL);

        if(tKDData != null) {
            if (tKDData.Result != null) {

                if (tKDData.Result.equals(Constants.HNML_RESULT_OK)) {
                    if (tKDData.ReceiveString != null) {

                        MainInfoParser(tKDData.ReceiveString);
                        if (mMyGlobal.GlobalDeviceList.size() == 0) {
                            mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    } else {
                        mProgressDialog.Dismiss();
                    }
                } else {
                    mProgressDialog.Dismiss();

                    if (mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(getActivity(), R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }
            }
        } else {
            mProgressDialog.Dismiss();
        }
    }

    private void MainInfoParser(String tContents){
        mMyGlobal.GlobalDeviceList = new ArrayList<>();

        if(tContents != null){
            XmlPullParser tParser = Xml.newPullParser();

            try{
                tParser.setInput(new StringReader(tContents));
                int tEventType  = tParser.getEventType();
                String tName    = null;

                while(tEventType != XmlPullParser.END_DOCUMENT){
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
                                if (tName.equals("DeviceType")) {
                                    tName = "";
                                    if (tConvert.length() == 0) {
                                        mMyGlobal.GlobalDeviceList.add("");
                                    } else {
                                        mMyGlobal.GlobalDeviceList.add(tParser.getText().trim());

                                        if (tParser.getText().trim().equals(Constants.DEVICE_GAS)) {
                                            mControlLinGas.setVisibility(View.VISIBLE);
                                        } else if (tParser.getText().trim().equals(Constants.DEVICE_VENTILATION)) {
                                            mControlLinVentilation.setVisibility(View.VISIBLE);
                                        } else if (tParser.getText().trim().equals(Constants.DEVICE_BOILER)) {
                                            mControlLinHeat.setVisibility(View.VISIBLE);
                                        } else if (tParser.getText().trim().equals(Constants.DEVICE_LIGHT)) {
                                            mControlLinLight.setVisibility(View.VISIBLE);
                                        } else if (tParser.getText().trim().equals(Constants.DEVICE_STANDINGPOWER)) {
                                            mControlLinPower.setVisibility(View.VISIBLE);
                                        }
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
            }catch (Exception e){
                e.printStackTrace();
                Log.e("HNML", "Main info Parser Error");
            }

            try {
                String nableUse = mLocalConfig.getStringValue(Constants.SAVE_DATA_NABLE_USE);

                if (nableUse != null) {
                    if (nableUse.equals("true")) {
                        mMyGlobal.GlobalDeviceList.add(Constants.DEVICE_HOMEVIEW);
                    }
                }
                mMyGlobal.GlobalDeviceList.add(Constants.DEVICE_OUTMODE);

            }catch (Exception e) {
                e.printStackTrace();
                Log.e("MainInfoParser", "Main info Parser nableUse Error");
            }
        }
    }

    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    };
}
