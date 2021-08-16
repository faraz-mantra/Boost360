package com.nowfloats.riachatsdk.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;

import com.nowfloats.riachatsdk.BuildConfig;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by NowFloats on 30-03-2017.
 */

public class DeviceDetails {
    public static String getDeviceId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    public static String getOs() {
        return "Android";
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getOSVersion() {
        return Build.VERSION.BASE_OS;
    }

    public static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getCodeName() {
        return Build.VERSION.CODENAME;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getLibVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    public static String getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName(false, TimeZone.SHORT) + "|" + tz.getID();
    }

    public static String getLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    public static String getCountry() {
        return Locale.getDefault().getDisplayCountry();
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


}
