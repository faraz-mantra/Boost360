package com.nowfloats.customerassistant.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import static com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity.ADD_PRODUCTS;
import static com.nowfloats.customerassistant.ThirdPartySuggestionDetailActivity.ADD_UPDATES;

/**
 * Created by Admin on 11-10-2017.
 */

public class ThirdPartySuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<SugUpdates> updateList = new ArrayList<>();
    private List<SugProducts> productList = new ArrayList<>();
    private ListType listType;
    ThirdPartyFragment callback;
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
            if(!TextUtils.isEmpty(updateList.get(position).getImage())){
                updateHolder.updateImage.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(updateList.get(position).getImage())
                        .into(updateHolder.updateImage);
            }else{
                updateHolder.updateImage.setVisibility(View.GONE);
            }

            updateHolder.updateName.setText(updateList.get(position).getName());
            updateHolder.frameLayout.setVisibility(checkList.contains(position) ? View.VISIBLE : View.GONE);
        }
    }

    public void sendSuggestions(int type) {
        ((ThirdPartyCallbacks)mContext).addSuggestions(type,checkList);
    }

    public void setProductList(List<SugProducts> list,ThirdPartyFragment callback){
        productList = list;
        this.callback = callback;
    }
    public void setUpdateList(List<SugUpdates> list,ThirdPartyFragment callback){
        updateList = list;
        this.callback = callback;
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
                    callback.itemSelected(ADD_PRODUCTS, checkList.size());
                }
            });
        }
    }

    private class MyUpdatesViewHolder extends RecyclerView.ViewHolder {

        int viewType;
        TextView updateName;
        FrameLayout frameLayout;
        ImageView updateImage;
        public MyUpdatesViewHolder(View itemView) {
            super(itemView);
            this.viewType = getItemViewType();
            updateName = (TextView) itemView.findViewById(R.id.tv_name);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.ll_selected);
            updateImage = (ImageView) itemView.findViewById(R.id.imageView);
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
                    callback.itemSelected(ADD_UPDATES,checkList.size());
                }
            });
        }
    }

    public interface ThirdPartyFragment{
        void itemSelected(int type,int num);
    }
}
