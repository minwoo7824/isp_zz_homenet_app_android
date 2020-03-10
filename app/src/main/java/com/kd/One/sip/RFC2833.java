package com.kd.One.sip;

public class RFC2833 {
    //4bytes
    // EVENT | VOLUME | RESERVED | EDGE | DURATION |
    // 8       6        1          1      16
/*
	0                   1                   2                   3
    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
   |     event     |E|R| volume    |          duration             |
   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

*/

    protected boolean bStart=false;
    protected boolean bEnd=false;
    static int payloadType=101;
    protected int event=0;
    protected int volume=0;
    protected int reserved=0;
    protected int edge=0;
    protected int duration=0;
    protected int exceedDuration=0;

    //DEVELOPEMENT CONTROL
    protected boolean bActive;
    protected boolean flag;
    protected byte[] rfcdata=new byte[4];

    public RFC2833() {
        //if(SIPStack.SIP_CALLHANDLE_DEBUG==true) System.out.println("sdp handle created.");
        bActive	= false;
        flag	= true;
    }
    public boolean setDtmf(int event,int volume,int reserved,int duration)
    {
        System.out.println("DTMF SET :"+event+":"+volume+":"+reserved+":"+duration);
        this.bStart=true;
        this.bEnd=false;
        this.event=event;
        this.volume=volume;
        this.reserved=reserved;
        this.edge=0;
        this.duration=duration;
        this.bActive=true;
        return true;
    }
    public boolean resetDtmf()
    {
        bStart=false;
        bEnd=false;
        event=0;
        volume=0;
        reserved=0;
        edge=0;
        duration=0;
        exceedDuration=0;

        bActive=false;
        return true;
    }
    public byte[] constructDtmfPacket()
    {
        int field=0;
        //EVENT SET
        field=event;
        rfcdata[0]=(byte)( (field << 24) >> 24 );
        //VOLUME|RESERVED|EDGE SET
        field=0;
        field = ((volume << 26) >> 26) & 0x3F;
        field |= ((edge << 31) >> 24) & 0x80;
        rfcdata[1]=(byte)( (field << 24) >> 24 );
        //DURATION SET
        field=duration;
        rfcdata[2] =(byte)( (field << 16) >> 24 );
        rfcdata[3] =(byte)( (field << 24) >> 24 );
        return rfcdata;
    }
    public byte[] constructDtmfPacket(int consumeduration)
    {
        if(exceedDuration>=duration)
        {
            return null;
        }
        if(exceedDuration>0) bStart=false;

        exceedDuration += consumeduration;
        if(exceedDuration>=duration)
        {
            edge=1;
            bEnd=true;
        }

        this.edge=edge;
        int field=0;
        //EVENT SET
        field=event;
        rfcdata[0]=(byte)( (field << 24) >> 24 );
        //VOLUME|RESERVED|EDGE SET
        field=0;
        field = ((volume << 26) >> 26) & 0x3F;
        field |= ((edge << 31) >> 24) & 0x80;
        rfcdata[1]=(byte)( (field << 24) >> 24 );
        //DURATION SET
        field=duration;
        rfcdata[2] =(byte)( (field << 16) >> 24 );
        rfcdata[3] =(byte)( (field << 24) >> 24 );
        //System.out.println("RFC2833:"+rfcdata[0]+":"+rfcdata[1]+":"+rfcdata[2]+":"+rfcdata[3]);
        return rfcdata;
    }
}
