package com.nowfloats.Store.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.Store.Model.ActivePackage;
import com.nowfloats.Store.Model.InvoiceDetailsResult;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.TopUpPlansActivity;
import com.nowfloats.Store.YourPurchasedPlansActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.nowfloats.Store.YourPurchasedPlansActivity.PlansType.YOUR_ORDERS;

/**
 * Created by Admin on 01-02-2018.
 */

public class PurchasedPlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private YourPurchasedPlansActivity.PlansType mPlansType;
    private ArrayList<ActivePackage> purchasePlans;
    private ArrayList<InvoiceDetailsResult> planOrderItems;

    public PurchasedPlanAdapter(Context context, YourPurchasedPlansActivity.PlansType planType) {
        mContext = context;
        mPlansType = planType;
    }

    public void setPlansList(ArrayList<ActivePackage> plans) {
        purchasePlans = plans;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == YOUR_ORDERS.ordinal()) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_plan_orders, parent, false);
            return new MyPurchasePlanOrdersHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_active_plans, parent, false);
            return new MyPurchasePlansHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myHolder, int position) {
        if (myHolder instanceof MyPurchasePlansHolder && purchasePlans != null && !purchasePlans.isEmpty()) {
            ((MyPurchasePlansHolder) myHolder).setActiveOrExpiredHolder(purchasePlans.get(position));
        }

        if (myHolder instanceof MyPurchasePlanOrdersHolder && purchasePlans != null && !purchasePlans.isEmpty()) {

            MyPurchasePlanOrdersHolder holder = (MyPurchasePlanOrdersHolder) myHolder;
            ActivePackage activePackage = purchasePlans.get(position);
            holder.tvSerialNumber.setTypeface(null, Typeface.NORMAL);
            holder.tvPaymentDate.setTypeface(null, Typeface.NORMAL);
            holder.tvOrderId.setTypeface(null, Typeface.NORMAL);
            holder.tvPackageName.setTypeface(null, Typeface.NORMAL);
            holder.tvTotalAmount.setTypeface(null, Typeface.NORMAL);
            holder.tvCliamId.setTypeface(null, Typeface.NORMAL);
            holder.tvPaymentStatus.setTypeface(null, Typeface.NORMAL);
            holder.tvSerialNumber.setText(position + " ");
//            holder.tvPaymentDate.setText(Methods.getFormattedDate(activePackage.getPaymentDate(), "dd-MM-yyyy"));
//            holder.tvPaymentDate.setText(Methods.getFormattedDateFromFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd-MM-yyyy",activePackage.getPaymentDate()));
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

            Date date = null;
            try {
                date = input.parse(activePackage.getPaymentDate());
                holder.tvPaymentDate.setText(output.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvOrderId.setText(activePackage.getId());
            if (activePackage.getPackageDetails() != null && !activePackage.getPackageDetails().isEmpty()) {
                holder.tvPackageName.setText(activePackage.getPackageDetails().get(0).getPackageName());
                holder.tvTotalAmount.setText(activePackage.getCurrencyCode() + " " + activePackage.getPackageDetails().get(0).getNetPackagePrice());
            }
            holder.tvCliamId.setText(activePackage.getClaimid() != null ? activePackage.getClaimid() : " ");
            holder.tvPaymentStatus.setText(activePackage.getPaymentStatus() < 0 ? "EXPIRED" : activePackage.getPaymentStatus() == 0 ? "DEMO"
                    : activePackage.getPaymentStatus() > 0 ? "PAID" : " ");

        }
    }

    @Override
    public int getItemCount() {
        return purchasePlans != null ? purchasePlans.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mPlansType.ordinal();
    }

    class MyPurchasePlanOrdersHolder extends RecyclerView.ViewHolder {

        private final TextView tvSerialNumber;
        private final TextView tvPaymentDate;
        private final TextView tvOrderId;
        private final TextView tvPackageName;
        private final TextView tvTotalAmount;
        private final TextView tvCliamId;
        private final TextView tvPaymentStatus;

        public MyPurchasePlanOrdersHolder(View itemView) {
            super(itemView);
            tvSerialNumber = itemView.findViewById(R.id.tv_serial_number);
            tvPaymentDate = itemView.findViewById(R.id.tv_payment_date);
            tvOrderId = itemView.findViewById(R.id.tv_order_confirmation_id);
            tvPackageName = itemView.findViewById(R.id.tv_package_name);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvCliamId = itemView.findViewById(R.id.tv_claim_id);
            tvPaymentStatus = itemView.findViewById(R.id.tv_payment_status);
        }
    }

    class MyPurchasePlansHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView validityTv, visibleDetails, detailsTv, numberTv, statusTv, dateTv, renewTv, topUpTv;
        private ImageView planImV, topUpImV;
        private ActivePackage activePlan;
        private View mImagDivider;

        MyPurchasePlansHolder(View itemView) {
            super(itemView);
            mImagDivider = itemView.findViewById(R.id.view_divider);
            planImV = itemView.findViewById(R.id.img_plan);
            topUpImV = itemView.findViewById(R.id.img_top_up);
            validityTv = itemView.findViewById(R.id.tv_validity);
            detailsTv = itemView.findViewById(R.id.tv_details);
            numberTv = itemView.findViewById(R.id.tv_number);
            statusTv = itemView.findViewById(R.id.tv_status);
            dateTv = itemView.findViewById(R.id.tv_date);
            visibleDetails = itemView.findViewById(R.id.tv_visible_details);
            detailsTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | detailsTv.getPaintFlags());
            detailsTv.setOnClickListener(this);
            renewTv = itemView.findViewById(R.id.tv_renew_upgrade);
//            renewTv.setOnClickListener(this);
            topUpTv = itemView.findViewById(R.id.tv_top_up);
//            topUpTv.setOnClickListener(this);
        }

        public void setActiveOrExpiredHolder(ActivePackage activePlan) {
            this.activePlan = activePlan;
            renewTv.setVisibility(View.VISIBLE);
            if (activePlan.getProductClassification() != null) {

                if (activePlan.getProductClassification().getPackType() == 1) {
                    planImV.setVisibility(View.GONE);
                    mImagDivider.setVisibility(View.GONE);
                    validityTv.setText(Methods.fromHtml(String.format("<b><font color=%s>%s</font></b>",
                            ContextCompat.getColor(mContext, R.color.gray), activePlan.getName())));
                    topUpImV.setVisibility(View.VISIBLE);
                    topUpTv.setVisibility(View.INVISIBLE);
                } else {

                    topUpTv.setVisibility(BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats") ? View.VISIBLE : View.INVISIBLE);
                    Glide.with(mContext).load(activePlan.getPrimaryImageUri()).into(planImV);
                    if (activePlan.getName().toLowerCase().contains("lite")) {
                        //planImV.setImageResource(R.drawable.boost_lite_logo);
                    } else if (activePlan.getName().toLowerCase().contains("pro")) {
                        //planImV.setImageResource(R.drawable.boost_pro_logo);
                    } else if (activePlan.getName().toLowerCase().contains("light")) {
                        //planImV.setImageResource(R.drawable.lighthouse);
                        renewTv.setVisibility(View.INVISIBLE);
                    }
                    mImagDivider.setVisibility(View.VISIBLE);
                    planImV.setVisibility(View.VISIBLE);
                    topUpImV.setVisibility(View.GONE);
                    validityTv.setText(Methods.fromHtml(String.format("Validity: <b><font color=%s>%s</font></b>",
                            ContextCompat.getColor(mContext, R.color.gray), activePlan.getTotalMonthsValidity().intValue() + " Months")));
                }
                if (activePlan.isExpanded()) {
                    detailsTv.setText("Hide Plan Details");
                    visibleDetails.setVisibility(View.VISIBLE);
                } else {
                    detailsTv.setText("Show Plan Details");
                    visibleDetails.setVisibility(View.GONE);
                }

                visibleDetails.setText(activePlan.getFeatures());
                switch (activePlan.getActiveStatus()) {
                    case "Expired":
                        statusTv.setText(Methods.fromHtml(String.format("Status: <font color=%s>%s</font>",
                                ContextCompat.getColor(mContext, R.color.red), activePlan.getActiveStatus())));
                        break;
                    case "Not Active":
                    case "Active":
                    default:
                        statusTv.setText(Methods.fromHtml(String.format("Status: <font color=%s>%s</font>",
                                ContextCompat.getColor(mContext, R.color.gray), activePlan.getActiveStatus())));
                        break;
                }
                numberTv.setText(Methods.fromHtml(String.format("Invoice Number: <font color=%s>%s</font>",
                        ContextCompat.getColor(mContext, R.color.gray), TextUtils.isEmpty(activePlan.getNfInternalERPId()) ? "NA" : activePlan.getNfInternalERPId())));
                dateTv.setText(Methods.fromHtml(String.format("Activated on: <font color=%s>%s</font>",
                        ContextCompat.getColor(mContext, R.color.gray), Methods.getFormattedDate(activePlan.getToBeActivatedOn(), "dd MMM yyyy"))));

            }
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_details:
                    if (!activePlan.isExpanded()) {
                        detailsTv.setText("Hide Plan Details");
                        visibleDetails.setVisibility(View.VISIBLE);
                        activePlan.setExpanded(true);
                    } else {
                        detailsTv.setText("Show Plan Details");
                        visibleDetails.setVisibility(View.GONE);
                        activePlan.setExpanded(false);
                    }
                    break;
                case R.id.tv_top_up:
                    showTopUps();
                    break;
                case R.id.tv_renew_upgrade:
                    showRenewPlans();
                    break;
            }
        }

        private void showRenewPlans() {
            if (activePlan.getProductClassification() != null
                    && activePlan.getProductClassification().getPackType() == 0) {
                Intent i = new Intent(mContext, NewPricingPlansActivity.class);
                i.putExtra("fragmentName", "BasePlans");
                mContext.startActivity(i);
            } else if (!Constants.PACKAGE_NAME.equals("com.redtim")) {
                showTopUps();
            }
        }

        private void showTopUps() {
            Intent i = new Intent(mContext, TopUpPlansActivity.class);
            mContext.startActivity(i);
        }
    }
}