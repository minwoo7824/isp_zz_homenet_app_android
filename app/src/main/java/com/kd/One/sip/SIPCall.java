package com.kd.One.sip;

import android.util.Log;

import java.util.Date;
import java.util.StringTokenizer;

public class SIPCall  {

    String commandLine				= null;
    String viaH						= null;
    StringBuffer viaArray			= new StringBuffer();
    String routeH					= null;
    StringBuffer routeArray 		= new StringBuffer();
    String recordrouteH				= null;
    StringBuffer recordrouteArray	= new StringBuffer();
    String maxforwardH				= null;
    String contactH					= null;
    String toH						= null;
    String fromH					= null;
    String callidH					= null;
    String cseqH					= null;
    String expiresH					= null;
    String allowH					= null;
    String useragentH				= null;
    String remoteuseragentH			= null;
    String contentlengthH			= null;
    String contenttypeH				= null;
    String authorizationINVITEH		= null;
    String authorizationCANCELH		= null;
    String authorizationBYEH		= null;
    String authorizationACKH		= null;
    String passertedidentityH		= null;
    String supportedH				= null;
    String serverIp					= null;
    int	serverPort					= 5050;
    String serverDomain				= null;
    String remoteIp					= null;
    int	remotePort					= 5050;
    String remoteContactIp			= null;
    int	remoteContactPort			= 5050;
    String remoteContactUri			= null;
    String localIp					= null;
    int	localPort					= 5050;
    String id						= null;
    String cid						= null;
    String authid					= null;
    String authpassword				= null;
    String fromTag					= null;
    String toTag					= null;
    String fromHeaderValue			= null;
    String toHeaderValue			= null;
    String callId					= null;
    String dnis						= null;
    String viaBranch				= null;//2013 02 13
    int CSEQ_NUMBER					= SIPStack.SIP_SEQUENCE_INVITE;
    int INVITE_CSEQ					= 0;
    int CANCEL_CSEQ					= 0;
    int ACK_CSEQ					= 0;
    int BYE_CSEQ					= 0;
    int callDirection				= SIPStack.SIP_CALLDIRECTION_NONE;

    public static int callState		= SIPStack.SIP_CALLSTATE_IDLE;
    int callMode					= SIPStack.SIP_CALLMODE_NONE;
    Date callTime_TI				= null;//idle timer
    Date callTime_T0				= null;//invite timer
    Date callTime_T00				= null;//repeat invite timer
    int invitingTimes				= 0;
    Date callTime_T1				= null;//proceeding timer
    Date callTime_T2				= null;//progressing timer
    Date callTime_T3				= null;//accepted timer
    Date callTime_T4				= null;//connected timer
    Date callTime_T40				= null;//connected duration report timer
    Date callTime_T5				= null;//disconnecting timer
    Date callTime_T6				= null;//terminating timer
    Date callTime_T7				= null;//offerred timer
    Date callTime_T8				= null;//cancelling timer

    int expiresTI					= 0;//idle timer
    int expiresT0					= 0;//invite timer
    int expiresT00					= 0;//repeat invite timer
    int expiresT1					= 0;//proceeding timer
    int expiresT2					= 0;//progressing timer
    int expiresT3					= 0;//accepted timer
    int expiresT4					= 0;//connected timer
    int expiresT5					= 0;//disconnecting timer
    int expiresT6					= 0;//terminating timer
    int expiresT7					= 0;//offerred timer
    int expiresT8					= 0;//cancelling timer

    //2014 12 02
    int finalCode 					= 0;
    int callDuration 				= 0;
    //

    //SDP
    SIPSdp	sdp						= null;
    SIPSdp	remoteSdp				= null;
    //

    String message					= null;
    String finalResponseMessage		= null;

    //DEVELOPEMENT CONTROL
    protected boolean flag;
    //UPDATEHEADERS CLASS
    UPDATEHeaders updateHeaders		= null;
    //SUPPLEMENTARY SERVICE - HOLD
    protected boolean	bHolding=false;
    //CONFERENCE CALL
    boolean 			bConference=false;
    //DTMF DETECT
    boolean				bDtmfdetected=false;
    int					detectedDtmf=0;
    int					dtmfDuration=0;
    //2012 07 16
    boolean				bCancelRequest	= false;
    boolean				bRejectRequest	= false;
    boolean				bNewcallRequest	= false;
    boolean				bUpdateRequest	= false; //2012 08 20
    String				number			= "";
    boolean 			bCallPrepared 	= false;
    boolean 			bDestroyActivity = false;
    boolean 			bRelayCall 		= false;
    boolean 			bVideoIframeMarked=false;

    public  String ifIp				= null;
    public  int ifPort				= 5050;

    //

    public SIPCall() {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("call handle created.");
        updateHeaders	= new UPDATEHeaders();
        CSEQ_NUMBER		= SIPStack.SIP_SEQUENCE_INVITE;

        callState		= SIPStack.SIP_CALLSTATE_IDLE;
        callMode		= SIPStack.SIP_CALLMODE_BASIC;
        callTime_TI		= new Date();
        callTime_T0		= new Date();
        callTime_T00	= new Date();
        invitingTimes	= 0;
        callTime_T1		= new Date();
        callTime_T2		= new Date();
        callTime_T3		= new Date();
        callTime_T4		= new Date();
        callTime_T40	= new Date();
        callTime_T5		= new Date();
        callTime_T6		= new Date();
        callTime_T7		= new Date();
        callTime_T8		= new Date();
        expiresTI		= 0;
        expiresT0		= 0;
        expiresT00		= 0;
        expiresT1		= 0;
        expiresT2		= 0;
        expiresT3		= 0;
        expiresT4		= 0;
        expiresT5		= 0;
        expiresT6		= 0;
        expiresT7		= 0;
        expiresT8		= 0;

        sdp				= null;
        remoteSdp		= null;
        bHolding		= false;
        bConference		= false;
        bCancelRequest  = false;
        bRejectRequest  = false;
        bNewcallRequest	= false;
        bUpdateRequest	= false;
        bCallPrepared 	= false;
        bDestroyActivity = false;
        number			= "";
        viaBranch		= null;//2013 02 13

        callDirection	= SIPStack.SIP_CALLDIRECTION_NONE;
        finalCode 		= 0;
        callDuration	= 0;
        ifIp="";
        ifPort=0;
        finalResponseMessage= null;
        bRelayCall 		= false;
        bVideoIframeMarked=false;

        flag=false;
    }
    public SIPCall(boolean bConferenceCall) {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("call handle created.");
        updateHeaders	= new UPDATEHeaders();
        CSEQ_NUMBER		= SIPStack.SIP_SEQUENCE_INVITE;

        callState		= SIPStack.SIP_CALLSTATE_IDLE;
        callMode		= SIPStack.SIP_CALLMODE_BASIC;
        callTime_TI		= new Date();
        callTime_T0		= new Date();
        callTime_T00	= new Date();
        invitingTimes	= 0;
        callTime_T1		= new Date();
        callTime_T2		= new Date();
        callTime_T3		= new Date();
        callTime_T4		= new Date();
        callTime_T40	= new Date();
        callTime_T5		= new Date();
        callTime_T6		= new Date();
        callTime_T7		= new Date();
        callTime_T8		= new Date();
        expiresTI		= 0;
        expiresT0		= 0;
        expiresT00		= 0;
        expiresT1		= 0;
        expiresT2		= 0;
        expiresT3		= 0;
        expiresT4		= 0;
        expiresT5		= 0;
        expiresT6		= 0;
        expiresT7		= 0;
        expiresT8		= 0;

        sdp				= null;
        remoteSdp		= null;
        bHolding		= false;
        bCancelRequest  = false;
        bRejectRequest  = false;
        bNewcallRequest	= false;
        bUpdateRequest	= false;
        bCallPrepared 	= false;
        bDestroyActivity = false;
        number			= "";
        viaBranch		= null;//2013 02 13

        bConference		= bConferenceCall;

        finalCode 		= 0;
        callDuration	= 0;
        finalResponseMessage= null;
        bRelayCall 		= false;
        bVideoIframeMarked=false;

        flag=false;
    }
    public void resetCall()
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) {
            System.out.println("call handle reset.");
            SIPStack.printAudioRtpPortInfo();
        }
        commandLine				= null;
        viaH					= null;
        viaArray				= null;//new StringBuffer();
        routeH					= null;
        routeArray				= null;//new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= null;//new StringBuffer();
        maxforwardH				= null;
        contactH				= null;
        toH						= null;
        fromH					= null;
        callidH					= null;
        cseqH					= null;
        expiresH				= null;
        allowH					= null;
        useragentH				= "User-Agent: KwangHaeSoft SmartSip release 0100o";
        remoteuseragentH		= null;
        contentlengthH			= null;
        contenttypeH			= null;
        authorizationINVITEH	= null;
        authorizationCANCELH	= null;
        authorizationBYEH		= null;
        authorizationACKH		= null;
        passertedidentityH		= null;
        serverIp				= null;
        serverPort				= 5050;
        serverDomain			= null;
        remoteIp				= null;
        remotePort				= 5050;
        remoteContactIp			= null;
        remoteContactPort		= 5050;
        remoteContactUri		= null;
        localIp					= null;
        localPort				= 5050;
        id						= null;
        cid						= null;
        authid					= null;
        authpassword			= null;
        fromTag					= null;
        toTag					= null;
        fromHeaderValue			= null;
        toHeaderValue			= null;
        callId					= null;
        dnis					= null;
        CSEQ_NUMBER				= SIPStack.SIP_SEQUENCE_INVITE;

        callState				= SIPStack.SIP_CALLSTATE_IDLE;
        callMode				= SIPStack.SIP_CALLMODE_BASIC;
        callTime_TI				= new Date();
        callTime_T0				= new Date();
        callTime_T00			= new Date();
        invitingTimes			= 0;
        callTime_T1				= new Date();
        callTime_T2				= new Date();
        callTime_T3				= new Date();
        callTime_T4				= new Date();
        callTime_T40			= new Date();
        callTime_T5				= new Date();
        callTime_T6				= new Date();
        callTime_T7				= new Date();
        callTime_T8				= new Date();

        expiresTI				= 0;
        expiresT0				= 0;
        expiresT00				= 0;
        expiresT1				= 0;
        expiresT2				= 0;
        expiresT3				= 0;
        expiresT4				= 0;
        expiresT5				= 0;
        expiresT6				= 0;
        expiresT7				= 0;
        expiresT8				= 0;
        //2012 03 22
        if(sdp!=null && sdp.flag==true && sdp.audioM!=null)
        {
            SIPStack.freeAudioRtpPort(sdp.audioM.mediaPort);
        }
        //
        sdp						= null;
        remoteSdp				= null;

        message					= null;
        bHolding				= false;
        bConference				= false;
        bCancelRequest  		= false;
        bRejectRequest  		= false;
        bNewcallRequest			= false;
        bUpdateRequest			= false;
        bCallPrepared 			= false;
        bDestroyActivity 		= false;
        number					= "";
        viaBranch				= null;//2013 02 13
        callDirection			= SIPStack.SIP_CALLDIRECTION_NONE;

        finalCode 				= 0;
        callDuration			= 0;
        finalResponseMessage	= null;
        bRelayCall 		= false;
        bVideoIframeMarked=false;

        flag=false;
        SIPStack.exceptionCountAtCurrentCall=0;//2012 03 23
        System.gc();//2012 02 22
        return;
    }
    //2012 02 10
    public void initializeRedirectCall()
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("redirect call handle initialize.");
        viaH					= null;
        viaArray				= null;//new StringBuffer();
        routeH					= null;
        routeArray				= null;//new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= null;//new StringBuffer();
        remoteContactIp			= null;
        remoteContactPort		= 5050;
        remoteContactUri		= null;
        callId					= null;
        authorizationINVITEH	= null;
        authorizationCANCELH	= null;
        authorizationBYEH		= null;
        authorizationACKH		= null;
        viaBranch				= null;//2013 02 13

        callState				= SIPStack.SIP_CALLSTATE_IDLE;
        callMode				= SIPStack.SIP_CALLMODE_BASIC;
        callTime_TI				= new Date();
        callTime_T0				= new Date();
        callTime_T00			= new Date();
        invitingTimes			= 0;
        callTime_T1				= new Date();
        callTime_T2				= new Date();
        callTime_T3				= new Date();
        callTime_T4				= new Date();
        callTime_T40			= new Date();
        callTime_T5				= new Date();
        callTime_T6				= new Date();
        callTime_T7				= new Date();
        callTime_T8				= new Date();
        expiresTI				= 0;
        expiresT0				= 0;
        expiresT00				= 0;
        expiresT1				= 0;
        expiresT2				= 0;
        expiresT3				= 0;
        expiresT4				= 0;
        expiresT5				= 0;
        expiresT6				= 0;
        expiresT7				= 0;
        expiresT8				= 0;

        //2012 03 23
        if(sdp!=null && sdp.flag==true && sdp.audioM!=null)
        {
            SIPStack.freeAudioRtpPort(sdp.audioM.mediaPort);
        }
        //
        sdp						= null;
        remoteSdp				= null;

        message					= null;
        bHolding				= false;
        bConference				= false;
        bCancelRequest  		= false;
        bRejectRequest  		= false;
        bNewcallRequest			= false;
        bUpdateRequest			= false;
        bCallPrepared 			= false;
        bDestroyActivity 		= false;
        number					= "";
        viaBranch				= null;//2013 02 13
        callDirection			= SIPStack.SIP_CALLDIRECTION_NONE;
        finalCode 				= 0;
        callDuration			= 0;
        finalResponseMessage	= null;
        bRelayCall 				= false;
        bVideoIframeMarked=false;

        return;
    }

    //
    public boolean activeCallHandle(
            String id,
            String cid,
            String authid,
            String authpassword,
            String number,
            String serverIp,
            int serverPort,
            String serverDomain,
            String localIp,
            int localPort
    )
    {
        if(this.flag==true) return false;

        resetCall();

        this.flag=true;
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("call handle activated.");
        this.id				= id;
        this.cid			= cid;
        this.authid			= authid;
        this.authpassword	= authpassword;
        this.dnis			= number;
        this.serverIp		= serverIp;
        this.serverPort		= serverPort;
        this.serverDomain	= serverDomain;
        this.remoteIp		= serverIp;
        this.remotePort		= serverPort;
        this.localIp		= localIp;
        this.localPort		= localPort;

        //2012 07 27
        callTime_TI				= new Date();
        callTime_T0				= new Date();
        callTime_T00			= new Date();
        invitingTimes			= 0;
        callTime_T1				= new Date();
        callTime_T2				= new Date();
        callTime_T3				= new Date();
        callTime_T4				= new Date();
        callTime_T40			= new Date();
        callTime_T5				= new Date();
        callTime_T6				= new Date();
        callTime_T7				= new Date();
        callTime_T8				= new Date();
        expiresTI				= 0;
        expiresT0				= 0;
        expiresT00				= 0;
        expiresT1				= 0;
        expiresT2				= 0;
        expiresT3				= 0;
        expiresT4				= 0;
        expiresT5				= 0;
        expiresT6				= 0;
        expiresT7				= 0;
        expiresT8				= 0;

        finalCode 				= 0;
        callDuration			= 0;

        //
        return true;
    }

    public boolean activeConferenceCallHandle(
            String id,
            String cid,
            String authid,
            String authpassword,
            String number,
            String serverIp,
            int serverPort,
            String serverDomain,
            String localIp,
            int localPort
    )
    {
        if(this.flag==true) return false;

        resetCall();

        this.flag			= true;
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("conference call activated.");
        this.id				= id;
        this.cid			= cid;
        this.authid			= authid;
        this.authpassword	= authpassword;
        this.dnis			= number;
        this.serverIp		= serverIp;
        this.serverPort		= serverPort;
        this.serverDomain	= serverDomain;
        this.remoteIp		= serverIp;
        this.remotePort		= serverPort;
        this.localIp		= localIp;
        this.localPort		= localPort;

        this.bConference	= true;

        //2012 07 27
        callTime_TI				= new Date();
        callTime_T0				= new Date();
        callTime_T00			= new Date();
        invitingTimes			= 0;
        callTime_T1				= new Date();
        callTime_T2				= new Date();
        callTime_T3				= new Date();
        callTime_T4				= new Date();
        callTime_T40			= new Date();
        callTime_T5				= new Date();
        callTime_T6				= new Date();
        callTime_T7				= new Date();
        callTime_T8				= new Date();
        expiresTI				= 0;
        expiresT0				= 0;
        expiresT00				= 0;
        expiresT1				= 0;
        expiresT2				= 0;
        expiresT3				= 0;
        expiresT4				= 0;
        expiresT5				= 0;
        expiresT6				= 0;
        expiresT7				= 0;
        expiresT8				= 0;
        //

        return true;
    }

    public boolean constructHeaders(String message,String remoteIp,int remotePort)
    {
        if(message==null || message.length()==0) return false;

        viaH					= null;
        viaArray				= new StringBuffer();
        routeH					= null;
        routeArray				= new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= new StringBuffer();

        this.message=message;

        StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);
        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();
            if(token.length()<=0) continue;
            if(token.compareTo("\r")==0) continue;
            if(token.compareTo("\n")==0) continue;
            if(token.startsWith("From: ")==true)
            {
                fromH=token;
                fromHeaderValue=token.substring(6);
            }
            else if(token.startsWith("f: ")==true) //2012 04 27
            {
                fromH=token;
                fromHeaderValue=token.substring(3);
            }
            else if(token.startsWith("To: ")==true)
            {
                toH=token;
                if(toH.indexOf("tag=")<0) {
                    toTag=""+new Date().getTime();
                    toH=token+";tag="+toTag;
                    toHeaderValue=token.substring(4)+";tag="+toTag;
                }
                else toHeaderValue=token.substring(4);
            }
            else if(token.startsWith("t: ")==true) //2012 04 27
            {
                toH=token;
                if(toH.indexOf("tag=")<0) {
                    toTag=""+new Date().getTime();
                    toH=token+";tag="+toTag;
                    toHeaderValue=token.substring(3)+";tag="+toTag;
                }
                else toHeaderValue=token.substring(3);
            }
            else if(token.startsWith("Via: ")==true)
            {
                if(viaH==null) {
                    viaH=token;
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch="";

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);

                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("v: ")==true) //2012 04 27
            {
                if(viaH==null) {
                    viaH=token;
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch=branch;

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);
                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("Record-Route: ")==true)
            {
                if(recordrouteH==null) {
                    routeH=null;
                    routeArray=new StringBuffer();
                    recordrouteH=token;
                    recordrouteArray.append(recordrouteH+SIPStack.SIP_LINE_END);
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
                else {
                    recordrouteArray.append(token+SIPStack.SIP_LINE_END);//2013 02 13
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
            }
            else if(token.startsWith("Contact: ")==true)
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                        //
                    }
                }
            }
            else if(token.startsWith("m: ")==true) //2012 04 27
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                        //
                    }
                }
            }
            else if(token.startsWith("Call-ID: ")==true)
            {
                callidH=token;
                callId=token.substring(9).trim();
            }
            else if(token.startsWith("i: ")==true) //2012 04 27
            {
                callidH=token;
                callId=token.substring(3).trim();
            }
            else if(token.startsWith("CSeq: ")==true)
            {
                cseqH=token;
                StringTokenizer fieldArray= new StringTokenizer(token," ",true);
                int tokenCount=0;
                while(fieldArray.hasMoreTokens())
                {
                    String fieldtoken=fieldArray.nextToken();

                    if(fieldtoken.length()>0 && fieldtoken.compareTo(" ")!=0)
                    {
                        tokenCount++;
                        if(tokenCount==2)
                        {
                            CSEQ_NUMBER=Integer.parseInt(fieldtoken);
                            break;
                        }
                    }
                }

            }
            else if(token.startsWith("Content-Type: ")==true)
            {
                contenttypeH=token;
            }
            else if(token.startsWith("c: ")==true) //2012 04 27
            {
                contenttypeH=token;
            }
            else if(token.startsWith("Content-Length: ")==true)
            {
                contentlengthH=token;
            }
            else if(token.startsWith("l: ")==true) //2012 04 27
            {
                contentlengthH=token;
            }
            else if(token.startsWith("User-Agent: ")==true)
            {
                remoteuseragentH=token;
                useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
            }
            else if(token.startsWith("P-Asserted-Identity: ")==true)
            {
                passertedidentityH=token;
            }
            else if(token.startsWith("Supported: ")==true)
            {
                supportedH=token;
                int pos=token.indexOf("relay");
                if(pos>0) this.bRelayCall=true;
                Log.i("CALL","+++++  Relay Call:"+this.bRelayCall);
            }

        }
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        maxforwardH="Max-Forwards: 70";

        //LOCAL SDP CONSTRUCT
        int audioport=SIPStack.getFreeAudioRtpPort();
        if(audioport>0) constructSdp();
        if(sdp != null && sdp.flag==true)
        {
            sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
            if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G729)
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G729,
                        "G729/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");

            }
            sdp.setCodec(
                    SIPStack.SIP_MEDIATYPE_AUDIO,
                    RFC2833.payloadType,
                    "telephone-event/8000");
            sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
        }

        //REMOTE SDP CONSTRUCT
        if(true==parseRemoteSdp(message))
        {

            if(
                    remoteSdp!=null && //2012 03 22
                            remoteSdp.flag==true)
            {
                if(
                        remoteSdp.audioM!=null && //2012 03 22
                                sdp != null && sdp.flag==true && sdp.audioM!=null && //2012 03 22
                                remoteSdp.audioM.flow != null &&
                                remoteSdp.audioM.flow.length()>0
                )
                {
                    if(remoteSdp.audioM.flow.compareTo("sendrecv")==0)
                    {
                        sdp.audioM.setFlow("sendrecv");
                    }
                    else if(remoteSdp.audioM.flow.compareTo("sendonly")==0)
                    {
                        sdp.audioM.setFlow("recvonly");
                    }
                    else if(remoteSdp.audioM.flow.compareTo("recvonly")==0)
                    {
                        sdp.audioM.setFlow("sendonly");
                    }
                }
            }
        }
        negotiateAudioCodec();
        return true;
    }
    public boolean constructUPDATEHeaders(
            String message,
            String remoteIp,
            int remotePort
    )
    {
        if(message==null || message.length()==0) return false;

        resetUpdateheaders();
        setUpdateheaders();

        viaH					= null;
        viaArray				= new StringBuffer();
        routeH					= null;
        routeArray				= new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= new StringBuffer();

        this.message=message;

        StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);
        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();
            if(token.length()<=0) continue;
            if(token.compareTo("\r")==0) continue;
            if(token.compareTo("\n")==0) continue;
            if(token.startsWith("From: ")==true)
            {
                fromH=token;
                int iS=fromH.indexOf(";tag=");
                if(iS>0)
                {
                    String str=null;
                    int iE=fromH.indexOf(";",iS+5);
                    if(iE>=0) {
                        if(iE>iS+5) str=fromH.substring(iS+5,iE);
                        else str="";
                    }
                    else str=fromH.substring(iS+5);
                    if(str != null && str.length()>0) fromTag=str.trim();
                }
                fromHeaderValue=token.substring(6);
                System.out.println("FROM TAG:"+fromTag);
            }
            else if(token.startsWith("f: ")==true) //2012 04 27
            {
                fromH=token;
                int iS=fromH.indexOf(";tag=");
                if(iS>0)
                {
                    String str=null;
                    int iE=fromH.indexOf(";",iS+5);
                    if(iE>=0) {
                        if(iE>iS+5) str=fromH.substring(iS+5,iE);
                        else str="";
                    }
                    else str=fromH.substring(iS+5);
                    if(str != null && str.length()>0) fromTag=str.trim();
                }
                fromHeaderValue=token.substring(3);
                System.out.println("FROM TAG:"+fromTag);
            }
            else if(token.startsWith("To: ")==true)
            {
                toH=token;
                int iS=toH.indexOf(";tag=");
                if(iS>0)
                {
                    String str=null;
                    int iE=toH.indexOf(";",iS+5);
                    if(iE>=0) {
                        if(iE>iS+5) str=toH.substring(iS+5,iE);
                        else str="";
                    }
                    else str=toH.substring(iS+5);
                    if(str != null && str.length()>0) toTag=str.trim();
                }
                toHeaderValue=token.substring(4);
                System.out.println("TO TAG:"+toTag);
            }
            else if(token.startsWith("To: ")==true) //2012 04 27
            {
                toH=token;
                int iS=toH.indexOf(";tag=");
                if(iS>0)
                {
                    String str=null;
                    int iE=toH.indexOf(";",iS+5);
                    if(iE>=0) {
                        if(iE>iS+5) str=toH.substring(iS+5,iE);
                        else str="";
                    }
                    else str=toH.substring(iS+5);
                    if(str != null && str.length()>0) toTag=str.trim();
                }
                toHeaderValue=token.substring(3);
                System.out.println("TO TAG:"+toTag);
            }
            else if(token.startsWith("Via: ")==true)
            {
                if(viaH==null) {
                    viaH=token;
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch="";

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);

                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("v: ")==true) //2012 04 27
            {
                if(viaH==null) {
                    viaH=token;
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch=branch;

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);
                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("Record-Route: ")==true)
            {
                if(recordrouteH==null) {
                    routeH=null;
                    routeArray=new StringBuffer();
                    recordrouteH=token;
                    recordrouteArray.append(recordrouteH+SIPStack.SIP_LINE_END);
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
                else {
                    recordrouteArray.append(token+SIPStack.SIP_LINE_END);//2013 02 13
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
            }
            else if(token.startsWith("Contact: ")==true)
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                    }
                }
            }
            else if(token.startsWith("m: ")==true) //2012 04 27
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                    }
                }
            }
            else if(token.startsWith("Call-ID: ")==true)
            {
                callidH=token;
                callId=token.substring(9).trim();
            }
            else if(token.startsWith("i: ")==true) //2012 04 27
            {
                callidH=token;
                callId=token.substring(3).trim();
            }
            else if(token.startsWith("CSeq: ")==true)
            {
                cseqH=token;
                StringTokenizer fieldArray= new StringTokenizer(token," ",true);
                int tokenCount=0;
                while(fieldArray.hasMoreTokens())
                {
                    String fieldtoken=fieldArray.nextToken();

                    if(fieldtoken.length()>0 && fieldtoken.compareTo(" ")!=0)
                    {
                        tokenCount++;
                        if(tokenCount==2)
                        {
                            CSEQ_NUMBER=Integer.parseInt(fieldtoken);
                            break;
                        }
                    }
                }

            }
            else if(token.startsWith("Content-Type: ")==true)
            {
                contenttypeH=token;
            }
            else if(token.startsWith("c: ")==true) //2012 04 27
            {
                contenttypeH=token;
            }
            else if(token.startsWith("Content-Length: ")==true)
            {
                contentlengthH=token;
            }
            else if(token.startsWith("l: ")==true) //2012 04 27
            {
                contentlengthH=token;
            }
            else if(token.startsWith("User-Agent: ")==true)
            {
                remoteuseragentH=token;
                useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
            }
            else if(token.startsWith("P-Asserted-Identity: ")==true)
            {
                passertedidentityH=token;
            }

        }
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        maxforwardH="Max-Forwards: 70";

        //LOCAL SDP CONSTRUCT
        //int audioport=SIPStack.getFreeAudioRtpPort(); original
        int audioport=updateHeaders.sdp.audioM.mediaPort; //2012 03 24
        if(audioport>0) constructSdp();
        if(sdp != null && sdp.flag==true)
        {
            sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
            if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G729)
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G729,
                        "G729/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else
            {
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");

            }
            sdp.setCodec(
                    SIPStack.SIP_MEDIATYPE_AUDIO,
                    RFC2833.payloadType,
                    "telephone-event/8000");
            sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
        }

        //REMOTE SDP CONSTRUCT
        if(true==parseRemoteSdp(message))
        {

            if(
                    remoteSdp!=null && //2012 03 22
                            remoteSdp.flag==true)
            {
                if(
                        remoteSdp.audioM!=null && //2012 03 22
                                sdp != null && sdp.flag==true && sdp.audioM!=null && //2012 03 22
                                remoteSdp.audioM.flow != null &&
                                remoteSdp.audioM.flow.length()>0
                )
                {
                    if(remoteSdp.audioM.flow.compareTo("sendrecv")==0)
                    {
                        sdp.audioM.setFlow("sendrecv");
                    }
                    else if(remoteSdp.audioM.flow.compareTo("sendonly")==0)
                    {
                        sdp.audioM.setFlow("recvonly");
                    }
                    else if(remoteSdp.audioM.flow.compareTo("recvonly")==0)
                    {
                        sdp.audioM.setFlow("sendonly");
                    }
                }
            }
        }
        negotiateAudioCodec();
        return true;
    }
    public void reverseRecordRoute(String message)
    {
        if(message==null || message.length()==0) return;

        recordrouteArray		= null;
        routeH					= null;

        StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);
        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();
            if(token.length()<=0) continue;
            if(token.compareTo("\r")==0) continue;
            if(token.compareTo("\n")==0) continue;

            if(token.startsWith("Record-Route: ")==true)
            {
                routeH=token.substring(7).trim();
                if(routeArray==null) {
                    routeArray=new StringBuffer();
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
                else if(routeArray!=null && routeArray.length()>0)
                    routeArray.insert(0,token.substring(7)+SIPStack.SIP_LINE_END);
                else if(routeArray!=null)
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("Contact: ")==true)
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                        //
                    }
                }
            }
            else if(token.startsWith("m: ")==true) //2012 04 27
            {
                contactH=token;
                if(token.length()>0)
                {
                    int iS=0;
                    iS=token.indexOf("sip:");
                    if(iS>0)
                    {
                        String str=null;
                        int iE=0;
                        iE=token.indexOf(">",iS);
                        if(iE>0)
                        {
                            str=token.substring(iS,iE);
                        }
                        else str=token.substring(iS);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(";");
                        if(iE>0) str=str.substring(0,iE);
                        iE=str.indexOf(" ");
                        if(iE>0) str=str.substring(0,iE);
                        //GET REMOTE CONTACT URI
                        if(str.length()>0) {
                            remoteContactUri=str;
                        }
                        if(str.length()>0)
                        {
                            //GET REMOTE CONTACT IP
                            iS=str.indexOf("@");
                            if(iS>0) str=str.substring(iS+1);
                            else str=str.substring(4);
                            int iM=str.indexOf(":",iS);
                            if(iM<0) {
                                remoteContactIp=str;
                                remoteContactPort=5050;
                            }
                            else {
                                remoteContactIp=str.substring(0,iM).trim();
                                remoteContactPort=Integer.parseInt(str.substring(iM+1).trim());
                            }
                        }
                        //
                    }
                }
            }

        }//while

        return;

    }
    //2012 01 20
    public String getViaArray(String message)
    {
        if(message==null || message.length()==0) return null;

        StringBuffer viaArray	= new StringBuffer();
        String viaH					= null;

        StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);
        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();
            if(token.length()<=0) continue;
            if(token.compareTo("\r")==0) continue;
            if(token.compareTo("\n")==0) continue;

            if(token.startsWith("Via: ")==true)
            {
                if(viaH==null) {
                    viaH=token;
                    //original
                    //int iS=token.indexOf(";rport=");
                    //if(iS>0 && token.indexOf(";received=")<0)
                    //{
                    //	viaH=token.substring(0,iS)+";received="+remoteIp+";rport="+remotePort;
                    //}
                    //viaArray.append(viaH+SIPStack.SIP_LINE_END);
                    //2013 02 13
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch="";

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);

                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("v: ")==true) //2012 04 27
            {
                if(viaH==null) {
                    viaH=token;
                    if(token.indexOf(";received=")<0)
                    {
                        String branch="";
                        int index=token.indexOf(";branch=");
                        if(index>0) branch=token.substring(index+8);
                        if(branch!=null && branch.length()>0) {
                            index=branch.indexOf(";");
                            if(index>0) this.viaBranch=branch.substring(0,index);
                            else this.viaBranch=branch;
                        }
                        else this.viaBranch=branch;

                        index=token.indexOf(";branch=");
                        if(index>0) viaH=token.substring(0,index);
                        else viaH=token;
                        index=viaH.indexOf(";rport=");
                        if(index>0) viaH=viaH.substring(0,index);

                        viaH=viaH+";received="+remoteIp+";rport="+remotePort+";branch="+this.viaBranch;
                    }
                    viaArray.append(viaH+SIPStack.SIP_LINE_END);
                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }


        }//while

        return viaArray.toString();

    }
    public boolean constructSdp()
    {
        if(this.sdp != null && this.sdp.flag==true) return false;
        //2014 11 19
        if(SIPStack.bBlindSdp==true)
        {
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            {
                this.sdp=new SIPSdp(dnis,ifIp);
            }
            else
            {
                if(SIPStack.localSdpIp!=null && SIPStack.localSdpIp.length()>0 && SIPStack.usePrivateWiMax==true)
                {
                    this.sdp=new SIPSdp(dnis,localIp,SIPStack.localSdpIp);
                }
                else this.sdp=new SIPSdp(dnis,localIp);
            }
        }
        //
        else
        {
            if(SIPStack.localSdpIp!=null && SIPStack.localSdpIp.length()>0 && SIPStack.usePrivateWiMax==true)
            {
                this.sdp=new SIPSdp(dnis,localIp,SIPStack.localSdpIp);
            }
            else this.sdp=new SIPSdp(dnis,localIp);
        }
        //
        if(this.sdp!= null && this.sdp.flag==true) return true;

        return false;
    }

    public boolean parseRemoteSdp(String message)
    {
        if(message==null || message.length()==0) return false;
        boolean bContinueParsing=true;
        //STEP 1: Content-Type:
        int iS=0;
        int iE=0;
        String header=null;
        if(bContinueParsing==true)
        {
            bContinueParsing=false;
            iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Type: ");
            if(iS>0)
            {
                iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                if(iS>0 && iE>0)
                {
                    header=message.substring(iS+2,iE);
                    String headerValue=null;
                    if(header!=null && header.length()>0) headerValue=header.substring(14);
                    if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/sdp")==0)
                    {
                        bContinueParsing=true;
                    }
                }

            }
            else //2012 04 27
            {
                iS=message.indexOf(SIPStack.SIP_LINE_END+"c: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(3);
                        if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/sdp")==0)
                        {
                            bContinueParsing=true;
                        }
                    }

                }

            }


        }
        //STPE 2: Content-Length:
        iS=0;
        iE=0;
        header=null;
        int contentLength=0;
        if(bContinueParsing==true)
        {
            bContinueParsing=false;
            iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Length: ");
            if(iS>0)
            {
                iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                if(iS>0 && iE>0)
                {
                    header=message.substring(iS+2,iE);
                    String headerValue=null;
                    if(header!=null && header.length()>0) headerValue=header.substring(16);
                    if(headerValue!=null && headerValue.length()>0 )
                    {
                        contentLength=Integer.parseInt(headerValue);
                        bContinueParsing=true;
                    }
                }

            }
            else {//2012 04 27
                iS=message.indexOf(SIPStack.SIP_LINE_END+"l: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(3);
                        if(headerValue!=null && headerValue.length()>0 )
                        {
                            contentLength=Integer.parseInt(headerValue);
                            bContinueParsing=true;
                        }
                    }

                }

            }

        }

        //STEP 3: \r\n\r\nv=0\r\n
        iS=0;
        iE=0;
        header=null;
        String body=null;

        if(bContinueParsing==true)
        {
            bContinueParsing=false;
            iS=message.indexOf(SIPStack.SIP_LINE_DOUBLEEND+"v=0"+SIPStack.SIP_LINE_END);
            if(iS>0)
            {
                body=message.substring(iS+4);
                if(body!=null && body.length()>=contentLength )
                {
                    bContinueParsing=true;
                }
            }
        }

        //
        if(bContinueParsing==true)
        {
            this.remoteSdp=new SIPSdp(body);
            if(this.remoteSdp!= null && this.remoteSdp.flag==true) return true;
            //

        }
        return false;

    }
    public boolean parseDtmfSdp(String message,int dtmftype)
    {
        if(message==null || message.length()==0) return false;
        boolean bContinueParsing=true;
        //STEP 1: Content-Type:
        int iS		= 0;
        int iE		= 0;
        int dtmf	= 0;
        bDtmfdetected=false;

        String header=null;

        if(dtmftype==SIPStack.SIP_DTMFINFO_DTMFRELAY)
        {
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Type: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(14);
                        if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/dtmf-relay")==0)
                        {
                            bContinueParsing=true;
                        }
                    }

                }
                else {//2012 04 27
                    iS=message.indexOf(SIPStack.SIP_LINE_END+"c: ");
                    if(iS>0)
                    {
                        iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                        if(iS>0 && iE>0)
                        {
                            header=message.substring(iS+2,iE);
                            String headerValue=null;
                            if(header!=null && header.length()>0) headerValue=header.substring(3);
                            if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/dtmf-relay")==0)
                            {
                                bContinueParsing=true;
                            }
                        }

                    }

                }

            }
            //STPE 2: Content-Length:
            iS=0;
            iE=0;
            header=null;
            int contentLength=0;
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Length: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(16);
                        if(headerValue!=null && headerValue.length()>0 )
                        {
                            contentLength=Integer.parseInt(headerValue);
                            bContinueParsing=true;
                        }
                    }

                }
                else {//2012 04 27
                    iS=message.indexOf(SIPStack.SIP_LINE_END+"l: ");
                    if(iS>0)
                    {
                        iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                        if(iS>0 && iE>0)
                        {
                            header=message.substring(iS+2,iE);
                            String headerValue=null;
                            if(header!=null && header.length()>0) headerValue=header.substring(3);
                            if(headerValue!=null && headerValue.length()>0 )
                            {
                                contentLength=Integer.parseInt(headerValue);
                                bContinueParsing=true;
                            }
                        }

                    }

                }

            }

            //STEP 3: \r\n\r\nv=0\r\n
            iS=0;
            iE=0;
            header=null;
            String body=null;

            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_DOUBLEEND+"Signal=");
                if(iS>0)
                {
                    body=message.substring(iS+4);
                    if(body!=null && body.length()>=contentLength )
                    {
                        bContinueParsing=true;
                    }
                }
            }

            //
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                //
                String sH				= null;

                //GET signal
                iS=body.indexOf("Signal=");
                if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
                if(iS>=0 && iE>iS)
                {
                    sH =body.substring(iS+7,iE).trim();
                    dtmf=Integer.parseInt(sH);
                    if(dtmf>=0 && dtmf<12) {
                        bContinueParsing=true;
                        detectedDtmf=dtmf;
                        bDtmfdetected=true;
                        dtmfDuration=0;

                    }
                }
                if(bContinueParsing==true)
                {
                    //GET duration
                    iS=body.indexOf("Duration=");
                    if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
                    if(iS>=0 && iE>iS)
                    {
                        sH =body.substring(iS+9,iE).trim();
                        dtmfDuration=Integer.parseInt(sH);
                    }
                    //
                }
            }

            return bDtmfdetected;

        }
        else if(dtmftype==SIPStack.SIP_DTMFINFO_DTMF)
        {
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Type: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(14);
                        if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/dtmf")==0)
                        {
                            bContinueParsing=true;
                        }
                    }

                }
                else { //2012 04 27
                    iS=message.indexOf(SIPStack.SIP_LINE_END+"c: ");
                    if(iS>0)
                    {
                        iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                        if(iS>0 && iE>0)
                        {
                            header=message.substring(iS+2,iE);
                            String headerValue=null;
                            if(header!=null && header.length()>0) headerValue=header.substring(3);
                            if(headerValue!=null && headerValue.length()>0 && headerValue.toLowerCase().compareTo("application/dtmf")==0)
                            {
                                bContinueParsing=true;
                            }
                        }

                    }

                }

            }
            //STPE 2: Content-Length:
            iS=0;
            iE=0;
            header=null;
            int contentLength=0;
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_END+"Content-Length: ");
                if(iS>0)
                {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                    if(iS>0 && iE>0)
                    {
                        header=message.substring(iS+2,iE);
                        String headerValue=null;
                        if(header!=null && header.length()>0) headerValue=header.substring(16);
                        if(headerValue!=null && headerValue.length()>0 )
                        {
                            contentLength=Integer.parseInt(headerValue);
                            bContinueParsing=true;
                        }
                    }

                }
                else {//2012 04 27
                    iS=message.indexOf(SIPStack.SIP_LINE_END+"l: ");
                    if(iS>0)
                    {
                        iE=message.indexOf(SIPStack.SIP_LINE_END,iS+2);
                        if(iS>0 && iE>0)
                        {
                            header=message.substring(iS+2,iE);
                            String headerValue=null;
                            if(header!=null && header.length()>0) headerValue=header.substring(3);
                            if(headerValue!=null && headerValue.length()>0 )
                            {
                                contentLength=Integer.parseInt(headerValue);
                                bContinueParsing=true;
                            }
                        }

                    }

                }

            }

            //STEP 3: \r\n\r\nv=0\r\n
            iS=0;
            iE=0;
            header=null;
            String body=null;

            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                iS=message.indexOf(SIPStack.SIP_LINE_DOUBLEEND);
                if(iS>0)
                {
                    body=message.substring(iS+4);
                    if(body!=null && body.length()>=contentLength )
                    {
                        bContinueParsing=true;
                    }
                }
            }

            //
            if(bContinueParsing==true)
            {
                bContinueParsing=false;
                //
                String sH				= null;

                //GET signal
                iS=0;
                iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
                if(iE>iS)
                {
                    sH =body.substring(iS,iE).trim();
                    dtmf=Integer.parseInt(sH);
                    if(dtmf>=0 && dtmf<12) {
                        bContinueParsing=true;
                        detectedDtmf=dtmf;
                        bDtmfdetected=true;
                        dtmfDuration=0;

                    }
                }
            }

            return bDtmfdetected;

        }
        //

        return false;

    }

    public boolean negotiateAudioCodec()
    {
        if(
                sdp!=null && sdp.flag==true && sdp.audioM!=null && sdp.audioM.flag==true &&
                        remoteSdp!=null && remoteSdp.flag==true && remoteSdp.audioM!=null &&
                        remoteSdp.audioM.flag==true
        )
        {
            if(callDirection ==SIPStack.SIP_CALLDIRECTION_OUT)
            {
                int finalCodec=remoteSdp.audioM.negotiateAudioCodec(sdp.audioM.codecS.toString());
                if(finalCodec>=0) {
                    sdp.audioM.setCommonCodec(finalCodec);
                    return true;
                }
            }
            else if(callDirection ==SIPStack.SIP_CALLDIRECTION_IN)
            {
                int finalCodec=sdp.audioM.negotiateAudioCodec(remoteSdp.audioM.codecS.toString());
                if(finalCodec>=0) {
                    remoteSdp.audioM.setCommonCodec(finalCodec);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean negotiateVideoCodec()
    {
        return true;
    }

    //CALL EXCEPTION PROCESSING
    public void exceptionTI()//IDLE
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("IDLE CALL EXCEPTION occurred.");
        resetCall();
        return;
    }
    public void exceptionT0()//INVITE
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("INVITING CALL EXCEPTION occurred.");
        resetCall();
        return;
    }
    public void exceptionT1()//PROCEEDING
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("PROCEEDING CALL EXCEPTION occurred.");
        return;
    }
    public void exceptionT2()//PROGRESSING
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("PROGRESSING CALL EXCEPTION occurred.");
        return;
    }
    public void exceptionT3()//ACCEPTED
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("ACCEPTED CALL EXCEPTION occurred.");
        return;
    }
    public void exceptionT4()//CONNECTED
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("CONNECTED CALL EXCEPTION occurred.");
        return;
    }
    public void exceptionT5()//DISCONNECTING
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("DISCONNECTING CALL EXCEPTION occurred.");
        resetCall();
        return;
    }
    public void exceptionT6()//TERMINATING
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("TERMINATING CALL EXCEPTION occurred.");
        resetCall();
        return;
    }
    public void exceptionT7()//OFFERRED
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("OFFERRED CALL EXCEPTION occurred.");
        return;
    }
    public void exceptionT8()//CANCELLING
    {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("CANCELLING CALL EXCEPTION occurred.");
        resetCall();
        return;
    }

    //UPDATEHEADERS update
    public void setUpdateheaders()
    {
        if(updateHeaders==null) return;

        updateHeaders.commandLine			= this.commandLine;
        updateHeaders.viaH					= this.viaH;
        updateHeaders.viaArray				= this.viaArray;
        updateHeaders.routeH				= this.routeH;
        updateHeaders.routeArray 			= this.routeArray;
        updateHeaders.recordrouteH			= this.recordrouteH;
        updateHeaders.recordrouteArray		= this.recordrouteArray;
        updateHeaders.maxforwardH			= this.maxforwardH;
        updateHeaders.contactH				= this.contactH;
        updateHeaders.toH					= this.toH;
        updateHeaders.fromH					= this.fromH;
        updateHeaders.callidH				= this.callidH;
        updateHeaders.cseqH					= this.cseqH;
        updateHeaders.expiresH				= this.expiresH;
        updateHeaders.allowH				= this.allowH;
        updateHeaders.useragentH			= this.useragentH;
        updateHeaders.remoteuseragentH		= this.remoteuseragentH;
        updateHeaders.contentlengthH		= this.contentlengthH;
        updateHeaders.contenttypeH			= this.contenttypeH;
        updateHeaders.authorizationINVITEH	= this.authorizationINVITEH;
        updateHeaders.authorizationCANCELH	= this.authorizationCANCELH;
        updateHeaders.authorizationBYEH		= this.authorizationBYEH;
        updateHeaders.authorizationACKH		= this.authorizationACKH;
        updateHeaders.passertedidentityH	= this.passertedidentityH;
        updateHeaders.serverIp				= this.serverIp;
        updateHeaders.serverPort			= this.serverPort;
        updateHeaders.serverDomain			= this.serverDomain;
        updateHeaders.remoteIp				= this.remoteIp;
        updateHeaders.remotePort			= this.remotePort;
        updateHeaders.remoteContactIp		= this.remoteContactIp;
        updateHeaders.remoteContactPort		= this.remoteContactPort;
        updateHeaders.remoteContactUri		= this.remoteContactUri;
        updateHeaders.localIp				= this.localIp;
        updateHeaders.localPort				= this.localPort;
        updateHeaders.id					= this.id;
        updateHeaders.cid					= this.cid;
        updateHeaders.authid				= this.authid;
        updateHeaders.authpassword			= this.authpassword;
        updateHeaders.fromTag				= this.fromTag;
        updateHeaders.toTag					= this.toTag;
        updateHeaders.fromHeaderValue		= this.fromHeaderValue;
        updateHeaders.toHeaderValue			= this.toHeaderValue;

        updateHeaders.callId				= this.callId;
        updateHeaders.dnis					= this.dnis;
        updateHeaders.CSEQ_NUMBER			= this.CSEQ_NUMBER;
        updateHeaders.INVITE_CSEQ			= this.INVITE_CSEQ;
        updateHeaders.CANCEL_CSEQ			= this.CANCEL_CSEQ;
        updateHeaders.ACK_CSEQ				= this.ACK_CSEQ;
        updateHeaders.BYE_CSEQ				= this.BYE_CSEQ;
        updateHeaders.callDirection			= this.callDirection;
        updateHeaders.viaBranch				= this.viaBranch;

        updateHeaders.callState				= this.callState;
        updateHeaders.callMode				= this.callMode;
        updateHeaders.callTime_TI			= this.callTime_TI;//idle timer
        updateHeaders.callTime_T0			= this.callTime_T0;//invite timer
        updateHeaders.callTime_T00			= this.callTime_T00;//2012 11 29
        updateHeaders.invitingTimes			= this.invitingTimes;
        updateHeaders.callTime_T1			= this.callTime_T1;//proceeding timer
        updateHeaders.callTime_T2			= this.callTime_T2;//progressing timer
        updateHeaders.callTime_T3			= this.callTime_T3;//accepted timer
        updateHeaders.callTime_T4			= this.callTime_T4;//connected timer
        updateHeaders.callTime_T40			= this.callTime_T40;//connected duration report timer
        updateHeaders.callTime_T5			= this.callTime_T5;//disconnecting timer
        updateHeaders.callTime_T6			= this.callTime_T6;//terminating timer
        updateHeaders.callTime_T7			= this.callTime_T7;//offerred timer
        updateHeaders.callTime_T8			= this.callTime_T8;//cancelling timer

        updateHeaders.expiresTI				= this.expiresTI;//idle timer
        updateHeaders.expiresT0				= this.expiresT0;//invite timer
        updateHeaders.expiresT1				= this.expiresT1;//proceeding timer
        updateHeaders.expiresT2				= this.expiresT2;//progressing timer
        updateHeaders.expiresT3				= this.expiresT3;//accepted timer
        updateHeaders.expiresT4				= this.expiresT4;//connected timer
        updateHeaders.expiresT5				= this.expiresT5;//disconnecting timer
        updateHeaders.expiresT6				= this.expiresT6;//terminating timer
        updateHeaders.expiresT7				= this.expiresT7;//offerred timer
        updateHeaders.expiresT8				= this.expiresT8;//cancelling timer

        //SDP
        updateHeaders.sdp					= this.sdp;
        updateHeaders.remoteSdp				= this.remoteSdp;
        //

        updateHeaders.message				= this.message;
        //
        commandLine				= null;
        viaH					= null;
        viaArray				= null;//new StringBuffer();
        routeH					= null;
        routeArray				= null;//new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= null;//new StringBuffer();
        maxforwardH				= null;
        contactH				= null;
        toH						= null;
        fromH					= null;
        callidH					= null;
        cseqH					= null;
        expiresH				= null;
        allowH					= null;
        useragentH				= null;
        remoteuseragentH		= null;
        contentlengthH			= null;
        contenttypeH			= null;
        authorizationINVITEH	= null;
        authorizationCANCELH	= null;
        authorizationBYEH		= null;
        authorizationACKH		= null;

        passertedidentityH		= null;
        fromTag					= null;
        toTag					= null;
        callId					= null;
        viaBranch				= null;//2013 02 13

        sdp						= null;
        remoteSdp				= null;
        //

        message					= null;

        updateHeaders.flag		= true;

    }
    public void setUpdateheaders(String flow)
    {
        if(updateHeaders==null) return;

        updateHeaders.commandLine			= this.commandLine;
        updateHeaders.viaH					= this.viaH;
        updateHeaders.viaArray				= this.viaArray;
        updateHeaders.routeH				= this.routeH;
        updateHeaders.routeArray 			= this.routeArray;
        updateHeaders.recordrouteH			= this.recordrouteH;
        updateHeaders.recordrouteArray		= this.recordrouteArray;
        updateHeaders.maxforwardH			= this.maxforwardH;
        updateHeaders.contactH				= this.contactH;
        updateHeaders.toH					= this.toH;
        updateHeaders.fromH					= this.fromH;
        updateHeaders.callidH				= this.callidH;
        updateHeaders.cseqH					= this.cseqH;
        updateHeaders.expiresH				= this.expiresH;
        updateHeaders.allowH				= this.allowH;
        updateHeaders.useragentH			= this.useragentH;
        updateHeaders.remoteuseragentH		= this.remoteuseragentH;
        updateHeaders.contentlengthH		= this.contentlengthH;
        updateHeaders.contenttypeH			= this.contenttypeH;
        updateHeaders.authorizationINVITEH	= this.authorizationINVITEH;
        updateHeaders.authorizationCANCELH	= this.authorizationCANCELH;
        updateHeaders.authorizationBYEH		= this.authorizationBYEH;
        updateHeaders.authorizationACKH		= this.authorizationACKH;
        updateHeaders.passertedidentityH	= this.passertedidentityH;
        updateHeaders.serverIp				= this.serverIp;
        updateHeaders.serverPort			= this.serverPort;
        updateHeaders.serverDomain			= this.serverDomain;
        updateHeaders.remoteIp				= this.remoteIp;
        updateHeaders.remotePort			= this.remotePort;
        updateHeaders.remoteContactIp		= this.remoteContactIp;
        updateHeaders.remoteContactPort		= this.remoteContactPort;
        updateHeaders.remoteContactUri		= this.remoteContactUri;
        updateHeaders.localIp				= this.localIp;
        updateHeaders.localPort				= this.localPort;
        updateHeaders.id					= this.id;
        updateHeaders.cid					= this.cid;
        updateHeaders.authid				= this.authid;
        updateHeaders.authpassword			= this.authpassword;
        updateHeaders.fromTag				= this.fromTag;
        updateHeaders.toTag					= this.toTag;
        updateHeaders.fromHeaderValue		= this.fromHeaderValue;
        updateHeaders.toHeaderValue			= this.toHeaderValue;

        updateHeaders.callId				= this.callId;
        updateHeaders.dnis					= this.dnis;
        updateHeaders.CSEQ_NUMBER			= this.CSEQ_NUMBER;
        updateHeaders.INVITE_CSEQ			= this.INVITE_CSEQ;
        updateHeaders.CANCEL_CSEQ			= this.CANCEL_CSEQ;
        updateHeaders.ACK_CSEQ				= this.ACK_CSEQ;
        updateHeaders.BYE_CSEQ				= this.BYE_CSEQ;
        updateHeaders.callDirection			= this.callDirection;
        updateHeaders.viaBranch				= this.viaBranch;//2013 02 13

        updateHeaders.callState				= this.callState;
        updateHeaders.callMode				= this.callMode;
        updateHeaders.callTime_TI			= this.callTime_TI;//idle timer
        updateHeaders.callTime_T0			= this.callTime_T0;//invite timer
        updateHeaders.callTime_T00			= this.callTime_T00;//invite timer
        updateHeaders.invitingTimes			= this.invitingTimes;
        updateHeaders.callTime_T1			= this.callTime_T1;//proceeding timer
        updateHeaders.callTime_T2			= this.callTime_T2;//progressing timer
        updateHeaders.callTime_T3			= this.callTime_T3;//accepted timer
        updateHeaders.callTime_T4			= this.callTime_T4;//connected timer
        updateHeaders.callTime_T40			= this.callTime_T40;//connected duration report timer
        updateHeaders.callTime_T5			= this.callTime_T5;//disconnecting timer
        updateHeaders.callTime_T6			= this.callTime_T6;//terminating timer
        updateHeaders.callTime_T7			= this.callTime_T7;//offerred timer
        updateHeaders.callTime_T8			= this.callTime_T8;//cancelling timer

        updateHeaders.expiresTI				= this.expiresTI;//idle timer
        updateHeaders.expiresT0				= this.expiresT0;//invite timer
        updateHeaders.expiresT1				= this.expiresT1;//proceeding timer
        updateHeaders.expiresT2				= this.expiresT2;//progressing timer
        updateHeaders.expiresT3				= this.expiresT3;//accepted timer
        updateHeaders.expiresT4				= this.expiresT4;//connected timer
        updateHeaders.expiresT5				= this.expiresT5;//disconnecting timer
        updateHeaders.expiresT6				= this.expiresT6;//terminating timer
        updateHeaders.expiresT7				= this.expiresT7;//offerred timer
        updateHeaders.expiresT8				= this.expiresT8;//cancelling timer

        //SDP
        updateHeaders.sdp					= this.sdp;
        updateHeaders.remoteSdp				= this.remoteSdp;
        //

        updateHeaders.message				= this.message;
        //
        commandLine				= null;
        viaH					= null;
        viaArray				= null;//new StringBuffer();
        recordrouteH			= null;
        recordrouteArray		= null;//new StringBuffer();
        maxforwardH				= null;
        contactH				= null;
        if(updateHeaders.callDirection==SIPStack.SIP_CALLDIRECTION_IN)
        {
            if(updateHeaders.fromH!=null && updateHeaders.fromH.length()>5) //2012 03 22
            {
                if(updateHeaders.fromH.startsWith("f: ")==true) //2012 04 27
                {
                    toH="t:"+updateHeaders.fromH.substring(2);
                }
                else toH="To:"+updateHeaders.fromH.substring(5);
            }
            if(updateHeaders.toH!=null && updateHeaders.toH.length()>3) //2012 03 22
            {
                if(updateHeaders.toH.startsWith("t: ")==true) //2012 04 27
                {
                    fromH="From:"+updateHeaders.toH.substring(2);
                }
                else fromH="From:"+updateHeaders.toH.substring(3);
            }
            fromTag					= updateHeaders.toTag;
            toTag					= updateHeaders.fromTag;

        }
        cseqH					= null;
        expiresH				= null;
        allowH					= null;
        useragentH				= null;
        remoteuseragentH		= null;
        contentlengthH			= null;
        contenttypeH			= null;
        viaBranch				= null;
        if(updateHeaders.callDirection==SIPStack.SIP_CALLDIRECTION_IN)
        {
            authorizationINVITEH	= null;
            authorizationCANCELH	= null;
            authorizationBYEH		= null;
            authorizationACKH		= null;
        }
        passertedidentityH		= null;
        callState				= SIPStack.SIP_CALLSTATE_UPDATING;
        callMode				= SIPStack.SIP_CALLMODE_HOLD;

        callTime_TI				= new Date();
        callTime_T0				= new Date();
        callTime_T00			= new Date();
        invitingTimes			= 0;
        callTime_T1				= new Date();
        callTime_T2				= new Date();
        callTime_T3				= new Date();
        callTime_T4				= new Date();
        callTime_T40			= new Date();
        callTime_T5				= new Date();
        callTime_T6				= new Date();
        callTime_T7				= new Date();
        callTime_T8				= new Date();
        expiresTI				= 0;
        expiresT0				= 0;
        expiresT1				= 0;
        expiresT2				= 0;
        expiresT3				= 0;
        expiresT4				= 0;
        expiresT5				= 0;
        expiresT6				= 0;
        expiresT7				= 0;
        expiresT8				= 0;

        sdp						= null;
        remoteSdp				= null;
        //LOCAL SDP CONSTRUCT
        int audioport=updateHeaders.sdp.audioM.mediaPort;
        if(audioport>0) constructSdp();
        if(sdp != null && sdp.flag==true)
        {
            sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
            if(sdp.audioM!=null)//2012 03 22
            {
                if(updateHeaders.sdp.audioM.commonCodec==SIPStack.SIP_CODEC_G711U)
                {
                    sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");
                }
                else if(updateHeaders.sdp.audioM.commonCodec==SIPStack.SIP_CODEC_G711A)
                {
                    sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");

                }
                sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        RFC2833.payloadType,
                        "telephone-event/8000");
                sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
                sdp.audioM.setFlow(flow);

            }
        }

        message					= null;
        finalResponseMessage	= null;
        updateHeaders.flag		= true;

    }
    //UPDATEHEADERS restore
    public void restoreUpdateheaders()
    {
        if(updateHeaders==null || updateHeaders.flag==false) return;

        this.commandLine			= updateHeaders.commandLine;
        this.viaH					= updateHeaders.viaH;
        this.viaArray				= updateHeaders.viaArray;
        this.routeH					= updateHeaders.routeH;
        this.routeArray 			= updateHeaders.routeArray;
        this.recordrouteH			= updateHeaders.recordrouteH;
        this.recordrouteArray		= updateHeaders.recordrouteArray;
        this.maxforwardH			= updateHeaders.maxforwardH;
        this.contactH				= updateHeaders.contactH;
        this.toH					= updateHeaders.toH;
        this.fromH					= updateHeaders.fromH;
        this.callidH				= updateHeaders.callidH;
        this.cseqH					= updateHeaders.cseqH;
        this.expiresH				= updateHeaders.expiresH;
        this.allowH					= updateHeaders.allowH;
        this.useragentH				= updateHeaders.useragentH;
        this.remoteuseragentH		= updateHeaders.remoteuseragentH;
        this.contentlengthH			= updateHeaders.contentlengthH;
        this.contenttypeH			= updateHeaders.contenttypeH;
        this.authorizationINVITEH	= updateHeaders.authorizationINVITEH;
        this.authorizationCANCELH	= updateHeaders.authorizationCANCELH;
        this.authorizationBYEH		= updateHeaders.authorizationBYEH;
        this.authorizationACKH		= updateHeaders.authorizationACKH;
        this.passertedidentityH		= updateHeaders.passertedidentityH;
        this.serverIp				= updateHeaders.serverIp;
        this.serverPort				= updateHeaders.serverPort;
        this.serverDomain			= updateHeaders.serverDomain;
        this.remoteIp				= updateHeaders.remoteIp;
        this.remotePort				= updateHeaders.remotePort;
        this.remoteContactIp		= updateHeaders.remoteContactIp;
        this.remoteContactPort		= updateHeaders.remoteContactPort;
        this.remoteContactUri		= updateHeaders.remoteContactUri;
        this.localIp				= updateHeaders.localIp;
        this.localPort				= updateHeaders.localPort;
        this.id						= updateHeaders.id;
        this.cid					= updateHeaders.cid;
        this.authid					= updateHeaders.authid;
        this.authpassword			= updateHeaders.authpassword;
        this.fromTag				= updateHeaders.fromTag;
        this.toTag					= updateHeaders.toTag;
        this.fromHeaderValue		= updateHeaders.fromHeaderValue;
        this.toHeaderValue			= updateHeaders.toHeaderValue;

        this.callId					= updateHeaders.callId;
        this.dnis					= updateHeaders.dnis;
        this.CSEQ_NUMBER			= updateHeaders.CSEQ_NUMBER;
        this.INVITE_CSEQ			= updateHeaders.INVITE_CSEQ;
        this.CANCEL_CSEQ			= updateHeaders.CANCEL_CSEQ;
        this.ACK_CSEQ				= updateHeaders.ACK_CSEQ;
        this.BYE_CSEQ				= updateHeaders.BYE_CSEQ;
        this.callDirection			= updateHeaders.callDirection;
        this.viaBranch				= updateHeaders.viaBranch;

        this.callState				= updateHeaders.callState;
        this.callMode				= updateHeaders.callMode;
        this.callTime_TI			= updateHeaders.callTime_TI;//idle timer
        this.callTime_T0			= updateHeaders.callTime_T0;//invite timer
        this.callTime_T00			= updateHeaders.callTime_T00;//2012 11 29
        this.invitingTimes			= updateHeaders.invitingTimes;
        this.callTime_T1			= updateHeaders.callTime_T1;//proceeding timer
        this.callTime_T2			= updateHeaders.callTime_T2;//progressing timer
        this.callTime_T3			= updateHeaders.callTime_T3;//accepted timer
        this.callTime_T4			= updateHeaders.callTime_T4;//connected timer
        this.callTime_T40			= updateHeaders.callTime_T40;//connected duration report timer
        this.callTime_T5			= updateHeaders.callTime_T5;//disconnecting timer
        this.callTime_T6			= updateHeaders.callTime_T6;//terminating timer
        this.callTime_T7			= updateHeaders.callTime_T7;//offerred timer
        this.callTime_T8			= updateHeaders.callTime_T8;//cancelling timer

        this.expiresTI				= updateHeaders.expiresTI;//idle timer
        this.expiresT0				= updateHeaders.expiresT0;//invite timer
        this.expiresT1				= updateHeaders.expiresT1;//proceeding timer
        this.expiresT2				= updateHeaders.expiresT2;//progressing timer
        this.expiresT3				= updateHeaders.expiresT3;//accepted timer
        this.expiresT4				= updateHeaders.expiresT4;//connected timer
        this.expiresT5				= updateHeaders.expiresT5;//disconnecting timer
        this.expiresT6				= updateHeaders.expiresT6;//terminating timer
        this.expiresT7				= updateHeaders.expiresT7;//offerred timer
        this.expiresT8				= updateHeaders.expiresT8;//cancelling timer

        //SDP
        this.sdp					= updateHeaders.sdp;
        this.remoteSdp				= updateHeaders.remoteSdp;
        //

        this.message				= updateHeaders.message;
        updateHeaders.flag					= false;

    }
    public void resetUpdateheaders()
    {
        if(updateHeaders==null || updateHeaders.flag==false) return;
        updateHeaders.reset();
        return;
    }
    //


}//class SIPCall
