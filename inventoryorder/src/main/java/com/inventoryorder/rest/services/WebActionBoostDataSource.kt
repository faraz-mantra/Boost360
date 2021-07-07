package com.inventoryorder.rest.services

import com.inventoryorder.model.apointmentData.DoctorAppointmentResponse
import com.inventoryorder.model.apointmentData.addRequest.AddAptConsultRequest
import com.inventoryorder.model.apointmentData.updateRequest.UpdateConsultRequest
import com.inventoryorder.model.weeklySchedule.GetDoctorWeeklySchedule
import com.inventoryorder.rest.EndPoints
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface WebActionBoostDataSource {

  @GET(EndPoints.WEEKLY_SCHEDULE_DOCTOR)
  fun getWeekSchedule(
    @Header("Authorization") auth: String?,
    @Query("query", encoded = true) query: String?,
    @Query("sort") sort: String?,
    @Query("limit") limit: Int?
  ): Observable<Response<GetDoctorWeeklySchedule>>

  @GET(EndPoints.ALL_APT_DOCTOR)
  fun getAllAptConsultDoctor(
    @Header("Authorization") auth: String?,
    @Query("query", encoded = true) query: String?,
    @Query("sort") sort: String?,
    @Query("limit") limit: Int?
  ): Observable<Response<DoctorAppointmentResponse>>

  @POST(EndPoints.ADD_APT_CONSULT_DATA)
  fun addAptConsultData(
    @Header("Authorization") auth: String?,
    @Body request: AddAptConsultRequest?
  ): Observable<Response<ResponseBody>>

  @POST(EndPoints.UPDATE_APT_CONSULT_DATA)
  fun updateAptConsultData(
    @Header("Authorization") auth: String?,
    @Body request: UpdateConsultRequest?
  ): Observable<Response<ResponseBody>>
}