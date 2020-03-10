package com.kd.One.Custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kd.One.R;


/**
 * Created by lwg on 2016-07-11.
 */
public class CustomPopupBasic extends Dialog {
    private TextView                mTextViewTitle;
    private TextView                mTextViewContents;
    private Button                  mBtnOK;
    private Button                  mBtnCancel;
    private EditText                mEdtCode;

    private int                     mLayout;
    private String                  mTitle;
    private String                  mContents;
    private View.OnClickListener    mClickListenerOK;
    private View.OnClickListener    mClickListenerCancel;
    public static String            mPassword;

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        WindowManager.LayoutParams tParam = new WindowManager.LayoutParams();
        tParam.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        tParam.dimAmount = 0.8f;
        getWindow().setAttributes(tParam);
        setContentView(mLayout);
        setLayOut();
    }

    //**********************************************************************************************
    /**
     * @breif 아무것도 없는 dialog
     * @param tContext
     */
    public CustomPopupBasic(Context tContext){
        super(tContext, android.R.style.Theme_Translucent_NoTitleBar);
    }
    //**********************************************************************************************

    public CustomPopupBasic(Context tContext, int tLayout, String tTitle, String tContent,
                            View.OnClickListener tOKListener){
        super(tContext, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLayout            = tLayout;
        this.mTitle             = tTitle;
        this.mContents          = tContent;
        this.mClickListenerOK   = tOKListener;
    }

    public CustomPopupBasic(Context tContext, int tLayout, String tTitle, String tContent,
                            View.OnClickListener tCancelListener,
                            View.OnClickListener tOKListener){
        super(tContext, android.R.style.Theme_Translucent_NoTitleBar);
        this.mLayout                = tLayout;
        this.mTitle                 = tTitle;
        this.mContents              = tContent;
        this.mClickListenerCancel   = tCancelListener;
        this.mClickListenerOK       = tOKListener;
    }

    /**
     *
     */
    private void setLayOut(){
        mTextViewTitle    = (TextView)findViewById(R.id.TextView_Popup_Basic_Title);
        mTextViewContents = (TextView)findViewById(R.id.TextView_Popup_Basic_Contents);
        mBtnOK            = (Button)findViewById(R.id.Btn_Popup_Basic_Confirm);
        mTextViewTitle.setText(mTitle);
        mTextViewContents.setText(mContents);
        mBtnOK.setOnClickListener(mClickListenerOK);
        switch(mLayout){
            case    R.layout.popup_basic_onebutton:
                break;
            case    R.layout.popup_basic_twobutton:
                mBtnCancel = (Button)findViewById(R.id.Btn_Popup_Basic_Cancel);
                mBtnCancel.setOnClickListener(mClickListenerCancel);
                break;
            case R.layout.dialog_door_open:
                mEdtCode = (EditText)findViewById(R.id.EditView_Popup_Basic_Code);
                mBtnCancel = (Button)findViewById(R.id.Btn_Popup_Basic_Cancel);
                mBtnCancel.setOnClickListener(mClickListenerCancel);

                mEdtCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        mPassword = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            default:
                break;
        }
    }


    public void onBackPressed(){
    }
}
