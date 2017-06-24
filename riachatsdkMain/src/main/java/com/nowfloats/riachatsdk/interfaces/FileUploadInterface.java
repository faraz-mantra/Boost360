package com.nowfloats.riachatsdk.interfaces;

import com.nowfloats.riachatsdk.models.FileResultModel;

import retrofit.Callback;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by NowFloats on 28-03-2017.
 */

public interface FileUploadInterface {
    @Multipart
//    @POST("/riacards/api/Service/ReceiveFile")
    @POST("/plugin/api/Service/ReceiveFile")
    void upload(@Query("filename") String fileName, @Part("file") TypedFile file, @Part("description") String description, Callback<FileResultModel> cb);
}
