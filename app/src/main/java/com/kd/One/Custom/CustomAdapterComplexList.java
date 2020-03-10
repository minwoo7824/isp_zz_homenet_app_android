package com.kd.One.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;


import com.kd.One.R;

import java.util.ArrayList;

/**
 * Created by lwg on 2016-07-11.
 */
public class CustomAdapterComplexList extends ArrayAdapter<String> implements Filterable{
    private Context mContext;
    private ArrayList<String> mComplex;
    private LayoutInflater mLayoutInflater;

    public CustomAdapterComplexList(Context tContext, ArrayList<String> tComplex){
        super(tContext, R.layout.layout_row_login_complex, tComplex);
        this.mContext   = tContext;
        this.mComplex   = tComplex;
        this.mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = mLayoutInflater.inflate(R.layout.layout_row_login_complex, null);
        TextView tTextViewName = (TextView)convertView.findViewById(R.id.TextView_Complex_List);
        tTextViewName.setText(mComplex.get(position));

        return convertView;
    }


}
