package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseLocalService
import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.model.serviceTiming.AddServiceTimingRequest
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.StaffNowFloatsApiClient
import com.appservice.rest.services.StaffNowFloatsRemoteData
import com.appservice.model.staffModel.*
import com.appservice.ui.testimonial.newflow.model.DataItem
import com.appservice.ui.testimonial.newflow.model.DeleteTestimonialRequestNew
import com.appservice.ui.testimonial.newflow.model.TestimonialListingRequest
import com.appservice.ui.testimonial.newflow.model.UpdateTestimonialImage
import com.framework.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit

object NowFloatsRepository : AppBaseRepository<StaffNowFloatsRemoteData, AppBaseLocalService>() {
    override fun getRemoteDataSourceClass(): Class<StaffNowFloatsRemoteData> {
        return StaffNowFloatsRemoteData::class.java
    }

    fun createProfile(request: StaffCreateProfileRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createStaffProfile(request), TaskCode.CREATE_STAFF_PROFILE)
    }

    override fun getApiClient(): Retrofit {
        return StaffNowFloatsApiClient.shared.retrofit
    }

    override fun getLocalDataSourceInstance(): AppBaseLocalService {
        return AppBaseLocalService()
    }

    fun getServicesListing(request: ServiceListRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.fetchServices(request), TaskCode.GET_SERVICE_LISTING)
    }

    fun getStaffListing(request: GetStaffListingRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.fetchStaffList(request), TaskCode.GET_STAFFS_PROFILE_LIST)
    }

    fun updateProfile(request: StaffProfileUpdateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffProfileUpdate(request), TaskCode.GET_STAFFS_PROFILE_LIST)
    }
    fun updateImage(request: StaffUpdateImageRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffUpdateImage(request), TaskCode.UPDATE_ACCOUNT)
    }
    fun addStaffTiming(request: StaffTimingAddUpdateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffAddTimings(request), TaskCode.POST_ADD_SERVICE_TIMING)
    }

    fun staffUpdateTimings(request: StaffTimingAddUpdateRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffUpdateTimings(request), TaskCode.POST_UPDATE_SERVICE_TIMING)
    }


    fun getStaffDetails(request: String?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffDetails(request), TaskCode.GET_STAFFS_DETAILS)
    }

    fun deleteStaffProfile(request: StaffDeleteImageProfileRequest): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.staffProfileDelete(request),TaskCode.DELETE_STAFF_PROFILE)
    }

    fun addServiceTiming(request: AddServiceTimingRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.addServiceTiming(request), TaskCode.POST_ADD_SERVICE_TIMING)

    }

    fun updateServiceTiming(request: AddServiceTimingRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.updateServiceTiming(request), TaskCode.POST_UPDATE_SERVICE_TIMING)

    }

    fun getServiceTiming(serviceId: String?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.getServiceTimings(serviceId), TaskCode.GET_SERVICE_TIMING)

    }

    fun getTestimonialListing(request: TestimonialListingRequest?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.fetchTestimonialList(request), TaskCode.GET_TESTIMONIAL_LISTING)
    }

    fun getTestimonialDetails(id: String?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.getTestimonialData(id), TaskCode.GET_TESTIMONIAL_DETAILS)
    }
    fun createTestimonial(request: DataItem?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createTestimonial(request), TaskCode.ADD_TESTIMONIAL)
    }
    fun updateTestimonial(request: DataItem?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.createTestimonial(request), TaskCode.UPDATE_TESTIMONIAL)
    }
    fun deleteTestimonial(id: DeleteTestimonialRequestNew?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.deleteTestimonial(id), TaskCode.DELETE_TESTIMONIAL)
    }
    fun deleteTestimonialImage(id: DeleteTestimonialRequestNew?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.deleteTestimonialImage(id), TaskCode.DELETE_TESTIMONIAL_IMAGE)
    }
    fun updateTestimonialImage(request: UpdateTestimonialImage?): Observable<BaseResponse> {
        return makeRemoteRequest(remoteDataSource.updateTestimonialImage(request = request), TaskCode.UPDATE_TESTIMONIAL_IMAGE)
    }

}