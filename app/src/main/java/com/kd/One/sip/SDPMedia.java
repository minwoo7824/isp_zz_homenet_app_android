package com.kd.One.sip;

import java.util.StringTokenizer;

public class SDPMedia {

    int mediaType=SIPStack.SIP_MEDIATYPE_NONE;
    String mediaIp=null;
    int mediaPort=0;
    int flowIndicator=SIPStack.SIP_MEDIAFLOW_NONE;
    StringBuffer rtpmapBuffer=null;
    StringBuffer fmtpBuffer=null;
    String flow="sendrecv";
    StringBuffer codecS=null;
    int commonCodec=-1;
    //DEVELOPEMENT CONTROL
    protected boolean flag=false;

    String mediaString = null;


    public SDPMedia(int mediatype) {
        this.mediaType=mediatype;
        mediaIp=null;
        mediaPort	= 0;
        rtpmapBuffer= new StringBuffer();
        fmtpBuffer	= new StringBuffer();
        flow		= "sendrecv";
        codecS		= new StringBuffer();
        commonCodec	= -1;
        flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;//2012 03 24

        flag=true;
    }
    public SDPMedia(String mediadescribe,String platformIp) {
        //System.out.println("sdp:"+mediadescribe);
        //System.out.println("platformIp:"+platformIp);
        this.mediaType=SIPStack.SIP_MEDIATYPE_NONE;
        this.mediaIp=platformIp;
        mediaPort	= 0;
        rtpmapBuffer= new StringBuffer();
        fmtpBuffer	= new StringBuffer();
        flow		= "sendrecv";
        codecS		= new StringBuffer();
        commonCodec	= -1;
        flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;//2012 03 24
        boolean bContinueParse=true;
        if(mediadescribe!=null && mediadescribe.length()>0)
        {
            int iS=0;
            int iE=0;
            String header=null;
            //DECIDE MEDIA TYPE
            if(mediadescribe.startsWith("m=audio ")==true) this.mediaType=SIPStack.SIP_MEDIATYPE_AUDIO;
            else if(mediadescribe.startsWith("m=video ")==true) this.mediaType=SIPStack.SIP_MEDIATYPE_VIDEO;
            else bContinueParse=false;
            //Validate media description
            if(bContinueParse==true)
            {
                iE=mediadescribe.indexOf(SIPStack.SIP_LINE_END);
                if(iE>0) {
                    header=mediadescribe.substring(0,iE);
                    StringTokenizer st= new StringTokenizer(header," ",true);
                    int fieldCount=0;
                    while(st.hasMoreTokens())
                    {
                        String str=st.nextToken().trim();
                        //System.out.println("body parse:"+str);
                        if(str.length()>0 && str.compareTo(" ")!=0)
                        {

                            if(fieldCount==0)//m=audio
                            {
                                //2015 06 16 update
                                if(str.compareTo("m=audio")!=0 && str.compareTo("m=video")!=0)
                                {
                                    bContinueParse=false;
                                    break;
                                }
                            }
                            else if(fieldCount==1) //media port
                            {
                                mediaPort=Integer.parseInt(str);
                            }
                            else if(fieldCount==2) //RTP/AVP
                            {
                                if(str.compareTo("RTP/AVP")!=0)
                                {
                                    bContinueParse=false;
                                    break;
                                }
                            }
                            else if(fieldCount>2) //codec
                            {
                                if(codecS.length()>0) codecS.append(" "+str);
                                else codecS.append(""+str);
                                //System.out.println("codecS==>"+codecS);
                            }

                            fieldCount++;
                        }
                    }
                }
                else bContinueParse=false;
            }
            //
            if(bContinueParse==true)
            {
                StringTokenizer st= new StringTokenizer(mediadescribe,"\n",true);
                while(st.hasMoreTokens())
                {

                    String str=st.nextToken().trim();
                    if(str.length()>0 && str.startsWith("a=")==true)
                    {
                        if(str.startsWith("a=rtpmap:")==true)
                        {
                            rtpmapBuffer.append(str+"\n");
                        }
                        else if(str.startsWith("a=fmtp:")==true)
                        {
                            fmtpBuffer.append(str+"\n");
                        }
                        else if(str.startsWith("a=sendrecv")==true)
                        {
                            flow="sendrecv";
                            flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;
                        }
                        else if(str.startsWith("a=sendonly")==true)
                        {
                            flow="sendonly";
                            flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDONLY;
                        }
                        else if(str.startsWith("a=recvonly")==true)
                        {
                            flow="recvonly";
                            flowIndicator=SIPStack.SIP_MEDIAFLOW_RECVONLY;
                        }
                        //
                    }
                }

            }
            if(bContinueParse==true && mediadescribe.indexOf("\nc=IN IP4 ")>0)//
            {
                iS=0;iE=0;
                iS=mediadescribe.indexOf("c=");
                if(iS>=0)
                {//2012 03 23
                    iE=mediadescribe.indexOf(SIPStack.SIP_LINE_END,iS);
                    header="";
                    if(iS>=0 && iE>iS)
                    {
                        header=mediadescribe.substring(iS,iE+2);
                        iE=header.indexOf(SIPStack.SIP_LINE_END);
                        mediaIp=header.substring("c=IN IP4 ".length(),iE);
                        //if(SIPStack.SIP_CALLHANDLE_DEBUG==true)
                        //	System.out.println("MEDIA IP: "+mediaIp);
                    }
                }
            }

            //
        }

        if(bContinueParse==false) resetMedia();


        flag=true;
    }
    public void resetMedia()
    {
        mediaType	= SIPStack.SIP_MEDIATYPE_NONE;
        mediaIp		= null;
        mediaPort	= 0;
        rtpmapBuffer= null;
        fmtpBuffer	= null;
        flow		= "sendrecv";
        flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;
        codecS		= null;
        commonCodec	= -1;

        flag		= false;
        mediaString = null;
        return;
    }
    public String getMediaString()
    {
        if(this.flag==false) return null;

        String mString="";
        String aString="";
        String fmtpString="";
        String flowString="a="+flow+SIPStack.SIP_LINE_END;

        if(this.mediaType == SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            mString="m=audio "+mediaPort+" RTP/AVP "+codecS.toString()+SIPStack.SIP_LINE_END;
        }
        else if(this.mediaType == SIPStack.SIP_MEDIATYPE_VIDEO)
        {

        }
        else return null;
        if(rtpmapBuffer.length()>0) aString=rtpmapBuffer.toString();
        if(fmtpBuffer.length()>0) fmtpString=fmtpBuffer.toString();

        mediaString= mString +
                aString +
                fmtpString +
                flowString ;
        return mediaString;
    }
    public String getFinalMediaString()
    {
        //System.out.println("flag==>"+this.flag);
        if(this.flag==false) return null;
        //System.out.println("common codec==>"+commonCodec);
        if(commonCodec<0) return null;
        //System.out.println("media type==>"+this.mediaType);
        String mString="";
        String aString="";
        String flowString="a="+flow+SIPStack.SIP_LINE_END;
        if(this.mediaType == SIPStack.SIP_MEDIATYPE_AUDIO)
        {
            mString="m=audio "+mediaPort+" RTP/AVP "+commonCodec+SIPStack.SIP_LINE_END;
        }
        else if(this.mediaType == SIPStack.SIP_MEDIATYPE_VIDEO)
        {

        }
        else return null;
        if(commonCodec==0) aString="a=rtpmap:0 PCMU/8000"+SIPStack.SIP_LINE_END;
        else if(commonCodec==8) aString="a=rtpmap:8 PCMA/8000"+SIPStack.SIP_LINE_END;
        else if(commonCodec==18) aString="a=rtpmap:18 G729/8000"+SIPStack.SIP_LINE_END;

        mediaString= mString +
                aString +
                flowString ;
        return mediaString;
    }

    public boolean setMediaAddress(String ip,int port)
    {
        if(this.flag==false) return false;
        if(port<0 || port>65556) return false;
        this.mediaIp=ip;
        this.mediaPort=port;
        return true;
    }

    public boolean setCodec(int codec)
    {
        //System.out.println("SET CODEC:"+codec);
        if(this.flag==false) return false;
        if(codecS.length()>0) codecS.append(" "+codec);
        else codecS.append(""+codec);

        return true;
    }
    public boolean setCodec(int codec,String describe)
    {
        if(this.flag==false) return false;

        setCodec(codec);
        rtpmapBuffer.append("a=rtpmap:"+codec+" "+describe+SIPStack.SIP_LINE_END);

        return true;
    }
    public boolean setFmtpDescribe(int codec,String describe)
    {
        if(this.flag==false) return false;
        fmtpBuffer.append("a=fmtp:"+codec+" "+describe+SIPStack.SIP_LINE_END);

        return true;
    }
    public boolean setCommonCodec(int codec)
    {
        if(this.flag==false) return false;
        commonCodec=codec;
        return true;
    }
    public boolean setFlow(String flow)
    {
        if(flow==null) return false;//2012 03 23
        if(this.flag==false) return false;
        this.flow=flow;
        if(flow.compareTo("sendrecv")==0) this.flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;
        else if(flow.compareTo("sendonly")==0) this.flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDONLY;
        else if(flow.compareTo("recvonly")==0) this.flowIndicator=SIPStack.SIP_MEDIAFLOW_RECVONLY;
        else this.flowIndicator=SIPStack.SIP_MEDIAFLOW_NONE;
        return true;
    }
    //2012 03 07
    public int negotiateAudioCodec(String sideCodecs)
    {
        //System.out.println("negotiateAudioCodec===>side: "+sideCodecs+"  local:"+codecS.toString());
        if(codecS==null || codecS.length()<=0) return -1;
        String myCodecs=codecS.toString();
        myCodecs.trim();
        if(myCodecs==null || myCodecs.length()<=0) return -1;
        sideCodecs.trim();
        if(sideCodecs==null || sideCodecs.length()<=0) return -1;

        //MY CODEC TOKENIZER
        StringTokenizer my_st= new StringTokenizer(myCodecs," ",true);
        String str=null;
        int codec=-1;

        while(my_st.hasMoreTokens())
        {

            str=my_st.nextToken().trim();
            //System.out.println("my codec token:"+str);
            if(str==null || str.length()==0) continue;
            codec=Integer.parseInt(str);
            if(codec>=0 && codec<100)
            {
                //System.out.println("my process codec token:"+codec);
                //SIDE CODEC TOKENIZER
                StringTokenizer side_st= new StringTokenizer(sideCodecs," ",true);
                int sidecodec=-1;
                boolean bDecided=false;

                while(side_st.hasMoreTokens())
                {

                    str=side_st.nextToken().trim();
                    //System.out.println("side codec token:"+str);
                    if(str==null || str.length()==0) continue;
                    sidecodec=Integer.parseInt(str);
                    if(str.length()>0 && sidecodec>=0 && codec<100)
                    {
                        //System.out.println("process sede codec token:"+sidecodec);
                        if(sidecodec==codec) {
                            bDecided=true;
                            commonCodec=codec;
                            break;
                        }
                    }
                }
                if(bDecided==true) return commonCodec;
                else continue;


            }
            else continue;
        }
        return -1;


    }

}//class SDPMedia
