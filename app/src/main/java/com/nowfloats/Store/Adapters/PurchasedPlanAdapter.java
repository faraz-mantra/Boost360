package com.nowfloats.Store.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.YourPurchasedPlansActivity;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Admin on 01-02-2018.
 */

public class PurchasedPlanAdapter extends RecyclerView.Adapter<PurchasedPlanAdapter.MyPurchasePlansHolder>{

    private Context mContext;
    private YourPurchasedPlansActivity.PlansType mPlansType;
    private ArrayList<ActivePackage> purchasePlans;
    public PurchasedPlanAdapter(Context context, YourPurchasedPlansActivity.PlansType planType){
        mContext = context;
        mPlansType = planType;

    }

    public void setPlansList(ArrayList<ActivePackage> plans){
        purchasePlans = plans;
    }
    @Override
    public MyPurchasePlansHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_active_plans,parent,false);
        return new MyPurchasePlansHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPurchasePlansHolder myHolder, int position) {
        myHolder.setActiveOrExpiredHolder(purchasePlans.get(position));
    }

    @Override
    public int getItemCount() {
        return purchasePlans.size();
    }

    @Override
    public int getItemViewType(int position) {
        return  mPlansType.ordinal();
    }

    class MyPurchasePlansHolder extends RecyclerView.ViewHolder {

        private TextView validityTv,visibleDetails, detailsTv, numberTv, statusTv, dateTv, renewTv, topUpTv;
        private ImageView planImV;
        private ActivePackage activePlan;
        MyPurchasePlansHolder(View itemView) {
            super(itemView);
            planImV = itemView.findViewById(R.id.img_plan);
            validityTv = itemView.findViewById(R.id.tv_validity);
            detailsTv = itemView.findViewById(R.id.tv_details);
            numberTv = itemView.findViewById(R.id.tv_number);
            statusTv = itemView.findViewById(R.id.tv_status);
            dateTv = itemView.findViewById(R.id.tv_date);
            visibleDetails = itemView.findViewById(R.id.tv_visible_details);
            detailsTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | detailsTv.getPaintFlags());
            detailsTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!activePlan.isExpanded()){
                        detailsTv.setText("Hide Plan Details");
                        visibleDetails.setVisibility(View.VISIBLE);
                        activePlan.setExpanded(true);
                    }else{
                        detailsTv.setText("Show Plan Details");
                        visibleDetails.setVisibility(View.GONE);
                        activePlan.setExpanded(false);
                    }

                }
            });
            renewTv = itemView.findViewById(R.id.tv_renew_upgrade);
            topUpTv = itemView.findViewById(R.id.tv_top_up);
        }

        void setActiveOrExpiredHolder(ActivePackage activePlan){
            this.activePlan = activePlan;
            if(activePlan.getProductClassification().getPackType() == 1){
                planImV.setVisibility(View.GONE);
                validityTv.setText(Methods.fromHtml(String.format("<b><font color=%s>%s</font></b>",
                        ContextCompat.getColor(mContext,R.color.gray), activePlan.getName())));
            }else{
                planImV.setVisibility(View.VISIBLE);
                validityTv.setText(Methods.fromHtml(String.format("Validity: <b><font color=%s>%s</font></b>",
                        ContextCompat.getColor(mContext,R.color.gray),activePlan.getTotalMonthsValidity()+" Months")));
            }
            if (activePlan.isExpanded()){
                detailsTv.setText("Hide Plan Details");
                visibleDetails.setVisibility(View.VISIBLE);
            }else{
                detailsTv.setText("Show Plan Details");
                visibleDetails.setVisibility(View.GONE);
            }
            visibleDetails.setText(activePlan.getFeatures());
            statusTv.setText(Methods.fromHtml(String.format("Status: <font color=%s>%s</font>",
                    ContextCompat.getColor(mContext,R.color.gray),activePlan.getActiveStatus())));
            numberTv.setText(Methods.fromHtml(String.format("Invoice Number: <font color=%s>%s</font>",
                    ContextCompat.getColor(mContext,R.color.gray), TextUtils.isEmpty(activePlan.getNfInternalERPId())? "NA":activePlan.getNfInternalERPId())));
            dateTv.setText(Methods.fromHtml(String.format("Activated on: <font color=%s>%s</font>",
                    ContextCompat.getColor(mContext,R.color.gray), Methods.getFormattedDate(activePlan.getToBeActivatedOn(),"dd MMM yyyy"))));
        }
    }
}