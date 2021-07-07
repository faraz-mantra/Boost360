package com.nowfloats.ProductGallery.Adapter;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NowFloats on 17-04-2018.
 */

public class ProductImageListAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<ProductImageResponseModel> mList = new ArrayList<>();

    public ProductImageListAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addImages(List<ProductImageResponseModel> imageList) {
        mList.addAll(imageList);
        this.notifyDataSetChanged();
    }

    public void addImage(ProductImageResponseModel image) {
        mList.add(image);
        this.notifyDataSetChanged();
    }

    public void removeImage(int position) {
        mList.remove(position);
        this.notifyDataSetChanged();
    }

    public ProductImageResponseModel get(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public ArrayList<ProductImageResponseModel> getImages() {
        return (ArrayList<ProductImageResponseModel>) mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.product_images_pager_layout, container, false);
        ImageView productImage = view.findViewById(R.id.iv_product_image);
        if (mList.get(position).getImage().url.startsWith("http")) {
            Picasso.get().load(mList.get(position).getImage().url).into(productImage);
        } else {
            Uri uri = Uri.fromFile(new File(mList.get(position).getImage().url));
            Picasso.get().load(uri).into(productImage);
        }
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }


}
