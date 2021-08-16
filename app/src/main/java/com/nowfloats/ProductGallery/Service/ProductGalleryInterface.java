package com.nowfloats.ProductGallery.Service;

import com.nowfloats.ProductGallery.Model.AddressInformation;
import com.nowfloats.ProductGallery.Model.AssuredPurchase;
import com.nowfloats.ProductGallery.Model.BankInformation;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Model.Product_Gallery_Update_Model;
import com.nowfloats.ProductGallery.Model.ShippingMetricsModel;
import com.nowfloats.manageinventory.models.APIResponseModel;
import com.nowfloats.manageinventory.models.MerchantProfileModel;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.sellerprofile.model.SellerProfile;
import com.nowfloats.sellerprofile.model.WebResponseModel;
import com.nowfloats.util.Constants;
import com.nowfloats.webactions.webactioninterfaces.MOD_DELETE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by guru on 08-06-2015.
 */
public interface ProductGalleryInterface {
    @GET("/Product/v1/GetListings")
    public void getProducts(@QueryMap Map<String, String> map, Callback<ArrayList<ProductListModel>> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/Product/v1/Create")
    public void addProduct(@Body HashMap<String, String> map, Callback<String> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/Product/v1/Create")
    public void addProduct(@Body Product product, Callback<String> callback);

    @PUT("/Product/v1/Update")
    void put_UpdateGalleryUpdate(@Body Product_Gallery_Update_Model model, Callback<ArrayList<String>> callback);


    @FormUrlEncoded
    @PUT("/Product/v1/AddImage")
    public void uploadPic(@Body byte[] image, @QueryMap HashMap<String, String> map, Callback<String> cb);

    @Headers({"Content-Type: application/json"})
    @DELETE("/Product/v1/Delete")
    void deleteProduct(@Body HashMap<String, String> map, Callback<String> callback);

    @Headers({"Content-Type: application/json"})
    @DELETE("/Product/v2/DeleteImage")
    void deleteProductImage(@Body HashMap<String, String> map, Callback<String> callback);

    @GET("/ProductCatalogue/v1/floatingpoints/getDeliveryPriceForMerchant")
    public void getShippingCharge(@QueryMap Map<String, String> query, Callback<APIResponseModel<Integer>> callback);

    @GET("/product_details3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getShippingMetric(@Query("query") String query, Callback<WebActionModel<ShippingMetricsModel>> callback);

    @POST("/product_details3/update-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void updateProductMetrics(@Body WaUpdateDataModel updateDataModel, Callback<String> callback);

    @POST("/product_details3/add-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void addProductMetrics(@Body WAAddDataModel<ShippingMetricsModel> updateDataModel, Callback<String> callback);

    @POST("/product_details3/add-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void addAssuredPurchase(@Body WAAddDataModel<AssuredPurchase> updateDataModel, Callback<String> callback);

    @GET("/merchant_profile3/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getMerchantProfileData(@Query("query") String query, Callback<WebActionModel<MerchantProfileModel>> callback);

    @GET("/SellerInformationFetch")
        //@Headers({"Authorization: " + Constants.WA_KEY})
    void getSellerProfileData(@Query("sellerId") String sellerId, Callback<WebResponseModel<SellerProfile>> callback);


    /**
     * Dev Endpoint Need to be replace
     */

    /**
     * New
     *
     * @param map
     * @param callback
     */
    @GET("/Product/v1/GetListings")
    void getAllProducts(@QueryMap Map<String, String> map, Callback<List<Product>> callback);

    @GET("/Product/v1/categories")
    void getAllCategories(@QueryMap Map<String, String> map, Callback<List<String>> callback);

    @GET("/Product/v1/tags")
    void getAllTags(@QueryMap Map<String, String> map, Callback<List<String>> callback);

    @Headers({"Content-Type: application/json"})
    @MOD_DELETE("/Product/v1/Delete")
    void removeProduct(@Body HashMap<String, String> map, Callback<String> callback);

    @PUT("/Product/v1/Update")
    void updateProduct(@Body Product_Gallery_Update_Model model, Callback<ArrayList<String>> callback);


    @POST("/product_details/add-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void addAssuredPurchaseDetails(@Body WAAddDataModel<AssuredPurchase> updateDataModel, Callback<String> callback);

    @GET("/product_details/get-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getAssuredPurchaseDetails(@Query("query") String query, Callback<WebActionModel<AssuredPurchase>> callback);

    @POST("/product_details/update-data")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void updateAssuredPurchaseDetails(@Body WaUpdateDataModel updateDataModel, Callback<String> callback);

    @POST("/api/Seller/InformationSave")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void saveBankInformation(@Body BankInformation bankInformation, Callback<WebResponseModel<Object>> callback);

    @GET("/api/Seller/InformationFetch")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getBankInformation(@Query("sellerId") String sellerId, Callback<WebResponseModel<BankInformation>> callback);

    @POST("/api/Seller/CreateOrUpdatePickupAddress")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void savePickupAddress(@Body AddressInformation addressInformation, Callback<WebResponseModel<AddressInformation>> callback);

    @GET("/api/Seller/PickupAddressList")
    @Headers({"Authorization: " + Constants.WA_KEY})
    void getPickupAddress(@Query("websiteId") String websiteId, Callback<WebResponseModel<List<AddressInformation>>> callback);
}