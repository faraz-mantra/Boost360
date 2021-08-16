package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.RazorApiClient
import com.appservice.rest.services.BoostKitDevRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.Retrofit

object BoostKitDevRepository : AppBaseRepository<BoostKitDevRemoteData, AppBaseLocalService>() {

  fun getWebActionList(themeID: String?, websiteId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getWebActionList(themeID, websiteId),
      TaskCode.GET_WEB_ACTION_TESTIMONIAL
    )
  }

  fun getTestimonialsList(
    token: String?,
    testimonialType: String?,
    query: JSONObject?,
    skip: Int,
    limit: Int
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getTestimonialsList(
        token,
        testimonialType,
        query,
        skip,
        limit
      ), TaskCode.GET_TESTIMONIAL
    )
  }

//  fun addTestimonials(token: String?, testimonialType: String?, body: AddTestimonialsData?): Observable<BaseResponse> {
//    return makeRemoteRequest(remoteDataSource.addTestimonials(token, testimonialType, body), TaskCode.ADD_TESTIMONIAL)
//  }
//
//  fun updateTestimonials(token: String?, testimonialType: String?, body: UpdateTestimonialsData?): Observable<BaseResponse> {
//    return makeRemoteRequest(remoteDataSource.updateTestimonials(token, testimonialType, body), TaskCode.UPDATE_TESTIMONIAL)
//  }

  fun deleteTestimonials(
    token: String?,
    testimonialType: String?,
    body: DeleteTestimonialRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.deleteTestimonials(token, testimonialType, body),
      TaskCode.DELETE_TESTIMONIAL
    )
  }


  override fun getRemoteDataSourceClass(): Class<BoostKitDevRemoteData> {
    return BoostKitDevRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return RazorApiClient.shared.retrofit
  }
}
