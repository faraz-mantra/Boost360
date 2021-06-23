package com.boost.presignin.rest.services.remote

import io.reactivex.Observable
import com.boost.presignin.model.other.AccountDetailsResponse
import com.boost.presignin.model.verification.RequestValidateEmail
import com.boost.presignin.model.verification.RequestValidatePhone
import com.boost.presignin.rest.EndPoints
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WithFloatsRemoteDataSource {

  @GET(EndPoints.USER_DETAILS_WITH_FLOAT)
  fun userAccountDetail(@Path("fpId") fpId: String?, @Query("clientId") clientId: String?): Observable<Response<AccountDetailsResponse>>

  @POST(EndPoints.VERIFY_EMAIL)
  fun validateUserEmail(@Body requestValidateEmail: RequestValidateEmail?): Observable<Response<ResponseBody>>

  @POST(EndPoints.VERIFY_PHONE)
  fun validateUserPhone(@Body requestValidatePhone: RequestValidatePhone?): Observable<Response<ResponseBody>>
}