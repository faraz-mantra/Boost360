package com.framework.glide.customsvgloader;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.List;

public class SvgDrawableListener implements RequestListener<CustomPictureDrawable> {
    public List<PosterKeyModel> model;
    public String url;

    public SvgDrawableListener() {

    }

    public SvgDrawableListener(List<PosterKeyModel> model, String url) {
        this.model = model;
        this.url = url;
    }

    @Override
    public boolean onLoadFailed(
            GlideException e, Object model, Target<CustomPictureDrawable> target, boolean isFirstResource) {
//    ImageView view = ((ImageViewTarget<?>) target).getView();
//    view.setLayerType(ImageView.LAYER_TYPE_NONE, null);
        return false;
    }

    @Override
    public boolean onResourceReady(
            CustomPictureDrawable resource,
            Object model,
            Target<CustomPictureDrawable> target,
            DataSource dataSource,
            boolean isFirstResource) {
        Log.d("SvgDrawableListener", "onResourceReady() called with: resource ");
        ImageView view = ((ImageViewTarget<?>) target).getView();
        try {

            String svgString = SvgRenderCacheUtil.Companion.getInstance().retrieveFromCache(url);
            if(svgString == null || svgString.isEmpty()){
                svgString = resource.svgModel.getConvertedString();
                SvgRenderCacheUtil.Companion.getInstance().saveToCache(url, svgString);
            }
            SVG svg = SVG.getFromString(svgString);
            Picture picture = svg.renderToPicture();
            PictureDrawable drawable = new PictureDrawable(picture);
            view.setImageDrawable(drawable);
        } catch (SVGParseException e) {
            e.printStackTrace();
        }
//    view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null);
        return true;
    }
}