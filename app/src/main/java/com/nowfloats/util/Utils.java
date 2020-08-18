package com.nowfloats.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.thinksity.R;

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

    public static String getCustomerAppointmentTaxonomyFromServiceCode(String category_code) {
        //" Customer Appointments" for "SVC","DOC", "HOS","SPA", "SAL"  & "Customer Orders" for all others.
        switch (category_code) {
            case "DOC":
            case "HOS":
                return "Customer Appointments";
            case "SVC":
            case "SPA":
            case "SAL":
                return "Customer Bookings";
            default:
                return "Customer Orders";
        }
    }

    public static String[] getCustomerAppointmentBarChartCode(Context context, String category_code) {
        switch (category_code) {
            case "DOC":
            case "HOS":
                return context.getResources().getStringArray(R.array.appointment_analytics);
            case "SVC":
            case "SPA":
            case "SAL":
                return context.getResources().getStringArray(R.array.booking_analytics);
            default:
                return context.getResources().getStringArray(R.array.order_analytics);
        }
    }

    public static String getCustomerTypeFromServiceCode(String category_code) {
        //" Customer Appointments" for "SVC","DOC", "HOS","SPA", "SAL"  & "Customer Orders" for all others.
        switch (category_code) {
            case "DOC":
            case "HOS":
                return "Appointments";
            case "SVC":
            case "SPA":
            case "SAL":
                return "Bookings";
            default:
                return "Orders";
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

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getAbsoluteFilePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
