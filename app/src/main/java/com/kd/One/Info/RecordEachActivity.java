package com.kd.One.Info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import java.io.File;

public class RecordEachActivity extends AppCompatActivity {

    Activity act;
    VideoView mVideo;
    MediaController mMedia;
    int pause_flag;
    LinearLayout linBack;
    TextView txtTitle;

    private Messenger mVersionResponse = null;
    private Messenger       mVersionRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#fafafa"));
            }
        }

        setContentView(R.layout.activity_record_each);

        act = this;

        mVersionResponse = new Messenger(responseHandler);

        registerReceiver();

        linBack = (LinearLayout)findViewById(R.id.lin_record_each_back);
        txtTitle = (TextView)findViewById(R.id.txt_record_each_title);
        mVideo = findViewById(R.id.video_view);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String subTitle = intent.getStringExtra("subTitle");

        txtTitle.setText(subTitle.substring(0,4) + "년 " + subTitle.substring(5,7) + "월 " + subTitle.substring(8,10) + "일 " + subTitle.substring(11,22));

        mMedia = new MediaController(this);

        mMedia.setAnchorView(mVideo);

        String sdcard = Environment.getExternalStorageState();
        File file = null;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
        {
            file = Environment.getRootDirectory();
        }
        else
        {
            file = Environment.getExternalStorageDirectory();
        }

        String dstMediaPath = file.getAbsolutePath() + String.format("/kdone/" + title);

        Uri uri = Uri.parse(dstMediaPath);

        mVideo.setMediaController(mMedia);
        mVideo.setVideoURI(uri);
        mVideo.requestFocus();

        mVideo.start();

        linBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mVideo.isPlaying())
        {
            mVideo.pause();
        }
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:

                if (mVideo.isPlaying()) {
                    this.mVideo.pause();
                    pause_flag = 1;
                }
                else {
                    pause_flag = 0;
                }

                mVideo.setRotation(90f);


                if (pause_flag == 1) {
                    mVideo.postInvalidate();
                    this.mVideo.start();

                }

                break;

            case Configuration.ORIENTATION_PORTRAIT:

                if (mVideo.isPlaying()) {
                    this.mVideo.pause();
                    pause_flag = 1;
                }
                else {
                    pause_flag = 0;
                }

                mVideo.setRotation(0);

                if (pause_flag == 1) {

                    mVideo.postInvalidate();
                    this.mVideo.start();

                }
                break;

            case Configuration.ORIENTATION_UNDEFINED:
                break;

            default:
                break;
        }
    }

    public void registerReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_APP_FINISH);
        intentFilter.addAction(Constants.ACTION_APP_NETWORK_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_SERVER_CONNECT_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_OP_TIMEOUT);
        registerReceiver(appReceiver, new IntentFilter(intentFilter));
    }

    public final BroadcastReceiver appReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.ACTION_APP_FINISH)) {
                finish();
            } else if (action.equals(Constants.ACTION_APP_NETWORK_ERROR)) {
            } else if (action.equals(Constants.ACTION_APP_SOCKET_CLOSE)) {
            } else if (action.equals(Constants.ACTION_APP_SERVER_CONNECT_ERROR)) {
            } else if( action.equals(Constants.ACTION_APP_OP_TIMEOUT)){
                TimeOutMoving.TimeOutMoving(mVersionRequest, mVersionResponse, RecordEachActivity.this);
            }
        }
    };

    private Handler responseHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    break;
            }
        }
    };

    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mVersionRequest = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mVersionRequest = null;
        }
    };

    /**
     * @breif destroy operating
     */
    public void onDestroy() {
        super.onDestroy();
        if(mVideo.isPlaying())
        {
            mVideo.pause();
        }
        unregisterReceiver(appReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(getBaseContext(), HomeTokService.class);
        bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(requestConnection);
    }
}
