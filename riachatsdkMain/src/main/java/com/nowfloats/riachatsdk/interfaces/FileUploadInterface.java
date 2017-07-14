package com.nowfloats.riachatsdk.interfaces;

import com.nowfloats.riachatsdk.models.FileResultModel;

import okhttp3.RequestBody;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by NowFloats on 28-03-2017.
 */

public interface FileUploadInterface {

    //    @POST("/riacards/api/Service/ReceiveFile")

    @Multipart
    @POST("/plugin/api/Service/ReceiveFile")
    void upload(@Query("filename") String fileName, @Part("file") TypedFile file, @Part("description") String description, Callback<FileResultModel> cb);

    @POST("/plugin/api/Service/ReceiveFile")
    void uploadBinary(@Query("filename") String fileName, @Body RequestBody photo, Callback<FileResultModel> cb);


}
