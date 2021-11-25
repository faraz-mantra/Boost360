package com.framework.glide.customsvgloader;

import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

public class CustomPictureDrawable extends PictureDrawable {
    public SvgCustomDataModel svgModel;
    /**
     * Construct a new drawable referencing the specified picture. The picture
     * may be null.
     *
     * @param picture The picture to associate with the drawable. May be null.
     */
    public CustomPictureDrawable(Picture picture) {
        super(picture);
    }
}
