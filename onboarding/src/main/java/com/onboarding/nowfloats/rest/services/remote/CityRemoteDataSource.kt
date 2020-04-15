package com.onboarding.nowfloats.rest.services.remote

import com.onboarding.nowfloats.rest.response.category.CategoryListResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET


interface CityRemoteDataSource {

  @GET("")
  fun getCategories(): Observable<Response<CategoryListResponse>>

}