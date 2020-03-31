package com.onboarding.nowfloats.rest.repositories

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.base.rest.AppBaseRepository
import com.onboarding.nowfloats.rest.Taskcode
import com.onboarding.nowfloats.rest.services.local.feature.FeatureLocalDataSource
import com.onboarding.nowfloats.rest.services.remote.feature.FeatureRemoteDataSource
import io.reactivex.Observable

object FeatureRepository : AppBaseRepository<FeatureRemoteDataSource, FeatureLocalDataSource>() {

  override fun getRemoteDataSourceClass(): Class<FeatureRemoteDataSource> {
    return FeatureRemoteDataSource::class.java
  }

  override fun getLocalDataSourceInstance(): FeatureLocalDataSource {
    return FeatureLocalDataSource
  }
}