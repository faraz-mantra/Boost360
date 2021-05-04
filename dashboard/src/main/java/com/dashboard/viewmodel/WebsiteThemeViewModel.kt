package com.dashboard.viewmodel

import androidx.lifecycle.LiveData
import com.dashboard.model.websitetheme.WebsiteThemeUpdateRequest
import com.dashboard.rest.repository.NowFloatsRepository
import com.dashboard.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class WebsiteThemeViewModel : BaseViewModel() {
  fun getWebsiteTheme(floatingPointId: String): LiveData<BaseResponse> {
    return NowFloatsRepository.getWebsiteCustomTheme(floatingPointId).toLiveData()
  }

  fun updateWebsiteTheme(request: WebsiteThemeUpdateRequest): LiveData<BaseResponse> {
    return NowFloatsRepository.updateWebsiteTheme(request).toLiveData()
  }
}