package com.nowfloats.riachatsdk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Items;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 02-08-2017.
 */

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselItemHolder> {

    private List<Items> itemsList;
    private Map<String, String> mDataMap;
    private Context mContext;
    private int visibleItemPos = 0;

    public CarouselAdapter(Context mContext, List<Items> items, Map<String, String> dataMap) {
        this.mContext = mContext;
        itemsList = items;
        this.mDataMap = dataMap;
    }

    @Override
    public CarouselItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_carousel_item_layout, parent, false);
        return new CarouselItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CarouselItemHolder carouselItemHolder, int position) {
        Items items = itemsList.get(position);
        carouselItemHolder.tvTitle.setText(getParsedPrefixPostfixText(items.getTitle()));
        carouselItemHolder.tvCaption.setText(getParsedPrefixPostfixText(items.getCaption()));

        String imageURL = getParsedPrefixPostfixText(items.getImageUrl());
        carouselItemHolder.ivLogo.setImageResource(R.drawable.site_sc_default);
        Glide.with(mContext)
                .load(getParsedPrefixPostfixText(imageURL))
                .centerCrop()
                .placeholder(R.drawable.site_sc_default)
                .into(carouselItemHolder.ivLogo);

        carouselItemHolder.imgParentLayout.setBackgroundResource(visibleItemPos == position?R.drawable.blue_carousel_bg : R.drawable.grey_carousel_bg);
    }

    private String getParsedPrefixPostfixText(String text) {
        if (text == null)
            return null;
        Matcher m = Pattern.compile("\\[~(.*?)\\]").matcher(text);
        while (m.find()) {
            if (mDataMap.get(m.group()) != null) {
                text = text.replace(m.group(), mDataMap.get(m.group()));
            }
        }
        return text;
    }

    public void notifyVisibleItemChanged(int pos){
        visibleItemPos = pos;
        notifyItemChanged(pos);
    }
    @Override
    public int getItemCount() {
        if (itemsList != null && itemsList.size() > 0)
            return itemsList.size();
        return 0;
    }

    class CarouselItemHolder extends RecyclerView.ViewHolder {

        ImageView ivLogo;
        TextView tvTitle, tvCaption;
        LinearLayout imgParentLayout;
        public CarouselItemHolder(View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.img_logo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvCaption = (TextView) itemView.findViewById(R.id.tv_caption);
            imgParentLayout = (LinearLayout) itemView.findViewById(R.id.ll_img);
        }
    }
}
