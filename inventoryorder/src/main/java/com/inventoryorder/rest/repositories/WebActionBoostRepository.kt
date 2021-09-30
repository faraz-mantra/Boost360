package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.apointmentData.addRequest.AddAptConsultRequest
import com.inventoryorder.model.apointmentData.updateRequest.UpdateConsultRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.WebActionBoostKitApiClient
import com.inventoryorder.rest.services.WebActionBoostDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object WebActionBoostRepository :
  AppBaseRepository<WebActionBoostDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<WebActionBoostDataSource> {
    return WebActionBoostDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  fun getWeekSchedule(
    auth: String?,
    query: String?,
    sort: String? = "{CreatedOn: -1}",
    limit: Int = 1000
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getWeekSchedule(auth, query, sort, limit),
      TaskCode.GET_DOCTOR_WEEKLY_SCHEDULE
    )
  }

  fun getAllAptConsultDoctor(
    auth: String?,
    query: String?,
    sort: String? = "{CreatedOn: -1}",
    limit: Int = 1000
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getAllAptConsultDoctor(auth, query, sort, limit),
      TaskCode.GET_DOCTOR_API_DATA
    )
  }

  fun addAptConsultData(auth: String?, request: AddAptConsultRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.addAptConsultData(auth, request),
      TaskCode.ADD_API_CONSULT_DATA
    )
  }

  fun updateAptConsultData(
    auth: String?,
    request: UpdateConsultRequest?
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateAptConsultData(auth, request),
      TaskCode.UPDATE_API_CONSULT_DATA
    )
  }

  override fun getApiClient(): Retrofit {
    return WebActionBoostKitApiClient.shared.retrofit
  }
}