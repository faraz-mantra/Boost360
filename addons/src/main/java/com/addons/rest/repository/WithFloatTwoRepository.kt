package com.addons.rest.repository

import com.addons.base.rest.AppBaseRepository

import com.addons.rest.apiClients.WithFloatsTwoApiClient
import com.addons.rest.services.WithFloatTwoRemoteData
import com.addons.rest.services.local.MarketPlaceLocalDataSource
import retrofit2.Retrofit

object WithFloatTwoRepository :
    AppBaseRepository<WithFloatTwoRemoteData, MarketPlaceLocalDataSource>() {


    override fun getRemoteDataSourceClass(): Class<WithFloatTwoRemoteData> {
        return WithFloatTwoRemoteData::class.java
    }

    override fun getLocalDataSourceInstance(): MarketPlaceLocalDataSource {
        return MarketPlaceLocalDataSource
    }

    override fun getApiClient(): Retrofit {
        return WithFloatsTwoApiClient.shared.retrofit
    }
}
