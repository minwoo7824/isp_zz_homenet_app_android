package com.kd.One.sip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class VideoDecode {
    static public int 		DEFAULT_RTP_PORT= 3002;
    static public int 		VIDEO_CODEC_H264= 96;
    DatagramSocket runningSocket	= null;
    DatagramPacket dp				= null;
    Socket tcpSocket		= null;
    public boolean 			bReceiveRunning	= false;

    public byte[] 			buffer 			= null;
    public byte[] 			rtpVideoBuffer	= null;
    public byte[] 			data			= null;
    public byte[]			sizeBuffer		= new byte[20];
    public static byte[]	netdata			= new byte[50000];
    public static int 		netdatasize 	= 0;
    public int 				RTP_VIDEO_SIZE	= 50000;
    public static boolean 	bInvalidate		= false;

    //RTP ENCODING FACTOR
    public int 				RTPversion		= 0;
    public int 				RTPpadding		= 0;
    public int 				RTPextension	= 0;
    public int 				RTPcsrc			= 0;
    public int 				RTPmark			= 0;
    public int 				RTPpayload		= 0;
    public int 				RTPsequence		= 0;
    public int 				RTPtimestamp	= 0;
    public int 				RTPssrc			= 0;
    public boolean 			bService 		= false;
    public String 			remoteIp 		= "127.0.0.1";
    public int 				remotePort 		= 3002;
    public byte[] 			rtpfieldBytes	= null;
    public boolean 			bIframeset 		= false;
    public boolean 			bIframedoubleset= false;
    public boolean 			bView 			= false;

    ////////////////////////////////////////////////
    public int 				PARSEversion	= 0;
    public int 				PARSEpadding	= 0;
    public int 				PARSEextension	= 0;
    public int 				PARSEcsrc		= 0;
    public int 				PARSEmark		= 0;
    public int 				PARSEpayload	= 0;
    public int 				PARSEsequence	= 0;
    public int 				PARSEtimestamp	= 0;
    public int 				PARSEssrc		= 0;

    public int 				PARSEview 		= 0;
    public int 				lastSequence	= 0;
    public Date frameCheckTime  = null;
    public int 				frameCount 		= 0;
    public boolean 			bViewme			= false;
    public static boolean 	bRemoteVideoExist=false;
    public static Queue<VideoPacket> videosPool = null;
    public static int MAX_PACKETPOOL_SIZE	= 10;//30;

    ////////////////////////////////////////////////////////////////////
    public VideoDecode()
    {
        buffer = new byte[RTP_VIDEO_SIZE];
        rtpVideoBuffer=new byte[RTP_VIDEO_SIZE];
        data=new byte[RTP_VIDEO_SIZE];
        VideoDecode.videosPool= new LinkedList<VideoPacket>();

        RTPversion		= 0;
        RTPpadding		= 0;
        RTPextension	= 0;
        RTPcsrc			= 0;
        RTPmark			= 0;
        RTPpayload		= 0;
        RTPsequence		= 0;
        RTPtimestamp	= 0;
        RTPssrc			= 0;
        bService 		= false;
        remoteIp 		= "127.0.0.1";
        remotePort 		= 3002;
        rtpfieldBytes	= new byte[4];
        bIframeset 		= false;
        bIframedoubleset= false;
        bView 			= false;
        bRemoteVideoExist=false;
        frameCheckTime	= new Date();

    }
    public void init()
    {
        RTPversion		= 0;
        RTPpadding		= 0;
        RTPextension	= 0;
        RTPcsrc			= 0;
        RTPmark			= 0;
        RTPpayload		= 0;
        RTPsequence		= 0;
        RTPtimestamp	= 0;
        RTPssrc			= 0;
        bService 		= false;
        remoteIp 		= "127.0.0.1";
        remotePort 		= 3002;
        bIframeset 		= false;
        bIframedoubleset= false;
        bView 			= false;
        bRemoteVideoExist=false;
        frameCheckTime	= new Date();
        return;
    }
    public boolean encodeRtpHeader(
            int payload
    )
    {
        if(rtpVideoBuffer==null || rtpVideoBuffer.length<12) return false;
        // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
        // 2 1 1 4  1 7  16              32        32
        int iField=0;
        int iAppend=0;
        //VERSION
        iField=(RTPversion << 6) & 0x000000C0; //version
        //PADDING
        iAppend=(RTPpadding << 5) & 0x00000020; //padding
        iField |= iAppend;
        //EXTENSION
        iAppend=(RTPextension << 4) & 0x00000010; //extension
        iField |= iAppend;
        //CSRC COUN
        iAppend=RTPcsrc & 0x0000000F; //csrc count
        iField |= iAppend;

        rtpVideoBuffer[0]=(byte)iField;
        iField=0;
        //MARK
        iField=(RTPmark << 7) & 0x00000080; //mark
        //PAYLOAD TYPE
        iAppend=payload & 0x0000007F; //payload
        iField |= iAppend;

        rtpVideoBuffer[1]=(byte)iField;
        iField=0;
        //SEQUENC NUMBER
        rtpVideoBuffer[2]=(byte)( (RTPsequence << 16) >> 24 );
        rtpVideoBuffer[3]=(byte)( (RTPsequence << 24) >> 24 );
        //TIME STAMP PARSING
        rtpVideoBuffer[4] =(byte)( RTPtimestamp >> 24 );
        rtpVideoBuffer[5] =(byte)( (RTPtimestamp << 8) >> 24 );
        rtpVideoBuffer[6] =(byte)( (RTPtimestamp << 16) >> 24 );
        rtpVideoBuffer[7] =(byte)( (RTPtimestamp << 24) >> 24 );
        //SSRC PARSING
        rtpVideoBuffer[8] =(byte)( RTPssrc >> 24 );
        rtpVideoBuffer[9] =(byte)( (RTPssrc << 8) >> 24 );
        rtpVideoBuffer[10] =(byte)( (RTPssrc << 16) >> 24 );
        rtpVideoBuffer[11] =(byte)( (RTPssrc << 24) >> 24 );

        //
        return true;

    }
    public boolean encodeHNSRtpHeader(
            int payload,
            boolean bView
    )
    {
        if(rtpVideoBuffer==null || rtpVideoBuffer.length<12) return false;
        // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
        // 2 1 1 4  1 7  16              32        32
        int iField=0;
        int iAppend=0;
        //VERSION
        iField=(RTPversion << 6) & 0x000000C0; //version
        //PADDING
        iAppend=(RTPpadding << 5) & 0x00000020; //padding
        iField |= iAppend;
        //EXTENSION
        iAppend=(RTPextension << 4) & 0x00000010; //extension
        iField |= iAppend;
        //CSRC COUN
        iAppend=RTPcsrc & 0x0000000F; //csrc count
        iField |= iAppend;

        rtpVideoBuffer[0]=(byte)iField;
        iField=0;
        //MARK
        iField=(RTPmark << 7) & 0x00000080; //mark
        //PAYLOAD TYPE
        iAppend=payload & 0x0000007F; //payload
        iField |= iAppend;

        rtpVideoBuffer[1]=(byte)iField;
        iField=0;
        //SEQUENC NUMBER
        rtpVideoBuffer[2]=(byte)( (RTPsequence << 16) >> 24 );
        rtpVideoBuffer[3]=(byte)( (RTPsequence << 24) >> 24 );
        //TIME STAMP PARSING
        rtpVideoBuffer[4] =(byte)( RTPtimestamp >> 24 );
        rtpVideoBuffer[5] =(byte)( (RTPtimestamp << 8) >> 24 );
        rtpVideoBuffer[6] =(byte)( (RTPtimestamp << 16) >> 24 );
        rtpVideoBuffer[7] =(byte)( (RTPtimestamp << 24) >> 24 );
        //SSRC PARSING
        rtpVideoBuffer[8] =(byte)( RTPssrc >> 24 );
        rtpVideoBuffer[9] =(byte)( (RTPssrc << 8) >> 24 );
        rtpVideoBuffer[10] =(byte)( (RTPssrc << 16) >> 24 );
        rtpVideoBuffer[11] =(byte)( (RTPssrc << 24) >> 24 );
        //VIEW MARK HNC FACTOR
        if(bView==true) iField=1 &  0x00000001; //video view
        else  iField= 0 &  0x00000001; //video view
        rtpVideoBuffer[12]=(byte)iField;
        //
        return true;

    }
    public boolean parseRTPHeader()
    {
        try
        {
            PARSEversion	= 0;
            PARSEpadding	= 0;
            PARSEextension	= 0;
            PARSEcsrc		= 0;
            PARSEmark		= 0;
            PARSEpayload	= 0;
            PARSEsequence	= 0;
            PARSEtimestamp	= 0;
            PARSEssrc		= 0;

            PARSEview 		= 0;

            //System.out.println("<<< receive:"+dp.getLength());
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
            PARSEmark = (((int)rtpfieldBytes[0]) >> 7) & 0x01;
            //System.out.println("Mark:"+PARSEmark);
            PARSEpayload = ((int)rtpfieldBytes[0]) & 0x7F;

            //SEQUENC NUMBER

            int sequence1=(int)buffer[2] & 0xFF;//((int)buffer[2] & 0x0F << 8) | ((int)buffer[3] & 0x0F);
            int sequence2=(int)buffer[3] & 0xFF;
            int sequence=(sequence1 << 8) | sequence2;

            if(lastSequence+1!=sequence)
                System.out.println("Sequence order not correct."+lastSequence+"/"+sequence);

            lastSequence=sequence;

            //System.out.println("seq: "+sequence+"("+sequence1+","+sequence2+")"+" payload:"+PARSEpayload);

        }catch(Exception e){return false;}
        return true;
    }
    public boolean parseTCPRTPHeader()
    {
        try
        {
            PARSEversion	= 0;
            PARSEpadding	= 0;
            PARSEextension	= 0;
            PARSEcsrc		= 0;
            PARSEmark		= 0;
            PARSEpayload	= 0;
            PARSEsequence	= 0;
            PARSEtimestamp	= 0;
            PARSEssrc		= 0;

            PARSEview 		= 0;

            //System.out.println("<<< receive:"+dp.getLength());
            // V|P|X|CC|M|PT|SEQUENCE NUMBER|TIMESTAMP|SSRCI|
            // 2 1 1 4  1 7  16              32        32

			/*
			//VERSION
			rtpfieldBytes[0]=buffer[0];
			int version = (((int)rtpfieldBytes[0]) >> 6) & 0x03;
			System.out.println("VERSION:"+version);
			 */

            //PAYLOAD TYPE
            rtpfieldBytes[0]=netdata[1];
            PARSEmark = (((int)rtpfieldBytes[0]) >> 7) & 0x01;
            //System.out.println("Mark:"+PARSEmark);
            PARSEpayload = ((int)rtpfieldBytes[0]) & 0x7F;

            //SEQUENC NUMBER
            int sequence1=(int)netdata[2] & 0xFF;//((int)buffer[2] & 0x0F << 8) | ((int)buffer[3] & 0x0F);
            int sequence2=(int)netdata[3] & 0xFF;
            int sequence=(sequence1 << 8) | sequence2;

            //Log.e("sequence", String.format("[FULL SEQ : %d]", sequence));

            if(lastSequence+1!=sequence)
                System.out.println("Sequence order not correct."+lastSequence+"/"+sequence);

            lastSequence=sequence;

            //System.out.println("seq: "+sequence+"("+sequence1+","+sequence2+")"+" payload:"+PARSEpayload);

        }catch(Exception e){return false;}
        return true;
    }
    public boolean parseHNSRTPHeader()
    {
        try
        {
            PARSEversion	= 0;
            PARSEpadding	= 0;
            PARSEextension	= 0;
            PARSEcsrc		= 0;
            PARSEmark		= 0;
            PARSEpayload	= 0;
            PARSEsequence	= 0;
            PARSEtimestamp	= 0;
            PARSEssrc		= 0;

            PARSEview 		= 0;

            //System.out.println("<<< receive:"+dp.getLength());
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
            PARSEmark = (((int)rtpfieldBytes[0]) >> 7) & 0x01;
            PARSEpayload = ((int)rtpfieldBytes[0]) & 0x7F;

            rtpfieldBytes[0]=buffer[12];
            PARSEview = ((int)rtpfieldBytes[0]) & 0x01;

            if(PARSEview==1) bView=true;
            else bView=false;


        }catch(Exception e){return false;}
        return true;
    }

    public static String getMediaDescription(String ip)
    {
        //if(bRemoteVideoExist==false) return "";
        try
        {
            String mediaDesc="m=video "+VideoDecode.DEFAULT_RTP_PORT+" RTP/AVP "+VideoDecode.VIDEO_CODEC_H264+"\r\n"+
                    "c=IN IP4 "+ip+"\r\n"+
                    "a=rtpmap:"+VideoDecode.VIDEO_CODEC_H264+" H264/90000\r\n"+
                    "a=fmtp:"+VideoDecode.VIDEO_CODEC_H264+" profile-level-id=42801f\r\n"+
                    "a=recvonly\r\n";
            return mediaDesc;
        }catch(Exception e){}
        return "";
    }


}
