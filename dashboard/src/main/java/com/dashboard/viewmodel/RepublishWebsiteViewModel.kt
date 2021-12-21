package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.rest.repository.WithFloatTwoRepositoryD
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class RepublishWebsiteViewModel : BaseViewModel() {

  fun republishWebsite(fpTag: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepositoryD.republishWebsite(fpTag = fpTag ?: "").toLiveData()
  }

}
