package com.nowfloats.ProductGallery.Service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomWidget.HttpDeleteWithBody;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Product_Gallery_Fragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by guru on 10-06-2015.
 */

public class ProductDelete extends AsyncTask<String, String, String> {

    private String url = "", values = "";
    private Activity activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    private int position;
    private ArrayList<Integer> arrSelectedProducts;
    private DeleteProductGalleryInterface deleteProductGalleryInterface;

    public ProductDelete(String url, String values, Activity activity, int position) {
        this.url = url;
        this.values = values;
        this.activity = activity;
        this.position = position;
        flag = false;
    }

    public ProductDelete(String url, Activity activity, ArrayList<Integer> arrSelectedProducts,
                         DeleteProductGalleryInterface deleteProductGalleryInterface) {
        this.url = url;
        this.activity = activity;
        this.arrSelectedProducts = arrSelectedProducts;
        this.deleteProductGalleryInterface = deleteProductGalleryInterface;
        flag = false;
    }

    @Override
    protected void onPreExecute() {
        materialProgress = new MaterialDialog.Builder(activity)
                .widgetColorRes(R.color.accentColor)
                .content(activity.getString(R.string.deleting))
                .progress(true, 0)
                .show();
        materialProgress.setCancelable(false);
    }

    @Override
    protected void onPostExecute(final String result) {
        if (materialProgress != null)
            materialProgress.dismiss();
        if (flag) {
            if (arrSelectedProducts != null && arrSelectedProducts.size() > 0) {
                if (deleteProductGalleryInterface != null) {
                    deleteProductGalleryInterface.galleryProductDeleted();
                }
            } else {

                if (Product_Gallery_Fragment.productItemModelList != null && Product_Gallery_Fragment.productItemModelList.size() > 0)
                    Product_Gallery_Fragment.productItemModelList.remove(position);
                Methods.showSnackBarPositive(activity, activity.getString(R.string.product_removed));
                activity.finish();
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        } else {
            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
        }
    }

    @Override
    protected String doInBackground(String... params) {

        if (arrSelectedProducts != null && arrSelectedProducts.size() > 0) {
            int count = 0;
            for (Integer selectedPos : arrSelectedProducts) {
                if (selectedPos > 0) {
                    selectedPos = selectedPos - count;
                }
                ProductListModel product_data = Product_Gallery_Fragment.productItemModelList.get((int) selectedPos);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("clientId", Constants.clientId);
                    jsonObject.put("productId", product_data._id);
                    String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/Delete";
                    deleteProduct(jsonObject.toString());
                    Product_Gallery_Fragment.productItemModelList.remove((int) selectedPos);
                    count++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } else {
            deleteProduct(values.toString());
        }
        return null;
    }

    private void deleteProduct(String values) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpDeleteWithBody del = new HttpDeleteWithBody(url);
        StringEntity se;
        try {
            se = new StringEntity(values, HTTP.UTF_8);
            se.setContentType("application/json");
            del.setEntity(se);
            HttpResponse response = httpclient.execute(del);

            StatusLine status = response.getStatusLine();
            Log.i("Delete Product---", "status----" + status);
            if (status.getStatusCode() == 200) {
                Log.i("Delete msg...", "Success");
                flag = true;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface DeleteProductGalleryInterface {
        public void galleryProductDeleted();
    }
}
