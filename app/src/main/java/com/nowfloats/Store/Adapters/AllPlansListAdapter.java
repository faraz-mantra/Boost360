package com.nowfloats.Store.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nowfloats.Store.Model.WidgetPacks;
import com.nowfloats.Store.PricingDetailsFragment;
import com.thinksity.R;

import java.util.List;

class AllPlansListAdapter extends RecyclerView.Adapter<AllPlansListAdapter.ViewHolder> {
    public final PricingDetailsFragment.OnPlanDescriptionClickListener planDescriptionClickListener;
    final Context mContext;
    private List<WidgetPacks> mDataset;
    private PopupWindow popup;

    public AllPlansListAdapter(Context mContext, PricingDetailsFragment.OnPlanDescriptionClickListener planDescriptionClickListener) {
        this.mContext = mContext;
        this.planDescriptionClickListener = planDescriptionClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_plans_group_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvItemName.setText(mDataset.get(position).Name);

    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public void setDataSrc(List<WidgetPacks> groupItems) {
        this.mDataset = groupItems;
        this.notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvItemName;
        private final ImageView ivQuestion;
        private TextView tvDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_group_item_name);
            ivQuestion = itemView.findViewById(R.id.img_question_plan_group);
            ivQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    planDescriptionClickListener.onPlanClick(ivQuestion, mDataset.get(getAdapterPosition()).Desc);
                }
            });
        }
    }
}
