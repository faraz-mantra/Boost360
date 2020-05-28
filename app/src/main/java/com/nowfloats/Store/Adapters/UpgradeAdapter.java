package com.nowfloats.Store.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Store.Model.OnItemClickCallback;
import com.thinksity.BuildConfig;
import com.thinksity.R;

/**
 * Created by Admin on 13-02-2018.
 */

public class UpgradeAdapter extends RecyclerView.Adapter<UpgradeAdapter.UpgradeCardHolder> {

    private OnItemClickCallback callbackListner;
    private Context mContext;
    public UpgradeAdapter(Context context, OnItemClickCallback callback){
        callbackListner = callback;
        mContext = context;
    }
    @Override
    public UpgradeCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_upgrade_item,parent,false);
        return new UpgradeCardHolder(view);
    }

    @Override
    public void onBindViewHolder(UpgradeCardHolder holder, int position) {
        if (position == 0){
            holder.setCardHolderContent(R.drawable.ic_base_plan,"Base Plans", mContext.getString(R.string.upgrade_base_plan_description),
                    mContext.getString(R.string.upgrade_base_plan_content),
                    "Check Base Plans");
        }else{
            holder.setCardHolderContent(R.drawable.ic_top_up_plans,"Top-up Plans","WildFire, Dictate, My Business App",
                    "You can add top ups to your website to get added features that enable your website to get more visibility and hence more business.",
                    "Check Top Up Plans");
        }
    }

    @Override
    public int getItemCount() {
        return BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")?2:1;
    }

    class UpgradeCardHolder extends RecyclerView.ViewHolder{

        private ImageView planTypeImg;
        private TextView planMainTv, planDescriptionTv, planMessageTv, planShowTv;
        UpgradeCardHolder(View itemView) {
            super(itemView);
            planTypeImg = itemView.findViewById(R.id.img_plans_type);
            planMainTv = itemView.findViewById(R.id.tv_main);
            planDescriptionTv = itemView.findViewById(R.id.tv_description);
            planMessageTv = itemView.findViewById(R.id.tv_message);
            planShowTv = itemView.findViewById(R.id.tv_show_plans);
            planShowTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callbackListner.onItemClick(getAdapterPosition());
                }
            });
        }

        private void setCardHolderContent(int resId,String mainText, String description, String message, String btnText){
            planTypeImg.setImageResource(resId);
            planMainTv.setText(mainText);
            planDescriptionTv.setText(description);
            planShowTv.setText(btnText);
            planMessageTv.setText(message);
        }
    }
}
