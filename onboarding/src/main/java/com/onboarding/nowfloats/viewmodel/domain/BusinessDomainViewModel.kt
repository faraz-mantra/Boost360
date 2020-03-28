package com.onboarding.nowfloats.viewmodel.domain

import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.model.domain.BusinessDomainRequest
import com.onboarding.nowfloats.rest.repositories.BusinessDomainRepository

class BusinessDomainViewModel : BaseViewModel() {

    fun checkBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
        return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
    }
}