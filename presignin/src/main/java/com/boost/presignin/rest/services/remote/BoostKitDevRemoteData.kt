package com.boost.presignin.rest.services.remote

import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.authToken.AccessTokenResponse
import com.boost.presignin.model.category.ApiCategoryResponse
import com.boost.presignin.model.fpList.FPListResponse
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.model.login.ForgotPassRequest
import com.boost.presignin.model.login.UserProfileVerificationRequest
import com.boost.presignin.model.login.VerificationRequestResult
import com.boost.presignin.model.login.VerifyOtpResponse
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.userprofile.BusinessProfileResponse
import com.boost.presignin.model.userprofile.ConnectUserProfileResponse
import com.boost.presignin.model.userprofile.ResponseMobileIsRegistered
import com.boost.presignin.rest.EndPoints
import com.framework.constants.Constants
import com.framework.pref.clientId
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface BoostKitDevRemoteData {


  @Headers("Authorization: ${Constants.AUTH_KEY}")
  @GET(EndPoints.GET_CATEGORIES)
  fun getCategories(@Query("website") website:String="619cc4d1d0d3a1000142190d"): Observable<Response<ApiCategoryResponse>>

}
