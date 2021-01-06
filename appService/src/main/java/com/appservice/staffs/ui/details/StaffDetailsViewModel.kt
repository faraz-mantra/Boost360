package com.appservice.staffs.ui.details

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.StaffWithFloatsRepository
import com.appservice.staffs.model.StaffCreateProfileRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class StaffDetailsViewModel : BaseViewModel() {
    fun createStaffProfile(@Body request: StaffCreateProfileRequest?): LiveData<BaseResponse> {
        return StaffWithFloatsRepository.createProfile(request = request).toLiveData()
    }
}