package com.marketplace.rest.repository

import com.framework.base.BaseResponse
import com.marketplace.base.rest.AppBaseRepository
import com.marketplace.rest.TaskCode
import com.marketplace.rest.apiClients.DeveloperBoostKitApiClient

import com.marketplace.rest.apiClients.WithFloatsTwoApiClient
import com.marketplace.rest.services.DeveloperBoostKitRemoteData
import com.marketplace.rest.services.WithFloatTwoRemoteData
import com.marketplace.rest.services.local.MarketPlaceLocalDataSource
import io.reactivex.Observable
import retrofit2.Retrofit

object DeveloperBoostKitRepository : AppBaseRepository<DeveloperBoostKitRemoteData, MarketPlaceLocalDataSource>() {

    fun getAllFeatures(auth:String,website:String):Observable<BaseResponse>{
        return makeRemoteRequest(remoteDataSource.getAllFeatures(auth, website),taskCode = TaskCode.GET_ALL_FEATURES)
    }

    override fun getRemoteDataSourceClass(): Class<DeveloperBoostKitRemoteData> {
        return DeveloperBoostKitRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): MarketPlaceLocalDataSource {
        return MarketPlaceLocalDataSource
    }

    override fun getApiClient(): Retrofit {
        return DeveloperBoostKitApiClient.shared.retrofit
    }


}
