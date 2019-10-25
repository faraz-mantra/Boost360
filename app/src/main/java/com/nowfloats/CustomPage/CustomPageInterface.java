package com.nowfloats.CustomPage;

import com.nowfloats.CustomPage.Model.CreatePageModel;
import com.nowfloats.CustomPage.Model.CustomPageLink;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.CustomPage.Model.PageDetail;
import com.nowfloats.CustomPage.Model.UploadImageToS3Model;
import com.nowfloats.CustomPage.Model.UploadImageToS3ResponseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by guru on 25/08/2015.
 */
public interface CustomPageInterface {
    @GET("/Discover/v1/floatingPoint/{FPTAG}/getcustompages/{CLIENTID}")
    public void getPageList(@Path("FPTAG") String FPTAG,
                            @Path("CLIENTID") String CLIENTID,
                            Callback<ArrayList<CustomPageModel>> callback);

    @POST("/Discover/v1/floatingpoint/custompage/create")
    public void createPage(@Body CreatePageModel s,Callback<String> callback);

    @GET("/Discover/v1/floatingPoint/{FPTAG}/getcustompagedetails/{PAGEID}/{CLIENTID}")
    public void getPageDetail(@Path("FPTAG") String FPTAG,
                              @Path("PAGEID") String PAGEID,
                              @Path("CLIENTID") String CLIENTID,
                              Callback<List<PageDetail>> callback);
    @POST("/Discover/v1/floatingpoint/custompage/update")
    public void updatePage(@Body HashMap<String,String> s,Callback<String> callback);

    @POST("/v1")
    void uploadImageToS3(@Body UploadImageToS3Model uploadImageToS3Model , Callback<UploadImageToS3ResponseModel> callback);

    @GET("/api/KitsuneData/GetBusinessCustomPages")
     void getPageUrl(@Query("tag") String tag,@Query("skip") int skip,@Query("limit") int limit, @Query("sortDirection") int sortDirection,Callback<CustomPageLink> callback);

//    @DELETE("/Discover/v1/floatingpoint/custompage/delete")
//    public void deletePage(@Body HashMap<String,String> s,Callback<String> callback);
}