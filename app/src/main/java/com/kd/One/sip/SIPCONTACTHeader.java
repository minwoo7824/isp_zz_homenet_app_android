package com.kd.One.sip;

public class SIPCONTACTHeader {
    String header		= null;
    String headerValue	= null;
    int	expires			= -1;
    //
    String display		= null;
    String username		= null;
    String sipuri		= null;
    String uriusertag	= null;//< ;user=phone>
    String ip			= null;
    int port			= 0;

    String tag			= null;
    //
    boolean flag=false;

    public SIPCONTACTHeader(String message)
    {
        if(message!=null && message.length()>0)
        {
            //GET CONTACT HEADER
            int iS=message.indexOf("Contact: ");
            int iE=0;
            if(iS<0) {
                iS=message.indexOf(SIPStack.SIP_LINE_END+"m: ");//comfact type
                if(iS>=0) iS+=2;
            }

            if(iS>=0)
            {
                iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                if(iE>0)
                {
                    header=message.substring(iS,iE);
                    if(header.startsWith("Contact: ")==true)
                    {
                        headerValue=header.substring(9);
                    }
                    else if(header.startsWith("m: ")==true)
                    {
                        headerValue=header.substring(3);
                    }
                    //EXPIRES GET
                    String strExpires=null;
                    iS=headerValue.indexOf(";expires=");
                    iE=0;
                    if(iS>=0) {
                        iE=headerValue.indexOf(";",iS+1);
                        //2012 1 13
                        int iE_=headerValue.indexOf(",",iS+1);
                        if(iE_>0 && iE_<iE) iE=iE_;
                        //2012 03 23
                        if(iE>0) {
                            strExpires=headerValue.substring(iS,iE);
                        }
                        else if(iE<0) {
                            strExpires=headerValue.substring(iS);
                        }
                        //if(strExpires.length()>0)
                        if(strExpires!=null && strExpires.length()>0)//2012 03 07
                        {
                            expires=Integer.parseInt(strExpires.substring(9).trim());
                        }
                        //
                    }
                }
            }
            iS=0;
            iE=0;
            if(headerValue != null && headerValue.length()>0)
            {
                //DISPLAY
                iE=headerValue.indexOf("<sip:");
                if(iE>0)
                {
                    String str=null;
                    str=headerValue.substring(0,iE).trim();
                    if(str != null && str.length()>1
                            && str.startsWith("\"")==true
                            && str.endsWith("\"")==true
                    )
                    {
                        this.display=str.substring(1,str.length()-1);
                    }
                    else if(str != null && str.length()>0) this.display=str;
                }
                //URI
                iS=headerValue.indexOf("sip:");
                if(iS>0)
                {
                    String str=null;
                    iE=headerValue.indexOf(">",iS+4);
                    if(iE<0) iE=headerValue.indexOf(";",iS+4);
                    if(iE>0) str=headerValue.substring(iS,iE);
                    else str=headerValue.substring(iS);
                    if(str != null && str.length()>0) this.sipuri=str.trim();
                }
                //USERNAME
                if(this.sipuri != null && this.sipuri.length()>0)
                {
                    iS=this.sipuri.indexOf("@",4);
                    if(iS>0)
                    {
                        this.username=this.sipuri.substring(4,iS).trim();
                    }
                }
                //HOST
                if(this.sipuri != null && this.sipuri.length()>0)
                {
                    iS=this.sipuri.indexOf("@",4);
                    iE=this.sipuri.indexOf(";",4);
                    String str=null;
                    if(iS>=0 && iE>=0)
                    {
                        str=this.sipuri.substring(iS+1,iE).trim();
                    }
                    else if(iS>=0)
                    {
                        str=this.sipuri.substring(iS+1).trim();
                    }
                    else if(iE>=0)
                    {
                        str=this.sipuri.substring(4,iE).trim();
                    }
                    else {
                        str=this.sipuri.substring(4).trim();
                    }

                    //PORT
                    this.port=5050;
                    if(str != null && str.length()>0)
                    {
                        iS=str.indexOf(":");
                        if(iS>0) {
                            this.port=Integer.parseInt(str.substring(iS+1).trim());
                            this.ip=str.substring(0,iS);
                        }
                        else {
                            this.ip=str;
                        }
                    }
                }
                //TAG
                iS=headerValue.indexOf(";tag=");
                if(iS>0)
                {
                    String str=null;
                    iE=headerValue.indexOf(";",iS+5);
                    if(iE>=0) {
                        if(iE>iS+5) str=headerValue.substring(iS+5,iE);
                        else str="";
                    }
                    else str=headerValue.substring(iS+5);
                    if(str != null && str.length()>0) this.tag=str.trim();
                }
                //URIUSERTAG
                iS=headerValue.indexOf("sip:");
                if(iS>0)
                {
                    String str=null;
                    iE=headerValue.indexOf(">",iS+4);
                    if(iE>0) str=headerValue.substring(iS,iE);
                    if(str != null && str.length()>0)
                    {
                        iS=str.indexOf(";user=",iS+4);
                        if(iS>0) {
                            this.uriusertag=str.substring(iS+6);
                            if(this.uriusertag != null && this.uriusertag.length()>0 && this.uriusertag.indexOf(";")>=0)
                            {
                                if(this.uriusertag.indexOf(";")==0) this.uriusertag=null;
                                else this.uriusertag=this.uriusertag.substring(0,this.uriusertag.indexOf(";")).trim();
                            }
                        }
                    }
                }

                //
                flag=true;

            }
            //

        }
    }
    public SIPCONTACTHeader(String message,String requesturi)
    {
        if(message!=null && message.length()>0)
        {
            //GET CONTACT HEADER
            int iS=message.indexOf("Contact: ");
            int iE=0;
            if(iS<0) {
                iS=message.indexOf(SIPStack.SIP_LINE_END+"m: ");//comfact type
                if(iS>=0) iS+=2;
            }

            if(iS>=0)
            {
                iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                if(iE>0)
                {
                    header=message.substring(iS,iE);
                    if(header.startsWith("Contact: ")==true)
                    {
                        headerValue=header.substring(9);
                    }
                    else if(header.startsWith("m: ")==true)
                    {
                        headerValue=header.substring(3);
                    }
                }
            }
            iS=0;
            iE=0;
            if(headerValue != null && headerValue.length()>0)
            {
                //DISPLAY
                this.display="";

                String tempValue=headerValue;
                iS=tempValue.indexOf("sip:");
                String str="";
                boolean bFound=false;
                while(iS>=0)
                {
                    tempValue=tempValue.substring(iS);
                    //System.out.println("debug 1-1"+tempValue);
                    iE=tempValue.indexOf(">",4);
                    if(iE<0) iE=tempValue.indexOf(";",4);
                    if(iE>0) str=tempValue.substring(0,iE);
                    else str=tempValue;
                    if(str != null && str.length()>0) this.sipuri=str.trim();
                    //System.out.println("!!!!!!!!! WHILE REQUEST URI GET==>"+this.sipuri);
                    if(
                            str != null &&
                                    str.length()>0 &&
                                    requesturi!=null && //2012 03 23
                                    requesturi.length()>0 &&  //2012 03 23
                                    str.trim().compareTo(requesturi)==0
                    )
                    {
                        bFound=true;
                        this.sipuri=str.trim();
                        //EXPIRES GET
                        String strExpires=null;
                        iS=tempValue.indexOf(";expires=");
                        iE=0;
                        if(iS>=0) {
                            iE=tempValue.indexOf(";",iS+1);
                            //2012 1 13
                            int iE_=tempValue.indexOf(",",iS+1);
                            if(iE_>0 && iE_<iE) iE=iE_;
                            //2012 03 23
                            if(iE>0) {
                                strExpires=tempValue.substring(iS,iE);
                            }
                            else if(iE<0) {
                                strExpires=tempValue.substring(iS);
                            }
                            if(strExpires!=null && strExpires.length()>0)
                            {
                                expires=Integer.parseInt(strExpires.substring(9).trim());
                            }
                            //
                        }
                        break;
                    }
                    tempValue=tempValue.substring(iE+1).trim();
                    iS=tempValue.indexOf("sip:");

                }
                if(bFound==true)
                {
                    //USERNAME
                    if(this.sipuri != null && this.sipuri.length()>0)
                    {
                        iS=this.sipuri.indexOf("@",4);
                        if(iS>0)
                        {
                            this.username=this.sipuri.substring(4,iS).trim();
                        }
                    }
                    //HOST
                    if(this.sipuri != null && this.sipuri.length()>0)
                    {
                        iS=this.sipuri.indexOf("@",4);
                        iE=this.sipuri.indexOf(";",4);
                        //String str=null;
                        if(iS>=0 && iE>=0)
                        {
                            str=this.sipuri.substring(iS+1,iE).trim();
                        }
                        else if(iS>=0)
                        {
                            str=this.sipuri.substring(iS+1).trim();
                        }
                        else if(iE>=0)
                        {
                            str=this.sipuri.substring(4,iE).trim();
                        }
                        else {
                            str=this.sipuri.substring(4).trim();
                        }

                        //PORT
                        this.port=5050;
                        if(str != null && str.length()>0)
                        {
                            iS=str.indexOf(":");
                            if(iS>0) {
                                this.port=Integer.parseInt(str.substring(iS+1).trim());
                                this.ip=str.substring(0,iS);
                            }
                            else {
                                this.ip=str;
                            }
                        }
                    }
                    //TAG
                    iS=headerValue.indexOf(";tag=");
                    if(iS>0)
                    {
                        //String str=null;
                        iE=headerValue.indexOf(";",iS+5);
                        if(iE>=0) {
                            if(iE>iS+5) str=headerValue.substring(iS+5,iE);
                            else str="";
                        }
                        else str=headerValue.substring(iS+5);
                        if(str != null && str.length()>0) this.tag=str.trim();
                        else this.tag="";//2012 03 22
                    }
                    //URIUSERTAG
                    iS=headerValue.indexOf("sip:");
                    if(iS>0)
                    {
                        //String str=null;
                        iE=headerValue.indexOf(">",iS+4);
                        if(iE>0) str=headerValue.substring(iS,iE);
                        if(str != null && str.length()>0)
                        {
                            iS=str.indexOf(";user=",iS+4);
                            if(iS>0) {
                                this.uriusertag=str.substring(iS+6);
                                if(this.uriusertag != null && this.uriusertag.length()>0 && this.uriusertag.indexOf(";")>=0)
                                {
                                    if(this.uriusertag.indexOf(";")==0) this.uriusertag=null;
                                    else this.uriusertag=this.uriusertag.substring(0,this.uriusertag.indexOf(";")).trim();
                                }
                            }
                        }
                    }
                    //
                    flag=true;
                }
            }
            //
        }
    }
    public String getDisplay()
    {
        return display;
    }
    public String getUsername()
    {
        return username;
    }
    public String getSipuri()
    {
        return sipuri;
    }
    public String getSipuriusertag()
    {
        return uriusertag;
    }
    public String getIp()
    {
        return ip;
    }
    public int getPort()
    {
        return port;
    }
    public String getTag()
    {
        return tag;

    }
    public int getExpires()
    {
        return this.expires;
    }
}
