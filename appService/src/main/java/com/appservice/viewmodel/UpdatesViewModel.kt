package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class UpdatesViewModel: BaseViewModel()  {

  fun getMessageUpdates(map: Map<String?, String?>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getMessageUpdates(map).toLiveData()
  }
}