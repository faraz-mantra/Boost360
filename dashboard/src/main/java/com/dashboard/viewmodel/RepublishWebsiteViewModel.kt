package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.rest.repository.WithFloatTwoRepositoryD
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class RepublishWebsiteViewModel : BaseViewModel() {

  fun republishWebsite(clientId: String,fpTag: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepositoryD.republishWebsite(clientId,fpTag = fpTag ?: "").toLiveData()
  }

}
