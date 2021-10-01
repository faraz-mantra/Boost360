package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.NowfloatsApiRepository
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.appservice.model.staffModel.*
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


  fun updateStaffImage(@Body request: StaffUpdateImageRequest?): LiveData<BaseResponse> {
    return StaffNowFloatsRepository.updateImage(request = request).toLiveData()
  }

  fun getSearchListings(
    fpTag: String?,
    fpId: String?,
    searchString: String? = "",
    offset: Int? = 0,
    limit: Int? = 0
  ): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getServiceSearchListing(fpTag, fpId, searchString, offset, limit)
      .toLiveData()
  }

}