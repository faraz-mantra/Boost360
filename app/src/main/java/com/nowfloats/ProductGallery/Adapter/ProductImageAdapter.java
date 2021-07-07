package com.nowfloats.ProductGallery.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.darsh.multipleimageselect.models.Image;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.List;

/**
 * Created by NowFloats on 02-09-2016.
 */
public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.MyViewHolder> {

    private List<Image> mImages;
    private Context mContext;
    private ItemClickListener mClickListener;

    public ProductImageAdapter(List<Image> images) {
        this.mImages = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View v = LayoutInflater.from(mContext).inflate(R.layout.carousel_itm, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String path = mImages.get(position).path;
        //holder.ivProductImg.setImageDrawable(null);
        Log.d("path:", path);
        if (path.contains("https://")) {
            Picasso.get().load(path)
                    .placeholder(R.drawable.post_update_normal_icon)
                    .resize(0, 500).into(holder.ivProductImg);
        } else {
            Picasso.get().load("file://" + path)
                    .placeholder(R.drawable.post_update_normal_icon)
                    .resize(0, 500).into(holder.ivProductImg);
        }
        //Picasso.with(mContext).load(Uri.parse("/storage/emulated/0/Pictures/1472719977655.jpg")).into();
        //holder.tvImageName.setText(mImages.get(position).name);
    }


    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.mClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivProductImg;
        //public TextView tvImageName;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivProductImg = (ImageView) itemView.findViewById(R.id.iv_product_img);
            itemView.setOnClickListener(this);
            //tvImageName = (TextView) itemView.findViewById(R.id.tv_image_name);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null) {
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
