package com.kd.One.sip;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeUtil {
    public static String hexStringFromCharacter(byte[] buffer, int start, int len) {

        //===== 2. 수신된 데이터 byte -> String 으로 변경 =====
        // convert the byte to hex format method 2 : EncodeUtil.java 참고하면 됨
        StringBuffer hexString = new StringBuffer();
        for (int i = start; i < start + len; i++) {
            String hex = Integer.toHexString(0xff & buffer[i]);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }


    public static String hexStringFromCharacter(byte buffer) {

        //===== 2. 수신된 데이터 byte -> String 으로 변경 =====
        // convert the byte to hex format method 2 : EncodeUtil.java 참고하면 됨
        StringBuffer hexString = new StringBuffer();

        String hex = Integer.toHexString(0xff & buffer);
        if (hex.length() == 1)
            hexString.append('0');
        hexString.append(hex);

        return hexString.toString();
    }

    public static String DecimalStringFromCharacter(byte buffer) {

        //===== 2. 수신된 데이터 byte -> String 으로 변경 =====
        // convert the byte to hex format method 2 : EncodeUtil.java 참고하면 됨
        StringBuffer hexString = new StringBuffer();

        String hex = Integer.toHexString(0xff & buffer);
        if (hex.length() == 1)
            hexString.append('0');
        hexString.append(hex);												//1차 헥사로 변환

        int intValue = Integer.parseInt(hexString.toString(), 16);			//2차 INT로 변환

        String DecString = String.valueOf(intValue);

        return DecString;
    }



    //======================================================================
    //old
    public static String MD5(String text) {
        StringBuffer hexString = new StringBuffer();

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }

            // convert the byte to hex format method 2
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hexString.toString();
    }

    public static byte[] KeyED(String text, String key) {
        byte tmp1 = 0;
        byte tmp2 = 0;

        int ctr = 0;
        int keyLen = 0;
        int txtLen = 0;

        byte[] tmp = MD5(key).getBytes();
        byte[] tmpText = text.getBytes();

        keyLen = tmp.length;
        txtLen = tmpText.length;

        if (txtLen > 4094) {
            txtLen = 4094;
        }

        byte[] result = new byte[txtLen];

        for (int i = 0; i < txtLen; i++) {
            if (ctr == keyLen) {
                ctr = 0;
            }

            tmp1 = tmpText[i];
            tmp2 = tmp[ctr];

            if (tmp1 == tmp2) {
                result[i] = tmp1;
            } else {
                result[i] = (byte) (tmp1 ^ tmp2);
            }

            ctr++;
        }

        return result;
    }

    public static String encData(String text, String key) {
        String sResult = "";
        try {
            byte[] retval = KeyED(text, key);
            sResult = Base64Coder.encodeString(new String(retval));
        } catch (IllegalArgumentException e) {
            Log.e("ERROR", e.getMessage());
        }
        return sResult;
    }

    public static String decData(String encText, String key) {
        String sResult = "";

        try {
            byte[] retval = KeyED(Base64Coder.decodeString(encText), key);
            sResult = new String(retval);
        } catch (IllegalArgumentException e) {
            Log.e("ERROR", e.getMessage());
        }

        return sResult;
    }

    public static int updateCRC(byte[] data, int dataLength) {
        int[] CRC_TABLE = {
                0x0000, 0x8005, 0x800F, 0x000A, 0x801B, 0x001E, 0x0014, 0x8011,
                0x8033, 0x0036, 0x003C, 0x8039, 0x0028, 0x802D, 0x8027,
                0x0022, 0x8063, 0x0066, 0x006C, 0x8069, 0x0078, 0x807D,
                0x8077, 0x0072, 0x0050, 0x8055, 0x805F, 0x005A, 0x804B,
                0x004E, 0x0044, 0x8041, 0x80C3, 0x00C6, 0x00CC, 0x80C9,
                0x00D8, 0x80DD, 0x80D7, 0x00D2, 0x00F0, 0x80F5, 0x80FF,
                0x00FA, 0x80EB, 0x00EE, 0x00E4, 0x80E1, 0x00A0, 0x80A5,
                0x80AF, 0x00AA, 0x80BB, 0x00BE, 0x00B4, 0x80B1, 0x8093,
                0x0096, 0x009C, 0x8099, 0x0088, 0x808D, 0x8087, 0x0082,
                0x8183, 0x0186, 0x018C, 0x8189, 0x0198, 0x819D, 0x8197,
                0x0192, 0x01B0, 0x81B5, 0x81BF, 0x01BA, 0x81AB, 0x01AE,
                0x01A4, 0x81A1, 0x01E0, 0x81E5, 0x81EF, 0x01EA, 0x81FB,
                0x01FE, 0x01F4, 0x81F1, 0x81D3, 0x01D6, 0x01DC, 0x81D9,
                0x01C8, 0x81CD, 0x81C7, 0x01C2, 0x0140, 0x8145, 0x814F,
                0x014A, 0x815B, 0x015E, 0x0154, 0x8151, 0x8173, 0x0176,
                0x017C, 0x8179, 0x0168, 0x816D, 0x8167, 0x0162, 0x8123,
                0x0126, 0x012C, 0x8129, 0x0138, 0x813D, 0x8137, 0x0132,
                0x0110, 0x8115, 0x811F, 0x011A, 0x810B, 0x010E, 0x0104,
                0x8101, 0x8303, 0x0306, 0x030C, 0x8309, 0x0318, 0x831D,
                0x8317, 0x0312, 0x0330, 0x8335, 0x833F, 0x033A, 0x832B,
                0x032E, 0x0324, 0x8321, 0x0360, 0x8365, 0x836F, 0x036A,
                0x837B, 0x037E, 0x0374, 0x8371, 0x8353, 0x0356, 0x035C,
                0x8359, 0x0348, 0x834D, 0x8347, 0x0342, 0x03C0, 0x83C5,
                0x83CF, 0x03CA, 0x83DB, 0x03DE, 0x03D4, 0x83D1, 0x83F3,
                0x03F6, 0x03FC, 0x83F9, 0x03E8, 0x83ED, 0x83E7, 0x03E2,
                0x83A3, 0x03A6, 0x03AC, 0x83A9, 0x03B8, 0x83BD, 0x83B7,
                0x03B2, 0x0390, 0x8395, 0x839F, 0x039A, 0x838B, 0x038E,
                0x0384, 0x8381, 0x0280, 0x8285, 0x828F, 0x028A, 0x829B,
                0x029E, 0x0294, 0x8291, 0x82B3, 0x02B6, 0x02BC, 0x82B9,
                0x02A8, 0x82AD, 0x82A7, 0x02A2, 0x82E3, 0x02E6, 0x02EC,
                0x82E9, 0x02F8, 0x82FD, 0x82F7, 0x02F2, 0x02D0, 0x82D5,
                0x82DF, 0x02DA, 0x82CB, 0x02CE, 0x02C4, 0x82C1, 0x8243,
                0x0246, 0x024C, 0x8249, 0x0258, 0x825D, 0x8257, 0x0252,
                0x0270, 0x8275, 0x827F, 0x027A, 0x826B, 0x026E, 0x0264,
                0x8261, 0x0220, 0x8225, 0x822F, 0x022A, 0x823B, 0x023E,
                0x0234, 0x8231, 0x8213, 0x0216, 0x021C, 0x8219, 0x0208,
                0x820D, 0x8207, 0x0202
        };

        int i, j;
        int crc_accum = 0;
        for (j = 0; j < dataLength; j++) {
            i = (int)((crc_accum >> 8) ^ data[j]) & 0xff;
            crc_accum = (int)((crc_accum << 8) ^ CRC_TABLE[i]);
        }
        return crc_accum;
    }
}
