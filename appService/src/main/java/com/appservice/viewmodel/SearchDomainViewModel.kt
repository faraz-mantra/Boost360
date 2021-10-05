package com.appservice.viewmodel

import androidx.lifecycle.LiveData
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

    fun createDomain(
        clientId: String, domainName: String, domainType: String,
        existingFPTag: String, validityInYears: String, domainOrderType: Int
    ): LiveData<BaseResponse> {
        return BoostPluginWithFloatsRepository.createDomain(
            clientId, domainName, domainType,
            existingFPTag, validityInYears, domainOrderType
        )
            .toLiveData()
    }
}