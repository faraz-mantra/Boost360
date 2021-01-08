package com.appservice.rest.services

import com.appservice.rest.EndPoints
import com.appservice.staffs.model.*
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StaffNowFloatsRemoteData {
    @POST(EndPoints.CREATE_STAFF_PROFILE)
    fun createStaffProfile(@Body request: StaffCreateProfileRequest?): Observable<Response<StaffCreateProfileResponse>>

    @POST(EndPoints.FETCH_STAFF_SERVICES)
    fun fetchServices(@Body request: ServiceListRequest?): Observable<Response<ServiceListResponse>>

    @POST(EndPoints.GET_STAFF_LISTING)
    fun fetchStaffList(@Body request: GetStaffListingRequest?): Observable<Response<GetStaffListingResponse>>

    @POST(EndPoints.STAFF_PROFILE_UPDATE)
    fun staffProfileUpdate(@Body request: StaffProfileUpdateRequest?): Observable<Response<ResponseUpdateProfile>>

    @POST(EndPoints.STAFF_PROFILE_DELETE)
    fun staffProfileDelete(@Body request: StaffDeleteImageProfileRequest?): Observable<Response<BaseResponse>>

    @POST(EndPoints.STAFF_DELETE_IMAGE)
    fun staffImageDelete(@Body request: StaffDeleteImageProfileRequest?): Observable<Response<BaseResponse>>

    @POST(EndPoints.STAFF_ADD_TIMING)
    fun staffAddTimings(@Body request: StaffTimingAddUpdateRequest?): Observable<Response<BaseResponse>>

    @POST(EndPoints.STAFF_UPDATE_TIMING)
    fun staffUpdateTimings(@Body request: StaffTimingAddUpdateRequest?): Observable<Response<BaseResponse>>

    @POST(EndPoints.STAFF_UPDATE_IMAGE)
    fun staffUpdateImage(@Body request: StaffUpdateImageRequest?): Observable<Response<BaseResponse>>
//    @GET(EndPoints.GET_STAFF_DETAILS)
//    fun staffGetDetails(@Query(value = "staffId"): Observable<Response<BaseResponse>>


}