package com.boost.presignin.rest.repository

import com.boost.presignin.base.rest.AppBaseLocalService
import com.boost.presignin.base.rest.AppBaseRepository
import com.boost.presignin.rest.TaskCode
import com.boost.presignin.rest.services.remote.NfxFacebookAnalyticsRemoteDataSource
import com.framework.base.BaseResponse
import io.reactivex.Observable

object NfxFacebookAnalyticsRepository : AppBaseRepository<NfxFacebookAnalyticsRemoteDataSource, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<NfxFacebookAnalyticsRemoteDataSource> {
        return NfxFacebookAnalyticsRemoteDataSource::class.java
    }

    fun getSocialTokens(query: String?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.nfxGetSocialTokens(query), TaskCode.POST_CHECK_BUSINESS_DOMAIN)
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }
}