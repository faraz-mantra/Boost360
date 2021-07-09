package com.nowfloats.ProductGallery.Service;

import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.ProductGallery.Product_Detail_Activity;
import com.thinksity.R;

/**
 * Created by NowFloats on 07-09-2016.
 */
public class DeleteProductImage extends AsyncTask<String, Void, Void> {

    private Product_Detail_Activity mProductDtailsActivity;
    private MaterialDialog materialProgress;

    public DeleteProductImage(Product_Detail_Activity activity){
        this.mProductDtailsActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        materialProgress = new MaterialDialog.Builder(mProductDtailsActivity)
                .widgetColorRes(R.color.accentColor)
                .content(mProductDtailsActivity.getString(R.string.deleting_image))
                .progress(true, 0)
                .show();
        materialProgress.setCancelable(false);
    }

    @Override
    protected Void doInBackground(String... params) {
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
