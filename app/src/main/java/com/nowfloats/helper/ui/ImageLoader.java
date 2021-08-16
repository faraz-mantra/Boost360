package com.nowfloats.helper.ui;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageLoader {

    public static void load(Context context, String url, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(Context context, File path, ImageView imageView) {
        try {
            Glide.with(context)
                    .load(path)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}