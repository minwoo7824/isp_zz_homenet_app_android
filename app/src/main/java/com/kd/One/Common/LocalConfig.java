package com.kd.One.Common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 10110303 on 2016-03-11.
 */
public class LocalConfig {
    private Context mContext;
    private SharedPreferences mPairPrefs;

    public LocalConfig(Context pContext){
        mContext = pContext;
        mPairPrefs = mContext.getSharedPreferences("saveconfig", Activity.MODE_PRIVATE);
    }

    public Boolean setValue(String pKey, int pValue){
        SharedPreferences.Editor pairPrefsEditor = mPairPrefs.edit();
        pairPrefsEditor.putInt(pKey, pValue);
        pairPrefsEditor.commit();
        return true;
    }
    public Boolean setValue(String pKey, String pValue){
        SharedPreferences.Editor pairPrefsEditor = mPairPrefs.edit();
        pairPrefsEditor.putString(pKey, pValue);
        pairPrefsEditor.commit();
        return true;
    }

    public Boolean remove(String pKey){
        SharedPreferences.Editor pairPrefsEditor = mPairPrefs.edit();
        pairPrefsEditor.remove(pKey);
        pairPrefsEditor.commit();
        return true;
    }

    public boolean IsContain(String pKey){
        if(mPairPrefs.contains(pKey))
            return true;
        else
            return false;
    }
    public int getIntValue(String pKey){
        return mPairPrefs.getInt(pKey, -1);
    }
    public String getStringValue(String pKey){
        return mPairPrefs.getString(pKey, "");
    }
}
