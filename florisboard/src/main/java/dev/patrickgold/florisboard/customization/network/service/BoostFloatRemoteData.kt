package dev.patrickgold.florisboard.customization.network.service

import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import dev.patrickgold.florisboard.customization.network.EndPoints
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BoostFloatRemoteData {

  @FormUrlEncoded
  @POST(EndPoints.MERCHANT_PROFILE)
  suspend fun getMerchantProfile(@Field("floatingpointId") floatingId: String?): Response<MerchantProfileResponse>

}