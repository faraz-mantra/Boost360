package com.nowfloats.Store.Service;

import com.appservice.model.accountDetails.AccountDetailsResponse;
import com.appservice.model.kycData.PaymentKycDataResponse;
import com.google.gson.JsonObject;
import com.nowfloats.Store.DiscountCoupon;
import com.nowfloats.Store.Model.ChequePaymentModel;
import com.nowfloats.Store.Model.EnablePackageResponse;
import com.nowfloats.Store.Model.InitiateModel;
import com.nowfloats.Store.Model.InvoiceDetailsModel;
import com.nowfloats.Store.Model.MailModel;
import com.nowfloats.Store.Model.MarkAsPaidModel;
import com.nowfloats.Store.Model.OPCModels.UpdateDraftInvoiceModel;
import com.nowfloats.Store.Model.PaymentTokenResult;
import com.nowfloats.Store.Model.PricingPlansModel;
import com.nowfloats.Store.Model.ReceivedDraftInvoice;
import com.nowfloats.Store.Model.SalesmanModel;
import com.nowfloats.Store.Model.SendDraftInvoiceModel;
import com.nowfloats.Store.Model.StoreMainModel;
import com.nowfloats.Store.Model.SupportedPaymentMethods;
import com.nowfloats.Store.RedeemDiscountRequestModel;
import com.nowfloats.widget.WidgetResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by guru on 29-04-2015.
 */
/*https://api.withfloats.com/Support/v1/
            GetFloatingPointPackages?
            identifier=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&
            clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70*/
//?identifier=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70
public interface StoreInterface {
    //https://api.withfloats.com/Support/v1/floatingpoint/getpackages?
    // identifier={IDENTIFIER}&clientId={CLIENTID}&fpId={FPID}&country={COUNTRY}
    ///Support/v1/floatingpoint/getpackages?identifier=524304ad4ec0a40d1c9a93f3&clientId=217FF5B9CE214CDDAC4985C853AE7F75AAFA11AF2C4B47CB877BCA26EC217E6D
//    @GET("/Support/v1/GetFloatingPointPackages")
    @GET("/Support/v2/floatingpoint/getpackages")
    void getOldStoreList(@QueryMap Map<String, String> map, Callback<StoreMainModel> callback);

    @GET("/Support/v5/floatingpoint/getpackages")
    void getStoreList(@QueryMap Map<String, String> map, Callback<PricingPlansModel> callback);

    @GET("/Support/v1/FloatingPoint/GetInvoiceDetailsByFPTag")
    void getInvoiceDetailsByFPTag(@QueryMap Map<String, String> map, Callback<InvoiceDetailsModel> callback);

    //https://api.withfloats.com/Discover/v1/floatingPoint/5406bd254ec0a40d409f2b2b/requestplan?
    // clientId=2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21&plantype=mach3
    @GET("/Discover/v1/floatingPoint/{fpid}/requestplan")
    void requestWidget(@Path("fpid") String fpid, @QueryMap Map<String, String> map, Callback<String> callback);

    ///DomainService/v1/checkAvailability/yahoo?clientId=DB96EA35A6E44C0F8FB4A6BAA94DB017C0DFBE6F9944B14AA6C3C48641B3D70&domainType=.com
    @GET("/DomainService/v1/checkAvailability/{tagField_}")
    void checkDomain(@Path("tagField_") String tagField_, @QueryMap Map<String, String> map, Callback<String> callback);

    /* @"clientId",@"domainType",@"domainName",@"existingFPTag" */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/domainservice/v1/requestdomainpurchase")
    void purchaseDomain(@Body HashMap<String, String> map, Callback<String> callback);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Support/v1/floatingpont/enablePackage")
    void enableWidgetPack(@Body HashMap<String, String> map, Callback<EnablePackageResponse> callback);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/Discover/v1/FloatingPoint/SendEmailWithPriority")
    void mail(@Body MailModel data, Callback<String> callback);

    @POST("/payment/v1/floatingpoint/updateDraftInvoice")
    void updateDraftInvoice(@QueryMap Map<String, String> params, @Body UpdateDraftInvoiceModel model, Callback<ReceivedDraftInvoice> callback);

    @POST("/payment/v1/floatingpoint/createDraftInvoice")
    void createDraftInvoice(@QueryMap Map<String, String> params, @Body SendDraftInvoiceModel model, Callback<ReceivedDraftInvoice> callback);

    @POST("/payment/v2/floatingpoint/initiatePaymentProcess")
    void initiatePaymentProcess(@QueryMap Map<String, String> params, @Body SupportedPaymentMethods model, Callback<PaymentTokenResult> callback);


    @POST("/payment/v1/invoice/UpdateChequePaymentLog")
    void updateChequeLog(@Body ChequePaymentModel model, Callback<String> res);

    @POST("/Support/v2/MarkFloatingPointAsPaid")
    void markAsPaid(@Body MarkAsPaidModel model, Callback<String> res);

    @POST("/Payment/v1/Discount/RedeemDiscountCouponCode")
    void redeemDiscountCode(@Body RedeemDiscountRequestModel redeemDiscountRequestModel, Callback<DiscountCoupon> res);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/v1/GetActiveEmployees")
    void getActiveEmployees(@Body JsonObject jsonObject, Callback<ArrayList<SalesmanModel>> res);

    @POST("/payment/v1/floatingpoints/initiate/{clientId}")
    void initiate(@Path("clientId") String clientId, @Body InitiateModel model, Callback<String> res);


    @GET("/Support/v5/floatingpoint/getpackages")
    void getActiveWidgetList(@QueryMap Map<String, String> map, Callback<WidgetResponse> callback);

    @GET("/support/v1/floatingpoint/widgetLimit")
    void getWidgetLimit(@QueryMap Map<String, String> map, Callback<Object> callback);

    @GET("/discover/v9/business/paymentProfile/{fpId}")
    void userAccountDetail(@Path("fpId") String fpId, @Query("clientId") String clientId, Callback<AccountDetailsResponse> callback);

    @Headers({"Authorization: 597ee93f5d64370820a6127c", "Accept: application/json"})
    @GET("/api/v1/kycdoc/get-data")
    void getSelfBrandedKyc(@Query("query") String query, Callback<PaymentKycDataResponse> callback);


    @POST("/discover/v1/FloatingPoint/AccessToken/Create")
    void createAccessToken(@Body com.boost.presignin.model.accessToken.AccessTokenRequest request, Callback<com.boost.presignin.model.authToken.AccessTokenResponse> callback);
}