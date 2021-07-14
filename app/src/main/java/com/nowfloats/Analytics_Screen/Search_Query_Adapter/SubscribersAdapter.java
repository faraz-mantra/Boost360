package com.nowfloats.Analytics_Screen.Search_Query_Adapter;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.model.SubscriberModel;
import com.nowfloats.util.Constants;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 02-03-2017.
 */

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.MyHolder> {

    private ArrayList<SubscriberModel> mSubscriberList;
    private Context mContext;

    public SubscribersAdapter(Context context, ArrayList<SubscriberModel> subscriberModelList) {
        mContext = context;
        mSubscriberList = subscriberModelList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_subscribers_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (holder == null) {
            return;
        }
        holder.mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        holder.emailTextView.setText(mSubscriberList.get(position).getUserMobile());
        try {
            int status = Integer.parseInt(mSubscriberList.get(position).getSubscriptionStatus());
            if (Constants.SubscriberStatus.SUBSCRIBED.value == status) {
                holder.statusTextView.setText("Subscribed");
                //holder.emailTextView.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
            } else if (Constants.SubscriberStatus.REQUESTED.value == status) {
                holder.statusTextView.setText("Subscription initiated");
            } else if (Constants.SubscriberStatus.UNSUBSCRIBED.value == status) {
                holder.statusTextView.setText("Unsubscribed");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mSubscriberList.size();
    }

    public interface SubscriberInterfaceMethods {
        void onitemSeleted(int data);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView arrowImage;
        TextView emailTextView, statusTextView;
        View view;
        LinearLayout mLinearLayout;

        MyHolder(View itemView) {
            super(itemView);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            arrowImage = (ImageView) itemView.findViewById(R.id.arrowImage);
            emailTextView = (TextView) itemView.findViewById(R.id.subscriber_text);
            statusTextView = (TextView) itemView.findViewById(R.id.subscriber_status);
            view = itemView.findViewById(R.id.divider);
            mLinearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_layout:
                    ((SubscriberInterfaceMethods) mContext).onitemSeleted(getAdapterPosition());
                    break;
            }
        }
    }

}