package com.nowfloats.Store.Adapters;

import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.Store.Model.OPCModels.OPCItems;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 09-11-2016.
 */

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.MyItemsViewHolder> {

    private List<OPCItems> mOPCItems;
    private String mCurrency;

    public ItemsRecyclerViewAdapter(List<OPCItems> mOPCItems, String mCurrency){
        this.mOPCItems = mOPCItems;
        this.mCurrency = mCurrency;
    }

    @Override
    public MyItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_items_opc_row_layout, parent, false);
        return new MyItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyItemsViewHolder holder, int position) {
        OPCItems data = mOPCItems.get(position);
        holder.tvPlan.setText(data.packagename);
        holder.tvQuantity.setText(data.quantity + "");
        holder.tvUnitPrice.setText(mCurrency + " " + data.price + " /-");
        holder.tvDiscount.setText(data.discount + " %");
        holder.tvDiscountedPrice.setText(mCurrency + " " + data.amount + " /-");

    }

    @Override
    public int getItemCount() {
        return mOPCItems.size();
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
