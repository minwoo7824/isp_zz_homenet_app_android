package com.kd.One.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kd.One.R;

import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-19.
 */
public class CustomAdpaterSpinner extends ArrayAdapter<String>{
    private Context             mContext;
    private int                 mResource;
    private ArrayList<String>   mList;
    private LayoutInflater      mInflater;
    private ArrayList<String>   mState;
    private String              mTitle;
    private int                 mImageSelect;
    private int                 mImage;


    public CustomAdpaterSpinner(Context tContext, int tLayoutResource,
                                String tTitle, ArrayList<String> tState, ArrayList<String> tList, int tImageSelect, int tImage){
        super(tContext, tLayoutResource, tList);
        this.mContext       = tContext;
        this.mResource      = tLayoutResource;
        this.mList          = tList;
        this.mState         = tState;
        this.mTitle         = tTitle;
        this.mImageSelect   = tImageSelect;
        this.mImage         = tImage;
        this.mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String tString = mList.get(position);

        mResource = R.layout.spinner_basic;

        if(convertView == null){
            convertView = mInflater.inflate(mResource, null);
        }

        if(!tString.equals("")){
            TextView tTextView = (TextView)convertView.findViewById(R.id.Spinner_TextView);
            tTextView.setText(tString);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getSpinnerCustomView(position, convertView, parent);
    }

    public View getSpinnerCustomView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View tRow = inflater.inflate(R.layout.spinner_row, null);

        ImageView tImage        = (ImageView)tRow.findViewById(R.id.Spinner_Row_ImageView);
        TextView  tText         = (TextView)tRow.findViewById(R.id.Spinner_Row_TextView);

        if(position == 0){
            tRow = inflater.inflate(R.layout.spinner_row_first, null);
            TextView  tTextTitle  = (TextView)tRow.findViewById(R.id.Spinner_Row_Top_TextView);
            tTextTitle.setText(mTitle);
        }else{
            String tString = mList.get(position);

            if(!tString.equals("")){
                if(mState.get(position).equals("Open") || mState.get(position).equals("Close")){
                    if (mState.get(position).equals("Open")) {
                        tImage.setImageResource(mImageSelect);
                    } else {
                        tImage.setImageResource(mImage);
                    }
                }else {
                    if (mState.get(position).equals("On")) {
                        tImage.setImageResource(mImageSelect);
                    } else {
                        tImage.setImageResource(mImage);
                    }
                }
                tText.setText(tString);
            }
        }
        return tRow;
    }
}
