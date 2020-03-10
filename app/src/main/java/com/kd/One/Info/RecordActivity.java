package com.kd.One.Info;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kd.One.Common.Constants;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.R;
import com.kd.One.Service.HomeTokService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "RecordActivity";
    public static Activity act;
    private ArrayList<Map<String, String>> mArraylist;

    LinearLayout linBack;
    RecyclerView recyclerView = null;

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

        setContentView(R.layout.activity_record);

        mVersionResponse = new Messenger(responseHandler);

        act = this;

        FindViewById();

        registerReceiver();

    }

    void FindViewById(){
        linBack = (LinearLayout) findViewById(R.id.lin_record_back);
        recyclerView = findViewById(R.id.recycler_view);

        linBack.setOnClickListener(this);

        reloadFileList();

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new VideoListAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                TimeOutMoving.TimeOutMoving(mVersionRequest, mVersionResponse, RecordActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lin_record_back :
                onBackPressed();
                break;
        }
    }

    class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,recyclerView.getHeight()/10);
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final Map<String ,String> map = mArraylist.get(position);

            holder.videoName.setText(map.get("date").substring(0,10));
            holder.videoDate.setText(map.get("date").substring(10,22));
            holder.layoutVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act, RecordEachActivity.class);
                    intent.putExtra("title", map.get("name").toString());
                    intent.putExtra("subTitle", map.get("date").toString());
                    startActivity(intent);
                }
            });
            holder.videoDuration.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return mArraylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layoutVideo;
            TextView videoName;
            TextView videoDate;
            TextView videoDuration;

            public ViewHolder(View itemView) {
                super(itemView);
                layoutVideo = itemView.findViewById(R.id.layout_video);
                videoName = itemView.findViewById(R.id.video_name);
                videoDate = itemView.findViewById(R.id.video_date);
                videoDuration = itemView.findViewById(R.id.video_duration);
            }
        }
    }

    public void reloadFileList(){
        String sdcard = Environment.getExternalStorageState();
        File files = null;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
        {
            // SD카드가 마운트되어있지 않음
            files = Environment.getRootDirectory();
        }
        else
        {
            // SD카드가 마운트되어있음
            files = getExternalStorageDirectory();
        }

        String dstMediaPath = files.getAbsolutePath() + "/kdone/";

        File kdFiles = new File(dstMediaPath);

        if ( !kdFiles.exists() )
        {
            kdFiles.mkdirs();
        }

        if(mArraylist == null)
        {
            mArraylist = new ArrayList<Map<String, String>>();
        }
        mArraylist.clear();

        if(kdFiles.listFiles().length>0)
        {
            for(File file : kdFiles.listFiles())
            {
                if(file.getName().endsWith(".mp4") && file.getName().startsWith("Kd"))
                {
                    Map<String, String> map = new HashMap();
                    map.put("name", file.getName());
                    map.put("date", new SimpleDateFormat("yyyy.MM.dd HH시 mm분 ss초").format(file.lastModified()));
                    mArraylist.add(map);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mArraylist.sort(new Comparator<Map<String, String>>() {
                            @Override
                            public int compare(Map<String, String> o1, Map<String, String> o2) {
                                return o2.get("name").compareTo(o1.get("name"));
                            }
                        });
                    }
                }
            }
        }
    }
}
