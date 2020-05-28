package com.nowfloats.AccountDetails;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.AccountDetails.Model.AccountDetailModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by guru on 07-07-2015.
 */
public class AccountInfoAdapter extends RecyclerView.Adapter<AccountInfoAdapter.MyViewHolder>{
    Activity activity;
    ArrayList<AccountDetailModel> detailModels;
    DateFormat dateFormat;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,pur_date,exp_date,status,purchase;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.account_plan_name);
            this.pur_date = (TextView) itemView.findViewById(R.id.account_pur_date);
            this.exp_date = (TextView) itemView.findViewById(R.id.account_exp_date);
            this.status = (TextView) itemView.findViewById(R.id.account_status);
            this.purchase = (TextView) itemView.findViewById(R.id.storebtn);
        }
    }

    public AccountInfoAdapter(Activity activity, ArrayList<AccountDetailModel> detailModels){
        this.activity = activity;
        this.detailModels = detailModels;
        dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        dateFormat.setTimeZone(TimeZone.getDefault());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_info_design, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(detailModels.size()>0){
            AccountDetailModel detail = detailModels.get(position);
            try {
                holder.purchase.setVisibility(View.GONE);
                detail.ToBeActivatedOn = detail.ToBeActivatedOn.replace("/Date(", "").replace(")/", "");
                Long epochTime = Long.parseLong(detail.ToBeActivatedOn);
                Date date = new Date(epochTime),date1 = null;
                holder.name.setText(detail.NameOfWidget);
                String value = dateFormat.format(date);
                holder.pur_date.setText(value);
                if(detail.totalMonthsValidity!=null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MONTH, Integer.parseInt(detail.totalMonthsValidity));
                    date1 = calendar.getTime();
                    detail.totalMonthsValidity = dateFormat.format(date1);
                }
                holder.exp_date.setText(detail.totalMonthsValidity);
                if(Methods.compareDate(date1,new Date())){
                    holder.status.setText(activity.getResources().getString(R.string.active));
                    holder.status.setTextColor(activity.getResources().getColor(R.color.green));
                    holder.purchase.setVisibility(View.GONE);
                }else {
                    holder.status.setText(activity.getResources().getString(R.string.expired));
                    holder.status.setTextColor(activity.getResources().getColor(android.R.color.holo_red_light));
                    holder.purchase.setVisibility(View.VISIBLE);
                    holder.purchase.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           activity.finish();
                           Constants.gotoStore = true;
                        }
                    });
                }
            }catch(Exception e){e.printStackTrace();}
        }
    }

    @Override
    public int getItemCount() {
        return detailModels.size();
    }
}