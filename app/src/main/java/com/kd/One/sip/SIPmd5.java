package com.kd.One.sip;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SIPmd5 {
    private MessageDigest md = null;
    static private SIPmd5 md5 = null;
    // private static final char[] hexChars ={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    /**
     * Constructor is private so you must use the getInstance method
     */
    private SIPmd5() throws NoSuchAlgorithmException
    {
        md = MessageDigest.getInstance("MD5");
    }


    /**
     * This returns the singleton instance
     */
    public static SIPmd5 getInstance()throws NoSuchAlgorithmException
    {

        if (md5 == null)
        {
            md5 = new SIPmd5();

        }

        return (md5);
    }

    public String hashData(byte[] dataToHash)

    {
        //
        return hexStringFromBytes((calculateHash(dataToHash)));
    }



    private byte[] calculateHash(byte[] dataToHash)

    {
        md.update(dataToHash, 0, dataToHash.length);

        return (md.digest());
    }



    public String hexStringFromBytes(byte[] b)

    {
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<b.length;i++) {
            String md5Char=String.format("%02x",0xff&(char)b[i]);
            sb.append(md5Char);
        }
        return sb.toString();
    }
}
