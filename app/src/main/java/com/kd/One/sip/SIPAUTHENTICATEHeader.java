package com.kd.One.sip;

public class SIPAUTHENTICATEHeader {
    String header		= null;
    String headerValue	= null;
    String realmValue	= null;
    String nonceValue	= null;
    String qopValue		= null;
    String strStale		= null;
    String strAlgorithm	= null;

    int headerType	= SIPStack.SIP_HEADERTYPE_NONE;
    boolean flag	= false;

    public SIPAUTHENTICATEHeader(String header,int headerType)
    {
        String keyWord;

        if(header!=null)
        {
            if(headerType == SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE) 			keyWord	= "WWW-Authenticate: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE) 		keyWord	= "Www-Authenticate: ";//2013 02 12
            else if(headerType == SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE) 	keyWord	= "Proxy-Authenticate: ";
            else keyWord=null;

            if(keyWord != null)
            {
                if(header.startsWith(keyWord)==true)
                {
                    this.headerType=headerType;
                    this.header=header;
                    flag=true;

                    //PARSE AUTHENTICATE FACTORS
                    //
                    int iS=0;
                    int iE=0;
                    //REALM GET
                    String strRealm=null;
                    iS=header.indexOf("realm=");
                    iE=0;
                    if(iS>=0) { //2012 03 22
                        iE=header.indexOf(",",iS);

                        if(iE>0) {
                            strRealm=header.substring(iS,iE);
                        }
                        else if(iE<0) {
                            strRealm=header.substring(iS);
                        }
                        if(strRealm.length()>0)
                        {
                            iS=strRealm.indexOf("=");
                            String strValue=strRealm.substring(iS+1);
                            strValue=strValue.trim();
                            if(strValue.indexOf("\"")==0) iS=1;
                            if(iS>0) iE=strValue.indexOf("\"",iS);
                            realmValue=strValue.substring(iS,iE);
                        }
                    }
                    else realmValue="";
                    //QOP GET
                    String strQop=null;
                    iS=header.indexOf("qop=");
                    iE=0;
                    if(iS>=0)
                    {
                        iE=header.indexOf(",",iS);
                        if(iE>0) {
                            strQop=header.substring(iS,iE);
                        }
                        else if(iE<0) {
                            strQop=header.substring(iS);
                        }
                        qopValue="";
                        if(strQop.length()>0)
                        {
                            iS=strQop.indexOf("=");
                            String strValue=strQop.substring(iS+1);
                            strValue=strValue.trim();
                            if(strValue.indexOf("\"")==0) iS=1;
                            if(iS>0) iE=strValue.indexOf("\"",iS);
                            qopValue=strValue.substring(iS,iE);
                            //System.out.println("QOP VALUE IS =======>"+qopValue);
                        }
                    }
                    else qopValue="";
                    //NONCE GET
                    String strNonce=null;
                    iS=header.indexOf("nonce=");
                    iE=0;
                    if(iS>=0)
                    {//2012 03 22
                        iE=header.indexOf(",",iS);
                        if(iE>0) {
                            strNonce=header.substring(iS,iE);
                        }
                        else if(iE<0) {
                            strNonce=header.substring(iS);
                        }
                        if(strNonce.length()>0)
                        {
                            iS=strNonce.indexOf("=");
                            String strValue=strNonce.substring(iS+1);
                            strValue=strValue.trim();
                            if(strValue.indexOf("\"")==0) iS=1;
                            if(iS>0) iE=strValue.indexOf("\"",iS);
                            nonceValue=strValue.substring(iS,iE);
                        }
                    }
                    else nonceValue="";
                    //STALE GET
                    iS=header.indexOf("stale=");
                    iE=0;
                    if(iS>=0)
                    { //2012 03 22
                        iE=header.indexOf(",",iS);
                        if(iE>0) {
                            strStale=header.substring(iS+6,iE);
                        }
                        else if(iE<0) {
                            strStale=header.substring(iS+6);
                        }
                    }
                    else strStale="";
                    //ALGORITHM GET
                    iS=header.indexOf("algorithm=");
                    iE=0;
                    if(iS>=0)
                    { //2012 03 22
                        iE=header.indexOf(",",iS);
                        if(iE>0) {
                            strAlgorithm=header.substring(iS+10,iE);
                        }
                        else if(iE<0) {
                            strAlgorithm=header.substring(iS+10);
                        }
                    }
                    else strAlgorithm="";
                }
            }//if

        }
    }
}
