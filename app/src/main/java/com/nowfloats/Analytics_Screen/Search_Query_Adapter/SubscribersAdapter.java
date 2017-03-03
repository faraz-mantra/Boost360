package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.CustomWidget.CircularCheckBox;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 02-03-2017.
 */

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.MyHolder> {

    private boolean deleteView ;
    int pos=-1;
    ArrayList<SubscriberModel> mSubscriberList;
    Context mContext;
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
            holder.radioButton.setVisibility(View.GONE);
            holder.arrowImage.setVisibility(View.VISIBLE);
        }
        if(pos == position) {
            holder.radioButton.setChecked(true);
        }else{
            holder.radioButton.setChecked(false);
        }
        holder.mTextView.setText(mSubscriberList.get(position).getUserMobile());
        //position == mSubscriberList.size()-1 ? View.GONE :
    }

    @Override
    public int getItemCount() {
        return mSubscriberList.size();
    }

    private void deleteView(int pos){
        deleteView = true;
        this.pos=pos;
        notifyDataSetChanged();
    }
    class MyHolder extends RecyclerView.ViewHolder{

        ImageView  arrowImage;
        TextView mTextView;
        CircularCheckBox radioButton;
        View view;
        LinearLayout mLinearLayout;
        public MyHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            radioButton = (CircularCheckBox) itemView.findViewById(R.id.radioButton);
            mTextView = (TextView) itemView.findViewById(R.id.subscriber_text);
            view = itemView.findViewById(R.id.divider);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(radioButton.getVisibility() == View.VISIBLE) {
                        radioButton.setChecked(!radioButton.isChecked());
                    }else{
                        //start detail activity
                    }

                }
            });
            mLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(pos ==-1){
                        deleteView(getAdapterPosition());
                    }
                    return true;
                }
            });
        }

    }
}
