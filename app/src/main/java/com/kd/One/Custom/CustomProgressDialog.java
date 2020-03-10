package com.kd.One.Custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * Created by lwg on 2016-07-11.
 */
public class CustomProgressDialog {
    private ProgressDialog mDialog;
    private Context mContext;
    private boolean         mDialogFlag;
    private boolean         mDialogCancelFlag;
    private String          mDialogString;
    private Handler mDialogTimerHandler;

    public CustomProgressDialog(Context tContext){
        mContext = tContext;
    }

    public void ThreadProgress(){
        if(mDialog == null){
            mDialog = new ProgressDialog(mContext);
            mDialog.setIndeterminate(true);
            mDialog.setCanceledOnTouchOutside(false);
            if(mDialogCancelFlag == false){
                mDialog.setCancelable(false);
            }else{
                mDialog.setCancelable(true);
            }
            mDialog.setMessage(mDialogString);
            mDialog.show();
        }else{
            mDialog.setMessage(mDialogString);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mDialogFlag){
                    if(mDialog == null){
                        Log.e("dialog fail", "fail");
                    }
                }

                mDialog.dismiss();
                mDialog = null;
                mDialogCancelFlag = false;
            }
        }).start();
    }

    public void Show(String tString){
        if(mDialog == null){
            mDialogFlag         = true;
            mDialogCancelFlag   = false;
            mDialogString       = tString;
            ThreadProgress();
        }else{
            mDialog.setMessage(tString);
        }
    }

    public void ShowCancelEnable(String tString){
        if(mDialog == null){
            mDialogFlag         = true;
            mDialogCancelFlag   = true;
            mDialogString       = tString;
            ThreadProgress();
        }else{
            mDialogCancelFlag = true;
            mDialog.setMessage(tString);
        }
    }

    public void Dismiss(){
        mDialogFlag         = false;
        mDialogCancelFlag   = false;
        mDialogString       = "";
    }

    public boolean getDialogShow(){
        if(mDialog != null){
            return true;
        }
        return false;
    }
}
