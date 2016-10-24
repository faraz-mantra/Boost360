package com.nowfloats.CustomWidget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import com.thinksity.R;

public class roboto_lt_33__b6b6b6_383838_edittext extends EditText {

    public roboto_lt_33__b6b6b6_383838_edittext(Context context) {
        super(context);
        init(context);
    }
    public roboto_lt_33__b6b6b6_383838_edittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public roboto_lt_33__b6b6b6_383838_edittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public void init(Context context)
    {
        setCustomFont(context,"Roboto-Light.ttf");
        setTextColor(getResources().getColor(R.color.text_color_roboto_lt_30_edittext));
        setHintTextColor(getResources().getColor(R.color.hint_color_roboto_lt_30_edittext));
        setTextSize(17);
        setMinimumHeight(45);
        setGravity(Gravity.LEFT);
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

