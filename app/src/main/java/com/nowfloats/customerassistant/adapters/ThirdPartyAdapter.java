package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.List;

/**
 * Created by Admin on 09-10-2017.
 */

public class ThirdPartyAdapter extends RecyclerView.Adapter<ThirdPartyAdapter.MySmsViewHolder> {


    private Context mContext;
    private List<SuggestionsDO> rvList;
    public ThirdPartyAdapter(Context context, List<SuggestionsDO> list){
        mContext = context;
        rvList = list;
    }

    @Override
    public MySmsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_third_party_sms_item,parent,false);
        return new MySmsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySmsViewHolder holder, int position) {
        SuggestionsDO suggestionsDO = rvList.get(position);
        holder.addressTextView.setText(suggestionsDO.getActualMessage());
        holder.timeTextView.setText(Methods.getFormattedDate(suggestionsDO.getDate()));
        holder.titleTextView.setText(suggestionsDO.getActualMessage());
        holder.responseTextView.setText("Respond in "+suggestionsDO.getExpiryTimeOfMessage());
    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }

    public void refreshListData(List<SuggestionsDO> newList){
        if(newList == null){
            return;
        }
        if(rvList.size()>0) {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SuggestionCallback(newList));
            result.dispatchUpdatesTo(this);
            rvList.clear();
            rvList.addAll(newList);
        }else{
            rvList.addAll(newList);
            notifyDataSetChanged();
        }

    }

    private class SuggestionCallback extends DiffUtil.Callback{

        List<SuggestionsDO> newList;
        private SuggestionCallback(List<SuggestionsDO> list){
            newList = list;
        }
        @Override
        public int getOldListSize() {
            return rvList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if(newList.get(newItemPosition).getMessageId() != null && rvList.get(oldItemPosition).getMessageId() != null){
                return newList.get(newItemPosition).getMessageId().equals(rvList.get(oldItemPosition).getMessageId());
            }
           return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return false;
        }
    }
    class MySmsViewHolder extends RecyclerView.ViewHolder{

        TextView addressTextView,titleTextView,timeTextView,responseTextView;
        public MySmsViewHolder(View itemView) {
            super(itemView);
            addressTextView = (TextView) itemView.findViewById(R.id.tv_address);
            titleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            timeTextView = (TextView) itemView.findViewById(R.id.tv_time);
            responseTextView = (TextView) itemView.findViewById(R.id.tv_response);
            responseTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, ThirdPartySuggestionDetailActivity.class);
                    i.putExtra("message",rvList.get(getAdapterPosition()));
                    mContext.startActivity(i);
                }
            });
        }
    }

    private void showActualMessage(){

    }
}
