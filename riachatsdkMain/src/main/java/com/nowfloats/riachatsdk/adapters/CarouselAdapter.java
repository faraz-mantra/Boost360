package com.nowfloats.riachatsdk.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.models.Items;
import com.squareup.picasso.Picasso;

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
    private DisplayMetrics metrics;
    private int leftSpace,topSpace,padding,paddingTop;
    public CarouselAdapter(Context mContext, List<Items> items, Map<String, String> dataMap) {
        this.mContext = mContext;
        itemsList = items;
        metrics = mContext.getResources().getDisplayMetrics();
        this.mDataMap = dataMap;

        leftSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                mContext.getResources().getDisplayMetrics());
        topSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10,
                mContext.getResources().getDisplayMetrics());
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25,
                mContext.getResources().getDisplayMetrics());
        paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23,
                mContext.getResources().getDisplayMetrics());
    }

    @Override
    public CarouselItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_carousel_item_layout, parent, false);
        CarouselItemHolder carouselItemHolder = new CarouselItemHolder(view);
        return carouselItemHolder;
    }

    @Override
    public void onBindViewHolder(CarouselItemHolder carouselItemHolder, int position) {
        LinearLayout.LayoutParams linLayoutParams = new LinearLayout.LayoutParams(metrics.widthPixels * 70 / 100,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayoutParams.setMargins(leftSpace, topSpace, leftSpace, topSpace);
        carouselItemHolder.itemView.setLayoutParams(linLayoutParams);
        carouselItemHolder.itemView.setPadding(padding, paddingTop, padding, padding);

        Items items = itemsList.get(position);
        carouselItemHolder.tvTitle.setText(getParsedPrefixPostfixText(items.getTitle()));
        carouselItemHolder.tvCaption.setText(getParsedPrefixPostfixText(items.getCaption()));


        String imageURL = getParsedPrefixPostfixText(items.getImageUrl());
        carouselItemHolder.ivLogo.setImageResource(R.drawable.site_sc_default);
        /*Glide.with(mContext)
                .load(getParsedPrefixPostfixText(imageURL))
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.site_sc_default))
                .into(carouselItemHolder.ivLogo);*/

        Picasso.get().load(getParsedPrefixPostfixText(getParsedPrefixPostfixText(imageURL)))
                .placeholder(R.drawable.site_sc_default)
                .centerCrop()
                .into(carouselItemHolder.ivLogo);

        carouselItemHolder.imgParentLayout.setBackgroundResource(visibleItemPos == position ? R.drawable.blue_carousel_bg : R.drawable.grey_carousel_bg);
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

    public void notifyVisibleItemChanged(int pos) {
        visibleItemPos = pos;
        notifyDataSetChanged();
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
