package com.kd.One.Service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.Xml;

import com.kd.One.Common.Constants;
import com.kd.One.Common.KDData;
import com.kd.One.Common.KDUtil;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Service.Protocol.ControlProtocol.KDBchBreaker;
import com.kd.One.Service.Protocol.ControlProtocol.KDGas;
import com.kd.One.Service.Protocol.ControlProtocol.KDHeat;
import com.kd.One.Service.Protocol.ControlProtocol.KDLight;
import com.kd.One.Service.Protocol.ControlProtocol.KDSecurity;
import com.kd.One.Service.Protocol.ControlProtocol.KDStandbypower;
import com.kd.One.Service.Protocol.ControlProtocol.KDVentilation;
import com.kd.One.Service.Protocol.DoorOpenProtocol.KDDoorOpen;
import com.kd.One.Service.Protocol.InfoProtocol.KDEms;
import com.kd.One.Service.Protocol.InfoProtocol.KDNotice;
import com.kd.One.Service.Protocol.InfoProtocol.KDVisitor;
import com.kd.One.Service.Protocol.KDHeader;
import com.kd.One.Service.Protocol.LoginProtocol.KDComplex;
import com.kd.One.Service.Protocol.LoginProtocol.KDLogin;
import com.kd.One.Service.Protocol.MainProtocol.KDMain;
import com.kd.One.Service.Protocol.SetupProtocol.KDLogout;
import com.kd.One.Service.Protocol.SetupProtocol.KDMember;
import com.kd.One.Service.Protocol.SetupProtocol.KDPushSetting;
import com.kd.One.Service.Protocol.SetupProtocol.KDVersion;
import com.kd.One.Service.Protocol.SetupProtocol.KDWithdrawal;

import org.xmlpull.v1.XmlPullParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class HomeTokService extends Service {

    //**********************************************************************************************
    private final   Messenger               mReceiveMessenger = new Messenger(new IncommingHandler());
    private         Socket                  mSocket = null;
    private         ArrayList<Messenger>    mSendMessenger = new ArrayList<>();
    private         InputStreamReader       mInputStreamReader;
    //**********************************************************************************************

    private         Handler                 mTimeHandler;

    private         int                     mTimeCnt            = 0;
    private static final int                TIME_SERVICE_CYCLE  = 1000;
    private static final int                TIME_SERVICE_MAX    = 600;
    private static final int                TIME_SERVICE_NULL   = 0;
    private boolean  stopFlag = false;
    //**********************************************************************************************
    public HomeTokService() {

    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @param intent
     * @return
     * @breif onbind service
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mReceiveMessenger.getBinder();
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif receive service message filter
     */
    class IncommingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MSG_WHAT_TCP_SOCKET_CLOSE:
                    disConnectTcpClient();
                    break;
                case Constants.MSG_WHAT_REGISTER_MESSENGER:
                    if (!mSendMessenger.contains(msg.replyTo)) {
                        mSendMessenger.add(msg.replyTo);
                    }
                    break;
                case Constants.MSG_WHAT_UNREGISTER_MESSENGER:
                    mSendMessenger.remove(msg.replyTo);
                    break;
                case Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST:
                case Constants.MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST:
                case Constants.MSG_WHAT_LOGIN_REGISTRATION_REQUEST:
                case Constants.MSG_WHAT_LOGIN_CERTIFY_REQUEST:
                case Constants.MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION:
                case Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST:
                case Constants.MSG_WHAT_MAIN_DISPLAY_REQUEST:
                case Constants.MSG_WHAT_MAIN_INFORMATION_REQUEST:
                case Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST:
                case Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST:
                case Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST:
                case Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST:
                case Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST:
                case Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST:
                case Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST:
                case Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST:
                case Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_RESERVATION_REQUEST:
                case Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST:
                case Constants.MSG_WHAT_INFO_NOTICE_REQUEST:
                case Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST:
                case Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST:
                case Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST:
                case Constants.MSG_WHAT_LOGIN_LOGOUT:
                case Constants.MSG_WHAT_LOGIN_WITHDRAWAL:
                case Constants.MSG_WHAT_LOGIN_VERSION_REQUEST:
                case Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST:
                case Constants.MSG_WHAT_LOGIN_FIND_ID:
                case Constants.MSG_WHAT_INFO_ENERGY_REQUEST:
                case Constants.MSG_WHAT_CONTROL_BREAKER_REQUEST:
                case Constants.MSG_WHAT_CONTROL_BREAKER_EACH:
                case Constants.MSG_WHAT_CONTROL_BREAKER_GROUP:
                case Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST:
                case Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST:
                case Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST:
                case Constants.MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO:
                    TcpSendReceive(msg);
                    break;
                case Constants.MSG_WHAT_TIMER_START:
                    mTimeCnt = 0;
                    TimeHandlerService(true, TIME_SERVICE_CYCLE);
                    break;
                case Constants.MSG_WHAT_TIMER_END:
                    TimeHandlerService(false, TIME_SERVICE_NULL);
                    break;
                default:
                    break;
            }
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif timer service
     * @param tState
     * @param tTime
     */
    private void TimeHandlerService(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null) {
                mTimeHandler = new Handler();
            }
                mTimeHandler.postDelayed(delayService, tTime);
        }else{
            mTimeHandler = null;
        }
    }
    //**********************************************************************************************


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFlag = true;
        Log.e("HomeTokService","Destroy!!!!!!!!!!!!!!!!!!!");
    }


    private Runnable delayService = new Runnable() {
        @Override
        public void run() {

                if(!stopFlag) {
                    if (mTimeHandler != null) {
                        mTimeCnt++;
//                        Log.e("HomeTok Service", String.valueOf(mTimeCnt));
                        mTimeHandler.postDelayed(delayService, TIME_SERVICE_CYCLE);

                        if (mTimeCnt >= TIME_SERVICE_MAX) {
                            mTimeCnt = 0;

                            Intent broadcast = new Intent(Constants.ACTION_APP_OP_TIMEOUT);
                            sendBroadcast(broadcast);
                        }

                    }

                }


            }


//        }
    };

    //**********************************************************************************************
    /**
     * @param tMsg
     * @breif tcp data receive send task
     */
    private void TcpSendReceive(Message tMsg) {
        Bundle tMsgData = tMsg.getData();
        if ((mSocket != null) && (mSocket.isConnected())) {
            SendMSG(tMsg.replyTo, tMsg.what, null);
            return;
        }
        KDData tKDData;
        tKDData = KDData.KDDataParser(tMsgData);
        new TCPSendTask().execute(tKDData);
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif tcp asynctask tcp send receive
     */
    private class TCPSendTask extends AsyncTask<KDData, Void, Void> {
        @Override
        protected Void doInBackground(KDData... param) {
            String tSendData;
            try {
                // 서버와 통신할 Socket 객체 생성. 파라메터 : 서버IP, port
                if (mSocket == null) {
                    if(param[0].WHAT != Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST) {
                        LocalConfig tLocalConfig = new LocalConfig(getBaseContext());
//                        SocketAddress socketAddress = new InetSocketAddress(tLocalConfig.getStringValue(Constants.SAVE_DATA_LOCAL_IP), tLocalConfig.getIntValue(Constants.SAVE_DATA_LOCAL_PORT));
                        SocketAddress socketAddress = new InetSocketAddress(tLocalConfig.getStringValue(Constants.SAVE_DATA_LOCAL_IP), tLocalConfig.getIntValue(Constants.SAVE_DATA_LOCAL_PORT));
                        mSocket = new Socket(tLocalConfig.getStringValue(Constants.SAVE_DATA_LOCAL_IP), tLocalConfig.getIntValue(Constants.SAVE_DATA_LOCAL_PORT));
                        if (!mSocket.isConnected()){
                            mSocket.connect(socketAddress, 3000);
                        }
                    }else{
                        Log.e("HomeTok Service", "Server local complex ip ok");
                        SocketAddress socketAddress = new InetSocketAddress(Constants.SERVER_LOCAL_COMPLEX_IP, Constants.SERVER_LOCAL_COMPLEX_PORT);
                        mSocket = new Socket();
                        mSocket.connect(socketAddress, 3000);
                    }
                }

                switch (param[0].WHAT) {
                    case Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST:
                        tSendData = KDComplex.KDComplex();
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST:
                        tSendData = KDLogin.KDIDDuplication(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_REGISTRATION_REQUEST:
                        tSendData = KDLogin.KDRegistration(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_CERTIFY_REQUEST:
                        tSendData = KDLogin.KDCertify(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION:
                        tSendData = KDLogin.KDAuthentication(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST:
                        tSendData = KDLogin.KDLogin(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service1", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_MAIN_DISPLAY_REQUEST:
                        tSendData = KDMain.KDMainDisplayRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_MAIN_INFORMATION_REQUEST:
                        tSendData = KDMain.KDMainInformationRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST:
                        tSendData = KDLight.LightStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST:
                        tSendData = KDLight.LightEachRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST:
                        tSendData = KDLight.LightGroupRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST:
                        tSendData = KDGas.GasStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST:
                        tSendData = KDGas.GasEachRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST:
                        tSendData = KDGas.GasGroupRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST:
                        tSendData = KDSecurity.SecurityStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST:
                        tSendData = KDSecurity.SecurityOutStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST:
                        tSendData = KDStandbypower.StandbypowerStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST:
                        tSendData = KDStandbypower.StandbypowerEachRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST:
                        tSendData = KDStandbypower.StandbypowerGroupRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST:
                        tSendData = KDVentilation.VentilationStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST:
                        tSendData = KDVentilation.VentilationEachRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST:
                        tSendData = KDVentilation.VentilationGroupRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST:
                        tSendData = KDHeat.HeatStateRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST:
                        tSendData = KDHeat.HeatEachHeatRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST:
                        tSendData = KDHeat.HeatGroupHeatRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST:
                        tSendData = KDHeat.HeatEachHotwaterRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST:
                        tSendData = KDHeat.HeatEachModeRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_RESERVATION_REQUEST:
                        tSendData = KDHeat.HeatEachReservationRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST:
                        tSendData = KDHeat.HeatEachTempRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_INFO_NOTICE_REQUEST:
                        tSendData = KDNotice.KDKDNoticeRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST:
                        tSendData = KDVisitor.VisitListRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST:
                        tSendData = KDVisitor.VisitVideoRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST:
                        tSendData = KDVisitor.VisitConfirmRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_LOGOUT:
                        tSendData = KDLogout.Logout(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_WITHDRAWAL:
                        tSendData = KDWithdrawal.Withdrawal(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_VERSION_REQUEST:
                        tSendData = KDVersion.Version();
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST:
                        tSendData = KDMember.KDMember(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_FIND_ID:
                        tSendData = KDLogin.KDFindID(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_INFO_ENERGY_REQUEST:
                        tSendData = KDEms.KDEms(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_BREAKER_REQUEST:
                        tSendData = KDBchBreaker.BreakerRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_BREAKER_EACH:
                        tSendData = KDBchBreaker.BreakerEach(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_CONTROL_BREAKER_GROUP:
                        tSendData = KDBchBreaker.BreakerGroup(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO:
                        tSendData = KDLogin.KDDeviceInfomationRequest(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST:
                        tSendData = KDDoorOpen.KDDoorOpen(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST:
                        tSendData = KDPushSetting.KDPushSettingChange(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                        Log.e("HomeTok Service", "SendData : "+tSendData);
                        break;
                    case Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST:
                        tSendData = KDPushSetting.KDPushSettingLookUp(param[0]);
                        TcpSendData(tSendData, param[0].WHAT);
                    default:
                        break;
                }

                try {
                    if (mSocket != null) {
                        InputStream tInputStream = null;
                        byte[] tReceiveByte;
                        byte[] tReceiveContentsByte;
                        byte[] tLengthByte;
                        int tContentsLength = 0;
                        String tContentsString = null;

                        mInputStreamReader = new InputStreamReader(mSocket.getInputStream(), "UTF-8");
                        tInputStream = mSocket.getInputStream();
                        tReceiveByte = new byte[100];
                        tInputStream.read(tReceiveByte, 0, 100);
                        tLengthByte = new byte[4];
                        System.arraycopy(tReceiveByte, 4, tLengthByte, 0, 4);

                        tContentsLength = Integer.parseInt(KDUtil.bytesToHex(tLengthByte), 16) - 100;
                        if (tContentsLength > 0) {
                            tReceiveContentsByte = new byte[tContentsLength];

                            tReceiveContentsByte = receive(tContentsLength);
                            //tInputStream.read(tReceiveContentsByte, 0, tContentsLength);
                            tContentsString = KDUtil.cnvBytesToString(tReceiveContentsByte);

                            Log.e("service1", tContentsString);

                            if (!tContentsString.startsWith("<HNML>") && !tContentsString.endsWith("</HNML>")) {
                                tContentsString = "";
                            }

                            HNMLDataParser(tContentsString);
                        }
                        disConnectTcpClient();
                    } else {
                        throw new IOException("");
                    }

                } catch (SocketTimeoutException e) {
                    // read timeout 발생
                    Intent broadcast = new Intent(Constants.ACTION_APP_TIMEOUT);
                    sendBroadcast(broadcast);
                    disConnectTcpClient();
                    Log.e("HomeTok Service", "SocketTimeout Exception "+e.getMessage());
                } catch (UnsupportedEncodingException e) {
                    // 지원하지 않는 인코딩 방식
                    Intent broadcast = new Intent(Constants.ACTION_APP_NETWORK_ERROR);
                    sendBroadcast(broadcast);
                    disConnectTcpClient();
                    Log.e("HomeTok Service", "UnsupportedEncoding Exception "+e.getMessage());
                } catch (IOException e) {
                    // 기타 오류
                    Intent broadcast = new Intent(Constants.ACTION_APP_NETWORK_ERROR);
                    sendBroadcast(broadcast);
                    disConnectTcpClient();
                    Log.e("HomeTok Service", "IOException "+e.getMessage());
                }
                this.cancel(true);
            } catch (Exception e) {
                Intent broadcast = new Intent(Constants.ACTION_APP_SERVER_CONNECT_ERROR);
                sendBroadcast(broadcast);
                disConnectTcpClient();
                e.printStackTrace();
                Log.e("HomeTok Service", "doInBackground exception"+e.getMessage());
            }
            return null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif tcp data send
     * @param tData
     * @param tMsg
     */
    private void TcpSendData(String tData, int tMsg){
        byte[] tHeaderByte;
        byte[] tSendByte;
        byte[] tContentsByte;

        String tDong    = "0000";
        String tHo      = "0000";
        LocalConfig tLocalConfig = new LocalConfig(getBaseContext());
        KDHeader tHeader = new KDHeader();

        tDong   = tLocalConfig.getStringValue(Constants.SAVE_DATA_DONG);
        tHo     = tLocalConfig.getStringValue(Constants.SAVE_DATA_HO);

        if(tDong.length() != 4){
            tDong = "0000";
        }

        if(tHo.length() != 4){
            tHo = "0000";
        }

        tHeader.SOURCE_ID = "0000" + tDong + tHo + "00" + "120100";

        try {
            tContentsByte = tData.getBytes();
            tHeaderByte = tHeader.setHeaderArray(tContentsByte.length);
            tSendByte = KDUtil.byteAppend(tHeaderByte, tContentsByte);

            DataOutputStream tDataStream = new DataOutputStream(mSocket.getOutputStream());
            tDataStream.write(tSendByte);
            tDataStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif disconnect tcp client
     */
    private void disConnectTcpClient(){
        try {
            if(mSocket != null){
                mSocket.close();
                mSocket = null;
            }

            if(mInputStreamReader != null) {
                mInputStreamReader.close();
            }
            Log.e("HomeTok Service", "disconnect tcp client success");
        }catch (IOException e){
            e.printStackTrace();
            Log.e("HomeTok Service", "disconnect tcp client exception"+e.getMessage());
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @param rcvLen
     * @return
     */
    private byte[] receive(int rcvLen) {
        InputStream dis = null;

        try {
            int offset = 0;
            int wanted = rcvLen;
            int len = 0;
            int totlen = 0;

            byte[] buffer = new byte[rcvLen];

            if(null!=mSocket.getInputStream()){
            dis = mSocket.getInputStream();}

            while (wanted > 0) {
                len = dis.read(buffer, offset, wanted);
                if (len == -1) {
                    System.out.println("RsSocket: readMsg common header read");
                }

                wanted -= len;
                offset += len;
                totlen += len;
            }

            byte[] header = new byte[totlen];
            System.arraycopy(buffer, 0, header, 0, totlen);

            return header;
        } catch (Exception e) {
            System.out.println("RsSocket: readMsg Exception common header read");
            e.printStackTrace();
            return new byte[0];
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @param tSendMessenger
     * @param pWhat
     * @param pMessage
     * @breif hometok message send function
     */
    private void SendMSG(Messenger tSendMessenger, int pWhat, Object pMessage) {
        Message sendMsg = Message.obtain();
        sendMsg.what = pWhat;
        sendMsg.obj = pMessage;

        try {
            tSendMessenger.send(sendMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("HomeTok Service", "send message exception" + e.getMessage());
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif tcp receive data hnml basic parser
     * @param tContents
     */
    private void HNMLDataParser(String tContents){
        int mMessageData = 0;
        ArrayList<String> mStartTag = new ArrayList<>();
        ArrayList<String> mTextTag = new ArrayList<>();

        //**************************************************************************
        if (tContents != null) {
            String tData;
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
                            if(name.equals(Constants.HNML_RESULT)){
                                mStartTag.add(name);
                            }else if(name.equals(Constants.HNML_FUNCTION_ID)){
                                mStartTag.add(name);
                            }else if(name.equals(Constants.HNML_EXCEPTION)){
                                mStartTag.add(name);
                            }else if(name.equals("Data")){
                                if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_COMPLEX)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_DONG)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_HO)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_NAME)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_LEVEL_CODE)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_PHONE_NUMBER)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_USER_PAD_CERTIFY)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else if(tParser.getAttributeValue(null, "name").equals(Constants.HNML_DEVICE_TYPE)){
                                    mStartTag.add(tParser.getAttributeValue("", "name"));
                                }else{
                                    tName = "";
                                }
                            }else{
                                tName = "";
                            }
                            break;
                        case XmlPullParser.TEXT:
                            if (tName != "" && tParser.getText() != "") {
                                tName = "";
                                mTextTag.add(tParser.getText());
                            }
                            break;
                        default:
                            break;
                    }
                    tEventType = tParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("HomeTok Service", "HNML Parser data ok");
            try {
                for (int i = 0; i < mStartTag.size(); i++) {
                    if (mStartTag.get(i).equals(Constants.HNML_FUNCTION_ID)) {
                        tData = mTextTag.get(i);
                        mMessageData = MessageCmdConversion(tData);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HomeTok Service", "HNML parser data function id exception"+e.getMessage());
            }

            HNMLDataMessageParser(mMessageData, mStartTag, mTextTag, tContents);
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif parser tcp basic data
     * @param tMessage
     * @param tName
     * @param tContents
     * @param tString
     */
    private void HNMLDataMessageParser(int tMessage, List<String> tName, List<String> tContents, String tString){
        try{
            KDData tKDData = new KDData();
            tKDData.ReceiveString = tString;

            for (int i = 0; i < tName.size(); i++) {
                if (tName.get(i).equals(Constants.HNML_RESULT)) {
                    tKDData.Result = tContents.get(i);
                    if (tKDData.Result.equals(Constants.HNML_RESULT_CERTIFY_CHAR_ERROR) ||
                            tKDData.Result.equals(Constants.HNML_RESULT_CERTIFY_ERROR)) {
                        disConnectTcpClient();
                    }
                } else if (tName.get(i).equals(Constants.HNML_EXCEPTION)) {
                    tKDData.Exception = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_COMPLEX)) {
                    tKDData.Complex = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_DONG)) {
                    tKDData.Dong = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_HO)) {
                    tKDData.Ho = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_NAME)) {
                    tKDData.Name = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_LEVEL_CODE)) {
                    tKDData.LevelCode = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_PHONE_NUMBER)) {
                    tKDData.SmartPhone = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_PHONE_CERTIFY)) {
                    tKDData.PhoneCertify = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_USER_PAD_CERTIFY)) {
                    tKDData.PadCertify = tContents.get(i);
                } else if (tName.get(i).equals(Constants.HNML_DEVICE_TYPE)) {
                    tKDData.DeviceType = tContents.get(i);
                }
            }

            for (int i = mSendMessenger.size() - 1; i >= 0; i--) {
                SendMSG(mSendMessenger.get(i), tMessage, tKDData);
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.e("HomeTok Service", "HNML Data message parser exception"+e.getMessage());
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif tcp receive data command conversion string -> integer
     * @param tData
     * @return
     */
    private int MessageCmdConversion(String tData){
        if(tData.equals(Constants.MSG_STRING_LOGIN_COMPLEX_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_COMPLEX_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_ID_DUPLICATION_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_REGISTRATION_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_REGISTRATION_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_CERTIFY_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_CERTIFY_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_CERTIFY_AUTHENTICATION)){
            return Constants.MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_LOGIN_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_LOGIN_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_MAIN_DISPLAY_REQUEST)){
            return Constants.MSG_WHAT_MAIN_DISPLAY_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_MAIN_INFORMATION_REQUEST)){
            return Constants.MSG_WHAT_MAIN_INFORMATION_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_LIGHT_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_LIGHT_EACH_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_LIGHT_GROUP_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_GAS_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_GAS_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_GAS_EACH_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_GAS_EACH_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_GAS_GROUP_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_GAS_GROUP_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_SECURITY_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_SECURITY_OUT_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_POWER_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_POWER_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_POWER_EACH_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_POWER_EACH_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_POWER_GROUP_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_POWER_GROUP_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_VENTILATION_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_VENTILATION_EACH_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_VENTILATION_GROUP_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_STATE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_STATE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_EACH_HEAT_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_GROUP_HEAT_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_HOTWATER_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_MODE_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_MODE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_RESERVATION_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_RESERVATION_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_HEAT_TEMP_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_INFO_NOTICE_REQUEST)){
            return Constants.MSG_WHAT_INFO_NOTICE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_INFO_VISIT_LIST_REQUEST)){
            return Constants.MSG_WHAT_INFO_VISIT_LIST_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_INFO_VISIT_VIDEO_REQUEST)){
            return Constants.MSG_WHAT_INFO_VISIT_VIDEO_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_INFO_VISIT_VIDEO_CONFIRM_REQUEST)){
            return Constants.MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_LOGOUT)){
            return Constants.MSG_WHAT_LOGIN_LOGOUT;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_WITHDRAWAL)){
            return Constants.MSG_WHAT_LOGIN_WITHDRAWAL;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_VERSION_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_VERSION_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_INFO_CHANGE_REQUEST)){
            return Constants.MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_FIND_ID)){
            return Constants.MSG_WHAT_LOGIN_FIND_ID;
        } else if(tData.equals(Constants.MSG_STRING_INFO_ENERGY_REQUEST)){
            return Constants.MSG_WHAT_INFO_ENERGY_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_BREAKER_REQUEST)){
            return Constants.MSG_WHAT_CONTROL_BREAKER_REQUEST;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_BREAKER_EACH)){
            return Constants.MSG_WHAT_CONTROL_BREAKER_EACH;
        } else if(tData.equals(Constants.MSG_STRING_CONTROL_BREAKER_GROUP)){
            return Constants.MSG_WHAT_CONTROL_BREAKER_GROUP;
        } else if(tData.equals(Constants.MSG_STRING_LOGIN_DEVICE_INFO)){
            return Constants.MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO;
        }else if (tData.equals(Constants.MSG_STRING_PUSH_OPEN_DOOR_REQUEST)){
            return Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST;
        }else if (tData.equals(Constants.MSG_STRING_PUSH_SETTING_CHANGE_SAVE_REQUEST)){
            return Constants.MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST;
        }else if (tData.equals(Constants.MSG_STRING_PUSH_SETTING_CHANGE_STATE_REQUEST)){
            return Constants.MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST;
        }
        return 0;
    }
    //**********************************************************************************************
}
