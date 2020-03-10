package com.kd.One.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.github.barteksc.pdfviewer.PDFView;
import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.R;


/**
 * Created by lwg on 2016-07-12.
 */
public class AgreeActivity extends Activity {
    //**********************************************************************************************
    private CustomPopupBasic    mCustomPopup;
    private TextView            mAgreeTxtTab01;
    private TextView            mAgreeTxtTab02;
    private LinearLayout        mAgreeLinTab01;
    private LinearLayout        mAgreeLinTab02;
    private WebView             mAgreeWebView;
    private LinearLayout        mAgreeLinVisible;
    private TextView            mAgreeTxtTitle;
    private LinearLayout        mAgreeLinService;
    private LinearLayout        mAgreeLinPersonalInfo;
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif oncreate intro activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#fafafa"));
            }
        }
        setContentView(R.layout.activity_login_agree);

        //******************************************************************************************
        /**
         * @breif activity find action
         */
        mAgreeTxtTab01  = (TextView)findViewById(R.id.txt_agree_tab01);
        mAgreeTxtTab02  = (TextView)findViewById(R.id.txt_agree_tab02);
        mAgreeLinTab01  = (LinearLayout)findViewById(R.id.linear_agree_tab01);
        mAgreeLinTab02  = (LinearLayout)findViewById(R.id.linear_agree_tab02);
//        mAgreeWebView = (WebView)findViewById(R.id.web_agree_view);
        mAgreeLinService = (LinearLayout)findViewById(R.id.linear_agree_service);
        mAgreeLinPersonalInfo = (LinearLayout)findViewById(R.id.linear_agree_personal_info);
        mAgreeTxtTitle  = (TextView)findViewById(R.id.Agree_Txt_title);
        mAgreeLinVisible = (LinearLayout)findViewById(R.id.linear_agree_tab_visible);
        //******************************************************************************************

        //******************************************************************************************


        if (getIntent().hasExtra("login")){
            mAgreeLinVisible.setVisibility(View.GONE);
            mAgreeTxtTitle.setText(getResources().getString(R.string.Activity_Back));
        }else{
            mAgreeLinVisible.setVisibility(View.VISIBLE);
            mAgreeTxtTitle.setText(getResources().getString(R.string.Activity_Name_Setting));
        }

        if (getIntent().hasExtra("mode")){
            if (getIntent().getStringExtra("mode").equals("tab01")){
                mAgreeLinTab01.setVisibility(View.VISIBLE);
                mAgreeLinTab02.setVisibility(View.INVISIBLE);
                mAgreeTxtTab01.setTextColor(getResources().getColor(R.color.colorPrimary));
                mAgreeTxtTab02.setTextColor(getResources().getColor(R.color.colorBlack));

                mAgreeLinService.setVisibility(View.VISIBLE);
                mAgreeLinPersonalInfo.setVisibility(View.GONE);
//                pdfView.fromAsset("home_tok_service.pdf")
//                        .pages(0,1,2,3,4,5,6,7,8)
//                        .enableSwipe(true)
//                        .swipeHorizontal(false)
//                        .enableDoubletap(true)
//                        .defaultPage(0)
//                        .enableAnnotationRendering(false)
//                        .password(null)
//                        .scrollHandle(null)
//                        .enableAntialiasing(true)
//                        .load();
//                mAgreeWebView.loadUrl("file:///android_asset/home_tok_service_html.html");
            }else{
                mAgreeLinTab01.setVisibility(View.INVISIBLE);
                mAgreeLinTab02.setVisibility(View.VISIBLE);
                mAgreeTxtTab01.setTextColor(getResources().getColor(R.color.colorBlack));
                mAgreeTxtTab02.setTextColor(getResources().getColor(R.color.colorPrimary));

                mAgreeLinService.setVisibility(View.GONE);
                mAgreeLinPersonalInfo.setVisibility(View.VISIBLE);

//                pdfView.fromAsset("home_tok_personal_info.pdf")
//                        .pages(0,1,2,3,4,5,6,7,8)
//                        .enableSwipe(true)
//                        .swipeHorizontal(false)
//                        .enableDoubletap(true)
//                        .defaultPage(0)
//                        .enableAnnotationRendering(false)
//                        .password(null)
//                        .scrollHandle(null)
//                        .enableAntialiasing(true)
//                        .load();
//                mAgreeWebView.loadUrl("file:///android_asset/home_tok_personal_html.html");
            }
        }else{
            mAgreeLinTab01.setVisibility(View.VISIBLE);
            mAgreeLinTab02.setVisibility(View.INVISIBLE);
            mAgreeTxtTab01.setTextColor(getResources().getColor(R.color.colorPrimary));
            mAgreeTxtTab02.setTextColor(getResources().getColor(R.color.colorBlack));

            mAgreeLinService.setVisibility(View.VISIBLE);
            mAgreeLinPersonalInfo.setVisibility(View.GONE);

//            pdfView.fromAsset("home_tok_service.pdf")
//                    .pages(0,1,2,3,4,5,6,7,8)
//                    .enableSwipe(true)
//                    .swipeHorizontal(false)
//                    .enableDoubletap(true)
//                    .defaultPage(0)
//                    .enableAnnotationRendering(false)
//                    .password(null)
//                    .scrollHandle(null)
//                    .enableAntialiasing(true)
//                    .invalidPageColor(getResources().getColor(R.color.colorWhite))
//                    .load();
//            mAgreeWebView.loadUrl("file:///android_asset/home_tok_service_html.html");
        }
        //******************************************************************************************
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief resume operating
     */
    public void onResume() {
        super.onResume();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @brief pause operating
     */
    public void onPause() {
        super.onPause();

        if(mCustomPopup != null){
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif destroy operating
     */
    public void onDestroy() {
        super.onDestroy();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup ok button
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    };
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif back button select
     */
    @Override
    public void onBackPressed(){
//        Intent intent = new Intent(AgreeCheckActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();
        super.onBackPressed();
    }
    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif activity button
     * @param v
     */
    public void OnClickBtnAgree(View v){
        switch (v.getId()){
            case    R.id.Agree_Lin_Back:
                onBackPressed();
                break;
            case R.id.txt_agree_tab01 :
                mAgreeLinTab01.setVisibility(View.VISIBLE);
                mAgreeLinTab02.setVisibility(View.INVISIBLE);
                mAgreeTxtTab01.setTextColor(getResources().getColor(R.color.colorPrimary));
                mAgreeTxtTab02.setTextColor(getResources().getColor(R.color.colorBlack));

                mAgreeLinService.setVisibility(View.VISIBLE);
                mAgreeLinPersonalInfo.setVisibility(View.GONE);

//                pdfView.fromAsset("home_tok_service.pdf")
//                        .pages(0,1,2,3,4,5,6,7,8)
//                        .enableSwipe(true)
//                        .swipeHorizontal(false)
//                        .enableDoubletap(true)
//                        .defaultPage(0)
//                        .enableAnnotationRendering(false)
//                        .password(null)
//                        .scrollHandle(null)
//                        .enableAntialiasing(true)
//                        .spacing(3)
//                        .load();
//                mAgreeWebView.loadUrl("file:///android_asset/home_tok_service_html.html");
                break;
            case R.id.txt_agree_tab02 :
                mAgreeLinTab01.setVisibility(View.INVISIBLE);
                mAgreeLinTab02.setVisibility(View.VISIBLE);
                mAgreeTxtTab01.setTextColor(getResources().getColor(R.color.colorBlack));
                mAgreeTxtTab02.setTextColor(getResources().getColor(R.color.colorPrimary));

                mAgreeLinService.setVisibility(View.GONE);
                mAgreeLinPersonalInfo.setVisibility(View.VISIBLE);

//                pdfView.fromAsset("home_tok_personal_info.pdf")
//                        .pages(0,1,2,3,4,5,6,7,8)
//                        .enableSwipe(true)
//                        .swipeHorizontal(false)
//                        .enableDoubletap(true)
//                        .defaultPage(0)
//                        .enableAnnotationRendering(false)
//                        .password(null)
//                        .scrollHandle(null)
//                        .enableAntialiasing(true)
//                        .spacing(3)
//                        .load();
//                mAgreeWebView.loadUrl("file:///android_asset/home_tok_personal_html.html");
                break;
            default:
                break;
        }
    }
}
