package com.kd.One.Common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.kd.One.Main.MainFragment;

/**
 * Created by CJY on 2016-10-19.
 */

public class TimeOutMoving {
    public static void TimeOutMoving(Messenger tRequest, Messenger tResponse, Activity activity){
        Log.e("Activity", "ACTION_APP_OP_TIMEOUT");
        Message tMsg = Message.obtain();
        tMsg.replyTo = tResponse;
        tMsg.what    = Constants.MSG_WHAT_TCP_SOCKET_CLOSE;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TCP_SOCKET_CLOSE);
        tMsg.setData(bundle);
        try{
            if(tRequest != null){
                tRequest.send(tMsg);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }

        tMsg = Message.obtain();
        tMsg.replyTo = tResponse;
        tMsg.what    = Constants.MSG_WHAT_TIMER_END;

        Bundle tbundle = new Bundle();
        tbundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_TIMER_END);
        tMsg.setData(tbundle);
        try{
            if(tRequest != null){
                tRequest.send(tMsg);
            }
        }catch (RemoteException e){
            e.printStackTrace();
        }

        Intent intent1 = new Intent(activity, MainFragment.class);
        intent1.putExtra(Constants.INTENT_TIMEOUT, Constants.INTENT_TIMEOUT);
        activity.startActivity(intent1);
        activity.finish();
    }
}
