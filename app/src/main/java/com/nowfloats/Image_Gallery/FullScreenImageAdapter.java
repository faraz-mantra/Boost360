package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Dell on 10-02-2015.
 */
public class FullScreenImageAdapter extends PagerAdapter {
    private final LayoutInflater inflater;
    Activity context;
    ArrayList<String> imagesList = null;
    int size = 0;

    public interface ImageAdapter_interface {
        public void ImageDeleted();
    }

    //  ImageAdapter_interface imageInterface ;

    public FullScreenImageAdapter(Activity context) {

        imagesList = new ArrayList<>();
        imagesList.add("https://s3.ap-south-1.amazonaws.com/boost-content-cdn/Boost+Android+Assets/Boost+Bubble_v2.gif");
        imagesList.add("https://s3.ap-south-1.amazonaws.com/boost-content-cdn/Boost+Android+Assets/Boost+Bubble_v1.gif");

        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("ImageAdapter", "instantiateItem : Position " + position);
        ImageView imageView = new ImageView(context);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);

        String serverImage = imagesList.get(position);
        Log.d("ImageAdapter", "server position : " + serverImage);
        String baseName = serverImage;

        Glide.with(context)
                .load(serverImage)
                .asGif()
                .placeholder(R.drawable.default_product_image)
                .crossFade()
                .into(imageView);
        container.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    public void delete(int deletePosition) {

        //DeleteGalleryImages task = new DeleteGalleryImages((android.app.Activity) context,this,deletePosition);
        //task.execute();

    }


}
