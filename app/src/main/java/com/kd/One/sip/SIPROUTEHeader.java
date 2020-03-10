package com.kd.One.sip;

public class SIPROUTEHeader {
    String header		= null;
    String headerValue	= null;
    //
    String username		= null;
    String sipuri		= null;
    String uriusertag	= null;//< ;user=phone>
    String ip			= null;
    int port			= 0;

    String tag			= null;
    //
    boolean flag=false;

    public SIPROUTEHeader(String header)
    {
        int iS=0;
        int iE=0;
        if(header!=null && header.length()>0 && header.startsWith("Route: ")==true)
        {
            //GET CONTACT HEADER
            this.header=header;
            headerValue=header.substring(7).trim();
            if(headerValue != null && headerValue.length()>0)
            {
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
}
