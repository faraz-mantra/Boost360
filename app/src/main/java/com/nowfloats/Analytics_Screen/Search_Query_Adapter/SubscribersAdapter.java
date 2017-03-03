package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 02-03-2017.
 */

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.MyHolder> {

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
        holder.mTextView.setText(mSubscriberList.get(position).getUserMobile());

    }

    @Override
    public int getItemCount() {
        return mSubscriberList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView radioButton, arrowImage;
        TextView mTextView;
        public MyHolder(View itemView) {
            super(itemView);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            radioButton = (ImageView) itemView.findViewById(R.id.radioButton);
            mTextView = (TextView) itemView.findViewById(R.id.subscriber_text);
        }

    }
}
