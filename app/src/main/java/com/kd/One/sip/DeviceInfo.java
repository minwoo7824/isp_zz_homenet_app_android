package com.kd.One.sip;

import java.util.Date;

public class DeviceInfo {
    public static final int DEVICE_TYPE_NONE=0;
    public static final int DEVICE_TYPE_ANDROID=1;
    public static final int DEVICE_TYPE_IOS=2;

    public String id;
    public String deviceId;
    public String pushKey;
    public Date T0;
    public boolean flag;
    public String body;
    public DeviceInfo()
    {
        id		= "";
        deviceId= "";
        pushKey	= "";
        T0		= new Date();
        flag	= false;
        body	= null;
    }
    public String getBody()
    {
        if(flag==false) return "";
        this.body="";
        try
        {
            this.body="v=0\r\n"+
                    "id="+this.id+"\r\n"+
                    "device-id="+this.deviceId+"\r\n"+
                    "push-key="+this.pushKey+"\r\n"+
                    "device-type="+DEVICE_TYPE_ANDROID+"\r\n";

            //
        }catch(Exception e){}
        return this.body;
    }

}
