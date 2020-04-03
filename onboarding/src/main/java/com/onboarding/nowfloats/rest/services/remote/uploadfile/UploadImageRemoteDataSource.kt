package com.onboarding.nowfloats.rest.services.remote.uploadfile

import com.onboarding.nowfloats.rest.EndPoints
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Query

interface UploadImageRemoteDataSource {
    @Headers("Accept: application/json", "Content-Type: application/octet-stream")
    @PUT(EndPoints.PUT_UPLOAD_IMAGE)
    fun putUploadImageBusiness(@Query("clientId") clientId: String?,
                               @Query("fpId") fpId: String?,
                               @Query("identifierType") identifierType: String?,
                               @Query("fileName") fileName: String?,
                               @Body requestBody: RequestBody?): Observable<Response<String>>
}