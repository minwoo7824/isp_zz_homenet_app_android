package com.kd.One.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kd.One.R;

import java.util.ArrayList;

/**
 * Created by lwg on 2016-09-17.
 */
public class CustomAdapterBasicSpinner extends ArrayAdapter<String>{
    private Context mContext;
    private int                 mResource;
    private ArrayList<String>   mList;
    private LayoutInflater      mInflater;


    public CustomAdapterBasicSpinner(Context tContext, int tLayoutResource,
                                ArrayList<String> tList){
        super(tContext, tLayoutResource, tList);
        this.mContext       = tContext;
        this.mResource      = tLayoutResource;
        this.mList          = tList;
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
        View tRow = inflater.inflate(R.layout.spinner_basic_row, null);

        TextView    tText   = (TextView)tRow.findViewById(R.id.Spinner_Basic_Row_TextView);
        String      tString = mList.get(position);
        tText.setText(tString);

        return tRow;
    }
}
