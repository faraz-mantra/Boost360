package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 27-04-2017.
 */

public class VmnCallAdapter extends BaseExpandableListAdapter {


    private ArrayList<ArrayList<VmnCallModel>> listData;
    private Context mContext;
    public VmnCallAdapter(Context context,ArrayList<ArrayList<VmnCallModel>> hashMap){
        mContext = context;
        listData = hashMap;
    }

    @Override
    public int getGroupCount() {
        return listData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listData.get(groupPosition).get(0).getCallerNumber();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        MyParentHolder parentHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_vmn_call_item, parent, false);
            parentHolder = new MyParentHolder(convertView);
            convertView.setTag(parentHolder);

        }else{
            parentHolder = (MyParentHolder) convertView.getTag();
        }
        parentHolder.callerNumber.setText((String)getGroup(groupPosition));

        if (isExpanded) {
            parentHolder.arrowImage.setImageResource(R.drawable.ic_arrow_drop_up);
        }else {
            parentHolder.arrowImage.setImageResource(R.drawable.ic_arrow_drop_down);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MyChildHolder childHolder;
        VmnCallModel childModel = (VmnCallModel) getChild(groupPosition, childPosition);
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.vmn_call_child_item, parent, false);
            childHolder = new MyChildHolder(convertView);
            convertView.setTag(childHolder);

        }else{
            childHolder = (MyChildHolder) convertView.getTag();
        }
        childHolder.date.setText(Methods.getFormattedDate(childModel.getCallDateTime()));
        if(childModel.getCallStatus().equalsIgnoreCase("MISSED")){
            childHolder.callImage.setImageResource(R.drawable.ic_call_missed);
            childHolder.mediaImage.setVisibility(View.GONE);
        }else{
            childHolder.callImage.setImageResource(R.drawable.ic_call_received);
            childHolder.mediaImage.setVisibility(View.VISIBLE);
            childHolder.mediaImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    private class MyParentHolder {

        TextView callerNumber;
        ImageView arrowImage;
        MyParentHolder(View itemView) {
            callerNumber = (TextView) itemView.findViewById(R.id.caller_number);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
        }
    }

    private class MyChildHolder {

        TextView date;
        ImageView mediaImage, callImage;
        MyChildHolder(View itemView) {
            date = (TextView) itemView.findViewById(R.id.tv_date);
            mediaImage = (ImageView) itemView.findViewById(R.id.media_img);
            callImage = (ImageView) itemView.findViewById(R.id.call_img);
        }
    }
}
