package com.kd.One.Login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kd.One.Common.Constants;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.R;

/**
 * Created by CJY on 2016-11-07.
 */

public class PermissionCheckActivity extends Activity {

    private Button                  btnNext;
    public static final int         MY_PERMISSION_RECORD_AUDIO = 1;
    public static final int         MY_PERMISSION_READ_EXTERNAL_STORAGE = 4;
    public static final int         MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 5;
    public static final int         MY_PERMISSION_CAMERA = 6;
    public static final int         MY_PERMISSION_READ_PHONE_STATE = 7;

    private LocalConfig             mLocalConfig;
    private Handler                 mTimeHandler;
    private CustomProgressDialog    mProgressDialog;

    //**********************************************************************************************
    /**
     * @param savedInstanceState
     * @brief oncreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_permissioncheck);

        Log.e("Permission Activity", "onCreate");

        //******************************************************************************************
        /**
         * @breif progress dialog create
         */
        mProgressDialog = new CustomProgressDialog(this);
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getBaseContext());
        //******************************************************************************************
        btnNext = (Button)findViewById(R.id.btn_permission_check_next);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

        }else{
            NextActivity();
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
    //**********************************************************************************************

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        if (mTimeHandler != null){
            mTimeHandler.removeCallbacks(delayrunner);
        }
        super.onDestroy();
    }

    //**********************************************************************************************
    /**
     * @breif permission check
     */
    private void check() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE}, 1234);

        }else{
            NextActivity();
        }
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private void checkMultiplePermissions(int permissionCode, Context context) {
//
//        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (!hasPermissions(context, PERMISSIONS)) {
//            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, permissionCode);
//        } else {
//            // Open your camera here.
//        }
//    }

    private boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }else {

                }
            }
        }
        return true;
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif nextactivity
     */
    public void NextActivity() {
        Log.e("next activity", "activity");
        String tToken = "";

        tToken = mLocalConfig.getStringValue(Constants.SAVE_DATA_TOKEN);
        Log.e("permission activity", "token : " + tToken);

        if(tToken != "") {
            Intent intent = new Intent(PermissionCheckActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }
            mTimeHandler.postDelayed(delayrunner, 0);
            mProgressDialog.Show(getString(R.string.Permission_progress_token));
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif delayrunner
     */
    private Runnable delayrunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                String tToken = "";
                tToken = FirebaseInstanceId.getInstance().getToken();
                Log.e("permission activity", "time token : " + tToken);

                if(tToken != ""){
                    mProgressDialog.Dismiss();
                    Intent intent = new Intent(PermissionCheckActivity.this, IntroActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mTimeHandler.postDelayed(delayrunner, 0);
                }
            }
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif
     */
    public void FinishActivity(){
        Toast.makeText(PermissionCheckActivity.this, "권한 사용에 동의하지 않아 APP을 종료 합니다.", Toast.LENGTH_SHORT).show();
        finish();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1234:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    check();
                } else {
                    FinishActivity();
                }
                break;
            case MY_PERMISSION_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    NextActivity();
                } else {
                    FinishActivity();
                }
                break;
            default:
                break;
        }
    }
    //**********************************************************************************************
}
