package com.nowfloats.NavigationDrawer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 30-11-2017.
 */

public class TextExpandableAdapter extends BaseExpandableListAdapter {
    private ArrayList<ArrayList<String>> doubleList;
    private ArrayList<String> titleList;
    private Context mContext;
    public TextExpandableAdapter(Context context, ArrayList<ArrayList<String>> doubleList, ArrayList<String> titleList){
        mContext = context;
        this.doubleList = doubleList;
        this.titleList = titleList;
    }
    @Override
    public int getGroupCount() {
        return titleList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return doubleList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return titleList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return doubleList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        TextExpandableAdapter.MyHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_expandable_parent_item,viewGroup,false);
            holder = new TextExpandableAdapter.MyHolder();
            holder.text = view.findViewById(R.id.text1);
            holder.image = view.findViewById(R.id.image1);
            view.setTag(holder);
            int padding1 = Methods.dpToPx(10,mContext);
            view.setPadding(padding1,padding1/2,padding1,padding1/2);
        }

        holder = (TextExpandableAdapter.MyHolder) view.getTag();
        holder.text.setText(titleList.get(i));
        holder.image.setImageResource(b?R.drawable.up_arrow:R.drawable.down_arrow);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        TextExpandableAdapter.MyHolder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_text_item,viewGroup,false);
            holder = new TextExpandableAdapter.MyHolder();
            holder.text = view.findViewById(R.id.text1);
            view.setTag(holder);
            view.setBackgroundResource(R.color.white);
            int padding1 = Methods.dpToPx(5,mContext);
            view.setPadding(2*padding1,0,2*padding1,0);
        }

        holder = (TextExpandableAdapter.MyHolder) view.getTag();
        holder.text.setTextSize(13f);
        holder.text.setText(Methods.fromHtml(doubleList.get(i).get(i1)));

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    class MyHolder{
        public TextView text;
        public ImageView image;
    }
}
