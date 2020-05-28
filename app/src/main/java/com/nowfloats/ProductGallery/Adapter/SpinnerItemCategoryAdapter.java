package com.nowfloats.ProductGallery.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

        if (itemView == null)
        {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item_item_category_option, null /*parent, false*/);
        }

        TextView tvItemType = itemView.findViewById(R.id.label_item_type);
        TextView tvItemDescription = itemView.findViewById(R.id.label_item_description);
        ImageView ivIcon = itemView.findViewById(R.id.iv_icon);

        tvItemType.setText(option.title);
        tvItemDescription.setText(option.body);
        ivIcon.setImageDrawable(ContextCompat.getDrawable(context, option.selected_icon));

        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View itemView =  super.getDropDownView(position, convertView, parent);

        if (position == mSelectedIndex)
        {
            itemView.findViewById(R.id.layout_child).setBackgroundResource(R.drawable.spinner_selected_highlight);
            ((ImageView)(itemView.findViewById(R.id.layout_child).findViewById(R.id.iv_icon))).setImageResource(itemCategoryList.get(position).selected_icon);
        }

        else
        {
            ((ImageView)(itemView.findViewById(R.id.layout_child).findViewById(R.id.iv_icon))).setImageResource(itemCategoryList.get(position).icon);
        }

        return itemView;
    }

    @Override
    public int getCount() {
        return itemCategoryList.size();
    }

    static class ItemCategory {

        String title, body;
        int icon, selected_icon;

        ItemCategory()
        {

        }

        ItemCategory(String title, String body, int icon, int selected_icon)
        {
            this.title = title;
            this.body = body;
            this.icon = icon;
            this.selected_icon = selected_icon;
        }

        public List<ItemCategory> getList()
        {
            List<ItemCategory> paymentOptions = new ArrayList<>();

            paymentOptions.add(new ItemCategory("Physical Product", "Can be packaged and shipped to buyer. E.g. book, watch, toy, garment.", R.drawable.ic_product, R.drawable.ic_product_filled));
            paymentOptions.add(new ItemCategory("Service Offering", "Tasks that are performed by individuals for the benefit of others. E.g. therapy, training, financial consultation.", R.drawable.ic_services, R.drawable.ic_services_filled));

            return paymentOptions;
        }
    }
}