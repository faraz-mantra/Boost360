package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.panGst.PanGstUpdateBody
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class BusinessVerificationViewModel : BaseViewModel() {

  fun panGstUpdate(body: PanGstUpdateBody): LiveData<BaseResponse> {
    return WithFloatTwoRepository.panGstUpdate(body).toLiveData()
  }

  fun getPanGstDetail(fpId: String?,clientId:String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getPanGstDetail(fpId,clientId).toLiveData()
  }
}