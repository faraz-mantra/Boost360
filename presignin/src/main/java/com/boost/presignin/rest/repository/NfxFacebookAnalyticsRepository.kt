package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.services.remote.NfxFacebookAnalyticsRemoteDataSource

object NfxFacebookAnalyticsRepository : AppBaseRepository<NfxFacebookAnalyticsRemoteDataSource, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<NfxFacebookAnalyticsRemoteDataSource> {
        return NfxFacebookAnalyticsRemoteDataSource::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }
}