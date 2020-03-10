package com.kd.One.sip;

import android.util.Log;

public class DIDInfo {
    public String[] gasGroupNick;
    public String[] gasNick;
    public String[] heatGroupNick;
    public int heatGroupCount;
    public String[] heatNick;
    public String[] lightGroupNick;
    public String[] lightNick;
    public String[] standbyGroupNick;
    public String[] standbyNick;
    public String[] airconGroupNick;
    public String[] airconNick;
    public String[] ventGroupNick;
    public String[] ventNick;
    public byte ventModel;

    public void setState(byte[] data){
        int nHeaderSize = 11;

        int gasGroupCount = data[nHeaderSize];
        int gasGroupDataLen = 0;

        if(gasGroupCount != 0) {
            gasGroupNick = new String[gasGroupCount];

            for(int k = 0; k < gasGroupCount; k++) {
                int gasGroupLen = data[nHeaderSize + k + gasGroupDataLen + 1];

                byte[] nickname = new byte[gasGroupLen];

                for (int i = 1; i <= gasGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + k + gasGroupDataLen + 1 + i];
                }

                gasGroupDataLen += gasGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    gasGroupNick[k] = str;

                    Log.e("gasGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int gasCount = data[nHeaderSize + gasGroupCount + gasGroupDataLen + 1];
        int gasDataLen = 0;

        if(gasCount != 0){
            gasNick = new String[gasCount];

            for(int k = 0; k < gasCount; k++) {
                int gasLen = data[nHeaderSize + gasGroupCount + gasGroupDataLen + 1 + k + gasDataLen + 1];

                byte[] nickname = new byte[gasLen];

                for (int i = 1; i <= gasLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + gasGroupDataLen + 1 + k + gasDataLen + 1 + i];
                }

                gasDataLen += gasLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    gasNick[k] = str;

                    Log.e("gasNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        heatGroupCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1];
        int heatGroupDataLen = 0;

        if(heatGroupCount != 0){
            heatGroupNick = new String[heatGroupCount];

            for(int k = 0; k < heatGroupCount; k++) {
                int heatGroupLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + k + heatGroupDataLen + 1];

                byte[] nickname = new byte[heatGroupLen];

                for (int i = 1; i <= heatGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + k + heatGroupDataLen + 1 + i];
                }

                heatGroupDataLen += heatGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    heatGroupNick[k] = str;

                    Log.e("heatGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int heatCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1];
        int heatDataLen = 0;

        if(heatCount != 0){
            heatNick = new String[heatCount];

            for(int k = 0; k < heatCount; k++) {
                int heatLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + k + heatDataLen + 1];

                byte[] nickname = new byte[heatLen];

                for (int i = 1; i <= heatLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + k + heatDataLen + 1 + i];
                }

                heatDataLen += heatLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    heatNick[k] = str;

                    Log.e("heatNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int lightGroupCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1];
        int lightGroupDataLen = 0;

        if(lightGroupCount != 0){
            lightGroupNick = new String[lightGroupCount];

            for(int k = 0; k < lightGroupCount; k++) {
                int lightGroupLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupDataLen + 1 + k ];

                byte[] nickname = new byte[lightGroupLen];

                for (int i = 1; i <= lightGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupDataLen + 1 + k + i];
                }

                lightGroupDataLen += lightGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    lightGroupNick[k] = str;

                    Log.e("lightGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int lightCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1];
        int lightDataLen = 0;

        if(lightCount != 0){
            lightNick = new String[lightCount];

            for(int k = 0; k < lightCount; k++) {
                int lightLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightDataLen + 1 + k ];

                byte[] nickname = new byte[lightLen];

                for (int i = 1; i <= lightLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightDataLen + 1 + k + i];
                }

                lightDataLen += lightLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    lightNick[k] = str;

                    Log.e("lightNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int standbyGroupCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1];
        int standbyGroupDataLen = 0;

        if(standbyGroupCount != 0){
            standbyGroupNick = new String[standbyGroupCount];

            for(int k = 0; k < standbyGroupCount; k++) {
                int standbyGroupLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightCount + lightDataLen + 1 + standbyGroupDataLen + 1 + k ];

                byte[] nickname = new byte[standbyGroupLen];

                for (int i = 1; i <= standbyGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupDataLen + 1 + k + i];
                }

                standbyGroupDataLen += standbyGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    standbyGroupNick[k] = str;

                    Log.e("standbyGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int standbyCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1];
        int standbyDataLen = 0;

        if(standbyCount != 0){
            standbyNick = new String[standbyCount];

            for(int k = 0; k < standbyCount; k++) {
                int standbyLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyDataLen + 1 + k ];

                byte[] nickname = new byte[standbyLen];

                for (int i = 1; i <= standbyLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyDataLen + 1 + k + i];
                }

                standbyDataLen += standbyLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    standbyNick[k] = str;

                    Log.e("standbyNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int airconGroupCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1];
        int airconGroupDataLen = 0;

        if(airconGroupCount != 0){
            airconGroupNick = new String[airconGroupCount];

            for(int k = 0; k < airconGroupCount; k++) {
                int airconGroupLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupDataLen + 1 + k ];

                byte[] nickname = new byte[airconGroupLen];

                for (int i = 1; i <= airconGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupDataLen + 1 + k + i];
                }

                airconGroupDataLen += airconGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    airconGroupNick[k] = str;

                    Log.e("airconGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int airconCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1];
        int airconDataLen = 0;

        if(airconCount != 0){
            airconNick = new String[airconCount];

            for(int k = 0; k < airconCount; k++) {
                int airconLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconDataLen + 1 + k ];

                byte[] nickname = new byte[airconLen];

                for (int i = 1; i <= airconLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconDataLen + 1 + k + i];
                }

                airconDataLen += airconLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    airconNick[k] = str;

                    Log.e("airconNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int ventGroupCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1];
        int ventGroupDataLen = 0;

        if(ventGroupCount != 0){
            ventGroupNick = new String[ventGroupCount];

            for(int k = 0; k < ventGroupCount; k++) {
                int ventGroupLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                        + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupDataLen + 1 + k ];

                byte[] nickname = new byte[ventGroupLen];

                for (int i = 1; i <= ventGroupLen; i++) {
                    nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupDataLen + 1 + k + i];
                }

                ventGroupDataLen += ventGroupLen;

                try {
                    String str = new String(nickname, "UTF-8");
                    ventGroupNick[k] = str;

                    Log.e("ventGroupNick", str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(data.length > nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupCount + ventGroupDataLen + 1) {
            int ventCount = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                    + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupCount + ventGroupDataLen + 1];
            int ventDataLen = 0;

            if (ventCount != 0) {
                ventNick = new String[ventCount];

                for (int k = 0; k < ventCount; k++) {
                    int ventLen = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                            + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupCount + ventGroupDataLen + 1
                            + ventDataLen + 1 + k];

                    byte[] nickname = new byte[ventLen];

                    for (int i = 1; i <= ventLen; i++) {
                        nickname[i - 1] = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                                + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1 + ventGroupCount + ventGroupDataLen + 1
                                + ventDataLen + 1 + k + i];
                    }

                    ventDataLen += ventLen;

                    try {
                        String str = new String(nickname, "UTF-8");
                        ventNick[k] = str;

                        Log.e("ventNick", str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            ventModel = data[nHeaderSize + gasGroupCount + 1 + gasGroupDataLen + gasCount + gasDataLen + 1 + heatGroupCount + heatGroupDataLen + 1 + heatCount + heatDataLen + 1 + lightGroupCount + lightGroupDataLen + 1
                    + lightCount + lightDataLen + 1 + standbyGroupCount + standbyGroupDataLen + 1 + standbyCount + standbyDataLen + 1 + airconGroupCount + airconGroupDataLen + 1 + airconCount + airconDataLen + 1
                    + ventGroupCount + ventGroupDataLen + 1 + ventCount + ventDataLen + 1];
        }
    }
}
