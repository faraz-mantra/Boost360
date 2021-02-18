package com.nowfloats.hotel.seasonalOffers.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nowfloats.hotel.API.model.GetOffers.Data;
import com.nowfloats.hotel.Interfaces.SeasonalOffersListener;
import com.thinksity.R;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

public class SeasonalOffersAdapter extends RecyclerView.Adapter<SeasonalOffersAdapter.ViewHolder> {

    private List<Data> itemList;
    private SeasonalOffersListener listener;
    private int menuPosition = -1;
    private boolean menuStatus = false;
    private Context context;

    public SeasonalOffersAdapter(List<Data> data, SeasonalOffersListener listener) {
        this.itemList = data;
        this.listener = listener;
    }


    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seasonal_offers, null);
        context = v.getContext();
        return new ViewHolder(v);
    }

    public void menuOption(int pos, boolean status) {
        menuPosition = pos;
        menuStatus = status;
    }

    public void updateList(List<Data> list){
        itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.menuOptionLayout.setVisibility(View.GONE);
        if (menuPosition == position) {
            if (menuStatus) {
                holder.menuOptionLayout.setVisibility(View.VISIBLE);
            } else {
                holder.menuOptionLayout.setVisibility(View.GONE);
            }
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemMenuOptionStatus(position, false);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.menuOptionLayout.getVisibility() == View.GONE) {
                    listener.itemMenuOptionStatus(position, true);
//                    holder.menuOptionLayout.setVisibility(View.VISIBLE);
                } else {
                    listener.itemMenuOptionStatus(position, false);
//                    holder.menuOptionLayout.setVisibility(View.GONE);
                }
            }
        });

        holder.editOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editOptionClicked(itemList.get(position));
            }
        });

        holder.deleteOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteOptionClicked(itemList.get(position));
            }
        });

        holder.offerTitle.setText(itemList.get(position).getOfferTitle());
        if(itemList.get(position).getDiscountedPrice() != itemList.get(position).getOrignalPrice()){
            SpannableString content = new SpannableString("Rs."+ itemList.get(position).getOrignalPrice());
            content.setSpan(new StrikethroughSpan(), 0, itemList.get(position).getOrignalPrice().toString().length()+3, 0);
            holder.mrpPrice.setText(content);
            holder.mrpPrice.setVisibility(View.VISIBLE);
        }else{
            holder.mrpPrice.setVisibility(View.GONE);
        }
        holder.offerPrice.setText( "Rs."+ itemList.get(position).getDiscountedPrice());
        holder.offerDescription.setText(itemList.get(position).getOfferImage().getDescription());
        double discount = (itemList.get(position).getOrignalPrice() - itemList.get(position).getDiscountedPrice()) / (double)(itemList.get(position).getOrignalPrice());
        discount *= 100.0;
        holder.offerDiscount.setText(new DecimalFormat("##.##").format(discount)+ "% Flat Discount");

        Glide.with(context)
                .load(itemList.get(position).getOfferImage().getUrl())
                .into(holder.offerImage);

        holder.dummyView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        holder.dummyView1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView menuButton, offerImage;
        LinearLayout menuOptionLayout;
        ConstraintLayout mainLayout;
        TextView offerTitle, offerPrice, mrpPrice, offerDescription, offerDiscount, editOption, deleteOption;
        View dummyView, dummyView1;

        public ViewHolder(View itemView) {
            super(itemView);

            offerTitle = itemView.findViewById(R.id.offer_title);
            offerPrice = itemView.findViewById(R.id.offer_price);
            mrpPrice = itemView.findViewById(R.id.mrp_price);
            offerDescription = itemView.findViewById(R.id.offer_description);
            offerDiscount = itemView.findViewById(R.id.discount_percentage);
            offerImage = (ImageView) itemView.findViewById(R.id.offer_profile_image);
            menuButton = (ImageView) itemView.findViewById(R.id.single_item_menu_button);
            menuOptionLayout = (LinearLayout) itemView.findViewById(R.id.menu_options);
            mainLayout = (ConstraintLayout) itemView.findViewById(R.id.main_layout);
            editOption = itemView.findViewById(R.id.edit_option);
            deleteOption = itemView.findViewById(R.id.delete_option);
            dummyView = itemView.findViewById(R.id.dummy_view);
            dummyView1 = itemView.findViewById(R.id.dummy_view1);
        }
    }
}
