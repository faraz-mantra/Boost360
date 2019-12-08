package com.nowfloats.Store.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.nowfloats.Store.Model.PackageDetails;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NowFloats on 21-10-2017.
 */

public class TopUpDialogRvAdapter extends RecyclerView.Adapter<TopUpDialogRvAdapter.CheckBoxViewHolder> {

    private List<PackageDetails> mTopUpPlans;

    private double mTotalAmount = 0.0;
    private UpdateAmountListener mUpdateAmountListener;
    private List<String> mPackageIds = new ArrayList<>();

    public TopUpDialogRvAdapter(List<PackageDetails> topUpPlans, UpdateAmountListener updateAmountListener){
        this.mTopUpPlans = topUpPlans;
        this.mUpdateAmountListener = updateAmountListener;
    }

    @Override
    public CheckBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CheckBoxViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.top_dialog_row_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckBoxViewHolder holder, int position) {
        final PackageDetails packageDetail = mTopUpPlans.get(position);
        holder.cbTopUpPlan.setText(packageDetail.getName());
        holder.tvPrice.setText(packageDetail.getCurrencyCode() + " " + packageDetail.getPrice());
        holder.cbTopUpPlan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mTotalAmount += packageDetail.getPrice();
                    mPackageIds.add(packageDetail.getId());
                } else {
                    mTotalAmount -= packageDetail.getPrice();
                    mPackageIds.remove(packageDetail.getId());
                }

                mUpdateAmountListener.onAmountUpdated(mTotalAmount, mPackageIds);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTopUpPlans.size();
    }

    class CheckBoxViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbTopUpPlan;
        TextView tvPrice;

        public CheckBoxViewHolder(View itemView) {
            super(itemView);

            cbTopUpPlan = (CheckBox) itemView.findViewById(R.id.cb_top_up_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
        }
    }

    public interface UpdateAmountListener {
        void onAmountUpdated(double amount, List<String> packageIds);
    }
}
