package com.kd.One.sip;

import java.util.StringTokenizer;

public class SIPVIAHeader {
    //Via: SIP/2.0/UDP 192.168.10.77:5060;branch=z9hG4bK-1323769392936-4;rport
    String header		= null;
    String headerValue	= null;
    //
    String branch		= null;
    String received		= null;
    int	rport			= -1;
    String ip			= null;
    int port			= 0;

    String tag			= null;
    boolean rportService=false;
    //
    boolean flag=false;

    public SIPVIAHeader(String message)
    {
        if(message!=null && message.length()>0)
        {
            rport=0;
            received=null;

            //GET CONTACT HEADER
            int iS=message.indexOf("Via: ");
            int iE=0;
            if(iS<0) {
                iS=message.indexOf(SIPStack.SIP_LINE_END+"v: ");//comfact type
                if(iS>=0) iS+=2;
            }

            if(iS>=0)
            {
                iE=message.indexOf(SIPStack.SIP_LINE_END,iS);
                if(iE>0)
                {
                    header=message.substring(iS,iE);
                    if(header.startsWith("Via: ")==true)
                    {
                        headerValue=header.substring(5);
                    }
                    else if(header.startsWith("v: ")==true)
                    {
                        headerValue=header.substring(3);
                    }
                }
            }

            if(headerValue!=null && headerValue.length()>0)
            {
                StringTokenizer st= new StringTokenizer(headerValue,";",true);
                int tokenCount=0;
                String token=null;
                //System.out.println(">>> "+headerValue);
                while(st.hasMoreTokens())
                {
                    token=st.nextToken().trim();
                    if(token!=null && token.length()>0 && token.compareTo(";")!=0)
                    {
                        tokenCount++;
                        if(tokenCount==1 && token.startsWith("SIP/")==true)
                        {
                            StringTokenizer tokenArray=new StringTokenizer(token," ",true);
                            int fieldCount=0;
                            while(tokenArray.hasMoreTokens())
                            {
                                String field=tokenArray.nextToken().trim();
                                if(field!=null && field.length()>0 && field.compareTo(" ")!=0)
                                {
                                    fieldCount++;
                                    if(fieldCount==2)
                                    {
                                        //HOST:PORT
                                        iS=field.indexOf(":");
                                        if(iS>0) {
                                            port=Integer.parseInt(field.substring(iS+1));
                                            ip=field.substring(0,iS);
                                            //System.out.println(">>> HOST:"+ip+" PORT:"+port);
                                        }
                                        else {
                                            port=5050;
                                            ip=field;
                                            //System.out.println(">>> HOST:"+ip+" PORT:default");
                                        }
                                    }
                                }
                            }
                        }
                        //branch tag get
                        else if(tokenCount!=1 && token.startsWith("branch=")==true)
                        {
                            branch=token.substring(7);
                            //System.out.println(">>> BRANCH:["+branch+"]");
                        }
                        //rport
                        else if(tokenCount!=1 && token.compareTo("rport")==0)
                        {
                            rportService=true;
                            //System.out.println(">>> RPORT SERVICE: true");
                        }
                        //rport=
                        else if(tokenCount!=1 && token.startsWith("rport=")==true)
                        {
                            rportService=true;
                            rport=Integer.parseInt(token.substring(6).trim());
                            //System.out.println(">>> RPORT SERVICE: true rport:"+rport);
                        }
                        //received=
                        else if(tokenCount!=1 && token.startsWith("received=")==true)
                        {
                            rportService=true;
                            received=token.substring(9).trim();
                            //System.out.println(">>> RPORT SERVICE: true received:"+received);
                        }
                        //System.out.println(">>>["+token+"]");
                    }
                }

                //
                flag=true;
            }

        }
    }
    public String getReceived()
    {
        return this.received;
    }
    public int getRport()
    {
        return this.rport;
    }
    public String getAddress()
    {
        return this.ip+":"+this.port;
    }
    public String getIp()
    {
        return this.ip;
    }
    public int getPort()
    {
        return this.port;
    }
    public String getBranch()
    {
        return this.branch;
    }
}
