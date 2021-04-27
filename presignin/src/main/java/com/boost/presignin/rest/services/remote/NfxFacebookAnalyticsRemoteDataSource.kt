package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.other.NfxGetTokensResponse
import com.boost.presignin.rest.EndPoints
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NfxFacebookAnalyticsRemoteDataSource {
    @Headers("key:78234i249123102398", "pwd:JYUYTJH*(*&BKJ787686876bbbhl)", "Content-Type:application/json")
    @GET(EndPoints.FETCH_DATA_ANALYTICS)
    fun nfxFetchFacebookData(@Query("nowfloats_id") id: String?, @Query("identifier") identifier:String?):Response<NfxGetTokensResponse>

    @Headers("key:78234i249123102398", "pwd:JYUYTJH*(*&BKJ787686876bbbhl)", "Content-Type:application/json")
    @GET(EndPoints.GET_ACCESS_TOKENS)
    fun nfxGetSocialTokens(@Query("nowfloats_id") id: String?):Response<NfxGetTokensResponse>
}