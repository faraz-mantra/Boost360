package com.appservice.staffs.ui.services

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.StaffNowFloatsRepository
import com.appservice.staffs.model.ServiceListRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import retrofit2.http.Body

class StaffServicesViewModel : BaseViewModel() {
    fun getServiceListing(@Body request:ServiceListRequest ?): LiveData<BaseResponse> {
        return StaffNowFloatsRepository.getServicesListing(request = request).toLiveData()
    }
}