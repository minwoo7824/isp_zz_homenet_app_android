package com.kd.One.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kd.One.Custom.CustomPopupBasic;
import com.kd.One.Login.AgreeActivity;
import com.kd.One.Login.LoginActivity;
import com.kd.One.Login.RegistActivity;
import com.kd.One.R;

public class AgreeCheckActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout                       mRegistImgBack;
    private CheckBox                        mRegistChbAgree01;
    private CheckBox                        mRegistChbAgree02;
    private CheckBox                        mRegistChbAgreeAll;
    private TextView                        mRegistTxtAgree01;
    private TextView                        mRegistTxtAgree02;
    private Button                          mRegistBtnNext;

    private CustomPopupBasic mCustomPopup;

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
        setContentView(R.layout.activity_agree_check);

        mRegistImgBack              = (LinearLayout)findViewById(R.id.linear_agree_check_back);
        mRegistChbAgree01           = (CheckBox)findViewById(R.id.Regist_Chb_agree01);
        mRegistChbAgree02           = (CheckBox)findViewById(R.id.Regist_Chb_agree02);
        mRegistChbAgreeAll          = (CheckBox)findViewById(R.id.Regist_Chb_agree_all);
        mRegistTxtAgree01           = (TextView) findViewById(R.id.Regist_Txt_agree01);
        mRegistTxtAgree02           = (TextView) findViewById(R.id.Regist_Txt_agree02);
        mRegistBtnNext              = (Button)findViewById(R.id.btn_agree_check_next);

        mRegistImgBack.setOnClickListener(this);
        mRegistBtnNext.setOnClickListener(this);
        mRegistChbAgree01.setOnClickListener(this);
        mRegistChbAgree02.setOnClickListener(this);
        mRegistChbAgreeAll.setOnClickListener(this);
        mRegistTxtAgree01.setOnClickListener(this);
        mRegistTxtAgree02.setOnClickListener(this);
    }

    //**********************************************************************************************

    //**********************************************************************************************
    /**
     * @breif popup timeout ok button
     */
    private View.OnClickListener mPopupListenerOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCustomPopup.dismiss();
            mCustomPopup = null;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.linear_agree_check_back : {
                onBackPressed();
                break;
            }
            case R.id.btn_agree_check_next : {
                if (mRegistChbAgree01.isChecked() && mRegistChbAgree02.isChecked()){
                    Intent intentRegist = new Intent(this, RegistActivity.class);
                    startActivity(intentRegist);
                }else{
                    if(mCustomPopup == null) {
                        mCustomPopup = new CustomPopupBasic(this, R.layout.popup_basic_onebutton,
                                getString(R.string.Main_popup_error_title), "필수항목을 체크해주세요.",
                                mPopupListenerOK);
                        mCustomPopup.show();
                    }
                }

                break;
            }
            case R.id.Regist_Chb_agree01 :
                if (mRegistChbAgree01.isChecked()){
                    mRegistChbAgree01.setTextColor(getResources().getColor(R.color.colorBlack));
                }else{
                    mRegistChbAgree01.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgreeAll.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgreeAll.setChecked(false);
                }
                break;
            case R.id.Regist_Chb_agree02 :
                if (mRegistChbAgree02.isChecked()){
                    mRegistChbAgree02.setTextColor(getResources().getColor(R.color.colorBlack));
                }else{
                    mRegistChbAgree02.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgreeAll.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgreeAll.setChecked(false);
                }
                break;
            case R.id.Regist_Chb_agree_all :
                if (mRegistChbAgreeAll.isChecked()){
                    mRegistChbAgreeAll.setTextColor(getResources().getColor(R.color.colorBlack));
                    mRegistChbAgree01.setTextColor(getResources().getColor(R.color.colorBlack));
                    mRegistChbAgree02.setTextColor(getResources().getColor(R.color.colorBlack));
                    mRegistChbAgree01.setChecked(true);
                    mRegistChbAgree02.setChecked(true);
                }else{
                    mRegistChbAgreeAll.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgree01.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgree02.setTextColor(getResources().getColor(R.color.colorb8b8b8));
                    mRegistChbAgree01.setChecked(false);
                    mRegistChbAgree02.setChecked(false);
                }
                break;
            case R.id.Regist_Txt_agree01 : {
                Intent intent = new Intent(this, AgreeActivity.class);
                intent.putExtra("mode","tab01");
                intent.putExtra("login","Y");
                startActivity(intent);
                break;
            }
            case R.id.Regist_Txt_agree02 :{
                Intent intent = new Intent(this, AgreeActivity.class);
                intent.putExtra("mode","tab02");
                intent.putExtra("login","Y");
                startActivity(intent);
                break;
            }
        }
    }
}
