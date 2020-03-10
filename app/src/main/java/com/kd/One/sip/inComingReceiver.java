package com.kd.One.sip;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class inComingReceiver extends BroadcastReceiver {
    static SmartHomeviewActivity rootActivity = null;
    private static Context mContext;
    private Intent mIntent;
    // private ITelephony telephonyService;
    protected Toast incomingcallToast = null;

    @Override

    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;
        Bundle bundle = intent.getExtras();

        if (null == bundle) return;
        /* original
        if(rootActivity!=null ) {
        	rootActivity.notifyIncomingGeneralCall();
        	rootActivity=null;
        }
        */
        //기존코드 end

        mContext = context;
        mIntent = intent;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                int events = PhoneStateListener.LISTEN_CALL_STATE;
                tm.listen(phoneStateListener, events);
                //System.out.println(">>>>> telephone event:"+events);

                //
            } else if (rootActivity != null) {
                rootActivity.notifyIncomingGeneralCall();
                //rootActivity=null; 2014 09 29 temporary marked
            }
        } catch (Exception e) {
        }

    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String callState = "UNKNOWN";
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        callState = "IDLE";
                    /* 2014 09 29 temporary marked
                    if(
                        	rootActivity!=null &&
                        	//rootActivity.bActive==true &&
                        	(
                        		rootActivity.callManager==null ||
                        		rootActivity.callManager.sipCall==null ||
                        		rootActivity.callManager.sipCall.flag==false
                        	)
                    )
                    {

                    	rootActivity.notifyIncomingGeneralCall();
                    	rootActivity=null;
                    }
                    */
                        //if(SIPStack.bFreeCall)
                        try {
                            if (rootActivity != null) rootActivity.notifyIdleGeneralCall();
                        } catch (Exception e) {
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        // -- check international call or not.
                        if (incomingNumber.startsWith("00")) {
                            //Toast.makeText(mContext,"International Call- " + incomingNumber,Toast.LENGTH_LONG).show();
                            callState = "International - Ringing (" + incomingNumber + ")";
                        } else {
                            //Toast.makeText(mContext, "Local Call - " + incomingNumber, Toast.LENGTH_LONG).show();
                            callState = "Local - Ringing (" + incomingNumber + ")";
                        }
                    /*
                    if(
                    		rootActivity!=null &&
                    		//rootActivity.bActive==true &&
                    		rootActivity.callManager!=null &&
                    		rootActivity.callManager.sipCall!=null &&
                    		rootActivity.callManager.sipCall.flag==true
                    		)
                    {
                    	 //구현 2 : call blocking
            			TelephonyManager telManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
         			   	Class c = Class.forName(telManager.getClass().getName());
         			   	Method m = c.getDeclaredMethod("getITelephony");
         			   	m.setAccessible(true);
         			   	telephonyService = (ITelephony) m.invoke(telManager);
         			   	//telephonyService.silenceRinger();
         			   	telephonyService.endCall();
         			   	//System.out.println("!!! congurate call end.");
         			   	incomingcallToast=null;
                   		cellcallShow(incomingNumber,SIPStack.SIP_CALLDIRECTION_IN);
                		//
                		//Toast.makeText(mContext,"Phone Call " + incomingNumber +" arrived.",Toast.LENGTH_LONG).show();

                		//

                    }
                    else
                    	*/
                        if (
                                rootActivity != null //&&
                            //rootActivity.bActive==true
                        ) {
                            rootActivity.notifyIncomingGeneralCall();
                            //rootActivity=null; 2014 09 29 temporary marked
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        String dialingNumber = mIntent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                        if (dialingNumber.startsWith("00")) {
                            //Toast.makeText(mContext,"International - " + dialingNumber,Toast.LENGTH_LONG).show();
                            callState = "International - Dialing (" + dialingNumber + ")";
                        } else {
                            //Toast.makeText(mContext, "Local Call - " + dialingNumber,Toast.LENGTH_LONG).show();
                            callState = "Local - Dialing (" + dialingNumber + ")";
                        }

                        if (
                                rootActivity != null //&&
                            //rootActivity.bActive==true
                        ) {
                            rootActivity.notifyIncomingGeneralCall();
                            //rootActivity=null;  2014 09 29 temporary marked
                        }

                        break;
                    default:
                        if (
                                rootActivity != null
                        ) {
                            try {
                                rootActivity.notifyIncomingGeneralCall();
                                //rootActivity=null; 2014 09 29 temporary marked
                            } catch (Exception e) {
                            }
                        }

                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(">>>Broadcast", "onCallStateChanged " + callState);
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    static public String getPhoneNumber() {
        String phoneNum = "";
        try {
            TelephonyManager telManager = (TelephonyManager) rootActivity.getSystemService(rootActivity.TELEPHONY_SERVICE);
            if (telManager != null) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    phoneNum = telManager.getLine1Number();
                    if(phoneNum != null)
                    {
                        if(phoneNum.startsWith("+82")==true) phoneNum="0"+phoneNum.substring(3);
                    }
                    else phoneNum="";
                    //FOR TEST
                    SIPStack.hwImei = telManager.getDeviceId();
                    SIPStack.hwImsi = telManager.getSubscriberId();
                    System.out.println("IMEI:"+SIPStack.hwImei+"  IMSI:"+SIPStack.hwImsi);
                    //2012 10 09
                    if(phoneNum==null || phoneNum.length()==0)
                    {
                        if(SIPStack.hwImei!=null && SIPStack.hwImei.length()>0)
                        {
                            phoneNum=SIPStack.hwImei;
                        }
                    }
                    if(SIPStack.hwImsi==null || SIPStack.hwImsi.length()==0)
                    {
                        if(SIPStack.hwImei!=null && SIPStack.hwImei.length()>0)
                        {
                            SIPStack.hwImsi=SIPStack.hwImei;
                        }
                    }
                }
            }
        }catch(Exception e) {}
        return phoneNum;

    }


}
