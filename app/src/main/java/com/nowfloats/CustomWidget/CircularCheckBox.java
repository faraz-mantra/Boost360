package com.nowfloats.CustomWidget;

import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.thinksity.R;

/**
 * Created by Admin on 03-03-2017.
 */

public class CircularCheckBox extends AppCompatCheckBox {
    public CircularCheckBox(Context context) {
        super(context);
    }

    public CircularCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if(checked){
            this.setButtonDrawable(R.drawable.domain_available);
        }else{
            this.setButtonDrawable(R.drawable.circle_stroke);
        }
    }
}
