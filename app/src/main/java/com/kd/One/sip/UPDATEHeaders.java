package com.kd.One.sip;

import java.util.Date;

class UPDATEHeaders {

    String commandLine				= null;
    String viaH						= null;
    StringBuffer viaArray			= null;
    String routeH					= null;
    StringBuffer routeArray 		= null;
    String recordrouteH				= null;
    StringBuffer recordrouteArray	= null;
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

    int callState					= SIPStack.SIP_CALLSTATE_IDLE;
    int callMode					= SIPStack.SIP_CALLMODE_NONE;
    Date callTime_TI				= null;//idle timer
    Date callTime_T0				= null;//invite timer
    Date callTime_T00				= null;//invite timer
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
    int expiresT00					= 0;//invite timer
    int expiresT1					= 0;//proceeding timer
    int expiresT2					= 0;//progressing timer
    int expiresT3					= 0;//accepted timer
    int expiresT4					= 0;//connected timer
    int expiresT5					= 0;//disconnecting timer
    int expiresT6					= 0;//terminating timer
    int expiresT7					= 0;//offerred timer
    int expiresT8					= 0;//cancelling timer

    //SDP
    SIPSdp	sdp						= null;
    SIPSdp	remoteSdp				= null;
    //

    String message					= null;

    //DEVELOPEMENT CONTROL
    protected boolean flag;


    public UPDATEHeaders() {
        if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("call handle created.");
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

        flag=false;
    }
    public void reset()
    {
        commandLine				= null;
        viaH					= null;
        viaArray				= null;
        routeH					= null;
        routeArray 				= null;
        recordrouteH			= null;
        recordrouteArray		= null;
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
        viaBranch				= null;//2013 02 13
        CSEQ_NUMBER				= SIPStack.SIP_SEQUENCE_INVITE;
        INVITE_CSEQ				= 0;
        CANCEL_CSEQ				= 0;
        ACK_CSEQ				= 0;
        BYE_CSEQ				= 0;
        callDirection			= SIPStack.SIP_CALLDIRECTION_NONE;

        callState				= SIPStack.SIP_CALLSTATE_IDLE;
        callMode				= SIPStack.SIP_CALLMODE_NONE;
        callTime_TI				= null;//idle timer
        callTime_T0				= null;//invite timer
        callTime_T00			= null;//2012 11 29
        invitingTimes			= 0;
        callTime_T1				= null;//proceeding timer
        callTime_T2				= null;//progressing timer
        callTime_T3				= null;//accepted timer
        callTime_T4				= null;//connected timer
        callTime_T40			= null;//connected duration report timer
        callTime_T5				= null;//disconnecting timer
        callTime_T6				= null;//terminating timer
        callTime_T7				= null;//offerred timer
        callTime_T8				= null;//cancelling timer

        expiresTI				= 0;//idle timer
        expiresT0				= 0;//invite timer
        expiresT00				= 0;//invite timer
        expiresT1				= 0;//proceeding timer
        expiresT2				= 0;//progressing timer
        expiresT3				= 0;//accepted timer
        expiresT4				= 0;//connected timer
        expiresT5				= 0;//disconnecting timer
        expiresT6				= 0;//terminating timer
        expiresT7				= 0;//offerred timer
        expiresT8				= 0;//cancelling timer

        //SDP
        sdp						= null;
        remoteSdp				= null;
        //

        message					= null;

        //DEVELOPEMENT CONTROL
        flag=false;

    }
}
