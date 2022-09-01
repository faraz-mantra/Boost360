package com.appservice.rest.services

import com.framework.models.UpdateDraftBody
import com.appservice.rest.EndPoints

import okhttp3.ResponseBody
import retrofit2.http.*
import io.reactivex.Observable
import retrofit2.Response

interface UsCentralNowFloatsRemoteData {


  @POST(EndPoints.UPDATE_DRAFT)
  fun updateDraft(
    @Body body: UpdateDraftBody
  ): Observable<Response<ResponseBody>>

}