package nowfloats.nfkeyboard.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;

import nowfloats.nfkeyboard.interface_contracts.UrlToBitmapInterface;

/**
 * Created by Admin on 26-02-2018.
 */

public class MethodUtils {
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
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), inImage, imageId+ ".png", "drawing");
        if (TextUtils.isEmpty(path)){
            return null;
        }
        return Uri.parse(path);
    }

    public static void onGlideBitmapReady(final UrlToBitmapInterface listener, final String text, String imageUrl, final String imageId){
        Glide.with(listener.getContext())
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        listener.onResourcesReady(resource, text,imageId);

                    }
                });
    }

    public static void startBoostActivity(Context mContext ){
        PackageManager packageManager = mContext.getPackageManager();
        if (!isPackageInstalled("com.biz2.nowfloats",packageManager)){
            Toast.makeText(mContext, "App is not installed", Toast.LENGTH_SHORT).show();
        }
        try {
            Intent LaunchIntent = packageManager.getLaunchIntentForPackage("com.biz2.nowfloats");
            mContext.startActivity(LaunchIntent);
        }catch(Exception e){
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
}
