package com.appservice.staffs.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.appservice.staffs.model.*
import com.appservice.ui.catalog.common.AppointmentModel
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class StaffViewModel : BaseViewModel() {
    fun createStaffProfile(@Body request: StaffCreateProfileRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.createProfile(request = request).toLiveData()
    }

    fun updateStaffProfile(@Body request: StaffProfileUpdateRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.updateProfile(request = request).toLiveData()
    }

    fun getStaffList(@Body request: GetStaffListingRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getStaffListing(request = request).toLiveData()
    }

    fun getStaffDetails(@Body staffId: String?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getStaffDetails(request = staffId).toLiveData()
    }

    fun getServiceListing(@Body request: ServiceListRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getServicesListing(request = request).toLiveData()
    }

    fun updateStaffTiming(@Body request: StaffTimingAddUpdateRequest): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.staffUpdateTimings(request = request).toLiveData()

    }

    fun addStaffTiming(@Body request: StaffTimingAddUpdateRequest): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.addStaffTiming(request = request).toLiveData()

    }
    fun deleteStaffProfile(@Body request: StaffDeleteImageProfileRequest): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.deleteStaffProfile(request = request).toLiveData()

    }

    fun getStaffTimings(): MutableLiveData<ArrayList<AppointmentModel>> {
        return MutableLiveData(AppointmentModel.getDefaultTimings())
    }


}