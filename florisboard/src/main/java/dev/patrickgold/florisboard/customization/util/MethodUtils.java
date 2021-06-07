package dev.patrickgold.florisboard.customization.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created by Admin on 26-02-2018.
 */

public class MethodUtils {

    static int i;

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static Uri getImageUri(Context mContext, Bitmap inImage, String imageId) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, imageId + ".png", "drawing");
            if (TextUtils.isEmpty(path)) return null;
            return Uri.parse(path);
        } catch (Exception e) {
            Log.e(MethodUtils.class.getName(), e.getLocalizedMessage());
            return null;
        }
    }


    public static void startBoostActivity(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        if (!isPackageInstalled(mContext.getPackageName(), packageManager)) {
            Toast.makeText(mContext, "App is not installed", Toast.LENGTH_SHORT).show();
        }
        try {
            Intent LaunchIntent = packageManager.getLaunchIntentForPackage(mContext.getPackageName());
            mContext.startActivity(LaunchIntent);
        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to open Boost App ", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isPackageInstalled(String packagename, PackageManager manager) {
        try {
            manager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = connectivityManager.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
