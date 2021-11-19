package com.framework.firebaseUtils.firestore.restApi.repository

import com.framework.base.BaseLocalService
import com.framework.base.BaseRepository
import com.framework.base.BaseResponse
import com.framework.firebaseUtils.firestore.restApi.DrScoreApiClient
import com.framework.firebaseUtils.firestore.restApi.TaskCode
import com.framework.firebaseUtils.firestore.restApi.model.CreateDrRequest
import com.framework.firebaseUtils.firestore.restApi.model.UpdateDrRequest
import com.framework.firebaseUtils.firestore.restApi.service.DrScoreRemoteData
import io.reactivex.Observable
import retrofit2.Retrofit

object DrScoreRepository : BaseRepository<DrScoreRemoteData, BaseLocalService>() {

  fun createDrScoreData(request: CreateDrRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.createDrScoreData(request),
      TaskCode.CREATE_DR_SCORE.ordinal
    )
  }

  fun updateDrScoreData(request: UpdateDrRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.updateDrScoreData(request),
      TaskCode.UPDATE_DR_SCORE.ordinal
    )
  }

  override fun getRemoteDataSourceClass(): Class<DrScoreRemoteData> {
    return DrScoreRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): BaseLocalService {
    return BaseLocalService()
  }

  override fun getApiClient(): Retrofit {
    return DrScoreApiClient.shared.retrofit
  }
}
