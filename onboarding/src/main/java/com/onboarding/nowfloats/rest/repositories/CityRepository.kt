package com.onboarding.nowfloats.rest.repositories

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.services.local.CityLocalDataSource
import com.onboarding.nowfloats.rest.services.remote.CityRemoteDataSource
import io.reactivex.Observable

object CityRepository : AppBaseRepository<CityRemoteDataSource, CityLocalDataSource>() {

  fun getCities(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(CityLocalDataSource.getCities(context), Taskcode.GET_CITIES)
  }

  override fun getRemoteDataSourceClass(): Class<CityRemoteDataSource> {
    return CityRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): CityLocalDataSource {
    return CityLocalDataSource
  }

}