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

  @Headers("X-Auth-Version: 2")
  @GET(EndPoints.WEEKLY_SCHEDULE_DOCTOR)
  fun getWeekSchedule(
    @Header("X-User-Id") auth: String?,
    @Query("query", encoded = true) query: String?,
    @Query("sort") sort: String?,
    @Query("limit") limit: Int?
  ): Observable<Response<GetDoctorWeeklySchedule>>

  @Headers("X-Auth-Version: 2")
  @GET(EndPoints.ALL_APT_DOCTOR)
  fun getAllAptConsultDoctor(
    @Header("X-User-Id") auth: String?,
    @Query("query", encoded = true) query: String?,
    @Query("sort") sort: String?,
    @Query("limit") limit: Int?
  ): Observable<Response<DoctorAppointmentResponse>>

  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.ADD_APT_CONSULT_DATA)
  fun addAptConsultData(
    @Header("X-User-Id") auth: String?,
    @Body request: AddAptConsultRequest?
  ): Observable<Response<ResponseBody>>

  @Headers("X-Auth-Version: 2")
  @POST(EndPoints.UPDATE_APT_CONSULT_DATA)
  fun updateAptConsultData(
    @Header("X-User-Id") auth: String?,
    @Body request: UpdateConsultRequest?
  ): Observable<Response<ResponseBody>>
}