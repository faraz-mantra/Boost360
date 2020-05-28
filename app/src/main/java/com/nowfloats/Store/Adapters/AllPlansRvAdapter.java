package com.nowfloats.Store.Adapters;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.PricingDetailsFragment;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 19-10-2017.
 */

public class AllPlansRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    List<Pair<String, Boolean>> mPlanDetails;
    private List<Pair<String, List<WidgetPacks>>> mRedTimPlanDetails;
    private PricingDetailsFragment.OnPlanDescriptionClickListener planDescriptionClickListener;

    public AllPlansRvAdapter(List<Pair<String, Boolean>> planDetails, FragmentActivity activity) {
        this.mPlanDetails = planDetails;
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plans_odd_row_layout, parent, false);
            return new GreyTextViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plans_even_row_layout, parent, false);
            return new WhiteTextViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GreyTextViewHolder) {
            GreyTextViewHolder greyTextViewHolder = (GreyTextViewHolder) holder;
            if (mPlanDetails != null) {
                greyTextViewHolder.tvContent.setText(Html.fromHtml(mPlanDetails.get(position).first));

                if (mPlanDetails.get(position).second) {
                    // greyTextViewHolder.ivTick.setVisibility(View.VISIBLE);
                } else {
                    // greyTextViewHolder.ivTick.setVisibility(View.INVISIBLE);
                }
            } else if (mRedTimPlanDetails != null) {
                if (!mRedTimPlanDetails.isEmpty() && mRedTimPlanDetails.get(position).second != null && mRedTimPlanDetails.get(position).second.isEmpty()) {
                    greyTextViewHolder.ivQuestion.setVisibility(View.GONE);
                } else {
                    greyTextViewHolder.ivQuestion.setVisibility(View.VISIBLE);
                }
                greyTextViewHolder.tvContent.setText(Html.fromHtml(mRedTimPlanDetails.get(position).first));
                if (mRedTimPlanDetails.get(position).second != null && !mRedTimPlanDetails.get(position).second.isEmpty() && mRedTimPlanDetails.get(position).second.get(0).Group != null) {
                    greyTextViewHolder.mList.setVisibility(View.VISIBLE);
                    AllPlansListAdapter listAdapter = new AllPlansListAdapter(mContext, planDescriptionClickListener);
                    greyTextViewHolder.mList.setAdapter(listAdapter);
                    greyTextViewHolder.mList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    listAdapter.setDataSrc(mRedTimPlanDetails.get(position).second);
                } else {
                    greyTextViewHolder.mList.setVisibility(View.GONE);
                }
            }
        } else {
            WhiteTextViewHolder whiteTextViewHolder = (WhiteTextViewHolder) holder;
            if (mPlanDetails != null) {
                whiteTextViewHolder.tvContent.setText(Html.fromHtml(mPlanDetails.get(position).first));
                if (mPlanDetails.get(position).second) {
                    //whiteTextViewHolder.ivTick.setVisibility(View.VISIBLE);
                } else {
                    //whiteTextViewHolder.ivTick.setVisibility(View.INVISIBLE);
                }
            } else if (mRedTimPlanDetails != null) {
                if (!mRedTimPlanDetails.isEmpty() && mRedTimPlanDetails.get(position).second != null && mRedTimPlanDetails.get(position).second.isEmpty()) {
                    whiteTextViewHolder.ivQuestion.setVisibility(View.GONE);
                } else {
                    whiteTextViewHolder.ivQuestion.setVisibility(View.VISIBLE);
                }
                whiteTextViewHolder.tvContent.setText(Html.fromHtml(mRedTimPlanDetails.get(position).first));
                if (mRedTimPlanDetails.get(position).second != null && !mRedTimPlanDetails.get(position).second.isEmpty() && mRedTimPlanDetails.get(position).second.get(0).Group != null) {
                    whiteTextViewHolder.mList.setVisibility(View.VISIBLE);
                    AllPlansListAdapter listAdapter = new AllPlansListAdapter(mContext, planDescriptionClickListener);
                    whiteTextViewHolder.mList.setAdapter(listAdapter);
                    whiteTextViewHolder.mList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
                    listAdapter.setDataSrc(mRedTimPlanDetails.get(position).second);
                } else {
                    whiteTextViewHolder.mList.setVisibility(View.GONE);
                }
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2);
    }

    @Override
    public int getItemCount() {
        return mPlanDetails != null ? mPlanDetails.size() : mRedTimPlanDetails != null ? mRedTimPlanDetails.size() : 0;
    }

    public void setPlanDetails(List<Pair<String, Boolean>> planDetails) {
        this.mPlanDetails = planDetails;
        this.mRedTimPlanDetails = null;
        this.notifyDataSetChanged();
    }

    public void setRedTimPlanDetails(List<Pair<String, List<WidgetPacks>>> planDetails) {
        this.mRedTimPlanDetails = planDetails;
        this.mPlanDetails = null;
        this.notifyDataSetChanged();
    }

    public void setClickListener(PricingDetailsFragment.OnPlanDescriptionClickListener planDescriptionClickListener) {
        this.planDescriptionClickListener = planDescriptionClickListener;
    }

    class GreyTextViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView mList;
        TextView tvContent;
        ImageView ivTick;
        private final ImageView ivQuestion;

        public GreyTextViewHolder(View itemView) {
            super(itemView);

            mList = itemView.findViewById(R.id.all_plan_list);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivTick = (ImageView) itemView.findViewById(R.id.iv_tick);
            ivQuestion = itemView.findViewById(R.id.img_question);
            ivQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRedTimPlanDetails != null && !mRedTimPlanDetails.isEmpty() && mRedTimPlanDetails.get(getAdapterPosition()).second != null && !mRedTimPlanDetails.get(getAdapterPosition()).second.isEmpty()) {
                        planDescriptionClickListener.onPlanClick(ivQuestion, mRedTimPlanDetails.get(getAdapterPosition()).second.get(0).Desc);
                    }
                }
            });
        }
    }

    class WhiteTextViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView mList;
        private final ImageView ivQuestion;
        TextView tvContent;
        ImageView ivTick;

        public WhiteTextViewHolder(View itemView) {
            super(itemView);
            mList = itemView.findViewById(R.id.all_plan_list);

            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivTick = (ImageView) itemView.findViewById(R.id.iv_tick);
            ivQuestion = itemView.findViewById(R.id.img_question);
            ivQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRedTimPlanDetails != null && !mRedTimPlanDetails.isEmpty() && mRedTimPlanDetails.get(getAdapterPosition()).second != null && !mRedTimPlanDetails.get(getAdapterPosition()).second.isEmpty()) {
                        planDescriptionClickListener.onPlanClick(ivQuestion, mRedTimPlanDetails.get(getAdapterPosition()).second.get(0).Desc);
                    }
                }
            });
        }
    }
}
