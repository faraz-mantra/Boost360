package com.nowfloats.ProductGallery.Service;

import android.os.AsyncTask;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.ProductGallery.Product_Detail_Activity_V45;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by guru on 10-06-2015.
 */
public class ProductImageUploadV45 extends AsyncTask<String, String, String> {
    public String url = "";
    public byte[] imageData;
    Product_Detail_Activity_V45 activity;
    private boolean flag = false;
    private MaterialDialog materialProgress;
    private String productId;

    public ProductImageUploadV45(String url, byte[] imageData, Product_Detail_Activity_V45 activity, String productId) {
        this.activity = activity;
        this.url = url;
        this.imageData = imageData;
        this.productId = productId;
    }

    @Override
    protected void onPreExecute() {
        materialProgress = new MaterialDialog.Builder(activity)
                .widgetColorRes(R.color.accentColor)
                .content(activity.getString(R.string.uploading_image))
                .progress(true, 0)
                .show();
        materialProgress.setCancelable(false);
    }

    @Override
    protected void onPostExecute(final String result) {
        try {
            if (flag) {
                MixPanelController.track("AddProductSuccess", null);
                activity.retryImage = 0;
                activity.path = "";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(6000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (materialProgress != null)
                            materialProgress.dismiss();
                        activity.invokeGetProductList(productId);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Methods.showSnackBarPositive(activity, activity.getString(R.string.new_product_added));
                            }
                        });
                    }
                }).start();
            } else {
                if (false) {
                    new ProductImageUploadV45(url, imageData, activity, productId).execute();
                    activity.retryImage++;
                } else {
                    activity.retryImage = 0;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (materialProgress != null)
                                materialProgress.dismiss();
                            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (materialProgress != null)
                        materialProgress.dismiss();
                    Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                }
            });
        }
    }

    @Override
    protected String doInBackground(String... params) {
        DataOutputStream outputStream = null;
        try {
            URL new_url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) new_url.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(Constants.HTTP_PUT);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", Utils.getAuthToken());
            if (imageData != null) {
                outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.write(imageData, 0, imageData.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200 || responseCode == 202) {
                flag = true;
            } else {
                flag = false;
            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder responseContent = new StringBuilder();
                String temp = null;
                boolean isFirst = true;
                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append(Constants.NEW_LINE);
                    responseContent.append(temp);
                    isFirst = false;
                }
                if (responseContent != null || responseContent.length() == 0) {
                    String response = responseContent.toString();
                    Log.d("Product IMage", "Upload Response : " + response);
                    if (response == null || response.trim().length() == 0) flag = false;
                } else {
                    flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }
        } catch (Exception ex) {
            flag = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }
        return null;
    }
}