package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.domainBooking.request.CreateDomainRequest
import com.appservice.rest.repository.BoostPluginWithFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import org.json.JSONObject

class SearchDomainViewModel : BaseViewModel() {

    fun searchDomain(domain: String, clientId: String, domainType: String): LiveData<BaseResponse> {
        return BoostPluginWithFloatsRepository.searchDomain(domain, clientId, domainType)
            .toLiveData()
    }

    fun createDomain(createDomainRequest: CreateDomainRequest): LiveData<BaseResponse> {
        return BoostPluginWithFloatsRepository.createDomain(createDomainRequest).toLiveData()
    }
}