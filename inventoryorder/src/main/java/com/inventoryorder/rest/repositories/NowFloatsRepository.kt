package com.inventoryorder.rest.repositories

import com.framework.base.BaseResponse
import com.inventoryorder.base.rest.AppBaseLocalService
import com.inventoryorder.base.rest.AppBaseRepository
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.rest.TaskCode
import com.inventoryorder.rest.apiClients.NowFloatClient
import com.inventoryorder.rest.services.NowFloatsDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object NowFloatsRepository : AppBaseRepository<NowFloatsDataSource, AppBaseLocalService>() {

  override fun getRemoteDataSourceClass(): Class<NowFloatsDataSource> {
    return NowFloatsDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return NowFloatClient.shared.retrofit
  }

  fun getSearchListing(
    fpTag: String,
    fpId: String,
    searchString: String,
    offset: Int,
    limit: Int
  ): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getSearchListing(
        fpTag,
        fpId,
        searchString,
        offset,
        limit
      ), TaskCode.GET_SEARCH_LISTING
    )
  }

  fun getBookingSlots(bookingSlotsRequest: BookingSlotsRequest): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getBookingSlots(bookingSlotsRequest),
      TaskCode.GET_SEARCH_LISTING
    )
  }
}