package com.nowfloats.riachatsdk.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NowFloats on 08-05-2017.
 */

public class Utils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String getFormattedDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(date);
    }

    public static String getMapUrlFromLocation(String lattitude, String longitude){

        String url =  "http://maps.google.com/maps/api/staticmap?center=" + lattitude + ","
                + longitude + "&zoom=19&size=1000x300&sensor=false" + "&key="
                + Constants.MAP_KEY;
        Log.d("LatLong", url);
        return url;
    }
}
