package com.nowfloats.Analytics_Screen.Graph;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by tushar on 20-05-2015.
 */
public class CustomValueFormatter implements ValueFormatter{

    private DecimalFormat mFormat;

    public CustomValueFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
    }

    @Override
    public String getFormattedValue(float v) {
        int value = (int) v;
        if(value == 0)
            return "";
        return value+""; // append a dollar-sign
    }
}
