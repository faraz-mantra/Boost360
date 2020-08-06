package com.nowfloats.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager mConnectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectManager != null) {
            NetworkInfo[] mNetworkInfo = mConnectManager.getAllNetworkInfo();
            for (int i = 0; i < mNetworkInfo.length; i++) {
                if (mNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return false;
    }

    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static void logOnBoardingCompleteConversionGoals(Context context, String fpId) {
        try {
            WebEngageController.trackEvent("Business Profile Creation Success", "Business Profile Creation Success", fpId);

            String loginMethod = "unknown";
            try {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    for (UserInfo profile : user.getProviderData()) {
                        // Id of the provider (ex: google.com)
                        loginMethod = profile.getProviderId();
                    }
                }
            } catch (Exception e1) {
                loginMethod = "unknown";
            }

            FirebaseAnalytics mAnalytics = FirebaseAnalytics.getInstance(context);
            if (mAnalytics != null) {
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.METHOD, loginMethod);
                mAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);
            }
        } catch (Exception e) {

        }
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static String getDefaultTrasactionsTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "SVC":
            case "DOC":
            case "HOS":
            case "SPA":
            case "SAL":
                return "Appointments";
            case "EDU":
                return "Admission Requests";
            case "HOT":
                return "Room Bookings";
            case "RTL":
            case "MFG":
                return "Orders";
            case "CAF":
                return "Food Orders";
            default:
                return "Orders";
        }
    }

    public static String getSecondTypeTrasactionsTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "DOC":
            case "HOS":
                return "Video Consultations";
            default:
                return "";
        }
    }

    public static String getOrderAnalyticsTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "DOC":
            case "HOS":
            case "SVC":
            case "SPA":
            case "SAL":
                return "Appointment Analytics";
            case "EDU":
                return "Admission Request Analytics";
            case "HOT":
                return "Booking Analytics";
            case "RTL":
            case "MFG":
            case "CAF":
                return "Order Analytics";
            default:
                return "Order Analytics";
        }
    }

    public static boolean isRoomBooking(String category_code) {
        return ("HOT".equals(category_code));
    }

    public static String getProductCatalogTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "SVC":
            case "DOC":
            case "HOS":
            case "SPA":
            case "SAL":
                return "Service Catalogue";
            case "EDU":
                return "Course Catalogue";
            case "HOT":
                return "Room Inventory";
            case "RTL":
            case "MFG":
                return "Products Catalogue";
            case "CAF":
                return "Digital Food Menu";
            default:
                return "Catalogue";
        }
    }

    public static String getLatestUpdatesTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "DOC":
            case "HOS":
                return "Latest Updates & Health Tips";
            case "SPA":
            case "SAL":
                return "Latest Updates & Offers";
            case "HOT":
                return "Latest Updates, News & Events";
            case "MFG":
                return "Latest Updates & News";
            case "CAF":
            case "EDU":
                return "Latest Updates & Tips";
            default:
                return "Latest Updates";
        }
    }

    public static String getSingleProductTaxonomyFromServiceCode(String category_code) {
        switch (category_code) {
            case "SVC":
            case "DOC":
            case "HOS":
            case "SPA":
            case "SAL":
                return "Service";
            case "EDU":
                return "Course";
            case "HOT":
                return "Room";
            case "RTL":
            case "MFG":
                return "Product";
            case "CAF":
                return "Food Item";
            default:
                return "Catalogue";
        }
    }


    public static String getProductType(String category_code) {
        switch (category_code) {
            case "SVC":
            case "DOC":
            case "HOS":
            case "SPA":
            case "SAL":
                return "SERVICES";
            default:
                return "PRODUCTS";
        }
    }

    public static String getPictureName() {
        SimpleDateFormat tst = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = tst.format(new Date());
        return "Camera_"+ timestamp + ".jpg";
    }
}
