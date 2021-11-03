package com.festive.poster.reset.services

import com.festive.poster.reset.EndPoints
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Query

interface WithFloatsRemoteData {

    @PUT(EndPoints.UPLOAD_USER_PROFILE_IMAGE)
    fun uploadUserProfileImage(
        @Query("clientId") clientId: String?,
        @Query("loginId") loginId: String?,
        @Query("fileName") fileName: String?,
        @Body file: RequestBody?
    ): Observable<Response<String>>
}