package com.onboarding.nowfloats.viewmodel.business

import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.model.business.BusinessCreateRequest
import com.onboarding.nowfloats.rest.repositories.BusinessCreateRepository
import io.reactivex.Observable

class BusinessCreateViewModel : BaseViewModel() {

    fun createBusinessOnboarding(request: BusinessCreateRequest): Observable<BaseResponse> {
        return BusinessCreateRepository.postCreateBusinessOnboarding(request)
    }
}