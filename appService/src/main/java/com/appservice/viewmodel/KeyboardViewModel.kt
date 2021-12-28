package com.appservice.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.appservice.rest.repository.KeyboardRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class KeyboardViewModel : BaseViewModel() {

  fun getMessageUpdates(context: Context): LiveData<BaseResponse> {
    return KeyboardRepository.getTabsKeyboard(context).toLiveData()
  }

}