package com.nowfloats.managecustomers.adapters;

import android.annotation.SuppressLint;
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

import com.thinksity.R;

/**
 * Created by Admin on 02-08-2017.
 */

public class EnquiryCarouselAdapter extends RecyclerView.Adapter<EnquiryCarouselAdapter.CarouselItemHolder> {

    private Context mContext;
    private int visibleItemPos = 0;
    private DisplayMetrics metrics;
    private int leftSpace, topSpace, padding, paddingTop;

    public EnquiryCarouselAdapter(Context mContext) {
        this.mContext = mContext;
        metrics = mContext.getResources().getDisplayMetrics();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.enquiry_carousel_item_layout, parent, false);
        CarouselItemHolder carouselItemHolder = new CarouselItemHolder(view);
        return carouselItemHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(CarouselItemHolder carouselItemHolder, int position) {
        LinearLayout.LayoutParams linLayoutParams = new LinearLayout.LayoutParams(metrics.widthPixels * 70 / 100,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        linLayoutParams.setMargins(leftSpace, topSpace, leftSpace, topSpace);
        carouselItemHolder.itemView.setLayoutParams(linLayoutParams);
        carouselItemHolder.itemView.setPadding(padding, paddingTop, padding, padding);

        carouselItemHolder.tvTitle.setText("Enquiry " + position);
        carouselItemHolder.tvCaption.setText("Caption " + position);


        carouselItemHolder.ivLogo.setImageResource(R.drawable.site_sc_default);
        carouselItemHolder.imgParentLayout.setBackgroundColor(visibleItemPos == position ? R.color.blue : R.color.gray);
    }


    public void notifyVisibleItemChanged(int pos) {
        visibleItemPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return 5;
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
