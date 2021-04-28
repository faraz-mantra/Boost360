package com.boost.presignin.rest.services.remote

import io.reactivex.Observable
import com.boost.presignin.model.other.AccountDetailsResponse
import com.boost.presignin.rest.EndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WithFloatsRemoteDataSource {
    @GET(EndPoints.USER_DETAILS_WITH_FLOAT)
    fun userAccountDetail(@Path("fpId") fpId: String?, @Query("clientId") clientId: String?):Observable<Response<AccountDetailsResponse>>
}