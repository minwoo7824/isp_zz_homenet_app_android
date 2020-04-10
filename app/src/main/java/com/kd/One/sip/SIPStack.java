package com.kd.One.sip;

import android.os.Build;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class SIPStack {
    //SIP STACK LOCAL PORT
    static int SIP_LOCAL_PORT						= 5050;//51881;
    static int MAX_SIGNAL_PORTS						= 100; //2012 08 10
    //METHOD TYPE DEFINE
    static int SIP_METHODTYPE_NONE 					= 0;
    static int SIP_METHODTYPE_REGISTER 				= 1;
    static int SIP_METHODTYPE_INVITE 				= 2;
    static int SIP_METHODTYPE_CANCEL 				= 3;
    static int SIP_METHODTYPE_BYE 					= 4;
    static int SIP_METHODTYPE_INFO 					= 5;
    static int SIP_METHODTYPE_OPTIONS 				= 6;
    static int SIP_METHODTYPE_ACK 					= 7;
    static int SIP_METHODTYPE_REFER 				= 8;
    static int SIP_METHODTYPE_NOTIFY 				= 9;
    static int SIP_METHODTYPE_MESSAGE 				= 10;
    static int SIP_METHODTYPE_SUBSCRIBE 			= 11;
    static int SIP_METHODTYPE_PRACK 				= 12;
    static int SIP_METHODTYPE_PUBLISH				= 13;
    //HEADER TYPE DEFINE
    static int SIP_HEADERTYPE_NONE					= 0;
    static int SIP_HEADERTYPE_ACCEPT				= 1;
    static int SIP_HEADERTYPE_ACCEPTENCODING		= 2;
    static int SIP_HEADERTYPE_ACCEPTLANGUAGE		= 3;
    static int SIP_HEADERTYPE_ALERTINFO				= 4;
    static int SIP_HEADERTYPE_ALLOW					= 5;
    static int SIP_HEADERTYPE_ALLOWEVENTS			= 6;
    static int SIP_HEADERTYPE_AUTHENTICATIONINFO	= 7;
    static int SIP_HEADERTYPE_AUTHORIZATION			= 8;
    static int SIP_HEADERTYPE_CALLID				= 9;
    static int SIP_HEADERTYPE_CALLINFO				= 10;
    static int SIP_HEADERTYPE_CONTACT				= 11;
    static int SIP_HEADERTYPE_CONTENTDISPOSITION	= 12;
    static int SIP_HEADERTYPE_CONTENTENCODING		= 13;
    static int SIP_HEADERTYPE_CONTENTLANGUAGE		= 14;
    static int SIP_HEADERTYPE_CONTENTLENGTH			= 15;
    static int SIP_HEADERTYPE_CONTENTTYPE			= 16;
    static int SIP_HEADERTYPE_CSEQ					= 17;
    static int SIP_HEADERTYPE_DATE					= 18;
    static int SIP_HEADERTYPE_EVENT					= 19;
    static int SIP_HEADERTYPE_ERRORINFO				= 20;
    static int SIP_HEADERTYPE_EXPIRES				= 21;
    static int SIP_HEADERTYPE_FROM					= 22;
    static int SIP_HEADERTYPE_INREPLYTO				= 23;
    static int SIP_HEADERTYPE_MAXFORWARDS			= 24;
    static int SIP_HEADERTYPE_MINEXPIRES			= 25;
    static int SIP_HEADERTYPE_MIMEVERSION			= 26;
    static int SIP_HEADERTYPE_ORGANIZATION			= 27;
    static int SIP_HEADERTYPE_PRIORITY				= 28;
    static int SIP_HEADERTYPE_PROXYAUTHENTICATE		= 29;
    static int SIP_HEADERTYPE_PROXYAUTHORIZATION	= 30;
    static int SIP_HEADERTYPE_PROXYREQUIRE			= 31;
    static int SIP_HEADERTYPE_PASSERTEDIDENTITY		= 32;
    static int SIP_HEADERTYPE_RECORDROUTE			= 33;
    static int SIP_HEADERTYPE_REPLYTO				= 34;
    static int SIP_HEADERTYPE_REQUIRE				= 35;
    static int SIP_HEADERTYPE_RETRYAFTER			= 36;
    static int SIP_HEADERTYPE_ROUTE					= 37;
    static int SIP_HEADERTYPE_SERVER				= 38;
    static int SIP_HEADERTYPE_SUBJECT				= 39;
    static int SIP_HEADERTYPE_SUPPORTED				= 40;
    static int SIP_HEADERTYPE_TIMESTAMP				= 41;
    static int SIP_HEADERTYPE_TO					= 42;
    static int SIP_HEADERTYPE_UNSUPPORTED			= 43;
    static int SIP_HEADERTYPE_USERAGENT				= 44;
    static int SIP_HEADERTYPE_VIA					= 45;
    static int SIP_HEADERTYPE_WARNING				= 46;
    static int SIP_HEADERTYPE_WWWAUTHENTICATE		= 47;
    static int SIP_HEADERTYPE_WwwAUTHENTICATE		= 48;//2013 02 12
    static String SIP_LINE_END						= "\r\n";
    static String SIP_LINE_DOUBLEEND			    = "\r\n\r\n";
    static String SIP_LINE_END_SINGLE				= "\n";
    static String USER_AGENT						= "kdwon SmartSip release 0100o";//2013 02 12
    //MESSAGETYPE
    static int SIP_MSGTYPE_NONE						= 0;
    static int SIP_MSGTYPE_REQUEST					= 1;
    static int SIP_MSGTYPE_RESPONSE					= 2;
    //REGISTER STATE DEFINE
    static int SIP_REGSTATE_IDLE					= 0;
    static int SIP_REGSTATE_REGISTERING				= 1;
    static int SIP_REGSTATE_REGISTERED				= 2;
    static int SIP_REGSTATE_UNREGISTERING			= 3;
    static int SIP_REGSTATE_AUTHORIZING				= 4;
    static int SIP_REGSTATE_UNAUTHORIZED			= 5;
    static int SIP_REGSTATE_UNAVAILABLE				= 6;
    //CALL STATE DEFINE
    static int SIP_CALLSTATE_IDLE					= 0;
    static int SIP_CALLSTATE_INVITING				= 1;
    static int SIP_CALLSTATE_OFFERRED				= 2;
    static int SIP_CALLSTATE_PROCEEDING				= 3;
    static int SIP_CALLSTATE_PROGRESSING			= 4;
    static int SIP_CALLSTATE_ACCEPTED				= 5;
    static int SIP_CALLSTATE_REMOTEACCEPTED			= 6;
    public static int SIP_CALLSTATE_CONNECTED		= 7;
    static int SIP_CALLSTATE_DISCONNECTING			= 8;
    static int SIP_CALLSTATE_TERMINATING			= 9;
    static int SIP_CALLSTATE_UNAUTHORIZED			= 10;
    static int SIP_CALLSTATE_CANCELLING				= 11;
    static int SIP_CALLSTATE_CANCELLED				= 12;
    static int SIP_CALLSTATE_REJECTED				= 13;
    static int SIP_CALLSTATE_REDIRECTED				= 14;
    static int SIP_CALLSTATE_UPDATING				= 15;
    static int SIP_CALLSTATE_UPDATEPROCEEDING		= 16;
    static int SIP_CALLSTATE_UPDATEPROGRESSING		= 17;
    static int SIP_CALLSTATE_UPDATEOFFERRED			= 18;
    static int SIP_CALLSTATE_UPDATEACCEPTED			= 19;
    static int SIP_CALLSTATE_UPDATEREJECTED			= 20;
    static int SIP_CALLSTATE_UPDATEREMOTEACCEPTED	= 21;
    static int SIP_CALLSTATE_UPDATEREMOTEREJECTED	= 22;
    static int SIP_CALLSTATE_UPDATEUNAUTHORIZED		= 23;
    static int SIP_CALLSTATE_UPDATEREDIRECTED		= 24;
    //CALL MODE DEFINE
    static int SIP_CALLMODE_NONE					= 0;
    static int SIP_CALLMODE_BASIC					= 1;
    static int SIP_CALLMODE_HOLD					= 2;
    static int SIP_SERVICEMODE_BASIC				= 0;
    static int SIP_SERVICEMODE_CONFERENCE			= 1;
    //CALL DIRECTION DEFINE
    static int SIP_CALLDIRECTION_NONE				= 0;
    static int SIP_CALLDIRECTION_IN					= 1;
    static int SIP_CALLDIRECTION_OUT				= 2;
    //MEDIA TYPE DEFINE
    static int SIP_MEDIATYPE_NONE					= 0;
    static int SIP_MEDIATYPE_AUDIO					= 1;
    static int SIP_MEDIATYPE_VIDEO					= 2;
    //CODE DEFINE
    static int SIP_CODEC_NONE						= -1;
    static int SIP_CODEC_G711U						= 0;
    static int SIP_CODEC_G711A						= 8;
    static int SIP_CODEC_G7231						= 4;
    static int SIP_CODEC_G729						= 18;
    static int SIP_CODEC_H263						= 34;
    static int SIP_CODEC_H264						= 96;
    static int PRIMARY_CODEC_AUDIO					= SIP_CODEC_G711U;
    static int PRIMARY_CODEC_VIDEO					= SIP_CODEC_H264;
    //INFO DTMF TYPE
    static int SIP_DTMFINFO_DTMFRELAY				= 0;
    static int SIP_DTMFINFO_DTMF					= 1;
    //
    static int SIP_SEQUENCE_INVITE					= 1;
    static int SIP_SEQUENCE_REGISTER				= 1;
    //MEDIA START PORT
    static int SIP_MEDIA_INITPORT					= 3000;
    static int SIP_MEDIA_PORTS						= 100;
    //MEDIA FLOW
    static int SIP_MEDIAFLOW_NONE					= 0;
    static int SIP_MEDIAFLOW_SENDRECV				= 1;
    static int SIP_MEDIAFLOW_SENDONLY				= 2;
    static int SIP_MEDIAFLOW_RECVONLY				= 3;

    static int RTP_AUDIO_SIZE						= 1024;
    //
    static boolean[] SIP_MEDIA_PORTFLAG				= new boolean[SIP_MEDIA_PORTS];
    static int SIP_MEDIA_PORTSEQ					= 0;
    //CALL STATE TIMEOUT DEFINE
    static int CS_TIMEOUT_TI						= 3; //IDLE TIMEOUT
    static int CS_TIMEOUT_T0						= 15; //INVITING TIMEOUT
    static int CS_TIMEOUT_T00						= 10;//1; //INVITING REPEAT TIMEOUT 2012 03 22 1->5 2012 11 29 5->10
    static int CS_TIMEOUT_T1						= 120; //PROCEEDING TIMEOUT 2012 11 29 15->30 2014 12 19 30->120
    static int CS_TIMEOUT_T2						= 120; //PROGRESSING TIMEOUT
    static int CS_TIMEOUT_T3						= 7; //ACCEPTED TIMEOUT
    static int CS_TIMEOUT_T4						= 7200; //CONNECTED TIMEOUT
    static int CS_TIMEOUT_T5						= 7; //DISCONNECTING TIMEOUT
    static int CS_TIMEOUT_T6						= 0; //TERMINATING TIMEOUT
    static int CS_TIMEOUT_T7						= 120; //OFFERRED TIMEOUT 2014 12 19 30->120
    static int CS_TIMEOUT_T8						= 7; //CANCELLING TIMEOUT

    static int CS_TIMEOUT_RESPONSE					= 1; //MESSAGE RESPONSE TIMEOUT
    static int NETWORKIF_CHECK_TIME					= 5;//NETWORK INTERFACE CHANGE CHECK seconds
    //2012 06 08
    static int TEARDOWN_CODE_NORMAL					= 0;
    static int TEARDOWN_CODE_EEREJECT				= 1;
    static int TEARDOWN_CODE_ERREJECT				= 2;
    static int TEARDOWN_CODE_NOTFOUND				= 3;
    static int TEARDOWN_CODE_ERROR					= 4;
    //2012 08 10
    static int SIP_NETIF_AVAILABLE					= 1;
    static int SIP_NETIF_UNAVAILABLE				= 0;
    static int SIP_NETIF_UNREACHABLE				= 2;
    //2012 07 16
    static int REPEAT_SENDCOUNT						= 10;//3 2015 03 30 3->10
    static int DIALER_IDLE_TIME 					= 20000;//milli seconds 2015 03 30 updated
    static int CALLER_INVOKE_DELAY					= 1500; //0  2015 03 30 0->0

    //
    static int SIP_MAXMESSAGE_SIZE					= 2500;//65507;

    //DEVELOPEMENT CONTROL
    static boolean SIP_MESSAGE_DEBUG				= true;
    static boolean SIP_CALLHANDLE_DEBUG				= false;

    //static SIPSound sipSound						= null;
    static Date bootTime							= new Date();
    static int exceptionCountAtCurrentCall			= 0;

    static boolean ventureCall						= false;
    static boolean bLimitedUse						= false;

    static int BACKGROUNDIDLE_MAX_SECONDS			= 600;//10 minutes
    static int networkStatus						= SIP_NETIF_UNAVAILABLE;

    //2014 12 16
    static int VIBRATION_TIMER_TIME					= 3500;
    static int VIBRATION_LONG_TIME					= 700;
    static int VIBRATION_SHORT_TIME					= 500;
    //

    static String generalPhoneNumber				= "";
    static String hwModelName						= "";
    static String hwId								= "";
    static String firmVersion						= "";
    static String hwImsi							= "";
    static String hwImei							= "";
    static String gcmRegistId						= "";
    static boolean bGCMRegistSend					= false;
    static String ANDROID_API_KEY 					= "AIzaSyDZcNyOPs5iTQ9kGZaF56Ngf4eCQDzMEl0";
    //2015 06 15

    static String SERVER_IP							= "124.111.208.92";
    static int SERVER_PORT 							= 5050;//6008;//21010;

    static boolean bConstantSpeaker					= true;
    //
    static boolean isInternetWiMax 					= false;
    static boolean isInternetWiFi 					= false;
    static boolean isInternetMobile 				= false;
    static String  localSdpIp						= "0.0.0.0";
    static boolean usePrivateWiMax					= true;
    static boolean bShutdownApplication				= false;
    static boolean bOuttraffic						= false;
    static boolean bIntraffic						= false;
    static boolean bActive 							= false;
    static boolean bFreeCall 						= false;
    static boolean bBlindCall 						= true;
    static boolean bBlindSdp 						= false;
    static boolean bRegistMode 						= true;
    static DeviceInfo deviceInfo=new DeviceInfo();
    static boolean bTcpVideoMode					= true;//2015 07 07

    static String ip = null;

    public SIPStack() {

        SERVER_PORT = 21010;

        bTcpVideoMode = true;
        bRegistMode = true;
    }

    public static void init()
    {
        //2012 08 31
		/*
		System.out.println("======== SYSTEM INFO. =======");
		System.out.println("modelName is :"+Build.MODEL);
		System.out.println("device is :"+Build.DEVICE);
		System.out.println("ProductName is :"+Build.PRODUCT);
		System.out.println("ID is :"+Build.ID);
		System.out.println("USER is :"+Build.USER);
		System.out.println("TAGS is :"+Build.TAGS);
		System.out.println("VERSION is :"+Build.VERSION.SDK_INT);
		System.out.println("INCREMENTAL is :"+Build.VERSION.INCREMENTAL);
		System.out.println("RELEASE is :"+Build.VERSION.RELEASE);
		*/
        //
        hwModelName= Build.MODEL;
        hwId=Build.ID;
        firmVersion=Build.VERSION.RELEASE;
        bActive=true;
        SIP_MEDIA_PORTSEQ=(int)((new Date()).getSeconds() % 60);
        //

    }
    //


    public static String getResponseDescription(int code)
    {
        switch(code)
        {
            case 100:
                return "Trying";
            case 180:
                return "Ringing";
            case 181:
                return "Call Is Being Forwarded";
            case 182:
                return "Call Queued";
            case 183:
                return "Session Progress";
            case 200:
                return "OK";
            case 300:
                return "Multiple Choices";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Moved Temporarily";
            case 305:
                return "Use Proxy";
            case 380:
                return "Alternative Service";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 402:
                return "Payment Required";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 406:
                return "Not Acceptable";
            case 407:
                return "Proxy Authentication Required";
            case 408:
                return "Request Timeout";
            case 409:
                return "Conflict";
            case 410:
                return "Gone";
            case 411:
                return "Length Required";
            case 413:
                return "Request Entity Too Large";
            case 414:
                return "Request-URI Too Long";
            case 415:
                return "Unsupported Media Type";
            case 420:
                return "Bad Extension";
            case 421:
                return "Extension Required";
            case 423:
                return "Interval Too Brief";
            case 480:
                return "Temporarily Unavailable";
            case 481:
                return "Call Leg Does Not Exist";
            case 482:
                return "Loop Detected";
            case 483:
                return "Two Many Hops";
            case 484:
                return "Address Incomplete";
            case 485:
                return "Ambiguous";
            case 486:
                return "Busy Here";
            case 487:
                return "Request Canceled";
            case 488:
                return "Not Acceptable Here";
            case 500:
                return "Server Internal Error";
            case 501:
                return "Not Implemented";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";
            case 504:
                return "Gateway Timeout";
            case 505:
                return "Version Not Supported";
            case 600:
                return "Busy Everywhere";
            case 603:
                return "Decline";
            case 604:
                return "Does Not Exist Anywhere";
            case 606:
                return "Not Acceptable";
            default:
                break;
        }
        return "";
    }
    public static String BSSMD5Get(String data)
    {
        if(data==null||data.length()<=0) return null;//2012 03 23

        try
        {
            SIPmd5 md = SIPmd5.getInstance();
            return md.hashData(data.getBytes());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace(System.out);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        }

        return null;

    }
    public static String getViaBranch()
    {
        Date today=new Date();
        return "z9hG4bK-"+today.getTime()+"-"+((int)today.getTime() % 7);
    }
    public static String newTag(String prefix)
    {
        Date today=new Date();
        return prefix+today.getTime()+"-"+today.hashCode();
    }
    public static String newTag()
    {
        Date today=new Date();
        return today.getTime()+"."+today.hashCode();
    }

    public static String getSHVE140SLocalIpAddress()
    {
        String validIp=null;
        try {
            Enumeration en= NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements())
            {
                NetworkInterface intf=(NetworkInterface) en.nextElement();
                //System.out.println("Network Interface "+intf.toString()+"  name:"+intf.getName());

                Enumeration enumIpAddr = intf.getInetAddresses();
                while(enumIpAddr.hasMoreElements()) {
                    //System.out.println("InetAddress ");
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();

                    //System.out.println(" ==>"+inetAddress.toString());
                    String strIp=null;
                    if(inetAddress.toString()!=null && inetAddress.toString().length()>1)
                    {
                        strIp=inetAddress.toString().substring(1);
                        //Log.e("Host IP", "strIp : "+strIp);

                        StringTokenizer st= new StringTokenizer(strIp,".",true);
                        int tokenCount=0;
                        String token=null;
                        //System.out.println(">>> "+headerValue);
                        int ipdigit_0=0;
                        int ipdigit_1=0;
                        int ipdigit_2=0;
                        int ipdigit_3=0;
                        while(st.hasMoreTokens())
                        {
                            token=st.nextToken().trim();

                            if(token!=null && token.length()>0  && token.length()<=3 && token.compareTo(".")!=0)
                            {
                                if(tokenCount==0)
                                {
                                    if(token.trim().equals("::1")){
                                        return "127.0.0.1";
                                    }
                                    ipdigit_0=Integer.parseInt(token.trim());
                                }
                                else if(tokenCount==1)
                                {
                                    ipdigit_1=Integer.parseInt(token.trim());
                                }
                                else if(tokenCount==2)
                                {
                                    ipdigit_2=Integer.parseInt(token.trim());
                                }
                                else if(tokenCount==3)
                                {
                                    ipdigit_3=Integer.parseInt(token.trim());
                                }
                                tokenCount++;
                            }
                        }
                        //Log.e("SIP host", String.format("tokenCount : %d",tokenCount));

                        if(tokenCount==4)
                        {
    						/*
    						if(
    							ipdigit_0>0 && ipdigit_0<=255 &&
    							ipdigit_1>=0 && ipdigit_1<=255 &&
    							ipdigit_2>=0 && ipdigit_2<=255 &&
    							ipdigit_3>=0 && ipdigit_3<=255 &&
    							!( //localloop
    								ipdigit_0 == 127 &&
    								ipdigit_1 == 0 &&
    								ipdigit_2 == 0 &&
    								ipdigit_3 == 1
    							)
    						)
    						{
    							validIp=ipdigit_0+"."+ipdigit_1+"."+ipdigit_2+"."+ipdigit_3;
    							System.out.println("We got i/f:"+validIp);
    							return validIp;
    						}
    						*/
                            if(
                                    ipdigit_0>0 && ipdigit_0<=255 &&
                                            ipdigit_1>=0 && ipdigit_1<=255 &&
                                            ipdigit_2>=0 && ipdigit_2<=255 &&
                                            ipdigit_3>=0 && ipdigit_3<=255

                            )
                            {
                                validIp=ipdigit_0+"."+ipdigit_1+"."+ipdigit_2+"."+ipdigit_3;

                                System.out.println("We got i/f:"+validIp);
                                return validIp;
                            }
                        }
                        //
                    }
                }
            }
        } catch(SocketException exception) {
            System.out.println("We got Exception here:"+exception.toString());
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        } catch(Exception e){
            validIp = "127.0.0.1";
        }
        return validIp;
    }
    //
    public static int getFreeAudioRtpPort()
    {
        try
        {
            //System.out.println(">>>getFreeAudioRtpPort");
            int index=0;
            for(int i=0;i<SIP_MEDIA_PORTS;i++)
            {
                index=(SIP_MEDIA_PORTSEQ+i) % SIP_MEDIA_PORTS;
                if(SIP_MEDIA_PORTFLAG[index]==false) {
                    SIP_MEDIA_PORTFLAG[index]=true;
                    SIP_MEDIA_PORTSEQ=(index+1) % SIP_MEDIA_PORTS;
                    //System.out.println("Audio Rtp Port Allocated:"+(SIP_MEDIA_INITPORT+i*2));
                    return SIP_MEDIA_INITPORT+index*2;
                }
            }
        }catch(Exception e){}
        return 0;
    }

    public static boolean freeAudioRtpPort(int port)
    {
        //System.out.println(">>>freeAudioRtpPort");
        int index=(port-SIP_MEDIA_INITPORT)/2;
        if(index>=0 && index<SIP_MEDIA_PORTS)
        {
            SIP_MEDIA_PORTFLAG[index]=false;
            //System.out.println("Audio Rtp Port Freed:"+port);
            return true;
        }
        return false;
    }

    public static void printAudioRtpPortInfo()
    {
        System.out.print(">>>> RTP PORT STATUS  ");
        for(int i=0;i<SIP_MEDIA_PORTS;i++)
        {
            System.out.print("  "+SIP_MEDIA_PORTFLAG[i]);
        }
        System.out.println("  ");
        return;
    }
    public static String getIPV4(final String host)
    {
        Thread thread = new Thread() {
            @Override
            public void run() {
                if(host==null || host.length()==0) ip = null;

                try {
                    InetAddress ia=InetAddress.getByName(host);
                    byte[] address = ia.getAddress();
                    if(address.length==4)
                    {
                        int unsignedByte1 = address[0]<0 ? address[0]+256 : address[0];
                        int unsignedByte2 = address[1]<0 ? address[1]+256 : address[1];
                        int unsignedByte3 = address[2]<0 ? address[2]+256 : address[2];
                        int unsignedByte4 = address[3]<0 ? address[3]+256 : address[3];
                        ip=unsignedByte1+"."+unsignedByte2+"."+unsignedByte3+"."+unsignedByte4;
                    }

                    if(ip == null){
                        InetAddress[] addresses  = Inet4Address.getAllByName(host);
                        Log.e("length" ,addresses.length + "/");
                        for(int i = 0; i < addresses.length; i++){
                            Log.e("addressses", addresses[i].toString());
                        }
                    }

                } catch(UnknownHostException uhe) {
                    uhe.printStackTrace();
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                } catch(Exception e) {
                    e.printStackTrace();
                }
                Log.e("getIPV4","Host:"+host+", ip:"+ip);
            }
        };
        thread.start();

        try {
            thread.join();
        }catch (Exception e){
            e.printStackTrace();
        }

        return ip;
    }

    //2013 02 14
    public static boolean isPrivateIp(String ip)
    {
        if(ip==null || ip.length()<7) return false;

        boolean bPrivate=false;
        //private ip prefix
        //10.0.0.0 - 10.255.255.255 (10/8 prefix)
        //172.16.0.0 - 172.31.255.255 (172.16/12 prefix)
        //192.168.0.0 - 192.168.255.255 (192.168/16 prefix)
        StringTokenizer st= new StringTokenizer(ip,".",true);
        int tokenCount=0;
        String token=null;
        //System.out.println(">>> "+headerValue);
        int ipdigit_0=0;
        int ipdigit_1=0;
        int ipdigit_2=0;
        int ipdigit_3=0;
        while(st.hasMoreTokens())
        {
            token=st.nextToken().trim();
            if(token!=null && token.length()>0
                    && token.length()<=3
                    && token.compareTo(".")!=0)
            {
                if(tokenCount==0)
                {
                    ipdigit_0=Integer.parseInt(token.trim());
                }
                else if(tokenCount==1)
                {
                    ipdigit_1=Integer.parseInt(token.trim());
                }
                else if(tokenCount==2)
                {
                    ipdigit_2=Integer.parseInt(token.trim());
                }
                else if(tokenCount==3)
                {
                    ipdigit_3=Integer.parseInt(token.trim());
                }
                tokenCount++;
            }
        }
        if(tokenCount==4)
        {
            bPrivate= ipdigit_0==10 ||
                    (ipdigit_0==172 && ipdigit_1>=16 && ipdigit_1<=31) ||
                    (ipdigit_0==192 && ipdigit_1==168);
        }

        return bPrivate;
    }

    //


}//class SIPStack
