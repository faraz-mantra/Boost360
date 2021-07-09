package com.nowfloats.util;


import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.thinksity.R;

public class HeaderText extends TextView{

	public HeaderText(Context context) {
		super(context);
		init(context);
	}
	public HeaderText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
	public void init(Context context)
	{
		setCustomFont(context,"HN55Roman.ttf");
		setTextColor(getResources().getColor(R.color.title_grey));
		setTextSize(19);
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
