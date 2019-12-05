package com.nowfloats.Store.Adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.Store.Model.ActivePackage;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by NowFloats on 17-10-2017.
 */

public class ActivePlansRvAdapter extends RecyclerView.Adapter<ActivePlansRvAdapter.PlanDetailsViewHolder> {

    private List<ActivePackage> mPackages;
    private boolean isExpired;
    private Context mContext;

    private OnItemClickInterface mOnitemClickInterface;

    public ActivePlansRvAdapter(Context context) {
        mPackages = new ArrayList<>();
        this.mContext = context;
    }

    @Override
    public PlanDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_plans_row_layout, parent, false);
        return new PlanDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PlanDetailsViewHolder holder, final int position) {
        final ActivePackage activePackage = mPackages.get(position);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Picasso.get().load(activePackage.getPrimaryImageUri()).into(holder.ivPlanIcon);
        holder.tvPlanName.setText(activePackage.getName());
        holder.tvActivationDate.setText(parseDate(activePackage.getToBeActivatedOn()));
        holder.tvExpiryDate.setText(format.format(getExpiryDate(activePackage.getToBeActivatedOn(),
                activePackage.getTotalMonthsValidity())));
        if(isExpired){
            holder.tvExpiresIn.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            holder.tvExpiryDate.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            holder.tvExpiresIn.setText("(expired)");
            holder.tvAddFeatures.setVisibility(View.GONE);
        }else {
            holder.tvExpiresIn.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            holder.tvExpiryDate.setTextColor(Color.parseColor("#808080"));
            holder.tvExpiresIn.setText("(expires in "
                    + getDaysRemaining(activePackage.getToBeActivatedOn(),
                    activePackage.getTotalMonthsValidity())
                    + " days)");
            holder.tvAddFeatures.setVisibility(View.VISIBLE);
        }
        String variable = activePackage.getTotalMonthsValidity() < 2 ? " month":" months";
        holder.tvValidity.setText("(Validity: " + activePackage.getTotalMonthsValidity() + variable + ")");
        if(activePackage.isExpanded()){
            holder.llPlanDetails.setVisibility(View.VISIBLE);
            holder.tvSeePlanDetails.setText(Html.fromHtml("<u>Hide Plan Details</u>"));
        }else {
            holder.llPlanDetails.setVisibility(View.GONE);
            holder.tvSeePlanDetails.setText(Html.fromHtml("<u>See Plan Details</u>"));
        }
        holder.tvPlanDetails.setText(activePackage.getFeatures());
        holder.tvSeePlanDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activePackage.isExpanded()){
                    holder.llPlanDetails.setVisibility(View.GONE);
                    activePackage.setExpanded(false);
                    holder.tvSeePlanDetails.setText(Html.fromHtml("<u>See Plan Details</u>"));
                }else {
                    holder.llPlanDetails.setVisibility(View.VISIBLE);
                    activePackage.setExpanded(true);
                    holder.tvSeePlanDetails.setText(Html.fromHtml("<u>Hide Plan Details</u>"));
                }
            }
        });
    }

    private Date getExpiryDate(String toBeActivatedOn, double totalMonthsValidity) {
        long time = Long.parseLong(toBeActivatedOn.replaceAll("[^\\d]", ""));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.MONTH, (int)Math.floor(totalMonthsValidity));
        calendar.add(Calendar.DATE, (int) ((totalMonthsValidity-Math.floor(totalMonthsValidity))*30));
        return calendar.getTime();
    }

    private long getDaysRemaining(String toBeActivatedOn, double totalMonthsValidity) {
        Date expDate = getExpiryDate(toBeActivatedOn, totalMonthsValidity);
        long duration = expDate.getTime() - new Date().getTime();
        duration = duration < 0 ? 0 : duration;
        return duration / 86400000;
    }

    private String parseDate(String createdOn) {
        Date date = new Date(Long.parseLong(createdOn.replaceAll("[^\\d]", "")));
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return format.format(date);
    }



    @Override
    public int getItemCount() {
        return mPackages.size();
    }

    public void setActivePackages(List<ActivePackage> activePackages){
        mPackages.clear();
        isExpired = false;
        mPackages.addAll(activePackages);
        this.notifyDataSetChanged();
    }

    public void setExpiredPlans(List<ActivePackage> expiredPackages) {
        mPackages.clear();
        isExpired = true;
        mPackages.addAll(expiredPackages);
        this.notifyDataSetChanged();
    }

    class PlanDetailsViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPlanIcon;
        TextView tvPlanName, tvActivationDate, tvExpiryDate, tvValidity, tvExpiresIn, tvSeePlanDetails, tvRenewUpgrade, tvAddFeatures, tvPlanDetails;
        LinearLayout llPlanDetails;

        public PlanDetailsViewHolder(View itemView) {
            super(itemView);

            ivPlanIcon = (ImageView) itemView.findViewById(R.id.iv_package_icon);
            tvPlanName = (TextView) itemView.findViewById(R.id.tv_package_name);
            tvActivationDate = (TextView) itemView.findViewById(R.id.tv_activation_date);
            tvExpiryDate = (TextView) itemView.findViewById(R.id.tv_expiry_date);
            tvValidity = (TextView) itemView.findViewById(R.id.tv_validity);
            tvExpiresIn = (TextView) itemView.findViewById(R.id.tv_expires_in);
            tvSeePlanDetails = (TextView) itemView.findViewById(R.id.tv_see_plan_details);
            tvRenewUpgrade = (TextView) itemView.findViewById(R.id.tv_renew_upgrade);
            tvAddFeatures = (TextView) itemView.findViewById(R.id.tv_add_features);
            tvPlanDetails = (TextView ) itemView.findViewById(R.id.tv_package_details);
            llPlanDetails = (LinearLayout) itemView.findViewById(R.id.ll_plan_details);

            tvRenewUpgrade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnitemClickInterface!=null){
                        mOnitemClickInterface.onRenewOrUpdate();
                    }
                }
            });

            tvAddFeatures.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnitemClickInterface.onAddFeatures();
                }
            });

        }
    }

    public void setOnItemClickInterface(OnItemClickInterface onItemClickInterface){
        this.mOnitemClickInterface = onItemClickInterface;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.mOnitemClickInterface =  null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public interface OnItemClickInterface {
        public void onRenewOrUpdate();
        public void onAddFeatures();
    }
}
