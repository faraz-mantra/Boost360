package com.nowfloats.Product_Gallery.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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

    private List<ItemCategory> itemCategoryList;
    private int mSelectedIndex = -1;

    public SpinnerItemCategoryAdapter(Context context)
    {
        this.itemCategoryList = new ItemCategory().getList();
        this.context = context;
    }

    public void setSelection(int position)
    {
        mSelectedIndex =  position;
        notifyDataSetChanged();
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

        if (itemView == null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_item_category_option, null /*parent, false*/);
        }

        TextView tvItemType = itemView.findViewById(R.id.label_item_type);
        TextView tvItemDescription = itemView.findViewById(R.id.label_item_description);
        ImageView ivIcon = itemView.findViewById(R.id.iv_icon);

        tvItemType.setText(option.title);
        tvItemDescription.setText(option.body);
        ivIcon.setImageDrawable(ContextCompat.getDrawable(context, option.icon));

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View itemView =  super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex)
        {
            itemView.findViewById(R.id.layout_child).setBackgroundResource(R.drawable.spinner_selected_highlight);
        }

        /*else
        {
            itemView.setBackgroundColor(Color.TRANSPARENT);
        }*/

        return itemView;
    }

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
            paymentOptions.add(new ItemCategory("Service Offering", "Can be given in person and doesn’t need delivery e.g: Therapy, course, financial consultation.", R.drawable.ic_services));

            return paymentOptions;
        }
    }
}
