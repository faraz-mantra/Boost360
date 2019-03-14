package com.nowfloats.helper;

import java.text.DecimalFormat;

public class Helper
{

    public static DecimalFormat getCurrencyFormatter()
    {
        return new DecimalFormat("#,##,##,##,##,##,##,###.##");
    }
}
