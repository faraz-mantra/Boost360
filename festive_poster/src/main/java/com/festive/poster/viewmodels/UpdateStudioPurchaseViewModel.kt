package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.reset.repo.DevBoostRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class UpdateStudioPurchaseViewModel: BaseViewModel() {

    fun getUpgradeData(): LiveData<BaseResponse> {
        return DevBoostRepository.getUpgradeData().toLiveData()
    }


}