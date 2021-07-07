package com.nowfloats.CustomWidget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.thinksity.R;


public class roboto_md_34_primarycolor extends TextView {

    public roboto_md_34_primarycolor(Context context) {
        super(context);
        init(context);
    }

    public roboto_md_34_primarycolor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public roboto_md_34_primarycolor(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        setCustomFont(context, "Roboto-Medium.ttf");
        setTextColor(getResources().getColor(R.color.primaryColor));
        setTextSize(17);


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





