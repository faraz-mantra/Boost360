package com.framework.models.firestore.restApi.service

import com.framework.models.firestore.restApi.EndPoints
import com.framework.models.firestore.restApi.model.CreateDrRequest
import com.framework.models.firestore.restApi.model.UpdateDrRequest
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DrScoreRemoteData {

  @POST(EndPoints.CREATE_DR_SCORE)
  fun createDrScoreData(@Body request: CreateDrRequest?): Observable<Response<Any>>

  @POST(EndPoints.UPDATE_DR_SCORE)
  fun updateDrScoreData(@Body request: UpdateDrRequest?): Observable<Response<Any>>

}