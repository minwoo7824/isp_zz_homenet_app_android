package com.kd.One.sip;

import java.util.Date;

public class VideoPacket {
    public final int MAX_PACKET_SIZE=50000;
    public byte[] data= null;
    public Date setTime= null;
    public int size=0;
    public boolean bFlag=false;
    public VideoPacket()
    {
        data=new byte[MAX_PACKET_SIZE];
        setTime=new Date();
        size=0;
        bFlag=false;
    }
    public VideoPacket(byte[] _data,int start,int _size)
    {
        if(_size>0)
        {
            data=new byte[_size];
            setTime=new Date();
            System.arraycopy(_data,start,this.data,0,_size);
            this.size=_size;
            bFlag=true;
            //
        }
        else {
            data=new byte[MAX_PACKET_SIZE];
            setTime=new Date();
            size=0;
            bFlag=false;
            //
        }
    }
}
