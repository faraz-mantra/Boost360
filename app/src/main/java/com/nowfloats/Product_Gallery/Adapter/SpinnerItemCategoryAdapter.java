package com.nowfloats.Product_Gallery.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerItemCategoryAdapter extends BaseAdapter
{
    private Context context;

    List<ItemCategory> itemCategoryList;

    public SpinnerItemCategoryAdapter(Context context)
    {
        this.itemCategoryList = new ItemCategory().getList();
        this.context = context;
    }

    @Override
    public Object getItem(int pos)
    {
        return itemCategoryList.get(pos);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @NonNull
    public View getView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return initView(position, itemView, parent);
    }


    private View initView(int position, View itemView, ViewGroup parent) {

        ItemCategory option = itemCategoryList.get(position);

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_item_category_option, null /*parent, false*/);

            TextView tvItemType = itemView.findViewById(R.id.label_item_type);
            TextView tvItemDescription = itemView.findViewById(R.id.label_item_description);
            ImageView ivIcon = itemView.findViewById(R.id.iv_icon);

            tvItemType.setText(option.title);
            tvItemDescription.setText(option.body);
            ivIcon.setImageDrawable(ContextCompat.getDrawable(context, option.icon));
        }


        /*if (parent == null)
        {
            itemView = LayoutInflater.from(painitViewrent.getContext()).inflate(R.layout.spinner_item_dynamic, null);
        }

        else
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_dynamic, parent, false);
        }*/

        /*CircleImageView thumbnail = itemView.findViewById(R.id.patient_image);
        TextView textView = itemView.findViewById(R.id.patient_name);

        if(position == (mList.size() - 1) && mList.get(position) == null)
        {
            textView.setText(String.valueOf("Add New"));
            thumbnail.setImageResource(R.drawable.ic_plus);
            return itemView;
        }

        else
        {
            textView.setText(Helper.toCamelCase(patient.getFullName()));

            try
            {
                if(!patient.getProfilePic().isEmpty())
                {
                    Glide.with(context)
                            .load(patient.getProfilePic())
                            .placeholder(R.drawable.anonymous)
                            .dontAnimate()
                            .override(50, 50)
                            .centerCrop()
                            .into(thumbnail);
                }

                else
                {
                    Glide.with(context)
                            .load(R.drawable.anonymous)
                            .placeholder(R.drawable.anonymous)
                            .dontAnimate()
                            .override(50, 50)
                            .centerCrop()
                            .into(thumbnail);
                }
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/

        return itemView;

        //return LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_payment_configuration_option, null /*parent, false*/);
    }

    /*@Override
    public View getDropDownView(int position, View itemView, @NonNull ViewGroup parent)
    {
        return getView(position, itemView, parent);
    }*/

    @Override
    public int getCount() {
        return itemCategoryList.size();
    }

    /*public void setItems(int count)
    {
        if(count < 4)
        {
            this.mList.add(null);
        }

        notifyDataSetChanged();
    }*/

    static class ItemCategory {

        String title, body;
        int icon;

        ItemCategory()
        {

        }

        ItemCategory(String title, String body, int icon)
        {
            this.title = title;
            this.body = body;
            this.icon = icon;
        }

        public List<ItemCategory> getList()
        {
            List<ItemCategory> paymentOptions = new ArrayList<>();

            paymentOptions.add(new ItemCategory("Physical Product", "Can be packaged and delivered to buyer. e.g. book, watch, toy, garment etc.", R.drawable.ic_product));
            paymentOptions.add(new ItemCategory("Service Offering", "Can be given in person and doesn’t need delivery e.g: Therapy, course, financial consultation.", R.drawable.ic_service));

            return paymentOptions;
        }
    }
}
