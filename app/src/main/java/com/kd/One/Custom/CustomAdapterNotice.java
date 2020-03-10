package com.kd.One.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kd.One.Common.KDUtil;
import com.kd.One.R;

import java.util.ArrayList;

/**
 * Created by HN_USER on 2016-08-12.
 */
public class CustomAdapterNotice extends ArrayAdapter<String>{
    private Context mContext;
    private ArrayList<String>   mDate;
    private ArrayList<String>   mTitle;
    private ArrayList<String>   mContents;
    private LayoutInflater      mLayoutInflater;

    public CustomAdapterNotice(Context tContext, ArrayList<String> tDate,ArrayList<String> tTitle,ArrayList<String> tContents){
        super(tContext, R.layout.view_notice_list_item_layout, tTitle);
        this.mContext   = tContext;
        this.mDate      = tDate;
        this.mTitle     = tTitle;
        this.mContents  = tContents;
        this.mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        convertView = mLayoutInflater.inflate(R.layout.view_notice_list_item_layout, null);

        LinearLayout linearClick = (LinearLayout)convertView.findViewById(R.id.NoticeList_Lin_List_Item_Click);
        final LinearLayout linearVisible = (LinearLayout)convertView.findViewById(R.id.NoticeList_Lin_List_Item_Visible);

        TextView tTextViewDate = (TextView)convertView.findViewById(R.id.NoticeList_Txt_List_Item_date);
        TextView tTextViewTitle = (TextView)convertView.findViewById(R.id.NoticeList_Txt_List_Item_Title);
        TextView tTextViewContents = (TextView)convertView.findViewById(R.id.NoticeList_Txt_List_Item_Contents);

        final ImageView imgArrow = (ImageView)convertView.findViewById(R.id.NoticeList_Img_List_Item_Arrow);

        tTextViewDate.setText(mDate.get(position));
        tTextViewTitle.setText(mTitle.get(position));
        tTextViewContents.setText(mContents.get(position));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearVisible.getVisibility() == View.GONE){
                    KDUtil.Expand(linearVisible);
                    imgArrow.setRotation(180);
                }else{
                    KDUtil.Collapse(linearVisible);
                    imgArrow.setRotation(0);
                }
            }
        });
        return convertView;
    }
}
