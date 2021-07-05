package com.appservice.ui.staffs.ui.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.NowfloatsApiRepository
import com.appservice.rest.repository.NowFloatsRepository
import com.appservice.model.staffModel.*
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class StaffViewModel : BaseViewModel() {
    fun createStaffProfile(@Body request: StaffCreateProfileRequest?): LiveData<BaseResponse> {
        return NowFloatsRepository.createProfile(request = request).toLiveData()
    }

    fun updateStaffProfile(@Body request: StaffProfileUpdateRequest?): LiveData<BaseResponse> {
        return NowFloatsRepository.updateProfile(request = request).toLiveData()
    }

    fun getStaffList(@Body request: GetStaffListingRequest?): LiveData<BaseResponse> {
        return NowFloatsRepository.getStaffListing(request = request).toLiveData()
    }

    fun getStaffDetails(@Body staffId: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.getStaffDetails(request = staffId).toLiveData()
    }

    fun getServiceListing(@Body request: ServiceListRequest?): LiveData<BaseResponse> {
        return NowFloatsRepository.getServicesListing(request = request).toLiveData()
    }

    fun updateStaffTiming(@Body request: StaffTimingAddUpdateRequest): LiveData<BaseResponse> {
        return NowFloatsRepository.staffUpdateTimings(request = request).toLiveData()

    }

    fun addStaffTiming(@Body request: StaffTimingAddUpdateRequest): LiveData<BaseResponse> {
        return NowFloatsRepository.addStaffTiming(request = request).toLiveData()

    }

    fun deleteStaffProfile(@Body request: StaffDeleteImageProfileRequest): LiveData<BaseResponse> {
        return NowFloatsRepository.deleteStaffProfile(request = request).toLiveData()

    }


    fun updateStaffImage(@Body request: StaffUpdateImageRequest?): LiveData<BaseResponse> {
        return NowFloatsRepository.updateImage(request = request).toLiveData()
    }

    fun getSearchListings(fpTag: String?, fpId: String?, searchString: String?="", offset: Int?=0, limit: Int?=0):LiveData<BaseResponse>{
        return NowfloatsApiRepository.getServiceSearchListing(fpTag, fpId, searchString, offset, limit).toLiveData()
    }

}