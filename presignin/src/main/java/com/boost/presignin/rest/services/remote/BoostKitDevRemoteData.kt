package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.category.ApiCategoryResponse
import com.boost.presignin.rest.EndPoints
import com.framework.constants.Constants
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BoostKitDevRemoteData {

  @Headers("Authorization: ${Constants.AUTH_KEY}")
  @GET(EndPoints.GET_CATEGORIES)
  fun getCategories(@Query("website") website: String = "619cc4d1d0d3a1000142190d"): Observable<Response<ApiCategoryResponse>>
}
