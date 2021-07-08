package com.nowfloats.webactions;

import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.nowfloats.ProductGallery.Model.ProductKeywordResponseModel;
import com.nowfloats.webactions.models.*;
import com.nowfloats.webactions.webactioninterfaces.MOD_DELETE;

import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;

/**
 * Created by NowFloats on 11-04-2018.
 */

public interface IWebActionNetworkInterface {

    @GET("/list")
    public void getWebActionList(@Header("Authorization") String authHeader, @QueryMap Map<String, String> query, Callback<WebActionList> callback);

    @GET("/{webactionnname}/get-data")
    public <T> void getDataById(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @Query("_id") String id, Callback<WebActionDataResponse<T>> callback);

    @GET("/{webactionnname}/get-data")
    public <T> void getData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @QueryMap Map<String, String> query, Callback<WebActionDataResponse<T>> callback);

    @GET("/{webactionnname}/get-data")
    public void getProductKeywordsData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @QueryMap Map<String, String> query, Callback<WebActionDataResponse<ProductKeywordResponseModel>> callback);

    @GET("/{webactionnname}/get-data")
    public void getProductImagesData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @QueryMap Map<String, String> query, Callback<WebActionDataResponse<ProductImageResponseModel>> callback);

    @POST("/{webactionnname}/add-data")
    public <T> void addData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @Body WebActionAddDataModel<T> body, Callback<String> callback);

    @POST("/{webactionnname}/update-data")
    public void updateData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @Body WebActionUpdateRequestModel body, Callback<String> callback);

    @MOD_DELETE("/{webactionnname}/delete-data")
    public void deleteData(@Header("Authorization") String authHeader, @Path("webactionnname") String webActionName, @Body WebActionUpdateRequestModel body, Callback<String> callback);

    @Multipart
    @POST("/{webactionnname}/upload-file")
    public void uploadFile(@Header("Authorization") String authHeader, @Query("assetFileName") String assetFileName, @Path("webactionnname") String webActionName, @Part("file") TypedFile file, Callback<String> callback);

}
