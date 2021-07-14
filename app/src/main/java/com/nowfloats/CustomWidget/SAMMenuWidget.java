package com.nowfloats.CustomWidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thinksity.R;

public class SAMMenuWidget extends ViewGroup {

    private Paint mCirclePaint;
    private ImageView mView;

    public SAMMenuWidget(Context context) {
        super(context);
        init(context);
    }

    public SAMMenuWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    public SAMMenuWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SAMMenuWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context context) {
        setWillNotDraw(false);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(getResources().getColor(R.color.color_trans));
        mCirclePaint.setStyle(Paint.Style.FILL);

        mView = new ImageView(getContext());

        mView.setImageResource(R.drawable.boost_bubble);
        mView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        addView(mView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int parentWidth = ((ViewGroup) getParent()).getWidth();
        int parentHeight = ((ViewGroup) getParent()).getHeight();

        int newWidthMeasureSpec = widthMeasureSpec;
        int newHeightMeasureSpec = heightMeasureSpec;
        if (height == 0) {
            newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(parentHeight, MeasureSpec.AT_MOST);
            height = MeasureSpec.getSize(newHeightMeasureSpec);
        }

        if (width == 0) {
            newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.AT_MOST);
            width = MeasureSpec.getSize(newWidthMeasureSpec);
        }

        int lCenterIconSize = (int) Math.sqrt(100 * 100 / 2.0);
        int centerImageMeasureWidthSpec = MeasureSpec.makeMeasureSpec(lCenterIconSize, MeasureSpec.EXACTLY);
        int centerImageMeasureHeightSpec = MeasureSpec.makeMeasureSpec(lCenterIconSize, MeasureSpec.EXACTLY);
        mView.measure(centerImageMeasureWidthSpec, centerImageMeasureHeightSpec);

        width = resolveSize(width, newWidthMeasureSpec);
        height = resolveSize(height, newHeightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int containerWidth = r - l;
        int centerImageLeft = 0;
        int centerImageTop = 0;
        int centerImageRight = 0;
        int centerImageBottom = 0;


        centerImageTop = 0;
        centerImageBottom = mView.getMeasuredHeight();

        centerImageLeft = 0;
        centerImageRight = mView.getMeasuredWidth();


        mView.layout(centerImageLeft, centerImageTop, centerImageRight, centerImageBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(0, 150, 100, mCirclePaint);
    }
}
