package com.nowfloats.Store.Adapters;


import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Store.Model.Screenshots;
import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ViewPagerAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<Screenshots> imagePaths;
    private LayoutInflater inflater;
    int designLayout;

    // constructor
    public ViewPagerAdapter(Activity activity,
                            ArrayList<Screenshots> imagePaths, int layout) {
        this.activity = activity;
        this.imagePaths = imagePaths;
        this.designLayout=layout;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==  object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View viewLayout = inflater.inflate(designLayout, container,
                false);

        try {
            Picasso picasso = Picasso.with(activity);
            ImageViewTouch imgDisplay = (ImageViewTouch) viewLayout.findViewById(R.id.pinchImage);
            String url = Constants.NOW_FLOATS_API_URL  + imagePaths.get(position).imageUri;
            picasso.load(url).into(imgDisplay);
            ((ViewPager) container).addView(viewLayout);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
