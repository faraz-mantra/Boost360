package com.nowfloats.Image_Gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.thinksity.R;

/**
 * Created by Dell on 17-01-2015.
 */
public class GridViewAdapter extends BaseAdapter {

//    private final ImageLoader imgLoader;
    private Context mContext ;
    private LruCache<Integer, NetworkImageView> imageCache;
    private String imageURI ;
    Bitmap imageUploaded ;

    private RequestQueue queue ;
    ImageView imageView;

    public GridViewAdapter(Context c, String imageURI)
    {
        mContext = c ;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);

//        imgLoader= AppController.getInstance().getImageLoader();
        queue = Volley.newRequestQueue(c);
        this.imageURI = imageURI ;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View grid = inflater.inflate(R.layout.grid_view_list_item, parent, false);

        if (convertView == null) {

            imageView = (ImageView)grid.findViewById(R.id.grid_Image);
            imageView.setImageBitmap(imageUploaded);

            //imageCache.put(1,imageView);

            //imageView.setImageUrl(picimageURI,imgLoader);

        } else {
            grid = (View) convertView;
        }
        return grid;
    }

//    @Override
//    public void uploadedPictureListener(String imageURL) {
//
//        imageUploaded = BitmapFactory.decodeFile(imageURL);
//        //mImage.setImageBitmap(img);
//
//    }
}
