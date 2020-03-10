package com.kd.One.Common;

import android.content.Context;
import android.util.Log;

import com.fingerpush.android.FingerPushManager;
import com.fingerpush.android.NetworkUtility;
import com.fingerpush.android.dataset.DeviceInfo;
import com.fingerpush.android.dataset.TagList;
import com.kd.One.Main.MainFragment;

import com.kd.One.Common.LocalConfig;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class FingerPushUtil {

    public LocalConfig mLocalConfig;

    /**
     * 핑거푸시 Device 등록
     * 핸드폰 단말기 등록 -> 스플래시 호출
     */
    public static void setFingerPush(Context context) {
        Log.d("FingerPush", "setDevice start");
        FingerPushManager.getInstance(context).setDevice(new NetworkUtility.ObjectListener() {
            @Override
            public void onError(String code, String message) {
                Log.e("FingerPush", "setIdentity onError : code : " + code + ", message : " + message);
            }

            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                Log.e("FingerPush", "setIdentity onComplete : code : " + code + ", message : " + message);
            }
        });





    }

    /**
     * 핑거푸시 Identity 등록
     * @param identity
     * UserID 등록 -> 로그인 시 호출
     */
    public static void setIdentityFingerPush(Context context, final String identity) {


        Log.d("FingerPush", "setIdentity start (ID:" + identity+")");
        FingerPushManager.getInstance(context).setIdentity(identity,  // 식별자 값
                new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                    @Override
                    public void onError(String code, String message) {
                        Log.e("FingerPush", "setIdentity onError : code : " + code + ", message : " + message);
                    }

                    @Override
                    public void onComplete(String code, String message, JSONObject ObjectData) {
                        Log.e("FingerPush", "setIdentity onComplete : code : " + code + ", message : " + message);
//                        registPushID(identity);
//                        requestPushSetData();
                    }
                }
        );
    }

    /**
     * 핑거푸시 Identity 삭제
     * UserID 삭제 -> 회원탈퇴 시 호출
     */
    public static void removeIdentityFingerPush(Context context) {
        Log.d("FingerPush", "removeIdentity start");
        FingerPushManager.getInstance(context).removeIdentity(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                Log.d("FingerPush", "removeIdentity onComplete : code : " + code + ", message : " + message + ", ObjectData : " + ObjectData);
            }

            @Override
            public void onError(String code, String message) {
                Log.d("FingerPush", "removeIdentity onError : code : " + code + ", message : " + message);
            }
        });
    }

    /**
     * 핑거푸시 DeviceInfo 확인
     */
    public static void getDeviceInfoFingerPush(Context context) {
        Log.d("FingerPush", "getDeviceInfo start");

        FingerPushManager.getInstance(context).getDeviceInfo(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                String appkey = ObjectData.optString(DeviceInfo.APPKEY);
                String device_type = ObjectData.optString(DeviceInfo.DEVICE_TYPE);
                String activity = ObjectData.optString(DeviceInfo.ACTIVITY);
                String ad_activity = ObjectData.optString(DeviceInfo.AD_ACTIVITY);
                String identity = ObjectData.optString(DeviceInfo.IDENTITY);
                String timezone = ObjectData.optString(DeviceInfo.TIMEZONE);
                String country = ObjectData.optString(DeviceInfo.COUNTRY);
                String version_code = ObjectData.optString(DeviceInfo.VERCODE);
                String version_name = ObjectData.optString(DeviceInfo.VERNAME);
                String os_version = ObjectData.optString(DeviceInfo.OSVER);

                String logStr = appkey + "\n";
                logStr += device_type + "\n";
                logStr += activity + "\n";
                logStr += ad_activity + "\n";
                logStr += identity + "\n";
                logStr += timezone + "\n";
                logStr += country + "\n";
                logStr += version_code + "\n";
                logStr += version_name + "\n";
                logStr += os_version;
                Log.e("Finger Push", logStr);
            }

            @Override
            public void onError(String code, String message) {
                Log.d("FingerPush", "getDeviceInfo onError : code : " + code + ", message : " + message);
            }
        });
    }

    /**
     * 핑거푸시 Tag 등록
     * @param tag
     * DeviceId 등록 -> 디바이스 등록 시 호출
     */
    public static void setTagFingerPush(Context context, String tag) {
        Log.d("FingerPush", "setTag start (Tag:" + tag+")");
        FingerPushManager.getInstance(context).setTag(tag,  // 식별자 값
                new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                    @Override
                    public void onError(String code, String message) {
                        Log.d("FingerPush", "setIdentity onError : code : " + code + ", message : " + message);
                    }

                    @Override
                    public void onComplete(String code, String message, JSONObject ObjectData) {
                        Log.d("FingerPush", "setIdentity onComplete : code : " + code + ", message : " + message);
                    }
                }
        );
    }

    /**
     * 핑거푸시 Tag 삭제
     * DeviceId 삭제 -> 디바이스 삭제 시 호출
     */
    public static void removeTagFingerPush(final Context context, String tag) {
        Log.d("FingerPush", "removeTag start (Tag:" + tag+")");
        FingerPushManager.getInstance(context).removeTag(tag, new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                Log.d("FingerPush", "removeTag onComplete : code : " + code + ", message : " + message + ", ObjectData : " + ObjectData);
            }

            @Override
            public void onError(String code, String message) {
                Log.d("FingerPush", "removeTag onError : code : " + code + ", message : " + message);
            }
        });
    }

    /**
     * 핑거푸시 Tag 전체 삭제
     * DeviceId 전체 삭제 -> 회원탈퇴 시 호출
     */
    public static void removeAllTagFingerPush(final Context context) {
        Log.d("FingerPush", "removeAllTag start");
        FingerPushManager.getInstance(context).removeAllTag(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                Log.d("FingerPush", "removeTag onComplete : code : " + code + ", message : " + message + ", ObjectData : " + ObjectData);
            }

            @Override
            public void onError(String code, String message) {
                Log.d("FingerPush", "removeTag onError : code : " + code + ", message : " + message);
            }
        });
    }

    /**
     * 핑거푸시 Tag 리스트 확인
     */
    public static void getTagListFingerPush(Context context) {
        Log.d("FingerPush", "getTagList start");
        FingerPushManager.getInstance(context).getDeviceTag(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject ObjectData) {
                try {
                    JSONArray ArrayData = ObjectData.getJSONArray(TagList.TAGLIST);
                    if(ArrayData.length() > 0) {
                        for (int i = 0; i < ArrayData.length(); i++) {
                            Log.e("TagList", ArrayData.getJSONObject(i).optString("tag") + " : " + ArrayData.getJSONObject(i).optString("date"));
                        }
                    }
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String code, String message) {
                Log.d("FingerPush", "getTagList onError : code : " + code + ", message : " + message);
            }
        });
    }


}
