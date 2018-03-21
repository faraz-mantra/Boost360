package com.nowfloats.on_boarding;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.businessApps.FragmentsFactoryActivity;
import com.nowfloats.Product_Gallery.ProductGalleryActivity;
import com.thinksity.R;

/**
 * Created by Admin on 16-03-2018.
 */

public class OnBoardingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mContext;
    private String[] titles, descriptions, buttonText;
    private LinearLayout.LayoutParams linearLayoutParams;
    private int leftSpace, topSpace;
    private DisplayMetrics metrics;
    public OnBoardingAdapter(Context context){
        mContext = context;
        metrics = mContext.getResources().getDisplayMetrics();
        leftSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        topSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, metrics);
        linearLayoutParams = new LinearLayout.LayoutParams(metrics.widthPixels * 80 / 100,
                metrics.heightPixels * 55 / 100);
        linearLayoutParams.setMargins(leftSpace, topSpace, leftSpace, topSpace);
        descriptions = mContext.getResources().getStringArray(R.array.on_boarding_description);
        titles = mContext.getResources().getStringArray(R.array.on_boarding_titles);
        buttonText = mContext.getResources().getStringArray(R.array.on_boarding_btnTexts);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_onboarding_item,parent,false);
                break;

            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.adapter_onboarding_item,parent,false);
                break;

        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder){
            MyViewHolder myHolder = (MyViewHolder) holder;
            myHolder.titleTv.setText(titles[position]);
            myHolder.cardNumberTv.setText(String.valueOf(position+1));
            myHolder.descriptionTv.setText(descriptions[position]);
            myHolder.btnTv.setText(buttonText[position]);
        }
    }

    @Override
    public int getItemCount() {
        return descriptions.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titleTv, cardNumberTv, descriptionTv, btnTv;
        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setLayoutParams(linearLayoutParams);

            titleTv = itemView.findViewById(R.id.tv_title);
            cardNumberTv = itemView.findViewById(R.id.tv_card_number);
            descriptionTv = itemView.findViewById(R.id.tv_description);
            btnTv = itemView.findViewById(R.id.btn_tv);
            btnTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = null;
                    switch (getAdapterPosition()){
                        case 0:
                            return;
                        case 1:
                            intent = new Intent(mContext, FragmentsFactoryActivity.class);
                            intent.putExtra("fragmentName","SiteMeterFragment");
                            break;
                        case 2:
                            intent = new Intent(mContext, CustomPageActivity.class);
                            break;
                        case 3:
                            intent = new Intent(mContext, ProductGalleryActivity.class);
                            break;
                        case 4:
                            break;
                        case 5:
                            intent  = new Intent(mContext, Mobile_Site_Activity.class);
                            intent.putExtra("WEBSITE_NAME","https://www.whatsapp.com/faq/en/android/23130793");
                            break;
                        default:
                            return;
                    }
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
