package com.kd.One.Service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kd.One.Common.Constants;
import com.kd.One.Common.LocalConfig;

public class HomeTokInstanceIdService extends FirebaseInstanceIdService {
    //**********************************************************************************************
    LocalConfig     mLocalConfig;
    //**********************************************************************************************

    private static final String TAG = "HomeTokInstanceIdService";
    @Override
    public void onTokenRefresh(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e("HomeTokInstanceIdService", "Refreshed token : "+refreshedToken);

        SendRegistrationToServer(refreshedToken);
    }

    private void SendRegistrationToServer(String token){

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(this);
        //******************************************************************************************

        mLocalConfig.setValue(Constants.SAVE_DATA_TOKEN, token);

        Log.e("HomeTokInstanceIdService", "new Token : "+token);
    }
}
