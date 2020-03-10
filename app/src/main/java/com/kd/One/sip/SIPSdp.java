package com.kd.One.sip;

import java.util.Date;

public class SIPSdp {

    String vH				= null;
    String oH				= null;
    String sH				= null;
    String cH				= null;
    String tH				= null;
    SDPMedia	audioM 		= null;
    SDPMedia	videoM 		= null;


    String body				= null;
    String dn				= null;
    String platformIp		= null;
    String audioDescription = null;
    String videoDescription = null;

    //DEVELOPEMENT CONTROL
    protected boolean flag;


    public SIPSdp(String dn,String localIp) {
        //if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("sdp handle created.");

        this.dn=dn;
        this.platformIp=localIp;
        long ltime=new Date().getTime();

        vH="v=0"+SIPStack.SIP_LINE_END;
        oH="o="+dn+" "+ltime+" "+(ltime + 1)+" IN IP4 "+this.platformIp+SIPStack.SIP_LINE_END;
        sH="s=Smart Android call"+SIPStack.SIP_LINE_END;
        cH="c=IN IP4 "+this.platformIp+SIPStack.SIP_LINE_END;
        tH="t=0 0"+SIPStack.SIP_LINE_END;
        audioDescription="";
        videoDescription="";
        flag=true;
    }
    //2013 02 14
    public SIPSdp(String dn,String localIp,String localSdpIp) {
        //if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("sdp handle created.");

        this.dn=dn;
        this.platformIp=localIp;
        long ltime=new Date().getTime();

        vH="v=0"+SIPStack.SIP_LINE_END;
        oH="o="+dn+" "+ltime+" "+(ltime + 1)+" IN IP4 "+localSdpIp+SIPStack.SIP_LINE_END;
        sH="s=Smart Android call"+SIPStack.SIP_LINE_END;
        cH="c=IN IP4 "+localSdpIp+SIPStack.SIP_LINE_END;
        tH="t=0 0"+SIPStack.SIP_LINE_END;
        audioDescription="";
        videoDescription="";
        flag=true;
    }
    //
    public SIPSdp(String body) {
        if(body!=null && body.length()>0)//2012 03 23
        {
            this.body=body;
            int iS=0;
            int iE=0;
            //GET vH
            iS=body.indexOf("v=");
            if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
            if(iS>=0 && iE>iS)
            {
                vH=body.substring(iS,iE+2);
            }
            //GET oH
            iS=0;iE=0;
            iS=body.indexOf("o=");
            if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
            if(iS>=0 && iE>iS)
            {
                oH=body.substring(iS,iE+2);
            }
            //GET sH
            iS=0;iE=0;
            iS=body.indexOf("s=");
            if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
            if(iS>=0 && iE>iS)
            {
                sH=body.substring(iS,iE+2);
            }
            //GET cH
            iS=0;iE=0;
            iS=body.indexOf("c=");
            if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
            if(iS>=0 && iE>iS)
            {
                cH=body.substring(iS,iE+2);
            }
            //GET tH
            iS=0;iE=0;
            iS=body.indexOf("t=");
            if(iS>=0) iE=body.indexOf(SIPStack.SIP_LINE_END,iS);
            if(iS>=0 && iE>iS)
            {
                tH=body.substring(iS,iE+2);
            }
            if(
                    vH!=null && vH.length()>0 &&
                            oH!=null && oH.length()>0 &&
                            tH!=null && tH.length()>0
            )
            {
                //parse connection
                platformIp="0.0.0.0";
                //c=IN IP4 192.168.10.133
                if(cH != null && cH.length()>0 && cH.startsWith("c=IN IP4 ")==true)
                {
                    iE=cH.indexOf(SIPStack.SIP_LINE_END);
                    platformIp=cH.substring("c=IN IP4 ".length(),iE);
                }
                //GET m=audio
                iS=body.indexOf("m=audio ");
                if(iS>0)
                {
                    iE=body.indexOf("m=",iS+8);
                    if(iE>0 && iE>iS) audioM=new SDPMedia(body.substring(iS,iE),platformIp);
                    else if(iE<0) audioM=new SDPMedia(body.substring(iS),platformIp);
                }
                //GET m=video
                iS=body.indexOf("m=video ");
                if(iS>0)
                {
                    iE=body.indexOf("m=",iS+8);
                    if(iE>0 && iE>iS) videoM=new SDPMedia(body.substring(iS,iE),platformIp);
                    else if(iE<0) videoM=new SDPMedia(body.substring(iS),platformIp);
                    if(videoM!=null && videoM.flag==true) VideoDecode.bRemoteVideoExist=true;
                }
            }
            flag=true;
        }
        //
    }
    public boolean setMediaPort(int mediaType,int port)
    {
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            if(audioM==null) {
                audioM=new SDPMedia(mediaType);
                if(audioM!=null && audioM.flag==true) return audioM.setMediaAddress(platformIp,port);
            }
        }
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO)
        {
            if(videoM==null) {
                videoM=new SDPMedia(mediaType);
                if(videoM!=null && videoM.flag==true) return videoM.setMediaAddress(platformIp,port);
            }
        }


        return false;
    }
    public boolean setCodec(int mediaType,int codec)
    {
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            if(audioM!=null && audioM.flag==true) {
                return audioM.setCodec(codec);
            }
        }
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO)
        {
            if(videoM!=null && videoM.flag==true) {
                return videoM.setCodec(codec);
            }
        }


        return false;
    }
    public boolean setCodec(int mediaType,int codec,String describe)
    {
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            if(audioM!=null && audioM.flag==true) {
                return audioM.setCodec(codec,describe);
            }
        }
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO)
        {
            if(videoM!=null && videoM.flag==true) {
                return videoM.setCodec(codec,describe);
            }
        }


        return false;
    }
    public boolean setFmtpDescribe(int mediaType,int codec,String describe)
    {
        if(mediaType==SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            if(audioM!=null && audioM.flag==true) {
                return audioM.setFmtpDescribe( codec,describe);
            }
        }
        else if(mediaType==SIPStack.SIP_MEDIATYPE_VIDEO)
        {
            if(videoM!=null && videoM.flag==true) {
                return videoM.setFmtpDescribe(codec,describe);
            }
        }

        return false;
    }

    public void resetSdp()
    {

        flag=false;
        return;
    }
    public String getBodyString(
    )
    {
        audioDescription="";
        videoDescription="";
        if(this.flag==false) return null;
        if(audioM != null && audioM.flag==true)
        {
            audioDescription=audioM.getMediaString();
            if(audioDescription==null) audioDescription="";
        }
        //2015 06 16
        if(videoM != null && videoM.flag==true)
        {
            videoDescription=VideoDecode.getMediaDescription(this.platformIp);//videoM.getMediaString();
            if(videoDescription==null) videoDescription="";
        }
        videoDescription=VideoDecode.getMediaDescription(this.platformIp);
        //


        body= vH +
                oH +
                sH +
                cH +
                tH +
                audioDescription +
                videoDescription;
        return body;
    }
    public String getFinalBodyString()
    {
        audioDescription="";
        videoDescription="";
        if(this.flag==false) return null;
        if(audioM != null && audioM.flag==true)
        {
            audioDescription=audioM.getFinalMediaString();
            //System.out.println("audio ===>"+audioDescription);
            if(audioDescription==null) audioDescription="";
        }
        //2015 06 16
        if(videoM != null && videoM.flag==true)
        {
            videoDescription=VideoDecode.getMediaDescription(this.platformIp);//videoM.getFinalMediaString();
            if(videoDescription==null) videoDescription="";
        }
        videoDescription=VideoDecode.getMediaDescription(this.platformIp);
        //

        body= vH +
                oH +
                sH +
                cH +
                tH +
                audioDescription +
                videoDescription;
        return body;

    }
    public boolean update(String body)
    {
        return true;
    }

}//class SIPSdp
