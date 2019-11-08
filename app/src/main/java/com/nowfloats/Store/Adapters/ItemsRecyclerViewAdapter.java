package com.nowfloats.Store.Adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Store.Model.PurchaseDetail;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by NowFloats on 09-11-2016.
 */

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.MyItemsViewHolder> {

    private List<PurchaseDetail> mPurchaseItems;
    private String mCurrency;
    private boolean mShowDiscount;

    public ItemsRecyclerViewAdapter(List<PurchaseDetail> purchaseItems, String mCurrency, boolean showDiscount){
        this.mPurchaseItems = purchaseItems;
        this.mCurrency = mCurrency;
        this.mShowDiscount = showDiscount;
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
        holder.tvUnitPrice.setText(mCurrency + " " + NumberFormat.getNumberInstance(Locale.US).format(data.getBasePrice()) + " /-");
        String discountedPrice;
        if(data.getDiscount()==null) {
            holder.tvDiscount.setText("N/A");
            discountedPrice = NumberFormat.getNumberInstance(Locale.US).format(data.getBasePrice());
        }else {
            holder.tvDiscount.setText(data.getDiscount().value + " %");
            discountedPrice = String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round((data.getBasePrice()-(data.getDiscount().value*data.getBasePrice())/100.0) * 100) /100));
        }

        holder.tvDiscountedPrice.setText(mCurrency + " " + discountedPrice + " /-");
        if(mShowDiscount){
            holder.tvDiscount.setVisibility(View.VISIBLE);
            holder.tvDiscountedPrice.setVisibility(View.VISIBLE);
            holder.tvTextDiscountedPrice.setVisibility(View.VISIBLE);
            holder.tvTextDiscount.setVisibility(View.VISIBLE);
        }else {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvDiscountedPrice.setVisibility(View.GONE);
            holder.tvTextDiscountedPrice.setVisibility(View.GONE);
            holder.tvTextDiscount.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mPurchaseItems.size();
    }

    public class MyItemsViewHolder extends RecyclerView.ViewHolder{
        TextView tvPlan, tvQuantity, tvUnitPrice, tvDiscount, tvDiscountedPrice, tvTextDiscountedPrice, tvTextDiscount;

        public MyItemsViewHolder(View itemView) {
            super(itemView);
            tvPlan = (TextView) itemView.findViewById(R.id.tv_plan);
            tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tv_unit_price);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount_percent);
            tvDiscountedPrice = (TextView) itemView.findViewById(R.id.tv_discount_price);
            tvTextDiscountedPrice = (TextView) itemView.findViewById(R.id.tvTextDiscountedPrice);
            tvTextDiscount = (TextView) itemView.findViewById(R.id.tvTextDiscount);
        }
    }
}
