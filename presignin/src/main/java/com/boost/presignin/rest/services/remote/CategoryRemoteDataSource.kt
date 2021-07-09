package com.boost.presignin.rest.services.remote

import com.boost.presignin.rest.response.ResponseDataCategory
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET

interface CategoryRemoteDataSource {

    @GET("")
    fun getCategories(): Observable<Response<ResponseDataCategory>>

}