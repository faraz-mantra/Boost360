package com.nowfloats.manageinventory.interfaces;

import com.nowfloats.Analytics_Screen.model.OrderStatusSummary;
import com.nowfloats.Analytics_Screen.model.RevenueSummary;
import com.nowfloats.BusinessProfile.UI.Model.WhatsAppBusinessNumberModel;
import com.nowfloats.manageinventory.models.CommonStatus;
import com.nowfloats.manageinventory.models.MarkOrderAsShipped;
import com.nowfloats.manageinventory.models.OrderDataModel;
import com.nowfloats.manageinventory.models.OrderDetailDataModel;
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.OrdersCountModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.nowfloats.manageinventory.models.SellerSummary;
import com.nowfloats.manageinventory.models.UserModel;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.util.Constants;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by NowFloats on 28-08-2017.
 */

public interface WebActionCallInterface {

    @GET("/orders3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getOrders(@Query("query") String query, @Query("skip") long skip, @Query("limit") int limit, @Query("sort") String sort, Callback<WebActionModel<OrderModel>> callback);

    @GET("/ListOrders")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getOrdersList(@QueryMap HashMap<String, String> hashMap, @Query("skip") long skip, @Query("limit") int limit, Callback<OrderDataModel> callback);

    @GET("/ListInProgressOrders")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getInProgressOrdersList(@QueryMap HashMap<String, String> hashMap, @Query("skip") long skip, @Query("limit") int limit, Callback<OrderDataModel> callback);


    @GET("/GetOrderDetails")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getOrdersDetails(@Query("orderId") String orderId, Callback<OrderDetailDataModel> callback);


    @GET("/users3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getUser(@Query("id") String id, @Query("limit") int limit, Callback<WebActionModel<UserModel>> callback);

    @GET("/products3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getProducts(@Query("query") String query, @Query("limit") int limit, Callback<WebActionModel<ProductModel>> callback);

    @GET("/orders3/aggregate-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getAllOrdersCount(@Query("pipeline") String pipeline, Callback<WebActionModel<OrdersCountModel>> callback);

    @GET("/SellerSummary")
    void getRevenueSummary(@Query("sellerId") String sellerId, Callback<SellerSummary> callback);

    @GET("/confirmOrder")
    void confirmOrder(@Query("orderId") String orderId, Callback<CommonStatus> callback);

    @GET("/cancelOrder")
    void cancelOrder(@Query("orderId") String orderId, Callback<CommonStatus> callback);

    @GET("/TriggerOrderDeliveryConfirmation")
    void triggerOrderDeliveryConfirmation(@Query("orderId") String orderId, @Query("notificationSentBy") String notificationSentBy, Callback<CommonStatus> callback);


    @GET("/RaiseOrderDispute")
    void raiseOrderDispute(@Query("orderId") String orderId, Callback<CommonStatus> callback);

    @POST("/MarkOrderAsShipped")
    void markOrderAsShipped(@Body MarkOrderAsShipped model, Callback<CommonStatus> callback);

    @GET("/OrderStatusSummary")
    void getOrderStatusSummary(@Query("sellerId") String sellerId, @Query("startDate") String startDate,
                               @Query("endDate") String endDate,
                               Callback<OrderStatusSummary> callback);

    @GET("/DailySellerRevenue")
    void dailySellerRevenue(@Query("sellerId") String sellerId, @Query("deliveredStartDate") String deliveredStartDate,
                            @Query("deliveredEndDate") String deliveredEndDate,
                            Callback<RevenueSummary> callback);

    @GET("/whatsapp_number/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getWhatsAppNumber(@Query("query") String query, Callback<WebActionModel<WhatsAppBusinessNumberModel>> callback);

    @POST("/whatsapp_number/add-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void addWhatsAppNumber(@Body WAAddDataModel<WhatsAppBusinessNumberModel> query, Callback<String> callback);

    @POST("/whatsapp_number/update-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void updateWhatsAppNumber(@Body WaUpdateDataModel query, Callback<String> callback);
}