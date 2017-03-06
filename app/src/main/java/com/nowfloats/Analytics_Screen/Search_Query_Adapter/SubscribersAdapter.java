package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.CustomWidget.CircularCheckBox;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 02-03-2017.
 */

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.MyHolder> {

    private boolean deleteView ;
    int pos = -1;
    ArrayList<SubscriberModel> mSubscriberList;
    Context mContext;
    HashMap<Integer,Boolean> selectedMap = new HashMap<>();
    public SubscribersAdapter(Context context,ArrayList<SubscriberModel> subscriberModelList){
        mContext= context;
        mSubscriberList = subscriberModelList;
    }
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_subscribers_item,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (holder == null){
            return;
        }
        if(deleteView){
            holder.radioButton.setVisibility(View.VISIBLE);
            holder.arrowImage.setVisibility(View.GONE);
        }
        else{
            holder.radioButton.setVisibility(View.INVISIBLE);
            holder.arrowImage.setVisibility(View.VISIBLE);
        }

        holder.radioButton.setChecked(pos == position);
        holder.mTextView.setText(mSubscriberList.get(position).getUserMobile());
    }

    @Override
    public int getItemCount() {
        return mSubscriberList.size();
    }

    private void deleteView(int pos){
        deleteView = true;
        this.pos=pos;
        notifyDataSetChanged();
        ((MenuItemDelete)mContext).onChangeView(deleteView);
    }
    private void initialView() {
        deleteView = false;
        notifyDataSetChanged();
        ((MenuItemDelete)mContext).onChangeView(deleteView);
    }
    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView  arrowImage;
        TextView mTextView;
        CircularCheckBox radioButton;
        View view;
        LinearLayout mLinearLayout;
        MyHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            radioButton = (CircularCheckBox) itemView.findViewById(R.id.radioButton);
            mTextView = (TextView) itemView.findViewById(R.id.subscriber_text);
            view = itemView.findViewById(R.id.divider);
            mLinearLayout.setOnClickListener(this);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        selectedMap.put(getAdapterPosition(),radioButton.isChecked());
                    }else{
                        selectedMap.remove(getAdapterPosition());
                        if(selectedMap.isEmpty()){
                            initialView();
                        }
                    }
                }
            });
            mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!deleteView){
                        selectedMap.put(getAdapterPosition(),true);
                        deleteView(getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(radioButton.getVisibility() == View.VISIBLE) {
                radioButton.setChecked(!radioButton.isChecked());
                if(radioButton.isChecked()) {
                    selectedMap.put(getAdapterPosition(),radioButton.isChecked());
                }
                else
                {
                    selectedMap.remove(getAdapterPosition());
                    if(selectedMap.isEmpty()){
                        initialView();
                    }
                }
                Log.v("ggg",radioButton.isChecked()+" "+selectedMap.isEmpty()+" "+selectedMap.size());

            }else{
                //start detail activity
            }

        }
    }

    public interface MenuItemDelete{
        void onChangeView(boolean view);
    }

}
