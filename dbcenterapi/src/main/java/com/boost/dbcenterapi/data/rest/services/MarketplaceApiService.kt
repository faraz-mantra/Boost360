package com.boost.dbcenterapi.data.rest.services

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.GetAllFeaturesResponse
import com.boost.dbcenterapi.data.api_model.GetFloatingPointWebWidgets.response.GetFloatingPointWebWidgetsResponse
import com.boost.dbcenterapi.data.rest.EndPoints
import com.framework.base.BaseResponse
import com.framework.models.UserProfileData
import com.framework.pref.clientId2
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface MarketplaceApiService {

  @GET(EndPoints.GET_DATA)
  fun getAllFeatures(
    @Header("Authorization") auth: String,
    @Query("website") website_id: String?,
  ): Observable<Response<GetAllFeaturesResponse>>

  @GET(EndPoints.GET_FLOATING_POINT_WEB_WIDGET)
  fun getFloatingPointWebWidgets(
    @Header("Authorization") auth: String,
    @Path("floatingPointId") floatingPointId: String,
    @Query("clientId") clientId: String
  ): Observable<Response<GetFloatingPointWebWidgetsResponse>>

}