package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowfloats.util.Constants;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by Dell on 10-02-2015.
 */
public class ImageAdapter extends PagerAdapter{
    private final LayoutInflater inflater;
    Activity context;
    ArrayList<String> imagesList = null;
    int size = 0;

    public interface ImageAdapter_interface {
        public void ImageDeleted();
    }

  //  ImageAdapter_interface imageInterface ;

    public ImageAdapter(Activity context){
        this.context=context;
      //  imageInterface = (ImageAdapter_interface) context;
        if (Constants.storeActualSecondaryImages != null) {
            imagesList = Constants.storeActualSecondaryImages;
            size = imagesList.size();

        }
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
        Log.d("ImageAdapter","instantiateItem : Position "+position);
        ImageView imageView = new ImageView(context);
//        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
//        imageView.setPadding(padding, padding, padding, padding);
      //  imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
       // imageView.setImageResource(imagesList.get(position));

        String serverImage = imagesList.get(position);
        Log.d("ImageAdapter","server position : "+serverImage);
        //Log.d(TAG, "Server Image : "+serverImage);
        String baseName = serverImage;
        if(serverImage!=null && serverImage.length()>0 && !serverImage.equals("null")) {
            if (!serverImage.contains("http")) {
                if (!serverImage.contains("Android")) {
                    baseName = Constants.BASE_IMAGE_URL+"" + serverImage;
                    Log.d("ImageAdapter", "Base Name : " + baseName);
                    //            imageLoader.displayImage(baseName, imageView, options);
                } else {
                    //baseName = imagesList.get(position).substring(imagesList.get(position).lastIndexOf('/')+1).trim();
                    //baseName = OtherImagesFragment.imageUrl;
                    //            imageLoader.displayImage("file://"+	baseName, imageView, options);
                }
            } else {
                baseName = serverImage;
            }
            Picasso.get().load(baseName).placeholder(R.drawable.default_product_image).into(imageView);
            container.addView(imageView, 0);
        }
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    public void delete(int deletePosition)
    {

        //DeleteGalleryImages task = new DeleteGalleryImages((android.app.Activity) context,this,deletePosition);
        //task.execute();

    }


}
