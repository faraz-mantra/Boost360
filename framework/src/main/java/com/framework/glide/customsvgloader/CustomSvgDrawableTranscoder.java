package com.framework.glide.customsvgloader;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;
import com.caverock.androidsvg.SVG;

import org.jetbrains.annotations.NotNull;

public class CustomSvgDrawableTranscoder implements ResourceTranscoder<SvgCustomDataModel, CustomPictureDrawable> {

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public Resource<CustomPictureDrawable> transcode(@NonNull @NotNull Resource<SvgCustomDataModel> toTranscode, @NonNull @NotNull Options options) {
        Log.d("CustomSvgTranscoder", "transcode() called with: toTranscode");
        CustomPictureDrawable drawable = new CustomPictureDrawable(null);
        drawable.svgModel = toTranscode.get();
        return new SimpleResource(drawable);
    }
}