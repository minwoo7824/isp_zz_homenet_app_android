package com.kd.One.Common;

/**
 * Created by lwg on 2016-07-07.
 */
public class Constants {

    /*
     * broadcast
     */
    public static final String ACTION_APP_FINISH = "com.n_Cladle.appfinish";
    public static final String ACTION_APP_MAINRETURN = "com.One.appmainreturn";
    public static final String ACTION_APP_GATEWAYRETURN = "com.One.appgatewayreturn";
    public static final String ACTION_APP_TIMEOUT = "com.n_Cladle.apptimeout";	//New : 10분 타임아웃
    public static final String ACTION_APP_ERRORAPPEAR = "com.n_FlatStation.apperrorappear";	//New : 에러발생
    public static final String ACTION_APP_DIALOGCLOSE = "com.One.appdialogclose";
    public static final String ACTION_APP_SUBDIALOGCLOSE = "com.One.appdialogclose.sub";
    public static final String ACTION_APP_SUBPAGEFINISH = "com.One.appsubpagefinish";
    public static final String ACTION_APP_CONTROLPAGERETURN = "com.One.appcontrolpagereturn";
    public static final String ACTION_APP_WIFISUCCESS = "com.One.wifisuccess";	//공유기 설정 완료(회원가입)
    public static final String ACTION_APP_WIFICONNECT = "com.One.wificonnect";	//공유기 설정 완료(공유기설정)
    public static final String ACTION_APP_WIFIFAIL = "com.One.wififail";	//공유기 설정 실패
    public static final String ACTION_APP_WIFIRN131 = "com.One.wifirn131";	//rn131 모듈
    public static final String ACTION_APP_PUSH_CALL	= "com.One.pushcall";	//Push Popup->Homeview
    public static final String ACTION_APP_PUSH_CLOSE = "com.One.pushclose";	//Push 알림 해제(통화)

    public static final String ACTION_APP_GET_SAMSUNG = "com.One.samsung";	//삼성에어컨
    public static final String ACTION_APP_DEL_SAMSUNG = "com.One.samsung.del";	//

    //
    public static final String ACTION_APP_HOMEVIEW_END = "com.One.homeviewclose";
    public static final String ACTION_APP_REFRESHSTATE = "com.One.apprefreshstate";

    //**********************************************************************************************
    /**
     * @breif Tab Name
     */
    public static final String TAB_CONTROL  = "control";
    public static final String TAB_INFO     = "info";
    public static final String TAB_SETTING  = "setting";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief broadcast constants
     */
    public static final String  ACTION_APP_NETWORK_ERROR                        = "com.n_Cladle.appgatewayreturn";
    public static final String  ACTION_APP_SOCKET_CLOSE                         = "Socket is closed";
    public static final String  ACTION_APP_SERVER_CONNECT_ERROR                 = "com.n_Cladle.serverconnecterror";
    public static final String  ACTION_APP_OP_TIMEOUT                           = "com.n_Cladle.appoperatingtimeout";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif save data constants
     */
    public static final String  SAVE_DATA_AUTO_ID                               = "SAVE_AUTO_ID";
    public static final String  SAVE_DATA_ID                                    = "SAVE_ID";
    public static final String  SAVE_DATA_PW                                    = "SAVE_PW";
    public static final String  SAVE_DATA_COMPLEX_CHECK                         = "SAVE_COMPLEX_CHECK";
    public static final String  SAVE_DATA_LOCAL_IP                              = "SAVE_LOCAL_IP";
    public static final String  SAVE_DATA_LOCAL_PORT                            = "SAVE_LOCAL_PORT";
    public static final String  SAVE_DATA_CERTIFY                               = "SAVE_CERTIFY";
    public static final String  SAVE_DATA_DONG                                  = "SAVE_DONG";
    public static final String  SAVE_DATA_HO                                    = "SAVE_HO";
    public static final String  SAVE_DATA_LOGIN_STATUS                          = "SAVE_LOGIN_STATUS";
    public static final String  SAVE_DATA_PHONE_NUM                             = "SAVE_PHONE_NUM";
    public static final String  SAVE_DATA_NAME                                  = "SAVE_NAME";
    public static final String  SAVE_DATA_CERTIFY_ID                            = "SAVE_CERTIFY_ID";
    public static final String  SAVE_DATA_CERTIFY_PW                            = "SAVE_CERTIFY_PW";
    public static final String  SAVE_DATA_COMPLEX_NAME                          = "SAVE_COMPLEX_NAME";
    public static final String  SAVE_DATA_TOKEN                                 = "SAVE_TOKEN";
    public static final String  SAVE_DATA_NABLE_USE                             = "SAVE_NABLE_USE";
    public static final String  SAVE_DATA_NABLE_LOCAL_CALL_IP                   = "SAVE_NABLE_LOCAL_CALL_IP";
    public static final String  SAVE_DATA_NABLE_LOCAL_CALL_PORT                 = "SAVE_NABLE_LOCAL_CALL_PORT";
    public static final String  SAVE_DATA_NABLE_LOCAL_STUN_IP                   = "SAVE_NABLE_LOCAL_STUN_IP";
    public static final String  SAVE_DATA_NABLE_LOCAL_STUN_PORT                 = "SAVE_NABLE_LOCAL_STUN_PORT";
    public static final String  SAVE_DATA_NABLE_CALL_IP                         = "SAVE_NABLE_CALL_IP";
    public static final String  SAVE_DATA_NABLE_CALL_PORT                       = "SAVE_NABLE_CALL_PORT";
    public static final String  SAVE_DATA_NABLE_STUN_IP                         = "SAVE_NABLE_STUN_IP";
    public static final String  SAVE_DATA_NABLE_STUN_PORT                       = "SAVE_NABLE_STUN_PORT";
    public static final String  SAVE_DATA_NABLE_DOMAIN                          = "SAVE_NABLE_DOMAIN";
    public static final String  SAVE_DATA_NABLE_PASSWORD                        = "SAVE_NABLE_PASSWORD";
    public static final String  SAVE_DATA_USE_PUSH                              = "SAVE_USE_PUSH";
    public static final String  SAVE_DATA_PUBLIC_IP                             = "SAVE_PUBLIC_IP";

    public static final String  SAVE_DATA_EMS_ELECTRICITY_USE                   = "SAVE_EMS_ELECTRICITY_USE";
    public static final String  SAVE_DATA_EMS_GAS_USE                           = "SAVE_EMS_GAS_USE";
    public static final String  SAVE_DATA_EMS_WATER_USE                         = "SAVE_EMS_WATER_USE";
    public static final String  SAVE_DATA_EMS_HOTWATER_USE                      = "SAVE_EMS_HOTWATER_USE";
    public static final String  SAVE_DATA_EMS_HEATING_USE                       = "SAVE_EMS_HEATING_USE";
    public static final String  SAVE_DATA_EMS_COOLING_USE                       = "SAVE_EMS_COOLING_USE";

    public static final String  SAVE_DATA_PUSH_SETTING_EMERGENCY                = "SAVE_PUSH_SETTING_EMERGENCY";
    public static final String  SAVE_DATA_PUSH_SETTING_CAR_INOUT                = "SAVE_PUSH_SETTING_CAR_INOUT";
    public static final String  SAVE_DATA_PUSH_SETTING_DELIVERY                 = "SAVE_PUSH_SETTING_DELIVERY";
    public static final String  SAVE_DATA_PUSH_SETTING_SIP                      = "SAVE_PUSH_SETTING_SIP";
    //**********************************************************************************************

    /**
     * @breif intent data constants
     */
    public static final String SAVE_SATUS_DOING_REGISTER                        = "SAVE_STATUS_DOGIN_REGISTER";

    //**********************************************************************************************
    /**
     * @breif local server ip & port idc information
     */
    public static final String  SERVER_LOCAL_COMPLEX_IP                         = "krvd.naviensmartcontrol.com";
    public static final int     SERVER_LOCAL_COMPLEX_PORT                       = 8900;
//    public static final String  SERVER_LOCAL_COMPLEX_IP                         = "192.168.26.2";
//    public static final int     SERVER_LOCAL_COMPLEX_PORT                       = 8900;
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif local server ip & port complex //유과장님 local ip
     */
    public static final String  SERVER_LOCAL_IP                                 = "192.168.27.195";
    public static final int     SERVER_LOCAL_PORT                               = 8880;
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif server private ip & port complex
     */
    public static final String  SERVER_PRIVATE_IP                               = "10.0.0.1";
    public static final int     SERVER_PRIVATE_PORT                             = 8880;
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif kd data message filter define
     */
    public static final String  KD_DATA_WHAT                                    = "KD_WHAT";
    public static final String  KD_DATA_FUNCTION_ID                             = "KD_FUNCTION_ID";
    public static final String  KD_DATA_ID                                      = "KD_ID";
    public static final String  KD_DATA_PW                                      = "KD_PW";
    public static final String  KD_DATA_RESULT                                  = "KD_RESULT";
    public static final String  KD_DATA_NAME                                    = "KD_NAME";
    public static final String  KD_DATA_CELLPHONENUM                            = "KD_CELLPHONENUM";
    public static final String  KD_DATA_DONG                                    = "KD_DONG";
    public static final String  KD_DATA_HO                                      = "KD_HO";
    public static final String  KD_DATA_END_DATE                                = "KD_END_DATE";
    public static final String  KD_DATA_GROUP_ID                                = "KD_GROUP_ID";
    public static final String  KD_DATA_SUB_ID                                  = "KD_SUB_ID";
    public static final String  KD_DATA_CERTIFY                                 = "KD_CERTIFY";
    public static final String  KD_DATA_ONOFF                                   = "KD_ONOFF";
    public static final String  KD_DATA_DIMMINGLEVEL                            = "KD_DIMMINGLEVEL";
    public static final String  KD_DATA_SECURITY_STATE                          = "KD_SECURITY_STATE";
    public static final String  KD_DATA_STANDBYPOWER_STATE                      = "KD_STANDBYPOWER_STATE";
    public static final String  KD_DATA_AUTOBLOCKSETTING                        = "KD_AUTOBLOCKSETTING";
    public static final String  KD_DATA_AUTOBLOCK                               = "KD_AUTOBLOCK";
    public static final String  KD_DATA_VENTILATION_EACH_STATE                  = "KD_VENTILATION_EACH_STATE";
    public static final String  KD_DATA_VENTILATION_MODE                        = "KD_VENTILATION_MODE";
    public static final String  KD_DATA_VENTILATION_WIND                        = "KD_VENTILATION_WIND";
    public static final String  KD_DATA_VENTILATION_POWER                       = "KD_VENTILATION_POWER";
    public static final String  KD_DATA_HOTWATER                                = "KD_HOTWATER";
    public static final String  KD_DATA_RESERVATION                             = "KD_RESERVATION";
    public static final String  KD_DATA_MODE                                    = "KD_MODE";
    public static final String  KD_DATA_TEMP                                    = "KD_TEMP";
    public static final String  KD_DATA_HEATING                                 = "KD_HEATING";
    public static final String  KD_DATA_VIDEO_ID                                = "KD_VIDEO_ID";
    public static final String  KD_DATA_LIST_NUM                                = "KD_LIST_NUM";
    public static final String  KD_DATA_VIDEO_STATE                             = "KD_VIDEO_STATE";
    public static final String  KD_DATA_HOME_ID                                 = "KD_HOME_ID";
    public static final String  KD_DATA_ENERGY_QUERY_TYPE                       = "KD_ENERGY_QUERY_TIME";
    public static final String  KD_DATA_ENERGY_START_TIME                       = "KD_ENERGY_START_TIME";
    public static final String  KD_DATA_ENERGY_END_TIME                         = "KD_ENERGY_END_TIME";
    public static final String  KD_DATA_ENERGY_CATEGORY_TYPE                    = "KD_ENERGY_CATEGORY_TYPE";
    public static final String  KD_DATA_BREAKER_LIGHT                           = "KD_BREAKER_LIGHT";
    public static final String  KD_DATA_BREAKER_GAS                             = "KD_BREAKER_GAS";

    public static final String  KD_DATA_DEVICE_INFO_ID                          = "KD_DEVICE_INFO_ID";
    public static final String  KD_DATA_DEVICE_INFO_TYPE                        = "KD_DEVICE_INFO_TYPE";
    public static final String  KD_DATA_DEVICE_INFO_OS                          = "KD_DEVICE_INFO_OS";
    public static final String  KD_DATA_DEVICE_INFO_OSVER                       = "KD_DEVICE_INFO_OSVER";
    public static final String  KD_DATA_DEVICE_INFO_APPVER                      = "KD_DEVICE_INFO_APPVER";
    public static final String  KD_DATA_DEVICE_INFO_MODEL                       = "KD_DEVICE_INFO_MODEL";
    public static final String  KD_DATA_DEVICE_INFO_PUSH_TYPE                   = "KD_DEVICE_INFO_PUSH_TYPE";
    public static final String  KD_DATA_DEVICE_INFO_PUSH_KEY                    = "KD_DEVICE_INFO_PUSH_KEY";
    public static final String  KD_DATA_PUSH_TYPE                               = "KD_PUSH_TYPE";
    public static final String  KD_DATA_PUSH_PASSWORD                           = "KD_PUSH_PASSWORD";

    public static final String  KD_DATA_PUSH_SETTING_EMERGENCY                  = "KD_PUSH_SETTING_EMERGENCY";
    public static final String  KD_DATA_PUSH_SETTING_CAR_INOUT                  = "KD_PUSH_SETTING_CAR_INOUT ";
    public static final String  KD_DATA_PUSH_SETTING_DELIVERY                   = "KD_PUSH_SETTING_DELIVERY";
    public static final String  KD_DATA_PUSH_SETTING_SIP                        = "KD_PUSH_SETTING_SIP";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief HNML parser string
     */
    public static final String  HNML_FUNCTION_ID                                = "FunctionID";
    public static final String  HNML_FUNCTION_CATEGORY                          = "FunctionCategory";
    public static final String  HNML_RESULT                                     = "Result";
    public static final String  HNML_EXCEPTION                                  = "Exception";
    public static final String  HNML_USER_COMPLEX                               = "Complex";
    public static final String  HNML_USER_DONG                                  = "Dong";
    public static final String  HNML_USER_HO                                    = "Ho";
    public static final String  HNML_USER_NAME                                  = "Name";
    public static final String  HNML_USER_LEVEL_CODE                            = "LevelCode";
    public static final String  HNML_USER_PHONE_NUMBER                          = "Smartphone";
    public static final String  HNML_USER_PHONE_CERTIFY                         = "PhoneCertify";
    public static final String  HNML_USER_WEB_CERTIFY                           = "WebCertify";
    public static final String  HNML_USER_PAD_CERTIFY                           = "PadCertify";
    public static final String  HNML_DEVICE_TYPE                                = "DeviceType";
    //**********************************************************************************************

    public final static String  PUSH_CODE_DOOR_OCC		= "001-13021";
    public final static String  PUSH_CODE_LOBBY_OCC	    = "001-13041";
    public final static String  PUSH_CODE_GUARD_OCC		= "001-13011";
    //**********************************************************************************************
    /**
     * @breif result contents
     */
    public static final String  HNML_RESULT_ID_OK                               = "5005";
    public static final String  HNML_RESULT_ID_USED                             = "0000";
    public static final String  HNML_RESULT_ID_ERROR                            = "5001";
    public static final String  HNML_RESULT_PW_ERROR                            = "5002";
    public static final String  HNML_RESULT_CERTIFY_ERROR                       = "5003";
    public static final String  HNML_RESULT_CERTIFY_CHAR_ERROR                  = "5007";

    public static final String  HNML_RESULT_OK                                  = "0000";
    public static final String  HNML_NOTICE_FINAL_RESULT                        = "2001";
    public static final String  HNML_RESULT_HOMESERVER_CONNECTION_ERROR         = "1fff";
    public static final String  HNML_RESULT_DATABASE_UPDATE_ERROR               = "2002";
    public static final String  HNML_RESULT_COMMUNICATION_ERROR                 = "7777";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif data send string command number
     */
    public static final String  MSG_STRING_LOGIN_COMPLEX_REQUEST                = "00000000";

    public static final String  MSG_STRING_LOGIN_LOGIN_REQUEST                  = "51070000";
    public static final String  MSG_STRING_LOGIN_INFO_CHANGE_REQUEST            = "51070001";
    public static final String  MSG_STRING_LOGIN_REGISTRATION_REQUEST           = "51070008";
    public static final String  MSG_STRING_LOGIN_WITHDRAWAL                     = "51070009";
    public static final String  MSG_STRING_LOGIN_FIND_ID                        = "51070006";
    public static final String  MSG_STRING_LOGIN_ID_DUPLICATION_REQUEST         = "51070007";
    public static final String  MSG_STRING_LOGIN_VERSION_REQUEST                = "51070060";
    public static final String  MSG_STRING_LOGIN_CERTIFY_REQUEST                = "51070061";
    public static final String  MSG_STRING_LOGIN_CERTIFY_AUTHENTICATION         = "51070062";
    public static final String  MSG_STRING_LOGIN_LOGOUT                         = "5107FFFF";
    public static final String  MSG_STRING_LOGIN_DEVICE_INFO                    = "510700F0";

    public static final String  MSG_STRING_MAIN_INFORMATION_REQUEST             = "1107001E";
    public static final String  MSG_STRING_MAIN_DISPLAY_REQUEST                 = "51070063";

    public static final String  MSG_STRING_CONTROL_LIGHT_STATE_REQUEST          = "11071101";
    public static final String  MSG_STRING_CONTROL_LIGHT_EACH_REQUEST           = "11071102";
    public static final String  MSG_STRING_CONTROL_LIGHT_GROUP_REQUEST          = "11071103";
    public static final String  MSG_STRING_CONTROL_GAS_STATE_REQUEST            = "11071301";
    public static final String  MSG_STRING_CONTROL_GAS_EACH_REQUEST             = "11071302";
    public static final String  MSG_STRING_CONTROL_GAS_GROUP_REQUEST            = "11071303";
    public static final String  MSG_STRING_CONTROL_SECURITY_STATE_REQUEST       = "1107000C";
    public static final String  MSG_STRING_CONTROL_SECURITY_OUT_REQUEST         = "1107000D";
    public static final String  MSG_STRING_CONTROL_POWER_STATE_REQUEST          = "11073801";
    public static final String  MSG_STRING_CONTROL_POWER_EACH_REQUEST           = "11073802";
    public static final String  MSG_STRING_CONTROL_POWER_GROUP_REQUEST          = "11073803";
    public static final String  MSG_STRING_CONTROL_VENTILATION_STATE_REQUEST    = "11071601";
    public static final String  MSG_STRING_CONTROL_VENTILATION_EACH_REQUEST     = "11071602";
    public static final String  MSG_STRING_CONTROL_VENTILATION_GROUP_REQUEST    = "11071603";
    public static final String  MSG_STRING_CONTROL_HEAT_STATE_REQUEST           = "11071501";
    public static final String  MSG_STRING_CONTROL_HEAT_EACH_HEAT_REQUEST       = "11071502";
    public static final String  MSG_STRING_CONTROL_HEAT_GROUP_HEAT_REQUEST      = "11071503";
    public static final String  MSG_STRING_CONTROL_HEAT_TEMP_REQUEST            = "11071504";
    public static final String  MSG_STRING_CONTROL_HEAT_MODE_REQUEST            = "11071505";
    public static final String  MSG_STRING_CONTROL_HEAT_RESERVATION_REQUEST     = "11071506";
    public static final String  MSG_STRING_CONTROL_HEAT_HOTWATER_REQUEST        = "11071507";
    public static final String  MSG_STRING_CONTROL_BREAKER_REQUEST              = "11071801";
    public static final String  MSG_STRING_CONTROL_BREAKER_EACH                 = "11071802";
    public static final String  MSG_STRING_CONTROL_BREAKER_GROUP                = "11071803";

    public static final String  MSG_STRING_INFO_NOTICE_REQUEST                  = "21070001";
    public static final String  MSG_STRING_INFO_VISIT_LIST_REQUEST              = "11070015";
    public static final String  MSG_STRING_INFO_VISIT_VIDEO_REQUEST             = "11070017";
    public static final String  MSG_STRING_INFO_VISIT_VIDEO_CONFIRM_REQUEST     = "11070029";
    public static final String  MSG_STRING_INFO_ENERGY_REQUEST                  = "1F030105";
    public static final String  MSG_STRING_PUSH_OPEN_DOOR_REQUEST               = "1F500202";
    public static final String MSG_STRING_PUSH_SETTING_CHANGE_SAVE_REQUEST      = "510700101";
    public static final String  MSG_STRING_PUSH_SETTING_CHANGE_STATE_REQUEST    = "510700100";
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif data send message command integer
     */
    public static final int     MSG_WHAT_LOGIN_COMPLEX_REQUEST                  = 1;

    public static final int     MSG_WHAT_LOGIN_LOGIN_REQUEST                    = 2;
    public static final int     MSG_WHAT_LOGIN_REGISTRATION_REQUEST             = 3;
    public static final int     MSG_WHAT_LOGIN_ID_DUPLICATION_REQUEST           = 4;
    public static final int     MSG_WHAT_LOGIN_CERTIFY_REQUEST                  = 5;
    public static final int     MSG_WHAT_LOGIN_CERTIFY_AUTHENTICATION           = 6;
    public static final int     MSG_WHAT_LOGIN_LOGOUT                           = 7;
    public static final int     MSG_WHAT_LOGIN_WITHDRAWAL                       = 8;
    public static final int     MSG_WHAT_LOGIN_VERSION_REQUEST                  = 9;
    public static final int     MSG_WHAT_LOGIN_INFO_CHANGE_REQUEST              = 10;
    public static final int     MSG_WHAT_LOGIN_FIND_ID                          = 11;
    public static final int     MSG_WHAT_LOGIN_CERTIFY_DEVICE_INFO              = 12;

    public static final int     MSG_WHAT_MAIN_INFORMATION_REQUEST               = 20;
    public static final int     MSG_WHAT_MAIN_DISPLAY_REQUEST                   = 21;

    public static final int     MSG_WHAT_CONTROL_LIGHT_STATE_REQUEST            = 30;
    public static final int     MSG_WHAT_CONTROL_LIGHT_EACH_REQUEST             = 31;
    public static final int     MSG_WHAT_CONTROL_LIGHT_GROUP_REQUEST            = 32;
    public static final int     MSG_WHAT_CONTROL_GAS_STATE_REQUEST              = 33;
    public static final int     MSG_WHAT_CONTROL_GAS_EACH_REQUEST               = 34;
    public static final int     MSG_WHAT_CONTROL_GAS_GROUP_REQUEST              = 35;
    public static final int     MSG_WHAT_CONTROL_SECURITY_STATE_REQUEST         = 36;
    public static final int     MSG_WHAT_CONTROL_SECURITY_OUT_REQUEST           = 37;
    public static final int     MSG_WHAT_CONTROL_POWER_STATE_REQUEST            = 38;
    public static final int     MSG_WHAT_CONTROL_POWER_EACH_REQUEST             = 39;
    public static final int     MSG_WHAT_CONTROL_POWER_GROUP_REQUEST            = 40;
    public static final int     MSG_WHAT_CONTROL_VENTILATION_STATE_REQUEST      = 41;
    public static final int     MSG_WHAT_CONTROL_VENTILATION_EACH_REQUEST       = 42;
    public static final int     MSG_WHAT_CONTROL_VENTILATION_GROUP_REQUEST      = 43;
    public static final int     MSG_WHAT_CONTROL_HEAT_STATE_REQUEST             = 44;
    public static final int     MSG_WHAT_CONTROL_HEAT_EACH_HEAT_REQUEST         = 45;
    public static final int     MSG_WHAT_CONTROL_HEAT_GROUP_HEAT_REQUEST        = 46;
    public static final int     MSG_WHAT_CONTROL_HEAT_TEMP_REQUEST              = 47;
    public static final int     MSG_WHAT_CONTROL_HEAT_MODE_REQUEST              = 48;
    public static final int     MSG_WHAT_CONTROL_HEAT_RESERVATION_REQUEST       = 49;
    public static final int     MSG_WHAT_CONTROL_HEAT_HOTWATER_REQUEST          = 50;
    public static final int     MSG_WHAT_CONTROL_BREAKER_REQUEST                = 51;
    public static final int     MSG_WHAT_CONTROL_BREAKER_EACH                   = 52;
    public static final int     MSG_WHAT_CONTROL_BREAKER_GROUP                  = 53;

    public static final int     MSG_WHAT_INFO_NOTICE_REQUEST                    = 70;
    public static final int     MSG_WHAT_INFO_VISIT_LIST_REQUEST                = 71;
    public static final int     MSG_WHAT_INFO_VISIT_VIDEO_REQUEST               = 72;
    public static final int     MSG_WHAT_INFO_VISIT_VIDEO_CONFIRM_REQUEST       = 73;
    public static final int     MSG_WHAT_INFO_ENERGY_REQUEST                    = 74;

    public static final int     MSG_WHAT_PUSH_OPEN_DOOR_REQUEST                 = 80;
    public static final int     MSG_WHAT_PUSH_SETTING_CHANGE_STATE_REQUEST      = 81;
    public static final int     MSG_WHAT_PUSH_SETTING_CHANGE_SAVE_REQUEST       = 82;

    public static final int     MSG_WHAT_TIMER_START                            = 90;
    public static final int     MSG_WHAT_TIMER_END                              = 91;

    public static final int     MSG_WHAT_REGISTER_MESSENGER                     = 100;
    public static final int     MSG_WHAT_UNREGISTER_MESSENGER                   = 110;
    public static final int     MSG_WHAT_TCP_SOCKET_CLOSE                       = 11000;
    //**********************************************************************************************

    //**********************************************************************************************
    public static final String  INTENT_TYPE_CERTIFY                             = "INTENT_CERTIFY";
    public static final int     INTENT_DATA_CERTIFY_SUCCESS_AUTO_LOGIN          = 1;
    public static final String  INTENT_TYPE_HOME_VIEW                           = "INTENT_HOME_VIEW";
    //**********************************************************************************************

    //**********************************************************************************************
    public static final String  DEVICE_LIGHT                                    = "1401";
    public static final String  DEVICE_GAS                                      = "1402";
    public static final String  DEVICE_BATCHBREAK                               = "1407";
    public static final String  DEVICE_BOILER                                   = "140C";
    public static final String  DEVICE_STANDINGPOWER                            = "1421";
    public static final String  DEVICE_VENTILATION                              = "140B";
    public static final String  DEVICE_HOMEVIEW                                 = "1112";
    public static final String  DEVICE_OUTMODE                                  = "1111";
    //**********************************************************************************************

    //**********************************************************************************************
    public static final String  INTENT_FRAGMENT_STATE                           = "INTENT_FRAGMENT";
    public static final int     FRAGMENT_CONTROL                                = 0;
    public static final int     FRAGMENT_INFO                                   = 1;
    public static final int     FRAGMENT_SETUP                                  = 2;
    public static final int     FRAGMENT_HOME                                   = 3;
    //**********************************************************************************************

    //**********************************************************************************************
    public static final String  INTENT_INFO_DATA_NUM                            = "INTENT_INFO_DATA_NUM";
    public static final String  INTENT_INFO_DATA                                = "INTENT_INFO_DATA";
    public static final String  INTENT_INFO_DATA_TITLE                          = "INTENT_INFO_DATA_TITLE";
    public static final String  INTENT_INFO_DATA_CONTENTS                       = "INTENT_INFO_DATA_CONTENTS";
    public static final String  INTENT_INFO_DATA_TIME                           = "INTENT_INFO_DATA_TIME";
    public static final String  INTENT_INFO_DATA_DATE                           = "INTENT_INFO_DATA_DATE";
    public static final String  INTENT_INFO_DATA_PAGE                           = "INTENT_INFO_DATA_PAGE";
    public static final String  INTENT_INFO_DATA_TOTALPAGE                      = "INTENT_INFO_DATA_TOTAL_PAGE";
    public static final String  INTENT_INFO_DATA_ID                             = "INTENT_INFO_DATA_ID";
    //**********************************************************************************************

    public static final String  INTENT_LOGIN_ID                                 = "INTENT_ID";
    public static final String  INTENT_TIMEOUT                                  = "INTENT_TIMEOUT";

    //**********************************************************************************************
    public static final String  EMS_CATEGORY_ELECTRIC                           = "Electricity";
    public static final String  EMS_CATEGORY_GAS                                = "Gas";
    public static final String  EMS_CATEGORY_WATER                              = "Water";
    public static final String  EMS_CATEGORY_HOTWATER                           = "Hotwater";
    public static final String  EMS_CATEGORY_HEAT                               = "Heating";
    public static final String  EMS_CATEGORY_COOLING                            = "Cooling";

    public static final String  EMS_CALENDAR_DAY                                = "Day";
    public static final String  EMS_CALENDAR_MONTH                              = "Month";
    //**********************************************************************************************

    //**********************************************************************************************
    public static final String  SETTING_PUSH_TYPE_EMERGENCY                     = "SETTING_PUSH_EMERGENCY";
    public static final String  SETTING_PUSH_TYPE_DELIVERY                      = "SETTING_PUSH_DELIVERY";
    public static final String  SETTING_PUSH_TYPE_PARKING                       = "SETTING_PUSH_PARKING";
    public static final String  SETTING_PUSH_TYPE_CALL                          = "SETTING_PUSH_CALL";
    public static final String  INTENT_PUSH_TYPE                                = "INTENT_PUSH_TYPE";
    public static final String  INTENT_PUSH_PASSWORD                            = "INTENT_PUSH_PASSWORD";
    //**********************************************************************************************
}
