package com.nowfloats.signup.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Created by admin on 6/29/2017.
 */

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;

    public TextDrawable(String text, Paint.Align align) {
        this.text = text;
        this.paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(36f);
        paint.setAntiAlias(true);
        paint.setTextAlign(align);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(text, 0, 6, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
