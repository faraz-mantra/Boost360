package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.thinksity.R;

/**
 * Created by Admin on 03-04-2017.
 */

public class SpinnerAdapter extends BaseAdapter {
    int[] images;
    Context mContext;
    SpinnerAdapter(Context context,int[] images){
        this.images = images;
        mContext = context;
    }
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_spinner_image_item,parent,false);
        }
        ImageView image = (ImageView) convertView.findViewById(R.id.image_parent);
        image.setImageResource(images[position]);

        return convertView;
    }

}
