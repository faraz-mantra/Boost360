package com.nowfloats.Store.Adapters;

import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 19-10-2017.
 */

public class AllPlansRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Pair<String, Boolean>> mPlanDetails;

    public AllPlansRvAdapter(List<Pair<String, Boolean>> planDetails) {
        this.mPlanDetails = planDetails;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plans_odd_row_layout, parent, false);
            return new GreyTextViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plans_even_row_layout, parent, false);
            return new WhiteTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof GreyTextViewHolder){
            GreyTextViewHolder greyTextViewHolder = (GreyTextViewHolder) holder;
            greyTextViewHolder.tvContent.setText(Html.fromHtml(mPlanDetails.get(position).first));
            if(mPlanDetails.get(position).second){
                greyTextViewHolder.ivTick.setVisibility(View.VISIBLE);
            }else {
                greyTextViewHolder.ivTick.setVisibility(View.INVISIBLE);
            }
        } else {
            WhiteTextViewHolder whiteTextViewHolder = (WhiteTextViewHolder) holder;
            whiteTextViewHolder.tvContent.setText(Html.fromHtml(mPlanDetails.get(position).first));
            if(mPlanDetails.get(position).second){
                whiteTextViewHolder.ivTick.setVisibility(View.VISIBLE);
            }else {
                whiteTextViewHolder.ivTick.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position%2);
    }

    @Override
    public int getItemCount() {
        return mPlanDetails.size();
    }

    public void setPlanDetails(List<Pair<String, Boolean>> planDetails) {
        this.mPlanDetails = planDetails;
        this.notifyDataSetChanged();
    }

    class GreyTextViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        ImageView ivTick;

        public GreyTextViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivTick = (ImageView) itemView.findViewById(R.id.iv_tick);
        }
    }

    class WhiteTextViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        ImageView ivTick;

        public WhiteTextViewHolder(View itemView) {
            super(itemView);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivTick = (ImageView) itemView.findViewById(R.id.iv_tick);
        }
    }
}
