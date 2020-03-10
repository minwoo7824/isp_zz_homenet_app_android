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
 * Created by lwg on 2016-09-01.
 */
public class CustomAdapterVisitor extends ArrayAdapter<String>{
    private Context mContext;
    private ArrayList<String>   mDate;
    private ArrayList<String>   mTitle;
    private ArrayList<String>   mStatus;
    private LayoutInflater mLayoutInflater;

    public CustomAdapterVisitor(Context tContext, ArrayList<String> tDate,ArrayList<String> tTitle,
                                ArrayList<String> tStatus){
        super(tContext, R.layout.view_visitor_list_item_layout, tTitle);
        this.mContext   = tContext;
        this.mDate      = tDate;
        this.mTitle     = tTitle;
        this.mStatus    = tStatus;
        this.mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = mLayoutInflater.inflate(R.layout.view_visitor_list_item_layout, null);

        TextView tTextViewDate = (TextView)convertView.findViewById(R.id.VisitorList_Txt_List_Item_Date);
        TextView tTextViewTitle = (TextView)convertView.findViewById(R.id.VisitorList_Txt_List_Item_Time);

        tTextViewDate.setText(mDate.get(position));
        tTextViewTitle.setText(mTitle.get(position));

        if(mStatus.get(position).equals("NEW")){
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorfafafa));
        }else{
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorWhite));
        }

        return convertView;
    }
}
