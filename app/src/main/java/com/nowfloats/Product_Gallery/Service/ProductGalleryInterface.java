package com.nowfloats.Product_Gallery.Service;

import com.nowfloats.Product_Gallery.Model.ProductListModel;
import com.nowfloats.Product_Gallery.Model.Product_Gallery_Update_Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.QueryMap;

/**
 * Created by guru on 08-06-2015.
 */
public interface ProductGalleryInterface {
    @GET("/Product/v1/GetListings")
    public void getProducts(@QueryMap Map<String,String> map, Callback<ArrayList<ProductListModel>> callback);

    @Headers({"Content-Type: application/json"})
    @POST("/Product/v1/Create")
    public void addProduct(@Body HashMap<String,String> map, Callback<String> callback);


    @PUT("/Product/v1/Update")
    void put_UpdateGalleryUpdate(@Body Product_Gallery_Update_Model model,Callback<ArrayList<String>> callback);


    @FormUrlEncoded
    @PUT("/Product/v1/AddImage")
    public void uploadPic(@Body byte[] image,@QueryMap HashMap<String,String> map,Callback<String> cb);

    @Headers({"Content-Type: application/json"})
    @DELETE("/Product/v1/Delete")
    void deleteProduct(@Body HashMap<String,String> map, Callback<String> callback);

    @Headers({"Content-Type: application/json"})
    @DELETE("/Product/v2/DeleteImage")
    void deleteProductImage(@Body HashMap<String, String> map, Callback<String> callback);
}