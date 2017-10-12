package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.customerassistant.callbacks.ThirdPartyCallbacks;
import com.nowfloats.customerassistant.models.SugProducts;
import com.nowfloats.customerassistant.models.SugUpdates;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 11-10-2017.
 */

public class ThirdPartySuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SugUpdates> updateList = new ArrayList<>();
    private List<SugProducts> productList = new ArrayList<>();
    private ListType listType;
    private ArrayList<Integer> checkList = new ArrayList<>();

    public enum ListType{
        PRODUCTS,UPDATES
    }
    public ThirdPartySuggestionAdapter(Context context, ListType listType){
        mContext = context;
        this.listType = listType;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (listType){
            case UPDATES:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_update_third_party_item,parent,false);
                return new MyUpdatesViewHolder(view);
            case PRODUCTS:
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_product_third_party_item,parent,false);
                return new MyProductsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof MyProductsViewHolder){

            MyProductsViewHolder productHolder = (MyProductsViewHolder) holder;
            Glide.with(mContext)
                    .load(productList.get(position).getImage())
                    .into(productHolder.productImage);
            productHolder.productName.setText(productList.get(position).getProductName());
            productHolder.frameLayout.setVisibility(checkList.contains(position) ? View.VISIBLE : View.GONE);

        } else if (holder instanceof MyUpdatesViewHolder){

            MyUpdatesViewHolder updateHolder = (MyUpdatesViewHolder) holder;
            updateHolder.updateName.setText(updateList.get(position).getName());
            updateHolder.frameLayout.setVisibility(checkList.contains(position) ? View.VISIBLE : View.GONE);
        }
    }

    public void sendSuggestions(int type) {
        ((ThirdPartyCallbacks)mContext).addSuggestions(type,checkList);
    }

    public void setProductList(List<SugProducts> list){
        productList = list;
    }
    public void setUpdateList(List<SugUpdates> list){
        updateList = list;
    }
    @Override
    public int getItemCount() {
        return listType == ListType.PRODUCTS ? productList.size() : updateList.size();
    }

    private class MyProductsViewHolder extends RecyclerView.ViewHolder{

        int viewType;
        ImageView productImage;
        TextView productName, productPrice;
        FrameLayout frameLayout;

        public MyProductsViewHolder(View itemView) {
            super(itemView);
            this.viewType = getItemViewType();
            productName = (TextView) itemView.findViewById(R.id.tv_name);
            productPrice = (TextView) itemView.findViewById(R.id.tv_price);
            productImage = (ImageView) itemView.findViewById(R.id.img_product);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.ll_selected);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkList.contains(getAdapterPosition())){
                        checkList.remove((Integer)getAdapterPosition());
                        frameLayout.setVisibility(View.GONE);
                    }else{
                        frameLayout.setVisibility(View.VISIBLE);
                        checkList.add(getAdapterPosition());
                    }
                }
            });
        }
    }

    private class MyUpdatesViewHolder extends RecyclerView.ViewHolder {

        int viewType;
        TextView updateName;
        FrameLayout frameLayout;
        public MyUpdatesViewHolder(View itemView) {
            super(itemView);
            this.viewType = getItemViewType();
            updateName = (TextView) itemView.findViewById(R.id.tv_name);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.ll_selected);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkList.contains(getAdapterPosition())){
                        checkList.remove((Integer)getAdapterPosition());
                        frameLayout.setVisibility(View.GONE);
                    }else{
                        frameLayout.setVisibility(View.VISIBLE);
                        checkList.add(getAdapterPosition());
                    }
                }
            });
        }
    }
}
