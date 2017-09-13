package com.nowfloats.manageinventory.interfaces;

import com.nowfloats.manageinventory.models.CountModel;
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.OrdersCountModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.nowfloats.manageinventory.models.UserModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;

import retrofit.Callback;
import retrofit.http.Headers;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by NowFloats on 28-08-2017.
 */

public interface WebActionCallInterface {

    @GET("/orders3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getOrders(@Query("query") String query, @Query("skip") long skip, @Query("limit") int limit, @Query("sort") String sort, Callback<WebActionModel<OrderModel>> callback);

    @GET("/users3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getUser(@Query("query") String query, @Query("limit") int limit, Callback<WebActionModel<UserModel>> callback);

    @GET("/products3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getProducts(@Query("query") String query, @Query("limit") int limit, Callback<WebActionModel<ProductModel>> callback);

    @GET("/orders3/aggregate-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getAllOrdersCount(@Query("pipeline") String pipeline, Callback<WebActionModel<OrdersCountModel>> callback);


}
