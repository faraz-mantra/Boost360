package com.nowfloats.CustomWidget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.thinksity.R;

/**
 * Created by Admin on 30-03-2017.
 */

public class HiddenTextSpinner extends AppCompatSpinner {

    Context mContext;
    int pos=0;
    int[] images = new int[]{R.drawable.facebook_signup,R.drawable.quikr};
    public HiddenTextSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public HiddenTextSpinner(Context context) {
        super(context);
        mContext = context;
    }

    public HiddenTextSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext =context;
    }

    @Override
    public View getChildAt(int index) {
        ImageView image = new ImageView(mContext);
        image.setImageResource(images[pos]);
        Log.v("ggg",index+" "+pos);
        return image;
    }


    @Override
    public long getItemIdAtPosition(int position) {
        Log.v("ggg","ITEM ID"+ position);
        pos=position;
        return super.getItemIdAtPosition(position);
    }

}
