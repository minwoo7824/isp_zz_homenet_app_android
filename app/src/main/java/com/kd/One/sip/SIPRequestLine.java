package com.kd.One.sip;

import java.util.StringTokenizer;

public class SIPRequestLine {
    String header;
    int iMethodType;
    String uri;
    int 	code;
    int 	version;
    int 	revision;
    int		iMessageType;
    boolean flag;

    public SIPRequestLine(String message) {
        header		= null;
        iMethodType		= SIPStack.SIP_METHODTYPE_NONE;
        uri			= null;
        version		= 2;
        revision	= 0;
        code		= 0;
        iMessageType	= SIPStack.SIP_MSGTYPE_NONE;

        flag		= false;
        if(message!=null)
        {
            //SIP PARSE
            StringTokenizer st= new StringTokenizer(message,"\n",true);
            int headerCount=0;
            while(st.hasMoreTokens())
            {
                header=st.nextToken();
                if(header.length()>1)
                {

                    //INVITE
                    if(headerCount==0 && header.startsWith("INVITE") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_INVITE;
                        flag=true;
                        break;
                    }
                    //BYE
                    else if(headerCount==0 && header.startsWith("BYE") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_BYE;
                        flag=true;
                        break;
                    }
                    //ACK
                    else if(headerCount==0 && header.startsWith("ACK") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_ACK;
                        flag=true;
                        break;
                    }
                    //CANCEL
                    else if(headerCount==0 && header.startsWith("CANCEL") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_CANCEL;
                        flag=true;
                        break;
                    }
                    //OPTIONS
                    else if(headerCount==0 && header.startsWith("OPTIONS") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_OPTIONS;
                        flag=true;
                        break;
                    }
                    //INFO
                    else if(headerCount==0 && header.startsWith("INFO") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_INFO;
                        flag=true;
                        break;
                    }
                    //PRACK
                    else if(headerCount==0 && header.startsWith("PRACK") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_PRACK;
                        flag=true;
                        break;
                    }
                    //MESSAGE
                    else if(headerCount==0 && header.startsWith("MESSAGE") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_MESSAGE;
                        flag=true;
                        break;
                    }
                    //SUBSCRIBE
                    else if(headerCount==0 && header.startsWith("SUBSCRIBE") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_SUBSCRIBE;
                        break;
                    }
                    //REFER
                    else if(headerCount==0 && header.startsWith("REFER") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_REFER;
                        break;
                    }
                    //NOTIFY
                    else if(headerCount==0 && header.startsWith("NOTIFY") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_NOTIFY;
                        break;
                    }
                    //PUBLISH
                    else if(headerCount==0 && header.startsWith("PUBLISH") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_PUBLISH;
                        break;
                    }
                    //REGISTER
                    else if(headerCount==0 && header.startsWith("REGISTER") && header.endsWith("SIP/2.0\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                        iMethodType=SIPStack.SIP_METHODTYPE_REGISTER;
                        flag=true;
                        break;
                    }
                    //RESPONSE
                    else if(headerCount==0 && header.startsWith("SIP/2.0") && header.endsWith("\r"))
                    {
                        iMessageType=SIPStack.SIP_MSGTYPE_RESPONSE;
                        iMethodType=SIPStack.SIP_METHODTYPE_NONE;
                        StringTokenizer tokenArray= new StringTokenizer(header," ",true);
                        int tokenCount=0;
                        //int iResponseCode=0;
                        while(tokenArray.hasMoreTokens())
                        {
                            String token=tokenArray.nextToken();
                            if(token.compareTo(" ")==0) continue;
                            tokenCount++;
                            if(tokenCount==2) code=Integer.parseInt(token);
                        }

                        int iS=message.indexOf("CSeq: ");
                        int iE=0;
                        if(iS>=0) { //2012 03 22
                            iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                            if(iE>0) {
                                //if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("CSEQ HEADER --- ["+s.substring(iS,iE));
                            }
                            else break;

                            String strCSEQ=message.substring(iS,iE);
                            tokenArray= new StringTokenizer(strCSEQ," ",true);
                            tokenCount=0;
                            while(tokenArray.hasMoreTokens())
                            {
                                String token=tokenArray.nextToken();

                                if(token.length()>0 && token.compareTo(" ")!=0)
                                {
                                    tokenCount++;
                                    if(tokenCount==3)
                                    {
                                        if(token.compareTo("REGISTER")==0) 		iMethodType=SIPStack.SIP_METHODTYPE_REGISTER;
                                        else if(token.compareTo("INVITE")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_INVITE;
                                        else if(token.compareTo("CANCEL")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_CANCEL;
                                        else if(token.compareTo("BYE")==0) 		iMethodType=SIPStack.SIP_METHODTYPE_BYE;
                                        else if(token.compareTo("INFO")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_INFO;
                                        else if(token.compareTo("OPTIONS")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_OPTIONS;
                                        else if(token.compareTo("ACK")==0) 		iMethodType=SIPStack.SIP_METHODTYPE_ACK;
                                        else if(token.compareTo("REFER")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_REFER;
                                        else if(token.compareTo("NOTIFY")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_NOTIFY;
                                        else if(token.compareTo("PUBLISH")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_PUBLISH;
                                        else if(token.compareTo("MESSAGE")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_MESSAGE;
                                        else if(token.compareTo("SUBSCRIBE")==0) iMethodType=SIPStack.SIP_METHODTYPE_SUBSCRIBE;
                                        else if(token.compareTo("PRACK")==0) 	iMethodType=SIPStack.SIP_METHODTYPE_PRACK;

                                        if(iMethodType!= SIPStack.SIP_METHODTYPE_NONE) flag=true;
                                        break;
                                    }
                                }
                            }
                            break;
                            //

                        }
                        else break;//2012 03 22

                    }
                }
            }//while
            if(flag==true && iMessageType==SIPStack.SIP_MSGTYPE_REQUEST && header!= null)
            {
                StringTokenizer tokenArray= new StringTokenizer(header," ",true);
                int tokenCount=0;
                //int iResponseCode=0;
                while(tokenArray.hasMoreTokens())
                {
                    String token=tokenArray.nextToken();
                    if(token.compareTo(" ")==0) continue;
                    //if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println(token);
                    tokenCount++;
                    if(tokenCount==2) uri=token;
                }

            }

        }//if
        //
    }//public SIPRequestLine(String message)


    public void print()
    {
        System.out.println("=====================================================");
        System.out.println("HEADER:");
        System.out.println(header);
        System.out.println(".....................................................");
        System.out.println("flag		:"+flag);
        System.out.println("iMethodType	:"+iMethodType);
        System.out.println("uri			:"+uri);
        System.out.println("code		:"+code);
        System.out.println("version		:"+version);
        System.out.println("revision	:"+revision);
        System.out.println("iMessageType:"+iMessageType);
        System.out.println("=====================================================");
    }


}
