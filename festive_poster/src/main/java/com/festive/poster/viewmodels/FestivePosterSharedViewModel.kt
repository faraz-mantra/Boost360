package com.festive.poster.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.models.PosterPackModel
import com.framework.models.BaseViewModel

class FestivePosterSharedViewModel: BaseViewModel() {

    val customizationDetails=MutableLiveData<PosterCustomizationModel>()
    var selectedPosterPack:PosterPackModel?=null

}