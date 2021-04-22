package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BusinessCreateRemoteDataSource {
    @PUT(EndPoints.PUT_CREATE_BUSINESS_URL)
    fun putCreateBusinessOnboarding(@Query("existingProfileId") profileId: String?, @Body request: BusinessCreateRequest): Observable<Response<String>>

}