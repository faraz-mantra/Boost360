package com.nowfloats.BusinessProfile.UI.API;

import com.nowfloats.util.Constants;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by NowFloatsDev on 28/05/2015.
 */
public interface ImageUpload_Interface {

    public static final String BASE_URL = Constants.NOW_FLOATS_API_URL+"";


//    @Multipart
//    @POST("/Discover/v1/floatingPoint/createImage?clientId={clientId}&fpId={fpid}&reqType=sequential&reqtId={reqtId}&totalChunks=1&currentChunkNumber=1")
//    void post_uploadIMAGEURI(@Path("clientId") String clientId,
//                             @Path("fpid") String fpid,
//                             @Path("reqtId") String reqtId,
//                             @Part("myfile") TypedFile file,
//
//                             Callback<String> callback);


    @Multipart
    @PUT("/Discover/v1/floatingPoint/createImage?reqType=sequential&totalChunks=1&currentChunkNumber=1")
    void put_uploadIMAGEURI(@Query("clientId") String clientId,
                             @Query("fpid") String fpid,
                             @Query("reqtId") String reqtId,
                             @Part("myfile") TypedFile file,

                             Callback<String> callback);



}
