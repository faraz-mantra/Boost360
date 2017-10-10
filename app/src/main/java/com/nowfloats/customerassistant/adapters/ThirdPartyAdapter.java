package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 09-10-2017.
 */

public class ThirdPartyAdapter extends RecyclerView.Adapter<ThirdPartyAdapter.MySmsViewHolder> {


    private Context mContext;
    private ArrayList<SuggestionsDO> rvList;
    public ThirdPartyAdapter(Context context, ArrayList<SuggestionsDO> list){
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
    }

    @Override
    public int getItemCount() {
        return rvList.size();
    }

    public void refreshListData(List<SuggestionsDO> newlist){
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SuggestionCallback(newlist));
        result.dispatchUpdatesTo(this);
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
        }
    }
}
