package com.festive.poster.reset.repo



import com.festive.poster.base.rest.AppBaseLocalService
import com.festive.poster.base.rest.AppBaseRepository
import com.festive.poster.reset.TaskCode
import com.festive.poster.reset.apiClients.NowFloatsApiClient
import com.festive.poster.reset.apiClients.WithFloatsTwoApiClient
import com.festive.poster.reset.services.NowFloatsRemoteData
import com.festive.poster.reset.services.WithFloatsRemoteData
import com.framework.base.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.Query


object WithFloatsRepository : AppBaseRepository<WithFloatsRemoteData, AppBaseLocalService>() {


  fun uploadUserProfileImage(clientId: String?,loginId:String?,fileName:String, file: RequestBody?): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.uploadUserProfileImage(
        clientId = clientId, loginId=loginId,
        fileName=fileName, file = file
      ), TaskCode.UPLOAD_USER_PROFILE_IMAGE
    )
  }

  override fun getRemoteDataSourceClass(): Class<WithFloatsRemoteData> {
    return WithFloatsRemoteData::class.java
  }



  override fun getApiClient(): Retrofit {
    return WithFloatsTwoApiClient.shared.retrofit
  }

  override fun getLocalDataSourceInstance(): AppBaseLocalService {
    return AppBaseLocalService()
  }
}
