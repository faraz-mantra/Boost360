package com.nowfloats.ProductGallery.Service;

import android.app.Activity;
import android.view.View;

import com.nowfloats.ProductGallery.LoadMoreProductEvent;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Product_Detail_Activity_V45;
import com.nowfloats.ProductGallery.Product_Gallery_Fragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 08-06-2015.
 */
public class ProductAPIService {
    public void getProductList(final Activity activity, final HashMap<String,String> map, final Bus bus){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Product_Gallery_Fragment.progressLayout!=null)
                    Product_Gallery_Fragment.progressLayout.setVisibility(View.VISIBLE);
            }
        });
        try {
            ProductGalleryInterface PDinterface = Constants.restAdapter.create(ProductGalleryInterface.class);
            PDinterface.getProducts(map, new Callback<ArrayList<ProductListModel>>() {
                @Override
                public void success(ArrayList<ProductListModel> data, Response response) {
                    try {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Product_Gallery_Fragment.progressLayout != null)
                                    Product_Gallery_Fragment.progressLayout.setVisibility(View.GONE);
                            }
                        });
                        if (map.get("skipBy").equals("0") && Product_Detail_Activity_V45.replaceImage) {
                            Product_Gallery_Fragment.productItemModelList = data;
                        }
                        if (bus != null) {
                            if (map.get("skipBy").equals("0")) {
                                bus.post(data);
                            } else {
                                bus.post(new LoadMoreProductEvent(data));
                            }
                        }
                    }catch(Exception e){e.printStackTrace();}
                }

                @Override
                public void failure(RetrofitError error) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Product_Gallery_Fragment.empty_layout!=null)
                                Product_Gallery_Fragment.empty_layout.setVisibility(View.VISIBLE);
                            if (Product_Gallery_Fragment.progressLayout!=null)
                                Product_Gallery_Fragment.progressLayout.setVisibility(View.GONE);
                            Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                        }
                    });
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Product_Gallery_Fragment.progressLayout!=null)
                        Product_Gallery_Fragment.progressLayout.setVisibility(View.GONE);
                    if (Product_Gallery_Fragment.productItemModelList!=null)
                        if (Product_Gallery_Fragment.productItemModelList.size() == 0) {
                            if (Product_Gallery_Fragment.empty_layout!=null)
                                Product_Gallery_Fragment.empty_layout.setVisibility(View.VISIBLE);
                        } else {
                            if (Product_Gallery_Fragment.empty_layout!=null)
                                Product_Gallery_Fragment.empty_layout.setVisibility(View.GONE);
                        }
                    Methods.showSnackBarNegative(activity, activity.getString(R.string.something_went_wrong_try_again));
                }
            });
        }
    }

}