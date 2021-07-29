package com.marketplace.rest.repository

import com.marketplace.base.rest.AppBaseRepository

import com.marketplace.rest.apiClients.WithFloatsTwoApiClient
import com.marketplace.rest.services.WithFloatTwoRemoteData
import com.marketplace.rest.services.local.MarketPlaceLocalDataSource
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
