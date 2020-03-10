package com.kd.One.Main;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kd.One.Common.Constants;
import com.kd.One.Common.LocalConfig;
import com.kd.One.Common.MyGlobal;
import com.kd.One.Info.NoticeActivity;
import com.kd.One.Info.RecordActivity;
import com.kd.One.Info.VisitorActivity;
import com.kd.One.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;


/**
 * Created by lwg on 2016-07-15.
 */
public class InfoFragment extends Fragment implements View.OnClickListener {
    //**********************************************************************************************
    private MyGlobal mMyGlobal;
    private LocalConfig mLocalConfig;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<Integer> mData = new ArrayList<>(0);
    private int mPosition = 0;
    //**********************************************************************************************

    //**********************************************************************************************
    private ArrayList<String> mInfoDeviceType;
    private ArrayList<String> mInfoDeviceName;
    private ArrayList<String> mInfoDeviceContents;
    private ArrayList<Class> mInfoDeviceClass;
    //**********************************************************************************************

    //**********************************************************************************************
    private LinearLayout mInfoLinNotice;
    private LinearLayout mInfoLinVisitorRecord;
    private LinearLayout mInfoLinVisitorAlbum;
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @breif create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_info, container, false);
    }
    //**********************************************************************************************


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //******************************************************************************************
        /**
         * @breif local variable registration
         */
        mLocalConfig = new LocalConfig(getActivity().getBaseContext());
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif global data setup
         */
        //******************************************************************************************
        mMyGlobal = mMyGlobal.getInstance();
        //******************************************************************************************

        //******************************************************************************************
        /**
         * @breif dot find activity
         */
        //******************************************************************************************
        mInfoLinNotice = (LinearLayout) getActivity().findViewById(R.id.InfoFrag_Lin_Notice);
        mInfoLinVisitorRecord = (LinearLayout) getActivity().findViewById(R.id.InfoFrag_Lin_Visitor_Record);
        mInfoLinVisitorAlbum = (LinearLayout) getActivity().findViewById(R.id.InfoFrag_Lin_Visitor_Album);


        if (null != mLocalConfig.getStringValue(Constants.SAVE_DATA_USE_PUSH)) {
            if (mLocalConfig.getStringValue(Constants.SAVE_DATA_USE_PUSH).equals("Y")) {
                mInfoLinVisitorRecord.setVisibility(View.VISIBLE);
            } else {
                mInfoLinVisitorRecord.setVisibility(View.GONE);
            }
        }


        mInfoLinNotice.setOnClickListener(this);
        mInfoLinVisitorRecord.setOnClickListener(this);
        mInfoLinVisitorAlbum.setOnClickListener(this);
    }

    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif onresum
     */
    @Override
    public void onResume() {
        super.onResume();
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif onpause
     */
    @Override
    public void onPause() {
        super.onPause();
    }
    //**********************************************************************************************

    //**********************************************************************************************

    /**
     * @breif ondistroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    //**********************************************************************************************

    //******************************************************************************************

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.InfoFrag_Lin_Notice: {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.InfoFrag_Lin_Visitor_Record: {
                Intent intent = new Intent(getActivity(), RecordActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.InfoFrag_Lin_Visitor_Album: {
                Intent intent = new Intent(getActivity(), VisitorActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
    //******************************************************************************************

    //**********************************************************************************************

}
