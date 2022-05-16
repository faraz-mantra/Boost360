package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.PanGstUpdateBody
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.rest.TaskCode
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import io.reactivex.Observable

class BusinessVerificationViewModel:BaseViewModel() {

    fun panGstUpdate(body: PanGstUpdateBody): LiveData<BaseResponse> {
        return WithFloatTwoRepository.panGstUpdate(
                body
            ).toLiveData()

    }
}