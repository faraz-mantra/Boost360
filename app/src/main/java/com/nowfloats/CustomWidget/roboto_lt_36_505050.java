package com.nowfloats.CustomWidget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.thinksity.R;


public class roboto_lt_36_505050 extends TextView {

    public roboto_lt_36_505050(Context context) {
        super(context);
        init(context);
    }

    public roboto_lt_36_505050(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public roboto_lt_36_505050(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        setCustomFont(context, "Roboto-Light.ttf");
        setTextColor(getResources().getColor(R.color.light_black));
        setTextSize(12);
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
            setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        } catch (Exception e) {

            return false;
        }
        setTypeface(tf);
        return true;
    }

}



