package com.dashboard.rest.services

import com.dashboard.rest.EndPoints
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WebActionKitsuneRemoteData {
    @Multipart
    @POST(EndPoints.WEB_ACTION_KITSUNE_UPLOAD_FILE)
    fun uploadOwnersProfileImage(
            @Header("Authorization") auth: String?,
            @Query("assetFileName") assetFileName: String?,
            @Part file: MultipartBody.Part?
    ): Observable<Response<ResponseBody>>
}