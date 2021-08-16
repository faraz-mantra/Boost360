package com.nowfloats.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

/**
 * Created by Admin on 05-06-2017.
 */

public class UploadLargeImage {
    private Context mContext;
    private String uri;
    private ImageCompressed listner;
    private int reqWidth, reqHeight;

    public UploadLargeImage(Context context, ImageCompressed listner, String uri, int reqWidth, int reqHeight) {
        mContext = context;
        this.uri = uri;
        this.listner = listner;
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
    }

    public void execute() {
        //uri = getRealPathFromURI(mContext,Uri.parse(uri));
        new LoadImage().execute();
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri == null) return null;
        Cursor cursor = null;
        String uri = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                uri = cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri;
    }

    public interface ImageCompressed {
        void onImageCompressed(Bitmap bitmap);
    }

    private class LoadImage extends AsyncTask<Void, Void, Bitmap> {
        private LoadImage() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            return Methods.decodeSampledBitmap(uri, reqWidth, reqHeight);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                listner.onImageCompressed(bitmap);
            }
        }
    }
}
