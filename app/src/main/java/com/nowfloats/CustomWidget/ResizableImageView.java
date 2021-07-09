package com.nowfloats.CustomWidget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {

    

	public ResizableImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	public ResizableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
         Drawable d = getDrawable();

         if(d!=null){
                 // ceil not round - avoid thin vertical gaps along the left/right edges
                 int width = MeasureSpec.getSize(widthMeasureSpec);
                 int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
                 setMeasuredDimension(width, height);
         }else{
                 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         }
    }

}