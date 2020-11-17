package com.dashboard.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.dashboard.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class AddOnsViewModel : BaseViewModel() {

  fun getBoostAddOns(context: Context): LiveData<BaseResponse> {
    return WithFloatRepository.getBoostAddOns(context).toLiveData()
  }
}
