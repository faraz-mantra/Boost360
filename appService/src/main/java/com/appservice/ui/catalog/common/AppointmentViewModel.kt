package com.appservice.ui.catalog.common

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.WithFloatTwoRepository
import com.appservice.ui.catalog.RequestWeeklyAppointment
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class AppointmentViewModel : BaseViewModel() {
    fun addServiceTiming(request: RequestWeeklyAppointment?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.addServiceTiming(request).toLiveData()
    }

    fun updateServiceTiming(request: RequestWeeklyAppointment?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.updateServiceTiming(request).toLiveData()
    }

    fun getServiceTiming(request: String?): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getServiceTiming(request).toLiveData()
    }
}