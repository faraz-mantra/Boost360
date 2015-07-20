package com.nowfloats.Store.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;
import com.nowfloats.Store.DomainLookup;
import com.nowfloats.Store.StoreDataActivity;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Activity context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    HashMap<String, ArrayList<String>> mainData;
    ViewFilterListHeader groupHolder;

    public ExpandableListAdapter(Activity context, List<String> listDataHeader,
                                 HashMap<String, ArrayList<String>> data) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.mainData = data;
    }

    @Override
    public String getChild(int groupPosition, int childPosititon) {
        ArrayList<String> value = mainData.get(this.listDataHeader.get(groupPosition));
        if (value==null) return null;
        return value.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            String value = getChild(groupPosition, childPosition),key = "",childValue = "";
            try {
                childValue = value.split("#")[0];
                key = value.split("#")[1];
            }catch (Exception e){
                e.printStackTrace();
            }
            if (value!=null){
                if (convertView == null) {
                    LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = infalInflater.inflate(R.layout.design_child_store_feature, null);
                }
                ((TextView) convertView.findViewById(R.id.product_info_tv)).setText(childValue);
                Button domainBtn =((Button) convertView.findViewById(R.id.lookupDomain));
                domainBtn.setVisibility(View.GONE);
                if (key!=null && key.equals("DOMAINPURCHASE")){
                    domainBtn.setVisibility(View.VISIBLE);
                    domainBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, DomainLookup.class);
                            context.startActivity(intent);
                            context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                }
                Log.i("groupPosition----", "" + groupPosition);
                if (groupHolder != null) {
                    if (StoreDataActivity.expandableList.isGroupExpanded(groupPosition)) {
                        if (StoreDataActivity.expandableList.isGroupExpanded(Integer.parseInt(groupHolder.title.getTag().toString()))){
                            groupHolder.arrow.setImageResource(R.drawable.up_arrow);
                            if ((groupPosition%2)==0){
                                ((LinearLayout)convertView.findViewById(R.id.layout_child)).setBackgroundColor(context.getResources().getColor(R.color.group_color1));
                            }else {
                                ((LinearLayout)convertView.findViewById(R.id.layout_child)).setBackgroundColor(context.getResources().getColor(R.color.group_color2));
                            }
                        }
                    }
                }
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;
        ArrayList<String> value = mainData.get(this.listDataHeader.get(groupPosition));
        if (value==null) count = 0;
        else count = value.size();
        return count;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        try {
            String headerTitle = (String) getGroup(groupPosition);
            groupHolder = new ViewFilterListHeader();
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.design_group_store_feature, null);

                groupHolder.title = (TextView) convertView.findViewById(R.id.lblListHeader);
                groupHolder.arrow = (ImageView) convertView.findViewById(R.id.indicatorImageView);
                groupHolder.layout = (LinearLayout) convertView.findViewById(R.id.group_layout);
                convertView.setTag(groupHolder);
            } else {
                groupHolder = (ViewFilterListHeader) convertView.getTag();
            }
            if ((groupPosition%2)==0){
                groupHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.group_color1));
            }else {
                groupHolder.layout.setBackgroundColor(context.getResources().getColor(R.color.group_color2));
            }
            if (headerTitle!=null && !headerTitle.trim().equals("null") && headerTitle.trim().length()>0)
                groupHolder.title.setText(headerTitle);
            groupHolder.title.setTag(groupPosition);
            groupHolder.arrow.setTag(groupPosition);
            groupHolder.arrow.setImageResource(R.drawable.down_arrow);
            int currentGrpPos = Integer.parseInt(groupHolder.title.getTag().toString());
            if (getChildrenCount(currentGrpPos) == 0) {
                groupHolder.arrow.setVisibility(View.INVISIBLE);
            } else {
                groupHolder.arrow.setVisibility(View.VISIBLE);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ViewFilterListHeader {
        public TextView title;
        public ImageView arrow;
        public LinearLayout layout;
    }

}