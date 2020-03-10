package com.kd.One.Common;

import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by lwg on 2016-07-10.
 */
public class KDData {
    public int                  WHAT                 = 0;
    public String               FunctionID           = null;
    public String               ID                   = null;
    public String               Password             = null;
    public String               Name                 = null;
    public String               SmartPhone           = null;
    public String               Dong                 = null;
    public String               EndDate              = null;
    public String               Ho                   = null;
    public String               PadCertify           = null;
    public String               AppCertify           = null;
    public String               Result               = null;
    public String               Exception            = null;
    public String               ReceiveString        = null;
    public String               size       = null;

    public String               DeviceType           = null;
    public String               DeviceName           = null;
    public String               GroupID              = null;
    public String               SubID                = null;
    public String               OnOff                = null;
    public String               DimmingLevel         = null;

    public String               VideoID              = null;
    public String               VideoState           = null;
    public String               VideoDeleteState     = null;
    public String               VideoDeleteNum       = null;
    public ArrayList<String>    VideoDeleteID;

    public String               Complex              = null;
    public String               LevelCode            = null;
    public String               ResponseCode         = null;
    public String               PhoneCertify         = null;

    public String               IPAddr               = null;
    public int                  Port                 = 0;
    public String               SoftwareVer          = null;
    public String               MacAdder             = null;
    public String               ModelNum             = null;
    public String               UserPassword         = "0000";
    public String               LobbyPassword        = "0000";
    public String               RemotePassword       = "0000";

    public String               AutoBlockSetup       = null;
    public String               AutoBlock            = null;
    public String               PowerState           = null;

    public String               ElevatorDirection    = null;

    public String               Heating              = null;
    public String               TargetTemp           = null;
    public String               Mode                 = null;
    public String               Reservation          = null;
    public String               HotWater             = null;

    public String               VentilationEachState = null;
    public String               VentilationPower     = null;
    public String               VentilationWind      = null;
    public String               VentilationMode      = null;

    public String               SecuriteState        = null;
    public String               OutState             = null;
    public String               OutTime              = null;
    public String               InTime               = null;
    public String               AreaMagnet           = null;
    public String               AreaMoving           = null;
    public String               OutGas               = null;
    public String               OutLight             = null;
    public String               OutRoomCon           = null;
    public String               OutStanbyPower       = null;

    public String               EnergyStartTime      = null;
    public String               EnergyEndTime        = null;
    public String               EnergyCategoryType   = null;
    public String               EnergyQueryType      = null;
    public String               EnergyAmount         = null;

    public String               HomeID               = null;

    public String               ListNum              = null;

    public String               BreakerLight         = null;
    public String               BreakerGas           = null;

    public String               DeviceInfoID         = null;
    public String               DeviceInfoType       = null;
    public String               DeviceInfoOS         = null;
    public String               DeviceInfoOSVer      = null;
    public String               DeviceInfoAppVer     = null;
    public String               DeviceInfoModel      = null;
    public String               DeviceInfoPushType   = null;
    public String               DeviceInfoPushKey    = null;

    public String               PushType             = null;
    public String               PushPassword         = null;

    public String               PushEmergency        = null;
    public String               PushCarInOut        = null;
    public String               PushDelivery        = null;
    public String               PushSip        = null;

    public static String ReturnKDTransID(){
        String tReturn = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currenttime = sdf.format(new Date());

        tReturn = "APP";
        tReturn += currenttime.trim();
        tReturn  = tReturn.replace(" ", "");
        tReturn  = tReturn.replace("  ", "");

        return tReturn;
    }

    public static String ReturnKDDate(){
        String tReturn = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String currenttime = sdf.format(new Date());

        tReturn = currenttime.trim();
        tReturn = tReturn.replace(" ", "");
        tReturn = tReturn.replace("  ", "");

        return tReturn;
    }

    //**********************************************************************************************
    /**
     * @param tMsgData
     * @return
     * @breif message receive kd data parsing
     */
    public static KDData KDDataParser(Bundle tMsgData) {
        KDData tKDData = new KDData();
        tKDData.WHAT                    = tMsgData.getInt(Constants.KD_DATA_WHAT);
        tKDData.FunctionID              = tMsgData.getString(Constants.KD_DATA_FUNCTION_ID);
        tKDData.ID                      = tMsgData.getString(Constants.KD_DATA_ID);
        tKDData.Result                  = tMsgData.getString(Constants.KD_DATA_RESULT);
        tKDData.Password                = tMsgData.getString(Constants.KD_DATA_PW);
        tKDData.Name                    = tMsgData.getString(Constants.KD_DATA_NAME);
        tKDData.SmartPhone              = tMsgData.getString(Constants.KD_DATA_CELLPHONENUM);
        tKDData.Dong                    = tMsgData.getString(Constants.KD_DATA_DONG);
        tKDData.Ho                      = tMsgData.getString(Constants.KD_DATA_HO);
        tKDData.EndDate                 = tMsgData.getString(Constants.KD_DATA_END_DATE);
        tKDData.AppCertify              = tMsgData.getString(Constants.KD_DATA_CERTIFY);
        tKDData.GroupID                 = tMsgData.getString(Constants.KD_DATA_GROUP_ID);
        tKDData.SubID                   = tMsgData.getString(Constants.KD_DATA_SUB_ID);
        tKDData.OnOff                   = tMsgData.getString(Constants.KD_DATA_ONOFF);
        tKDData.DimmingLevel            = tMsgData.getString(Constants.KD_DATA_DIMMINGLEVEL);
        tKDData.SecuriteState           = tMsgData.getString(Constants.KD_DATA_SECURITY_STATE);
        tKDData.AutoBlock               = tMsgData.getString(Constants.KD_DATA_AUTOBLOCK);
        tKDData.AutoBlockSetup          = tMsgData.getString(Constants.KD_DATA_AUTOBLOCKSETTING);
        tKDData.PowerState              = tMsgData.getString(Constants.KD_DATA_STANDBYPOWER_STATE);
        tKDData.VentilationEachState    = tMsgData.getString(Constants.KD_DATA_VENTILATION_EACH_STATE);
        tKDData.VentilationMode         = tMsgData.getString(Constants.KD_DATA_VENTILATION_MODE);
        tKDData.VentilationWind         = tMsgData.getString(Constants.KD_DATA_VENTILATION_WIND);
        tKDData.VentilationPower        = tMsgData.getString(Constants.KD_DATA_VENTILATION_POWER);
        tKDData.Heating                 = tMsgData.getString(Constants.KD_DATA_HEATING);
        tKDData.HotWater                = tMsgData.getString(Constants.KD_DATA_HOTWATER);
        tKDData.Heating                 = tMsgData.getString(Constants.KD_DATA_HEATING);
        tKDData.Reservation             = tMsgData.getString(Constants.KD_DATA_RESERVATION);
        tKDData.Mode                    = tMsgData.getString(Constants.KD_DATA_MODE);
        tKDData.TargetTemp              = tMsgData.getString(Constants.KD_DATA_TEMP);
        tKDData.VideoID                 = tMsgData.getString(Constants.KD_DATA_VIDEO_ID);
        tKDData.VideoState              = tMsgData.getString(Constants.KD_DATA_VIDEO_STATE);
        tKDData.ListNum                 = tMsgData.getString(Constants.KD_DATA_LIST_NUM);
        tKDData.HomeID                  = tMsgData.getString(Constants.KD_DATA_HOME_ID);
        tKDData.EnergyStartTime         = tMsgData.getString(Constants.KD_DATA_ENERGY_START_TIME);
        tKDData.EnergyEndTime           = tMsgData.getString(Constants.KD_DATA_ENERGY_END_TIME);
        tKDData.EnergyCategoryType      = tMsgData.getString(Constants.KD_DATA_ENERGY_CATEGORY_TYPE);
        tKDData.EnergyQueryType         = tMsgData.getString(Constants.KD_DATA_ENERGY_QUERY_TYPE);
        tKDData.BreakerLight            = tMsgData.getString(Constants.KD_DATA_BREAKER_LIGHT);
        tKDData.BreakerGas              = tMsgData.getString(Constants.KD_DATA_BREAKER_GAS);

        tKDData.DeviceInfoID            = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_ID);
        tKDData.DeviceInfoType          = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_TYPE);
        tKDData.DeviceInfoOS            = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_OS);
        tKDData.DeviceInfoOSVer         = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_OSVER);
        tKDData.DeviceInfoAppVer        = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_APPVER);
        tKDData.DeviceInfoModel         = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_MODEL);
        tKDData.DeviceInfoPushType      = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_PUSH_TYPE);
        tKDData.DeviceInfoPushKey       = tMsgData.getString(Constants.KD_DATA_DEVICE_INFO_PUSH_KEY);

        tKDData.PushType                = tMsgData.getString(Constants.KD_DATA_PUSH_TYPE);
        tKDData.PushPassword            = tMsgData.getString(Constants.KD_DATA_PUSH_PASSWORD);

        tKDData.PushEmergency           = tMsgData.getString(Constants.KD_DATA_PUSH_SETTING_EMERGENCY);
        tKDData.PushCarInOut           = tMsgData.getString(Constants.KD_DATA_PUSH_SETTING_CAR_INOUT);
        tKDData.PushDelivery          = tMsgData.getString(Constants.KD_DATA_PUSH_SETTING_DELIVERY);
        tKDData.PushSip          = tMsgData.getString(Constants.KD_DATA_PUSH_SETTING_SIP);
        return tKDData;
    }
    //**********************************************************************************************
}
