package com.nowfloats.Store.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Store.Model.PurchaseDetail;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 09-11-2016.
 */

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.MyItemsViewHolder> {

    private List<PurchaseDetail> mPurchaseItems;
    private String mCurrency;

    public ItemsRecyclerViewAdapter(List<PurchaseDetail> purchaseItems, String mCurrency){
        this.mPurchaseItems = purchaseItems;
        this.mCurrency = mCurrency;
    }

    @Override
    public MyItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_items_opc_row_layout, parent, false);
        return new MyItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyItemsViewHolder holder, int position) {
        PurchaseDetail data = mPurchaseItems.get(position);
        holder.tvPlan.setText(data.getPackageName());
        holder.tvQuantity.setText(1 + "");
        holder.tvUnitPrice.setText(mCurrency + " " + data.getBasePrice() + " /-");
        String discountedPrice;
        if(data.getDiscount()==null) {
            holder.tvDiscount.setText("N/A");
            discountedPrice = data.getBasePrice() + "";
        }else {
            holder.tvDiscount.setText(data.getDiscount().value + " %");
            discountedPrice = String.valueOf(Math.round((data.getBasePrice()-(data.getDiscount().value*data.getBasePrice())/100) * 100.0) /100.0);
        }

        holder.tvDiscountedPrice.setText(mCurrency + " " + discountedPrice + " /-");

    }

    @Override
    public int getItemCount() {
        return mPurchaseItems.size();
    }

    public class MyItemsViewHolder extends RecyclerView.ViewHolder{
        TextView tvPlan, tvQuantity, tvUnitPrice, tvDiscount, tvDiscountedPrice;

        public MyItemsViewHolder(View itemView) {
            super(itemView);
            tvPlan = (TextView) itemView.findViewById(R.id.tv_plan);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tv_unit_price);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount_percent);
            tvDiscountedPrice = (TextView) itemView.findViewById(R.id.tv_discount_price);
        }
    }
}
