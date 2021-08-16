package com.nowfloats.riachatsdk.CustomWidget;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

import android.text.Layout;
import android.util.AttributeSet;

/**
 * Created by admin on 7/14/2017.
 */

public class WrappedTextView extends AppCompatTextView {
    private boolean hasMaxWidth;

    public WrappedTextView(Context context) {
        this(context, null, 0);
    }

    public WrappedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrappedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Layout layout = getLayout();
        if (layout != null) {
            int width = (int) Math.ceil(getMaxLineWidth(layout))
                    + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }


    @Override
    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels);
        hasMaxWidth = true;
    }

    @Override
    public void setMaxEms(int maxems) {
        super.setMaxEms(maxems);
        hasMaxWidth = true;
    }
}
