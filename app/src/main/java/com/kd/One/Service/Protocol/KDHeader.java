package com.kd.One.Service.Protocol;

import android.annotation.SuppressLint;

import com.kd.One.Common.KDUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by lwg on 2016-07-10.
 */
public class KDHeader {
    public String SOURCE_ID           = "00000000000000120100"; // cell phone
    public String DESTINATION_ID      = "000000000000001F0300";

    private static final byte[] MessageStart        = {(byte)0xA0, (byte)0x00};
    private static final byte[] MessageEnd          = {(byte)0x00, (byte)0x0A};
    private static final byte[] Version             = {(byte)0x01};
    private static final byte[] Flag                = {(byte)0x00};

    private byte[]              Length              = new byte[4];
    private byte[]              MessageID           = {0x00, 0x00, 0x00, 0x00};
    private byte[]              SequenceNumber      = {0x00, 0x00, 0x00, 0x00};

    private byte[]              SourceID            = new byte[20];
    private byte[]              DestinationID       = new byte[20];

    private static final byte[] OPCode              = {0x21, 0x11};

    private byte[]              TransactionID       = new byte[20];

    private static final byte[] Crc                 = {0x00, 0x00, 0x00, 0x00};

    private byte[]              OptionData          = new byte[16];

    public KDHeader(){

    }

    @SuppressLint("SimpleDateFormat")
    public static String format(java.util.Date paramDate, String paramString)
    {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(paramString);
        return localSimpleDateFormat.format(paramDate);
    }

    public static String currentDateXML(){
        long l          = System.currentTimeMillis();
        Date localDate  = new Date(l);

        return format(localDate, "yyyy-MM-dd")+"T"+format(localDate, "HH:mm:ss");
    }

    public byte[]   setHeaderArray(int tLength){
        byte[]  returnValue = null;

        try{
            String tLengthHex = KDUtil.charFill(Integer.toHexString(tLength + 100), "0", 8);
            Length            = KDUtil.hexStringToByteArray(tLengthHex);
            SourceID          = SOURCE_ID.getBytes("UTF-8");
            DestinationID     = DESTINATION_ID.getBytes("UTF-8");
            String tTrans     = currentDateXML()+" ";
            TransactionID     = tTrans.getBytes("UTF-8");

            returnValue = KDUtil.byteAppend(MessageStart, Version);
            returnValue = KDUtil.byteAppend(returnValue, Flag);
            returnValue = KDUtil.byteAppend(returnValue, Length);
            returnValue = KDUtil.byteAppend(returnValue, MessageID);
            returnValue = KDUtil.byteAppend(returnValue, SequenceNumber);
            returnValue = KDUtil.byteAppend(returnValue, SourceID);
            returnValue = KDUtil.byteAppend(returnValue, DestinationID);
            returnValue = KDUtil.byteAppend(returnValue, OPCode);
            returnValue = KDUtil.byteAppend(returnValue, TransactionID);
            returnValue = KDUtil.byteAppend(returnValue, Crc);
            returnValue = KDUtil.byteAppend(returnValue, OptionData);
            returnValue = KDUtil.byteAppend(returnValue, MessageEnd);

        }catch (Exception e){
            e.printStackTrace();
        }

        return returnValue;
    }
}
