package com.kd.One.sip;

import java.util.StringTokenizer;

public class SIPHeader {
    String header=null;
    String headerValue=null;
    int headerType=SIPStack.SIP_HEADERTYPE_NONE;
    boolean flag=false;

    public SIPHeader(String message,int headerType)
    {
        String keyWord;
        String compactKeyWord=null;//2012 04 27

        if(message!=null)
        {
            if(headerType == SIPStack.SIP_HEADERTYPE_ACCEPT) keyWord = "Accept: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ACCEPTENCODING) keyWord = "Accept-Encoding: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ACCEPTLANGUAGE) keyWord = "Accept-Language: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ALERTINFO) keyWord = "Alert-Info: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ALLOW) keyWord = "Allow: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ALLOWEVENTS) keyWord = "Allow-Events: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_AUTHENTICATIONINFO) keyWord = "Authentication-Info: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_AUTHORIZATION) keyWord = "Authorization: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_CALLID) {
                keyWord = "Call-ID: ";
                compactKeyWord = "i: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_CALLINFO) keyWord = "Call-Info: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTACT) {
                keyWord = "Contact: ";
                compactKeyWord = "m: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTENTDISPOSITION) keyWord = "Content-Disposition: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTENTENCODING) keyWord = "Content-Encoding: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTENTLANGUAGE) keyWord = "Content-Language: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTENTLENGTH) {
                keyWord = "Content-Length: ";
                compactKeyWord = "l: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_CONTENTTYPE) {
                keyWord = "Content-Type: ";
                compactKeyWord = "c: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_CSEQ) keyWord = "CSeq: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_DATE) keyWord = "Date: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_EVENT) keyWord = "Event: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ERRORINFO) keyWord = "Error-Info: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_EXPIRES) keyWord = "Expires: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_FROM) {
                keyWord = "From: ";
                compactKeyWord = "f: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_INREPLYTO) keyWord = "In-Reply-To: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_MAXFORWARDS) keyWord = "Max-Forwards: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_MINEXPIRES) keyWord = "Min-Expires: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_MIMEVERSION) keyWord = "MIME-Version: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ORGANIZATION) keyWord = "Organization: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_PRIORITY) keyWord = "Priority: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE) keyWord = "Proxy-Authenticate: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_PROXYAUTHORIZATION) keyWord = "Proxy-Authorization: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_PROXYREQUIRE) keyWord = "Proxy-Require: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_PASSERTEDIDENTITY) keyWord = "P-Asserted-Identity: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_RECORDROUTE) keyWord = "Record-Route: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_REPLYTO) keyWord = "Reply-To: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_REQUIRE) keyWord = "Require: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_RETRYAFTER) keyWord = "Retry-After: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_ROUTE) keyWord = "Route: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_SERVER) keyWord = "Server: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_SUBJECT) keyWord = "Subject: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_SUPPORTED) keyWord = "Supported: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_TIMESTAMP) keyWord = "Timestamp: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_TO) {
                keyWord = "To: ";
                compactKeyWord = "t: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_UNSUPPORTED) keyWord = "Unsupported: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_USERAGENT) keyWord = "User-Agent: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_VIA) {
                keyWord = "Via: ";
                compactKeyWord = "v: ";//2012 04 27
            }
            else if(headerType == SIPStack.SIP_HEADERTYPE_WARNING) keyWord = "Warning: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE) keyWord = "WWW-Authenticate: ";
            else if(headerType == SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE) keyWord = "Www-Authenticate: ";//2013 02 12
            else keyWord=null;

            if(keyWord != null)
            {
                int iS=message.indexOf(keyWord);
                int iE=0;
                if(iS>=0) {//2012 03 22
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                    if(iE>0) {
                        header=message.substring(iS,iE);
                        headerValue=message.substring(iS+keyWord.length(),iE);
                        this.headerType=headerType;
                        flag=true;
                    }

                }
                else flag=false;
            }//if
            if(flag==false && compactKeyWord != null)//2012 04 27
            {
                int iS=message.indexOf(compactKeyWord);
                int iE=0;
                if(iS>=0) {
                    iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                    if(iE>0) {
                        header=message.substring(iS,iE);
                        headerValue=message.substring(iS+compactKeyWord.length(),iE);
                        this.headerType=headerType;
                        flag=true;
                    }

                }
                else flag=false;
            }//if

        }
    }

    public int callSequenceNumber()
    {
        int seqCode=0;
        if(this.headerType==SIPStack.SIP_HEADERTYPE_CSEQ && header != null && header.length()>0)
        {
            //
            StringTokenizer tokenArray= new StringTokenizer(header," ",true);
            int tokenCount=0;
            while(tokenArray.hasMoreTokens())
            {
                String token=tokenArray.nextToken();

                if(token.length()>0 && token.compareTo(" ")!=0)
                {
                    tokenCount++;
                    if(tokenCount==2)
                    {
                        seqCode=Integer.parseInt(token);
                        break;
                    }
                }
            }

            //
        }
        return seqCode;
    }
    public String getTag()
    {
        String tag=null;
        if(
                (this.headerType==SIPStack.SIP_HEADERTYPE_FROM || this.headerType==SIPStack.SIP_HEADERTYPE_TO)
                        && header != null && header.length()>0)
        {
            //
            StringTokenizer tokenArray= new StringTokenizer(header,";",true);
            while(tokenArray.hasMoreTokens())
            {
                String token=tokenArray.nextToken();

                if(token.length()>0 && token.startsWith("tag=")==true)
                {
                    tag=token.substring(4);
                    break;

                }
            }

            //
        }
        return tag;

    }
    public String getId()
    {
        String id="";//2012 03 22
        if(
                (this.headerType==SIPStack.SIP_HEADERTYPE_FROM || this.headerType==SIPStack.SIP_HEADERTYPE_TO)
                        && header != null && header.length()>0)
        {
            int iS=0;
            int iE=0;
            iS=header.indexOf("sip:");
            if(iS>0)
            {
                iE=header.indexOf("@",iS);
                if(iE>0)
                {
                    id=header.substring(iS+4,iE).trim();
                }
                //
            }
            //
        }
        return id;

    }
    public String getFieldValue(String fieldName)
    {
        if(headerValue==null || headerValue.length()<=0) return null;
        if(fieldName==null || fieldName.length()<=0) return null;

        StringTokenizer tokenArray= new StringTokenizer(headerValue,";><",true);
        String keyWord=fieldName+"=";
        String fieldValue=null;
        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();

            if(token.length()>0 && token.startsWith(keyWord)==true)
            {
                fieldValue=token.substring(keyWord.length());
                break;
            }
        }
        //return keyWord; original
        return fieldValue;
    }
}
