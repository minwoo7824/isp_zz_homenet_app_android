package com.kd.One.Common;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * Created by lwg on 2016-07-10.
 */
public class KDUtil {
    public static byte[] StringToByte(String base64str){
        byte[] bytetemp = null;
        try{
            bytetemp = Base64.decode(base64str, Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }

        return bytetemp;
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] byteAppend(byte[] a, byte[] b)
    {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static String bytesToHex(byte[] bytes)
    {
        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String cnvBytesToString(byte[] paramArrayOfByte)
    {
        byte[] arrayOfByte = paramArrayOfByte;
        int i = 0;
        int j = paramArrayOfByte.length;
        StringBuffer localStringBuffer = new StringBuffer();
        while (i < j)
        {
            int k = arrayOfByte[i] & 0xFF;
            int l;
            switch (k >> 4)
            {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    ++i;
                    localStringBuffer.append((char)k);
                    break;
                case 12:
                case 13:
                    if ((i += 2) > j)
                        return null;
                    l = arrayOfByte[(i - 1)];
                    if ((l & 0xC0) != 128)
                        return null;
                    localStringBuffer.append((char)((k & 0x1F) << 6 | l & 0x3F));
                    break;
                case 14:
                    if ((i += 3) > j)
                        return null;
                    l = arrayOfByte[(i - 2)];
                    int i1 = arrayOfByte[(i - 1)];
                    if (((l & 0xC0) != 128) || ((i1 & 0xC0) != 128))
                        return null;
                    localStringBuffer.append((char)((k & 0xF) << 12 | (l & 0x3F) << 6 | (i1 & 0x3F) << 0));
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                default:
                    return null;
            }
        }
        StringBuffer tString = new StringBuffer();
        tString.append(localStringBuffer);

        return new String(localStringBuffer);
    }

    public static String charFill(String orgLenStr, String fillStr, int len)
    {
        String resultSTR = "";
        for (int i = 0; i < len - orgLenStr.length(); i++)
        {
            resultSTR += fillStr;
        }

        return resultSTR + orgLenStr;
    }

    public static String sha256Base64(String pw)
    {
        String sha256 = null;
        try
        {
            byte[] bytes = pw.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            sha256 = Base64.encodeToString(md.digest(bytes), 0).trim();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return sha256;
    }

    //**********************************************************************************************
    /**
     * @brief input filter alphabet
     */
    public static InputFilter filterAlpha = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief input filter alphabet and number
     */
    public static InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief input filter alphabet capital
     */
    public static InputFilter filterAlphaCapital = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[A-Z]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief input filter number
     */
    public static InputFilter filterNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    //**********************************************************************************************

    public static void Expand(final View v) {
        v.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? WindowManager.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void Collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }
            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

}
