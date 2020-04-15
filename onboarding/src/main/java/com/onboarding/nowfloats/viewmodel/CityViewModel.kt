package com.onboarding.nowfloats.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.onboarding.nowfloats.rest.repositories.CityRepository

class CityViewModel : BaseViewModel() {

  fun getCities(context: Context): LiveData<BaseResponse> {
    return CityRepository.getCities(context).toLiveData()
  }
}