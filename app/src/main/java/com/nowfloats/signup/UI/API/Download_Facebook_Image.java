package com.nowfloats.signup.UI.API;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

/**
 * Created by NowFloatsDev on 10/06/2015.
 */
public class Download_Facebook_Image extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Create a progressdialog
//        mProgressDialog = new ProgressDialog(MainActivity.this);
//        // Set progressdialog title
//        mProgressDialog.setTitle("Download Image Tutorial");
//        // Set progressdialog message
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setIndeterminate(false);
//        // Show progressdialog
//        mProgressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];
        String fpID = URL[1];

        Bitmap bitmap = null;
        try {
            // Download Image from URL
            InputStream input = new java.net.URL(imageURL).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadFeaturedImage_Facebook uploadFacebookImage = new uploadFeaturedImage_Facebook(bitmap,fpID);
        uploadFacebookImage.execute();

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        // Set the bitmap into ImageView
        //image.setImageBitmap(result);
        // Close progressdialog
       // mProgressDialog.dismiss();
    }
}
//}
