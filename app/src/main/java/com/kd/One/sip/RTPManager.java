package com.kd.One.sip;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RTPManager {
    protected SmartHomeviewActivity parent	= null;
    //SIPCm 						callManager	= null;
    RFC2833						rfc2833			= null;
    DatagramSocket theSocket		= null;
    DatagramSocket				switchedSocket	= null;
    protected DatagramPacket dp				= null;
    protected int 				RTP_PORT		= 3000;
    protected int 				mediaType		= SIPStack.SIP_MEDIATYPE_NONE;
    byte[] 						buffer			= null;
    public static byte[]		pcmLinearBuffer = new byte[320];
    boolean 					bRTPlooping		= false;
    boolean 					bRTPparsing		= false;
    boolean						bActive			= false;
    //RTP ENCODING FACTOR
    protected int 				RTPversion		= 0;
    protected int 				RTPpadding		= 0;
    protected int 				RTPextension	= 0;
    protected int 				RTPcsrc			= 0;
    protected int 				RTPmark			= 0;
    protected int 				RTPpayload		= 0;
    protected int 				RTPsequence		= 0;
    protected int 				RTPtimestamp	= 0;
    protected int 				RTPssrc			= 0;
    protected byte[] 			rtpPacketBuffer	= new byte[172];
    protected byte[] 			fieldBytes		= new byte[4];
    protected byte[] 			rtpfieldBytes	= new byte[4];

    String 						remoteIp			= null;
    int 						remotePort			= 0;
    boolean 					running				= false;
    Thread 						receiveThread		= null;
    int 						remotePayloadType	= SIPStack.SIP_CODEC_NONE;
    String 						remoteMediaIp		= null;
    int 						remoteMediaPort		= 0;
    boolean						bDtmfDetected		= false;
    int							detectedDtmf		= 0;
    //2012 02 13
    byte[] 						conferenceBuffer			= null;
    byte[]						conferencePcmLinearBuffer 	= new byte[320];
    byte[]						conference1RecvPcmLinearBuffer 	= new byte[320];
    byte[]						conference2RecvPcmLinearBuffer 	= new byte[320];
    byte[]						mixedPcmLinearBuffer 		= new byte[320];
    boolean						bConferenceActive			= false;
    DatagramSocket 				theConferenceSocket			= null;
    protected DatagramPacket 	conferenceDp				= null;
    protected int 				conferenceRTP_PORT			= 3000;

    boolean conferenceRunning			= false;
    boolean bMasterPlayReady			= false;
    boolean bConferencePlayReady		= false;
    Thread 	conferenceReceiveThread		= null;
    int 	conferenceRemotePayloadType	= SIPStack.SIP_CODEC_NONE;
    String 	conferenceRemoteMediaIp		= null;
    int 	conferenceRemoteMediaPort	= 0;
    protected int 				conferenceRTPversion		= 0;
    protected int 				conferenceRTPpadding		= 0;
    protected int 				conferenceRTPextension		= 0;
    protected int 				conferenceRTPcsrc			= 0;
    protected int 				conferenceRTPmark			= 0;
    protected int 				conferenceRTPpayload		= 0;
    protected int 				conferenceRTPsequence		= 0;
    protected int 				conferenceRTPtimestamp		= 0;
    protected int 				conferenceRTPssrc			= 0;
    protected byte[] 			conferenceRtpPacketBuffer	= new byte[172];
    protected byte[] 			conferenceFieldBytes		= new byte[4];
    protected byte[] 			conferenceRtpfieldBytes		= new byte[4];
    protected Date rtpArrivalTime				= null;//rtp arrival timer   2012 03 16

    protected int 				packetSize					= 0;
    SIPSound sipSound=null;

    //
    int testval;
    //
    byte[] lineardata=new byte[320];
    Timer RTPSENDTimer;

    public DatagramSocket 				runningSocket = null;

    class SENDTimerTask extends TimerTask {
        public void run() {
            //if(RTPSENDTimer==null) return;
            if(RTPSENDTimer==null || bActive==false) return; //2012 02 22
            if(sipSound==null) return;//2012 03 22
            if(sipSound.bActive==false) return;//2012 03 22
            boolean bValid=false;
            //2012 12 05
            if(SIPSound.CAPTURE_DATA_TYPE_IS_BYTE==true) //RAW DATA PROCESSIN
            {
                try {
                    //System.out.println("D");
                    bValid=sipSound.getLineardata(lineardata, 320);
                } catch (Exception e) {
                    //2012 03 22
                    System.err.println("I/O problems: " + e);
                    //System.exit(-1);2012 03 23
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                    return; //2012 03 23
                }

                try {
                    if(true==bValid)
                    {
                        //2012 02 13
                        if(bConferenceActive==true && conferenceRemoteMediaIp!=null && conferenceRemoteMediaPort>0)
                        {

                            //play audio
                            boolean bRet1=false;
                            boolean bRet2=false;
                            bRet1=sipSound.getConfRecvLineardata(conference1RecvPcmLinearBuffer, 320,1);
                            bRet2=sipSound.getConfRecvLineardata(conference2RecvPcmLinearBuffer, 320,2);
                            if(bRet1==true && bRet2==true)
                            {
                                if(remoteMediaIp!=null && remoteMediaPort>0)
                                {
                                    if(true==SIPG711.mixPCMLinear(lineardata,conference2RecvPcmLinearBuffer,320,mixedPcmLinearBuffer))
                                    {
                                        rtpWriteExt(mixedPcmLinearBuffer,320,remotePayloadType
                                                ,remoteMediaIp,remoteMediaPort);
                                    }
                                    else {
                                        rtpWriteExt(lineardata,lineardata.length,remotePayloadType
                                                ,remoteMediaIp,remoteMediaPort);
                                    }
                                }
                                if(conferenceRemoteMediaIp!=null && conferenceRemoteMediaPort>0)
                                {
                                    if(true==SIPG711.mixPCMLinear(lineardata,conference1RecvPcmLinearBuffer,320,mixedPcmLinearBuffer))
                                    {
                                        conferenceRtpWriteExt(mixedPcmLinearBuffer,320,conferenceRemotePayloadType
                                                ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                    }
                                    else {
                                        conferenceRtpWriteExt(lineardata,lineardata.length,conferenceRemotePayloadType
                                                ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                    }
                                }

                                if(true==SIPG711.mixPCMLinear(conference1RecvPcmLinearBuffer,conference2RecvPcmLinearBuffer,320,mixedPcmLinearBuffer))
                                {
                                    sipSound.playAudio(mixedPcmLinearBuffer,320);
                                }

                            }
                            else if(bRet1==true)
                            {
                                if(remoteMediaIp!=null && remoteMediaPort>0)
                                {
                                    rtpWriteExt(lineardata,lineardata.length,remotePayloadType
                                            ,remoteMediaIp,remoteMediaPort);
                                }
                                if(conferenceRemoteMediaIp!=null && conferenceRemoteMediaPort>0)
                                {
                                    if(true==SIPG711.mixPCMLinear(lineardata,conference1RecvPcmLinearBuffer,320,mixedPcmLinearBuffer))
                                    {
                                        conferenceRtpWriteExt(mixedPcmLinearBuffer,320,conferenceRemotePayloadType
                                                ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                    }
                                    else {
                                        conferenceRtpWriteExt(lineardata,lineardata.length,conferenceRemotePayloadType
                                                ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                    }
                                }

                                sipSound.playAudio(conference1RecvPcmLinearBuffer,320);
                            }
                            else if(bRet2==true)
                            {
                                if(remoteMediaIp!=null && remoteMediaPort>0)
                                {
                                    if(true==SIPG711.mixPCMLinear(lineardata,conference2RecvPcmLinearBuffer,320,mixedPcmLinearBuffer))
                                    {
                                        rtpWriteExt(mixedPcmLinearBuffer,320,remotePayloadType
                                                ,remoteMediaIp,remoteMediaPort);
                                    }
                                    else {
                                        rtpWriteExt(lineardata,lineardata.length,remotePayloadType
                                                ,remoteMediaIp,remoteMediaPort);
                                    }
                                }
                                if(conferenceRemoteMediaIp!=null && conferenceRemoteMediaPort>0)
                                {
                                    conferenceRtpWriteExt(lineardata,lineardata.length,conferenceRemotePayloadType
                                            ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                }

                                sipSound.playAudio(conference2RecvPcmLinearBuffer,320);
                            }
                            else {
                                if(remoteMediaIp!=null && remoteMediaPort>0)
                                {
                                    rtpWriteExt(lineardata,lineardata.length,remotePayloadType
                                            ,remoteMediaIp,remoteMediaPort);
                                }
                                if(conferenceRemoteMediaIp!=null && conferenceRemoteMediaPort>0)
                                {
                                    conferenceRtpWriteExt(lineardata,lineardata.length,conferenceRemotePayloadType
                                            ,conferenceRemoteMediaIp,conferenceRemoteMediaPort);
                                }
                                Log.e("audio","audio no data");
                            }
                            //
                        }
                        else
                        //
                        {
                            if(remoteMediaIp!=null && remoteMediaIp.length()>0 && remoteMediaPort>0)
                            {
                                rtpWriteExt(lineardata,lineardata.length,remotePayloadType
                                        ,remoteMediaIp,remoteMediaPort);
                            }
                            //else {
                            //	System.out.println("RTP TIMER invalid remote address.");
                            //}
                        }
                    }
                } catch (Exception e) {
                    //2012 03 22
                    System.out.println("BSS DEBUG: SYSTEM DOWN CHECK POINT . Will be down.");
                    System.err.println("I/O problems: " + e);
                    //System.exit(-1); 2012 03 23
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                }

            }
            else { //ENCODED DATA PROCESSING
                int encpacketsize=0;
                try {
                    //System.out.println("D");

                    if(remotePayloadType==SIPStack.SIP_CODEC_G711U)
                    {
                        encpacketsize=160;
                    }
                    else if(remotePayloadType==SIPStack.SIP_CODEC_G711A)
                    {
                        encpacketsize=160;
                    }
                    else if(remotePayloadType==SIPStack.SIP_CODEC_G729)
                    {
                        encpacketsize=20;
                    }
                    bValid=sipSound.getEncodeddata(rtpPacketBuffer, encpacketsize,12);

                } catch (Exception e) {
                    //2012 03 22
                    System.err.println("I/O problems: " + e);
                    //System.exit(-1);2012 03 23
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                    return; //2012 03 23
                }

                try {
                    if(true==bValid)
                    {
                        if(remoteMediaIp!=null && remoteMediaIp.length()>0 && remoteMediaPort>0)
                        {
                            //System.out.println("encpacketsize:"+encpacketsize);
                            rtpWriteEnc(encpacketsize,remotePayloadType
                                    ,remoteMediaIp,remoteMediaPort);

                        }
                        //else {
                        //	System.out.println("RTP TIMER invalid remote address.");
                        //}
                    }
                } catch (Exception e) {
                    //2012 03 22
                    System.out.println("BSS DEBUG: SYSTEM DOWN CHECK POINT . Will be down.");
                    System.err.println("I/O problems: " + e);
                    //System.exit(-1); 2012 03 23
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                }

            }


        }
        //
    }
    //


    public RTPManager (Context context, int mediaType)
    {
        sipSound=null;
        parent=(SmartHomeviewActivity) context;

        if(SIPStack.SIP_MESSAGE_DEBUG==true) {
            if(this.mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) System.out.println("AUDIO RTP Thread receive ready. PORT:"+RTP_PORT);
            else if(this.mediaType==SIPStack.SIP_MEDIATYPE_VIDEO) System.out.println("VIDEO RTP Thread receive ready. PORT:"+RTP_PORT);
        }

        //callManager=cm;
        rfc2833=new RFC2833();
        this.mediaType=mediaType;
        //cm.registRTPManager(this,mediaType);
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) buffer = new byte[SIPStack.RTP_AUDIO_SIZE];
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO) buffer = new byte[65507];
        else buffer = new byte[65507];
        theSocket=null;

        RTPversion	= 2;
        RTPpadding	= 0;
        RTPextension= 0;
        RTPcsrc		= 0;
        RTPmark		= 0;
        RTPpayload	= 0;
        RTPsequence	= 0;
        Date date=new Date();

        RTPtimestamp= (int)date.getTime();
        RTPssrc		= (int)date.getTime();

        RTPSENDTimer= null;

        bActive				= false;
        bDtmfDetected		= false;
        detectedDtmf		= 0;
        //2012 02 13
        bConferenceActive	= false;
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) conferenceBuffer = new byte[SIPStack.RTP_AUDIO_SIZE];
        theConferenceSocket=null;
        //2012 03 16
        rtpArrivalTime=new Date();
        //

    }//RTPManager()
    public RTPManager (int mediaType)
    {
        sipSound=null;
        if(SIPStack.SIP_MESSAGE_DEBUG==true) {
            if(this.mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) Log.e("audio", "AUDIO RTP Thread receive ready. PORT:"+RTP_PORT);
            else if(this.mediaType==SIPStack.SIP_MEDIATYPE_VIDEO) Log.e("video","VIDEO RTP Thread receive ready. PORT:"+RTP_PORT);
        }

        //callManager=cm;
        rfc2833=new RFC2833();
        this.mediaType=mediaType;
        //cm.registRTPManager(this,mediaType);
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) buffer = new byte[SIPStack.RTP_AUDIO_SIZE];
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO) buffer = new byte[65507];
        else buffer = new byte[65507];
        theSocket=null;

        RTPversion	= 2;
        RTPpadding	= 0;
        RTPextension= 0;
        RTPcsrc		= 0;
        RTPmark		= 0;
        RTPpayload	= 0;
        RTPsequence	= 0;
        Date date=new Date();

        RTPtimestamp= (int)date.getTime();
        RTPssrc		= (int)date.getTime();

        RTPSENDTimer= null;

        bActive				= false;
        bDtmfDetected		= false;
        detectedDtmf		= 0;
        //2012 02 13
        bConferenceActive	= false;
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO) conferenceBuffer = new byte[SIPStack.RTP_AUDIO_SIZE];
        theConferenceSocket=null;
        //2012 03 16
        rtpArrivalTime=new Date();
        //

    }//RTPManager()


    public void receiveAudio() {
        if(receiveThread!=null)
        {
            Log.e("receiveAudio", "receiveThread null!!!!!!!!!!!!!!!!");
            return;
        }

        try {
            Thread runner = new Thread() {
                public void run() {

                    if(theSocket != null)
                    {
                        theSocket.disconnect();
                        theSocket = null;
                        theSocket.close();

                        testval = 9999;

                    }

                    try {
                        theSocket=new DatagramSocket(null);
                        theSocket.setReuseAddress(true);
                        theSocket.bind(new InetSocketAddress(RTP_PORT));
                    } catch (SocketException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                        testval = 8888;
                    }

                    if(theSocket == null)
                    {
                        System.out.println("RTP sound socket create failed on "+RTP_PORT);
                        return;
                    }

                    buffer = new byte[SIPStack.RTP_AUDIO_SIZE];
                    dp = new DatagramPacket(buffer,buffer.length);

                    System.out.println("RTP Thread start. Port is "+RTP_PORT);

                    running=true;
                    runningSocket=theSocket;
                    switchedSocket=theSocket;

                    while(running==true && runningSocket!=null) {
                        if(receiveThread==null)
                        {
                            Log.e("sound thread", "receiveThread null");
                            testval = 7777;
                            break;
                        }
                        if(receiveThread.isInterrupted())
                        {
                            Log.e("sound thread", "receiveThread isInterrupted");
                            testval = 6666;
                            break;
                        }
                        try {

                            dp.setData(buffer);
                            dp.setLength(SIPStack.RTP_AUDIO_SIZE);

                            runningSocket.receive(dp);
                            runningSocket.setSoTimeout(10000);


                            //Log.e("sound thread",String.format("Length:%d", dp.getLength()));

                            if(0 != RTPParser(dp))
                            {
                                //Log.i("RTPEND","Debug 6");
                                //break; original
                                continue;//2015 03 30
                            }
                            if( //2012 03 24
                                    sipSound!=null &&
                                            sipSound.flowIndicator == SIPStack.SIP_MEDIAFLOW_SENDONLY
                            )
                            {
                                sipSound.flowIndicator = SIPStack.SIP_MEDIAFLOW_SENDRECV;//2012 03 24
                            }
                            rtpArrivalTime=new Date();

                        }
                        catch(SocketException se) {
                            Log.i("RTPEND","Debug 3");
                            testval = 5555;
                            SIPStack.exceptionCountAtCurrentCall++;
                            break;
                        }
                        catch(SocketTimeoutException se) //2012 07 23
                        {
                            break;
                        }
                        catch(IOException e)
                        {
                            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                            break;
                        }
                        catch(Exception e)
                        {
                            Log.i("RTPEND","Debug 4");
                            testval = 4444;
                            System.err.println(e);
                            SIPStack.exceptionCountAtCurrentCall++;
                            break;
                        }


                    }//while
                    Log.i("RTP","Sound Ended");
                    if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("RTP Thread terminated.");
                    if(runningSocket!=null) {
                        if(runningSocket==theSocket) theSocket=null;
                        runningSocket.close();
                        runningSocket=null;
                        System.out.println("socket close of port:"+RTP_PORT);

                        testval = 3333;
                    }
                    running=false;
                    receiveThread=null;
                }
            };

            receiveThread = new Thread(runner);
            receiveThread.start();

        } catch (Exception e) {
            receiveThread=null;
            running=false;
            System.err.println("Exception: " + e);
            testval = 2222;
            SIPStack.exceptionCountAtCurrentCall++;
            return;
        }
        //Log.i("RTPEND","Debug 5");
        //System.out.println(" RTP Thread end");
    }

    public int RTPParser(DatagramPacket dp) {
        //String remoteIp=null;
        if(SIPStack.bBlindCall==true)
        {
            byte[] address = dp.getAddress().getAddress();
            if(address.length==4)
            {
                int unsignedByte1 = address[0]<0 ? address[0]+256 : address[0];
                int unsignedByte2 = address[1]<0 ? address[1]+256 : address[1];
                int unsignedByte3 = address[2]<0 ? address[2]+256 : address[2];
                int unsignedByte4 = address[3]<0 ? address[3]+256 : address[3];
                remoteIp=unsignedByte1+"."+unsignedByte2+"."+unsignedByte3+"."+unsignedByte4;
            }

            remotePort=dp.getPort();
        }

        //System.out.println("RTP:"+remoteIp+":"+remotePort+"   "+dp.getLength());
		/*
		if(bRTPlooping==true)
		{
			//System.out.println("RTP:"+dp.getLength());
			SendRTPPacket(remoteIp,remotePort,dp.getData(),dp.getLength());
		}
		else
		 */
        if(sipSound!=null && sipSound.bActive==true)//2012 03 22)
        { //PLAY Only
            // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
            // 2 1 1 4  1 7  16              32        32

			/*
			//VERSION
			rtpfieldBytes[0]=buffer[0];
			int version = (((int)rtpfieldBytes[0]) >> 6) & 0x03;
			System.out.println("VERSION:"+version);
			 */

            //PAYLOAD TYPE
            rtpfieldBytes[0]=buffer[1];
            int payloadType = ((int)rtpfieldBytes[0]) & 0x7F;

            //SEQUENC NUMBER
            int sequence1=(int)buffer[2] & 0xFF;//((int)buffer[2] & 0x0F << 8) | ((int)buffer[3] & 0x0F);
            int sequence2=(int)buffer[3] & 0xFF;
            int sequence=(sequence1 << 8) | sequence2;

            //Log.e("sound thread",String.format("audio seq: %d", sequence));

            testval = sequence;

            //System.out.println("audio seq:"+sequence+" "+payloadType+" "+dp.getLength());

            if(payloadType==SIPStack.SIP_CODEC_G711U)
            {
                if(sipSound.playStarted==true)
                {
                    packetSize=dp.getLength()-12;
                    //if(SIPG711.decodeULawExt(buffer,12,160,pcmLinearBuffer)==true) { original
                    if(SIPG711.decodeULawExt(buffer,12,packetSize,pcmLinearBuffer)==true)
                    { //2012 05 02

                        //sipSound.writeAudioDataToFile(pcmLinearBuffer, packetSize*2);

                        sipSound.playAudio(pcmLinearBuffer,packetSize*2);//2012 12 05
                        bMasterPlayReady=true;

                    }
                }
            }
            else if(payloadType==SIPStack.SIP_CODEC_G711A)
            {
                if(sipSound.playStarted==true)
                {
                    packetSize=dp.getLength()-12;
                    //if(SIPG711.decodeALawExt(buffer,12,160,pcmLinearBuffer)==true) { original
                    if(SIPG711.decodeALawExt(buffer,12,packetSize,pcmLinearBuffer)==true)
                    {//2012 05 02
                        sipSound.playAudio(pcmLinearBuffer,packetSize*2); //2012 05 02
                        bMasterPlayReady=true;

                    }
                }
            }

            //2012 02 21
            else if(payloadType==RFC2833.payloadType)
            {
				/*
				0                   1                   2                   3
			    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
			   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			   |     event     |E|R| volume    |          duration             |
			   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
				 */
                int field=0;
                byte[] rfcdata=new byte[4];
                //0..7 EVENT
                int dtmf=(int)buffer[12];
                //8..15 EDGE|RESERVED|VOLUME
                field=(int)buffer[13];
                int edge=(field & 0x00000080) >> 7;
                int volume=field & 0x0000003F;
                //16..32 DURATION
                int duration=((int)buffer[14] << 24)>>16 & 0x0000FF00;
                duration |= ((int)buffer[15] << 24)>>24 & 0x000000FF;
                //
                if(bDtmfDetected==true && detectedDtmf==dtmf && edge==1)
                {
                    detectedDtmf=0;
                    bDtmfDetected=false;
                }
                else if(edge!=1 && bDtmfDetected==false)
                {
                    bDtmfDetected=true;detectedDtmf=dtmf;
                }
                //
            }
            //System.out.println("RTP:"+dp.getLength());
        }


        return 0;//normal

    }//RTPParser()

    public void rtpWrite(short[] data,int datasize,int codec,String ip,int port)
    {
        int packetsize=0;
        boolean bSend=false;
        if(codec==SIPStack.SIP_CODEC_G711U)
        {
            packetsize=SIPG711.encode2ULaw(data,datasize,rtpPacketBuffer,12);
        }
        else if(codec==SIPStack.SIP_CODEC_G711A)
        {
            packetsize=SIPG711.encode2ALaw(data,datasize,rtpPacketBuffer,12);
            return;
        }
        if(rtpPacketBuffer !=null && packetsize>0)
        {
            bSend=
                    encodeRtpHeader(
                            rtpPacketBuffer,
                            RTPversion,
                            RTPpadding,
                            RTPextension,
                            RTPcsrc,
                            RTPmark,
                            codec,
                            RTPsequence,
                            RTPtimestamp,
                            RTPssrc
                    );
        }
        if(bSend==true)
        {
            RTPsequence++;
            RTPtimestamp+=160;
            SendRTPPacket(ip,port,rtpPacketBuffer,packetsize);
        }

        return;
    }
    public void rtpWriteExt(byte[] data,int datasize,int codec,String ip,int port)
    {
        int packetsize=0;
        boolean bSend=false;
        //DTMF PROCESSING
        if(rfc2833.bActive==true)
        {
            byte[] rfcdata=rfc2833.constructDtmfPacket(160);
            if(rfcdata!= null && rfcdata.length==4)
            {
                rtpPacketBuffer[12]=rfcdata[0];
                rtpPacketBuffer[13]=rfcdata[1];
                rtpPacketBuffer[14]=rfcdata[2];
                rtpPacketBuffer[15]=rfcdata[3];
                if(rfc2833.bEnd==true) RTPmark=1;
                else RTPmark=0;
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                RFC2833.payloadType,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );

                if(rfc2833.bStart==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }
                }
                else if(rfc2833.bEnd==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }
                    RTPtimestamp+=rfc2833.exceedDuration;
                    rfc2833.resetDtmf();
                }
                else {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }

                }
            }
            return;
        }

        try {
            if(codec==SIPStack.SIP_CODEC_G711U)
            {
                packetsize=SIPG711.encode2ULawExt(data,datasize,rtpPacketBuffer,12);
            }
            else if(codec==SIPStack.SIP_CODEC_G711A)
            {
                packetsize=SIPG711.encode2ALawExt(data,datasize,rtpPacketBuffer,12);
            }
            else if(codec==SIPStack.SIP_CODEC_G729)
            {
                return;
                //packetsize=SIPG729a.encode(data,datasize,rtpPacketBuffer,12);
                //System.out.println("G729 RTP send:"+packetsize);
            }
            else {
                return;
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-1);//2012 03 23
            return;//2012 03 23
        }

        try {
            if(rtpPacketBuffer !=null && packetsize>0)
            {
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                codec,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            //System.exit(-1);//2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return;//2012 03 23
        }

        try {
            if(bSend==true && ip!=null && ip.length()>0 && port>0) //2012 03 23
            {
                RTPsequence++;
                RTPtimestamp+=160;
                SendRTPPacket(ip,port,rtpPacketBuffer,packetsize);
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-1); //2012 03 23
            return;//2012 03 23
        }



        return;
    }
    public void rtpWriteEnc(byte[] data,int datasize,int codec,String ip,int port)
    {
        int packetsize=0;
        boolean bSend=false;
        //DTMF PROCESSING
        if(rfc2833.bActive==true)
        {
            byte[] rfcdata=rfc2833.constructDtmfPacket(160);
            if(rfcdata!= null && rfcdata.length==4)
            {
                rtpPacketBuffer[12]=rfcdata[0];
                rtpPacketBuffer[13]=rfcdata[1];
                rtpPacketBuffer[14]=rfcdata[2];
                rtpPacketBuffer[15]=rfcdata[3];
                if(rfc2833.bEnd==true) RTPmark=1;
                else RTPmark=0;
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                RFC2833.payloadType,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );

                if(rfc2833.bStart==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }
                }
                else if(rfc2833.bEnd==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);

                    }
                    RTPtimestamp+=rfc2833.exceedDuration;
                    rfc2833.resetDtmf();
                }
                else {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }

                }
            }
            return;
        }

        try {
            System.arraycopy(data,0,rtpPacketBuffer,12,datasize);

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-1);//2012 03 23
            return;//2012 03 23
        }

        try {
            if(rtpPacketBuffer !=null && packetsize>0)
            {
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                codec,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            //System.exit(-1);//2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return;//2012 03 23
        }

        try {
            if(bSend==true && ip!=null && ip.length()>0 && port>0) //2012 03 23
            {
                RTPsequence++;
                RTPtimestamp+=160;
                SendRTPPacket(ip,port,rtpPacketBuffer,packetsize);
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-1); //2012 03 23
            return;//2012 03 23
        }



        return;
    }
    public void rtpWriteEnc(int datasize,int codec,String ip,int port)
    {
        int packetsize=0;
        boolean bSend=false;
        //DTMF PROCESSING
        if(rfc2833.bActive==true)
        {
            byte[] rfcdata=rfc2833.constructDtmfPacket(160);
            if(rfcdata!= null && rfcdata.length==4)
            {
                rtpPacketBuffer[12]=rfcdata[0];
                rtpPacketBuffer[13]=rfcdata[1];
                rtpPacketBuffer[14]=rfcdata[2];
                rtpPacketBuffer[15]=rfcdata[3];
                if(rfc2833.bEnd==true) RTPmark=1;
                else RTPmark=0;
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                RFC2833.payloadType,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );

                if(rfc2833.bStart==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);

                    }
                }
                else if(rfc2833.bEnd==true)
                {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }
                    RTPtimestamp+=rfc2833.exceedDuration;
                    rfc2833.resetDtmf();
                }
                else {
                    if(bSend==true)
                    {
                        RTPsequence++;
                        SendRTPPacket(ip,port,rtpPacketBuffer,16);
                    }

                }
            }
            return;
        }


        try {
            packetsize=datasize+12;
            if(rtpPacketBuffer !=null && packetsize>0)
            {
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                codec,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );
            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            //System.exit(-1);//2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return;//2012 03 23
        }

        try {
            if(bSend==true && ip!=null && ip.length()>0 && port>0) //2012 03 23
            {
                RTPsequence++;
                RTPtimestamp+=160;
                SendRTPPacket(ip,port,rtpPacketBuffer,packetsize);
                //System.out.println("Send rtp:"+packetsize+" sent:"+sent);

            }

        } catch (Exception e) {
            //2012 03 22
            System.err.println("I/O problems: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-1); //2012 03 23
            return;//2012 03 23
        }



        return;
    }

    public void rtpWriteG729(String ip,int port)
    {
		/* G.729 Temporary remark
		int packetsize=0;
		boolean bSend=false;
		//DTMF PROCESSING
		if(rfc2833.bActive==true)
		{
			byte[] rfcdata=rfc2833.constructDtmfPacket(160);
			if(rfcdata!= null && rfcdata.length==4)
			{
				rtpPacketBuffer[12]=rfcdata[0];
				rtpPacketBuffer[13]=rfcdata[1];
				rtpPacketBuffer[14]=rfcdata[2];
				rtpPacketBuffer[15]=rfcdata[3];
				if(rfc2833.bEnd==true) RTPmark=1;
				else RTPmark=0;
				bSend=
						encodeRtpHeader(
						rtpPacketBuffer,
						RTPversion,
						RTPpadding,
						RTPextension,
						RTPcsrc,
						RTPmark,
						RFC2833.payloadType,
						RTPsequence,
						RTPtimestamp,
						RTPssrc
						);

				if(rfc2833.bStart==true)
				{
					if(bSend==true)
					{
						RTPsequence++;
						SendRTPPacket(ip,port,rtpPacketBuffer,16);
						SendRTPPacket(ip,port,rtpPacketBuffer,16);

					}
				}
				else if(rfc2833.bEnd==true)
				{
					if(bSend==true)
					{
						RTPsequence++;
						SendRTPPacket(ip,port,rtpPacketBuffer,16);
						SendRTPPacket(ip,port,rtpPacketBuffer,16);
					}
					RTPtimestamp+=rfc2833.exceedDuration;
					rfc2833.resetDtmf();
				}
				else {
					if(bSend==true)
					{
						RTPsequence++;
						SendRTPPacket(ip,port,rtpPacketBuffer,16);
					}

				}
			}
			return;
		}

		try {
			packetsize=SIPG729a.encode(SIPSound.dma729Buffer,160,rtpPacketBuffer,12);

		} catch (Exception e) {
			//2012 03 22
			System.err.println("I/O problems: " + e);
			SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
			//System.exit(-1);//2012 03 23
			return;//2012 03 23
		}

		try {
			if(rtpPacketBuffer !=null && packetsize>0)
			{
				bSend=
					encodeRtpHeader(
					rtpPacketBuffer,
					RTPversion,
					RTPpadding,
					RTPextension,
					RTPcsrc,
					RTPmark,
					SIPStack.SIP_CODEC_G729,
					RTPsequence,
					RTPtimestamp,
					RTPssrc
					);
			}

		} catch (Exception e) {
			//2012 03 22
			System.err.println("I/O problems: " + e);
			//System.exit(-1);//2012 03 23
			SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
			return;//2012 03 23
		}

		try {
			if(bSend==true && ip!=null && ip.length()>0 && port>0) //2012 03 23
			{
				RTPsequence++;
				RTPtimestamp+=160;
				int sent=SendRTPPacket(ip,port,rtpPacketBuffer,packetsize);
				//System.out.println("G729 RTP send:"+sent);
			}

		} catch (Exception e) {
			//2012 03 22
			System.err.println("I/O problems: " + e);
			SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
			//System.exit(-1); //2012 03 23
			return;//2012 03 23
		}


		 */
        return;
    }

    //2015 01 15

    //remoteMediaIp,remoteMediaPort
    public void connectVideo(int codec,String ip,int port)
    {
        int datasize=0;
        int packetsize=0;
        boolean bSend=false;
        //DTMF PROCESSING
        try
        {
            String cmd="VIDEO"+ip+":"+port+"END";
            System.out.println("connectVideo "+cmd);
            datasize=cmd.length();
            packetsize=12+datasize;
            if(ip.length()>0 && port>0 && port<65556)
            {
                System.arraycopy(cmd.getBytes(),0,rtpPacketBuffer,12,datasize);
                RTPmark=0;
                bSend=
                        encodeRtpHeader(
                                rtpPacketBuffer,
                                RTPversion,
                                RTPpadding,
                                RTPextension,
                                RTPcsrc,
                                RTPmark,
                                codec,
                                RTPsequence,
                                RTPtimestamp,
                                RTPssrc
                        );


                if(bSend==true)
                {
                    int sent=SendRTPPacket(remoteMediaIp,remoteMediaPort,rtpPacketBuffer,packetsize);
                    System.out.println("connect video sent:"+sent);
                    System.out.println(" to "+remoteMediaIp+"  "+remoteMediaPort);
                }

            }
            return;
        }catch(Exception e){}



        return;
    }
    //
    public void parseRtpHeader(byte[] buffer)
    {
        if(buffer==null || buffer.length<12) return;
        // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
        // 2 1 1 4  1 7  16              32        32
        //VERSION
        fieldBytes[0]=buffer[0];
        int version = (((int)fieldBytes[0]) >> 6) & 0x03;
        System.out.println("VERSION:"+version);
        //PAYLOAD TYPE
        fieldBytes[0]=buffer[1];
        int payloadType = ((int)fieldBytes[0]) & 0x7F;
        System.out.println("Payload:"+payloadType);
        //SEQUENC NUMBER
        fieldBytes[0]=0;
        fieldBytes[1]=0;
        fieldBytes[2]=buffer[2];
        fieldBytes[3]=buffer[3];
        int sequencNumber=getBytesToInt(fieldBytes);
        System.out.println("SEQUENCE NUMBER:"+sequencNumber);
        //TIME STAMP PARSING
        fieldBytes[0]=buffer[4];
        fieldBytes[1]=buffer[5];
        fieldBytes[2]=buffer[6];
        fieldBytes[3]=buffer[7];
        int timeStamp=getBytesToInt(fieldBytes);
        System.out.println("TIMESTAMP:"+timeStamp);
        //SSRC PARSING
        fieldBytes[0]=buffer[8];
        fieldBytes[1]=buffer[9];
        fieldBytes[2]=buffer[10];
        fieldBytes[3]=buffer[11];
        int ssrc=getBytesToInt(fieldBytes);
        System.out.println("SSRC:"+timeStamp);
        //


        return;

    }
    public boolean encodeRtpHeader(
            byte[] header,
            int version,
            int padding,
            int extension,
            int csrc,
            int mark,
            int payload,
            int sequence,
            int timestamp,
            int ssrc
    )
    {
        if(header==null || header.length<12) return false;

        // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
        // 2 1 1 4  1 7  16              32        32
        int iField=0;
        int iAppend=0;
        //VERSION
        iField=(version << 6) & 0x000000C0; //version
        //PADDING
        iAppend=(padding << 5) & 0x00000020; //padding
        iField |= iAppend;
        //EXTENSION
        iAppend=(padding << 4) & 0x00000010; //extension
        iField |= iAppend;
        //CSRC COUN
        iAppend=csrc & 0x0000000F; //csrc count
        iField |= iAppend;

        header[0]=(byte)iField;
        iField=0;
        //MARK
        iField=(mark << 7) & 0x00000080; //mark
        //PAYLOAD TYPE
        iAppend=payload & 0x0000007F; //payload
        iField |= iAppend;

        header[1]=(byte)iField;
        iField=0;
        //SEQUENC NUMBER
        header[2]=(byte)( (sequence << 16) >> 24 );
        header[3]=(byte)( (sequence << 24) >> 24 );
        //TIME STAMP PARSING
        header[4] =(byte)( timestamp >> 24 );
        header[5] =(byte)( (timestamp << 8) >> 24 );
        header[6] =(byte)( (timestamp << 16) >> 24 );
        header[7] =(byte)( (timestamp << 24) >> 24 );
        //SSRC PARSING
        header[8] =(byte)( ssrc >> 24 );
        header[9] =(byte)( (ssrc << 8) >> 24 );
        header[10] =(byte)( (ssrc << 16) >> 24 );
        header[11] =(byte)( (ssrc << 24) >> 24 );
        //

        return true;

    }
    public int getBytesToInt(byte[] bytes) {
        int newValue=0;
        newValue |= (((int)bytes[0]) << 24) & 0xFF000000;
        newValue |= (((int)bytes[1]) << 16) & 0xFF0000;
        newValue |= (((int)bytes[2]) << 8) & 0xFF00;
        newValue |= (((int)bytes[3])) & 0xFF;
        return newValue;
    }
    public byte[] getIntToBytes(int iValue) {
        byte[] newbytes=new byte[4];

        newbytes[0] =(byte)( iValue >> 24 );
        newbytes[1] =(byte)( (iValue << 8) >> 24 );
        newbytes[2] =(byte)( (iValue << 16) >> 24 );
        newbytes[3] =(byte)( (iValue << 24) >> 24 );
        return newbytes;
    }
    public int SendRTPPacket(String sIp,int iPort,byte[] data,int dataSize)
    {
        boolean bRemoteIp=false;
        if(SIPStack.bBlindCall==true)
        {
            if(remoteIp!=null && remoteIp.length()>0 && remotePort>0) bRemoteIp=true;
        }
        //bRemoteIp=false;

        int sent=0;
        if(sIp==null || sIp.length()<=0) return 0;//2012 03 23
        if(data==null || dataSize<=0) return 0;//2012 03 23
        if(iPort<=0) return 0;
        if(theSocket==null || running==false) return 0;

        try {
            if(bRemoteIp==true)
            {
                //System.out.println("bRemote "+remoteIp+" "+remotePort);
                InetAddress ia=InetAddress.getByName(remoteIp);
                if(ia!=null)//2012 03 23
                {
                    DatagramPacket dp=new DatagramPacket(data,dataSize,ia,remotePort);
                    if(dp != null) //2012 03 23
                    {
                        theSocket.send(dp);
                    }
                }
            }
            else
            {
                //System.out.println("sIp  "+sIp+" "+iPort);
                InetAddress ia=InetAddress.getByName(sIp);
                if(ia!=null)//2012 03 23
                {
                    DatagramPacket dp=new DatagramPacket(data,dataSize,ia,iPort);
                    if(dp != null) //2012 03 23
                    {
                        theSocket.send(dp);
                    }
                }
            }

            sent=dataSize;

        } catch(UnknownHostException uhe) {
            System.err.println(uhe);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        } catch(IOException ie){
            System.err.println(ie);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23

        }

        return sent;
    }
    public boolean setRTPPort(int port)
    {
        if(port<=0) return false;
        RTP_PORT=port;
        return true;
    }
    public boolean RTPEnd()
    {
        Log.i("RTP","End");
        remoteIp="";

        if(sipSound!=null)
            sipSound.flowIndicator = SIPStack.SIP_MEDIAFLOW_SENDRECV;//2012 03 24

        try {
            if(bActive==false)
                return false;

            if(sipSound!=null)
                sipSound.closeAudioDevice();

            bActive=false;

            running=false;

            if(RTPSENDTimer!=null)
            {
                RTPSENDTimer.cancel();
                RTPSENDTimer=null;
            }

            if(receiveThread!=null)
            {
                receiveThread.interrupt();
                running=false;
                receiveThread=null;
            }

            if(theSocket!=null) {
                theSocket.close();
                theSocket=null;
            }

            SmartHomeviewActivity.act.callActive = false;
            SmartHomeviewActivity.act.callEnd();
        }catch (Exception e){
            e.printStackTrace();
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        }

        SIPStack.freeAudioRtpPort(RTP_PORT);

        return true;

    }
    //2012 02 20

    public boolean RTPSwitch()
    {
        if(bConferenceActive==true)
        {
            int targetRTP_PORT			= conferenceRTP_PORT;

            int targetRTPpayload	= conferenceRTPpayload;
            int targetRTPsequence	= conferenceRTPsequence;

            int targetRTPtimestamp	= conferenceRTPtimestamp;
            int targetRTPssrc		= conferenceRTPssrc;
            String targetRemoteMediaIp	= conferenceRemoteMediaIp;
            int targetRemoteMediaPort=conferenceRemoteMediaPort;
            try {
                if(bActive==false) return false;

                //bActive=false;

                if(RTPSENDTimer!=null)
                {
                    RTPSENDTimer.cancel();
                    RTPSENDTimer=null;
                }



                if(this.receiveThread!=null)
                {
                    receiveThread.interrupt();
                    running=false;
                    receiveThread=null;
                }
                if(switchedSocket!=null) {
                    switchedSocket.close();
                    switchedSocket=null;
                }

                SIPStack.freeAudioRtpPort(RTP_PORT);
                conferenceSwitchRTPEnd();

                RTPInitExt(
                        targetRTP_PORT,
                        targetRTPpayload,
                        targetRTPsequence,
                        targetRTPtimestamp,
                        targetRTPssrc,
                        targetRemoteMediaIp,
                        targetRemoteMediaPort
                );


            }catch (Exception e){
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            }

            //System.out.println(">>>> RTPSwitched");
            return true;

        }
        else {
            try {
                if(bActive==false) return false;

                bActive=false;

                if(RTPSENDTimer!=null)
                {
                    RTPSENDTimer.cancel();
                    RTPSENDTimer=null;
                }

                if(this.receiveThread!=null)
                {
                    receiveThread.interrupt();
                    running=false;
                    receiveThread=null;
                }

                if(theSocket!=null) {
                    theSocket.close();
                    theSocket=null;
                }
                if(sipSound!=null) sipSound.closeAudioDevice();
            }catch (Exception e){
                SIPStack.exceptionCountAtCurrentCall++;
            }

            SIPStack.freeAudioRtpPort(RTP_PORT);
            return true;

        }


    }
    //
    public void RTPInit(int port)
    {
        Log.i("RTP","Init");

        remoteIp="";
        if(sipSound!=null) sipSound.flowIndicator = SIPStack.SIP_MEDIAFLOW_SENDRECV;
        RTPversion	= 2;
        RTPpadding	= 0;
        RTPextension= 0;
        RTPcsrc		= 0;
        RTPmark		= 0;
        RTPpayload	= 0;
        RTPsequence	= 0;
        Date date=new Date();

        RTPtimestamp= (int)date.getTime();
        RTPssrc		= (int)date.getTime();

        bDtmfDetected		= false;
        detectedDtmf		= 0;

        if(sipSound==null)
        {
            initSoundAndPrepareAudio();
        }

        if(sipSound==null) return;

        //
        if(this.running!=true) {
            Log.e("running", "running false");
            this.setRTPPort(port);
            this.receiveAudio();
        }

        if(RTPSENDTimer!=null) RTPSENDTimer.cancel();
        RTPSENDTimer = new Timer();
        RTPSENDTimer.scheduleAtFixedRate(new SENDTimerTask(), 0,sipSound.PTIME);
        //
        bActive=true;
        //
        return;

    }
    public void RTPInitExt(
            int port,
            int RTPpayload,
            int RTPsequence,
            int RTPtimestamp,
            int RTPssrc,
            String remoteMediaIp,
            int remoteMediaPort
    )
    {
        Log.i("RTP","Init");

        if(sipSound!=null) sipSound.flowIndicator = SIPStack.SIP_MEDIAFLOW_SENDRECV;//2012 03 24
        RTPversion	= 2;
        RTPpadding	= 0;
        RTPextension= 0;
        RTPcsrc		= 0;
        RTPmark		= 0;
        this.RTPpayload	= RTPpayload;
        this.RTPsequence	= RTPsequence;
        Date date=new Date();

        this.RTPtimestamp= RTPtimestamp;
        this.RTPssrc		= RTPssrc;
        this.remoteMediaIp=remoteMediaIp;
        this.remoteMediaPort=remoteMediaPort;

        bDtmfDetected		= false;
        detectedDtmf		= 0;
        if(sipSound==null)//2012 03 22 2014 12 02 unmark
        {
            initSoundAndPrepareAudio();
        }

        if(sipSound==null) return;//2012 03 22
		/*
		try {
			if(sipSound.playStarted!=true)
			{
				sipSound.prepareAudioTrack();
			}
			if(sipSound.recorderStarted!=true)
			{
				sipSound.captureAudio();
			}
			else {
				sipSound.closeCaptureDevice();
				sipSound.captureAudio();
			}
		}
		catch (Exception e)
		{
			SIPStack.exceptionCountAtCurrentCall++;
			return;
		}
		 */

        //
        if(this.running!=true) {
            this.setRTPPort(port);
            this.receiveSwitchAudio();
        }
        //2012 01 20
        if(RTPSENDTimer!=null) RTPSENDTimer.cancel();
        RTPSENDTimer = new Timer();
        RTPSENDTimer.scheduleAtFixedRate(new SENDTimerTask(), 0,sipSound.PTIME);
        //
        bActive=true;
        //
        return;

    }
    //2012 02 23
    public void receiveSwitchAudio() {
        if(receiveThread!=null) return;
        try {
            Runnable runner = new Runnable() {
                DatagramSocket 				runningSocket=null;

                public void run() {
                    try {
                        theSocket=new DatagramSocket(RTP_PORT);
                        if(theSocket==null) return;//2012 03 23
                        theSocket.setReuseAddress(true);
                        dp = new DatagramPacket(buffer,buffer.length);
                        if(dp==null) return;//2012 03 23
                        runningSocket=theSocket;

                    } catch(SocketException se)
                    {
                        System.err.println(se);
                        SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                        return;
                    } catch(NullPointerException ne) {
                        System.err.println(ne);
                        SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                        return;
                    }
                    //System.out.println("Switched RTP Thread start. Port is "+RTP_PORT);
                    running=true;
                    while(running==true && runningSocket!=null) {
                        if(receiveThread==null) break;
                        if(receiveThread.isInterrupted()) break;
                        try {

                            runningSocket.receive(dp);
                            if(0 != switchRTPParser(dp))
                            {
                                break;
                            }
                        }
                        catch(SocketException se) {
                            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                            break;
                        }catch(Exception e)
                        {
                            //System.err.println(e);
                            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                            break;
                        }


                    }//while
                    if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("Switched RTP Thread terminated.");
                    if(runningSocket!=null) {
                        if(runningSocket==theSocket) theSocket=null;
                        runningSocket.close();
                        runningSocket=null;
                    }
                    running=false;
                    receiveThread=null;
                }
            };
            receiveThread = new Thread(runner);
            receiveThread.start();
        } catch (Exception e) {
            receiveThread=null;
            running=false;
            System.err.println("Exception: " + e);
            //System.exit(-2); //2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return; //2012 03 23
        }
    }
    public int switchRTPParser(DatagramPacket dp) {
        //System.out.println("switched RTP:"+dp.getLength());
        if(sipSound!=null) //2012 03 22
        {
            //PAYLOAD TYPE
            rtpfieldBytes[0]=buffer[1];
            int payloadType = ((int)rtpfieldBytes[0]) & 0x7F;

            if(payloadType==SIPStack.SIP_CODEC_G711U)
            {
                if(sipSound.playStarted==true)
                {
                    if(SIPG711.decodeULawExt(buffer,12,160,pcmLinearBuffer)==true) {
                        if(bConferenceActive==true)
                        {
                            sipSound.confBufferring(pcmLinearBuffer,320,1);
                        }
                        else
                        {
                            sipSound.playAudio(pcmLinearBuffer,pcmLinearBuffer.length);
                            bMasterPlayReady=true;
                        }
                    }
                }
            }
            else if(payloadType==SIPStack.SIP_CODEC_G711A)
            {
                if(sipSound.playStarted==true)
                {
                    if(SIPG711.decodeALawExt(buffer,12,160,pcmLinearBuffer)==true) {
                        if(bConferenceActive==true)
                        {
                            sipSound.confBufferring(pcmLinearBuffer,320,1);
                        }
                        else
                        {
                            sipSound.playAudio(pcmLinearBuffer,pcmLinearBuffer.length);
                            bMasterPlayReady=true;
                        }
                    }
                }
            }
            //2012 02 21
            else if(payloadType==RFC2833.payloadType)
            {
				/*
				0                   1                   2                   3
			    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
			   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			   |     event     |E|R| volume    |          duration             |
			   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
				 */
                int field=0;
                byte[] rfcdata=new byte[4];
                //0..7 EVENT
                int dtmf=(int)buffer[12];
                //8..15 EDGE|RESERVED|VOLUME
                field=(int)buffer[13];
                int edge=(field & 0x00000080) >> 7;
                int volume=field & 0x0000003F;
                //16..32 DURATION
                int duration=((int)buffer[14] << 24)>>16 & 0x0000FF00;
                duration |= ((int)buffer[15] << 24)>>24 & 0x000000FF;
                //
                if(bDtmfDetected==true && detectedDtmf==dtmf && edge==1)
                {
                    detectedDtmf=0;
                    bDtmfDetected=false;
                }
                else if(edge!=1 && bDtmfDetected==false)
                {
                    bDtmfDetected=true;detectedDtmf=dtmf;
                }
            }
        }


        return 0;//normal

    }//switchRTPParser()

    //
    public void RTPLoopSet(boolean bSet)
    {
        bRTPparsing=false;
        bRTPlooping=bSet;
        return;
    }
    public void RTPParseSet(boolean bSet)
    {
        bRTPlooping=false;
        bRTPparsing=bSet;
        return;
    }
    public boolean invokeRFC2833(int digit)
    {
        if(rfc2833==null) return false;
        if(digit<0 || digit>11) return false;

        rfc2833.setDtmf(digit, 4, 0, 960);
        return true;
    }
    public void setRemoteMediaInfo(int payloadtype,String mediaIp,int mediaPort)
    {
        this.remotePayloadType=payloadtype;
        this.remoteMediaIp=mediaIp;
        this.remoteMediaPort=mediaPort;
        if(mediaType		== SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            SIPSound.remotePayloadType=remotePayloadType;
        }
        return;
    }

    public void setConferenceRemoteMediaInfo(int payloadtype,String mediaIp,int mediaPort)
    {
        this.conferenceRemotePayloadType=payloadtype;
        this.conferenceRemoteMediaIp=mediaIp;
        this.conferenceRemoteMediaPort=mediaPort;
        return;
    }

    public boolean conferenceRTPEnd()
    {
        //Log.i("RTPEND","Debug 2");
        //2012 12 04
        //if(SIPStack.PRIMARY_CODEC_AUDIO	== SIPStack.SIP_CODEC_G729)
        //{
        //	SIPG729a.codecClose();
        //}
        //

        try {
            if(bConferenceActive==false) return false;

            bConferenceActive=false;

            if(this.conferenceReceiveThread!=null)
            {
                conferenceReceiveThread.interrupt();
                conferenceRunning=false;
                conferenceReceiveThread=null;
            }
            if(theConferenceSocket!=null) {
                theConferenceSocket.close();
                theConferenceSocket=null;
            }
        }catch (Exception e){
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        }

        SIPStack.freeAudioRtpPort(conferenceRTP_PORT);
        return true;
    }
    public boolean conferenceSwitchRTPEnd()
    {
        //2012 12 04
        //if(SIPStack.PRIMARY_CODEC_AUDIO	== SIPStack.SIP_CODEC_G729)
        //{
        //	SIPG729a.codecClose();
        //}
        //

        try {
            if(bConferenceActive==false) return false;

            bConferenceActive=false;

            if(this.conferenceReceiveThread!=null)
            {
                conferenceReceiveThread.interrupt();
                conferenceRunning=false;
                conferenceReceiveThread=null;
            }
            if(theConferenceSocket!=null) {
                theConferenceSocket.close();
                theConferenceSocket=null;
            }
        }catch (Exception e){
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
        }

        return true;
    }
    public void conferenceRTPInit(int port)
    {
        conferenceRTPversion	= 2;
        conferenceRTPpadding	= 0;
        conferenceRTPextension	= 0;
        conferenceRTPcsrc		= 0;
        conferenceRTPmark		= 0;
        conferenceRTPpayload	= 0;
        conferenceRTPsequence	= 0;
        Date date=new Date();

        conferenceRTPtimestamp	= (int)date.getTime();
        conferenceRTPssrc		= (int)date.getTime();

        bMasterPlayReady		= false;
        bConferencePlayReady	= false;


        //
        if(this.conferenceRunning!=true) {
            this.setConferenceRTPPort(port);
            this.receiveConferenceAudio();
        }
        bConferenceActive=true;
        //
        return;

    }
    public boolean setConferenceRTPPort(int port)
    {
        if(port<=0) return false;
        conferenceRTP_PORT=port;
        return true;
    }

    public void receiveConferenceAudio() {
        if(conferenceReceiveThread!=null) return;
        try {
            Runnable runner = new Runnable() {
                DatagramSocket 				runningSocket		= null;

                public void run() {
                    try {
                        theConferenceSocket=new DatagramSocket(conferenceRTP_PORT);
                        if(theConferenceSocket==null) return;//2012 03 23
                        theConferenceSocket.setReuseAddress(true);
                        conferenceDp = new DatagramPacket(conferenceBuffer,conferenceBuffer.length);
                        if(conferenceDp==null)  return;//2012 03 23
                        runningSocket=theConferenceSocket;

                    } catch(SocketException se)
                    {
                        System.err.println(se);
                        SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                        return;
                    } catch(NullPointerException ne) {
                        System.err.println(ne);
                        SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                        return;
                    }
                    conferenceRunning=true;
                    while(conferenceRunning==true && runningSocket!=null) {
                        if(conferenceReceiveThread.isInterrupted()==true) break;
                        try {
                            conferenceDp.setData(conferenceBuffer);
                            conferenceDp.setLength(SIPStack.RTP_AUDIO_SIZE);

                            runningSocket.receive(conferenceDp);
                            if(0 != conferenceRTPParser(conferenceDp))
                            {
                            }
                        }
                        catch(SocketException se) {
                            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                            break;
                        }catch(Exception e)
                        {
                            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                            break;
                        }
                    }//while
                    if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("Conference RTP Thread terminated.");
                    if(runningSocket!=null) {
                        if(runningSocket==theConferenceSocket) theConferenceSocket=null;
                        runningSocket.close();
                        runningSocket=null;
                    }
                    conferenceRunning=false;
                    conferenceReceiveThread=null;
                }
            };
            conferenceReceiveThread = new Thread(runner);
            conferenceReceiveThread.start();
        } catch (Exception e) {
            conferenceReceiveThread	= null;
            conferenceRunning		= false;
            System.err.println("Exception: " + e);
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            //System.exit(-2); 2012 03 23
            return;//2012 03 23
        }
    }
    public int conferenceRTPParser(DatagramPacket dp) {
        if(sipSound!=null) //2012 03 22
        {
            //PAYLOAD TYPE
            conferenceRtpfieldBytes[0]=conferenceBuffer[1];
            int payloadType = ((int)conferenceRtpfieldBytes[0]) & 0x7F;

            if(payloadType==SIPStack.SIP_CODEC_G711U)
            {
                if(sipSound.playStarted==true)
                {
                    if(SIPG711.decodeULawExt(conferenceBuffer,12,160,conferencePcmLinearBuffer)==true) {
                        bConferencePlayReady=true;
                        sipSound.confBufferring(conferencePcmLinearBuffer,320,2);

                    }

                }

            }
            else if(payloadType==SIPStack.SIP_CODEC_G711A)
            {

                if(sipSound.playStarted==true)
                {
                    if(SIPG711.decodeALawExt(conferenceBuffer,12,160,conferencePcmLinearBuffer)==true) {
                        bConferencePlayReady=true;
                        sipSound.confBufferring(conferencePcmLinearBuffer,320,2);
                    }

                }

            }
        }


        return 0;//normal

    }//conferenceRTPParser()
    public void conferenceRtpWriteExt(byte[] data,int datasize,int codec,String ip,int port)
    {
        int packetsize=0;
        boolean bSend=false;
        //DTMF PROCESSING
        if(codec==SIPStack.SIP_CODEC_G711U)
        {
            packetsize=SIPG711.encode2ULawExt(data,datasize,conferenceRtpPacketBuffer,12);
        }
        else if(codec==SIPStack.SIP_CODEC_G711A)
        {
            packetsize=SIPG711.encode2ALawExt(data,datasize,conferenceRtpPacketBuffer,12);
        }
        else {
            return;
        }
        if(conferenceRtpPacketBuffer !=null && packetsize>0)
        {
            bSend=
                    encodeRtpHeader(
                            conferenceRtpPacketBuffer,
                            conferenceRTPversion,
                            conferenceRTPpadding,
                            conferenceRTPextension,
                            conferenceRTPcsrc,
                            conferenceRTPmark,
                            codec,
                            conferenceRTPsequence,
                            conferenceRTPtimestamp,
                            conferenceRTPssrc
                    );
        }
        if(bSend==true)
        {
            conferenceRTPsequence++;
            conferenceRTPtimestamp+=160;
            SendConferenceRTPPacket(ip,port,conferenceRtpPacketBuffer,packetsize);
        }

        return;
    }
    public int SendConferenceRTPPacket(String sIp,int iPort,byte[] data,int dataSize)
    {
        int sent=0;
        if(sIp==null || sIp.length()<=0) return 0;//2012 03 23
        if(data==null || dataSize<=0) return 0;//2012 03 23
        if(iPort<=0) return 0;
        if(theConferenceSocket==null || conferenceRunning==false) return 0;

        try {
            InetAddress ia=InetAddress.getByName(sIp);
            if(ia==null) return 0;//2012 03 23
            DatagramPacket dp=new DatagramPacket(data,dataSize,ia,iPort);
            if(dp==null) return 0;//2012 03 23
            theConferenceSocket.send(dp);

            sent=dataSize;

        } catch(UnknownHostException uhe) {
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            System.err.println(uhe);
        } catch(IOException ie){
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            System.err.println(ie);
        }

        return sent;
    }
    public int getkRtpArrivalMilliseconds() //milli seconds
    {
        Date currentTime=new Date();
        return (int)(currentTime.getTime()-rtpArrivalTime.getTime());
    }
    public int getkRtpArrivalSeconds() //milli seconds
    {
        Date currentTime=new Date();
        return (int)(currentTime.getTime()-rtpArrivalTime.getTime())/1000;
    }
    public void initSound()
    {
        //SOUND DEVICE PREPARE
        try {
            sipSound=new SIPSound();
        } catch(Exception e) {
            System.err.println("SIPSouond unavailable: " + e);
            //System.exit(-4); 2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return;//2012 03 23
        }
        //
        for(int i=0;i<SIPStack.SIP_MEDIA_PORTS;i++)
        {
            SIPStack.SIP_MEDIA_PORTFLAG[i]=false;
        }

        //
        return;
    }
    public void initSoundAndPrepareAudio()
    {
        //SOUND DEVICE PREPARE
        try {
            sipSound=new SIPSound();
        } catch(Exception e) {
            System.err.println("SIPSouond unavailable: " + e);
            //System.exit(-4); 2012 03 23
            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            return;//2012 03 23
        }
        //
        for(int i=0;i<SIPStack.SIP_MEDIA_PORTS;i++)
        {
            SIPStack.SIP_MEDIA_PORTFLAG[i]=false;
        }

        try {
            if(sipSound.playStarted!=true)
            {
                sipSound.prepareAudioTrack();
            }
            if(sipSound.recorderStarted!=true)
            {
                sipSound.captureAudio();
            }
            else {
                sipSound.closeCaptureDevice();
                sipSound.captureAudio();
            }
        }
        catch (Exception e)
        {
            SIPStack.exceptionCountAtCurrentCall++;
            return;
        }

        //
        return;
    }
    public void prepareAudio()
    {

        try {
            if(sipSound.playStarted!=true)
            {
                sipSound.prepareAudioTrack();
            }
            if(sipSound.recorderStarted!=true)
            {
                sipSound.captureAudio();
            }
            else {
                sipSound.closeCaptureDevice();
                sipSound.captureAudio();
            }
        }
        catch (Exception e)
        {
            SIPStack.exceptionCountAtCurrentCall++;
            return;
        }


    }
    public void resetAudio()
    {

        try {
            sipSound.closeAudioDevice();
        }
        catch (Exception e)
        {
            SIPStack.exceptionCountAtCurrentCall++;
            return;
        }


    }
    //
}//class RTPManager
