package com.appservice.staffs.ui.profile

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.appservice.staffs.model.GetStaffListingRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class StaffListingViewModel : BaseViewModel() {
    fun getStaffList(@Body request: GetStaffListingRequest?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getStaffListing(request = request).toLiveData()
    }
}