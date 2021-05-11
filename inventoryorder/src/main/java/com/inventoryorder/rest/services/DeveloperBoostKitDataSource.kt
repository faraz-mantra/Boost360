package com.inventoryorder.rest.services

import com.inventoryorder.rest.EndPoints
import com.inventoryorder.ui.tutorials.model.product.TutorialsDataResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DeveloperBoostKitDataSource {

    @GET(EndPoints.BOOST_EDUCATION_DATA)
    fun getTutorialsData(
            @Header("Authorization") auth: String?,
            @Query("website") website: String?,
    ): Observable<Response<TutorialsDataResponse>>


}