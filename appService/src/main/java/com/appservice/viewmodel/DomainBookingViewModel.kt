package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.domainBooking.request.ExistingDomainRequest
import com.appservice.rest.repository.AzureWebsiteNewRepository
import com.appservice.rest.repository.BoostPluginWithFloatsRepository
import com.appservice.rest.repository.RiaNowFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class DomainBookingViewModel : BaseViewModel() {

  fun domainDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return BoostPluginWithFloatsRepository.domainDetails(fpId, clientId).toLiveData()
  }

  fun addExistingDomain(fpId: String?, clientId: String?, existingDomainRequest: ExistingDomainRequest): LiveData<BaseResponse> {
    return RiaNowFloatsRepository.addExistingDomainDetails(fpId, clientId, existingDomainRequest).toLiveData()
  }

  fun getFeatureDetails(fpId: String?, clientId: String?): LiveData<BaseResponse> {
    return AzureWebsiteNewRepository.getFeatureDetails(fpId,clientId).toLiveData()
  }
}