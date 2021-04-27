package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.services.remote.NfxFacebookAnalyticsRemoteDataSource

object WebActionBoostKitRepository : AppBaseRepository<WebActionBoostKitRepository, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<WebActionBoostKitRepository> {
        return WebActionBoostKitRepository::class.java
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }
}