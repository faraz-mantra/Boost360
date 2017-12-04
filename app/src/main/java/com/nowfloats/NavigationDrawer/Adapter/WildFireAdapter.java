package com.nowfloats.NavigationDrawer.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 01-12-2017.
 */

public class WildFireAdapter extends RecyclerView.Adapter<WildFireAdapter.MyViewHolder> {

    private Context mContext;
    ArrayList<WildFireKeyStatsModel> modelList;
    ItemClick callback;
    public WildFireAdapter(ItemClick callback ,Context context ,ArrayList<WildFireKeyStatsModel> list){
        mContext = context;
        this.callback = callback;
        modelList = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(mContext).inflate(MyViewHolder.layout_id,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.emailTextView.setText(modelList.get(position).getAdGroupName());
        holder.statusTextView.setText(modelList.get(position).getKeywordState());
        if (modelList.get(position).getKeywordState().equalsIgnoreCase("enabled")){
            holder.statusTextView.setTextColor(ContextCompat.getColor(mContext,R.color.green));
        }else{
            holder.statusTextView.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final static int layout_id = R.layout.adapter_subscribers_item;
        ImageView arrowImage;
        TextView emailTextView,statusTextView;
        View view;
        LinearLayout mLinearLayout;
        MyViewHolder(View itemView) {
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
                    callback.onItemSelected(getAdapterPosition());
                    break;
            }
        }
    }
    public interface ItemClick{
        void onItemSelected(int data);
    }

}
