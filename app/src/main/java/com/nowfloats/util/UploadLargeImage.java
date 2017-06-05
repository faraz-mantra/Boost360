package com.nowfloats.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private int reqWidth,reqHeight;
    public UploadLargeImage(Context context, ImageCompressed listner, String uri, int reqWidth, int reqHeight){
        mContext = context;
        this.uri = uri;
        this.listner = listner;
        this.reqHeight=reqHeight;
        this.reqWidth=reqWidth;
    }
    public void execute(){
        //uri = getRealPathFromURI(mContext,Uri.parse(uri));
        new LoadImage().execute();
    }
    private class LoadImage extends AsyncTask<Void ,Void,Bitmap>
    {
      private LoadImage(){

      }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            return decodeSampledBitmap(uri,reqWidth,reqHeight);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
                listner.onImageCompressed(bitmap);
            }
        }
    }
    public interface ImageCompressed{
        void onImageCompressed(Bitmap bitmap);
    }
    private Bitmap decodeSampledBitmap(String uri, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inDither = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(uri, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri == null) return null;
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = 0;
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
