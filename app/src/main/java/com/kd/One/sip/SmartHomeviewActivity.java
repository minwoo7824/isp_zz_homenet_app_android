package com.kd.One.sip;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kd.One.Common.KDData;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.TimeOutMoving;
import com.kd.One.Common.jcodec.common.AndroidUtil;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Custom.CustomProgressDialog;
import com.kd.One.Main.MainFragment;
import com.kd.One.R;
import com.kd.One.Common.Constants;
import com.kd.One.Service.HomeTokService;
import com.rd.PageIndicatorView;

import org.jcodec.codecs.h264.H264Decoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


import static com.kd.One.sip.SmartHomeviewActivity.playView;

public class SmartHomeviewActivity extends AppCompatActivity {
    private String TAG = "SmartHomeviewActivity";
    private TextView txtTitle;
    private PageIndicatorView pageIndicatorView = null;
    public static final int PAGER_COUNT = 1;
    public static String SECTION_NUMBER = "section_number";
    private NonSwipeableViewPager viewPager = null;
    public static MoviePlayView playView = null;
    public RelativeLayout layoutVideo;
    public Button subStop = null;
    public ImageView imgBack;
    public Button play = null;
    public Button subPlay = null;
    public ProgressBar pb = null;
    public ImageView mask = null;
    public ImageView subMask = null;
    public Button subRec = null;
    public Button subSend = null;
    private TextView txtKind = null;
    public static int position = 0;
    public static SmartHomeviewActivity act;
    private LocalConfig mLocalConfig;

    public static Context context;
    protected static int         RTP_PORT      = 3002;
    private VideoRtpmanager videoManager   = null;
    private VideoTCPManager videoTCPManager    = null;

    public int currentFrames=0;

    Date bootTime=new Date();
    public int skippedCount=0;
    public static boolean bStarted=false;

    //HOMEVIEW CALL
    private ProgressDialog dialog;
    private static Handler mHandler = new Handler();
    private Handler    mDoorHandler = new Handler();
    private Handler    mEndHandler = new Handler();
    private String strMsgCode;
    private boolean isAutoMute;
    public static byte nowCallMode;

    public HomeState_HA homeState_HA;  //HomeState 의 변수 선언
    public PlaceholderFragment currFragment;

    private NwUtil util;

    //CALL TIMER
    private Handler mCallTimeHandler;

    //
    public static File recorderFile;
    public static boolean isRec = false;
    public static boolean isGooutStatus = false;
    public static boolean isDoorStatus = false;

    private boolean isCreateFile = false;

    ////////////////////////////////////////////////////////
    // SIP PART
    ////////////////////////////////////////////////////////
    public  final String KEY_BSS_SMARTINFO        = "bss_smartinfo";
    public  final String KEY_BSS_ID          = "bss_id";
    public  final String KEY_BSS_AUTHID       = "bss_authid";
    public  final String KEY_BSS_PASSWORD     = "bss_password";
    public  final String KEY_BSS_CID         = "bss_cid";
    public  final String KEY_BSS_SERVERIP     = "bss_serverip";
    public  final String KEY_BSS_SERVERDOMAIN  = "bss_serverdomain";
    public  final String KEY_BSS_SERVERPORT    = "bss_serverport";
    public  final String KEY_BSS_CALLEE       = "bss_callee";
    public  final String KEY_BSS_DIRECTION    = "bss_direction";
    public  final String KEY_BSS_PARTNERNAME   = "bss_partnername";
    public  final String KEY_BSS_PARTNERURI       = "bss_partneruri";
    public  final String KEY_BSS_DIALERKEY    = "bss_dialerkey";

    public  final int PHOTO_POS_OUT    = 0;
    public  final int PHOTO_POS_CON    = 1;
    public  final int PHOTO_POS_IN = 2;

    public SipSignalReceiver signalManager = null;
    public Timer SIGNALCTRLTimer         = null;
    Date netcheckIntervalTimer          = new Date();

    public boolean isRunning            = false;
    public String strLocalip            = null;
    public static boolean bActive              = false;

    public   String commandLine          = null;
    public   String viaH            = null;
    public   String maxforwardH          = null;
    public   String contactH         = null;
    public   String toH                = null;
    public   String fromH           = null;
    public   String callidH             = null;
    public   String cseqH           = null;
    public   String expiresH         = null;
    public   String allowH          = null;
    public   String useragentH       = null;
    public   String contentlengthH    = null;
    public   String authorizationH    = null;
    public   String warningH         = null;
    public   String message             = null;

    public  String serverIp             = null;
    public  int    serverPort          = 5050;
    public  String serverDomain          = null;
    public  String id              = null;
    public  String cid             = null;
    public  String authid           = null;
    public  String authpassword          = null;
    public  String localIp          = null;
    public  String fromTag          = null;
    public  String callId           = null;
    public  int localPort           = SIPStack.SIP_LOCAL_PORT;
    public int CSEQ_NUMBER          = SIPStack.SIP_SEQUENCE_REGISTER;
    public  int regState            = SIPStack.SIP_REGSTATE_IDLE;
    public  Date regTime            = new Date();
    public  String ifIp                = null;
    public  int ifPort             = 5050;
    public  String previfIp             = null;
    public  int previfPort          = 5050;
    public  boolean bInterfaceChanged  = false;

    public  int expiresSeconds       = 60;
    public  int repeatRegisterCount       = 0;
    public  int registerNonceCount    = 0;
    public  RTPManager     audioRTPManager = null;
    public  RTPManager     videoRTPManager = null;
    public  static SIPCall sipCall       = null;
    public  int unreceivedCount          = 0;
    public boolean bInitialRegist     = false;
    public boolean bUnregist         = false;

    //SIPCm
    ConnectivityManager connectivityManager    = null;

    Date registStatusTimer             = new Date();
    int ctrlIndex                    = 0;
    Date ctrlIntervalTimer             = new Date();
    Date callTime                    = new Date();

    protected int serviceMode           = SIPStack.SIP_SERVICEMODE_BASIC;
    boolean bRequestReadySet            = false;


    String strId            = "";
    String strCid           = "";
    String strAuthid         = "";
    String strAuthpassword    = "";
    String strServerHost      = "";
    String strServerDomain    = "";
    String strImsi          = "";
    String strGeneralPhone    = "";
    String strDial          = "";
    int  callDirection       = 0;
    String remoteContactName   = "";
    String remotePhotoUri     = "";
    String dialerKey         = "";

    String host = "";

    String dial                         = "";
    Activity curActivity               = null;

    String writtenStatus               = "";
    int callCount                    = 0;
    String screenSizeStr               = "";
    boolean bGeneralPhoneDetected        = false;
    boolean bOutgoingCoved             = false;
    boolean bOutgoingPictureDisplay          = false;
    boolean bConnectedCoved                = false;
    boolean bConnectedPadCoved          = false;
    boolean bIncomingCoved             = false;
    boolean bVideoCoved                = false;

    boolean bGalaxySIII                   = false;
    boolean bGalaxyStyle               = false;
    boolean bAutoRemoteCall             = false;

    //SIPG729a sipg729                   = null;
    boolean bNetworkActive             = false;
    Vibrator vibrator                 = null;
    boolean bFreeCoverOpened   = false;
    boolean bCancelInvoked    = false;
    boolean bRejectInvoked    = false;
    boolean bAcceptInvoked    = false;
    boolean bWaitingCall      = false;//2014 12 30
    boolean bIncomingNotified  = false;
    Timer  dialerControlTimer = null;

    //2014 12 16
    Timer  vibrateControlTimer    = null;
    boolean bLongViration=false;
    //
    Date workTime           = new Date();
    Date activeTime             = new Date();
    public final int CALL_RESULT_REQUEST = 1;  // The request code
    String callResult="";
    protected ToneGenerator toneGenerator  = null;
    protected Ringtone ringtone             = null;

    //2015 01 09
    protected MediaPlayer oEltongTone     = null;
    protected boolean bEltongRing        = true;

    //2015 07 07 MUTE
    private boolean mMute = false;
    private boolean mRequest = false;
    private boolean mEndRequest = false;
    public static LinearLayout linearRecord = null, linearVoiceSend = null, linearDoorOpen = null;

    private boolean isKdServer = false;
    private boolean isTunnelServer = false;
    //
    //2018 08 20 NetworkOnMainThread 관련
    String strServerip = "";
    public boolean isPushCall = false;
    public static boolean isFirst = true;
    public String topActivity = "";
    public static boolean callActive = true;
    Handler subHandler;
    Handler handler;
    Runnable runnable2;
    public boolean callStatus = false;

    public Handler timeOutHandler;
    public Runnable timeOutRunnable;

    class DialerControllerTask extends TimerTask { //2015 03 30 updated
        public void run() {

            int duration=(int)(new Date().getTime()-activeTime.getTime());
            try
            {
                //
                if(bCancelInvoked==true)
                {
                    if(duration>100)
                    {
                        if(bActive==true && sipCall.flag==true)
                        {
                            bCancelInvoked=false;
                            ongoingScreen(false);

                            dial="";

                            sipCall.bCancelRequest=true;
                            //2015 06 16 update
                            if(bStarted==true)
                            {
                                try
                                {
                                    if(SIPStack.bTcpVideoMode==false && videoManager!=null && videoManager.isAlive()==true && videoManager!=null)
                                    {
                                        videoManager.videodec.bReceiveRunning=false;
                                        videoManager.interrupt();
                                        videoManager.closeSocket();
                                        bStarted=false;
                                        playView.terminateCodec();
                                    }
                                    //2015 07 07
                                    else if(SIPStack.bTcpVideoMode==false && videoTCPManager!=null && videoTCPManager.isAlive()==true && videoTCPManager!=null)
                                    {
                                        videoTCPManager.videodec.bReceiveRunning=false;
                                        videoTCPManager.interrupt();
                                        videoTCPManager.closeSocket();
                                        bStarted=false;
                                        playView.terminateCodec();
                                    }
                                    //
                                }catch(Exception e){e.printStackTrace();}
                                //2015 06 16
                                try {
                                    commitDialer();
                                }catch(Exception e){e.printStackTrace();}
                                //
                            }
                            //

                        }
                    }
                }
                if(bRejectInvoked==true)
                {
                    if(duration>100)
                    {
                        if(bActive==true && sipCall.flag==true
                                && sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED)
                        {
                            bRejectInvoked=false;
                            playAlarm(false);
                            //
                            playRingback(false);
                            sipCall.bRejectRequest=true;

                            //
                        }
                    }
                }
                //2014 12 30
                //CALL 받음
                if(bAcceptInvoked==true)
                {
                    if(duration>100)
                    {
                        if(bActive==true )
                        {
                            bAcceptInvoked=false;
                            bWaitingCall=true;
                            playRingback(false);
                            playAlarm(false);
                            //
                            SIPSound.setTalkingMode(callDirection);

                            //2015 05 29
                            if(
                                    SIPStack.bTcpVideoMode==false &&
                                            sipCall!=null && sipCall.flag==true &&
                                            sipCall.remoteSdp!=null && sipCall.remoteSdp.flag==true &&
                                            sipCall.remoteSdp.videoM!=null && sipCall.remoteSdp.videoM.flag==true &&
                                            sipCall.remoteSdp.videoM.mediaPort>0 &&
                                            (videoManager==null || videoManager.isAlive()==false))
                            {
                                videoManager=new SmartHomeviewActivity.VideoRtpmanager();
                                videoManager.start();

                                VideoDecode.bInvalidate=false;

                                bStarted=true;

                                //Init
                                //                                if(!isPushCall){
                                playView = currFragment.playView;
                                //                                }else{
                                //                                    playView = findViewById(R.id.sub_video_view);
                                //                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(playView.bActive==false) playView.initializeCodec();
                                        else {
                                            playView.invalidateView();
                                        }
                                    }
                                });
                            }
                            else if(SIPStack.bTcpVideoMode==true &&
                                    sipCall!=null && sipCall.flag==true &&
                                    sipCall.remoteSdp!=null && sipCall.remoteSdp.flag==true &&
                                    sipCall.remoteSdp.videoM!=null && sipCall.remoteSdp.videoM.flag==true &&
                                    sipCall.remoteSdp.videoM.mediaPort>0 && (videoTCPManager==null || videoTCPManager.isAlive()==false))
                            {

                                //video TCP 시작
                                videoTCPManager=new SmartHomeviewActivity.VideoTCPManager();
                                videoTCPManager.start();

                                VideoDecode.bInvalidate=false;

                                bStarted=true;

                                //Init
                                //                                if(!isPushCall){
                                playView = currFragment.playView;
                                //                                }else{
                                //                                    playView = findViewById(R.id.sub_video_view);
                                //                                }

                                Log.i("TEST","codec mode2222");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(playView.bActive==false)
                                            playView.initializeCodec();
                                        else {
                                            playView.invalidateView();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
                if(bWaitingCall==true || bAcceptInvoked==true)
                //
                {
                    if(duration>100)
                    {
                        if(bActive==true && sipCall.flag==true
                                && sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED)
                        {
                            bWaitingCall=false;
                            playRingback(false);
                            playAlarm(false);

                            sipCall.bNewcallRequest=true;
                            sipCall.number=new String(dial);
                        }
                    }
                }

                if(sipCall!=null && sipCall.flag==true && sipCall.callState != SIPStack.SIP_CALLSTATE_IDLE)
                {
                    //System.out.println("working");
                    workTime=new Date();
                    return;
                }
            }catch(Exception e){e.printStackTrace();}
        }
    }

    private Runnable callTimeRunner = new Runnable() {
        public void run() {
            //Call End
            callActive = false;
            callEnd();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_smart_homeview);

        mLocalConfig = new LocalConfig(getBaseContext());

        strMsgCode = getIntent().getStringExtra("msg_code");
        isPushCall = getIntent().getBooleanExtra("isPushCall", false);

        callActive = true;

        bootTime = new Date();
        context = this;
        currFragment = new PlaceholderFragment();
        act = this;

        isFirst = true;

        Log.e("homeview start", "start");
        //Init Value
        util = new NwUtil(this);
        util.setLoginState("1", "T", "", "", "", "F");
        nowCallMode = (byte) 0xff;

        txtKind = findViewById(R.id.txt_kind);
        LinearLayout pushCallLayout = findViewById(R.id.sub);
        LinearLayout mainLayout = findViewById(R.id.main);

        imgBack = (ImageView)findViewById(R.id.img_home_view_back);
        txtTitle = (TextView)findViewById(R.id.txt_home_view_title);

        if (getIntent().getExtras() != null){
            if (getIntent().getStringExtra("pushType").equals("Door")){
                txtTitle.setText("현관");
            }else if (getIntent().getStringExtra("pushType").equals("door")){
                txtTitle.setText("현관");
            }else if (getIntent().getStringExtra("pushType").equals("Lobby")){
                txtTitle.setText("공동현관");
            }else if (getIntent().getStringExtra("pushType").equals("lobby")){
                txtTitle.setText("공동현관");
            }else{
                txtTitle.setText("홈뷰");
            }
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(isPushCall) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = getWindow();
                window.getDecorView().setSystemUiVisibility(0);
            }

            subMask = findViewById(R.id.sub_mask);
            subStop = findViewById(R.id.btn_sub_stop);
            subPlay = findViewById(R.id.btn_sub_play);
            subSend = findViewById(R.id.sub_send);
            subRec = findViewById(R.id.sub_rec);
            playView = findViewById(R.id.sub_video_view);
            layoutVideo = findViewById(R.id.layout_video);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300);
            layoutVideo.setLayoutParams(params);

            subMask.setVisibility(View.GONE);
            subPlay.setVisibility(View.GONE);

            subSend.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(sipCall != null){
                        if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED){

                            if(nowCallMode == (byte)0x04)
                            {
                                Toast.makeText(act, "외장 카메라 보기 중에는 음성통화 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            if(act.isAutoMute)
                            {
                                Toast.makeText(act, "자동 통화 전환 기능 사용 중에는 음성송출 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            else
                            {
                                if(nowCallMode != (byte)0x04)
                                {
                                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                                        act.mMute = false;
                                        SIPSound.setMute(act.mMute);

                                        return false;
                                    }

                                    else if(event.getAction() == MotionEvent.ACTION_UP) {
                                        act.mMute = true;
                                        SIPSound.setMute(act.mMute);

                                        return false;
                                    }
                                    else
                                    {
                                        return true;
                                    }
                                }
                                else
                                {
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            return true;
                        }
                    }
                    return true;
                }
            });

            subPlay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(sipCall != null)
                    {
                        if(sipCall.callState!=SIPStack.SIP_CALLSTATE_CONNECTED)
                        {
                            if(act.mRequest == false && act.mEndRequest == false)
                            {
                                subPlay.setVisibility(View.GONE);
                            }
                        }
                        else
                        {
                            Toast.makeText(act, "홈뷰 중입니다.\n홈뷰 종료 후 제어를 시작해 주십시오.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(act, "통화 준비 중입니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            subStop.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (sipCall != null && sipCall.callState == SIPStack.SIP_CALLSTATE_CONNECTED) {
                        onBackPressed();
                    }

                    if (isRec == true) {
                        subRec.setText("녹화하기");
                        isRec = false;
                        recVideo();
                        onBackPressed();
                    }
                }
            });

            playView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if((sipCall != null && sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED) && (subPlay.getVisibility() == View.GONE) && (subStop.getVisibility() == View.GONE)){
                        subStop.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                subStop.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                }
            });

            layoutVideo.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if((sipCall != null && sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED) && (subPlay.getVisibility() == View.GONE) && (subStop.getVisibility() == View.GONE)){
                        subStop.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                subStop.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                }
            });

            subRec.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(sipCall != null)
                    {
                        if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED)
                        {
                            if(isRec == true)
                            {
                                subRec.setText("녹화하기");
                                isRec = false;
                                recVideo();
                            }
                            else
                            {
                                subRec.setText("녹화중지");
                                if(isCreateFile == false)
                                {
                                    if(nowCallMode == (byte)0x10)
                                    {
                                        Toast.makeText(act, "경비 통화는 저장되지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        isRec = true;
                                    }
                                }
                                else
                                {
                                    Toast.makeText(act, "영상 저장 중 입니다. 저장 완료 후, 녹화를 시작해 주십시오.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                        else
                        {
                            Toast.makeText(act, "통화 중일 경우에만 영상 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(act, "통화 중일 경우에만 영상 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            setStatusTextColorBlack();
            pushCallLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }

        pageIndicatorView = findViewById(R.id.intro_pager_indicator);
        viewPager = findViewById(R.id.view_pager);

        final SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SmartHomeviewActivity.position = position;
                Log.e("position", SmartHomeviewActivity.position + "//");
                currFragment = (PlaceholderFragment) ((SectionsPagerAdapter) viewPager.getAdapter()).getItem(position);
                if(position == 2){
                    txtKind.setText(R.string.txt_out_camera);
                }else if(position == 1){
                    txtKind.setText(R.string.txt_wallpad);
                }else{
                    txtKind.setText(R.string.txt_interphone);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        currFragment = (PlaceholderFragment) ((SectionsPagerAdapter) viewPager.getAdapter()).getItem(0);

        pageIndicatorView.setViewPager(viewPager);
        pageIndicatorView.setCount(PAGER_COUNT);

        //Boradcast
        registerBroadcastReceiver();

        Intent intent_response = new Intent(this, HomeTokService.class);
        startService(intent_response);
        mDoorOpenResponse = new Messenger(responseHandler);

        //상태 refresh!
        //===================================================
        //ControlSubActivity cs = new ControlSubActivity();
        //refreshState(cs.getHomeState());
        //===================================================

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    //다이알로그가 Null 이 아니고 실행되고 있으면 종료한다.
                    Log.d("test", "[ =========== ACTION_APP_DIALOGCLOSE ============= ]");

                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }


                    SIPSound.setMute(true);       //2019/02/25

                    //영상 저장 중이라면 영상 저장 완료
                    if (isRec == true) {
                        isRec = false;
                        recVideo();
                    }

                    //----------------------------------------------------------------------------------

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mRequest == true) {
	                        /*pb = currFragment.pb;
	                        if (pb != null) {
	                           pb.setVisibility(View.GONE);
	                        }*/
                            }

                            if (mEndRequest == true) {
                                mEndRequest = false;

                                pb = currFragment.pb;
                                if (pb != null) {
                                    pb.setVisibility(View.GONE);
                                }

	                        /*play = currFragment.play;
	                        if (play != null) {
	                           play.setVisibility(View.VISIBLE);
	                        }*/
                                viewPager.setIsEnabled(false);
                            }
                        }
                    });
	               /*
	               if(DeviceMain.getTopActivity().contains("SmartHomeviewActivity") && DeviceMain.isSend && !DeviceMain.isGet) {

	                  Log.e("send", DeviceMain.isSend + "/" + !DeviceMain.isGet);

	                  callActive = false;
	                  callEnd();

	                  AlertDialog.Builder detailDialog = new AlertDialog.Builder(act);
	                  detailDialog.setMessage("네트워크 상태가 불안정합니다.\n잠시 후 다시 이용해 주세요.");
	                  detailDialog.setTitle("알림");
	                  detailDialog
	                        .setNegativeButton(
	                              "확인",
	                              new DialogInterface.OnClickListener() {
	                                 public void onClick(DialogInterface dialog, int which) {
	                                 }
	                              }).create().show();
	               }*/
                } else if (msg.what == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mRequest == true) {
                                pb = currFragment.pb;
                                if (pb != null) {
                                    pb.setVisibility(View.GONE);
                                }
                            }

                            if (mEndRequest == true) {
                                mEndRequest = false;

	                        /*pb = currFragment.pb;
	                        if (pb != null) {
	                           pb.setVisibility(View.GONE);
	                        }

	                        play = currFragment.play;
	                        if (play != null) {
	                           play.setVisibility(View.VISIBLE);
	                        }*/

	                        /*Toast.makeText(context, "네트워크 상태가 불안정합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
	                        Intent intent = new Intent(context, LoginMain.class);
	                        startActivity(intent);
	                        ((SmartHomeviewActivity) SmartHomeviewActivity.context).finish();*/
                            }
                        }
                    });
                }else if(msg.what == 2){

                }
            }
        };

        subHandler = new Handler();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };
        runnable2 = new Runnable() {
            @Override
            public void run() {
                //                mHandler.postDelayed(runnable2, 1000);
            }
        };

        subHandler.post(runnable2);

        //        if(isPushCall && getIntent().getBooleanExtra("isConfirmPopup", false)) {
        //
        //            if(sipCall == null) {
        //                initSip();
        //            }
        //
        //            if (strMsgCode.equals(KdConstant.PUSH_CODE_HOME_OCC)) {
        //                Toast.makeText(context, "타세대 통화는 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
        //
        //                //strMsgCode = null;
        //            } else if (strMsgCode.equals(KdConstant.PUSH_CODE_CRIME1_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME2_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME3_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME4_OCC) ||
        //                    strMsgCode.equals(KdConstant.PUSH_CODE_EMER_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_FIRE_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_GAS_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_LADDER_OCC)) {
        //                Toast.makeText(context, "비상 알람이 발생하였습니다.\n내장 또는 외장 카메라를 선택하여 홈뷰를 시작해주세요.", Toast.LENGTH_SHORT).show();
        //
        //                //strMsgCode = null;
        //            } else if (strMsgCode.equals(KdConstant.PUSH_CODE_DOOR_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_LOBBY_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_GUARD_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_HOME_CAN)) {
        //                Toast.makeText(context, "호출이 취소되었습니다.", Toast.LENGTH_SHORT).show();
        //
        //                //strMsgCode = null;
        //            } else if (strMsgCode.equals(KdConstant.PUSH_CODE_CRIME1_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME2_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME3_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME4_CAN) ||
        //                    strMsgCode.equals(KdConstant.PUSH_CODE_EMER_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_FIRE_CAN) || strMsgCode.equals(KdConstant.PUSH_CODE_GAS_CAN) || strMsgCode.equals((KdConstant.PUSH_CODE_CANCEL))) {
        //                Toast.makeText(context, "비상 발생이 해제되었습니다.", Toast.LENGTH_SHORT).show();
        //
        //                //strMsgCode = null;
        //            } else {
        //                if (strMsgCode.equals(KdConstant.PUSH_CODE_DOOR_OCC)) {
        //                    nowCallMode = (byte) 0x40;
        //                } else if (strMsgCode.equals(KdConstant.PUSH_CODE_GUARD_OCC)) {
        //                    nowCallMode = (byte) 0x10;
        //                } else if (strMsgCode.equals(KdConstant.PUSH_CODE_LOBBY_OCC)) {
        //                    nowCallMode = (byte) 0x20;
        //                }
        //            }
        //            startHomeview((byte) 0x0c, (byte) nowCallMode);
        //        }
    }

    public void initSip(){
        try
        {
            //2014 12 16
            vibrateControlTimer    = null;
            bLongViration     = false;
            //
            connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            inComingReceiver.rootActivity=act;
            SIPStack.init();

            sipCall          = null;
            bInitialRegist = true;
            workTime      = new Date();
            activeTime    = new Date();
            bUnregist     = false;

            SIPSound.amAudioManager = (AudioManager) act.getSystemService(AUDIO_SERVICE);

            audioRTPManager=new RTPManager(SIPStack.SIP_MEDIATYPE_AUDIO);
            try
            {
                audioRTPManager.parent=act;
                SIPSound.amAudioManager = (AudioManager) act.getSystemService(AUDIO_SERVICE);
            }catch(Exception e){}
            audioRTPManager.initSound();
            audioRTPManager.prepareAudio();

            //2015.07.09 PHONE_NUMBER
            //SIPStack.generalPhoneNumber=inComingReceiver.getPhoneNumber();
            SIPStack.generalPhoneNumber = "APP0000"+mLocalConfig.getStringValue(Constants.SAVE_DATA_DONG)+mLocalConfig.getStringValue(Constants.SAVE_DATA_HO);

            if(SIPStack.generalPhoneNumber==null) SIPStack.generalPhoneNumber="";
            Log.i("Phone","Number "+SIPStack.generalPhoneNumber);

            try
            {
                String host=getSHVE140SLocalIpAddress();
                //Log.i("SIPService", "Host:"+host);

                InetAddress thisComputer = null;

                if(host!=null && host.length()>0)
                    thisComputer=InetAddress.getByName(host);
                else
                    thisComputer=InetAddress.getByName("127.0.0.1");

                byte[] address = thisComputer.getAddress();

                if(address.length>=4)
                {
                    int unsignedByte1 = address[address.length-4]<0 ? address[address.length-4]+256 : address[address.length-4];
                    int unsignedByte2 = address[address.length-3]<0 ? address[address.length-3]+256 : address[address.length-3];
                    int unsignedByte3 = address[address.length-2]<0 ? address[address.length-2]+256 : address[address.length-2];
                    int unsignedByte4 = address[address.length-1]<0 ? address[address.length-1]+256 : address[address.length-1];
	               /*
	               int unsignedByte1 = address[0]<0 ? address[0]+256 : address[0];
	               int unsignedByte2 = address[1]<0 ? address[1]+256 : address[1];
	               int unsignedByte3 = address[2]<0 ? address[2]+256 : address[2];
	               int unsignedByte4 = address[3]<0 ? address[3]+256 : address[3];
	                */

                    strLocalip=unsignedByte1+"."+unsignedByte2+"."+unsignedByte3+"."+unsignedByte4;
                    localIp=new String(strLocalip);
                    localPort           = SIPStack.SIP_LOCAL_PORT;

                    SIPStack.localSdpIp=new String(strLocalip);

                }
                Log.e("strLocalip", "strLocalip:"+strLocalip);

                signalManager=new SipSignalReceiver(thisComputer,localPort);
                if(signalManager!=null) {
                    signalManager.start();
                    bActive=true;
                }
                else
                {
                    Log.e("signalManager", "signalManager: fail");
                }

                sipCall=new SIPCall();
                regTime=new Date();

                Log.e("sip", "sip init success");

            }catch (UnknownHostException uhe)
            {
                Log.e("sip", "sip UnknownHostException");
            }
            catch (Exception e){
                Log.e("sip", "sip Exception");
            }

            Log.i("SIPService", "Service Started.");


            callResult="";
            bAutoRemoteCall       = false;
            bCancelInvoked    = false;
            bRejectInvoked    = false;
            bAcceptInvoked        = false;
            bIncomingNotified  = false;
            bWaitingCall      = false;//2014 12 30
            vibrator      = (Vibrator) act.getSystemService(Context.VIBRATOR_SERVICE);
            curActivity       = act;
            bootTime      = new Date();
            SIPStack.bFreeCall = false;
            callCount        = 0;
            bFreeCoverOpened   = false;

            bGeneralPhoneDetected        = false;
            inComingReceiver.rootActivity  = act;

            //2013 04 18
            bNetworkActive             = false;
            bActive=true;

            //SCREEN SIZE
            DisplayMetrics displayMetrics = new DisplayMetrics();

            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            //--- displayMetrics.density : density / 160, 0.75 (ldpi), 1.0 (mdpi), 1.5 (hdpi)
            int dipWidth  = (int)(displayMetrics.widthPixels  / displayMetrics.density);
            int dipHeight = (int)(displayMetrics.heightPixels / displayMetrics.density);


            strId           = SIPStack.generalPhoneNumber;
            strCid          = SIPStack.generalPhoneNumber;
            strAuthid        = SIPStack.generalPhoneNumber;
            strAuthpassword       = SIPStack.generalPhoneNumber;
            strAuthpassword     = mLocalConfig.getStringValue(Constants.SAVE_DATA_NABLE_PASSWORD);

            strServerHost     = SIPStack.SERVER_IP;
            strServerDomain       = SIPStack.SERVER_IP;
            serverPort       = SIPStack.SERVER_PORT;

            strImsi             = SIPStack.generalPhoneNumber;
            strGeneralPhone       = SIPStack.generalPhoneNumber;
            strDial          = "";
            callDirection=SIPStack.SIP_CALLDIRECTION_IN;

            //2014 12 15
            if(SIPSound.amAudioManager!=null) {
                if(callDirection==SIPStack.SIP_CALLDIRECTION_IN) {
                    Log.i(TAG,"sip sound true");
                    SIPSound.amAudioManager.setMode(AudioManager.MODE_RINGTONE);
                    SIPSound.setSpeakerRoute(true);
                }
                else
                {
                    SIPSound.amAudioManager.setMode(AudioManager.MODE_NORMAL);
                    SIPSound.setSpeakerRoute(false);
                }
            }
            //


            toneGenerator= new   ToneGenerator(AudioManager.STREAM_DTMF,ToneGenerator.MAX_VOLUME);
            Log.e("SIP Init", "toneGenerator");

            try
            {
                Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_RINGTONE);
                ringtone = RingtoneManager.getRingtone(getApplicationContext(),ringtoneUri);
            }catch(Exception e){
                ringtone=null;//2015 03 30 add
            }

            //
            remotePhotoUri="";
            remoteContactName="";
            dialerKey="";

            //
            serverIp         = strServerHost;
            serverDomain      = strServerDomain;//2012 05 02
            id             = strId;
            cid                = strId;
            authid          = strAuthid;
            authpassword      = strAuthpassword;
            localIp             = strLocalip;

            if(       SIPStack.bRegistMode==true &&
                    (strId==null || strId.length()==0 || strAuthid==null || strAuthid.length()==0 ||
                            strAuthpassword==null || strAuthpassword.length()==0 || strServerHost==null || strServerHost.length()==0 || serverPort<=0) )
            {
                Log.e("SIP Init", "invalid sip regist information.");
            }
            else
            {
                Log.e("SIP Init", "sip information OK.");
            }

            //
            host=SIPStack.getSHVE140SLocalIpAddress();
            Log.e("SIP host", "host :"+host);
            //
            InetAddress thisComputer = null;

            if(host!=null && host.length()>0) thisComputer=InetAddress.getByName(host);
            else thisComputer=InetAddress.getByName("127.0.0.1");

            byte[] address = thisComputer.getAddress();

            if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.print("My Phone Network IP is ");

            if(address.length==4)
            {
                int unsignedByte1 = address[0]<0 ? address[0]+256 : address[0];
                int unsignedByte2 = address[1]<0 ? address[1]+256 : address[1];
                int unsignedByte3 = address[2]<0 ? address[2]+256 : address[2];
                int unsignedByte4 = address[3]<0 ? address[3]+256 : address[3];
                strLocalip=unsignedByte1+"."+unsignedByte2+"."+unsignedByte3+"."+unsignedByte4;
                System.out.print(strLocalip);
            }
            else if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("no ipv4.");


            if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println(strLocalip+" founded.");

            //SIPStack.initSound();
            //
            //SIP SIGNAL THREAD START
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.i("Home","getIPV4 1111");
                    String strServerip=SIPStack.getIPV4(strServerHost);
                    Log.e("SIP SIGNAL", "strServerip :"+strServerip);

                    if(strServerip==null)
                        strServerip="";
                    if(strServerDomain==null)
                        strServerDomain="";

                    localPort=localPort;

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    //2013 02 14
                    if (connectivityManager!=null && connectivityManager.getActiveNetworkInfo() != null)
                    {
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

                        switch (activeNetwork.getType())
                        {
                            case ConnectivityManager.TYPE_WIMAX: // 4g 망 체크
                                SIPStack.isInternetWiMax = true;
                                SIPStack.isInternetWiFi = false;
                                SIPStack.isInternetMobile = false;
                                SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                break;

                            case ConnectivityManager.TYPE_WIFI: // wifi망 체크
                                SIPStack.isInternetWiMax = false;
                                SIPStack.isInternetWiFi = true;
                                SIPStack.isInternetMobile = false;
                                SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                break;

                            case ConnectivityManager.TYPE_MOBILE: // 3g 망 체크
                                SIPStack.isInternetWiMax = false;//false;
                                SIPStack.isInternetWiFi = false;
                                SIPStack.isInternetMobile = true;
                                //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                break;
                        }
                    }

                    SIPStack.localSdpIp=strLocalip;
                    if( SIPStack.usePrivateWiMax   == true &&
                            (SIPStack.isInternetWiMax==true || SIPStack.isInternetMobile== true) && SIPStack.isPrivateIp(SIPStack.localSdpIp)==false )
                    {
                        SIPStack.localSdpIp="192.168.10.2";
                    }

                    //
                    System.out.println("+++ 2 4g is "+SIPStack.isInternetWiMax + "  wifi is "+SIPStack.isInternetWiFi +"  3g is "+ SIPStack.isInternetMobile);
                    System.out.println("Local sdp address:"+SIPStack.localSdpIp);


                    //2012 07 26
                    ifIp=strLocalip;
                    ifPort=localPort;
                    //Set server information
                    commandLine="REGISTER sip:"+serverDomain+":"+serverPort+" SIP/2.0";
                    //VIA HEADER
                    Date today=new Date();
                    viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort
                            +";branch="+SIPStack.getViaBranch()+";rport";
                    //MAXFORWARDS HEADER
                    maxforwardH="Max-Forwards: 70";
                    //CONTACT HEADER
                    contactH="Contact: <sip:"+id+"@"+localIp+":"+localPort+">";
                    //TO HEADER
                    toH="To: \""+id+"\"<sip:"+id+"@"+serverDomain+":"+serverPort+">";
                    //FROM HEADER
                    fromTag=SIPStack.newTag();
                    fromH="From: \""+id+"\"<sip:"+id+"@"
                            +serverDomain+":"+serverPort+">;tag="+fromTag;
                    //CALLID HEADER

                    callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getSeconds()+".";

                    callidH="Call-ID: "+callId;
                    //CSEQ HEADER
                    if(CSEQ_NUMBER>65556) CSEQ_NUMBER=0;
                    SIPStack.SIP_SEQUENCE_REGISTER=CSEQ_NUMBER;
                    cseqH="CSeq: "+CSEQ_NUMBER+" REGISTER";

                    //EXPIRES HEADER
                    expiresH="Expires: "+expiresSeconds;
                    //ALLOW HEADER
                    allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
                    //USER-AGENT HEADER
                    useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
                    //CONTENT-LENGTH HEADER
                    contentlengthH="Content-Length: 0";
                    //AUTHORIZATION HEADER
                    //
                    message=commandLine+SIPStack.SIP_LINE_END+
                            viaH+SIPStack.SIP_LINE_END+
                            maxforwardH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END +
                            fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            callidH+SIPStack.SIP_LINE_END+
                            cseqH+SIPStack.SIP_LINE_END+
                            expiresH+SIPStack.SIP_LINE_END+
                            allowH+SIPStack.SIP_LINE_END+
                            useragentH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_DOUBLEEND;

                    netcheckIntervalTimer=new Date();


                    try {
                        host=SIPStack.getSHVE140SLocalIpAddress();
                        if(host!=null && host.length()>0) {
                            Log.i("Home","getIPV4 2222");
                            String ip=SIPStack.getIPV4(host);
                            //System.out.println("ip address:"+ip);
                            if(ip!=null && ip.length()>0) SIPStack.networkStatus=SIPStack.SIP_NETIF_AVAILABLE;

                            if(ip!=null && ip.length()>0 && ip.compareToIgnoreCase(localIp)!=0)
                            {
                                System.out.println("++++++++ IP Changed "+localIp+" to "+ip);
                                localIp=new String(ip);
                                //2013 02 14
                                if(    SIPStack.usePrivateWiMax   == true &&
                                        (SIPStack.isInternetWiMax==true || SIPStack.isInternetMobile== true) && SIPStack.isPrivateIp(ip)==false )
                                {
                                    SIPStack.localSdpIp="192.168.10.2";
                                }

                                else SIPStack.localSdpIp=new String(ip);
                                //

                                ifIp=new String(localIp);
                                ifPort=SIPStack.SIP_LOCAL_PORT;

                                try
                                {
                                    if(sipCall!=null)
                                    {
                                        sipCall.ifIp=ifIp;
                                        sipCall.ifPort=ifPort;
                                    }
                                }catch(Exception e){}

                                authorizationH="";
                                if(SIPStack.bRegistMode==true) sendRegister();

                            }
                            //2012 08 10
                            else if(ip==null || ip.length()==0)
                            {
                                localIp="127.0.0.1";
                                //2013 02 14
                                SIPStack.localSdpIp="127.0.0.1";
                                //
                                System.out.println("Network Inferface is invalid.");
                                //parent.updateStatus("Network is off.",false);
                                SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                            }
                            //
                        }
                        //2012 08 10
                        else if(host==null || host.length()==0)
                        {
                            localIp="127.0.0.1";
                            //2013 02 14
                            SIPStack.localSdpIp="127.0.0.1";
                            //
                            System.out.println("Network Inferface is invalid.");
                            SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                        }
                        //2014 09 29
                        SIPStack.bootTime=new Date();
                        //
                    }catch(Exception e){}
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    try {
                        if(SIGNALCTRLTimer!=null)
                            SIGNALCTRLTimer.cancel();

                        SIGNALCTRLTimer = new Timer();
                        SIGNALCTRLTimer.scheduleAtFixedRate(new CTRLTimerTask(), 0,40);

                        expiresSeconds    = 10;

                        if(dialerControlTimer!=null)
                            dialerControlTimer.cancel();

                        dialerControlTimer = new Timer();
                        dialerControlTimer.scheduleAtFixedRate(new DialerControllerTask(), 0,100/*500*/); //2015 01 19 500->100
                        //

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    isRunning = true;
                    ///////////////// SIP PART END //////////////////////
                }
            };

            Handler handler = new Handler();
            handler.post(runnable);

        }
        catch (UnknownHostException uhe)
        {
            uhe.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        PlaceholderFragment fragment1;
        PlaceholderFragment fragment2;
        PlaceholderFragment fragment3;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    if (fragment1 == null) {
                        fragment1 = PlaceholderFragment.newInstance(position);
                    }
                    return fragment1;
                case 1:
                    if (fragment2 == null) {
                        fragment2 = PlaceholderFragment.newInstance(position);
                    }
                    return fragment2;
                case 2:
                    if (fragment3 == null) {
                        fragment3 = PlaceholderFragment.newInstance(position);
                    }
                    return fragment3;
                default:
                    if (fragment1 == null) {
                        fragment1 = PlaceholderFragment.newInstance(position);
                    }
                    return fragment1;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return PAGER_COUNT;
        }
    }

    public static class PlaceholderFragment extends Fragment {

        private String TAG = "PlaceholderFragment";
        SmartHomeviewActivity act;
        ProgressBar pb = null;
        Button play = null;
        Button stop = null;
        ImageView mask = null;
        MoviePlayView playView = null;
        Button rec = null;
        Button send = null;
        RelativeLayout videoList = null;
        LinearLayout linearRecord = null, linearVoiceSend = null, linearDoorOpen = null;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_homeview1, container, false);

            act = ((SmartHomeviewActivity)SmartHomeviewActivity.context);
            rec = rootView.findViewById(R.id.btn_rec);
            send = rootView.findViewById(R.id.btn_send_voice);
            videoList = rootView.findViewById(R.id.video_list);
            pb = rootView.findViewById(R.id.loading_bar);
            play = rootView.findViewById(R.id.btn_play);
            stop = rootView.findViewById(R.id.btn_stop);
            mask = rootView.findViewById(R.id.mask);
            playView = rootView.findViewById(R.id.video_view);
            linearRecord = rootView.findViewById(R.id.linear_fragment_homeview_record);
            linearVoiceSend = rootView.findViewById(R.id.linear_fragment_homeview_voice_send);
            linearDoorOpen = rootView.findViewById(R.id.linear_fragment_homeview_door_open);

            rec.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);

            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            linearDoorOpen.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        if(act.mCustomPopup == null) {
                            act.mCustomPopup = new CustomPopupBasic(act, R.layout.dialog_door_open,
                                    getString(R.string.txt_door_open), act.getIntent().getStringExtra("pushPassword") + " 을 입력해주세요.",
                                    act.mPopupPwListenerCancel,act.mPopupPwListenerOK);
                            act.mCustomPopup.show();
                        }
                    }
                    return true;
                }
            });

            linearVoiceSend.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN){
                        v.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
                        for (int i = 0; i < linearVoiceSend.getChildCount(); i++){
                            View childV = linearVoiceSend.getChildAt(i);
                            if (childV instanceof ImageView){
                                ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                            }else if (childV instanceof TextView){
                                ((TextView) childV).setTextColor(getResources().getColor(R.color.colorb8b8b8));
                            }
                        }
                        if(sipCall != null){
                            if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED){

                                Log.i(TAG,"VOICE SEND");

                                if(nowCallMode == (byte)0x04) {
                                    Toast.makeText(act, "외장 카메라 보기 중에는 음성통화 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                if(act.isAutoMute) {
                                    Toast.makeText(act, "자동 통화 전환 기능 사용 중에는 음성송출 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }else {
                                    Log.i(TAG,"nowCallMode : " + nowCallMode + " 0x04 : " + (byte)0x04);
                                    if(nowCallMode != (byte)0x04) {
                                        act.mMute = false;
                                        SIPSound.setMute(act.mMute);
                                    }
                                }
                            }
                        }
                    }else if (event.getAction() == MotionEvent.ACTION_UP){
                        Log.i(TAG,"ACTION_UP");
                        linearVoiceSend.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
                        for (int i = 0; i < linearVoiceSend.getChildCount(); i++){
                            View childV = linearVoiceSend.getChildAt(i);
                            if (childV instanceof ImageView){
                                ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorPrimary));
                            }else if (childV instanceof TextView){
                                ((TextView) childV).setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }

                        if(sipCall != null){
                            if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED){
                                if(nowCallMode == (byte)0x04)
                                {
                                    Toast.makeText(act, "외장 카메라 보기 중에는 음성통화 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                if(act.isAutoMute)
                                {
                                    Toast.makeText(act, "자동 통화 전환 기능 사용 중에는 음성송출 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                                else
                                {
                                    if(nowCallMode != (byte)0x04) {
                                        act.mMute = true;
                                        SIPSound.setMute(act.mMute);
                                    }
                                }
                            }
                        }
                    }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
                        Log.i(TAG,"ACTION_CANCEL");
                        linearVoiceSend.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
                        for (int i = 0; i < linearVoiceSend.getChildCount(); i++){
                            View childV = linearVoiceSend.getChildAt(i);
                            if (childV instanceof ImageView){
                                ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorPrimary));
                            }else if (childV instanceof TextView){
                                ((TextView) childV).setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                        }

                        if(sipCall != null){
                            if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED){
                                if(nowCallMode == (byte)0x04)
                                {
                                    Toast.makeText(act, "외장 카메라 보기 중에는 음성통화 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                if(act.isAutoMute)
                                {
                                    Toast.makeText(act, "자동 통화 전환 기능 사용 중에는 음성송출 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                                else
                                {
                                    if(nowCallMode != (byte)0x04) {
                                        act.mMute = true;
                                        SIPSound.setMute(act.mMute);
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            });

            play.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //                    if(IntentService.isSend) {
                    callActive = true;
                    isRec = true;
                    if (isFirst) {
                        act.initSip();
                        isFirst = false;
                    }

                    if (sipCall != null) {
                        if (sipCall.callState != SIPStack.SIP_CALLSTATE_CONNECTED) {
                            act.viewPager.setIsEnabled(true);

                            mask.setVisibility(View.GONE);
                            pb.setVisibility(View.VISIBLE);
                            play.setVisibility(View.GONE);

                            if (SmartHomeviewActivity.position == 0) {
                                act.startHomeview((byte) 0x0c, (byte) 0x02);
                            } else if (SmartHomeviewActivity.position == 1) {
                                act.startHomeview((byte) 0x0c, (byte) 0x08);
                            } else {
                                act.startHomeview((byte) 0x0c, (byte) 0x04);
                            }
                        } else {
                            Toast.makeText(act, "홈뷰 중입니다.\n홈뷰 종료 후 제어를 시작해 주십시오.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(act, "통화 준비 중입니다. 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            play.performClick();

            act.timeOutHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (sipCall.callState != SIPStack.SIP_CALLSTATE_CONNECTED){
                        act.onBackPressed();
                    }
                }
            };

            act.timeOutHandler.sendEmptyMessageDelayed(0,6000);

            stop.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (sipCall != null && sipCall.callState == SIPStack.SIP_CALLSTATE_CONNECTED) {
                        callActive = false;
                        act.callEnd();

                        pb.setVisibility(View.VISIBLE);
                        stop.setVisibility(View.GONE);
                    }

                    if (isRec == true) {
                        rec.setText("녹화하기");
                        isRec = false;
                        act.recVideo();

                        stop.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                    }
                }
            });

            playView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(sipCall != null && (sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED) && (play.getVisibility() == View.GONE) && (stop.getVisibility() == View.GONE)){
                        stop.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stop.setVisibility(View.GONE);
                            }
                        }, 2000);
                    }
                }
            });

            linearRecord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(sipCall != null){
                        if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED){
                            if(isRec == true){
                                v.setBackgroundResource(R.drawable.layer_list_shadow_radius_on);
                                for (int i = 0; i < linearRecord.getChildCount(); i++){
                                    View childV = linearRecord.getChildAt(i);
                                    if (childV instanceof ImageView){
                                        ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorPrimary));
                                    }else if (childV instanceof TextView){
                                        ((TextView) childV).setTextColor(getResources().getColor(R.color.colorPrimary));
                                    }
                                }
                                isRec = false;
                                act.recVideo();
                            }else{
                                v.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
                                for (int i = 0; i < linearRecord.getChildCount(); i++){
                                    View childV = linearRecord.getChildAt(i);
                                    if (childV instanceof ImageView){
                                        ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                                    }else if (childV instanceof TextView){
                                        ((TextView) childV).setTextColor(getResources().getColor(R.color.colorb8b8b8));
                                    }
                                }
                                if(act.isCreateFile == false){
                                    if(nowCallMode == (byte)0x10){
                                        Toast.makeText(act, "경비 통화는 저장되지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        isRec = true;
                                    }
                                }else{
                                    Toast.makeText(act, "영상 저장 중 입니다. 저장 완료 후, 녹화를 시작해 주십시오.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(act, "통화 중일 경우에만 영상 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(act, "통화 중일 경우에만 영상 저장이 가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    protected void setStatusTextColorBlack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //원하는 배경색이 흰색일경우 입력, 텍스트색상을 검정색으로 바꿔줍니다.
        }
    }

    Comparator<? super File> filecomparator = new Comparator<File>(){
        public int compare(File file1, File file2) {
            return String.valueOf(file1.getName()).compareTo(file2.getName());
        }
    };

    public void recVideo()
    {
        isCreateFile = true;

        Thread recThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                Date cDate = new Date(now);
                String fDate = new SimpleDateFormat("yyyyMMdd").format(cDate);
                String fTime = new SimpleDateFormat("HHmmss").format(cDate);
                String sdcard = Environment.getExternalStorageState();
                File file = null;

                if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
                {
                    // SD카드가 마운트되어있지 않음
                    file = Environment.getRootDirectory();
                }
                else
                {
                    // SD카드가 마운트되어있음
                    file = Environment.getExternalStorageDirectory();
                }

                //call mode 별 파일 이름 변경
                String strMode = "";

                if(nowCallMode == (byte)0x40)
                {
                    strMode = "_D";
                }
                else if(nowCallMode == (byte)0x20)
                {
                    strMode = "_L";
                }
                else if(nowCallMode == (byte)0x10)
                {
                    strMode = "_G";
                }
                else if(nowCallMode == (byte)0x08)
                {
                    strMode = "_H";
                }
                else if(nowCallMode == (byte)0x04)
                {
                    strMode = "_E";
                }
                else if(nowCallMode == (byte)0x02)
                {
                    strMode = "_D";
                }
                else if(nowCallMode == (byte)0x00)
                {
                    strMode = "_D";
                }
                else if(nowCallMode == (byte)0xff)
                {
                    strMode = "_D";
                }

                String dstMediaPath = file.getAbsolutePath() + String.format("/kdone/"+"Kd" + fDate +"_"+fTime+strMode+".mp4");
                String dstPath = file.getAbsolutePath() +"/kdone/";

                File filefolder = new File(dstPath);

                if ( !filefolder.exists() )
                {
                    // 디렉토리가 존재하지 않으면 디렉토리 생성
                    filefolder.mkdirs();
                }

                File[] files = filefolder.listFiles();

                if (files.length >= 10){
                    Arrays.sort(files, filecomparator);
                    files[0].delete();
                }

                File fileout = new File(dstMediaPath);
                MediaMuxer muxer = null;

                try {
                    muxer = new MediaMuxer(fileout.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                byte[] bytes0 = {0, 0, 0, 1, 103, 66, 64, 30, -90, -128, -96, 61, -112};
                byte[] bytes1 = {0, 0, 0, 1, 104, -50, 56, -128};

                ByteBuffer csd1 = ByteBuffer.wrap(bytes1);
                ByteBuffer csd0 = ByteBuffer.wrap(bytes0);

                MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", 640, 480);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 512000);
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 10);
                mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar);
                mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
                mediaFormat.setByteBuffer("csd-0", csd0);
                mediaFormat.setByteBuffer("csd-1", csd1);

	            /*MediaFormat audioFormat = new MediaFormat();
	            audioFormat.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
	            audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, 8000);
	            audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

	            int samplingFreq[] = {
	                  96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
	                  16000, 12000, 11025, 8000
	            };

	            int sampleIndex = -1;
	            for (int i = 0; i < samplingFreq.length; ++i) {
	               if (samplingFreq[i] == 8000) {
	                  sampleIndex = i;
	               }
	            }

	            ByteBuffer csd = ByteBuffer.allocate(2);
	            csd.put((byte) ((MediaCodecInfo.CodecProfileLevel.AACObjectLC  << 3) | (sampleIndex >> 1)));

	            csd.position(1);
	            csd.put((byte) ((byte) ((sampleIndex << 7) & 0x80) | (1 << 3)));
	            csd.flip();
	            audioFormat.setByteBuffer("csd-0", csd); // add csd-0*/

                int videoTrackIndex = muxer.addTrack(mediaFormat);
                //int audioTrackIndex = muxer.addTrack(audioFormat);

                ByteBuffer dstBuf;
                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                try {
                    muxer.start();
                    for (int i = 0; i < playView.arrayVideo.size(); i++) {
                        dstBuf = playView.arrayVideo.get(i);

                        bufferInfo.offset = 0;
                        bufferInfo.size = dstBuf.array().length;    // need buffer size
                        bufferInfo.presentationTimeUs = 110 * 400 * i;   //need mod

                        Log.i(TAG, "record i = " + i);
                        Log.i(TAG, "record presentationTimeUs = " + bufferInfo.presentationTimeUs);

                        if (i == 0)
                            bufferInfo.flags = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
                        else if (i == playView.arrayVideo.size() - 1)
                            bufferInfo.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        else
                            bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;

                        //bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                        // copy h264 buffer to dstBuf

                        muxer.writeSampleData(videoTrackIndex, dstBuf, bufferInfo);
                    }

                    muxer.stop();
                    muxer.release();
                    muxer = null;
                    playView.arrayVideo = new ArrayList<>();
                    isCreateFile = false;

                    //PlayShortAudioFileViaAudioTrack(dstMediaPath);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        recThread.start();
    }

    final GestureDetector mGestureDectector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    });

    //Background mode entered
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);

            if (!tasks.isEmpty())
            {
                ComponentName topActivity = tasks.get(0).topActivity;
                Log.e("topactivity", topActivity.getPackageName() + "//" + SmartHomeviewActivity.this.getPackageName());
                if (!topActivity.getPackageName().equals(SmartHomeviewActivity.this.getPackageName()))
                {
                    Log.e("app", "background");

                    //통화 중 이라면 통화 종료
                    //callEnd();

                    //서브페이지 종료, 메뉴 페이지로 이동
                    //Intent intent = new Intent(Constants.ACTION_APP_MAINRETURN);
                    //sendBroadcast(intent);
                }
                else
                {
                    PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);

                    if(pm.isScreenOn() == false)
                    {
                        Log.e("app", "screen off");

                        //통화 중 이라면 통화 종료
                        //callEnd();

                        //서브페이지 종료, 메뉴 페이지로 이동
                        //Intent intent = new Intent(Constants.ACTION_APP_MAINRETURN);
                        //sendBroadcast(intent);
                    }
                }
            }
        }
    };

    public void onPause()
    {
        super.onPause();
        //정확한 체크를 위해 핸들러로 체크
        //Handler handler = new Handler();
        //handler.post(mRunnable);

        unbindService(requestConnection);

        Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
        tMsg.replyTo = mDoorOpenResponse;
        sendMessage(tMsg);
        mDoorOpenRequest = null;
        mDataSendFlag = 0;
        TimeHandlerDoorOpen(false, TIMER_NULL);

        if(mCustomPopup != null){
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    }

    @Override
    //Background mode end.
    public void onResume() {
        super.onResume();
        this.setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);

        Intent intent = new Intent(getBaseContext(), HomeTokService.class);
        bindService(intent, requestConnection, Context.BIND_AUTO_CREATE);

        mProgressDialog = new CustomProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        strMsgCode = null;

        if(subHandler != null){
            subHandler.removeCallbacks(runnable2);
            subHandler = null;
        }

        if (timeOutHandler != null){
            timeOutHandler.removeMessages(0);
        }

        if (mOnBackHandler != null){
            mOnBackHandler.removeMessages(0);
        }

        callEnd();
        closeData();

        Intent intent = new Intent(this, MainFragment.class);
        //      intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }



    public void closeData(){
        playAlarm(false);
        playRingback(false);

        SIPStack.bActive = false;
        bActive = false;
        isGooutStatus = false;
        inComingReceiver.rootActivity = null;

        //background 일 경우 CALL 연결 방지
        sendUNRegister(previfIp, previfPort);

        regState = SIPStack.SIP_REGSTATE_UNREGISTERING;

        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        mHandler.removeMessages(0); //대기핸들러 종료
        mDoorHandler.removeMessages(0);

        commitDialer();

        try {
            try {
                if (SIPSound.amAudioManager != null) {
                    SIPSound.amAudioManager.setMode(AudioManager.MODE_NORMAL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

	      /*
	         audioRTPManager.receiveThread.interrupt();
	         audioRTPManager.receiveThread = null;
	         audioRTPManager.runningSocket.close();
	         audioRTPManager.runningSocket = null;
	       */
            audioRTPManager.sipSound.closeAudioDevice();
            audioRTPManager.sipSound = null;
            audioRTPManager = null;

            //
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (SIGNALCTRLTimer != null) {
                SIGNALCTRLTimer.cancel();
            }

            SIGNALCTRLTimer = null;

            if (dialerControlTimer != null) {
                dialerControlTimer.cancel();
                dialerControlTimer = null;
            }

            try {
                if (signalManager != null && signalManager.theSocket != null) {
                    signalManager.theSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            bActive = false;
            isRunning = false;
            sipCall = null;
            signalManager = null;

            if (toneGenerator != null) {
                toneGenerator.release();
            }
            //2014 12 26
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
            }

            //2015 01 09
            if (oEltongTone != null) {
                oEltongTone.stop();
                oEltongTone.release();
                oEltongTone = null;
            }
            //
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (vibrateControlTimer != null) {
                vibrateControlTimer.cancel();
                vibrateControlTimer = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(appReceiver);
        callEnd();
        if(strMsgCode == null || isPushCall) {
            closeData();
            Log.e("onClose", "onClose");
        }
        Log.e("onDestroy", "onDestroy");
    }

    //HomeView Start
    public void startHomeview(final byte Status, byte Mode)
    {
        //다이알로그 시작 : 끄기
	      /*if(dialog == null)
	      {
	         dialog = new ProgressDialog(SmartHomeviewActivity.this);
	      }*/

        isKdServer = false;
        isTunnelServer = false;

        Log.e("homeview flag in", String.format("%d, %s", Mode, strMsgCode));


	      /*if(Status == (byte)0x0c)
	      {
	         dialog.setMessage("요청 중입니다.");

	      }
	      else
	      {
	         dialog.setMessage("종료 중입니다.");
	      }

	      dialog.setIndeterminate(false);
	      dialog.setCancelable(false);
	      dialog.show();*/

        if(Status == 0x0c) {
            mHandler.sendEmptyMessageDelayed(1, 10000);    //대기시간 10초
        }else if(Status == 0x0d) {
            mHandler.sendEmptyMessageDelayed(0, 10000);    //대기시간 10초
        }

        byte callMode = Mode; //Mode는 홈뷰 시만 사용

        if(callMode != (byte)0x02 && callMode != (byte)0x04 && callMode != (byte)0x08)
        {
            //            if(strMsgCode != null)
            //            {
            //                if(strMsgCode.equals(KdConstant.PUSH_CODE_DOOR_OCC))
            //                {
            //                    callMode = (byte)0x40;
            //                }
            //                else if(strMsgCode.equals(KdConstant.PUSH_CODE_LOBBY_OCC))
            //                {
            //                    callMode = (byte)0x20;
            //                }
            //                else if(strMsgCode.equals(KdConstant.PUSH_CODE_GUARD_OCC))
            //                {
            //                    callMode = (byte)0x10;
            //                }
            //                else if(strMsgCode.equals(KdConstant.PUSH_CODE_CRIME1_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME2_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME3_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_CRIME4_OCC) ||
            //                        strMsgCode.equals(KdConstant.PUSH_CODE_EMER_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_FIRE_OCC) || strMsgCode.equals(KdConstant.PUSH_CODE_GAS_OCC))
            //                {
            //                    callMode = (byte)0x80;
            //                }
            //            }
        }

        final byte sendCallMode = callMode;
        Log.e("homeview flag out", String.format("%d", sendCallMode));

        Thread controlThread = new Thread(new Runnable() {
            public void run() {
                try {
                    handler.sendEmptyMessage(0);

                } catch (Exception e) {
                    e.printStackTrace();
                    if(sipCall != null && sipCall.callState == SIPStack.SIP_CALLSTATE_CONNECTED)
                    {
                        Log.e("homeview", "home->callend");
                        callEnd();
                    }
                    act.finish();
                }
            }
        });

        controlThread.start();

        //통화 시작 시 통화 요청 상태 저장
        if(Status == 0x0c)
        {
            nowCallMode = callMode;
            mRequest = true;
            mEndRequest = false;
        }
        else if(Status == 0x0d)
        {
            nowCallMode = (byte) 0xff;
            mRequest = false;
            mEndRequest = true;
        }
        //Clear
    }


    public void callAccept()
    {
        bAcceptInvoked=true;

        if(vibrateControlTimer!=null)
            vibrateControlTimer.cancel();

        vibrateControlTimer=null;
    }

    public void callReject()
    {
        bRejectInvoked=true;
        //2014 12 16
        if(vibrateControlTimer!=null) vibrateControlTimer.cancel();
        vibrateControlTimer=null;
    }

    //2015 06 16
    public void callEnd()
    {
        //통화 종료 서버로 전달
        Log.e("homeview", String.format("callMode : %08X", nowCallMode));

        if(nowCallMode != (byte)0xff)
        {
            startHomeview((byte)0x0d, nowCallMode);
        }

        viewPager.setOnTouchListener(null);

        //Sound init
        mMute = false;
        SIPSound.setMute(false);
        //call end
        try
        {
            if(SIPStack.bTcpVideoMode==false)
            {
                if(videoManager!=null && videoManager.isAlive()==true && videoManager!=null) {
                    videoManager.videodec.bReceiveRunning=false;
                    videoManager.interrupt();
                    videoManager.closeSocket();
                    bStarted=false;
                    if(playView != null)
                    {
                        playView.terminateCodec();
                    }
                }
            }
            else if(SIPStack.bTcpVideoMode==true)
            {
                if(videoTCPManager!=null && videoTCPManager.isAlive()==true && videoTCPManager!=null) {
                    videoTCPManager.videodec.bReceiveRunning=false;
                    videoTCPManager.interrupt();
                    videoTCPManager.closeSocket();
                    bStarted=false;
                    if(playView != null)
                    {
                        playView.terminateCodec();
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}

        try{
            if(sipCall!=null && sipCall.flag==true) {
                //System.out.println("++++++++++++++++++++++ call end");
                sipCall.bCancelRequest=true;
            }

            commitDialer();

            bCancelInvoked=true;

        }catch(Exception e){e.printStackTrace();}

        //Time Out 제거
        if(mCallTimeHandler != null)
        {
            mCallTimeHandler.removeCallbacks(callTimeRunner);
            mCallTimeHandler = null;
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mask = currFragment.mask;
                if(mask != null) {
                    mask.setVisibility(View.VISIBLE);
                }

                if(isPushCall) {
                    if (subMask != null) {
                        subMask.setVisibility(View.VISIBLE);
                    }
                }

                pb = currFragment.pb;
                if (pb != null) {
                    pb.setVisibility(View.GONE);
                }

                play = currFragment.play;
                if (play != null) {
                    play.setVisibility(View.VISIBLE);
                }

                linearRecord = currFragment.linearRecord;
                if (linearRecord != null) {
                    linearRecord.setVisibility(View.VISIBLE);
                }

                linearVoiceSend = currFragment.linearVoiceSend;
                if (linearVoiceSend != null) {
                    linearVoiceSend.setVisibility(View.VISIBLE);
                }

                linearDoorOpen = currFragment.linearDoorOpen;
                if (linearDoorOpen != null) {
                    linearDoorOpen.setVisibility(View.VISIBLE);
                }

                viewPager.setIsEnabled(false);
            }
        }, 4000);
    }

    public void callMute()
    {
        if(mMute == true)
        {
            if(SIPSound.amAudioManager != null)
            {
                mMute = false;
                SIPSound.setMute(false);
                //Toast.makeText(this, "Mute : Off", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if(SIPSound.amAudioManager != null)
            {
                mMute = true;
                SIPSound.setMute(true);
                //Toast.makeText(this, "Mute : On", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //
    public void registerBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_APP_FINISH);
        intentFilter.addAction(Constants.ACTION_APP_MAINRETURN);
        intentFilter.addAction(Constants.ACTION_APP_GATEWAYRETURN);
        intentFilter.addAction(Constants.ACTION_APP_DIALOGCLOSE);
        intentFilter.addAction(Constants.ACTION_APP_SUBDIALOGCLOSE);
        intentFilter.addAction(Constants.ACTION_APP_REFRESHSTATE);
        intentFilter.addAction(Constants.ACTION_APP_SUBPAGEFINISH);
        intentFilter.addAction(Constants.ACTION_APP_TIMEOUT);
        intentFilter.addAction(Constants.ACTION_APP_PUSH_CALL);
        intentFilter.addAction(Constants.ACTION_APP_HOMEVIEW_END);
        registerReceiver(appReceiver, new IntentFilter(intentFilter));
    }

    public final BroadcastReceiver appReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.ACTION_APP_FINISH)) {
            }
            else if(action.equals(Constants.ACTION_APP_MAINRETURN)) {
            }
            else if(action.equals(Constants.ACTION_APP_GATEWAYRETURN)) {
            }
            //서브페이지만 모두 종료시킬때 사용 (제어)
            else if(action.equals(Constants.ACTION_APP_SUBPAGEFINISH)) {
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
            else if(action.equals(Constants.ACTION_APP_TIMEOUT)) {
                if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED || mRequest == true)
                {
                    Log.e("homeview", "home->callend");
                    callActive = false;
                    callEnd();
                }
                TimeOutMoving.TimeOutMoving(mDoorOpenRequest, mDoorOpenResponse, SmartHomeviewActivity.this);
            }
            else if(action.equals(Constants.ACTION_APP_REFRESHSTATE)) {

                //최초 시작시 다이알로그를 돌린후 상태값을 정상 받은후 종료하도록 한다.!

                //ControlSubActivity cs = new ControlSubActivity();
                //refreshState(cs.getHomeState());
            }
            else if(action.equals(Constants.ACTION_APP_DIALOGCLOSE)) {
                //다이알로그가 Null 이 아니고 실행되고 있으면 종료한다.

	            /*if(isKdServer == true && isTunnelServer == true)
	            {
	               isKdServer = false;
	               isTunnelServer = false;

	               mHandler.removeMessages(0); //대기핸들러 종료
	               mDoorHandler.removeMessages(0);

	               if (dialog != null)
	               {
	                  if (dialog.isShowing())
	                  {
	                     dialog.dismiss();

	                     Log.d("test", "[ =========== ACTION_APP_DIALOGCLOSE ============= ]");
	                  }
	               }
	            }*/
            }
            else if(action.equals(Constants.ACTION_APP_SUBDIALOGCLOSE)) {
                //다이알로그가 Null 이 아니고 실행되고 있으면 종료한다.

	            /*if(isKdServer == true && isTunnelServer == true)
	            {
	               isKdServer = false;
	               isTunnelServer = false;

	               mHandler.removeMessages(0); //대기핸들러 종료
	               mDoorHandler.removeMessages(0);

	               if (dialog != null)
	               {
	                  if (dialog.isShowing())
	                  {
	                     dialog.dismiss();

	                     Log.d("test", "[ =========== ACTION_APP_SUBDIALOGCLOSE ============= ]");
	                  }
	               }
	            }*/
            }
            else if(action.equals(Constants.ACTION_APP_HOMEVIEW_END)) {
                //다이알로그가 Null 이 아니고 실행되고 있으면 종료한다.

                isKdServer = false;
                isTunnelServer = false;

                mHandler.removeMessages(0); //대기핸들러 종료
                mDoorHandler.removeMessages(0);

                if (dialog != null)
                {
                    if (dialog.isShowing())
                    {
                        dialog.dismiss();

                        Log.d("test", "[ =========== ACTION_APP_HOMEVIEW_END ============= ]");
                    }
                }
            }
            else if(action.equals(Constants.ACTION_APP_PUSH_CALL))
            {
                strMsgCode = intent.getStringExtra("msg_code");
                topActivity = intent.getStringExtra("topActivity");
                mHandler.sendEmptyMessage(2);
            }
        }
    };

    public void videoView()
    {
        if(SIPStack.bTcpVideoMode==false)
        {
            if(videoManager!=null && videoManager.isAlive()==true) return;
            videoManager=new SmartHomeviewActivity.VideoRtpmanager();
            videoManager.start();

            VideoDecode.bInvalidate=false;


            bStarted=true;

            //Init
            //            if(!isPushCall){
            playView = currFragment.playView;
            //            }else{
            //                playView = findViewById(R.id.sub_video_view);
            //            }

            if(playView.bActive==false) playView.initializeCodec();
            else {
                playView.invalidateView();
            }
        }
        else if(SIPStack.bTcpVideoMode==true)
        {
            if(videoTCPManager!=null && videoTCPManager.isAlive()==true) return;
            videoTCPManager=new SmartHomeviewActivity.VideoTCPManager();
            videoTCPManager.start();

            VideoDecode.bInvalidate=false;


            bStarted=true;

            //Init
            //            if(!isPushCall){
            playView = currFragment.playView;
            //            }else{
            //                playView = findViewById(R.id.sub_video_view);
            //            }

            if(playView.bActive==false)
                playView.initializeCodec();
            else {
                playView.invalidateView();
            }
        }
        //
    }




	   /*public final void displayFramecount(final int seq)
	   {

	      try {
	         Runnable runner =new Runnable() {
	            public void run() {

	               TextView frameText=(TextView)findViewById(R.id.frames);
	               //Log.i("Video","frame "+currentFrames);

	               frameText.setVisibility(View.GONE);
	               //frameText.setText(currentFrames+"/"+VideoDecode.videosPool.size());

	               int audio = audioRTPManager.testval;
	               frameText.setText("current Frames : " + audio);

	               frameText.setVisibility(View.GONE);

	               //
	               if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED)
	               {
	                  if(SIPSound.bMute == true)
	                  {
	                     mImageMic.setImageResource(R.drawable.img_mic_off);
	                  }

	                  else
	                  {
	                     mImageMic.setImageResource(R.drawable.img_mic_on);
	                  }

	                  //text Status
	                  if(nowCallMode == (byte)0x80)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_EMER_CALL);
	                  }
	                  else if(nowCallMode == (byte)0x40)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                     mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  }
	                  else if(nowCallMode == (byte)0x20)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_LOBBY_CALL);
	                     mButtonLobbyOpen.setEnabled(true);
	                  }
	                  else if(nowCallMode == (byte)0x10)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_GUARD_CALL);
	                     mImageView.setVisibility(View.VISIBLE);
	                  }
	                  else if(nowCallMode == (byte)0x08)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_CAM1_CALL);
	                     mButtonCam1.setBackgroundResource(R.drawable.btn_2_s);
	                  }
	                  else if(nowCallMode == (byte)0x04)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_CAM2_CALL);
	                     mButtonCam2.setBackgroundResource(R.drawable.btn_3_s);
	                  }
	                  else if(nowCallMode == (byte)0x02)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                     mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  }
	                  else if(nowCallMode == (byte)0x00)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                     mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  }
	                  else if(nowCallMode == (byte)0xff)
	                  {
	                     mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                     mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  }
	               }
	               else
	               {
	                  mImageMic.setImageResource(R.drawable.img_mic_off);
	               }
	            }
	         };

	         if(runner != null)
	            this.runOnUiThread(runner);
	      } catch(Exception e){
	         e.printStackTrace();
	         return;
	      }
	   }*/

    private Runnable endRunner = new Runnable() {
        public void run() {
            Intent broadcast = new Intent(Constants.ACTION_APP_HOMEVIEW_END);
            sendBroadcast(broadcast);

            mEndHandler.removeCallbacks(endRunner);
            mEndHandler = null;
        }
    };

    /////////////////// VOIP Control Methods //////////////////////////////
    /**
     * Updates the status box at the top of the UI with a messege of your choice.
     * @param status The String to display in the status box.
     */
    public final void updateStatus(final String status,final boolean bDialreset) {

        try {
            if(writtenStatus.compareTo(status)==0) return;
            //writtenStatus=status; original
            //Runnable runner =new Runnable() {
            //   public void run() {
            writtenStatus=status;//2013 02 18
            if(bDialreset==true)
            {
                dial="";
            }
            //    }
            //};
            //if(runner != null) this.runOnUiThread(runner);
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    public void notifyIncomingGeneralCall()
    {
        try{
            playAlarm(false);
            //
            playRingback(false);

            if(sipCall!=null && sipCall.flag==true
                    && sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED==true)
            {
                return;

            }
            if(sipCall==null || sipCall.flag==false)
            {

                commitDialer();
                //
                return;
                //
            }
            else {
                if(bActive==true && sipCall.flag==true
                        && sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED) {
                    sipCall.bRejectRequest=true;
                    return;
                    //
                }
                else if(bActive==true && sipCall.flag==true
                        && (
                        sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING )
                )
                {
                    sipCall.bCancelRequest=true;
                    return;
                    //
                }
                else {
                    commitDialer();
                    return;

                }
            }
        }catch(Exception e){e.printStackTrace();}
    }
    //2014 12 03 updated
    public final void registStatus(final boolean bActive) { //2014 12 01 updated
        try {
            Runnable runner =new Runnable() {
                public void run() {
                    if(bActive==true)        workTime=new Date();

                    if(bActive==true && bFreeCoverOpened==false)
                    {
                        if(callDirection==SIPStack.SIP_CALLDIRECTION_OUT)
                        {
                            if(strDial.length()>0 && callDirection==SIPStack.SIP_CALLDIRECTION_OUT) bAutoRemoteCall=true;

                            ongoingScreen(true);
                            bFreeCoverOpened=true;
                        }
                        else if(callDirection==SIPStack.SIP_CALLDIRECTION_IN)
                        {
                            dial=strDial;
                            incomingScreen(dial,true);
                            bFreeCoverOpened=true;

                        }
                    }
                    bNetworkActive=bActive;
                }
            };
            if(runner != null) this.runOnUiThread(runner);
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }

    public final void notifyCallCancel()
    {
        try
        {
            playAlarm(false);
            //
            playRingback(false);
            Runnable runner =new Runnable() {
                public void run() {
                    //2015 05 23 temporary marked
                    //ImageButton speakerButton=(ImageButton)findViewById(R.id.CoverConSpeak);
                    //speakerButton.setSelected(false);
                    //speakerButton.setPressed(false);
                    SIPSound.setSpeakerRoute(false);
                    //

                    ongoingScreen(false);
                    incomingScreen("",false);
                    commitDialer();
                }
            };
            if(runner != null) this.runOnUiThread(runner);

        }catch(Exception e){e.printStackTrace();}
    }
    //
    public final void notifyCallEnd(final String status, final boolean bDialreset, final int code, final int direction,    final boolean bConnect, final int seconds)
    {
        try {
            Runnable runner =new Runnable() {
                public void run() {

                    try
                    {
                        playAlarm(false);
                        playRingback(false);
                    }catch(Exception e){e.printStackTrace();}

                    try{
                        if(vibrateControlTimer!=null)
                        {
                            vibrateControlTimer.cancel();
                            vibrateControlTimer=null;
                        }

                    }catch(Exception e){e.printStackTrace();}

                    if(direction<=0) return;

                    //System.out.println("++++notifyCallEnd  code:"+code+"  direction:"+direction+"  connect:"+bConnect+"  seconds:"+seconds);
                    if(direction==SIPStack.SIP_CALLDIRECTION_IN || direction==SIPStack.SIP_CALLDIRECTION_OUT)
                    {
                        callResult="code="+code+":direction="+direction+":conversation="+bConnect+":duration="+seconds+":key="+dialerKey;
                    }

                    commitDialer();

                    if(sipCall!=null && sipCall.flag==true)
                        bCancelInvoked=true;

                    if(bDialreset==true)
                    {
                        dial="";
                    }

                    //status
                    //mTextStatus.setText(KdConstant.STR_HOMEVIEW_CALL_END);

                    //Image SHOW
                    SIPSound.setMute(true);

                    //프로그레스 종료!
                    mRequest = false;
                    isTunnelServer = true;


                    if(isTunnelServer==true && isKdServer == true)
                    {
                        mEndRequest = false;

                        if (mEndHandler == null)
                        {
                            mEndHandler = new Handler();
                        }

                        mEndHandler.postDelayed(endRunner, 2000);
                    }

                    //영상 저장 중이라면 영상 저장 완료
                    if(isRec == true)
                    {
                        isRec = false;

                        recVideo();
                    }
                }
            };

            if(runner != null)
                this.runOnUiThread(runner);
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }



    }

    public final void notifyCallConnected() {
        Log.e("homeview", "notifyCallConnected" + nowCallMode);
        //check now status

	      /*SharedPreferences pref = getSharedPreferences("push_popup", MODE_PRIVATE);

	      SharedPreferences.Editor editor = pref.edit();

	      String str = pref.getString("msg", ""); //키값, 저장값
	      //Boolean boo = pref.getBoolean("popup", false);
	      long time = pref.getLong("time", 0);
	      long when = System.currentTimeMillis();

	      byte pushCallmode = 0;

	      //delete
	      editor.clear();
	      editor.commit();

	      if(str.equals("") == false)
	      {
	         if(when-time < 30*1000)
	         {
	            Log.e("homeview push", str);

	            if(str.equals(KdConstant.PUSH_CODE_DOOR_OCC))
	            {
	               pushCallmode = (byte)0x40;
	            }
	            else if(str.equals(KdConstant.PUSH_CODE_GUARD_OCC))
	            {
	               pushCallmode = (byte)0x10;
	            }
	            else if(str.equals(KdConstant.PUSH_CODE_LOBBY_OCC))
	            {
	               pushCallmode = (byte)0x20;
	            }

	            //
	            if(pushCallmode != 0)
	            {
	               nowCallMode = pushCallmode;
	            }
	         }
	      }
	*/

        //
        isRec = false;

        //프로그레스 종료!
        isTunnelServer = true;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(strMsgCode == null) {
                    pb = currFragment.pb;
                    play = currFragment.play;
                    mask = currFragment.mask;
                    linearRecord = currFragment.linearRecord;
                    linearVoiceSend = currFragment.linearVoiceSend;
                    linearDoorOpen = currFragment.linearDoorOpen;

                    if (mask != null) {
                        mask.setVisibility(View.GONE);
                    }

                    if (pb != null) {
                        pb.setVisibility(View.GONE);
                    }

                    if (play != null) {
                        play.setVisibility(View.GONE);
                    }
                }
            }
        });

        try {
            Runnable runner =new Runnable() {
                public void run() {
                    SIPSound.setTalkingMode(callDirection);//2014 12 30

                    //text Status
	               /*if(nowCallMode == (byte)0x80)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_EMER_CALL);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x40)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                  mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x20)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_LOBBY_CALL);
	                  mButtonLobbyOpen.setEnabled(true);
	               }
	               else if(nowCallMode == (byte)0x10)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_GUARD_CALL);
	                  mImageView.setVisibility(View.VISIBLE);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x08)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_CAM1_CALL);
	                  mButtonCam1.setBackgroundResource(R.drawable.btn_2_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x04)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_CAM2_CALL);
	                  mButtonCam2.setBackgroundResource(R.drawable.btn_3_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x02)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                  mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0x00)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                  mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }
	               else if(nowCallMode == (byte)0xff)
	               {
	                  mTextStatus.setText(KdConstant.STR_HOMEVIEW_DOOR_CALL);
	                  mButtonDoor.setBackgroundResource(R.drawable.btn_1_s);
	                  mButtonLobbyOpen.setEnabled(false);
	               }

	               mButtonEnd.setBackgroundResource(R.drawable.callend_button_press);
	               mButtonEnd.setClickable(true);

	               mButtonMic.setBackgroundResource(R.drawable.btn_mic_n);
	               mButtonMic.setClickable(true);

	               mButtonRec.setBackgroundResource(R.drawable.btn_rec_n);*/

                    if(isAutoMute == true)
                    {
                        SIPSound.setAutoMute(true, 1500);
                    }
                    else
                    {
                        SIPSound.setMute(true);
                    }

                    onconnectedScreen(true);
                }
            };
            if(runner != null)
                this.runOnUiThread(runner);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return;
        }

        //Mute Init
        SIPSound.setMute(false);

        //Call Timer Start
        if(mCallTimeHandler != null)
        {
            mCallTimeHandler.removeCallbacks(callTimeRunner);
            mCallTimeHandler = null;
        }
        mCallTimeHandler = new Handler();
        mCallTimeHandler.postDelayed(callTimeRunner, 60000);

    }
    //2013 03 11
    public final void notifyRingbackTone(final boolean bActive) {

        try {
            Runnable runner =new Runnable() {
                public void run() {
                    playRingback(bActive);
                }
            };
            if(runner != null) this.runOnUiThread(runner);
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    public final void updateDuration(final int calltype,final String durS,final boolean bWakeup)
    {
        if(calltype==0) //Primary call
        {
            try {
                Runnable runner =new Runnable() {
                    public void run() {
                        if(bConnectedCoved==true)
                        {
                            //connectDuration
                            //2015 05 23 temporary marked
                            //TextView labelView = (TextView) findViewById(R.id.connectDuration);
                            //if(labelView!= null) labelView.setText(durS);

                        }
                        else if(bOutgoingCoved==false)
                        {
                            //2015 05 23 temporary marked
                            //TextView labelView = (TextView) findViewById(R.id.coverStatus);
                            //if(labelView!= null) labelView.setText(durS);

                        }
                    }
                };
                if(runner != null) this.runOnUiThread(runner);
            } catch(Exception e){
                e.printStackTrace();
                return;
            }

            if(bWakeup==true)
            {
                //System.out.println("+++ Wake screen.");
            }

        }
    }
    //
    public void notifyIncomingCall(final String status,final String dn) {
        if(bIncomingNotified==true) return;
        if(bRejectInvoked==true)   return;
        if(bAcceptInvoked==true)   return;

        bIncomingNotified=true;

        //

        try {
            Runnable runner =new Runnable() {
                public void run() {

                    //Toast.makeText(HomeviewActivity.this, "Call from "+dn, Toast.LENGTH_SHORT).show();
                    dial=dn;
                    incomingScreen(dn,true);//2012 08 16

                    playAlarm(false);
                    //
                    playRingback(false);
                    try{
                        if(vibrateControlTimer!=null)
                        {
                            vibrateControlTimer.cancel();
                            vibrateControlTimer=null;
                        }

                    }catch(Exception e){e.printStackTrace();}


                }
            };
            this.runOnUiThread(runner);
        } catch(Exception e){
            e.printStackTrace();
            return;
        }
    }
    public void notifyIdleGeneralCall() {
        bGeneralPhoneDetected=true;
        dial="";

    }

    //Ongoing Screen
    public void ongoingScreen(boolean bOn)
    {
        try
        {
            if(bOn==true)
            {
                //2014 12 02
                //
                bConnectedCoved          = false;
                bOutgoingCoved       = false;
                bConnectedPadCoved    = false;
                bVideoCoved             = false;
                bIncomingCoved       = false;
                bOutgoingPictureDisplay    = false;

            }
            else {
                bConnectedCoved       = false;
                bOutgoingCoved    = false;

                bConnectedPadCoved = false;
                bVideoCoved          = false;
                bIncomingCoved    = false;
            }
        }catch(Exception e){e.printStackTrace();}

    }
    //connected Screen
    public void onconnectedScreen(boolean bOn)
    {
        //System.out.println("+++++ onconnectedScreen "+bOn);
        try
        {
            if(bOn==true)
            {
                bConnectedCoved       = false;
                bVideoCoved          = false;
                bOutgoingCoved    = false;

                bConnectedPadCoved = false;
                bIncomingCoved    = false;
                //
            }
            else {
                bConnectedCoved       = false;
                bVideoCoved          = false;
                bOutgoingCoved    = false;

                bConnectedPadCoved = false;
                bIncomingCoved    = false;
            }
        }catch(Exception e){e.printStackTrace();}

    }
    //Ongoing Screen
    public void incomingScreen(String dnis,boolean bOn)
    {
        try
        {
            //System.out.println("+++++++ incomingScreen"+bOn);
            if(bOn==true)
            {

                bConnectedCoved       = false;
                bVideoCoved          = false;
                bOutgoingCoved    = false;

                bConnectedPadCoved = false;
                bIncomingCoved    = false;
                //

            }
            else {
                playAlarm(false);
                //
                bConnectedCoved       = false;
                bVideoCoved          = false;
                bOutgoingCoved    = false;

                bConnectedPadCoved = false;
                bIncomingCoved    = false;

            }
        }catch(Exception e){e.printStackTrace();}

    }
    //2015 01 09
    public void playAlarm(final boolean bPlay)
    {
        if(bEltongRing==false)
        {
            try
            {
                if(ringtone!=null)//2015 03 30 add
                {
                    if(bPlay && ringtone.isPlaying()==false) ringtone.play();
                    else {
                        if(ringtone.isPlaying()) ringtone.stop();
                    }
                }



            }catch(NullPointerException e){e.printStackTrace();}
            catch(Exception e){e.printStackTrace();}
        }
        else {
            try {
                Runnable runner =new Runnable() {
                    public void run() {
                        //oRingbackTone
                        if(bPlay==true)
                        {
                            try
                            {
                                if(oEltongTone!=null) {
                                    System.out.println("+++  MeidaPlayer already started.");
                                    return;
                                }

                                oEltongTone = MediaPlayer.create(getBaseContext(),
                                        R.raw.mptongistel);
                                if(oEltongTone!=null) {
                                    System.out.println("+++  MeidaPlayer started.");
                                    oEltongTone.start();

                                    oEltongTone.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        //@Override
                                        public void onCompletion(MediaPlayer mp) {
                                            oEltongTone.seekTo(0);
                                            oEltongTone.start();
                                        }
                                    });
                                }
                                else System.out.println("+++  MeidaPlayer is null");
                            }catch(Exception e){e.printStackTrace();}

                        }
                        else {
                            try
                            {
                                if(oEltongTone!=null) {
                                    oEltongTone.stop();
                                    oEltongTone.release();
                                    oEltongTone=null;
                                }
                            }catch(Exception e){e.printStackTrace();}
                        }

                    }
                };
                if(runner != null) this.runOnUiThread(runner);
            } catch(Exception e){
                e.printStackTrace();
                return;
            }
        }

        return;

    }
    //

    //2013 03 11
    public void playRingback(boolean bPlay)
    {
        try
        {
            if(bPlay) toneGenerator.startTone(ToneGenerator.TONE_CDMA_NETWORK_USA_RINGBACK, 1000*30);
            else {
                toneGenerator.stopTone();
                toneGenerator.release();
                toneGenerator=null;
            }


        }catch(NullPointerException e){}
        catch(Exception e){}

        return;

    }


    //2014 12 02
    protected InputStream getContactImageByNumber(String number)
    {
        InputStream photoDataStream=null;

        try {

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Uri contactUri=null;
            //System.out.println("++++ getContactImageByNumber:"+number+"  uri:"+uri.toString());
            Cursor contactLookup = curActivity.getContentResolver().query(
                    uri,
                    new String[] {
                            BaseColumns._ID,
                            ContactsContract.PhoneLookup.DISPLAY_NAME
                    },
                    null, null, null);
            try {
                if (contactLookup != null && contactLookup.getCount() > 0)
                {
                    contactLookup.moveToNext();
                    String _id=contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                    contactUri= ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,Integer.parseInt(_id));
                    photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(curActivity.getContentResolver(),contactUri);

                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }
        }catch(Exception e){
            photoDataStream=null;
        }
        return photoDataStream;
    }


    protected  InputStream getContactImageByUri(String photoUri)
    {
        InputStream photoDataStream=null;

        try {
            Uri uri = Uri.parse(photoUri);
            photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(curActivity.getContentResolver(),uri);
        }
        catch(Exception e){
            photoDataStream=null;
        }
        return photoDataStream;
    }

    protected String getContactDisplayNameByNumber(String number)
    {
        String name = new String(number);

        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor contactLookup = curActivity.getContentResolver().query(
                    uri,
                    new String[] {
                            BaseColumns._ID,
                            ContactsContract.PhoneLookup.DISPLAY_NAME
                    },
                    null, null, null);
            try {
                if (contactLookup != null && contactLookup.getCount() > 0)
                {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)); //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
                }
            } finally {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }
        }
        catch(IllegalArgumentException ille){
            name = new String(number);

        }
        catch(Exception e){
            name = new String(number);

        }
        return name;
    }


    //
    public void commitDialer()
    {

        //bActive=false;
        try
        {
            try
            {
                if(SIPSound.amAudioManager!=null)
                    SIPSound.amAudioManager.setMode(AudioManager.MODE_NORMAL);
            }catch(Exception e){e.printStackTrace();}

            if(audioRTPManager != null && audioRTPManager.sipSound != null) {
                audioRTPManager.sipSound.closeAudioDevice();
            }

	         /*if(audioRTPManager != null) {
	            audioRTPManager.sipSound = null;
	         }*/
	         /*
	         audioRTPManager.receiveThread.interrupt();
	         audioRTPManager.runningSocket.close();
	         audioRTPManager.runningSocket = null;
	         audioRTPManager=null;
	          */
            try
            {
                if(SIPStack.bTcpVideoMode==false)
                {
                    //2015 06 16 update
                    if(videoManager!=null && videoManager.isAlive()==true && videoManager!=null)
                    {
                        videoManager.videodec.bReceiveRunning=false;
                        videoManager.interrupt();
                        videoManager.closeSocket();
                        try
                        {
                            bStarted=false;
                            playView.terminateCodec();
                        }catch(Exception e){e.printStackTrace();}

                    }
                }
                else if(SIPStack.bTcpVideoMode==true)
                {
                    if(videoTCPManager!=null && videoTCPManager.isAlive()==true && videoTCPManager!=null)
                    {
                        videoTCPManager.videodec.bReceiveRunning=false;
                        videoTCPManager.interrupt();
                        videoTCPManager.closeSocket();
                        try
                        {
                            bStarted=false;
                            playView.terminateCodec();
                        }catch(Exception e){e.printStackTrace();}

                    }
                }
            }catch(Exception e){e.printStackTrace();}

            //
        }catch(Exception e){e.printStackTrace();}


        try
        {
            callResult="";
            bAutoRemoteCall       = false;
            bCancelInvoked    = false;
            bRejectInvoked    = false;
            bAcceptInvoked        = false;
            bIncomingNotified  = false;
            bWaitingCall      = false;//2014 12 30
            SIPStack.bFreeCall = false;
            callCount        = 0;
            bFreeCoverOpened   = false;

            bGeneralPhoneDetected        = false;


            audioRTPManager=new RTPManager(SIPStack.SIP_MEDIATYPE_AUDIO);
            try
            {
                audioRTPManager.parent=this;

                SIPSound.amAudioManager = null;
                SIPSound.amAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

            }catch(Exception e){}

            audioRTPManager.initSound();
            audioRTPManager.prepareAudio();

            SIPSound.setSpeakerRoute(true);


        }catch(Exception e){e.printStackTrace();}
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public boolean decideRegister()
    {
        if(bInitialRegist==true) {
            bInitialRegist=false;
            return true;
        }
        Date currentDate=new Date();
        int seconds=(int)(currentDate.getTime()-regTime.getTime())/1000;

        if(seconds>expiresSeconds-10 || seconds>60) {//10
            return true;
        }
        else {
            return false;
        }

    }
    public void SIPCTRLRegister()
    {
        try
        {
            if(
                    isRunning ==false ||
                            signalManager==null ||
                            signalManager.bReady==false
            )
            {
                return;
            }
            Date currentTime=new Date();
            int duration=(int)(currentTime.getTime()-regTime.getTime())/1000;

            int startduration=(int)(currentTime.getTime()-SIPStack.bootTime.getTime())/1000;
            if(regState==SIPStack.SIP_REGSTATE_IDLE && startduration>0 && startduration<10
                    && signalManager.bReady==true)
            {
                byte[] buffer=message.getBytes();
                if(message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);

                    regState      = SIPStack.SIP_REGSTATE_REGISTERING;
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                serverIp,serverPort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //
                    regTime          = new Date();
                }

            }
            else if(
                    decideRegister()==true &&
                            regState      != SIPStack.SIP_REGSTATE_REGISTERING &&
                            regState      != SIPStack.SIP_REGSTATE_AUTHORIZING &&
                            regState      != SIPStack.SIP_REGSTATE_UNAVAILABLE
            )
            {
                regState      = SIPStack.SIP_REGSTATE_REGISTERING;
                sendRegister();
            }
            else if(
                    duration>=SIPStack.CS_TIMEOUT_RESPONSE &&
                            regState == SIPStack.SIP_REGSTATE_REGISTERING) //30
            {
                regState      = SIPStack.SIP_REGSTATE_REGISTERING;
                sendRegister();
            }

            if(SIPStack.bGCMRegistSend==true)
            {
                try {
                    boolean registered = ServerUtilities.register(context, SIPStack.gcmRegistId,id);
                    // At this point all attempts to register with the app
                    // server failed, so we need to unregister the device
                    // from GCM - the app will try to register again when
                    // it is restarted. Note that GCM will send an
                    // unregistered callback upon completion, but
                    // GCMIntentService.onUnregistered() will ignore it.
                    if (!registered) {
                        //System.out.println(">>>>> GCM>>>>"+"not registered. unregist this context now.");
                        //GCMRegistrar.unregister(context);
                        //FirebaseInstanceIDService.setPushKey("");
                    }
                    else {
                        System.out.println(">>>>> GCM>>>>"+"registered");
                    }
                    expiresSeconds=3;
                }catch(Exception e){
                    System.out.println("doInBackground error:"+e.toString());
                }
                SIPStack.bGCMRegistSend=false;
            }
        }catch(Exception e){}
        return;
    }
    public boolean sendRegister()
    {

        String body=SIPStack.deviceInfo.getBody();
        CSEQ_NUMBER++;
        //REQUEST LINE

        Log.i("HomeTest","severDomain : " + serverDomain + " ,serverPort : " + serverPort);
        commandLine="REGISTER sip:"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        Date today=new Date();
        Log.i("HomeTest","localIp : " + localIp + " ,localPort : " + localPort);
        viaH="Via: SIP/2.0/UDP "+localIp+":"
                +localPort+";branch="+SIPStack.getViaBranch()+";rport";
        //MAXFORWARDS HEADER
        maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
        {
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        }
        else contactH="Contact: <sip:"+id+"@"+localIp+":"
                +localPort+">";

        Log.i("HomeTest","contactH : " + contactH);

        //TO HEADER

        Log.i("HomeTest","id : " + id);

        toH="To: \""+id+"\"<sip:"+id
                +"@"+serverDomain+":"+serverPort+">";
        //FROM HEADER
        fromTag=SIPStack.newTag();
        fromH="From: \""+id+"\"<sip:"
                +id+"@"+serverDomain+":"
                +serverPort+">;tag="+fromTag;
        //CALLID HEADER
        if(callId==null || callId.length()==0)
        {
            callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getTime()+".";
            callidH="Call-ID: "+callId;
        }
        //CSEQ HEADER
        if(CSEQ_NUMBER>65556) CSEQ_NUMBER=0;
        SIPStack.SIP_SEQUENCE_REGISTER=CSEQ_NUMBER;
        cseqH="CSeq: "+CSEQ_NUMBER+" REGISTER";
        //EXPIRES HEADER
        //if(bUnregist==true) expiresH="Expires: "+0;
        //else
        expiresH="Expires: "+expiresSeconds;
        //ALLOW HEADER
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
        //CONTENT-LENGTH HEADER
        if(body!=null && body.length()>0)
            contentlengthH="Content-Length: "+body.length();
        else contentlengthH="Content-Length: 0";
        //WARNING HEADER
        warningH="Warning: WIFI="+SIPStack.isInternetWiFi;
        //AUTHORIZATION HEADER
        //
        if(authorizationH != null && authorizationH.length()>0)
        {
            if(body!=null && body.length()>0)
            {
                //
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        authorizationH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        "Content-Type: plain/text"+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                        +body;
                ;

            }
            else {
                //
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        authorizationH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;

            }

        }
        else
        {
            if(body!=null && body.length()>0)
            {
                //
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        "Content-Type: plain/text"+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                        +body;
                ;

            }
            else {
                //
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;

            }
        }
        if(message.length()>0)
        {
            if(message.length()>0 && signalManager!=null) {
                byte[] buffer=message.getBytes();
                if(message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                serverIp,serverPort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    regTime          = new Date();
                }
            }
            return true;

        }
        return false;
    }

    public boolean sendUNRegister()
    {
        try
        {
            bUnregist=true;

            CSEQ_NUMBER++;
            //REQUEST LINE
            commandLine="REGISTER sip:"+serverDomain+":"+serverPort+" SIP/2.0";
            //VIA HEADER
            Date today=new Date();
            viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+SIPStack.getViaBranch()+";rport";
            //MAXFORWARDS HEADER
            maxforwardH="Max-Forwards: 70";
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            {
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">;expires=0";
            }
            else contactH="Contact: <sip:"+id+"@"+localIp+":"+localPort+">;expires=0";
            //TO HEADER
            toH="To: \""+id+"\"<sip:"+id+"@"+serverDomain+":"+serverPort+">";
            //FROM HEADER
            fromTag=SIPStack.newTag();
            fromH="From: \""+id+"\"<sip:"+id+"@"+serverDomain
                    +":"+serverPort+">;tag="+fromTag;
            //CALLID HEADER
            if(callId==null || callId.length()==0)
            {
                callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getTime()+".";
                callidH="Call-ID: "+callId;
            }
            //CSEQ HEADER
            if(CSEQ_NUMBER>65556) CSEQ_NUMBER=0;
            SIPStack.SIP_SEQUENCE_REGISTER=CSEQ_NUMBER;
            cseqH="CSeq: "+CSEQ_NUMBER+" REGISTER";
            //EXPIRES HEADER
            expiresH="Expires: 0";
            //ALLOW HEADER
            allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
            //USER-AGENT HEADER
            useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
            //WARNING HEADER
            warningH="Warning: WIFI="+SIPStack.isInternetWiFi;
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: 0";
            //AUTHORIZATION HEADER
            //
            if(authorizationH != null && authorizationH.length()>0)
            {
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        authorizationH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;
                if(message.length()>0 && signalManager!=null) {
                    byte[] buffer=message.getBytes();
                    if(message.length()>0) {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                    serverIp,serverPort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                        regTime          = new Date();
                    }
                }
                return true;
            }
            else
            {

                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        warningH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;
                if(message.length()>0 && signalManager!=null) {
                    byte[] buffer=message.getBytes();
                    if(message.length()>0) {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                    serverIp,serverPort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                        regTime          = new Date();
                    }
                }
                return true;
            }
        }catch(Exception e){}
        return false;
    }

    public boolean sendUNRegister(String contactip,int contactport)
    {
        try
        {
            bUnregist=true;

            CSEQ_NUMBER++;
            //REQUEST LINE
            commandLine="REGISTER sip:"+serverDomain+":"+serverPort+" SIP/2.0";
            //VIA HEADER
            Date today=new Date();
            viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort
                    +";branch="+SIPStack.getViaBranch()+";rport";
            //MAXFORWARDS HEADER
            maxforwardH="Max-Forwards: 70";
            //CONTACT HEADER
            if(contactip!=null && contactip.length()>0 && contactport>0)
            {
                contactH="Contact: <sip:"+id+"@"+contactip+":"+contactport+">";
            }
            else return true;
            //TO HEADER
            toH="To: \""+id+"\"<sip:"+id+"@"
                    +serverDomain+":"+serverPort+">";
            //FROM HEADER
            fromTag=SIPStack.newTag();
            fromH="From: \""+id+"\"<sip:"+id
                    +"@"+serverDomain+":"+serverPort+">;tag="+fromTag;
            //CALLID HEADER
            if(callId==null || callId.length()==0)
            {
                callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getTime()+".";
                callidH="Call-ID: "+callId;
            }
            //CSEQ HEADER
            if(CSEQ_NUMBER>65556) CSEQ_NUMBER=0;
            SIPStack.SIP_SEQUENCE_REGISTER=CSEQ_NUMBER;
            cseqH="CSeq: "+CSEQ_NUMBER+" REGISTER";
            //EXPIRES HEADER
            expiresH="Expires: 0";
            //ALLOW HEADER
            allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
            //USER-AGENT HEADER
            useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: 0";
            //AUTHORIZATION HEADER
            //
            if(authorizationH != null && authorizationH.length()>0)
            {
                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        authorizationH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;
                if(message.length()>0 && signalManager!=null) {
                    byte[] buffer=message.getBytes();
                    if(message.length()>0) {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                    serverIp,
                                    serverPort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                        regTime          = new Date();
                    }
                }
                return true;
            }
            else
            {

                message=commandLine+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        maxforwardH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END +
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        expiresH+SIPStack.SIP_LINE_END+
                        allowH+SIPStack.SIP_LINE_END+
                        useragentH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;
                if(message.length()>0 && signalManager!=null) {
                    byte[] buffer=message.getBytes();
                    if(message.length()>0) {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+message);
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(
                                    serverIp,
                                    serverPort,
                                    buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                        regTime          = new Date();
                    }
                }
                return true;
            }
        }catch(Exception e){}
        return false;
    }
    public static String getSHVE140SLocalIpAddress()
    {
        String validIp=null;
        boolean bWifi=false;

        try {
            Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements())
            {
                NetworkInterface intf=(NetworkInterface) en.nextElement();

                Log.e("SIP", "intf : "+intf.getName());
                if(intf!=null && intf.getName().length()>0 && intf.getName().startsWith("wlan")==true )
                {
                    bWifi=true;
                }
                else
                {
                    bWifi=false;
                }

                if(true)
                {

                    Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                    while(enumIpAddr.hasMoreElements())
                    {
                        InetAddress inetAddress=(InetAddress) enumIpAddr.nextElement();
                        String strIp=null;
                        Log.e("SIP", "addr : "+inetAddress.toString());

                        if(intf.getName().startsWith("wlan"))
                        {
                            if(inetAddress.toString()!=null && inetAddress.toString().length()>1)
                            {
                                strIp=inetAddress.toString().substring(1);
                                StringTokenizer st= new StringTokenizer(strIp,".",true);
                                int tokenCount=0;
                                String token=null;
                                int ipdigit_0=0;
                                int ipdigit_1=0;
                                int ipdigit_2=0;
                                int ipdigit_3=0;
                                while(st.hasMoreTokens())
                                {
                                    token=st.nextToken().trim();
                                    if(token!=null && token.length()>0
                                            && token.length()<=3
                                            && token.compareTo(".")!=0)
                                    {
                                        if(tokenCount==0)
                                        {
                                            if(token.trim().equals("::1")){
                                                return "127.0.0.1";
                                            }
                                            ipdigit_0=Integer.parseInt(token.trim());
                                        }
                                        else if(tokenCount==1)
                                        {
                                            ipdigit_1=Integer.parseInt(token.trim());
                                        }
                                        else if(tokenCount==2)
                                        {
                                            ipdigit_2=Integer.parseInt(token.trim());
                                        }
                                        else if(tokenCount==3)
                                        {
                                            ipdigit_3=Integer.parseInt(token.trim());
                                        }
                                        tokenCount++;
                                    }
                                }
                                if(tokenCount==4)
                                {
	                           /*
	                           if(    ipdigit_0>0 && ipdigit_0<=255 && ipdigit_1>=0 && ipdigit_1<=255 && ipdigit_2>=0 && ipdigit_2<=255 &&
	                              ipdigit_3>=0 && ipdigit_3<=255 &&
	                              !( //localloop
	                                       ipdigit_0 == 127 &&    ipdigit_1 == 0 && ipdigit_2 == 0 &&    ipdigit_3 == 1 ))
	                            */
                                    if(    ipdigit_0>0 && ipdigit_0<=255 && ipdigit_1>=0 && ipdigit_1<=255 && ipdigit_2>=0 && ipdigit_2<=255 &&
                                            ipdigit_3>=0 && ipdigit_3<=255 )
                                    {
                                        // wifi is wlan0

                                        boolean bLast=true;
                                        //that is  sk and etc all case
                                        if(!bLast || bWifi)
                                        {
                                            validIp=ipdigit_0+"."+ipdigit_1+"."+ipdigit_2+"."+ipdigit_3;
                                            Log.i("SIPCTRL","We got i/f:"+validIp);
                                            return validIp;

                                        }
                                        else if(bLast)
                                        {
                                            validIp=ipdigit_0+"."+ipdigit_1+"."+ipdigit_2+"."+ipdigit_3;
                                            Log.i("SIPCTRL","We got i/f:"+validIp);

                                        }
                                    }

                                }
                                //
                            }
                        }

                    }
                }
            }
        } catch(SocketException exception)
        {
            Log.i("SIPCTRL","We got Exception here:"+exception.toString());
        } catch (Exception e){
            validIp = null;
        }

        return validIp;

    }
    //SipSignalReceiver class define
    class SipSignalReceiver extends Thread {
        InetAddress                thisComputer;
        DatagramSocket theSocket;
        protected DatagramPacket dp;
        protected int           SIP_SIGNAL_PORT    = 5050;
        protected boolean        bFlag        = false;//2012 08 10
        boolean             bReady       = false;

        byte[] buffer;
        int port;

        public SipSignalReceiver (InetAddress thisComputer,int port_user)
        {
            bReady=false;
            port = port_user;
            this.thisComputer = thisComputer;
	         /*
	         try {

	            if(port>0 && port<=65556)
	            {
	               //System.setProperty("java.net.preferIPv6Addresses", "false");
	               SIP_SIGNAL_PORT=port;

	               for(int i=0;i<SIPStack.MAX_SIGNAL_PORTS;i++)
	               {
	                  theSocket=null;
	                  SIP_SIGNAL_PORT=port+i;
	                  System.out.println("+++++  try SIP Signal socket create  on "+SIP_SIGNAL_PORT);
	                  try {
	                     theSocket=new DatagramSocket(SIP_SIGNAL_PORT);
	                  } catch(Exception e){
	                     //theSocket.close();
	                     //break;
	                     continue;//2014 12 10
	                  }
	                  if(theSocket==null)
	                  {
	                     System.out.println("SIP Signal socket create failed on "+SIP_SIGNAL_PORT);
	                  }
	                  else {
	                     System.out.println("+++++  try SIP Signal socket create success  on "+SIP_SIGNAL_PORT);
	                     bFlag=true;
	                     localPort=SIP_SIGNAL_PORT;
	                     break;
	                  }

	               }
	               if(theSocket==null)
	               {
	                  System.out.println("!!! SOCKET CREATION ERROR.");
	                  bFlag=false;
	                  return;
	               }
	               theSocket.setReuseAddress(true);
	               //
	               this.thisComputer=thisComputer;
	            }

	            buffer = new byte[SIPStack.SIP_MAXMESSAGE_SIZE];

	         } catch(SocketException se)
	         {
	            //System.out.println("ERROR SipSignalReceiver Construct");
	            System.err.println(se);
	            SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
	         }
	         catch(Exception e)
	         {
	            System.out.println("SipSignalReceiver construct exception occurred.");
	         }
	          */

        }//SipSignalReceiver()

        public void run() {
            try {

                if(port>0 && port<=65556)
                {
                    //System.setProperty("java.net.preferIPv6Addresses", "false");
                    SIP_SIGNAL_PORT=port;

                    for(int i=0;i<SIPStack.MAX_SIGNAL_PORTS;i++)
                    {
                        theSocket=null;
                        SIP_SIGNAL_PORT=port+i;
                        System.out.println("+++++  try SIP Signal socket create  on "+SIP_SIGNAL_PORT);
                        try {
                            theSocket=new DatagramSocket(SIP_SIGNAL_PORT);
                        } catch(Exception e){
                            //theSocket.close();
                            //break;
                            continue;//2014 12 10
                        }
                        if(theSocket==null)
                        {
                            System.out.println("SIP Signal socket create failed on "+SIP_SIGNAL_PORT);
                        }
                        else {
                            System.out.println("+++++  try SIP Signal socket create success  on "+SIP_SIGNAL_PORT);
                            bFlag=true;
                            localPort=SIP_SIGNAL_PORT;
                            break;
                        }

                    }
                    if(theSocket==null)
                    {
                        System.out.println("!!! SOCKET CREATION ERROR.");
                        bFlag=false;
                        return;
                    }
                    theSocket.setReuseAddress(true);
                    //
                    //this.thisComputer=thisComputer;
                }

                buffer = new byte[SIPStack.SIP_MAXMESSAGE_SIZE];

            } catch(SocketException se)
            {
                //System.out.println("ERROR SipSignalReceiver Construct");
                System.err.println(se);
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            }
            catch(Exception e)
            {
                System.out.println("SipSignalReceiver construct exception occurred.");
            }

            try {
                dp = new DatagramPacket(buffer,buffer.length);
            }catch(NullPointerException ne) {
                System.err.println(ne);
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                return;
            }
            //


            bReady=true;

            while(bReady && theSocket!=null && dp!=null) {
                try {
                    //System.out.println("SOCKET SoTimeout:"+theSocket.getSoTimeout());
                    dp.setLength(SIPStack.SIP_MAXMESSAGE_SIZE);
                    dp.setData(buffer);
                    theSocket.receive(dp);

                    if(0 != BSSSipParser(dp))
                    {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SIP MESSAGE PARSER ERROR");
                        Thread.yield();
                    }
                    SIPStack.bIntraffic=true;

                }
                catch(SocketTimeoutException se) //2012 07 23
                {
                    break;
                }
                catch(IOException e)
                {
                    SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
                    break;
                }
                catch(Exception e1)
                {
                    //System.err.println(e1);
                    break;
                }
            }//while
            try
            {
                theSocket.close();
                theSocket=null;
            }catch(Exception e){}
            if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("###### Signal Thread terminated.");
        }//run()

        public int SIPParser(String message,int msgType,int methodType,String remoteIp,int remotePort) {
            if(SIPStack.SIP_MESSAGE_DEBUG==true)
            {
                System.out.println("<<< ("+remoteIp+":"+remotePort+")\n"+message);
            }
            //INVITE MESSAGE PROCESSING   20111201
            if(methodType==SIPStack.SIP_METHODTYPE_INVITE) {
                INVITEParser(message,msgType,remoteIp,remotePort);
            }
            //ACK MESSAGE PROCESSING 20111202
            else if(methodType==SIPStack.SIP_METHODTYPE_ACK) ACKParser(message,msgType,remoteIp,remotePort);
                //OPTIONS MESSAGE PROCESSING
            else if(methodType==SIPStack.SIP_METHODTYPE_OPTIONS)
            {
                if(
                        msgType    == SIPStack.SIP_MSGTYPE_REQUEST
                )
                {
                    sendOptionsResponse(message,remoteIp,remotePort,200);
                }
            }
            //INFO MESSAGE PROCESSING
            else if(methodType==SIPStack.SIP_METHODTYPE_INFO)
            {
                if(
                        msgType             == SIPStack.SIP_MSGTYPE_REQUEST    &&
                                sipCall.flag      == true                      &&
                                sipCall.callState  == SIPStack.SIP_CALLSTATE_CONNECTED
                )
                {

                    SIPRequestLine requestLine=new SIPRequestLine(message);
                    if(requestLine!=null)
                    {
                        //validate call
                        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                        boolean bValidCall=false;
                        if(sipHeader != null && sipHeader.flag==true)
                        {
                            if(
                                    sipHeader.headerValue!=null && //2012 03 23
                                            sipHeader.headerValue.length()>0 && //2012 03 23
                                            sipCall.callId!=null && //2012 03 23
                                            sipHeader.headerValue.compareTo(sipCall.callId)==0)
                            {
                                bValidCall=true;
                            }
                            else System.out.println("Invalid call");
                        }
                        //
                        if(bValidCall==true) {
                            sendInfoResponse(message,remoteIp,remotePort,200);
                        }
                    }
                }

            }
            //CANCEL MESSAGE PROCESSING 20111203
            else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) CANCELParser(message,msgType,remoteIp,remotePort);
                //BYE MESSAGE PROCESSING 20111204
            else if(methodType==SIPStack.SIP_METHODTYPE_BYE) BYEParser(message,msgType,remoteIp,remotePort);
                //REFER
            else if(methodType==SIPStack.SIP_METHODTYPE_REFER)
            {
                //CODING NEEDED
            }
            //NOTIFY
            else if(methodType==SIPStack.SIP_METHODTYPE_NOTIFY)
            {
                //CODING NEEDED
            }
            //MESSAGE
            else if(methodType==SIPStack.SIP_METHODTYPE_MESSAGE)
            {
                if(msgType == SIPStack.SIP_MSGTYPE_REQUEST)
                {
                    sendMessageResponse(message,remoteIp,remotePort,200);
                }
            }
            //SUBSCRIBE
            else if(methodType==SIPStack.SIP_METHODTYPE_SUBSCRIBE)
            {
                //CODING NEEDED
            }
            //PRACK
            else if(methodType==SIPStack.SIP_METHODTYPE_PRACK)
            {
                //CODING NEEDED
            }
            //
            return 0;
        }

        public int SIPParser(String message,int msgType,int methodType) {
            if(message==null || message.length()<=0) return 0;

            boolean bSendRegister=false;
            if(SIPStack.SIP_MESSAGE_DEBUG==true)
            {
                System.out.println("<<< \n"+message);
            }

            if(msgType!=SIPStack.SIP_MSGTYPE_RESPONSE || methodType!=SIPStack.SIP_METHODTYPE_REGISTER) return -1;


            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine==null || requestLine.flag==false) return 0;
            switch(requestLine.code)
            {
                case 200:
                {
                    //2012 11 27
                    repeatRegisterCount=0;
                    bUnregist=false;

                    //System.out.println(message);//for debug 2012 07 26
                    if(regState    == SIPStack.SIP_REGSTATE_REGISTERING || regState == SIPStack.SIP_REGSTATE_AUTHORIZING || regState == SIPStack.SIP_REGSTATE_UNREGISTERING)
                    {
                        //GET EXPIRES TIME
                        String requestUri="sip:"+id+"@"+localIp+":"+localPort;

                        Log.i("HomeTest","requestUri1111 : " + requestUri);

                        //2012 12 20
                        if(
                                ifIp!=null &&
                                        ifIp.length()>0 &&
                                        ifPort>0
                        )
                        {
                            if(bInterfaceChanged==true)
                                requestUri="sip:"+id+"@"+previfIp+":"+previfPort;
                            else requestUri="sip:"+id+"@"+ifIp+":"+ifPort;
                        }

                        Log.i("HomeTest","requestUri2222 : " + requestUri);

                        //2012 01 13
                        SIPCONTACTHeader contactHeader=new SIPCONTACTHeader(message,requestUri);
                        if(contactHeader!=null && contactHeader.flag==false)
                        {
                            contactHeader=new SIPCONTACTHeader(message);
                        }
                        if(contactHeader==null || contactHeader.flag==false || contactHeader.expires==0
                                || regState       == SIPStack.SIP_REGSTATE_UNREGISTERING ) //2013 02 12
                        {
                            if(bInterfaceChanged==true)
                            {
                                bInterfaceChanged=false;
                                regState      = SIPStack.SIP_REGSTATE_REGISTERING;
                                sendRegister();
                            }
                            else regState     = SIPStack.SIP_REGSTATE_IDLE;
                            break;
                        }
                        //GET EXPIRES HEADER
                        if(contactHeader.expires>0) {
                            expiresSeconds=contactHeader.getExpires();
                            System.out.println("++++++ expiresSeconds:"+expiresSeconds);
                        }
                        else if(contactHeader.expires<0)
                        {
                            SIPHeader expiresHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_EXPIRES);
                            if(expiresHeader!=null && expiresHeader.flag==true
                                    && expiresHeader.headerValue!=null && expiresHeader.headerValue.length()>0)
                            {
                                expiresSeconds=Integer.parseInt(expiresHeader.headerValue.trim());
                            }
                        }
                        //SET REGISTERSTATE AS REGISTERED
                        if(expiresSeconds>0)
                        {
                            regTime          = new Date();
                            regState      = SIPStack.SIP_REGSTATE_REGISTERED;
                            //Phone status set
                            try
                            {
                                if(sipCall==null || sipCall.flag==false)
                                {
                                    updateStatus("Ready.",false);
                                }


                            }catch(Exception e){

                            }
                            //2012 07 23 GET VIA RECEIVED
                            SIPVIAHeader viaHeader=new SIPVIAHeader(message);
                            if(viaHeader.flag==true && viaHeader.rportService==true
                                    && viaHeader.rport>0 && viaHeader.received.length()>0)
                            {
                                //2012 12 20
                                if(
                                        ifIp==null ||
                                                ifIp.length()==0 ||
                                                ifPort<=0 ||
                                                ifIp.compareToIgnoreCase(viaHeader.received) !=0 ||
                                                ifPort != viaHeader.rport
                                )
                                {
                                    bInterfaceChanged=true;
                                    previfIp=ifIp;
                                    previfPort=ifPort;
                                    ifIp=new String(viaHeader.received);
                                    ifPort=viaHeader.getRport();
                                    regState=SIPStack.SIP_REGSTATE_UNREGISTERING;
                                    sendUNRegister(previfIp,previfPort);
                                }
                            }
                        }
                        else if(expiresSeconds==0)
                        {
                            regTime          = new Date();
                            regState      = SIPStack.SIP_REGSTATE_IDLE;
                            //Phone status set
                            try
                            {
                                if(sipCall.flag==false) updateStatus("Service Off mode",false);
                            }catch(Exception e){}
                            //
                        }
                    }
                }
                break;
                case 401:
                {
                    if(repeatRegisterCount>3)
                    {
                        regState      = SIPStack.SIP_REGSTATE_UNAVAILABLE;
                        repeatRegisterCount=0;
                        //Phone status set
                        try
                        {
                            if(sipCall.flag==false) updateStatus("Server 인증 실패하였습니다.",false);
                        }catch(Exception e){}
                        //

                        break;
                    }

                    regState      = SIPStack.SIP_REGSTATE_UNAUTHORIZED;
                    //Phone status set
                    try
                    {
                        if(sipCall.flag==false) updateStatus("Regist Unauthorized.",false);
                    }catch(Exception e){}
                    //

                    authorizationH="";
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE);
                    //2013 02 12
                    if(sipHeader==null || sipHeader.flag==false)
                        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE);
                    //
                    if(sipHeader!=null && sipHeader.flag==true)//2012 03 22
                    {
                        SIPAUTHENTICATEHeader authHeader=
                                new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE);
                        //2013 02 12
                        if(authHeader==null || authHeader.flag==false)
                            authHeader=new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE);
                        //
                        if(authHeader!=null && authHeader.flag==true)//2012 03 22
                        {
                            if(authHeader.nonceValue.length()>0 && authHeader.realmValue.length()>0)
                            {
                                String qop=authHeader.qopValue;
                                String uri="sip:"+serverDomain+":"+serverPort;
                                authorizationH=getAuthorizationHeader(
                                        qop,authid,
                                        authHeader.realmValue,
                                        authpassword,uri,
                                        authHeader.nonceValue,
                                        SIPStack.SIP_METHODTYPE_REGISTER
                                );
                                bSendRegister=true;
                                repeatRegisterCount++;
                                break;
                            }
                        }
                    }
                    //
                }
                break;
                case 407:
                {
                    if(repeatRegisterCount>3)
                    {
                        regState      = SIPStack.SIP_REGSTATE_UNAVAILABLE;
                        repeatRegisterCount=0;
                        //Phone status set
                        try
                        {
                            if(sipCall.flag==false) updateStatus("Server 인증 실패하였습니다.",false);
                        }catch(Exception e){}
                        //

                        break;
                    }

                    //Phone status set
                    try
                    {
                        if(sipCall.flag==false) updateStatus("Regist Unauthorized.",false);
                    }catch(Exception e){}
                    //

                    regState      = SIPStack.SIP_REGSTATE_UNAUTHORIZED;
                    authorizationH="";
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE);
                    if(sipHeader!=null && sipHeader.flag==true)//2012 03 22
                    {
                        SIPAUTHENTICATEHeader authHeader=
                                new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE);
                        if(authHeader!=null && authHeader.flag==true)//2012 03 22
                        {
                            if(authHeader.nonceValue.length()>0 && authHeader.realmValue.length()>0)
                            {
                                String qop=authHeader.qopValue;
                                String uri="sip:"+serverDomain+":"+serverPort;
                                authorizationH=getProxyAuthorizationHeader(
                                        qop,authid,
                                        authHeader.realmValue,
                                        authpassword,uri,
                                        authHeader.nonceValue,
                                        SIPStack.SIP_METHODTYPE_REGISTER
                                );
                                bSendRegister=true;
                                //repeatRegisterCount++;
                                break;
                            }
                        }
                    }
                }
                break;
                //2012 02 01
                case 423:
                {
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_MINEXPIRES);
                    if(sipHeader!=null && sipHeader.flag==true)//2012 03 22
                    {
                        expiresSeconds=Integer.parseInt(sipHeader.headerValue);
                    }
                    regState      = SIPStack.SIP_REGSTATE_IDLE;
                    sendRegister();//2012 02 01
                    //
                }
                break;
                case 403:
                {
                    expiresSeconds=7;
                    regState      = SIPStack.SIP_REGSTATE_IDLE;
                    //sendRegister();//2012 02 01
                    //
                }
                break;
                default:
                {
                    if(requestLine.code>=400) {
                        regState      = SIPStack.SIP_REGSTATE_UNAVAILABLE;
                    }
                }
                break;
            }
            if(bSendRegister)
            {
                regState      = SIPStack.SIP_REGSTATE_AUTHORIZING;

                if(authorizationH!=null && authorizationH.length()>0) sendRegister();//2012 02 01

            }
            return 0;//normal

        }//SIPParser()
        public int BSSSipParser(DatagramPacket dp) {
            try
            {
                if(dp==null) return -1;//2012 03 23
                String remoteIp=null;

                byte[] address = dp.getAddress().getAddress();

                if(address==null || address.length<=0) return -1;//2012 03 23

                if(address.length>=4)
                {
                    int unsignedByte1 = address[address.length-4]<0 ? address[address.length-4]+256 : address[address.length-4];
                    int unsignedByte2 = address[address.length-3]<0 ? address[address.length-3]+256 : address[address.length-3];
                    int unsignedByte3 = address[address.length-2]<0 ? address[address.length-2]+256 : address[address.length-2];
                    int unsignedByte4 = address[address.length-1]<0 ? address[address.length-1]+256 : address[address.length-1];
                    remoteIp=unsignedByte1+"."+unsignedByte2+"."+unsignedByte3+"."+unsignedByte4;
                }

                else return -1;//2012 03 23


                int remotePort=dp.getPort();
                if(remotePort<=0) return -1;//2012 03 23
                int iMessageType=0; //0:not defined 1:request 2:response
                int iMethodType=0; //
                String s = new String(dp.getData(),0,0,dp.getLength());
                if(s==null|| s.length()<=0) return -1;//2012 03 23
                //SIP PARSE
                StringTokenizer st= new StringTokenizer(s,"\n",true);
                int headerCount=0;
                while(st.hasMoreTokens())
                {
                    String str=st.nextToken();

                    Log.i("TEST","str : " + str);

                    if(str.length()>0)
                    {
                        if(s.endsWith("\r\n")==false)
                        {
                            //System.out.println("+++++++ Warning: SIP MESSAGE End is not completed.");
                            //System.out.println("["+s+"]");

                        }
                        else {
                            // System.out.println("["+s+"]");
                            // System.out.println("MESSAGE LENGTH:"+s.length());
                            // s.trim();
                            // System.out.println("["+s+"]");
                            // System.out.println("TRIMMED MESSAGE LENGTH:"+s.length());
                            //
                            // System.out.println("++++++ SIP MESSAGE Ends normally.");
                        }
                        //INVITE
                        if(headerCount==0 && str.startsWith("INVITE") && str.endsWith("SIP/2.0\r"))
                        {
                            callStatus = true;
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;
                            iMethodType=SIPStack.SIP_METHODTYPE_INVITE;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);


                            break;//2012 07 24
                        }
                        //BYE
                        else if(headerCount==0 && str.startsWith("BYE") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_BYE;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //ACK
                        else if(headerCount==0 && str.startsWith("ACK") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_ACK;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //CANCEL
                        else if(headerCount==0 && str.startsWith("CANCEL") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_CANCEL;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //OPTIONS
                        else if(headerCount==0 && str.startsWith("OPTIONS") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_OPTIONS;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //INFO
                        else if(headerCount==0 && str.startsWith("INFO") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_INFO;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //PRACK
                        else if(headerCount==0 && str.startsWith("PRACK") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_PRACK;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //MESSAGE
                        else if(headerCount==0 && str.startsWith("MESSAGE") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_MESSAGE;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //SUBSCRIBE
                        else if(headerCount==0 && str.startsWith("SUBSCRIBE") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_SUBSCRIBE;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //REFER
                        else if(headerCount==0 && str.startsWith("REFER") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_REFER;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //NOTIFY
                        else if(headerCount==0 && str.startsWith("NOTIFY") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_NOTIFY;
                            SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            break;//2012 07 24
                        }
                        //REGISTER
                        else if(headerCount==0 && str.startsWith("REGISTER") && str.endsWith("SIP/2.0\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_REQUEST;iMethodType=SIPStack.SIP_METHODTYPE_REGISTER;
                            SIPParser(s,iMessageType,iMethodType);
                            break;
                        }
                        //RESPONSE
                        else if(headerCount==0 && str.startsWith("SIP/2.0") && str.endsWith("\r"))
                        {
                            iMessageType=SIPStack.SIP_MSGTYPE_RESPONSE;iMethodType=SIPStack.SIP_METHODTYPE_NONE;
                            int iS=s.indexOf("CSeq: ");
                            int iE=0;
                            if(iS>=0) iE=s.indexOf(SIPStack.SIP_LINE_END,iS);
                            if(iE>0) {
                            }
                            else break;

                            String strCSEQ=s.substring(iS,iE);

                            StringTokenizer tokenArray= new StringTokenizer(strCSEQ," ",true);
                            int tokenCount=0;
                            while(tokenArray.hasMoreTokens())
                            {
                                String token=tokenArray.nextToken();

                                if(token.length()>0 && token.compareTo(" ")!=0)
                                {
                                    tokenCount++;
                                    if(tokenCount==3)
                                    {
                                        if(token.compareTo("REGISTER")==0)        iMethodType=SIPStack.SIP_METHODTYPE_REGISTER;
                                        else if(token.compareTo("INVITE")==0)  iMethodType=SIPStack.SIP_METHODTYPE_INVITE;
                                        else if(token.compareTo("CANCEL")==0)  iMethodType=SIPStack.SIP_METHODTYPE_CANCEL;
                                        else if(token.compareTo("BYE")==0)        iMethodType=SIPStack.SIP_METHODTYPE_BYE;
                                        else if(token.compareTo("INFO")==0)    iMethodType=SIPStack.SIP_METHODTYPE_INFO;
                                        else if(token.compareTo("OPTIONS")==0)     iMethodType=SIPStack.SIP_METHODTYPE_OPTIONS;
                                        else if(token.compareTo("ACK")==0)        iMethodType=SIPStack.SIP_METHODTYPE_ACK;
                                        else if(token.compareTo("REFER")==0)   iMethodType=SIPStack.SIP_METHODTYPE_REFER;
                                        else if(token.compareTo("NOTIFY")==0)  iMethodType=SIPStack.SIP_METHODTYPE_NOTIFY;
                                        else if(token.compareTo("MESSAGE")==0)     iMethodType=SIPStack.SIP_METHODTYPE_MESSAGE;
                                        else if(token.compareTo("SUBSCRIBE")==0) iMethodType=SIPStack.SIP_METHODTYPE_SUBSCRIBE;
                                        else if(token.compareTo("PRACK")==0)   iMethodType=SIPStack.SIP_METHODTYPE_PRACK;
                                        break;
                                    }
                                }
                            }
                            //
                            if(iMethodType==SIPStack.SIP_METHODTYPE_REGISTER) SIPParser(s,iMessageType,iMethodType);
                            else if(iMethodType!=SIPStack.SIP_METHODTYPE_NONE) SIPParser(s,iMessageType,iMethodType,remoteIp,remotePort);
                            //
                            break;
                            //
                        }
                        else {
                            //System.out.println("Warning: NO SIP START data (length:"+s.length()+")");
                            break;
                        }
                    }
                }
            }catch(Exception e){}
            return 0;//normal

        }//BSSSipParser()

        public boolean sendProceeding(String remoteIp,int remotePort)
        {
            if(sipCall==null || sipCall.flag==false ) return false;


            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 100 "+SIPStack.getResponseDescription(100)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray!=null &&
                    sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: 0\r\n";
            //

            {
                String sendmessage=
                        commandLine+
                                viaString+
                                routeString+
                                sipCall.fromH+SIPStack.SIP_LINE_END+
                                sipCall.toH+SIPStack.SIP_LINE_END+
                                sipCall.callidH+SIPStack.SIP_LINE_END+
                                sipCall.cseqH+SIPStack.SIP_LINE_END+
                                contactH+SIPStack.SIP_LINE_END+
                                contentlengthH+SIPStack.SIP_LINE_END
                        ;
                if(sendmessage.length()>0 && signalManager!=null) {
                    byte[] buffer=sendmessage.getBytes();
                    if(sendmessage.length()>0) {
                        if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                        sipCall.callTime_T6          = new Date();
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }
            }

            return true;
        }

        public boolean sendProgressing(String remoteIp,int remotePort)
        {
            if(sipCall==null || sipCall.flag==false ) return false;


            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 180 "+SIPStack.getResponseDescription(180)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray!=null &&
                    sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();

            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: 0\r\n";
            //
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_END
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    sipCall.callTime_T6          = new Date();
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }

            return true;
        }
        public boolean rejectCall(String message,int code,String remoteIp,int remotePort)
        {
            if(message==null || message.length()==0 || remoteIp==null || remoteIp.length()==0
                    || remotePort<=0) return false;

            //CONSTRUCT RESPONSE HEADERS
            String viaH                   = "";
            StringBuffer viaArray        = new StringBuffer();
            String routeH              = "";
            StringBuffer routeArray          = new StringBuffer();
            String recordrouteH             = "";
            StringBuffer recordrouteArray  = new StringBuffer();
            String contactH                = "";
            String toH                = "";
            String fromH               = "";
            String callidH             = "";
            String cseqH               = "";
            String contentlengthH        = "";

            StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);
            while(tokenArray.hasMoreTokens())
            {
                String token=tokenArray.nextToken().trim();
                if(token.length()<=0) continue;
                if(token.compareTo("\r")==0) continue;
                if(token.compareTo("\n")==0) continue;
                if(token.startsWith("From: ")==true)
                {
                    fromH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("f: ")==true) //2012 04 27
                {
                    fromH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("To: ")==true)
                {
                    if(token.indexOf("tag=")<0) {
                        toH=token+";tag="+new Date().getTime()+SIPStack.SIP_LINE_END;
                    }
                    else toH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("t: ")==true) //2012 04 27
                {
                    if(token.indexOf("tag=")<0) {
                        toH=token+";tag="+new Date().getTime()+SIPStack.SIP_LINE_END;
                    }
                    else toH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("Via: ")==true)
                {
                    if(viaH==null || viaH.length()==0) {//2013 02 13
                        viaH=token;
                        int iS=token.indexOf(";rport=");
                        if(iS>0 && token.indexOf(";received=")<0)
                        {
                            String s1=token.substring(0,iS);
                            int iS_=token.indexOf(";",iS+1);
                            String s2=null;
                            if(iS_>0) {
                                s2=token.substring(iS_+1);
                                if(s1!=null && s2!=null) viaH=s1+";"+s2;
                                else if(s1!=null) viaH=s1;
                            }
                            else viaH=s1;

                            if(viaH!=null && viaH.length()>0) viaH=viaH+";received="+remoteIp+";rport="+remotePort;
                        }
                        if(viaH!=null && viaH.length()>0) viaArray.append(viaH+SIPStack.SIP_LINE_END);

                    }
                    else viaArray.append(token+SIPStack.SIP_LINE_END);
                }
                else if(token.startsWith("v: ")==true) //2012 04 27
                {
                    if(viaH==null || viaH.length()==0) {//2013 02 13
                        viaH=token;
                        int iS=token.indexOf(";rport=");
                        if(iS>0 && token.indexOf(";received=")<0)
                        {
                            String s1=token.substring(0,iS);
                            int iS_=token.indexOf(";",iS+1);
                            String s2=null;
                            if(iS_>0) {
                                s2=token.substring(iS_+1);
                                if(s1!=null && s2!=null) viaH=s1+";"+s2;
                                else if(s1!=null) viaH=s1;
                            }
                            else viaH=s1;

                            if(viaH!=null && viaH.length()>0) viaH=viaH+";received="+remoteIp+";rport="+remotePort;
                        }
                        if(viaH!=null && viaH.length()>0) viaArray.append(viaH+SIPStack.SIP_LINE_END);
                    }
                    else viaArray.append(token+SIPStack.SIP_LINE_END);
                }
                else if(token.startsWith("Record-Route: ")==true)
                {
                    if(recordrouteH==null || recordrouteH.length()==0) {//2013 02 13
                        routeH=null;
                        routeArray=new StringBuffer();
                        recordrouteH=token;
                        recordrouteArray.append(recordrouteH+SIPStack.SIP_LINE_END);
                        routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                    }
                    else {
                        recordrouteArray.append(token+SIPStack.SIP_LINE_END);
                        routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                    }
                }
                else if(token.startsWith("Call-ID: ")==true)
                {
                    callidH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("i: ")==true) //2012 04 27
                {
                    callidH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("CSeq: ")==true)
                {
                    cseqH=token+SIPStack.SIP_LINE_END;
                }
                else if(token.startsWith("Content-Length: ")==true)
                {
                    contentlengthH="Content-Length: 0\r\n";
                }
                else if(token.startsWith("l: ")==true) //2012 04 27
                {
                    contentlengthH="Content-Length: 0\r\n";
                }
            }

            //REQUEST LINE
            String commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(viaArray.length()>0) viaString=viaArray.toString();
            String routeString="";
            if(recordrouteArray.length()>0) routeString=recordrouteArray.toString();
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: "+0+SIPStack.SIP_LINE_END;

            //
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            fromH+
                            toH+
                            callidH+
                            cseqH+
                            contactH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_END
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
            return true;
        }
        public String getAuthorizationHeader(
                String qop,
                String authid,
                String realmValue,
                String authpassword,
                String uri,
                String nonceValue,
                int       methodType
        )
        {
            String strHeader=null;
            String finalDigest=null;
            String step1Digest=SIPStack.BSSMD5Get(authid+":"+realmValue+":"+authpassword);
            String step2Digest=null;
            if(methodType==SIPStack.SIP_METHODTYPE_REGISTER) step2Digest=SIPStack.BSSMD5Get("REGISTER:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_INVITE) step2Digest=SIPStack.BSSMD5Get("INVITE:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) step2Digest=SIPStack.BSSMD5Get("CANCEL:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_ACK) step2Digest=SIPStack.BSSMD5Get("ACK:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_BYE) step2Digest=SIPStack.BSSMD5Get("BYE:"+uri);
            registerNonceCount++;
            if(qop.length()>0)
            {
                int nonceCount=0;
                String cnonce=SIPStack.BSSMD5Get("60cf184b29500b20"+registerNonceCount);
                if(nonceValue.length()>0) nonceCount=1;
                finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+String.format("%08x",nonceCount)+":"+cnonce+":"+qop+":"+step2Digest);
                strHeader="Authorization: Digest"+
                        " username=\""+authid+"\","+
                        " realm=\""+realmValue+"\","+
                        " nonce=\""+nonceValue+"\","+
                        " uri=\""+uri+"\","+
                        " response=\""+finalDigest+"\","+
                        " cnonce=\""+cnonce+"\","+
                        " nc="+String.format("%08x",nonceCount)+","+
                        " qop="+qop+","+
                        " algorithm=MD5";

            }
            else {
                finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+step2Digest);
                strHeader="Authorization: Digest"+
                        " username=\""+authid+"\","+
                        " realm=\""+realmValue+"\","+
                        " nonce=\""+nonceValue+"\","+
                        " uri=\""+uri+"\","+
                        " response=\""+finalDigest+"\","+
                        " algorithm=MD5";
            }


            return strHeader;
        }

        public String getProxyAuthorizationHeader(
                String qop,
                String authid,
                String realmValue,
                String authpassword,
                String uri,
                String nonceValue,
                int       methodType
        )
        {
            String strHeader=null;
            String finalDigest=null;
            String step1Digest=SIPStack.BSSMD5Get(authid+":"+realmValue+":"+authpassword);
            String step2Digest=null;
            if(methodType==SIPStack.SIP_METHODTYPE_REGISTER) step2Digest=SIPStack.BSSMD5Get("REGISTER:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_INVITE) step2Digest=SIPStack.BSSMD5Get("INVITE:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) step2Digest=SIPStack.BSSMD5Get("CANCEL:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_ACK) step2Digest=SIPStack.BSSMD5Get("ACK:"+uri);
            else if(methodType==SIPStack.SIP_METHODTYPE_BYE) step2Digest=SIPStack.BSSMD5Get("BYE:"+uri);
            registerNonceCount++;

            if(qop.length()>0)
            {
                int nonceCount=0;
                //String cnonce="60cf184b29500b20";
                String cnonce=SIPStack.BSSMD5Get("60cf184b29500b20"+registerNonceCount);
                if(nonceValue.length()>0) nonceCount=1;
                finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"
                        +String.format("%08x",nonceCount)+":"+cnonce+":"+qop+":"+step2Digest);
                strHeader="Proxy-Authorization: Digest"+
                        " username=\""+authid+"\","+
                        " realm=\""+realmValue+"\","+
                        " nonce=\""+nonceValue+"\","+
                        " uri=\""+uri+"\","+
                        " response=\""+finalDigest+"\","+
                        " cnonce=\""+cnonce+"\","+
                        " nc="+String.format("%08x",nonceCount)+","+
                        " qop="+qop+","+
                        " algorithm=MD5";


            }
            else {
                finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+step2Digest);
                strHeader="Proxy-Authorization: Digest"+
                        " username=\""+authid+"\","+
                        " realm=\""+realmValue+"\","+
                        " nonce=\""+nonceValue+"\","+
                        " uri=\""+uri+"\","+
                        " response=\""+finalDigest+"\","+
                        " algorithm=MD5";

            }

            return strHeader;
        }


        public int BSSSipSendUdpPacket(final String sIp,final int iPort,final byte[] data,final int dataSize)
        {
            int sent=0;
            if(sIp==null || sIp.length()==0) return 0;//2012 03 22
            if(iPort<=0) return 0;
            if(dataSize<=0) return 0;
            //System.out.println("+++     BSSSipSendUdpPacket:"+sIp+":"+iPort);
            //System.out.println("+++     SOCKET STATE  closed:"+theSocket.isClosed()+"  connected:"+theSocket.isConnected());

            try {
                InetAddress ia=InetAddress.getByName(sIp);
                if(ia!=null)
                {
                    DatagramPacket dp=new DatagramPacket(data,dataSize,ia,iPort);
                    if(dp!=null) theSocket.send(dp);

                    sent=dataSize;
                    SIPStack.bOuttraffic=true;
                }

            }
            catch(UnknownHostException uhe) {
                System.err.println(uhe);
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            }
            //2012 07 26
            catch(SocketException she) {
                System.out.println("Cause:  "+she.getCause());
                System.out.println("Reason: "+she.getMessage());
                if(
                        she.getMessage().indexOf("ENETUNREACH")>=0 ||
                                she.getMessage().indexOf("Network is unreachable")>=0
                )
                {
                    SIPStack.networkStatus                = SIPStack.SIP_NETIF_UNREACHABLE;
                    //System.out.println("networkStatus was set to SIP_NETIF_UNREACHABLE");
                }
                System.err.println(she);
                try {
                    //System.out.println("!!! Socket is invalid, renew.");
                    theSocket=new DatagramSocket(SIP_SIGNAL_PORT);
                }catch(Exception e) {
                    System.err.println(e);
                }
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            }
            //
            catch(IOException ie) {
                System.err.println(ie);
                SIPStack.exceptionCountAtCurrentCall++;//2012 03 23
            } catch(Exception e) //2012 07 13 when call OnNetworkException error processing
            {
                //System.out.println("!!!BSSSipSendUdpPacket Exception occurred. <BSS>:"+sent);
                System.err.println(e);
            }


            return sent;

        }

    }//class SipSignalReceiver

    //
    class CTRLTimerTask extends TimerTask {
        public void run() {

            if(SIGNALCTRLTimer==null)
                return;

            try
            {
                //Phone status set
                if(sipCall!=null && sipCall.flag==true
                        && sipCall.bCallPrepared==true
                        && sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED)
                {
                    updateStatus("New call arrived.",false);
                    notifyIncomingCall("New call arrived.",sipCall.dnis);

                    sipCall.bCallPrepared=false;
                    sipCall.bDestroyActivity=true;
                    //Log.i("SIPService", ">> prepaired call is exist. notify to dialer call is comming.");
                }

            }catch(Exception e){}


            try
            {
                //2012 08 10
                if(SIPStack.networkStatus!=SIPStack.SIP_NETIF_AVAILABLE)
                {
                    try
                    {
                        if(SIPStack.networkStatus == SIPStack.SIP_NETIF_UNREACHABLE)
                        {
                            updateStatus("NETWORK UNREACHABLE.",true);
                        }
                        else if(SIPStack.networkStatus == SIPStack.SIP_NETIF_UNAVAILABLE)
                        {
                            updateStatus("NETWORK not ready.",true);
                        }
                    }catch(Exception e){}
                    if(SIPStack.networkStatus==SIPStack.SIP_NETIF_UNAVAILABLE)
                    {
                        if (connectivityManager!=null && connectivityManager.getActiveNetworkInfo() != null) {
                            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                            switch (activeNetwork.getType()) {
                                case ConnectivityManager.TYPE_WIMAX: // 4g 망 체크
                                    SIPStack.isInternetWiMax = true;
                                    SIPStack.isInternetWiFi = false;
                                    SIPStack.isInternetMobile = false;
                                    SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                    //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                    break;
                                case ConnectivityManager.TYPE_WIFI: // wifi망 체크
                                    SIPStack.isInternetWiMax = false;
                                    SIPStack.isInternetWiFi = true;
                                    SIPStack.isInternetMobile = false;
                                    SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                    //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                    break;
                                case ConnectivityManager.TYPE_MOBILE: // 3g 망 체크
                                    SIPStack.isInternetWiMax = false;//false;
                                    SIPStack.isInternetWiFi = false;
                                    SIPStack.isInternetMobile = true;
                                    //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                                    SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                                    break;
                            }
                        }
                        //else{
                        //   System.out.println("네트워크가 연결되어 있지 않습니다. 확인해주세요");
                        //}
                        //System.out.println("+++ 1 4g is "+SIPStack.isInternetWiMax + "  wifi is "+SIPStack.isInternetWiFi +"  3g is "+ SIPStack.isInternetMobile);

                        netcheckIntervalTimer=new Date();
                        try {
                            //
                            String host=SIPStack.getSHVE140SLocalIpAddress();
                            if(host!=null && host.length()>0) {
                                Log.i("Home","getIPV4 3333");
                                String ip=SIPStack.getIPV4(host);
                                //System.out.println("ip address:"+ip);
                                if(ip!=null && ip.length()>0) SIPStack.networkStatus=SIPStack.SIP_NETIF_AVAILABLE;
                                if(ip!=null && ip.length()>0 && ip.compareToIgnoreCase(localIp)!=0)
                                {
                                    //System.out.println("++++++++ IP Changed "+localIp+" to "+ip);
                                    localIp=new String(ip);
                                    //2013 02 14
                                    if(
                                            SIPStack.usePrivateWiMax   == true &&
                                                    (SIPStack.isInternetWiMax==true || SIPStack.isInternetMobile== true) &&
                                                    SIPStack.isPrivateIp(ip)==false
                                    )
                                    {
                                        SIPStack.localSdpIp="192.168.10.2";
                                    }
                                    else SIPStack.localSdpIp=new String(ip);
                                    //

                                    //2012 12 20
                                    if(ifIp==null || ifIp.length()==0 || ifPort<=0)
                                    {
                                        ifIp=new String(localIp);
                                        ifPort=SIPStack.SIP_LOCAL_PORT;
                                    }
                                    //
                                }
                                //2012 08 10
                                else if(ip==null || ip.length()==0)
                                {
                                    localIp="127.0.0.1";
                                    //2013 02 14
                                    SIPStack.localSdpIp="127.0.0.1";
                                    //

                                    SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                                }
                                //
                            }
                            //2012 08 10
                            else if(host==null || host.length()==0)
                            {
                                localIp="127.0.0.1";
                                //2013 02 14
                                SIPStack.localSdpIp="127.0.0.1";
                                //
                                SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                            }
                            //

                        }catch(Exception e){}
                    }


                    return;
                }
                //
                //System.out.println("Timer ....");
                //REGIST CONTROL
                if(ctrlIndex==0) {
                    if(SIPStack.bRegistMode==true) {
                        SIPCTRLRegister();
                    }
                    else {
                        regState=SIPStack.SIP_REGSTATE_REGISTERED;
                    }
                }
                //CALL CONTROL
                else if(ctrlIndex==1) {
                    //2012 07 16
                    if(sipCall!=null && sipCall.flag==true && sipCall.bCancelRequest )
                    {
                        callTerminate();
                        sipCall.bCancelRequest=false;
                    }
                    //2012 09 07
                    else if(sipCall!=null && sipCall.flag==true && sipCall.bRejectRequest )
                    {
                        if(
                                sipCall.remoteSdp!=null && //2012 03 22
                                        sipCall.remoteSdp.flag==true &&
                                        audioRTPManager!=null )
                        {
                            if(
                                    sipCall.remoteSdp.audioM!=null && //2012 03 22
                                            sipCall.remoteSdp.audioM.flag==true)
                            {
                                //
                                if(
                                        sipCall.sdp!=null && //2012 03 22
                                                sipCall.sdp.audioM!=null && //2012 03 22
                                                audioRTPManager != null)
                                {
                                    audioRTPManager.setRemoteMediaInfo(
                                            sipCall.remoteSdp.audioM.commonCodec,
                                            sipCall.remoteSdp.audioM.mediaIp,
                                            sipCall.remoteSdp.audioM.mediaPort);
                                    audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                }
                            }
                        }

                        sipCall.bRejectRequest=false;
                        sipCall.bCancelRequest=true;
                    }
                    //
                    else if(
                            sipCall!=null &&
                                    sipCall.bNewcallRequest &&
                                    regState==SIPStack.SIP_REGSTATE_REGISTERED
                    )
                    {
                        callActivate(sipCall.number);
                        sipCall.bNewcallRequest=false;
                    }
                    //2012 08 20
                    else if(sipCall!=null &&  sipCall.bUpdateRequest )
                    {
                        if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED) {
                            sendHold();
                        }
                        sipCall.bUpdateRequest=false;
                    }
                    //2019/03/05
                    else{
                        if(callActive || isPushCall) {
                            SIPCTRLCall();
                        }
                    }
                }
                //

                //
                if(ctrlIndex==0) ctrlIndex=1;
                else ctrlIndex=0;
            }catch(NullPointerException e){}
            catch(Exception ee){}
        }

    }
    //
    public void SIPCTRLCall()
    {
        Date currentTime=new Date();

        int durationMilli=(int)(currentTime.getTime()-ctrlIntervalTimer.getTime());
        int durationRegistStatus=(int)(currentTime.getTime()-registStatusTimer.getTime())/1000;
        if(durationMilli<500) {
            return;
        }
        else {
            ctrlIntervalTimer=new Date();

            durationMilli=(int)((currentTime.getTime()-netcheckIntervalTimer.getTime())/1000);
            if(durationMilli>SIPStack.NETWORKIF_CHECK_TIME)
            {
                if (connectivityManager!=null && connectivityManager.getActiveNetworkInfo() != null) {
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_WIMAX: // 4g 망 체크
                            //2013 02 14
                            SIPStack.isInternetWiMax = true;
                            SIPStack.isInternetWiFi = false;
                            SIPStack.isInternetMobile = false;
                            SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                            //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                            break;
                        case ConnectivityManager.TYPE_WIFI: // wifi망 체크
                            //2013 02 14
                            SIPStack.isInternetWiMax = false;
                            SIPStack.isInternetWiFi = true;
                            SIPStack.isInternetMobile = false;
                            SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                            //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                            break;
                        case ConnectivityManager.TYPE_MOBILE: // 3g 망 체크
                            //2013 02 14
                            SIPStack.isInternetWiMax = false;//false;
                            SIPStack.isInternetWiFi = false;
                            SIPStack.isInternetMobile = true;
                            //SIPStack.PRIMARY_CODEC_AUDIO = SIPStack.SIP_CODEC_G729;
                            SIPStack.PRIMARY_CODEC_AUDIO   = SIPStack.SIP_CODEC_G711U;
                            break;
                    }
                }
                netcheckIntervalTimer=new Date();
                try {
                    //
                    String host=SIPStack.getSHVE140SLocalIpAddress();
                    if(host!=null && host.length()>0) {
                        Log.i("Home","getIPV4 4444");
                        String ip=SIPStack.getIPV4(host);
                        //System.out.println("ip address:"+ip);
                        if(ip!=null && ip.length()>0) SIPStack.networkStatus=SIPStack.SIP_NETIF_AVAILABLE;
                        if(ip!=null && ip.length()>0 && ip.compareToIgnoreCase(localIp)!=0)
                        {
                            //System.out.println("++++++++ IP Changed "+localIp+" to "+ip);
                            localIp=new String(ip);
                            //2013 02 14
                            if(
                                    SIPStack.usePrivateWiMax   == true &&
                                            (SIPStack.isInternetWiMax==true || SIPStack.isInternetMobile== true) &&
                                            SIPStack.isPrivateIp(ip)==false
                            )
                            {
                                SIPStack.localSdpIp="192.168.10.2";
                            }
                            else SIPStack.localSdpIp=new String(ip);
                            //

                            //2012 12 20
                            bInterfaceChanged=true;
                            previfIp=ifIp;
                            previfPort=ifPort;
                            ifIp=new String(localIp);
                            ifPort=SIPStack.SIP_LOCAL_PORT;
                            try
                            {
                                if(sipCall!=null)
                                {
                                    sipCall.ifIp=ifIp;
                                    sipCall.ifPort=ifPort;
                                }
                            }catch(Exception e){}

                            sendUNRegister(previfIp,previfPort);
                            regState=SIPStack.SIP_REGSTATE_UNREGISTERING;
                            //

                        }
                        //2012 08 10
                        else if(ip==null || ip.length()==0)
                        {
                            localIp="127.0.0.1";
                            //2013 02 14
                            SIPStack.localSdpIp="127.0.0.1";
                            //
                            System.out.println("Network Inferface is invalid.");
                            //updateStatus("Network is off.",false);
                            SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                        }
                        //
                    }
                    //2012 08 10
                    else if(host==null || host.length()==0)
                    {
                        localIp="127.0.0.1";
                        //2013 02 14
                        SIPStack.localSdpIp="127.0.0.1";
                        //
                        System.out.println("Network Inferface is invalid.");
                        SIPStack.networkStatus=SIPStack.SIP_NETIF_UNAVAILABLE;

                    }
                    //

                }catch(Exception e){}
            }

        }
        if(durationRegistStatus>0)
        {
            try
            {
                if((SIPStack.bIntraffic||SIPStack.bOuttraffic) && sipCall!=null && sipCall.flag==false)
                {
                    if(regState==SIPStack.SIP_REGSTATE_REGISTERED) {
                        registStatus(true);
                    }
                    else {
                        registStatus(false);
                    }
                    registStatusTimer=new Date();

                }
                else if(durationRegistStatus>3)
                {
                    if(regState==SIPStack.SIP_REGSTATE_REGISTERED) {
                        registStatus(true);
                    }
                    else {
                        registStatus(false);
                    }
                    registStatusTimer=new Date();
                }
            }catch(Exception e){}
        }

        if(sipCall==null || sipCall.flag==false) {
            try
            {
                if(regState==SIPStack.SIP_REGSTATE_REGISTERED) {
                    updateStatus("Ready.",bRequestReadySet);
                }
                else updateStatus("Not registered.",bRequestReadySet);

                if(bRequestReadySet==true)
                {
                    bRequestReadySet=false;
                }
            }catch(Exception e){}
            //

            return;
        }
        //if(isActiveConference()==false)
        if( sipCall!=null && sipCall.flag==true)//2012 02 22
        {
            if(sipCall.callTime_TI!=null) sipCall.expiresTI=(int)(currentTime.getTime()-sipCall.callTime_TI.getTime())/1000;
            if(sipCall.callTime_T0!=null) sipCall.expiresT0=(int)(currentTime.getTime()-sipCall.callTime_T0.getTime())/1000;
            if(sipCall.callTime_T00!=null) sipCall.expiresT00=(int)(currentTime.getTime()-sipCall.callTime_T00.getTime())/1000;
            if(sipCall.callTime_T1!=null) sipCall.expiresT1=(int)(currentTime.getTime()-sipCall.callTime_T1.getTime())/1000;
            if(sipCall.callTime_T2!=null) sipCall.expiresT2=(int)(currentTime.getTime()-sipCall.callTime_T2.getTime())/1000;
            if(sipCall.callTime_T3!=null) sipCall.expiresT3=(int)(currentTime.getTime()-sipCall.callTime_T3.getTime())/1000;
            if(sipCall.callTime_T4!=null) sipCall.expiresT4=(int)(currentTime.getTime()-sipCall.callTime_T4.getTime())/1000;
            if(sipCall.callTime_T5!=null) sipCall.expiresT5=(int)(currentTime.getTime()-sipCall.callTime_T5.getTime())/1000;
            if(sipCall.callTime_T6!=null) sipCall.expiresT6=(int)(currentTime.getTime()-sipCall.callTime_T6.getTime())/1000;
            if(sipCall.callTime_T7!=null) sipCall.expiresT7=(int)(currentTime.getTime()-sipCall.callTime_T7.getTime())/1000;
            if(sipCall.callTime_T8!=null) sipCall.expiresT8=(int)(currentTime.getTime()-sipCall.callTime_T8.getTime())/1000;

            if(sipCall.callState==SIPStack.SIP_CALLSTATE_IDLE)
            {
                if(sipCall.expiresTI>SIPStack.CS_TIMEOUT_TI)
                {
                    //IDLE TIMEOUT PROCESSING
                    sipCall.exceptionTI();
                    updateStatus("Ready.",true);
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING)
            {
                //if(SIPStack.ventureCall==true)//original
                if(SIPStack.ventureCall == true && sipCall.expiresT00>3) //2013 02 12
                {
                    sendReInvite();
                    SIPStack.ventureCall=false;
                    //System.out.println("+++++++ send repeat invite:");
                }
                else if(sipCall.expiresT00>SIPStack.CS_TIMEOUT_T00)
                {
                    //INVITING REPEAT TIMEOUT PROCESSING
                    sendReInvite();
                    SIPStack.ventureCall=false;
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING)
            {
                if(sipCall.expiresT1>SIPStack.CS_TIMEOUT_T1)
                {
                    //PROCEEDING TIMEOUT PROCESSING
                    sipCall.exceptionT1();
                    //2012 02 20 CONFERENCE CALL CHECK NEEDED
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 1");
                        audioRTPManager.RTPEnd();//2012 01 19
                    }
                    sendCancel();
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING)
            {
                if(sipCall.expiresT2>SIPStack.CS_TIMEOUT_T2)
                {
                    //PROGRESSING TIMEOUT PROCESSING
                    sipCall.exceptionT2();
                    //2012 02 20 CONFERENCE CALL CHECK NEEDED
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 2");
                        audioRTPManager.RTPEnd();//2012 01 19
                    }
                    sendCancel();
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_ACCEPTED)
            {
                if(sipCall.expiresT3> 0 && (sipCall.expiresT3 % 2) ==0 && sipCall.finalResponseMessage!=null && sipCall.finalResponseMessage.length()>0)
                {
                    try
                    {
                        byte[] buffer=sipCall.finalResponseMessage.getBytes();
                        if(sipCall.finalResponseMessage.length()>0) {
                            if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sipCall.finalResponseMessage);
                            signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        }

                    }catch(Exception e){}
                }
                if(sipCall.expiresT3>SIPStack.CS_TIMEOUT_T3)
                {
                    //Log.i("BYE","Debug 1");
                    //ACCEPTED TIMEOUT PROCESSING
                    sipCall.exceptionT3();
                    //2012 02 20 CONFERENCE CALL CHECK NEEDED
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 3");
                        audioRTPManager.RTPEnd();//2012 01 19
                    }
                    sendBye();
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED)
            {
                try
                {
                    //
                    if(SIPStack.bTcpVideoMode==false && videoManager!=null && videoManager.isAlive()==true)//2015 07 07 updated
                    {
                        int hotbitDuration=(int)(new Date().getTime()-videoManager.hotbitTime.getTime());
                        if(hotbitDuration>3000) videoManager.SendHotbit();
                    }
                }catch(Exception e){}

                //2014 12 19
                try
                {
                    if(audioRTPManager!=null && sipCall.expiresT4>120)//second 15
                    {
                        //
                        int muteDuration=(int)(new Date().getTime()-audioRTPManager.rtpArrivalTime.getTime());

                        if(muteDuration>2000)
                        {
                            //Log.i("BYE","Debug 2");
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 4");
                                audioRTPManager.RTPEnd();//2012 01 19
                            }
                            sendBye();
                        }

                        //
                    }

                }catch(Exception e){}

                //
                if(sipCall.expiresT4>SIPStack.CS_TIMEOUT_T4)
                {
                    //Log.i("BYE","Debug 3");
                    //CONNECTED TIMEOUT PROCESSING
                    sipCall.exceptionT4();
                    //2012 02 20 CONFERENCE CALL CHECK NEEDED
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 5");
                        audioRTPManager.RTPEnd();//2012 01 19
                    }
                    sendBye();
                }
                //2012 05 28
                else {
                    int expiresT40=0;
                    if(sipCall.callTime_T40!=null) {
                        expiresT40=(int)(currentTime.getTime()-sipCall.callTime_T40.getTime())/1000;
                        sipCall.callDuration=sipCall.expiresT4;
                        //System.out.println("call duration:"+sipCall.callDuration);
                        if(expiresT40>0) {
                            int hour=sipCall.expiresT4/3600;
                            int min=(sipCall.expiresT4-hour*3600)/60;
                            int sec=sipCall.expiresT4-hour*3600-min*60;
                            String s=hour+":"+min+":"+sec;
                            boolean bWakeup=false;
                            if(sec==0 && min>0) bWakeup=true;
                            updateDuration(0,s,bWakeup);
                            sipCall.callTime_T40=new Date();
                        }
                    }
                }
                //
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_DISCONNECTING)
            {
                if(sipCall.expiresT5>SIPStack.CS_TIMEOUT_T5)
                {
                    //DISCONNECTING TIMEOUT PROCESSING
                    sipCall.exceptionT5();
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_TERMINATING)
            {
                if(sipCall.expiresT6>SIPStack.CS_TIMEOUT_T6)
                {
                    //TERMINATING TIMEOUT PROCESSING

                    sipCall.exceptionT6();
                    ctrlIntervalTimer=new Date();
                    bRequestReadySet=true;
                }

            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED)
            {
                //AUTO ACCEPT
                //2015 07 29

                //if(mRequest)
                //{
                bAcceptInvoked=true;
                //}

                if(vibrateControlTimer!=null) vibrateControlTimer.cancel();
                vibrateControlTimer=null;

                if(sipCall.expiresT7>SIPStack.CS_TIMEOUT_T7)
                {
                    //OFFERRED TIMEOUT PROCESSING
                    sipCall.exceptionT7();

                    //2012 02 20 CONFERENCE CALL CHECK NEEDED
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 6");
                        audioRTPManager.RTPEnd();//2012 01 19
                    }
                    rejectCall(408);
                }
            }
            else if(sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLING)
            {
                if(sipCall.expiresT8>SIPStack.CS_TIMEOUT_T8)
                {
                    //CANCELLING TIMEOUT PROCESSING
                    sipCall.exceptionT8();
                }
            }
        }
        else if(sipCall.callMode==SIPStack.SIP_CALLMODE_HOLD)
        {
            //more job
        }

        return;

    }

    //2011 12 22 처리중
    public void UPDATEParser(String message,int msgType,String remoteIp,int remotePort)
    {
        if(
                sipCall==null ||
                        sipCall.flag==false ||
                        (
                                sipCall.callMode != SIPStack.SIP_CALLMODE_BASIC &&
                                        sipCall.callMode != SIPStack.SIP_CALLMODE_HOLD
                        )
        )
        {
            //2012 07 31
            if(msgType==SIPStack.SIP_MSGTYPE_RESPONSE)
            {
                SIPRequestLine requestLine=new SIPRequestLine(message);
                if(requestLine!=null && requestLine.flag==true && requestLine.code>=300) sendFinalAck(message,remoteIp,remotePort);
            }
            //
            return;
        }

        if(msgType==SIPStack.SIP_MSGTYPE_REQUEST)
        {
            if(sipCall != null)
            {
                if(sipCall.flag==true)
                {//busy
                    //validate call
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                    boolean bValidCall=false;
                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        if(
                                sipHeader.headerValue!=null && //2012 03 23
                                        sipHeader.headerValue.length()>0 && //2012 03 23
                                        sipCall.callId!=null && //2012 03 23
                                        sipHeader.headerValue.compareTo(sipCall.callId)==0)
                        {
                            bValidCall=true;
                        }
                        else System.out.println("Invalid call");
                    }
                    //
                    if(bValidCall==true) {
                        //EXIST CALL
                        //IF HOLDMODE
                        if(sipCall.callMode==SIPStack.SIP_CALLMODE_HOLD)
                        {
                            if(sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEOFFERRED)
                            {
                                //ACCEPT
                            }
                            return;
                        }
                        //IF CONNECTED
                        else if(
                                sipCall.callMode==SIPStack.SIP_CALLMODE_BASIC  &&
                                        sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED
                        )
                        {
                            //System.out.println("UPDATE MODE SET");
                            //SET CALLMODE AS UPDATE
                            sipCall.callMode=SIPStack.SIP_CALLMODE_HOLD;
                            sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEOFFERRED;
                            //
                            sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_IN;
                            //parse message + construct local and remote sdp +negotiate codec
                            if(true==sipCall.constructUPDATEHeaders(message,remoteIp,remotePort))
                            {
                                //response trying
                                sendProceeding(remoteIp,remotePort);
                                //
                                if(false==sendUpdateAccept())
                                {
                                    rejectCall(message,480,remoteIp,remotePort);
                                    return;
                                }
                                //
                            }
                            else {
                                rejectCall(message,480,remoteIp,remotePort);
                                return;
                            }

                            //ACCEPT
                            //
                            return;
                        }
                        else {
                            //DISCARD
                            return;
                            //
                        }
                    }
                    else {
                        //invalid update call
                        rejectCall(message,400,remoteIp,remotePort);
                        return;
                        //
                    }

                }
                else { //invalid update call
                    rejectCall(message,400,remoteIp,remotePort);
                    return;
                }
            }
            else {//response 400
                //no sip call handle
                //response only
                rejectCall(message,400,remoteIp,remotePort);
                return;
            }

        }//if
        else if(msgType==SIPStack.SIP_MSGTYPE_RESPONSE  && sipCall.flag==true && sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_OUT)
        {
            if(sipCall.callMode!=SIPStack.SIP_CALLMODE_HOLD) return;
            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine!=null)
            {
                //validate call
                SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                boolean bValidCall=false;
                if(sipHeader != null && sipHeader.flag==true)
                {
                    if(
                            sipHeader.headerValue!=null && //2012 03 23
                                    sipHeader.headerValue.length()>0 && //2012 03 23
                                    sipCall.callId!=null && //2012 03 23
                                    sipHeader.headerValue.compareTo(sipCall.callId)==0)
                    {
                        //2012 1 13
                        SIPHeader seqHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
                        if(seqHeader != null && seqHeader.callSequenceNumber()==sipCall.CSEQ_NUMBER)
                        //
                        {
                            bValidCall=true;
                        }
                        else if(requestLine.code<200) return;
                    }
                    else System.out.println("Invalid call");
                }
                //
                if(bValidCall==true) {
                    sipHeader=null;
                    sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
                    if(sipHeader!=null)
                    {
                        sipCall.toH=sipHeader.header;
                        sipCall.toTag=sipHeader.getTag();
                    }
                }
                //
                sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                if(bValidCall==true)
                {
                    sipCall.remoteIp=remoteIp;
                    sipCall.remotePort=remotePort;
                    if(
                            requestLine.code>=200 &&
                                    sipHeader != null &&
                                    sipHeader.flag==true
                    )
                    {
                        sipCall.CSEQ_NUMBER=sipHeader.callSequenceNumber();
                    }
                    if(requestLine.code==100)//trying
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING;
                        sipCall.callTime_T1          = new Date();
                    }
                    else if(requestLine.code>=180 && requestLine.code<=183)//progressing
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING;
                        sipCall.callTime_T2          = new Date();
                        if(true==sipCall.parseRemoteSdp(message))
                        {
                            if(sipCall.sdp!=null && //2012 03 22
                                    sipCall.sdp.flag==true) {
                                sipCall.negotiateAudioCodec();
                            }

                            if(
                                    sipCall.remoteSdp!=null &&
                                            sipCall.remoteSdp.flag==true &&
                                            audioRTPManager!=null )
                            {
                                if(sipCall.remoteSdp.audioM.flag==true)
                                {
                                    //
                                    if(audioRTPManager != null)
                                    {
                                        audioRTPManager.setRemoteMediaInfo(
                                                sipCall.remoteSdp.audioM.commonCodec,
                                                sipCall.remoteSdp.audioM.mediaIp,
                                                sipCall.remoteSdp.audioM.mediaPort);
                                        if(
                                                sipCall.sdp!=null && //2012 03 22
                                                        sipCall.sdp.audioM!=null && //2012 03 22
                                                        audioRTPManager.bActive==false)
                                        {
                                            audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(
                            requestLine.code==200 &&
                                    sipCall.CSEQ_NUMBER==sipHeader.callSequenceNumber() &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING
                                    )
                    )//connected
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEREMOTEACCEPTED;
                        sipCall.callTime_T3          = new Date();
                        boolean ret=sendAck(message);

                        if(ret==true)
                        {
                            sipCall.callState=SIPStack.SIP_CALLSTATE_CONNECTED;
                            sipCall.callMode=SIPStack.SIP_CALLMODE_BASIC;
                            notifyCallConnected();//2012 08 14
                        }
                        //remote body analysis
                        //MUST BE CHANGE
                        if(true==sipCall.parseRemoteSdp(message))
                        {
                            if(sipCall.sdp!=null && //2012 03 22
                                    sipCall.sdp.flag==true) {
                                sipCall.negotiateAudioCodec();
                            }
                            if(
                                    sipCall.remoteSdp!= null &&
                                            sipCall.remoteSdp.flag==true &&
                                            audioRTPManager!=null )
                            {
                                if(
                                        sipCall.remoteSdp.audioM!=null && //20112 03 22
                                                sipCall.remoteSdp.audioM.flag==true)
                                {
                                    //
                                    if(audioRTPManager != null)
                                    {
                                        audioRTPManager.setRemoteMediaInfo(
                                                sipCall.remoteSdp.audioM.commonCodec,
                                                sipCall.remoteSdp.audioM.mediaIp,
                                                sipCall.remoteSdp.audioM.mediaPort);
                                        if(
                                                sipCall.sdp!=null && //2012 03 22
                                                        sipCall.sdp.audioM!= null && //2012 03 22
                                                        audioRTPManager.bActive==false)
                                        {
                                            audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                            audioRTPManager.sipSound.resetBufferInfo();//2012 04 30

                                        }
                                    }
                                }
                            }
                        }
                        //
                    }
                    else if(
                            requestLine.code==401 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING
                                    )
                    )//WWW-Athenticate
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEUNAUTHORIZED;
                        sendFinalAck(message);
                        if(reactionForWWWAUTHENTICATE(message)==false) {
                            //2012 09 07
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 7");
                                audioRTPManager.RTPEnd();
                            }
                            //
                            if(sipCall.bDestroyActivity==true)
                            {
                                SIPStack.bShutdownApplication=true;
                                sipCall.bDestroyActivity=false;
                            }
                            sipCall.resetCall();
                        }
                    }
                    else if(
                            requestLine.code==407 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING
                                    )
                    )//Proxy-Athenticate
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEUNAUTHORIZED;
                        sendFinalAck(message);
                        if(reactionForPROXYAUTHENTICATE(message)==false) {
                            //2012 09 07
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 8");
                                audioRTPManager.RTPEnd();
                            }
                            //
                            if(sipCall.bDestroyActivity==true)
                            {
                                SIPStack.bShutdownApplication=true;
                                sipCall.bDestroyActivity=false;
                            }
                            sipCall.resetCall();
                        }
                    }
                    //FORWARD - MUST BE PROCESSED MORE
                    else if(
                            requestLine.code==302 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING
                                    )
                    )//terminating
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                        sendFinalAck(message);
                        //2012 09 07
                        if(audioRTPManager!=null) {
                            //Log.i("RTPEND","call 9");
                            audioRTPManager.RTPEnd();
                        }
                        //
                        if(sipCall.bDestroyActivity==true)
                        {
                            SIPStack.bShutdownApplication=true;
                            sipCall.bDestroyActivity=false;
                        }
                        sipCall.resetCall();
                    }
                    //
                    else if(
                            requestLine.code>=300 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEPROGRESSING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEUNAUTHORIZED
                                    )
                    )//terminating
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                        sendFinalAck(message);
                        //2012 09 07
                        if(audioRTPManager!=null) {
                            //Log.i("RTPEND","call 10");
                            audioRTPManager.RTPEnd();
                        }
                        //
                        if(sipCall.bDestroyActivity==true)
                        {
                            SIPStack.bShutdownApplication=true;
                            sipCall.bDestroyActivity=false;
                        }
                        sipCall.resetCall();
                    }
                }
                //2012 1 13
                else {
                    sendFinalAck(message,remoteIp,remotePort);
                    //
                }

            }//if
        }//els if
        return;
    }
    //
    public void INVITEParser(String message,int msgType,String remoteIp,int remotePort)
    {
        if(msgType==SIPStack.SIP_MSGTYPE_REQUEST)
        {
            if(sipCall != null)
            {
                if(sipCall.flag==true)
                {//busy
                    //validate call
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                    boolean bValidCall=false;
                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        if(
                                sipHeader.headerValue!=null &&
                                        sipHeader.headerValue.length()>0 &&
                                        sipCall.callId!=null &&
                                        sipHeader.headerValue.compareTo(sipCall.callId)==0)
                        {
                            bValidCall=true;
                        }
                        else System.out.println("Invalid call");
                    }
                    //
                    if(bValidCall==true) {
                        //EXIST CALL
                        //IF HOLDEMODE
                        if(sipCall.callMode==SIPStack.SIP_CALLMODE_HOLD)
                        {
                            UPDATEParser(message,msgType,remoteIp,remotePort);
                            return;
                        }
                        //IF CONNECTED
                        else if(sipCall.callMode==SIPStack.SIP_CALLMODE_BASIC &&
                                sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED)
                        {
                            //UPDATE PROCESSING
                            UPDATEParser(message,msgType,remoteIp,remotePort);
                            return;
                        }
                        else {
                            //DISCARD
                            return;
                        }
                    }
                    else {
                        //BUSY
                        rejectCall(message,486,remoteIp,remotePort);
                        //
                    }
                }
                else { //processing inviting call
                    String fromId=null;
                    String toId=null;
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        toId=sipHeader.getId().trim();
                        if(toId!= null && toId.length()>0)
                        {
                            sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
                            if(sipHeader != null && sipHeader.flag==true)
                            {
                                fromId=sipHeader.getId().trim();
                            }
                        }//
                        if(
                                id!=null && //2012 03 23
                                        toId.compareTo(id)==0
                        )
                        {
                            //activate call handler
                            boolean ret=false;
                            if(remoteIp.compareTo(serverIp)==0)//2012 05 02
                            {
                                ret=sipCall.activeCallHandle(id,
                                        cid,authid
                                        ,authpassword,fromId,remoteIp,remotePort
                                        ,serverDomain,localIp
                                        ,localPort);
                            }
                            else {
                                ret=sipCall.activeCallHandle(id,cid,authid
                                        ,authpassword,fromId,remoteIp,remotePort
                                        ,remoteIp,localIp,localPort);
                            }
                            if(ret==true)
                            {
                                sipCall.callState=SIPStack.SIP_CALLSTATE_OFFERRED;
                                sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_IN;

                                //parse message + construct local and remote sdp +negotiate codec
                                if(true==sipCall.constructHeaders(message,remoteIp,remotePort))
                                {
                                    //response trying
                                    sendProceeding(remoteIp,remotePort);
                                    //Phone status set
                                    updateStatus("New call arrived.",false);
                                    notifyIncomingCall("New call arrived.",sipCall.dnis);
                                    if(sipCall.remoteSdp.flag==true && sipCall.remoteSdp.videoM.flag==true)
                                    {
                                        Log.i("SDP","++++ Video Sdp exist. port:"+sipCall.remoteSdp.videoM.mediaPort+"ip:"+
                                                sipCall.remoteSdp.videoM.mediaIp);
                                    }
                                    else {
                                        Log.i("SDP","++++ Video Sdp not exist.");
                                    }
                                    //

                                }
                                else {
                                    rejectCall(message,480,remoteIp,remotePort);
                                    //Phone status set
                                    updateStatus("Call rejected.",false);
                                    //

                                    return;
                                }

                            }
                            else {
                                //response temporary unavailable
                                rejectCall(message,480,remoteIp,remotePort);
                                return;
                            }
                            sendProgressing(remoteIp,remotePort);
                            //
                        }
                        //2015 03 30
                        else {
                            Log.i("CALL","++++ CALL ID IS dismatch");
                        }
                    }
                }
            }
            else {//response 480
                //no sip call handle
                //response only
                rejectCall(message,480,remoteIp,remotePort);
            }

        }//if
        else if(msgType==SIPStack.SIP_MSGTYPE_RESPONSE)
        {
            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine!=null && sipCall!=null && sipCall.flag==true && sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_OUT)
            {
                //validate call
                SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                boolean bValidCall=false;
                if(sipHeader != null && sipHeader.flag==true)
                {
                    if(
                            sipHeader.headerValue!=null && //2012 03 23
                                    sipHeader.headerValue.length()>0 && //2012 03 23
                                    sipCall.callId!=null && //2012 03 23
                                    sipHeader.headerValue.compareTo(sipCall.callId)==0)
                    {
                        //2012 1 13
                        SIPHeader seqHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
                        if(seqHeader != null && seqHeader.callSequenceNumber()==sipCall.CSEQ_NUMBER)
                        //
                        {
                            bValidCall=true;
                        }
                        else if(requestLine.code<200) return;
                    }
                    //2012 02 13
                    else System.out.println("Invalid call");
                }
                else {//2012 04 30
                    return;
                }
                //
                if(bValidCall==true) {
                    sipHeader=null;
                    sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
                    if(sipHeader!=null)
                    {
                        sipCall.toH=sipHeader.header;
                        sipCall.toTag=sipHeader.getTag();
                    }
                }
                //2012 07 31
                else {
                    if(requestLine!=null && requestLine.flag==true && requestLine.code>=300) sendFinalAck(message,remoteIp,remotePort);
                }
                //
                sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                if(bValidCall==true)
                {
                    //2011 12 28
                    if(sipCall.callMode==SIPStack.SIP_CALLMODE_HOLD)
                    {
                        UPDATEParser(message,msgType,remoteIp,remotePort);
                        return;
                    }
                    //2014 12 02
                    sipCall.finalCode=requestLine.code;
                    //
                    sipCall.remoteIp=remoteIp;
                    sipCall.remotePort=remotePort;
                    if(
                            requestLine.code>=200 &&
                                    sipHeader != null &&
                                    sipHeader.flag==true
                    )
                    {
                        sipCall.CSEQ_NUMBER=sipHeader.callSequenceNumber();
                    }
                    if(requestLine.code==100)//trying
                    {
                        sipCall.callState=SIPStack.SIP_CALLSTATE_PROCEEDING;
                        sipCall.callTime_T1          = new Date();
                        //Phone status set
                        updateStatus("Call proceeding.",false);
                        //

                    }
                    else if(requestLine.code>=180 && requestLine.code<=183)//progressing
                    {
                        //Phone status set
                        updateStatus("Call progressing.",false);
                        //

                        sipCall.callState=SIPStack.SIP_CALLSTATE_PROGRESSING;
                        sipCall.callTime_T2          = new Date();
                        if(true==sipCall.parseRemoteSdp(message))
                        {
                            if(sipCall.sdp!=null && //2012 03 22
                                    sipCall.sdp.flag==true) {
                                sipCall.negotiateAudioCodec();
                            }

                            if(
                                    sipCall.remoteSdp!=null && //2012 03 22
                                            sipCall.remoteSdp.flag==true &&
                                            audioRTPManager!=null )
                            {
                                if(
                                        sipCall.remoteSdp.audioM!=null &&
                                                sipCall.remoteSdp.audioM.flag==true)
                                {
                                    //
                                    if(audioRTPManager != null)
                                    {
                                        audioRTPManager.setRemoteMediaInfo(
                                                sipCall.remoteSdp.audioM.commonCodec,
                                                sipCall.remoteSdp.audioM.mediaIp,
                                                sipCall.remoteSdp.audioM.mediaPort);
                                        if(
                                                sipCall.sdp!=null && //2012 03 22
                                                        sipCall.sdp.audioM!=null && //2012 03 22
                                                        audioRTPManager.bActive==false)
                                        {
                                            audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                        }
                                    }
                                }
                            }
                        }
                        //2013 03 11
                        else { //local ringbacktone play
                            notifyRingbackTone(true);
                        }
                        //

                    }
                    else if(
                            requestLine.code==200 &&
                                    sipCall.CSEQ_NUMBER==sipHeader.callSequenceNumber() &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING
                                    )
                    )//connected
                    {
                        updateStatus("Connected.",false);

                        sipCall.callState=SIPStack.SIP_CALLSTATE_REMOTEACCEPTED;
                        sipCall.callTime_T3          = new Date();
                        boolean ret=sendAck(message);
                        if(ret==true) {
                            sipCall.callState=SIPStack.SIP_CALLSTATE_CONNECTED;
                            notifyCallConnected();//2012 08 14
                        }
                        //remote body analysis
                        //MUST BE CHANGE
                        if(true==sipCall.parseRemoteSdp(message))
                        {
                            if(sipCall.sdp != null && sipCall.sdp.flag==true) {//2012 03 22
                                sipCall.negotiateAudioCodec();
                            }
                            if(
                                    sipCall.remoteSdp!=null && //2012 03 22
                                            sipCall.remoteSdp.flag==true &&
                                            audioRTPManager!=null )
                            {
                                if(
                                        sipCall.remoteSdp.audioM!=null && //2012 03 22
                                                sipCall.remoteSdp.audioM.flag==true
                                )
                                {
                                    //
                                    if(audioRTPManager != null)
                                    {
                                        audioRTPManager.setRemoteMediaInfo(
                                                sipCall.remoteSdp.audioM.commonCodec,
                                                sipCall.remoteSdp.audioM.mediaIp,
                                                sipCall.remoteSdp.audioM.mediaPort);
                                        if(
                                                sipCall.sdp!=null && //2012 03 22
                                                        sipCall.sdp.audioM!=null && //2012 03 22
                                                        audioRTPManager.bActive==false)
                                        {
                                            audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                            audioRTPManager.sipSound.resetBufferInfo();//2012 04 30

                                        }
                                    }
                                }
                            }
                        }
                        //
                    }
                    else if(
                            requestLine.code==401 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING
                                    )
                    )//WWW-Athenticate
                    {
                        //Phone status set
                        updateStatus("Call Unauthorized(401).",false);
                        //

                        sipCall.callState=SIPStack.SIP_CALLSTATE_UNAUTHORIZED;
                        //2013 02 12
                        sipCall.authorizationACKH="";
                        //

                        sendFinalAck(message);
                        if(reactionForWWWAUTHENTICATE(message)==false) {
                            //2012 09 07
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 12");
                                audioRTPManager.RTPEnd();
                            }
                            //

                            try
                            {
                                if(sipCall.bDestroyActivity==true)
                                {
                                    SIPStack.bShutdownApplication=true;
                                    sipCall.bDestroyActivity=false;
                                    System.out.println(">>>>>>>>>> shutdown set");
                                }
                                if(sipCall.bDestroyActivity==true)
                                {
                                    SIPStack.bShutdownApplication=true;
                                    sipCall.bDestroyActivity=false;
                                }
                                notifyCallEnd("Call Unauthorized(401).",false
                                        ,sipCall.finalCode
                                        ,sipCall.callDirection
                                        ,false
                                        ,sipCall.callDuration
                                );
                                sipCall.resetCall();

                            }catch(Exception e){}
                        }
                    }
                    else if(
                            requestLine.code==407 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING
                                    )
                    )//Proxy-Athenticate
                    {
                        //Phone status set
                        updateStatus("Call Unauthorized(407).",false);
                        //

                        sipCall.callState=SIPStack.SIP_CALLSTATE_UNAUTHORIZED;
                        //2013 02 12
                        sipCall.authorizationACKH="";
                        //
                        sendFinalAck(message);
                        if(reactionForPROXYAUTHENTICATE(message)==false) {
                            //2012 09 07
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 13");
                                audioRTPManager.RTPEnd();
                            }
                            //
                            try
                            {
                                if(sipCall.bDestroyActivity==true)
                                {
                                    SIPStack.bShutdownApplication=true;
                                    sipCall.bDestroyActivity=false;
                                }
                                //2014 12 02
                                notifyCallEnd("Call Unauthorized(407).",false
                                        ,sipCall.finalCode
                                        ,sipCall.callDirection
                                        ,false
                                        ,sipCall.callDuration
                                );
                                //
                                sipCall.resetCall();


                            }catch(Exception e){}
                        }
                    }
                    //FORWARD - MUST BE PROCESSED MORE
                    //2012 02 10
                    else if(
                            requestLine.code==302 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING
                                    )
                    )//terminating
                    {
                        updateStatus("Call forwarded.",true);
	                  /*
	                  sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
	                  sendFinalAck(message);
	                  sipCall.resetCall();
	                   */
                        sipCall.callState=SIPStack.SIP_CALLSTATE_REDIRECTED;
                        sendRedirectFinalAck(message);

                        if(false==sendRedirectCall(message)) {
                            //2012 09 07
                            if(audioRTPManager!=null) {
                                //Log.i("RTPEND","call 14");
                                audioRTPManager.RTPEnd();
                            }
                            //
                            try
                            {
                                if(sipCall.bDestroyActivity==true)
                                {
                                    SIPStack.bShutdownApplication=true;
                                    sipCall.bDestroyActivity=false;
                                }
                                //
                                notifyCallEnd("Call forwarded.",false
                                        ,sipCall.finalCode
                                        ,sipCall.callDirection
                                        ,false
                                        ,sipCall.callDuration
                                );
                                sipCall.resetCall();
                                //

                            }catch(Exception e){}
                        }
                    }
                    //////////////////////////////////////////////////////////////////////////////////////

                    //
                    else if(
                            requestLine.code>=300 &&
                                    (
                                            sipCall.callState==SIPStack.SIP_CALLSTATE_INVITING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROCEEDING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_PROGRESSING ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_UNAUTHORIZED ||
                                                    sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLING
                                    )
                    )//terminating
                    {
                        //Phone status set
                        updateStatus("Call rejected by remoter.",true);
                        //

                        sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                        sendFinalAck(message);
                        //2012 09 07
                        if(audioRTPManager!=null) {
                            //Log.i("RTPEND","call 15");
                            audioRTPManager.RTPEnd();
                        }
                        //
                        try
                        {
                            if(sipCall.bDestroyActivity==true)
                            {
                                SIPStack.bShutdownApplication=true;
                                sipCall.bDestroyActivity=false;
                            }
                            //
                            notifyCallEnd("Call rejected by remoter.",false
                                    ,sipCall.finalCode
                                    ,sipCall.callDirection
                                    ,false
                                    ,sipCall.callDuration
                            );
                            sipCall.resetCall();
                            //
                        }catch(Exception e){}
                    }
                }
                //2012 1 13
                else {
                    sendFinalAck(message,remoteIp,remotePort);
                    //
                }
            }//if
            //2012 07 31
            else if(requestLine!=null && requestLine.code>=300)
            {
                sendFinalAck(message,remoteIp,remotePort);
            }
            //

        }//els if
        //2020 0219
        else {
            Toast.makeText(act, "현재 서버상태가 원활하지않습니다. 잠시후에 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return;
    }
    public void ACKParser(String message,int msgType,String remoteIp,int remotePort)
    {
        if(msgType==SIPStack.SIP_MSGTYPE_REQUEST &&
                sipCall.flag==true &&
                sipCall.callMode==SIPStack.SIP_CALLMODE_HOLD
        )
        {
            if(
                    sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEOFFERRED ||
                            sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEACCEPTED
            )
            {
                SIPRequestLine requestLine=new SIPRequestLine(message);
                if(requestLine!=null)
                {
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                    boolean bValidCall=false;
                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        if(
                                sipHeader.headerValue!=null && //2012 03 23
                                        sipHeader.headerValue.length()>0 && //2012 03 23
                                        sipCall.callId!=null && //2012 03 23
                                        sipHeader.headerValue.compareTo(sipCall.callId)==0)
                        {
                            bValidCall=true;
                        }
                        else System.out.println("Invalid call");
                    }
                    //
                    if(bValidCall==true) {
                        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                        if(sipHeader != null && sipHeader.flag==true)
                        {
                            sipCall.ACK_CSEQ=sipHeader.callSequenceNumber();
                            sipCall.callMode=SIPStack.SIP_CALLMODE_BASIC;
                            sipCall.callState=SIPStack.SIP_CALLSTATE_CONNECTED;
                            sipCall.resetUpdateheaders();
                            try
                            {
                                audioRTPManager.sipSound.resetBufferInfo();//2012 04 30
                            }catch(Exception e){}
                            notifyCallConnected();//2012 08 14

                            //System.out.println("!!! UPDATE CALL COMPLETED.");
                        }
                    }
                }
            }
        }
        else if(msgType==SIPStack.SIP_MSGTYPE_REQUEST && sipCall.flag==true &&
                (
                        sipCall.callState==SIPStack.SIP_CALLSTATE_ACCEPTED
                )
        )
        {

            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine!=null)
            {
                SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                boolean bValidCall=false;
                if(sipHeader != null && sipHeader.flag==true)
                {
                    if(
                            sipHeader.headerValue!=null && //2012 03 23
                                    sipHeader.headerValue.length()>0 && //2012 03 23
                                    sipCall.callId!=null && //2012 03 23
                                    sipHeader.headerValue.compareTo(sipCall.callId)==0)
                    {
                        bValidCall=true;
                    }
                    else System.out.println("Invalid call");
                }
                //
                if(bValidCall==true) {
                    sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        sipCall.ACK_CSEQ=sipHeader.callSequenceNumber();
                        sipCall.callState=SIPStack.SIP_CALLSTATE_CONNECTED;
                        //System.out.println("!!! CALL CONNECTED.");
                        updateStatus("Connected.",false);
                        audioRTPManager.sipSound.resetBufferInfo();//2012 04 30
                        notifyCallConnected();//2012 08 14
                    }
                }
            }
        }
        else if(msgType==SIPStack.SIP_MSGTYPE_REQUEST && sipCall.flag==true &&
                (
                        sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLED ||
                                sipCall.callState==SIPStack.SIP_CALLSTATE_TERMINATING
                )
        )
        {
            Log.i("HOMEVIEW","TEST2 3333");
            SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
            boolean bValidCall=false;
            if(sipHeader != null && sipHeader.flag==true)
            {
                if(
                        sipHeader.headerValue!=null && //2012 03 23
                                sipHeader.headerValue.length()>0 && //2012 03 23
                                sipCall.callId!=null && //2012 03 23
                                sipHeader.headerValue.compareTo(sipCall.callId)==0)
                {
                    bValidCall=true;
                }
                else System.out.println("Invalid call");
            }
            //
            if(bValidCall==true) {
                //2012 09 07
                if(audioRTPManager!=null) {
                    //Log.i("RTPEND","call 16");
                    audioRTPManager.RTPEnd();
                }
                //
                try
                {
                    if(sipCall.bDestroyActivity==true)
                    {
                        SIPStack.bShutdownApplication=true;
                        sipCall.bDestroyActivity=false;
                    }
                    //
                    notifyCallEnd("",false
                            ,sipCall.finalCode
                            ,sipCall.callDirection
                            ,false
                            ,sipCall.callDuration
                    );
                    sipCall.resetCall();
                    //

                }catch(Exception e){}
            }
        }
        //
        return;
    }
    public void CANCELParser(String message,int msgType,String remoteIp,int remotePort)
    {
        if(
                msgType             == SIPStack.SIP_MSGTYPE_REQUEST    &&
                        sipCall.flag      == true                      &&
                        sipCall.callState  == SIPStack.SIP_CALLSTATE_OFFERRED
        )
        {

            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine!=null)
            {
                //validate call
                SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                boolean bValidCall=false;
                if(sipHeader != null && sipHeader.flag==true)
                {
                    if(
                            sipHeader.headerValue!=null && //2012 03 23
                                    sipHeader.headerValue.length()>0 && //2012 03 23
                                    sipCall.callId!=null && //2012 03 23
                                    sipHeader.headerValue.compareTo(sipCall.callId)==0)
                    {
                        bValidCall=true;
                    }
                    else System.out.println("Invalid call");
                }
                //
                if(bValidCall==true) {
                    //Phone status set
                    updateStatus("Call cancelled.",true);
                    //2014 12 01
                    notifyCallCancel();
                    //

                    sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        sipCall.CANCEL_CSEQ=sipHeader.callSequenceNumber();
                        acceptCancelRequest(message,remoteIp,remotePort);
                        sipCall.callState=SIPStack.SIP_CALLSTATE_CANCELLED;
                        //2012 09 07
                        if(
                                sipCall.remoteSdp!=null && //2012 03 22
                                        sipCall.remoteSdp.flag==true &&
                                        audioRTPManager!=null )
                        {
                            if(
                                    sipCall.remoteSdp.audioM!=null && //2012 03 22
                                            sipCall.remoteSdp.audioM.flag==true)
                            {
                                //
                                if(
                                        sipCall.sdp!=null && //2012 03 22
                                                sipCall.sdp.audioM!=null && //2012 03 22
                                                audioRTPManager != null)
                                {
                                    audioRTPManager.setRemoteMediaInfo(
                                            sipCall.remoteSdp.audioM.commonCodec,
                                            sipCall.remoteSdp.audioM.mediaIp,
                                            sipCall.remoteSdp.audioM.mediaPort);
                                    audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                }
                            }
                        }
                        //

                        rejectCall(487);
                    }

                }
            }
            //
        }
        else if(
                msgType==SIPStack.SIP_MSGTYPE_RESPONSE && sipCall.flag==true
                        && sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLING
        )
        {
            SIPRequestLine requestLine=new SIPRequestLine(message);
            //if(requestLine!=null) requestLine.print();
            SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
            boolean bValidCall=false;
            if(sipHeader != null && sipHeader.flag==true)
            {
                if(
                        sipHeader.headerValue!=null && //2012 03 23
                                sipHeader.headerValue.length()>0 && //2012 03 23
                                sipCall.callId!=null && //2012 03 23
                                sipHeader.headerValue.compareTo(sipCall.callId)==0)
                {
                    bValidCall=true;
                }
                else System.out.println("Invalid call");
            }
            //
            if(bValidCall==true) {
                if(requestLine.code==401
                )//WWW-Athenticate
                {
                    //make Authorization
                    //sendCancelWithAuthorization();
                }
                else if(requestLine.code==407
                )//Proxy-Athenticate
                {
                    //make Proxy-Authorization
                    //sendCancelWithAuthorization();
                }
            }
        }

        return;
    }
    public void BYEParser(String message,int msgType,String remoteIp,int remotePort)
    {

        if( msgType == SIPStack.SIP_MSGTYPE_REQUEST && sipCall.flag    == true &&
                ( sipCall.callState==SIPStack.SIP_CALLSTATE_ACCEPTED || sipCall.callState==SIPStack.SIP_CALLSTATE_REMOTEACCEPTED
                        ||sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED))
        {
            SIPRequestLine requestLine=new SIPRequestLine(message);
            if(requestLine!=null)
            {
                //validate call
                SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                boolean bValidCall=false;
                if(sipHeader != null && sipHeader.flag==true)
                {
                    if(
                            sipHeader.headerValue!=null && //2012 03 23
                                    sipHeader.headerValue.length()>0 && //2012 03 23
                                    sipCall.callId!=null && //2012 03 23
                                    sipHeader.headerValue.compareTo(sipCall.callId)==0)
                    {
                        bValidCall=true;
                    }
                    else System.out.println("Invalid call");
                }
                //
                if(bValidCall==true) {


                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 17");
                        audioRTPManager.RTPEnd();//2011 12 19
                    }
                    sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        //2012 06 08
                        int duration=0;
                        Date currentTime=new Date();
                        if(sipCall.callTime_T4!=null) duration=(int)(currentTime.getTime()-sipCall.callTime_T4.getTime())/1000;

                        sipCall.BYE_CSEQ=sipHeader.callSequenceNumber();
                        sendByeResponse(message,remoteIp,remotePort);

                        //sipCall.resetCall();
                        sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                        sipCall.callTime_T6=new Date();
                    }
                    try
                    {
                        if(sipCall.bDestroyActivity==true)
                        {
                            SIPStack.bShutdownApplication=true;
                            sipCall.bDestroyActivity=false;
                        }
                        //Phone status set
                        updateStatus("Remote Party require call terminated.",false);
                        notifyCallEnd("Remote Party require call terminated.",false
                                ,sipCall.finalCode
                                ,sipCall.callDirection
                                ,true
                                ,sipCall.callDuration
                        );
                        sipCall.resetCall();//2015 06 15

                    }catch(Exception e){}

                }
            }
            //
        }
        else if(
                msgType             == SIPStack.SIP_MSGTYPE_RESPONSE &&
                        sipCall.flag      == true &&
                        sipCall.callState  == SIPStack.SIP_CALLSTATE_DISCONNECTING
        )
        {
            SIPRequestLine requestLine=new SIPRequestLine(message);
            //if(requestLine!=null) requestLine.print();
            SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
            boolean bValidCall=false;
            if(sipHeader != null && sipHeader.flag==true)
            {
                if(
                        sipHeader.headerValue!=null && //2012 03 23
                                sipHeader.headerValue.length()>0 && //2012 03 23
                                sipCall.callId!=null && //2012 03 23
                                sipHeader.headerValue.compareTo(sipCall.callId)==0)
                {
                    bValidCall=true;
                }
                //2012 02 13
                else System.out.println("Invalid call");
            }
            //
            if(bValidCall==true) {

                sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);

                if(sipHeader != null && sipHeader.flag==true)
                {
                    sipCall.BYE_CSEQ=sipHeader.callSequenceNumber();
                    //2012 09 07
                    if(audioRTPManager!=null) {
                        //Log.i("RTPEND","call 18");
                        audioRTPManager.RTPEnd();
                    }
                    //
                    if(sipCall.bDestroyActivity==true)
                    {
                        SIPStack.bShutdownApplication=true;
                        sipCall.bDestroyActivity=false;
                    }
                    notifyCallEnd("Ready.",false
                            ,sipCall.finalCode
                            ,sipCall.callDirection
                            ,true
                            ,sipCall.callDuration
                    );

                    sipCall.resetCall();
                }
                try
                {
                    if(sipCall.bDestroyActivity==true)
                    {
                        SIPStack.bShutdownApplication=true;
                        sipCall.bDestroyActivity=false;
                    }
                    //Phone status set
                    updateStatus("Ready.",true);
                    notifyCallEnd("Ready.",false
                            ,sipCall.finalCode
                            ,sipCall.callDirection
                            ,true
                            ,sipCall.callDuration
                    );
                    sipCall.resetCall();//2015 06 15

                }catch(Exception e){}

            }
        }
        onBackPressed();
        return;
    }

    public int SIPParser(String message,int msgType,int methodType, String remoteIp,int remotePort) {
        if(SIPStack.SIP_MESSAGE_DEBUG==true)
        {
            System.out.println("<<< ("+remoteIp+":"+remotePort+")\n"+message);
        }
        //INVITE MESSAGE PROCESSING   20111201
        if(methodType==SIPStack.SIP_METHODTYPE_INVITE) INVITEParser(message,msgType,remoteIp,remotePort);
            //ACK MESSAGE PROCESSING 20111202
        else if(methodType==SIPStack.SIP_METHODTYPE_ACK) ACKParser(message,msgType,remoteIp,remotePort);
            //OPTIONS MESSAGE PROCESSING
        else if(methodType==SIPStack.SIP_METHODTYPE_OPTIONS)
        {
            if(    msgType    == SIPStack.SIP_MSGTYPE_REQUEST )
            {
                sendOptionsResponse(message,remoteIp,remotePort,200);
            }
        }
        //INFO MESSAGE PROCESSING
        else if(methodType==SIPStack.SIP_METHODTYPE_INFO)
        {
            if(
                    msgType             == SIPStack.SIP_MSGTYPE_REQUEST    &&
                            sipCall.flag      == true                      &&
                            sipCall.callState  == SIPStack.SIP_CALLSTATE_CONNECTED
            )
            {

                SIPRequestLine requestLine=new SIPRequestLine(message);
                if(requestLine!=null)
                {
                    //validate call
                    SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
                    boolean bValidCall=false;
                    if(sipHeader != null && sipHeader.flag==true)
                    {
                        if(
                                sipHeader.headerValue!=null && //2012 03 23
                                        sipHeader.headerValue.length()>0 && //2012 03 23
                                        sipCall.callId!=null && //2012 03 23
                                        sipHeader.headerValue.compareTo(sipCall.callId)==0)
                        {
                            bValidCall=true;
                        }
                        else System.out.println("Invalid call");
                    }
                    //
                    if(bValidCall==true) {
                        sendInfoResponse(message,remoteIp,remotePort,200);
                    }
                }
            }

        }
        //CANCEL MESSAGE PROCESSING 20111203
        else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) CANCELParser(message,msgType,remoteIp,remotePort);
            //BYE MESSAGE PROCESSING 20111204
        else if(methodType==SIPStack.SIP_METHODTYPE_BYE) BYEParser(message,msgType,remoteIp,remotePort);
            //REFER
        else if(methodType==SIPStack.SIP_METHODTYPE_REFER)
        {
            //CODING NEEDED
        }
        //NOTIFY
        else if(methodType==SIPStack.SIP_METHODTYPE_NOTIFY)
        {
            //CODING NEEDED
        }
        //MESSAGE
        else if(methodType==SIPStack.SIP_METHODTYPE_MESSAGE)
        {
            if(    msgType    == SIPStack.SIP_MSGTYPE_REQUEST )
            {
                sendMessageResponse(message,remoteIp,remotePort,200);
            }
        }
        //SUBSCRIBE
        else if(methodType==SIPStack.SIP_METHODTYPE_SUBSCRIBE)
        {
            //CODING NEEDED
        }
        //PRACK
        else if(methodType==SIPStack.SIP_METHODTYPE_PRACK)
        {
            //CODING NEEDED
        }
        //
        return 0;
    }

    public String getAuthorizationHeader(
            String qop,
            String authid,
            String realmValue,
            String authpassword,
            String uri,
            String nonceValue,
            int       methodType
    )
    {
        String strHeader=null;
        String finalDigest=null;
        String step1Digest=SIPStack.BSSMD5Get(authid+":"+realmValue+":"+authpassword);
        String step2Digest=null;
        if(methodType==SIPStack.SIP_METHODTYPE_REGISTER) step2Digest=SIPStack.BSSMD5Get("REGISTER:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_INVITE) step2Digest=SIPStack.BSSMD5Get("INVITE:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) step2Digest=SIPStack.BSSMD5Get("CANCEL:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_ACK) step2Digest=SIPStack.BSSMD5Get("ACK:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_BYE) step2Digest=SIPStack.BSSMD5Get("BYE:"+uri);
        registerNonceCount++;
        if(qop.length()>0)
        {
            int nonceCount=0;
            String cnonce=SIPStack.BSSMD5Get("60cf184b29500b20"+registerNonceCount);
            if(nonceValue.length()>0) nonceCount=1;
            finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+String.format("%08x",nonceCount)+":"+cnonce+":"+qop+":"+step2Digest);
            strHeader="Authorization: Digest"+
                    " username=\""+authid+"\","+
                    " realm=\""+realmValue+"\","+
                    " nonce=\""+nonceValue+"\","+
                    " uri=\""+uri+"\","+
                    " response=\""+finalDigest+"\","+
                    " cnonce=\""+cnonce+"\","+
                    " nc="+String.format("%08x",nonceCount)+","+
                    " qop="+qop+","+
                    " algorithm=MD5";

        }
        else {
            finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+step2Digest);
            strHeader="Authorization: Digest"+
                    " username=\""+authid+"\","+
                    " realm=\""+realmValue+"\","+
                    " nonce=\""+nonceValue+"\","+
                    " uri=\""+uri+"\","+
                    " response=\""+finalDigest+"\","+
                    " algorithm=MD5";
        }


        return strHeader;
    }

    public String getProxyAuthorizationHeader(
            String qop,
            String authid,
            String realmValue,
            String authpassword,
            String uri,
            String nonceValue,
            int       methodType
    )
    {
        String strHeader=null;
        String finalDigest=null;
        String step1Digest=SIPStack.BSSMD5Get(authid+":"+realmValue+":"+authpassword);
        String step2Digest=null;
        if(methodType==SIPStack.SIP_METHODTYPE_REGISTER) step2Digest=SIPStack.BSSMD5Get("REGISTER:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_INVITE) step2Digest=SIPStack.BSSMD5Get("INVITE:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_CANCEL) step2Digest=SIPStack.BSSMD5Get("CANCEL:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_ACK) step2Digest=SIPStack.BSSMD5Get("ACK:"+uri);
        else if(methodType==SIPStack.SIP_METHODTYPE_BYE) step2Digest=SIPStack.BSSMD5Get("BYE:"+uri);
        registerNonceCount++;

        if(qop.length()>0)
        {
            int nonceCount=0;
            //String cnonce="60cf184b29500b20";
            String cnonce=SIPStack.BSSMD5Get("60cf184b29500b20"+registerNonceCount);
            if(nonceValue.length()>0) nonceCount=1;
            finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"
                    +String.format("%08x",nonceCount)+":"+cnonce+":"+qop+":"+step2Digest);
            strHeader="Proxy-Authorization: Digest"+
                    " username=\""+authid+"\","+
                    " realm=\""+realmValue+"\","+
                    " nonce=\""+nonceValue+"\","+
                    " uri=\""+uri+"\","+
                    " response=\""+finalDigest+"\","+
                    " cnonce=\""+cnonce+"\","+
                    " nc="+String.format("%08x",nonceCount)+","+
                    " qop="+qop+","+
                    " algorithm=MD5";


        }
        else {
            finalDigest=SIPStack.BSSMD5Get(step1Digest+":"+nonceValue+":"+step2Digest);
            strHeader="Proxy-Authorization: Digest"+
                    " username=\""+authid+"\","+
                    " realm=\""+realmValue+"\","+
                    " nonce=\""+nonceValue+"\","+
                    " uri=\""+uri+"\","+
                    " response=\""+finalDigest+"\","+
                    " algorithm=MD5";

        }

        return strHeader;
    }




    public boolean callActivate(String number)
    {
        if(sipCall==null) return false;


        String dial="";

        if(sipCall.flag==false)
        {
            //Phone status set
            updateStatus("Calling to "+number,false);
            //

            if(number.length()>32)
            {
                dial=number.substring(0,32);
            }
            else dial=new String(number);
            return callMake(dial);
        }
        else if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED
        )
        {
            //Phone status set
            updateStatus("Incoming call accept.",false);
            //

            return sendAccept();
        }

        return false;

    }

    public boolean callMake(String number)
    {
        callStatus = true;

        String header="";
        if(sipCall==null || sipCall.flag==true) return false;
        //2013 02 14
        if(regState!=SIPStack.SIP_REGSTATE_REGISTERED)
        {
            try
            {
                if(sipCall.bDestroyActivity==true)
                {
                    SIPStack.bShutdownApplication=true;
                    sipCall.bDestroyActivity=false;
                }
                notifyCallEnd("Not registered.",true
                        ,0
                        ,SIPStack.SIP_CALLDIRECTION_OUT
                        ,false
                        ,sipCall.callDuration
                );
                sipCall.resetCall();//2015 06 15

            }catch(Exception e){}
            return false;
        }
        //

        //sipCall.CSEQ_NUMBER=0; original
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;

        if(
                false==sipCall.activeCallHandle(
                        id,
                        cid,
                        authid,
                        authpassword,
                        number,
                        serverIp,
                        serverPort,
                        serverDomain,
                        localIp,
                        localPort
                )
        ) return false;

        //System.out.println("callMake: "+number);

        if(number.length()<=0) return false;
        //REQUEST LINE
        sipCall.commandLine="INVITE sip:"+number+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        Date today=new Date();
        sipCall.viaBranch=SIPStack.getViaBranch();//2013 02 13
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        sipCall.toH="To: \""+number+"\"<sip:"+number+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        sipCall.fromTag=SIPStack.newTag();
        sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        //CALLID HEADER
        sipCall.callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getSeconds()+".";
        sipCall.callidH="Call-ID: "+sipCall.callId;
        //CSEQ HEADER
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
        //2012 05 05 Virtual caller id
        if(sipCall.cid!=null && sipCall.cid.length()>0)
        {
            sipCall.passertedidentityH="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            header="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        }
        else {
            sipCall.passertedidentityH=null;
            header="";
        }
        //make body
        int audioport=SIPStack.getFreeAudioRtpPort();
        if(audioport>0) sipCall.constructSdp();
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
            if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
            {
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else
            {
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");

            }
            sipCall.sdp.setCodec(
                    SIPStack.SIP_MEDIATYPE_AUDIO,
                    RFC2833.payloadType,
                    "telephone-event/8000");
            sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
        }

        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }

        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_INVITING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                    sipCall.invitingTimes++;


                }
            }
            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                    sipCall.invitingTimes++;
                }
            }

            return true;
        }
    }

    public boolean sendReInvite()
    {
        String header=null;
        if(sipCall==null || sipCall.flag==false) return false;

        //sipCall.CSEQ_NUMBER++; 2012 07 27 mark
        //VIA HEADER
        //Date today=new Date();
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort
                +";branch="+sipCall.viaBranch+";rport";//2013 02 13
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_INVITING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;
        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }

        //2012 05 05 Virtual caller id
        if(sipCall.cid!=null && sipCall.cid.length()>0)
        {
            sipCall.passertedidentityH="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            header="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        }
        else {
            sipCall.passertedidentityH=null;
            header="";
        }


        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T00         = new Date();
                    sipCall.invitingTimes++;
                }
            }

            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T00         = new Date();
                    sipCall.invitingTimes++;
                }
            }

            return true;
        }
    }

    public boolean sendRepeatInvite() //2012 04 27
    {
        if(sipCall==null || sipCall.flag==false) return false;
        if(sipCall.message==null || sipCall.message.length()==0)  return false;

        if(sipCall.message.length()>0 && signalManager!=null) {
            byte[] buffer=sipCall.message.getBytes();
            if(sipCall.message.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

                sipCall.callTime_T00         = new Date();
                sipCall.invitingTimes++;
            }
        }

        return true;
    }

    //2012 02 10
    public boolean sendRedirectCall(String message)
    {
        String header=null;
        if(message==null || message.length()==0) return false;
        if(sipCall==null || sipCall.flag==false) return false;

        sipCall.initializeRedirectCall();
        //Get Redirect Contact
        SIPCONTACTHeader contactHeader=new SIPCONTACTHeader(message);
        //

        //sipCall.CSEQ_NUMBER++; original
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;
        //
        //VIA HEADER
        //REQUEST LINE
        if(contactHeader != null && contactHeader.sipuri!=null &&
                contactHeader.sipuri.length()>0)
        {
            sipCall.commandLine="INVITE "+contactHeader.sipuri+" SIP/2.0";
        }
        else {
            return false;
        }
        //VIA HEADER
        Date today=new Date();
        sipCall.viaBranch=SIPStack.getViaBranch();//2013 02 13
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"
                +localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        sipCall.toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        sipCall.fromTag=SIPStack.newTag();
        sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        //CALLID HEADER
        sipCall.callId=SIPStack.BSSMD5Get(viaH)+SIPStack.BSSMD5Get("1198602188")+today.getSeconds()+".";
        sipCall.callidH="Call-ID: "+sipCall.callId;
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
        //2012 05 05 Virtual caller id
        if(sipCall.cid!=null && sipCall.cid.length()>0)
        {
            sipCall.passertedidentityH="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            header="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        }
        else {
            sipCall.passertedidentityH=null;
            header="";
        }

        //make body
        //for debug
        int audioport=SIPStack.getFreeAudioRtpPort();
        if(audioport>0) sipCall.constructSdp();
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
            if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
            {
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
            }
            else
            {
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711A,
                        "PCMA/8000");
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        SIPStack.SIP_CODEC_G711U,
                        "PCMU/8000");

            }
            sipCall.sdp.setCodec(
                    SIPStack.SIP_MEDIATYPE_AUDIO,
                    RFC2833.payloadType,
                    "telephone-event/8000");
            sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
        }

        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }


        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_INVITING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        sipCall.message=
                sipCall.commandLine+SIPStack.SIP_LINE_END+
                        sipCall.viaH+SIPStack.SIP_LINE_END+
                        sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                        sipCall.contactH+SIPStack.SIP_LINE_END +
                        sipCall.fromH+SIPStack.SIP_LINE_END +
                        sipCall.toH+SIPStack.SIP_LINE_END+
                        sipCall.callidH+SIPStack.SIP_LINE_END+
                        sipCall.cseqH+SIPStack.SIP_LINE_END+
                        sipCall.allowH+SIPStack.SIP_LINE_END+
                        sipCall.useragentH+SIPStack.SIP_LINE_END+
                        header+
                        sipCall.contenttypeH+
                        sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                        body
        ;
        if(sipCall.message.length()>0 && signalManager!=null) {
            byte[] buffer=new byte[sipCall.message.length()];
            sipCall.message.getBytes(0,sipCall.message.length(),buffer,0);
            if(sipCall.message.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

                sipCall.callTime_T0          = new Date();
                sipCall.invitingTimes++;
            }

        }


        return true;

    }
    //
    public boolean reactionForWWWAUTHENTICATE(String message)
    {
        String header=null;
        if(sipCall==null || sipCall.flag==false) return false;

        //sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 31
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;


        //
        sipCall.authorizationINVITEH="";
        sipCall.authorizationCANCELH="";
        sipCall.authorizationBYEH="";
        sipCall.authorizationACKH="";
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE);
        //2013 02 12
        if(sipHeader==null || sipHeader.flag==false)
            sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE);
        //
        if(sipHeader!=null && sipHeader.flag==true)//2012 03 22
        {
            SIPAUTHENTICATEHeader authHeader=
                    new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_WWWAUTHENTICATE);
            //2013 02 12
            if(authHeader==null || authHeader.flag==false)
                authHeader=new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_WwwAUTHENTICATE);
            //
            if(authHeader!=null && authHeader.flag==true)//2012 03 22
            {
                if(authHeader.nonceValue.length()>0 && authHeader.realmValue.length()>0)
                {
                    String qop=authHeader.qopValue;
                    String uri="sip:"+serverDomain+":"+serverPort;
                    //INVITE AUTHORIZATION HEADER
                    sipCall.authorizationINVITEH=getAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_INVITE
                    );
                    //CANCEL AUTHORIZATION HEADER
                    sipCall.authorizationCANCELH=getAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_CANCEL
                    );
                    //BYE AUTHORIZATION HEADER
                    sipCall.authorizationBYEH=getAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_BYE
                    );
                    //ACK AUTHORIZATION HEADER
                    sipCall.authorizationACKH=getAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_ACK
                    );
                }
            }
        }

        //REQUEST LINE
        sipCall.commandLine="INVITE sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        sipCall.viaBranch=SIPStack.getViaBranch();//2013 02 13
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        sipCall.toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        sipCall.fromTag=SIPStack.newTag();
        sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        //CALLID HEADER
        // previous header use
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
        //2012 05 05 Virtual caller id
        if(sipCall.cid!=null && sipCall.cid.length()>0)
        {
            sipCall.passertedidentityH="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            header="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        }
        else {
            sipCall.passertedidentityH=null;
            header="";
        }

        //make body
        if(sipCall.sdp==null)
        {
            int audioport=SIPStack.getFreeAudioRtpPort();
            if(audioport>0) sipCall.constructSdp();
            if(sipCall.sdp != null && sipCall.sdp.flag==true)
            {
                sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
                if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                }
                else
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");

                }
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        RFC2833.payloadType,
                        "telephone-event/8000");
                sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
            }
        }
        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }


        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_INVITING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                }
            }
            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                }
            }
            return true;
        }
    }

    public boolean reactionForPROXYAUTHENTICATE(String message)
    {
        String header=null;
        if(sipCall==null || sipCall.flag==false) return false;

        //sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 31
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;
        //

        //
        sipCall.authorizationINVITEH="";
        sipCall.authorizationCANCELH="";
        sipCall.authorizationBYEH="";
        sipCall.authorizationACKH="";
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE);
        if(sipHeader!=null && sipHeader.flag==true) //2012 03 22
        {
            SIPAUTHENTICATEHeader authHeader=
                    new SIPAUTHENTICATEHeader(sipHeader.header,SIPStack.SIP_HEADERTYPE_PROXYAUTHENTICATE);
            if(authHeader.flag==true)
            {
                if(authHeader.nonceValue.length()>0 && authHeader.realmValue.length()>0)
                {
                    String qop=authHeader.qopValue;
                    String uri="sip:"+serverDomain+":"+serverPort;
                    //INVITE AUTHORIZATION HEADER
                    sipCall.authorizationINVITEH=getProxyAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_INVITE
                    );
                    //CANCEL AUTHORIZATION HEADER
                    sipCall.authorizationCANCELH=getProxyAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_CANCEL
                    );
                    //BYE AUTHORIZATION HEADER
                    sipCall.authorizationBYEH=getProxyAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_BYE
                    );
                    //ACK AUTHORIZATION HEADER
                    sipCall.authorizationACKH=getProxyAuthorizationHeader(
                            qop,authid,
                            authHeader.realmValue,
                            authpassword,uri,
                            authHeader.nonceValue,
                            SIPStack.SIP_METHODTYPE_ACK
                    );
                }

            }
        }

        //REQUEST LINE
        sipCall.commandLine="INVITE sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        //2013 02 13
        sipCall.viaBranch=SIPStack.getViaBranch();
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        sipCall.toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        sipCall.fromTag=SIPStack.newTag();
        sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        //CALLID HEADER
        // previous header use
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o";
        //2012 05 05 Virtual caller id
        if(sipCall.cid!=null && sipCall.cid.length()>0)
        {
            sipCall.passertedidentityH="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            header="P-Asserted-Identity: <sip:"+sipCall.cid+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        }
        else {
            sipCall.passertedidentityH=null;
            header="";
        }

        //make body
        if(sipCall.sdp==null)
        {
            int audioport=SIPStack.getFreeAudioRtpPort();
            if(audioport>0) sipCall.constructSdp();
            if(sipCall.sdp != null && sipCall.sdp.flag==true)
            {
                sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
                if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                }
                else
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");

                }
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        RFC2833.payloadType,
                        "telephone-event/8000");
                sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
            }
        }
        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }


        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_INVITING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                }
            }
            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            header+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                    sipCall.callTime_T00      = new Date();
                }
            }

            return true;
        }
    }

    public boolean sendProceeding(String remoteIp,int remotePort)
    {
        if(sipCall==null || sipCall.flag==false ) return false;


        String commandLine    = null;
        String contactH          = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 100 "+SIPStack.getResponseDescription(100)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        String viaString="";
        if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
        String routeString="";
        if(sipCall.recordrouteArray!=null &&
                sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        {
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_END
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    sipCall.callTime_T6          = new Date();
                    //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
        }

        return true;
    }

    public boolean sendProgressing(String remoteIp,int remotePort)
    {
        if(sipCall==null || sipCall.flag==false ) return false;


        String commandLine    = null;
        String contactH          = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 180 "+SIPStack.getResponseDescription(180)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        String viaString="";
        if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
        String routeString="";
        if(sipCall.recordrouteArray!=null &&
                sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();

        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //
        String sendmessage=
                commandLine+
                        viaString+
                        routeString+
                        sipCall.fromH+SIPStack.SIP_LINE_END+
                        sipCall.toH+SIPStack.SIP_LINE_END+
                        sipCall.callidH+SIPStack.SIP_LINE_END+
                        sipCall.cseqH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                sipCall.callTime_T6          = new Date();
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }

    public boolean acceptCancelRequest(String message,String remoteIp,int remotePort)
    {
        Log.i(TAG,"regi11 accept");
        if(sipCall==null || sipCall.flag==false ) return false;

        sipCall.BYE_CSEQ=sipCall.ACK_CSEQ;

        String commandLine    = null;
        String viaH             = null;
        String maxforwardH    = null;
        String contactH          = null;
        String toH          = null;
        String fromH         = null;
        String callidH       = null;
        String cseqH         = null;
        String expiresH          = null;
        String allowH        = null;
        String useragentH     = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 200 "+SIPStack.getResponseDescription(200)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_VIA);
        if(sipHeader != null && sipHeader.flag==true) viaH=sipHeader.header+SIPStack.SIP_LINE_END;
        else viaH="";
        //FROM HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipHeader != null && sipHeader.flag==true) fromH=sipHeader.header+SIPStack.SIP_LINE_END;
        else fromH="";
        //TO HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipHeader != null && sipHeader.flag==true) {
            toH=sipHeader.header+SIPStack.SIP_LINE_END;
            if(toH.indexOf("tag=")<0) toH=sipHeader.header+";tag="+SIPStack.newTag()+SIPStack.SIP_LINE_END;
        }
        else toH="";
        //CALLID HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipHeader != null && sipHeader.flag==true) callidH=sipHeader.header+SIPStack.SIP_LINE_END;
        else callidH="";
        //CSEQ HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipHeader != null && sipHeader.flag==true)
        {
            sipCall.CANCEL_CSEQ=sipHeader.callSequenceNumber();
            cseqH=sipHeader.header+SIPStack.SIP_LINE_END;
        }
        else cseqH="";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        String sendmessage=
                commandLine+
                        viaH+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                sipCall.callTime_T8          = new Date();
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }
    public boolean sendAccept()
    {
        Log.i(TAG,"regi11 send");
        if(sipCall==null || sipCall.flag==false ) return false;
        if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED
        )
        {
            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 200 "+SIPStack.getResponseDescription(200)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT
            String contenttypeString="";
            String bodyString="";
            if(sipCall.sdp != null && sipCall.sdp.flag==true )
            {
                String body=sipCall.sdp.getFinalBodyString();
                if(body!=null && body.length()>0)
                {
                    bodyString=body;
                    contenttypeString="Content-Type: application/SDP\r\n";
                }
            }
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: "+bodyString.length()+SIPStack.SIP_LINE_END;
            //

            sipCall.finalResponseMessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contenttypeString+
                            contentlengthH+SIPStack.SIP_LINE_END+
                            bodyString
            ;
            if(sipCall.finalResponseMessage.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.finalResponseMessage.getBytes();
                if(sipCall.finalResponseMessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sipCall.finalResponseMessage);
                    sipCall.callTime_T3          = new Date();//2012 02 03 T6->T3
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callState=SIPStack.SIP_CALLSTATE_ACCEPTED;
                    //2011 12 22
                    if(
                            sipCall.remoteSdp!=null && //2012 03 22
                                    sipCall.remoteSdp.flag==true &&
                                    audioRTPManager!=null )
                    {
                        if(
                                sipCall.remoteSdp.audioM!=null && //2012 03 22
                                        sipCall.remoteSdp.audioM.flag==true)
                        {
                            //
                            if(
                                    sipCall.sdp!=null && //2012 03 22
                                            sipCall.sdp.audioM!=null && //2012 03 22
                                            audioRTPManager != null)
                            {
                                audioRTPManager.setRemoteMediaInfo(
                                        sipCall.remoteSdp.audioM.commonCodec,
                                        sipCall.remoteSdp.audioM.mediaIp,
                                        sipCall.remoteSdp.audioM.mediaPort);
                                audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean sendUpdateAccept()
    {
        Log.i(TAG,"regi11 update");

        if(sipCall==null || sipCall.flag==false ) return false;
        if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        sipCall.callState==SIPStack.SIP_CALLSTATE_UPDATEOFFERRED
        )
        {
            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 200 "+SIPStack.getResponseDescription(200)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT
            String contenttypeString="";
            String bodyString="";
            if(sipCall.sdp != null && sipCall.sdp.flag==true )
            {
                String body=sipCall.sdp.getFinalBodyString();
                if(body!=null && body.length()>0)
                {
                    bodyString=body;
                    contenttypeString="Content-Type: application/SDP\r\n";
                }
            }

            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: "+bodyString.length()+SIPStack.SIP_LINE_END;

            //
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contenttypeString+
                            contentlengthH+SIPStack.SIP_LINE_END+
                            bodyString
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    sipCall.callTime_T6          = new Date();
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATEACCEPTED;
                    if(
                            sipCall.remoteSdp!=null && //2012 03 23
                                    sipCall.remoteSdp.flag==true &&
                                    audioRTPManager!=null )
                    {
                        if(
                                sipCall.remoteSdp.audioM!=null && //2012 03 23
                                        sipCall.remoteSdp.audioM.flag==true)
                        {
                            //
                            if(audioRTPManager != null)
                            {
                                audioRTPManager.setRemoteMediaInfo(
                                        sipCall.remoteSdp.audioM.commonCodec,
                                        sipCall.remoteSdp.audioM.mediaIp,
                                        sipCall.remoteSdp.audioM.mediaPort);
                                if(
                                        sipCall.sdp!=null && //2012 03 22
                                                sipCall.sdp.audioM!=null && //2012 03 22
                                                audioRTPManager.bActive==false)
                                {
                                    audioRTPManager.RTPInit(sipCall.sdp.audioM.mediaPort);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    public boolean rejectCall(int code)
    {
        if(sipCall==null || sipCall.flag==false ) return false;
        if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        (
                                sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED ||
                                        sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLED
                        )
        )
        {
            //Phone status set
            updateStatus("Incoming call rejected by me.",false);
            //

            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
            //CONTACT HEADER
            if(ifIp!=null && ifIp.length()>0 && ifPort>0)
                contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
            else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: "+0+SIPStack.SIP_LINE_END;

            //
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_END
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    sipCall.callTime_T6          = new Date();
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                    //Phone status set
                    updateStatus("Call reject.",true);
                    //

                }
            }
        }
        return true;
    }

    public boolean rejectCall(String message,int code,String remoteIp,int remotePort)
    {
        Log.i(TAG,"regi11 reject");
        if(message==null || message.length()==0 || remoteIp==null || remoteIp.length()==0
                || remotePort<=0) return false;

        //CONSTRUCT RESPONSE HEADERS
        String viaH                   = "";
        StringBuffer viaArray        = new StringBuffer();
        String routeH              = "";
        StringBuffer routeArray          = new StringBuffer();
        String recordrouteH             = "";
        StringBuffer recordrouteArray  = new StringBuffer();
        String contactH                = "";
        String toH                = "";
        String fromH               = "";
        String callidH             = "";
        String cseqH               = "";
        String contentlengthH        = "";

        StringTokenizer tokenArray= new StringTokenizer(message,SIPStack.SIP_LINE_END,true);

        //480 처리 중
        if(code == 480)
        {
            Toast.makeText(this, "통화 모듈 대기중입니다. 잠시 후 다시 시도해 주십시오.", Toast.LENGTH_SHORT).show();
        }

        while(tokenArray.hasMoreTokens())
        {
            String token=tokenArray.nextToken().trim();
            if(token.length()<=0) continue;
            if(token.compareTo("\r")==0) continue;
            if(token.compareTo("\n")==0) continue;
            if(token.startsWith("From: ")==true)
            {
                fromH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("f: ")==true) //2012 04 27
            {
                fromH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("To: ")==true)
            {
                if(token.indexOf("tag=")<0) {
                    toH=token+";tag="+new Date().getTime()+SIPStack.SIP_LINE_END;
                }
                else toH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("t: ")==true) //2012 04 27
            {
                if(token.indexOf("tag=")<0) {
                    toH=token+";tag="+new Date().getTime()+SIPStack.SIP_LINE_END;
                }
                else toH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("Via: ")==true)
            {
                if(viaH==null || viaH.length()==0) {//2013 02 13
                    viaH=token;
                    int iS=token.indexOf(";rport=");
                    if(iS>0 && token.indexOf(";received=")<0)
                    {
                        String s1=token.substring(0,iS);
                        int iS_=token.indexOf(";",iS+1);
                        String s2=null;
                        if(iS_>0) {
                            s2=token.substring(iS_+1);
                            if(s1!=null && s2!=null) viaH=s1+";"+s2;
                            else if(s1!=null) viaH=s1;
                        }
                        else viaH=s1;

                        if(viaH!=null && viaH.length()>0) viaH=viaH+";received="+remoteIp+";rport="+remotePort;
                    }
                    if(viaH!=null && viaH.length()>0) viaArray.append(viaH+SIPStack.SIP_LINE_END);

                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("v: ")==true) //2012 04 27
            {
                if(viaH==null || viaH.length()==0) {//2013 02 13
                    viaH=token;
                    int iS=token.indexOf(";rport=");
                    if(iS>0 && token.indexOf(";received=")<0)
                    {
                        String s1=token.substring(0,iS);
                        int iS_=token.indexOf(";",iS+1);
                        String s2=null;
                        if(iS_>0) {
                            s2=token.substring(iS_+1);
                            if(s1!=null && s2!=null) viaH=s1+";"+s2;
                            else if(s1!=null) viaH=s1;
                        }
                        else viaH=s1;

                        if(viaH!=null && viaH.length()>0) viaH=viaH+";received="+remoteIp+";rport="+remotePort;
                    }
                    if(viaH!=null && viaH.length()>0) viaArray.append(viaH+SIPStack.SIP_LINE_END);
                }
                else viaArray.append(token+SIPStack.SIP_LINE_END);
            }
            else if(token.startsWith("Record-Route: ")==true)
            {
                if(recordrouteH==null || recordrouteH.length()==0) {//2013 02 13
                    routeH=null;
                    routeArray=new StringBuffer();
                    recordrouteH=token;
                    recordrouteArray.append(recordrouteH+SIPStack.SIP_LINE_END);
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
                else {
                    recordrouteArray.append(token+SIPStack.SIP_LINE_END);
                    routeArray.append(token.substring(7)+SIPStack.SIP_LINE_END);
                }
            }
            else if(token.startsWith("Call-ID: ")==true)
            {
                callidH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("i: ")==true) //2012 04 27
            {
                callidH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("CSeq: ")==true)
            {
                cseqH=token+SIPStack.SIP_LINE_END;
            }
            else if(token.startsWith("Content-Length: ")==true)
            {
                contentlengthH="Content-Length: 0\r\n";
            }
            else if(token.startsWith("l: ")==true) //2012 04 27
            {
                contentlengthH="Content-Length: 0\r\n";
            }
        }


        //
        //if(sipCall==null || sipCall.flag==false ) return false; //2012 12 10 marked

        //REQUEST LINE
        String commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        String viaString="";
        if(viaArray.length()>0) viaString=viaArray.toString();
        String routeString="";
        if(recordrouteArray.length()>0) routeString=recordrouteArray.toString();
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: "+0+SIPStack.SIP_LINE_END;

        //
        String sendmessage=
                commandLine+
                        viaString+
                        routeString+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        contactH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }
        return true;
    }

    public boolean redirectIncomingCall(String redirectId)
    {
        if(sipCall==null || sipCall.flag==false ) return false;
        if(redirectId==null || redirectId.length()==0) return false;
        if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        (
                                sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED ||
                                        sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLED
                        )
        )
        {
            //Phone status set
            updateStatus("Call forwarding to "+redirectId,true);
            //

            String commandLine    = null;
            String contactH          = null;
            String contentlengthH  = null;

            //REQUEST LINE
            commandLine="SIP/2.0 "+302+" "+SIPStack.getResponseDescription(302)+SIPStack.SIP_LINE_END;
            //VIA HEADER GET
            String viaString="";
            if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
            String routeString="";
            if(sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();
            //CONTACT HEADER
            // 2012 02 09
            contactH="Contact: <sip:"+redirectId+"@"+serverIp+":"+serverPort+">";
	         /*
	         if(ifIp!=null && ifIp.length()>0)
	            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
	         else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
	          */
            //CONTENT-LENGTH HEADER
            contentlengthH="Content-Length: "+0+SIPStack.SIP_LINE_END;

            //
            String sendmessage=
                    commandLine+
                            viaString+
                            routeString+
                            sipCall.fromH+SIPStack.SIP_LINE_END+
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_END
                    ;
            if(sendmessage.length()>0 && signalManager!=null) {
                byte[] buffer=sendmessage.getBytes();
                if(sendmessage.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                    sipCall.callTime_T6          = new Date();
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callState=SIPStack.SIP_CALLSTATE_TERMINATING;
                }
            }
        }
        return true;
    }

    public boolean sendProgressing(String body,String remoteIp,int remotePort)
    {
        if(sipCall==null || sipCall.flag==false ) return false;
        if(body==null || body.length()<=0) return false;


        String commandLine    = null;
        String contactH          = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 183 "+SIPStack.getResponseDescription(183)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        String viaString="";
        if(sipCall.viaArray.length()>0) viaString=sipCall.viaArray.toString();
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_VIA);
        if(sipHeader != null && sipHeader.flag==true) viaH=sipHeader.header+SIPStack.SIP_LINE_END;
        else viaH="";
        String routeString="";
        if(sipCall.recordrouteArray!=null &&
                sipCall.recordrouteArray.length()>0) routeString=sipCall.recordrouteArray.toString();

        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: "+body.length()+SIPStack.SIP_LINE_END;
        String contenttypeH="";
        if(body != null && body.length()>0)
        {
            contenttypeH="Content-Type: application/SDP"+SIPStack.SIP_LINE_END;
        }

        //

        String sendmessage=
                commandLine+
                        viaString+
                        routeString+
                        sipCall.fromH+SIPStack.SIP_LINE_END+
                        sipCall.toH+SIPStack.SIP_LINE_END+
                        sipCall.callidH+SIPStack.SIP_LINE_END+
                        sipCall.cseqH+SIPStack.SIP_LINE_END+
                        contactH+SIPStack.SIP_LINE_END+
                        contenttypeH+
                        contentlengthH+
                        SIPStack.SIP_LINE_END+
                        body
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true)
                    System.out.println("SENT\n"+sendmessage);
                sipCall.callTime_T6          = new Date();
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }

    public boolean sendAck(String message)
    {
        String remoteIp=null;
        int remotePort=0;

        if(sipCall==null || sipCall.flag==false ) return false;

        sipCall.ACK_CSEQ=sipCall.CSEQ_NUMBER;

        sipCall.reverseRecordRoute(message);
        //REQUEST LINE
        if(sipCall.remoteContactUri!=null && sipCall.remoteContactUri.length()>0)
        {
            sipCall.commandLine="ACK "+sipCall.remoteContactUri+" SIP/2.0";
        }
        else sipCall.commandLine="ACK sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER(call cycle이 종료되지 않은 시점에서는 via header는 invite의 것을 그대로 유지해야 한다)
        if(sipCall.viaH==null || sipCall.viaH.length()==0) //2013 02 13
        {
            if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0) sipCall.viaBranch=SIPStack.getViaBranch();
            sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        }
        //CSEQ HEADER
        sipCall.cseqH="CSeq: "+sipCall.ACK_CSEQ+" ACK";


        //CONTENT-LENGTH HEADER
        sipCall.contentlengthH="Content-Length: 0";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //
        String routeString="";

        if(sipCall.routeArray!=null &&
                sipCall.routeArray.length()>0)
        {
            routeString=sipCall.routeArray.toString();
            if(sipCall.routeH!=null && sipCall.routeH.length()>0)
            {
                //SIPROUTEHeader(String header)
                SIPROUTEHeader header=new SIPROUTEHeader(sipCall.routeH);
                if(header!=null && header.flag==true)
                {
                    remoteIp=header.getIp();
                    remotePort=header.getPort();
                }
            }
        }


        //AUTHORIZATION HEADER
        //
        if(sipCall.authorizationACKH != null && sipCall.authorizationACKH.length() > 0)
        {

            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationACKH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T4          = new Date();
                    sipCall.callTime_T40      = new Date();
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }
            }
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T4          = new Date();
                    sipCall.callTime_T40      = new Date();
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                    }
                }
            }
        }
        return true;
    }
    //2012 07 27
    public boolean sendFinalAck(String message)
    {
        //2012 07 31
        String fromH               = null;
        String toH                = null;
        String callidH             = null;
        //
        String viaH                   = null;
        String commandLine          = null;
        String contentlengthH              = null;
        String cseqH               = null;

        String remoteIp=null;
        int remotePort=0;
        SIPHeader  sipheader         = null;

        if(sipCall==null || sipCall.flag==false ) return false;

        //2012 07 31
        //FROM HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            fromH=new String(sipheader.header);
        }
        else return false;
        //TO HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            toH=new String(sipheader.header);
        }
        else return false;
        //CALLID HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            callidH=new String(sipheader.header);
        }
        else return false;
        //

        int ACK_CSEQ=sipCall.CSEQ_NUMBER;
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipheader != null && sipheader.flag==true && sipheader.callSequenceNumber()>=0)
        {
            ACK_CSEQ=sipheader.callSequenceNumber();

        }

        sipCall.reverseRecordRoute(message);
        //REQUEST LINE
        if(sipCall.remoteContactUri!=null && sipCall.remoteContactUri.length()>0)
        {
            commandLine="ACK "+sipCall.remoteContactUri+" SIP/2.0";
        }
        else commandLine="ACK sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER(call cycle이 종료되지 않은 시점에서는 via header는 invite의 것을 그대로 유지해야 한다)
        //2013 02 13
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_VIA);
        if(sipheader != null && sipheader.flag==true)
        {
            viaH=new String(sipheader.header);
            if(viaH!=null && viaH.length()>0)
            {
                String branch="";
                int index=viaH.indexOf(";branch=");
                if(index>0) branch=viaH.substring(index+1);
                index=branch.indexOf(";");
                if(index>0) branch=branch.substring(0,index);
                index=viaH.indexOf(";received=");
                if(index>0) viaH=viaH.substring(0,index);
                index=viaH.indexOf(";rport=");
                if(index>0) viaH=viaH.substring(0,index);
                viaH=viaH+";"+branch;
            }
        }
        if(viaH==null || viaH.length()==0)
        {
            viaH="Via: SIP/2.0/UDP "+localIp
                    +":"+localPort+";branch="+SIPStack.getViaBranch()+";rport";
        }
        //CSEQ HEADER
        cseqH="CSeq: "+ACK_CSEQ+" ACK";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0";
        String routeString="";
        if(sipCall.routeArray!=null &&
                sipCall.routeArray.length()>0)
        {
            routeString=sipCall.routeArray.toString();
            if(sipCall.routeH!=null && sipCall.routeH.length()>0)
            {
                //SIPROUTEHeader(String header)
                SIPROUTEHeader header=new SIPROUTEHeader(sipCall.routeH);
                if(header!=null && header.flag==true)
                {
                    remoteIp=header.getIp();
                    remotePort=header.getPort();
                }
            }
        }

        //AUTHORIZATION HEADER
        //
        if(sipCall.authorizationACKH != null && sipCall.authorizationACKH.length() > 0)
        {
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            callidH+SIPStack.SIP_LINE_END+
                            cseqH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationACKH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }

            }
        }
        else
        {
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            callidH+SIPStack.SIP_LINE_END+
                            cseqH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else if(sipCall.remoteIp!=null && sipCall.remoteIp.length()>0 && sipCall.remotePort>0)//2012 03 22
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }

            }
        }



        return true;

    }
    //2012 07 31
    public boolean sendFinalAck(String message,String remoteIp,int remotePort)
    {
        //2012 07 31
        String fromH               = null;
        String toH                = null;
        String callidH             = null;
        //
        String viaH                   = null;
        String commandLine          = null;
        String contentlengthH              = null;
        String cseqH               = null;

        SIPHeader  sipheader         = null;


        //2012 07 31
        //FROM HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            fromH=new String(sipheader.header);
        }
        else return false;
        //TO HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            toH=new String(sipheader.header);
        }
        else return false;
        //CALLID HEADER GET
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipheader != null && sipheader.flag==true && sipheader.header!=null && sipheader.header.length()>0)
        {
            callidH=new String(sipheader.header);
        }
        else return false;
        //

        int ACK_CSEQ=0;
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipheader != null && sipheader.flag==true && sipheader.callSequenceNumber()>=0)
        {
            ACK_CSEQ=sipheader.callSequenceNumber();

        }

        //REQUEST LINE
        commandLine="ACK sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER(call cycle이 종료되지 않은 시점에서는 via header는 invite의 것을 그대로 유지해야 한다)
        //2013 02 13
        sipheader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_VIA);
        if(sipheader != null && sipheader.flag==true)
        {
            viaH=new String(sipheader.header);
            if(viaH!=null && viaH.length()>0)
            {
                String branch="";
                int index=viaH.indexOf(";branch=");
                if(index>0) branch=viaH.substring(index+1);
                index=branch.indexOf(";");
                if(index>0) branch=branch.substring(0,index);
                index=viaH.indexOf(";received=");
                if(index>0) viaH=viaH.substring(0,index);
                index=viaH.indexOf(";rport=");
                if(index>0) viaH=viaH.substring(0,index);
                viaH=viaH+";"+branch;
            }
        }
        if(viaH==null || viaH.length()==0)
        {
            viaH="Via: SIP/2.0/UDP "+localIp+":"
                    +localPort+";branch="+SIPStack.getViaBranch()+";rport";
        }
        //CSEQ HEADER
        cseqH="CSeq: "+ACK_CSEQ+" ACK";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0";

        //AUTHORIZATION HEADER
        //
        String sendmessage=
                commandLine+SIPStack.SIP_LINE_END+
                        fromH+SIPStack.SIP_LINE_END +
                        toH+SIPStack.SIP_LINE_END+
                        viaH+SIPStack.SIP_LINE_END+
                        callidH+SIPStack.SIP_LINE_END+
                        cseqH+SIPStack.SIP_LINE_END+
                        contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sendmessage);
                if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                {
                    //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                    }
                    //

                }
            }

        }



        return true;

    }
    //
    public boolean sendRedirectFinalAck(String message)
    {
        String remoteIp=null;
        int remotePort=0;

        if(sipCall==null || sipCall.flag==false ) return false;

        sipCall.ACK_CSEQ=sipCall.CSEQ_NUMBER;

        sipCall.reverseRecordRoute(message);
        //REQUEST LINE
        sipCall.commandLine="ACK sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER(call cycle이 종료되지 않은 시점에서는 via header는 invite의 것을 그대로 유지해야 한다)
        if(sipCall.viaH==null || sipCall.viaH.length()==0)//2013 02 13
        {
            if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0) sipCall.viaBranch=SIPStack.getViaBranch();
            sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"
                    +localPort+";branch="+sipCall.viaBranch+";rport";
        }
        //CSEQ HEADER
        sipCall.cseqH="CSeq: "+sipCall.ACK_CSEQ+" ACK";
        //CONTENT-LENGTH HEADER
        sipCall.contentlengthH="Content-Length: 0";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        String routeString="";
        if(sipCall.routeArray!=null &&
                sipCall.routeArray.length()>0)
        {
            routeString=sipCall.routeArray.toString();
            if(sipCall.routeH!=null && sipCall.routeH.length()>0)
            {
                //SIPROUTEHeader(String header)
                SIPROUTEHeader header=new SIPROUTEHeader(sipCall.routeH);
                if(header!=null && header.flag==true)
                {
                    remoteIp=header.getIp();
                    remotePort=header.getPort();
                }
            }
        }

        //AUTHORIZATION HEADER
        //
        if(sipCall.authorizationACKH != null && sipCall.authorizationACKH.length() > 0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationACKH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }

            }
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    if(remoteIp!=null && remoteIp.length()>0 && remotePort>0)
                    {
                        //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                    else
                    {
                        //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                        //2012 07 16
                        int repeatSendCount=0;
                        while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                        {
                            if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                            {
                                break;
                            }
                            repeatSendCount++;
                        }
                        //

                    }
                }

            }
        }



        return true;


    }

    //REJECT OR BYE OR CANCEL
    public boolean callTerminate()
    {
        if(sipCall==null || sipCall.flag==false ) return false;
        sipCall.bCancelRequest  = false;
        if(
                sipCall.callState == SIPStack.SIP_CALLSTATE_CONNECTED ||
                        sipCall.callState == SIPStack.SIP_CALLSTATE_ACCEPTED ||
                        sipCall.callState == SIPStack.SIP_CALLSTATE_REMOTEACCEPTED
        )
        {
            //Phone status set
            updateStatus("Call disconnecting.",false);
            //
            //2012 06 08
            int duration=0;
            Date currentTime=new Date();
            if(sipCall.callTime_T4!=null) duration=(int)(currentTime.getTime()-sipCall.callTime_T4.getTime())/1000;
            //

            //Log.i("BYE","Debug 4");
            if(audioRTPManager!=null) {
                //Log.i("RTPEND","call 19");
                audioRTPManager.RTPEnd();
            }
            return sendBye();

        }
        else if(
                sipCall.callState == SIPStack.SIP_CALLSTATE_INVITING ||
                        sipCall.callState == SIPStack.SIP_CALLSTATE_PROCEEDING ||
                        sipCall.callState == SIPStack.SIP_CALLSTATE_PROGRESSING
        )
        {
            //Phone status set
            updateStatus("Call cancelling.",false);
            //

            if(audioRTPManager!=null) {
                //Log.i("RTPEND","call 20");
                audioRTPManager.RTPEnd();
            }
            return sendCancel();


        }
        else if(
                sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN &&
                        (
                                sipCall.callState==SIPStack.SIP_CALLSTATE_OFFERRED ||
                                        sipCall.callState==SIPStack.SIP_CALLSTATE_CANCELLED
                        )
        )
        {
            //Phone status set
            updateStatus("Call reject.",false);
            //

            return rejectCall(480);
        }
        else if(sipCall.flag==true && sipCall.callState!=SIPStack.SIP_CALLSTATE_TERMINATING)
        {
            try
            {
                if(sipCall.bDestroyActivity==true)
                {
                    SIPStack.bShutdownApplication=true;
                    sipCall.bDestroyActivity=false;
                }
                if(audioRTPManager!=null) {
                    //Log.i("RTPEND","call 21");
                    audioRTPManager.RTPEnd();
                }

                notifyCallEnd("",false
                        ,sipCall.finalCode
                        ,sipCall.callDirection
                        ,(boolean)(sipCall.finalCode==200)
                        ,sipCall.callDuration
                );
                sipCall.resetCall();

            }catch(Exception e){}

        }
        return true;
    }

    //BYE
    public boolean sendBye()
    {
        if(sipCall==null || sipCall.flag==false ) return false;

        if(
                sipCall.callState != SIPStack.SIP_CALLSTATE_CONNECTED &&
                        sipCall.callState != SIPStack.SIP_CALLSTATE_ACCEPTED &&
                        sipCall.callState != SIPStack.SIP_CALLSTATE_REMOTEACCEPTED
        )
        {
            return false;
        }

        //REQUEST LINE
        String commandLine=null;
        if(sipCall.remoteContactUri!= null && sipCall.remoteContactUri.length()>0)
        {
            commandLine="BYE "+sipCall.remoteContactUri+" SIP/2.0";
        }
        else commandLine="BYE sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER 2013 02 13
        if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0)
        {
            sipCall.viaBranch=SIPStack.getViaBranch();
        }
        String viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch;
        //MAXFORWARDS HEADER
        String maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        String contactH=null;
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        String toH=null;
        if(sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN)
        {
            if(sipCall.fromHeaderValue==null || sipCall.fromHeaderValue.length()==0)
            {
                toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
            }
            else toH="To: "+sipCall.fromHeaderValue;
        }
        else {
            toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.toTag;
        }
        //FROM HEADER
        String fromH=null;
        if(sipCall.callDirection==SIPStack.SIP_CALLDIRECTION_IN)
        {
            if(sipCall.toHeaderValue==null || sipCall.toHeaderValue.length()==0)
            {
                fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.toTag;
            }
            else fromH="From: "+sipCall.toHeaderValue;
        }
        else
        {
            fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        }
        //CSEQ HEADER

        sipCall.BYE_CSEQ=sipCall.CSEQ_NUMBER+1;
        String cseqH="CSeq: "+sipCall.BYE_CSEQ+" BYE";
        //CONTENT-LENGTH HEADER
        String contentlengthH="Content-Length: 0";
        //ROUTE HEADER
        String routeString="";
        if(sipCall.routeArray!=null &&
                sipCall.routeArray.length()>0) routeString=sipCall.routeArray.toString();

        //AUTHORIZATION HEADER
        //
        if(sipCall.authorizationBYEH != null && sipCall.authorizationBYEH.length() > 0)
        {
            //
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            maxforwardH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END +
                            fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationBYEH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T5          = new Date();
                    sipCall.callTime_T6          = new Date();
                    sipCall.callState        = SIPStack.SIP_CALLSTATE_DISCONNECTING;
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
        }
        else
        {
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            maxforwardH+SIPStack.SIP_LINE_END+
                            contactH+SIPStack.SIP_LINE_END +
                            fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T5          = new Date();
                    sipCall.callTime_T6          = new Date();
                    sipCall.callState        = SIPStack.SIP_CALLSTATE_DISCONNECTING;
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
        }
        return true;
    }

    public boolean sendByeResponse(String message,String remoteIp,int remotePort)
    {
        if(sipCall==null || sipCall.flag==false ) return false;

        sipCall.BYE_CSEQ=sipCall.ACK_CSEQ;

        String commandLine    = null;
        String viaH             = null;
        String toH          = null;
        String fromH         = null;
        String callidH       = null;
        String cseqH         = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 200 "+SIPStack.getResponseDescription(200)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        viaH=sipCall.getViaArray(message);
        if(viaH == null || viaH.length()==0) viaH="";
        //FROM HEADER GET
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipHeader != null && sipHeader.flag==true) fromH=sipHeader.header+SIPStack.SIP_LINE_END;
        else fromH="";
        //TO HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipHeader != null && sipHeader.flag==true) {
            toH=sipHeader.header+SIPStack.SIP_LINE_END;
            if(toH.indexOf("tag=")<0) toH=sipHeader.header+";tag="+SIPStack.newTag()+SIPStack.SIP_LINE_END;
        }
        else toH="";
        //CALLID HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipHeader != null && sipHeader.flag==true) callidH=sipHeader.header+SIPStack.SIP_LINE_END;
        else callidH="";
        //CSEQ HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipHeader != null && sipHeader.flag==true)
        {
            sipCall.BYE_CSEQ=sipHeader.callSequenceNumber();
            cseqH=sipHeader.header+SIPStack.SIP_LINE_END;
        }
        else cseqH="";
        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        String sendmessage=
                commandLine+
                        viaH+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                sipCall.callTime_T6          = new Date();
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }
    //CANCEL
    public boolean sendCancel()
    {
        if(sipCall==null || sipCall.flag==false ) return false;

        sipCall.CANCEL_CSEQ=sipCall.CSEQ_NUMBER;
        if(
                sipCall.callState != SIPStack.SIP_CALLSTATE_INVITING &&
                        sipCall.callState != SIPStack.SIP_CALLSTATE_PROCEEDING &&
                        sipCall.callState != SIPStack.SIP_CALLSTATE_PROGRESSING
        )
        {
            return false;
        }

        //REQUEST LINE
        String commandLine="CANCEL sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //TO HEADER
        String toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";;
        //VIA HEADER(call cycle이 종료되지 않은 시점에서는 via header는 invite의 것을 그대로 유지해야 한다)
        if(sipCall.viaH==null || sipCall.viaH.length()==0) //2013 02 13
        {
            if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0) sipCall.viaBranch=SIPStack.getViaBranch();
            sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        }
        //CSEQ HEADER
        sipCall.cseqH="CSeq: "+sipCall.CANCEL_CSEQ+" CANCEL";
        //CONTENT-LENGTH HEADER
        sipCall.contentlengthH="Content-Length: 0";
        //ROUTE HEADER
        String routeString="";
        if(sipCall.routeArray!=null &&
                sipCall.routeArray.length()>0) routeString=sipCall.routeArray.toString();
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";

        //AUTHORIZATION HEADER
        //
        if(sipCall.authorizationCANCELH != null && sipCall.authorizationCANCELH.length() > 0)
        {
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationCANCELH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T8          = new Date();
                    sipCall.callState        = SIPStack.SIP_CALLSTATE_CANCELLING;
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
        }
        else
        {
            sipCall.message=
                    commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            routeString+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    sipCall.callTime_T8          = new Date();
                    sipCall.callState        = SIPStack.SIP_CALLSTATE_CANCELLING;
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                }
            }
        }
        return true;

    }
    //OPTIONS
    public boolean sendOptionsResponse(String message,String remoteIp,int remotePort,int code)
    {
        String commandLine    = null;
        String viaH             = null;
        String toH          = null;
        String fromH         = null;
        String callidH       = null;
        String cseqH         = null;
        String allowH        = null;
        String useragentH     = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        viaH=sipCall.getViaArray(message);
        if(viaH == null || viaH.length()==0) viaH="";
        //FROM HEADER GET
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipHeader != null && sipHeader.flag==true) fromH=sipHeader.header+SIPStack.SIP_LINE_END;
        else fromH="";
        //TO HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipHeader != null && sipHeader.flag==true) {
            toH=sipHeader.header+SIPStack.SIP_LINE_END;
            if(toH.indexOf("tag=")<0) toH=sipHeader.header+";tag="+SIPStack.newTag()+SIPStack.SIP_LINE_END;
        }
        else toH="";
        //CALLID HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipHeader != null && sipHeader.flag==true) callidH=sipHeader.header+SIPStack.SIP_LINE_END;
        else callidH="";
        //CSEQ HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipHeader != null && sipHeader.flag==true)
        {
            cseqH=sipHeader.header+SIPStack.SIP_LINE_END;
        }
        else cseqH="";
        //ALLOW HEADER
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO"+SIPStack.SIP_LINE_END;
        //USER-AGENT HEADER
        useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o"+SIPStack.SIP_LINE_END;

        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        String sendmessage=
                commandLine+
                        viaH+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        allowH+
                        useragentH+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }
    //INFO
    public boolean sendInfoResponse(String message,String remoteIp,int remotePort,int code)
    {
        String commandLine    = null;
        String viaH             = null;
        String toH          = null;
        String fromH         = null;
        String callidH       = null;
        String cseqH         = null;
        String allowH        = null;
        String useragentH     = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        viaH=sipCall.getViaArray(message);
        if(viaH == null || viaH.length()==0) viaH="";
        //FROM HEADER GET
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipHeader != null && sipHeader.flag==true) fromH=sipHeader.header+SIPStack.SIP_LINE_END;
        else fromH="";
        //TO HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipHeader != null && sipHeader.flag==true) {
            toH=sipHeader.header+SIPStack.SIP_LINE_END;
            if(toH.indexOf("tag=")<0) toH=sipHeader.header+";tag="+SIPStack.newTag()+SIPStack.SIP_LINE_END;
        }
        else toH="";
        //CALLID HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipHeader != null && sipHeader.flag==true) callidH=sipHeader.header+SIPStack.SIP_LINE_END;
        else callidH="";
        //CSEQ HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipHeader != null && sipHeader.flag==true)
        {
            cseqH=sipHeader.header+SIPStack.SIP_LINE_END;
        }
        else cseqH="";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
        {
            contactH="Contact: <sip:"+id+"@"+ifIp+":"
                    +ifPort+">"+SIPStack.SIP_LINE_END;
        }
        else contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">"+SIPStack.SIP_LINE_END;
        //ALLOW HEADER
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO"+SIPStack.SIP_LINE_END;
        //USER-AGENT HEADER
        useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o"+SIPStack.SIP_LINE_END;

        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        String sendmessage=
                commandLine+
                        viaH+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        contactH+
                        allowH+
                        useragentH+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        //DTMF PARSE
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CONTENTTYPE);
        if(
                sipHeader != null &&
                        sipHeader.flag==true &&
                        sipHeader.headerValue!=null && //2012 03 23
                        sipHeader.headerValue.length()>0)
        {
            if(sipHeader.headerValue.compareTo("application/dtmf-relay")==0) //DTMF-RELAY TYPE
            {
                //2012 02 22
                if(sipCall!=null && sipCall.flag==true && sipCall.parseDtmfSdp(message,SIPStack.SIP_DTMFINFO_DTMFRELAY)==true)
                {
                    System.out.println("INFO DTMF DETECTED:"+sipCall.detectedDtmf + "DURATION:"+sipCall.dtmfDuration);
                }
            }
            else if(sipHeader.headerValue.compareTo("application/dtmf")==0) //DTMF TYPE
            {
                //2012 02 22
                if(sipCall!=null && sipCall.flag==true && sipCall.parseDtmfSdp(message,SIPStack.SIP_DTMFINFO_DTMF)==true)
                {
                    System.out.println("INFO DTMF DETECTED:"+sipCall.detectedDtmf);
                }
            }
        }
        //

        return true;

    }
    //OPTIONS
    public boolean sendMessageResponse(String message,String remoteIp,int remotePort,int code)
    {
        String commandLine    = null;
        String viaH             = null;
        String toH          = null;
        String fromH         = null;
        String callidH       = null;
        String cseqH         = null;
        String allowH        = null;
        String useragentH     = null;
        String contentlengthH  = null;

        //REQUEST LINE
        commandLine="SIP/2.0 "+code+" "+SIPStack.getResponseDescription(code)+SIPStack.SIP_LINE_END;
        //VIA HEADER GET
        viaH=sipCall.getViaArray(message);
        if(viaH == null || viaH.length()==0) viaH="";
        //FROM HEADER GET
        SIPHeader sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_FROM);
        if(sipHeader != null && sipHeader.flag==true) fromH=sipHeader.header+SIPStack.SIP_LINE_END;
        else fromH="";
        //TO HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_TO);
        if(sipHeader != null && sipHeader.flag==true) {
            toH=sipHeader.header+SIPStack.SIP_LINE_END;
            if(toH.indexOf("tag=")<0) toH=sipHeader.header+";tag="+SIPStack.newTag()+SIPStack.SIP_LINE_END;
        }
        else toH="";
        //CALLID HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CALLID);
        if(sipHeader != null && sipHeader.flag==true) callidH=sipHeader.header+SIPStack.SIP_LINE_END;
        else callidH="";
        //CSEQ HEADER GET
        sipHeader=new SIPHeader(message,SIPStack.SIP_HEADERTYPE_CSEQ);
        if(sipHeader != null && sipHeader.flag==true)
        {
            cseqH=sipHeader.header+SIPStack.SIP_LINE_END;
        }
        else cseqH="";
        //ALLOW HEADER
        allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO"+SIPStack.SIP_LINE_END;
        //USER-AGENT HEADER
        useragentH="User-Agent: KwangHaeSoft SmartSip release 0100o"+SIPStack.SIP_LINE_END;

        //CONTENT-LENGTH HEADER
        contentlengthH="Content-Length: 0\r\n";
        //

        String sendmessage=
                commandLine+
                        viaH+
                        fromH+
                        toH+
                        callidH+
                        cseqH+
                        allowH+
                        useragentH+
                        contentlengthH+SIPStack.SIP_LINE_END
                ;
        if(sendmessage.length()>0 && signalManager!=null) {
            byte[] buffer=sendmessage.getBytes();
            if(sendmessage.length()>0) {
                if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SENT\n"+sendmessage);
                //signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length);
                //2012 07 16
                int repeatSendCount=0;
                while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                {
                    if(buffer.length==signalManager.BSSSipSendUdpPacket(remoteIp,remotePort,buffer,buffer.length))
                    {
                        break;
                    }
                    repeatSendCount++;
                }
                //

            }
        }

        return true;
    }
    //
    public boolean sendUpdate() //2011 12 26
    {
        if(sipCall==null || sipCall.flag==false) return false;
        String viaBranch=sipCall.viaBranch;//2013 02 13

        sipCall.setUpdateheaders("sendonly");

        //sipCall.CSEQ_NUMBER++; original
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;

        //REQUEST LINE
        sipCall.commandLine="INVITE sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        //2013 02 13
        sipCall.viaBranch=viaBranch;
        if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0) sipCall.viaBranch=SIPStack.getViaBranch();
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        if(sipCall.toH==null || sipCall.toH.length()==0)
            sipCall.toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        if(sipCall.fromH==null || sipCall.fromH.length()==0)
        {
            sipCall.fromTag=SIPStack.newTag();
            sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        }
        //CALLID HEADER
        // previous header use
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: "+SIPStack.USER_AGENT;
        //make body
        if(sipCall.sdp==null)
        {
            int audioport=SIPStack.getFreeAudioRtpPort();
            if(audioport>0) sipCall.constructSdp();
            if(sipCall.sdp != null && sipCall.sdp.flag==true)
            {
                sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
                if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                }
                else
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");

                }
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        RFC2833.payloadType,
                        "telephone-event/8000");
                sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
            }
        }
        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }
        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                }
            }
            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                }
            }
            return true;
        }

    }
    public boolean sendUpdate(String flow) //2011 12 26
    {
        if(sipCall==null || sipCall.flag==false) return false;
        String viaBranch=sipCall.viaBranch;//2013 02 13

        sipCall.setUpdateheaders(flow);

        //sipCall.CSEQ_NUMBER++; original
        sipCall.CSEQ_NUMBER=SIPStack.SIP_SEQUENCE_INVITE;//2012 07 27
        SIPStack.SIP_SEQUENCE_INVITE++;
        if(SIPStack.SIP_SEQUENCE_INVITE>65556) SIPStack.SIP_SEQUENCE_INVITE=1;
        //

        //REQUEST LINE
        sipCall.commandLine="INVITE sip:"+sipCall.dnis+"@"+serverDomain+":"+serverPort+" SIP/2.0";
        //VIA HEADER
        //2013 02 13
        sipCall.viaBranch=viaBranch;
        if(sipCall.viaBranch==null || sipCall.viaBranch.length()==0) sipCall.viaBranch=SIPStack.getViaBranch();
        sipCall.viaH="Via: SIP/2.0/UDP "+localIp+":"+localPort+";branch="+sipCall.viaBranch+";rport";
        //MAXFORWARDS HEADER
        sipCall.maxforwardH="Max-Forwards: 70";
        //CONTACT HEADER
        if(ifIp!=null && ifIp.length()>0 && ifPort>0)
            sipCall.contactH="Contact: <sip:"+id+"@"+ifIp+":"+ifPort+">";
        else sipCall.contactH="Contact: <sip:"+id+"@"+sipCall.localIp+":"+sipCall.localPort+">";
        //TO HEADER
        if(sipCall.toH==null || sipCall.toH.length()==0)
            sipCall.toH="To: \""+sipCall.dnis+"\"<sip:"+sipCall.dnis+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">";
        //FROM HEADER
        if(sipCall.fromH==null || sipCall.fromH.length()==0)
        {
            sipCall.fromTag=SIPStack.newTag();
            sipCall.fromH="From: \""+sipCall.id+"\"<sip:"+sipCall.id+"@"+sipCall.serverDomain+":"+sipCall.serverPort+">;tag="+sipCall.fromTag;
        }
        //CALLID HEADER
        // previous header use
        //CSEQ HEADER
        //2012 07 27 marked
        //if(sipCall.CSEQ_NUMBER>65556) sipCall.CSEQ_NUMBER=0;
        //SIPStack.SIP_SEQUENCE_INVITE=sipCall.CSEQ_NUMBER;
        sipCall.cseqH="CSeq: "+sipCall.CSEQ_NUMBER+" INVITE";
        //ALLOW HEADER
        sipCall.allowH="Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, NOTIFY, MESSAGE, SUBSCRIBE, INFO";
        //USER-AGENT HEADER
        sipCall.useragentH="User-Agent: "+SIPStack.USER_AGENT;
        //make body
        if(sipCall.sdp==null)
        {
            int audioport=SIPStack.getFreeAudioRtpPort();
            if(audioport>0) sipCall.constructSdp();
            if(sipCall.sdp != null && sipCall.sdp.flag==true)
            {
                sipCall.sdp.setMediaPort(SIPStack.SIP_MEDIATYPE_AUDIO,audioport);
                if(SIPStack.PRIMARY_CODEC_AUDIO==SIPStack.SIP_CODEC_G711U)
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                }
                else
                {
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711A,
                            "PCMA/8000");
                    sipCall.sdp.setCodec(
                            SIPStack.SIP_MEDIATYPE_AUDIO,
                            SIPStack.SIP_CODEC_G711U,
                            "PCMU/8000");

                }
                sipCall.sdp.setCodec(
                        SIPStack.SIP_MEDIATYPE_AUDIO,
                        RFC2833.payloadType,
                        "telephone-event/8000");
                sipCall.sdp.setFmtpDescribe(SIPStack.SIP_MEDIATYPE_AUDIO, RFC2833.payloadType, "0-15");
            }
        }
        //CONTENT-LENGTH HEADER
        String body=null;
        sipCall.contenttypeH="";
        if(sipCall.sdp != null && sipCall.sdp.flag==true)
        {
            sipCall.contenttypeH="Content-Type: application/SDP\r\n";
            body=sipCall.sdp.getBodyString();
            sipCall.contentlengthH="Content-Length: "+body.length();
        }
        else {
            body="";
            sipCall.contentlengthH="Content-Length: 0";
        }

        //
        sipCall.callState=SIPStack.SIP_CALLSTATE_UPDATING;
        sipCall.callDirection=SIPStack.SIP_CALLDIRECTION_OUT;

        if(sipCall.authorizationINVITEH != null && sipCall.authorizationINVITEH.length()>0)
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.authorizationINVITEH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                }
            }
            return true;
        }
        else
        {
            sipCall.message=
                    sipCall.commandLine+SIPStack.SIP_LINE_END+
                            sipCall.viaH+SIPStack.SIP_LINE_END+
                            sipCall.maxforwardH+SIPStack.SIP_LINE_END+
                            sipCall.contactH+SIPStack.SIP_LINE_END +
                            sipCall.fromH+SIPStack.SIP_LINE_END +
                            sipCall.toH+SIPStack.SIP_LINE_END+
                            sipCall.callidH+SIPStack.SIP_LINE_END+
                            sipCall.cseqH+SIPStack.SIP_LINE_END+
                            sipCall.allowH+SIPStack.SIP_LINE_END+
                            sipCall.useragentH+SIPStack.SIP_LINE_END+
                            sipCall.contenttypeH+
                            sipCall.contentlengthH+SIPStack.SIP_LINE_DOUBLEEND+
                            body
            ;
            if(sipCall.message.length()>0 && signalManager!=null) {
                byte[] buffer=sipCall.message.getBytes();
                if(sipCall.message.length()>0) {
                    if(SIPStack.SIP_MESSAGE_DEBUG==true) System.out.println("SEND\n"+sipCall.message);
                    //signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length);
                    //2012 07 16
                    int repeatSendCount=0;
                    while(repeatSendCount<SIPStack.REPEAT_SENDCOUNT)
                    {
                        if(buffer.length==signalManager.BSSSipSendUdpPacket(sipCall.remoteIp,sipCall.remotePort,buffer,buffer.length))
                        {
                            break;
                        }
                        repeatSendCount++;
                    }
                    //

                    sipCall.callTime_T0          = new Date();
                }
            }

            return true;
        }
    }
    public void sendHold()
    {
        if(sipCall!=null && sipCall.flag==true && sipCall.callMode==SIPStack.SIP_CALLMODE_BASIC
                && sipCall.callState==SIPStack.SIP_CALLSTATE_CONNECTED)
        {
            if(sipCall.bHolding==true)
            {
                sipCall.bHolding=false;
                sendUpdate("sendrecv");
                if(audioRTPManager.sipSound!=null) audioRTPManager.sipSound.flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDRECV;
            }
            else
            {
                sipCall.bHolding=true;
                sendUpdate();
                //2012 03 24
                if(audioRTPManager.sipSound!=null) audioRTPManager.sipSound.flowIndicator=SIPStack.SIP_MEDIAFLOW_SENDONLY;
            }
        }

    }
    public final void vibrateMilliseconds(final int milli,final Vibrator vibrator)
    {
        if(milli<=0) return;
        try {
            Runnable runner =new Runnable() {
                public void run() {
                    vibrator.vibrate(milli);
                }
            };
            if(runner != null) this.runOnUiThread(runner);
        } catch(Exception e){
            return;
        }
    }
    //

    public static File GetSDPathToFile(String filePatho, String fileName) {
        File wallpaperDirectory = new File(filePatho);

        if (filePatho == null || filePatho.length() == 0 || filePatho.charAt(0) != '/')
            filePatho = "/" + filePatho;

        wallpaperDirectory.mkdirs();

        File file = new File(wallpaperDirectory + filePatho);

        return new File(file.getAbsolutePath() + "/" + fileName);// file;
    }

    class VideoRtpmanager extends Thread {
        VideoDecode videodec=new VideoDecode();
        public Date hotbitTime=new Date();
        public RtpPacketizing rtpPacketizing = new RtpPacketizing();

        @Override
        public void run() {
            Log.i("UI", ">>>> VideoRtpmanager started");
            try {
                RTP_PORT=VideoDecode.DEFAULT_RTP_PORT; //2014 07 29
                Log.i("UI", ">>>> Video Port:"+RTP_PORT);
                videodec.runningSocket=new DatagramSocket(RTP_PORT);
                //theSocket.setSoTimeout(10); 10 ms이내에 데이터가 없으면 소켓을 닫음.
                videodec.runningSocket.setReuseAddress(true);
                videodec.dp = new DatagramPacket(videodec.buffer,videodec.buffer.length);

            } catch(SocketException se)
            {
                System.err.println(se);
                return;
            } catch(NullPointerException ne) {
                System.err.println(ne);
                return;
            }

            videodec.init();
            videodec.bReceiveRunning   = true;
            videodec.bService        = true;
            Date currentTime=null;
            while(videodec.bReceiveRunning==true && videodec.runningSocket!=null) {
                if(videoManager==null) break;
                if(videoManager.isInterrupted()) break;
                try {
                    if(videodec.buffer==null) break;
                    if(videodec.dp==null) break;

                    videodec.dp.setData(videodec.buffer);
                    videodec.dp.setLength(videodec.RTP_VIDEO_SIZE);

                    videodec.runningSocket.receive(videodec.dp);

                    //
                    videodec.parseRTPHeader();


                    int length = videodec.dp.getLength();
                    Log.i("VIDEO","UDP data received. "+length);

                    RtpPacketizing.RET_RFC3894_CODE ret;

                    byte[] input = new byte[videodec.dp.getLength()];

                    System.arraycopy(videodec.dp.getData(), 0, input, 0, videodec.dp.getLength());

                    ret = rtpPacketizing.RtpDepacketizing(input, input.length);


                    if(ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_SPS || ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_PPS
                            || ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_END)
                    {

                        System.arraycopy(rtpPacketizing.decodebuffer, 0, videodec.buffer, 0, rtpPacketizing.decodelen);
                        videodec.dp.setLength(rtpPacketizing.decodelen);

                        Log.e("LOG", String.format("%d", rtpPacketizing.decodelen));

	                  /*
	                  Log.e("LOG", String.format("%x %x %x %x %x %x %x %x %x %x %x %x", rtpPacketizing.decodebuffer[0], rtpPacketizing.decodebuffer[1],
	                        rtpPacketizing.decodebuffer[2], rtpPacketizing.decodebuffer[3], rtpPacketizing.decodebuffer[4], rtpPacketizing.decodebuffer[5], rtpPacketizing.decodebuffer[6],
	                        rtpPacketizing.decodebuffer[7], rtpPacketizing.decodebuffer[8], rtpPacketizing.decodebuffer[9], rtpPacketizing.decodebuffer[10],
	                        rtpPacketizing.decodebuffer[11]));
	                   */
                        //videodec.dp.setData(rtpPacketizing.decodebuffer);
                        //videodec.dp.setLength(rtpPacketizing.decodelen);

                        //NAL

                        if(videodec.dp!=null && videodec.dp.getLength()>0 && bStarted==true)
                        {
                            //Log.i("VideoManager","received video "+videodec.dp.getLength());
                            currentTime=new Date();

                            if(videodec.PARSEpayload!=videodec.VIDEO_CODEC_H264)
                            {
                                continue;
                            }

                            //2015 06 16
                            if(videodec.PARSEmark==1) {
                                if(sipCall!=null && sipCall.flag==true) sipCall.bVideoIframeMarked=true;
                                //Log.i("VIDEO","I frame marked.");
                            }
                            //
                            //more job
                            //int datasize=videodec.dp.getLength()-12;
                            int datasize=videodec.dp.getLength();
                            if(datasize>100 )
                            {
                                try
                                { //2015 06 16 update
                                    if(VideoDecode.videosPool!=null && playView.bActive==true)
                                    {
                                        VideoPacket elem=new VideoPacket(videodec.buffer,0,datasize);
                                        if(elem!=null) {
                                            if(VideoDecode.videosPool.size() > VideoDecode.MAX_PACKETPOOL_SIZE)
                                            {
                                                int count=VideoDecode.videosPool.size();
                                                for(int i=0;i<count;i++)
                                                    VideoDecode.videosPool.remove();
                                                //System.out.println(">>>>> packet pool reset");
                                            }
	                                 /*
	                                 else  if(VideoDecode.videosPool.size() > 0 &&
	                                   videodec.PARSEmark==1)
	                                 {
	                                        int count=VideoDecode.videosPool.size();
	                                    for(int i=0;i<count;i++)
	                                       VideoDecode.videosPool.remove();

	                                 }
	                                  */
                                            //



                                            VideoDecode.videosPool.offer(elem);
                                        }
                                    }
                                }catch(Exception e){}
                                //System.out.println("bufferred frames  "+VideoDecode.videosPool.size());


                            }
                            videodec.frameCount++;
                            try {

                                if(videodec.frameCheckTime.getSeconds()!=currentTime.getSeconds())
                                {

                                    currentFrames=videoManager.videodec.frameCount;
                                    //displayFramecount(videodec.lastSequence);

                                    videodec.frameCount=0;
                                    videodec.frameCheckTime    = new Date();

                                }
                            }catch(Exception e) {
                                Log.i("E",e.toString());
                            }

                            //
                            rtpPacketizing.decodebuffer = null;
                            rtpPacketizing.decodelen = 0;
                        }
                    }
                }

                catch(SocketException se) {
                    System.err.println(se);
                    break;
                }catch(Exception e)
                {
                    System.err.println(e);
                    break;
                }


            }//while
            Log.i("UI", "RTP Thread terminated.");
            if(videodec.runningSocket!=null) {
                videodec.runningSocket.close();
                videodec.runningSocket=null;
            }
            videodec.bReceiveRunning   = false;
            videoManager   = null;
            videodec.bService     = false;
        }

        public int getBytesToInt(byte[] bytes) {
            int newValue=0;
            newValue |= (((int)bytes[0]) << 24) & 0xFF000000;
            newValue |= (((int)bytes[1]) << 16) & 0xFF0000;
            newValue |= (((int)bytes[2]) << 8) & 0xFF00;
            newValue |= (((int)bytes[3])) & 0xFF;
            return newValue;
        }
        public byte[] getIntToBytes(int iValue) {
            byte[] newbytes=new byte[4];

            newbytes[0] =(byte)( iValue >> 24 );
            newbytes[1] =(byte)( (iValue << 8) >> 24 );
            newbytes[2] =(byte)( (iValue << 16) >> 24 );
            newbytes[3] =(byte)( (iValue << 24) >> 24 );
            return newbytes;
        }
        //endof rtp
        private int getIntByByteArr(final byte[] byteArr) {
            int byte_0 = (((int)byteArr[0]) << 24)  & 0xFF000000;
            int byte_1 = (((int)byteArr[1]) << 16)  & 0x00FF0000;
            int byte_2 = (((int)byteArr[2]) << 8)   & 0x0000FF00;
            int byte_3 = (((int)byteArr[3]) << 0)   & 0x000000FF;

            return (byte_0 | byte_1 | byte_2 | byte_3);
        }
        public SmartHomeviewActivity.VideoRtpmanager getRtpManager() {
            if (videoManager == null) {
                videoManager = new SmartHomeviewActivity.VideoRtpmanager();
            }

            return videoManager;
        }

        public byte[] getByteArrByInt (int intVal) {
            byte[] arrByte = new byte[4];

            arrByte[0] = (byte) ((intVal >>> 24) & 0xFF);
            arrByte[1] = (byte) ((intVal >>> 16) & 0xFF);
            arrByte[2] = (byte) ((intVal >>> 8) & 0xFF);
            arrByte[3] = (byte) ((intVal >>> 0) & 0xFF);

            return arrByte;
        }


        public int SendRTPPacket(String sIp,int iPort,byte[] data,int dataSize)
        {
            if(videodec.bReceiveRunning==false) return 0;
            int sent=0;
            if(sIp==null || sIp.length()<=0) return 0;
            if(data==null || dataSize<=0) return 0;
            if(iPort<=0) return 0;
            if(videodec.runningSocket==null) return 0;
            try {
                InetAddress ia=InetAddress.getByName(sIp);
                if(ia!=null)
                {
                    DatagramPacket dp=new DatagramPacket(data,dataSize,ia,iPort);
                    if(dp != null)
                    {
                        videodec.runningSocket.send(dp);
                    }
                }

                sent=dataSize;

            } catch(NullPointerException ne) {
                System.err.println(ne);
            } catch(UnknownHostException uhe) {
                System.err.println(uhe);
            } catch(IOException ie){
                System.err.println(ie);
            }

            return sent;
        }
        public int SendHotbit()
        {
            try
            {
                if(sipCall==null || sipCall.flag==false ||
                        sipCall.remoteSdp==null || sipCall.remoteSdp.flag==false ||
                        sipCall.remoteSdp.videoM==null || sipCall.remoteSdp.flag==false||
                        sipCall.remoteSdp.videoM.mediaIp.length()==0 ||
                        sipCall.remoteSdp.videoM.mediaPort<=0
                )
                    return 0;
            }catch(Exception e){}
            if(videodec.bReceiveRunning==false) return 0;
            int sent=0;
            String sIp=sipCall.remoteSdp.videoM.mediaIp;
            int iPort=sipCall.remoteSdp.videoM.mediaPort;
            byte[] data=new String("HOTBIT").getBytes();
            int dataSize=data.length;
            if(sIp==null || sIp.length()<=0) return 0;
            if(data==null || dataSize<=0) return 0;
            if(iPort<=0) return 0;
            if(videodec.runningSocket==null) return 0;
            try {
                InetAddress ia=InetAddress.getByName(sIp);
                if(ia!=null)
                {
                    DatagramPacket dp=new DatagramPacket(data,dataSize,ia,iPort);
                    if(dp != null)
                    {
                        videodec.runningSocket.send(dp);
                        //System.out.println("send hotbit");
                        hotbitTime=new Date();
                    }
                }

                sent=dataSize;

            } catch(NullPointerException ne) {
                System.err.println(ne);
            } catch(UnknownHostException uhe) {
                System.err.println(uhe);
            } catch(IOException ie){
                System.err.println(ie);
            }

            return sent;
        }

        public int SendRTPPacket(String sIp,int iPort,
                                 final byte[] encodedFrame, final int headerLen, final int frameLen, final int timeStamp
        )
        {
            int sent=0;
            if(sIp==null || sIp.length()<=0) return 0;
            if(encodedFrame==null || frameLen<=0) return 0;
            if(iPort<=0) return 0;
            if(videodec.runningSocket==null) return 0;
            try {
                InetAddress ia=InetAddress.getByName(sIp);
                int dataSize=0;
                if(ia!=null)
                {
                    int frameSize = (frameLen > 0) ? frameLen : headerLen;
                    byte[] headerByte = getByteArrByInt(headerLen);
                    byte[] frameByte = getByteArrByInt(frameLen);
                    byte[] timeByte = getByteArrByInt(timeStamp);
                    System.arraycopy(headerByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(frameByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(timeByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(encodedFrame,0,videodec.rtpVideoBuffer,dataSize,frameSize+12);
                    dataSize+=frameSize;

                    //
                    DatagramPacket dp=new DatagramPacket(videodec.rtpVideoBuffer,dataSize,ia,iPort);
                    if(dp != null)
                    {
                        videodec.runningSocket.send(dp);
                    }
                }

                sent=dataSize;

            } catch(NullPointerException ne) {
                System.err.println(ne);
            } catch(UnknownHostException uhe) {
                System.err.println(uhe);
            } catch(IOException ie){
                System.err.println(ie);
            }

            return sent;
        }
        public int writeRTPPacket(
                final byte[] encodedFrame,
                final int headerLen,
                final int frameLen,
                final int timeStamp,
                final boolean bIframe
        )
        {

            int sent=0;
            if(videodec.remoteIp==null || videodec.remoteIp.length()<=0) return 0;
            if(encodedFrame==null || frameLen<=0) return 0;
            if(videodec.remotePort<=0) return 0;
            if(videodec.runningSocket==null) return 0;
            try {
                InetAddress ia=InetAddress.getByName(videodec.remoteIp);
                int dataSize=12;
                if(ia!=null)
                {
                    int frameSize = (frameLen > 0) ? frameLen : headerLen;
                    byte[] headerByte = getByteArrByInt(headerLen);
                    byte[] frameByte = getByteArrByInt(frameLen);
                    byte[] timeByte = getByteArrByInt(timeStamp);

                    System.arraycopy(headerByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(frameByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(timeByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(encodedFrame,0,videodec.rtpVideoBuffer,dataSize,frameSize);
                    dataSize+=frameSize;

                    int packetsize=0;

                    try {
                        packetsize=dataSize;
                        videodec.RTPtimestamp=timeStamp;
                        if(videodec.rtpVideoBuffer !=null && packetsize>0)
                        {
                            if(bIframe==true) videodec.RTPmark=1;
                            else videodec.RTPmark=0;
                            videodec.RTPsequence++;
                            videodec.encodeRtpHeader(
                                    videodec.VIDEO_CODEC_H264
                            );
                        }

                    } catch (Exception e) {
                        System.err.println("I/O problems: " + e);
                        return 0;
                    }

                    //
                    DatagramPacket dp=new DatagramPacket(videodec.rtpVideoBuffer,dataSize,ia,videodec.remotePort);
                    if(dp != null)
                    {
                        videodec.runningSocket.send(dp);
                    }
                }

                sent=dataSize;

            } catch(NullPointerException ne) {
                System.err.println(ne);
            } catch(UnknownHostException uhe) {
                System.err.println(uhe);
            } catch(IOException ie){
                System.err.println(ie);
            }

            return sent;
        }
        public int writeHNSRTPPacket(
                final byte[] encodedFrame,
                final int headerLen,
                final int frameLen,
                final int timeStamp,
                final boolean bIframe
        )
        {
            int sent=0;
            if(videodec.remoteIp==null || videodec.remoteIp.length()<=0) return 0;
            if(encodedFrame==null || frameLen<=0) return 0;
            if(videodec.remotePort<=0) return 0;
            if(videodec.runningSocket==null) return 0;
            try {
                InetAddress ia=InetAddress.getByName(videodec.remoteIp);
                int dataSize=13;
                if(ia!=null)
                {
                    int frameSize = (frameLen > 0) ? frameLen : headerLen;
                    byte[] headerByte = getByteArrByInt(headerLen);
                    byte[] frameByte = getByteArrByInt(frameLen);
                    byte[] timeByte = getByteArrByInt(timeStamp);

                    System.arraycopy(headerByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(frameByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(timeByte,0,videodec.rtpVideoBuffer,dataSize,4);
                    dataSize+=4;
                    System.arraycopy(encodedFrame,0,videodec.rtpVideoBuffer,dataSize,frameSize);
                    dataSize+=frameSize;

                    int packetsize=0;

                    try {
                        packetsize=dataSize;
                        videodec.RTPtimestamp=timeStamp;
                        if(videodec.rtpVideoBuffer !=null && packetsize>0)
                        {
                            if(bIframe==true) videodec.RTPmark=1;
                            else videodec.RTPmark=0;
                            videodec.RTPsequence++;

                            videodec.encodeHNSRtpHeader(
                                    videodec.VIDEO_CODEC_H264,videodec.bViewme
                            );
                            //Log.i("VM"," "+BSSVideo.bViewme);
                        }

                    } catch (Exception e) {
                        System.err.println("I/O problems: " + e);
                        return 0;
                    }

                    //
                    DatagramPacket dp=new DatagramPacket(videodec.rtpVideoBuffer,dataSize,ia,videodec.remotePort);
                    if(dp != null)
                    {
                        videodec.runningSocket.send(dp);
                    }
                }

                sent=dataSize;

            } catch(NullPointerException ne) {
                System.err.println(ne);
            } catch(UnknownHostException uhe) {
                System.err.println(uhe);
            } catch(IOException ie){
                System.err.println(ie);
            }

            return sent;
        }

        public void closeSocket() {
            Log.d("CALL", "=================UdpClient closed!");

            try
            {
                if(videodec.runningSocket!=null) {
                    videodec.runningSocket.close();
                    videodec.runningSocket=null;
                }
                videodec.bReceiveRunning=false;
            }catch(Exception e){}
        }
    }
    ////////////////////[VIDEO TCP MANAGER START]//////////////////////
    class VideoTCPManager extends Thread {
        VideoDecode videodec=new VideoDecode();
        public static final int  VIDEO_MTU = 600;
        public RtpPacketizing rtpPacketizing = new RtpPacketizing();

        @Override
        public void run() {
            Log.i("UI", ">>>> VideoTCPManager started");

            videodec.init();
            videodec.bReceiveRunning   = true;
            videodec.bService        = true;
            videodec.remoteIp=sipCall.remoteSdp.videoM.mediaIp;
            //videodec.remotePort=sipCall.remoteSdp.videoM.mediaPort+1000;
            videodec.remotePort=sipCall.remoteSdp.videoM.mediaPort;

            try {
                // 서버 연결
                System.out.println("server connecting..."+videodec.remoteIp+"  "+videodec.remotePort);
                //          videodec.tcpSocket = new Socket(videodec.remoteIp, videodec.remotePort);
                videodec.tcpSocket = new Socket(videodec.remoteIp, videodec.remotePort);
                System.out.println("server connected");

                //displayFramecount(0000); //normal

            } catch (IOException e) {

                //displayFramecount(999999); //error
                e.printStackTrace();
            }


            Date currentTime=null;

            int recBytes=0;
            try {
                DataInputStream dis=new DataInputStream(videodec.tcpSocket.getInputStream());

                while(videodec.bReceiveRunning==true && videodec.tcpSocket!=null)
                {
                    if(videoTCPManager==null) break;
                    if(videoTCPManager.isInterrupted()) break;

                    try {
                        if(videodec.buffer==null) break;
                        if(videodec.sizeBuffer==null) break;
                        if(dis==null) break;

                        recBytes = dis.read(videodec.sizeBuffer,0,2);

                        if(recBytes==-1) break;

                        if(recBytes!=2) continue;

                        videodec.netdatasize=0;

                        int size1=(int)videodec.sizeBuffer[0] & 0xFF;
                        int size2=(int)videodec.sizeBuffer[1] & 0xFF;


                        int size=(size1 << 8) | size2;
                        //Log.i("VIDEO","TCP data size will be received. "+size);

                        int readSize=size;

                        //NAL
                        if(readSize<=0) continue;

                        recBytes = dis.read(videodec.buffer, 0, readSize);

                        if(recBytes==-1) break;
                        else {
                            //Log.i("VIDEO","TCP data received. "+recBytes);
                            //public static byte[] netdata          = new byte[50000];
                            //public static int       netdatasize    = 0;

                            System.arraycopy(videodec.buffer, 0, videodec.netdata, videodec.netdatasize, recBytes);
                            videodec.netdatasize+=recBytes;

                            readSize-=recBytes;
                            while(videodec.bReceiveRunning==true && videodec.tcpSocket!=null && readSize>0)
                            {
                                recBytes = dis.read(videodec.buffer,0,readSize);

                                if(recBytes==-1)
                                {
                                    videodec.bReceiveRunning=false;
                                    break;
                                }

                                System.arraycopy(videodec.buffer, 0, videodec.netdata, videodec.netdatasize, recBytes);
                                videodec.netdatasize+=recBytes;

                                //Log.i("VIDEO","TCP data received. "+recBytes);
                                readSize-=recBytes;
                            }

                            boolean FULL_PACKET_MODE = true;

                            if(FULL_PACKET_MODE == false)
                            {

                                currentFrames=videodec.lastSequence;

                                if(false==videodec.parseTCPRTPHeader()) continue;

                                //displayFramecount(videodec.lastSequence);

                                RtpPacketizing.RET_RFC3894_CODE ret;

                                byte[] input = new byte[videodec.netdatasize];

                                System.arraycopy(videodec.netdata, 0, input, 0, videodec.netdatasize);

                                ret = rtpPacketizing.RtpDepacketizing(input, input.length);

                                //rtpPacketizing.packetErr = false;

                                if(ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_SPS || ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_PPS
                                        || ret == RtpPacketizing.RET_RFC3894_CODE.RET_RFC3894_END)
                                {

                                    if(rtpPacketizing.packetErr == false)
                                    {
                                        System.arraycopy(rtpPacketizing.decodebuffer, 0, videodec.netdata, 0, rtpPacketizing.decodelen);
                                        size = rtpPacketizing.decodelen;
                                        videodec.netdatasize = size;
                                    }

                                    if(videodec.netdatasize == size && rtpPacketizing.packetErr == false)
                                    {
                                        //
                                        //Log.i("VideoManager","received video "+size);
                                        currentTime=new Date();

                                        if(videodec.PARSEpayload!=videodec.VIDEO_CODEC_H264) {
                                            continue;
                                        }

                                        //2015 06 16
                                        if(videodec.PARSEmark==1) {
                                            if(sipCall!=null && sipCall.flag==true) sipCall.bVideoIframeMarked=true;
                                            //Log.i("VIDEO","I frame marked.");
                                        }
                                        //
                                        //more job
                                        //int datasize=videodec.netdatasize-12;
                                        int datasize=videodec.netdatasize;
                                        if(datasize>100 )
                                        {
                                            try
                                            { //2015 06 16 update
                                                if(VideoDecode.videosPool!=null && playView.bActive==true)
                                                {
                                                    //VideoPacket elem=new VideoPacket(videodec.netdata,12,datasize);
                                                    VideoPacket elem=new VideoPacket(videodec.netdata,0,datasize);
                                                    if(elem!=null) {
                                                        if(VideoDecode.videosPool.size() > VideoDecode.MAX_PACKETPOOL_SIZE)
                                                        {
                                                            int count=VideoDecode.videosPool.size();
                                                            for(int i=0;i<count;i++)
                                                                VideoDecode.videosPool.remove();
                                                            System.out.println(">>>>> packet pool reset");
                                                        }
                                                        VideoDecode.videosPool.offer(elem);
                                                        Thread.sleep(1);
                                                    }
                                                }
                                            }catch(Exception e){}
                                            //System.out.println("bufferred frames  "+VideoDecode.videosPool.size());
                                        }
                                        videodec.frameCount++;
                                        rtpPacketizing.decodebuffer = null;
                                        rtpPacketizing.decodelen = 0;
                                        //System.out.println("f rameCount:  "+videodec.frameCount);
                                        try {
                                            if(videodec.frameCheckTime.getSeconds()!=currentTime.getSeconds())
                                            {
                                                //displayFramecount(videodec.lastSequence);
                                                currentFrames=videodec.lastSequence;

                                                videodec.frameCount=0;
                                                videodec.frameCheckTime    = new Date();
                                            }
                                        }catch(Exception e) {
                                            Log.i("E",e.toString());
                                        }
                                        ///////////////
                                    }
                                    else
                                    {
                                        rtpPacketizing.decodebuffer = null;
                                        rtpPacketizing.decodelen = 0;
                                        rtpPacketizing.packetErr = false;
                                    }
                                }
                            }
                            else
                            {
                                if(videodec.netdatasize == size)
                                {
                                    //
                                    //Log.i("VideoManager","received video "+size);
                                    currentTime=new Date();

                                    if(false==videodec.parseTCPRTPHeader()) {
                                        continue;
                                    }

                                    if(videodec.PARSEpayload!=videodec.VIDEO_CODEC_H264) {
                                        continue;
                                    }

                                    //2015 06 16
                                    if(videodec.PARSEmark==1) {
                                        if(sipCall!=null && sipCall.flag==true) sipCall.bVideoIframeMarked=true;
                                        //Log.i("VIDEO","I frame marked.");
                                    }
                                    //
                                    //more job
                                    int datasize=videodec.netdatasize-12;
                                    if(datasize>100 )
                                    {
                                        try
                                        { //2015 06 16 update
                                            if(VideoDecode.videosPool!=null && playView.bActive==true)
                                            {
                                                VideoPacket elem=new VideoPacket(videodec.netdata,12,datasize);
                                                if(elem!=null) {
                                                    if(VideoDecode.videosPool.size() > VideoDecode.MAX_PACKETPOOL_SIZE)
                                                    {
                                                        int count=VideoDecode.videosPool.size();
                                                        for(int i=0;i<count;i++)
                                                            VideoDecode.videosPool.remove();
                                                        System.out.println(">>>>> packet pool reset");
                                                    }

                                                    VideoDecode.videosPool.offer(elem);
                                                    Thread.sleep(1);
                                                }
                                            }
                                        }catch(Exception e){}
                                        //System.out.println("bufferred frames  "+VideoDecode.videosPool.size());
                                    }
                                    videodec.frameCount++;

                                    //System.out.println("f rameCount:  "+videodec.frameCount);

                                    try {
                                        if(videodec.frameCheckTime.getSeconds()!=currentTime.getSeconds())
                                        {
                                            //displayFramecount(videodec.lastSequence);
                                            currentFrames=videodec.lastSequence;

                                            videodec.frameCount=0;
                                            videodec.frameCheckTime    = new Date();
                                        }
                                    }catch(Exception e) {
                                        Log.i("E",e.toString());
                                    }

                                    ///////////////
                                }
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        System.err.println(e);
                        break;
                    }


                }//while
                dis.close();

                Log.i("UI", "Video TCP Thread terminated.");

                //displayFramecount(888888); //end

                if(videodec.tcpSocket!=null) {
                    videodec.tcpSocket.close();
                    videodec.tcpSocket=null;
                }
            }catch(Exception e){}

            videodec.bReceiveRunning   = false;
            videoManager   = null;
            videodec.bService     = false;
        }

        public int getBytesToInt(byte[] bytes) {
            int newValue=0;
            newValue |= (((int)bytes[0]) << 24) & 0xFF000000;
            newValue |= (((int)bytes[1]) << 16) & 0xFF0000;
            newValue |= (((int)bytes[2]) << 8) & 0xFF00;
            newValue |= (((int)bytes[3])) & 0xFF;
            return newValue;
        }
        public byte[] getIntToBytes(int iValue) {
            byte[] newbytes=new byte[4];

            newbytes[0] =(byte)( iValue >> 24 );
            newbytes[1] =(byte)( (iValue << 8) >> 24 );
            newbytes[2] =(byte)( (iValue << 16) >> 24 );
            newbytes[3] =(byte)( (iValue << 24) >> 24 );
            return newbytes;
        }
        //endof rtp
        private int getIntByByteArr(final byte[] byteArr) {
            int byte_0 = (((int)byteArr[0]) << 24)  & 0xFF000000;
            int byte_1 = (((int)byteArr[1]) << 16)  & 0x00FF0000;
            int byte_2 = (((int)byteArr[2]) << 8)   & 0x0000FF00;
            int byte_3 = (((int)byteArr[3]) << 0)   & 0x000000FF;

            return (byte_0 | byte_1 | byte_2 | byte_3);
        }
        public SmartHomeviewActivity.VideoTCPManager getVideoTCPManager() {
            if (videoTCPManager == null) {
                videoTCPManager = new SmartHomeviewActivity.VideoTCPManager();
            }

            return videoTCPManager;
        }

        public byte[] getByteArrByInt (int intVal) {
            byte[] arrByte = new byte[4];

            arrByte[0] = (byte) ((intVal >>> 24) & 0xFF);
            arrByte[1] = (byte) ((intVal >>> 16) & 0xFF);
            arrByte[2] = (byte) ((intVal >>> 8) & 0xFF);
            arrByte[3] = (byte) ((intVal >>> 0) & 0xFF);

            return arrByte;
        }

        public void closeSocket() {
            Log.d("CALL", "=================TcpClient closed!");

            try
            {
                if(videodec.tcpSocket!=null) {
                    videodec.tcpSocket.close();
                    videodec.tcpSocket=null;
                }
                videodec.bReceiveRunning=false;
            }catch(Exception e){}
        }
    }
    ////////////////////VideoTCPManager END

    //Spinner Adapter
    public class CustomAdapter extends ArrayAdapter<String>
    {
        private Context mContext;
        private int mResource;
        private ArrayList<String> mList;
        private LayoutInflater mInflater;

        public CustomAdapter(Context context, int layoutResource, ArrayList<String> objects)
        {
            super(context, layoutResource, objects);
            this.mContext = context;
            this.mResource = layoutResource;
            this.mList = objects;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            mResource = R.layout.file_spinner;

            if(convertView == null){
                convertView = mInflater.inflate(mResource, null);
            }

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.file_row, null);

            final String string = mList.get(position);

            if (!string.equals(""))
            {
                TextView text = (TextView) row.findViewById(R.id.control_row_text);

                text.setText(string);
            }

            return row;
        }
    }

    private int                                     mWaitCount              = 0;
    private int                                     mRequestState           = 0;
    private static final int                        REQUEST_DATA_CLEAR      = 0;
    private static final int                        REQUEST_DATA_SEND_START = 1;
    private static final int                        REQUEST_DATA_SEND_WAIT  = 2;

    private static final int                        TIMER_REQUEST           = 1500;  // 500msec
    private static final int                        TIMER_NULL              = 0;
    private static final int                        TIMER_WAIT_TIME         = 16;   // 10 * 500msec = 20sec

    private Messenger mDoorOpenResponse = null;
    private Messenger mDoorOpenRequest = null;

    private Handler                                 mTimeHandler;
    private Handler                                 mOnBackHandler;

    private int                                     mDataSendFlag           = 0;
    private CustomPopupBasic mCustomPopup;
    private CustomProgressDialog mProgressDialog;

    public void registerReceiver() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_APP_FINISH);
        intentFilter.addAction(Constants.ACTION_APP_NETWORK_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_SERVER_CONNECT_ERROR);
        intentFilter.addAction(Constants.ACTION_APP_OP_TIMEOUT);
        registerReceiver(appReceiver, new IntentFilter(intentFilter));
    }

    private void sendMessage(Message tMsg) {
        try {
            if(mDoorOpenRequest != null) {
                mDoorOpenRequest.send(tMsg);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void TimeHandlerDoorOpen(boolean tState, int tTime){
        if(tState == true){
            if(mTimeHandler == null){
                mTimeHandler = new Handler();
            }
            mTimeHandler.postDelayed(DoorOpenRunner, tTime);
        }else{
            mTimeHandler = null;
        }
    }

    private Runnable OnBackRunner = new Runnable() {
        @Override
        public void run() {
            onBackPressed();
        }
    };

    private Runnable DoorOpenRunner = new Runnable() {
        @Override
        public void run() {
            if(mTimeHandler != null){
                if(mRequestState == REQUEST_DATA_SEND_START){
                    DoorOpenRequest();
                    TimeHandlerDoorOpen(true, TIMER_REQUEST);
                }else{
                    mWaitCount++;
                    if(mWaitCount > TIMER_WAIT_TIME){
                        mWaitCount = 0;
                        mRequestState = REQUEST_DATA_CLEAR;
                        mProgressDialog.Dismiss();
                        TimeHandlerDoorOpen(false, TIMER_NULL);
                        if(mCustomPopup == null) {
                            mCustomPopup = new CustomPopupBasic(SmartHomeviewActivity.this, R.layout.popup_basic_onebutton,
                                    getString(R.string.Main_popup_error_title), getString(R.string.Main_popup_error_contents),
                                    mPopupListenerOK);
                            mCustomPopup.show();
                        }
                    }else{
                        TimeHandlerDoorOpen(true, TIMER_REQUEST);
                    }
                }
            }else{
                TimeHandlerDoorOpen(false, TIMER_NULL);
            }
        }
    };

    private void DoorOpenRequest(){
        mWaitCount    = 0;
        mRequestState = REQUEST_DATA_SEND_WAIT;
        TimeHandlerDoorOpen(true, TIMER_REQUEST);
        mProgressDialog.Show(getString(R.string.progress_request));

        Message tMsg = Message.obtain();
        tMsg.replyTo = mDoorOpenResponse;
        tMsg.what    = Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST;

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KD_DATA_WHAT, Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST);
        bundle.putString(Constants.KD_DATA_PUSH_TYPE, act.getIntent().getStringExtra("pushType"));
        bundle.putString(Constants.KD_DATA_PUSH_PASSWORD, act.getIntent().getStringExtra("pushPassword"));
        tMsg.setData(bundle);

        sendMessage(tMsg);
    }

    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup    = null;
            mRequestState   = REQUEST_DATA_CLEAR;
            TimeHandlerDoorOpen(false, TIMER_NULL);
            onBackPressed();
        }
    };

    private View.OnClickListener mPopupPwListenerCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup    = null;
        }
    };

    private View.OnClickListener mPopupPwListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup    = null;
            if (getIntent().getExtras() != null){
                if (mCustomPopup.mPassword.equals(getIntent().getStringExtra("pushPassword"))){
                    DoorOpenRequest();
                }else{
                    Toast.makeText(curActivity, "비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private Handler responseHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void handleMessage(Message msg) {
            Log.i(TAG,"msg.what : " + msg.what);
            switch (msg.what) {
                case    Constants.MSG_WHAT_PUSH_OPEN_DOOR_REQUEST:
                    DoorOpenResult((KDData)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    public void DoorOpenResult(KDData tKDData){

        Log.i("DoorOpenResult","result");

        mWaitCount  = 0;

        if(tKDData.Result.equals(Constants.HNML_RESULT_OK)){

            mDataSendFlag = 1;

            act.linearDoorOpen.setBackgroundResource(R.drawable.layer_list_shadow_radius_off);
            for (int i = 0; i < act.linearDoorOpen.getChildCount(); i++){
                View childV = act.linearDoorOpen.getChildAt(i);
                if (childV instanceof ImageView){
                    ((ImageView) childV).setImageResource(R.drawable.ic_door_open);
                    ((ImageView) childV).setColorFilter(getResources().getColor(R.color.colorb8b8b8));
                }else if (childV instanceof TextView){
                    ((TextView) childV).setTextColor(getResources().getColor(R.color.colorb8b8b8));
                }
            }

            linearDoorOpen.setEnabled(false);

            mOnBackHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    onBackPressed();
                }
            };

            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerDoorOpen(false, TIMER_NULL);

            mOnBackHandler.sendEmptyMessageDelayed(0,6000);

        }else{

            mProgressDialog.Dismiss();
            mRequestState = REQUEST_DATA_CLEAR;
            TimeHandlerDoorOpen(false, TIMER_NULL);

            if(mCustomPopup == null) {
                mCustomPopup = new CustomPopupBasic(SmartHomeviewActivity.this, R.layout.popup_basic_onebutton,
                        getString(R.string.Main_popup_error_title), getString(R.string.Popup_info_error_contents),
                        mPopupListenerOK);
                mCustomPopup.show();
            }
        }
    }

    private ServiceConnection requestConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDoorOpenRequest = new Messenger(service);
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_REGISTER_MESSENGER);
            tMsg.replyTo = mDoorOpenResponse;
            sendMessage(tMsg);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Message tMsg = Message.obtain(null, Constants.MSG_WHAT_UNREGISTER_MESSENGER);
            tMsg.replyTo = mDoorOpenResponse;
            sendMessage(tMsg);
            mDoorOpenRequest = null;
        }
    };
}

class MoviePlayView extends SurfaceView {
    private String TAG = "MoviePlayView";
    private Bitmap mBitmap = null;
    boolean bCodecInitialized = false;
    public Canvas canvas = null;
    public ArrayList<ByteBuffer> arrayVideo = new ArrayList<ByteBuffer>();
    private Rect rect;    // Variable rect to hold the bounds of the view
    private int draw_x = 37;
    private int draw_y = 50;

    private Rect draw_src;
    private Rect draw_dst;

    Context context;
    boolean bActive = false;
    boolean bCodecInit = false;

    public MoviePlayView(Context context) {
        super(context);
        this.context = context;
        playView = this;
        playView.setVisibility(View.VISIBLE);
        bActive = false;

        setWillNotDraw(false);
    }

    public MoviePlayView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.context = context;
        playView = this;
        playView.setVisibility(View.VISIBLE);
        bActive = false;
        setWillNotDraw(false);
    }


    public MoviePlayView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        this.context = context;
        playView = this;
        bActive = false;
        setWillNotDraw(false);
    }

    public void initializeCodec() {

        bCodecInit = true;

        Log.i("TEST","codec insert");

	/*        if (initBasicPlayer() < 0) {
	            Toast.makeText(context, "CPU doesn't support NEON", Toast.LENGTH_LONG).show();
	            ((Activity) context).finish();
	            bCodecInit = false;
	        }

	        int openResult = openMovie("");*/
        int openResult = 1;
        if (openResult < 0) {
            Toast.makeText(context, "Open Movie Error: " + openResult, Toast.LENGTH_LONG).show();
            ((Activity) context).finish();
            bCodecInit = false;
        } else {

            //            Log.i("TEST","codec width : " + getMovieWidth() + " height : " + getMovieHeight());
            int des_w = playView.getMeasuredWidth();//this.getMeasuredWidth();
            int des_h = playView.getMeasuredHeight();//this.getMeasuredHeight();


            mBitmap = Bitmap.createBitmap(640 , 480, Bitmap.Config.RGB_565);
            //            mBitmap = Bitmap.createBitmap(des_w, des_h, Bitmap.Config.ARGB_8888);
            int w = mBitmap.getWidth();
            int h = mBitmap.getHeight();
            Log.e("bitmap", String.format("w:%d,  h:%d", w, h));



            Log.e("initialize!!!Bitmap",mBitmap+"!!");
            Log.e("bitmap", String.format("playView w:%d,  playView h:%d", des_w, des_h));
            //            mBitmap = Bitmap.createBitmap(getMovieWidth(), getMovieHeight(), Bitmap.Config.RGB_565);

            arrayVideo = null;
            arrayVideo = new ArrayList<ByteBuffer>();

	/*            int des_w = playView.getMeasuredWidth();//this.getMeasuredWidth();
	            int des_h = playView.getMeasuredHeight();//this.getMeasuredHeight();*/

            draw_src = new Rect(0, 0, w, h);
            draw_dst = new Rect(0, 0, des_w, des_h);//이 크기로 변경됨

            Log.e("bitmap", String.format("des_w:%d,  des_h:%d", des_w, des_h));
        }

        bActive = true;

        invalidate();
    }


    public void invalidateView() {
	      /*
	      if(mBitmap != null)
	      {
	         int width = mBitmap.getWidth();
	         int height = mBitmap.getHeight();

	         Matrix matrix = new Matrix();
	         matrix.postRotate(90);

	         Bitmap resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, true);

	         mBitmap = resizedBitmap;
	      }

	      */
        if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//            canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
            Log.e("drawBitmap!!",mBitmap+"**1**"+  draw_src + draw_dst + ", "+draw_dst.right+ ", "+ draw_dst.bottom);
        }

        invalidate();

    }

    public void terminateCodec() {
        bActive = false;
        try {
            mBitmap.eraseColor(0);
            //invalidateView();             @주석 처리        풀 경우 fatal signal 11 sigsegv code 1  에러 뜸
            if (bCodecInit == true) {
                //                closeMovie();
                Log.e("closeMovie", "codec close success");
            } else {
                Log.e("closeMovie", "codec not init");
            }

            bCodecInit = false;
            //closeH264Decoder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width/19 * 11;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;

        if (SmartHomeviewActivity.bStarted == false || bActive == false) return;

        //draw_dst.set(new Rect(0, 0, this.draw_x *2, this.draw_y*2));

        if (SmartHomeviewActivity.bStarted == true && VideoDecode.videosPool.size() > 0) {
            try {
                VideoPacket elem = (VideoPacket) VideoDecode.videosPool.poll();
                Log.e("onDraw!!!elem",elem+"!!");
                if (elem.bFlag == true && elem.size > 0) {
                    //                    int len = renderFrameWithData(mBitmap, elem.data, elem.size);
                    int len = 1;
                    ByteBuffer buffer = ByteBuffer.wrap(elem.data);
                    Log.e("onDraw!!!buffer",buffer+"!!");

	                    /*  *
	                         int des_w = playView.getMeasuredWidth();//this.getMeasuredWidth();
	                         int des_h = playView.getMeasuredHeight();//this.getMeasuredHeight();
	                         mBitmap = Bitmap.createBitmap(des_w, des_h, Bitmap.Config.ARGB_8888);
	                    * */
                    int des_w = playView.getMeasuredWidth();//this.getMeasuredWidth();
                    int des_h = playView.getMeasuredHeight();//this.getMeasuredHeight();

                    H264Decoder decoder = new H264Decoder();
                    Picture out = Picture.create(640, 480, ColorSpace.YUV420); // Allocate output frame of max size
                    Log.e("onDraw!!!out",out.getData()+"!!");
                    Picture real = decoder.decodeFrame(buffer, out.getData());
                    Log.e("onDraw!!!real",real+"!!");
                    if(real!=null)
                    mBitmap = AndroidUtil.toBitmap(real);
                    Log.e("onDraw!!!mBitmap",mBitmap+"!!");
                    //mBitmap = com.kd.One.Common.jcodec.common.AndroidUtil.toBitmap(real);


                    //화면전환 예외처리
                    if (draw_dst.width() == 0 || draw_dst.height() == 0) {


                        Log.e("bitmap onDraw", String.format("des_w:%d,  des_h:%d", des_w, des_h));

                        draw_dst = new Rect(0, 0, des_w, des_h);//이 크기로 변경됨
                    }
                    //
                    if (SmartHomeviewActivity.isRec == true) {
                        arrayVideo.add(buffer);
                        Log.e(TAG,"arrayVideo size : " + arrayVideo.size());
                        if (arrayVideo.size() > 580){
                            Toast.makeText(context, "영상이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            SmartHomeviewActivity.linearRecord.performClick();
                        }
                    }

                    if (len > 0) {
						canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//                        canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
                        Log.e("drawBitmap!!",mBitmap+"**2**"+ draw_src + draw_dst + ", "+draw_dst.right+ ", "+ draw_dst.bottom);

                        if (bCodecInitialized == false) {
                            bCodecInitialized = true;
                            if (VideoDecode.videosPool != null && VideoDecode.videosPool.size() > 0) {
                                int count = VideoDecode.videosPool.size();
                                for (int i = 0; i < count; i++)
                                    VideoDecode.videosPool.remove();
                            }
                        }
                    } else {
                        if(mBitmap!=null)
						canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//                            canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
                        Log.e("drawBitmap!!",mBitmap+"**3**"+  draw_src + draw_dst + ", "+draw_dst.right+ ", "+ draw_dst.bottom);
                    }

                } else {
                    if(mBitmap!=null)
					canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//                        canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
                    Log.e("drawBitmap!!",mBitmap+"**4**"+  draw_src + draw_dst + ", "+draw_dst.right+ ", "+ draw_dst.bottom);
                }
            } catch (Exception e) {
                if(mBitmap!=null)
				canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//                    canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
                Log.e("drawBitmap!!",mBitmap+"**5**"+  draw_src + draw_dst + ", "+draw_dst.right+ ", "+ draw_dst.bottom);
            }
            //
        } else {
            if(mBitmap!=null)
                canvas.drawBitmap(mBitmap, draw_src, draw_dst, null);
//                        canvas.drawBitmap(getResizedBitmap(mBitmap, draw_dst.right, draw_dst.bottom), draw_src, draw_dst, null);
            Log.e("drawBitmap!!",mBitmap+"**6**"+ draw_src + draw_dst);
        }
        invalidate();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        //      bm.recycle();
        Log.e("resizedBitmap",resizedBitmap+"");
        return resizedBitmap;

    }

}
