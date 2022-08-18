package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appservice.model.updateBusiness.pastupdates.PastPromotionalCategoryModel
import com.appservice.model.updateBusiness.pastupdates.TagListRequest
import com.appservice.model.updateBusiness.pastupdates.asPastPromotionalCategoryModels
import com.appservice.rest.repository.NowfloatsApiRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.utils.toArrayList
import kotlinx.coroutines.launch

class PastUpdatesViewModel:BaseViewModel() {

    private val _promoUpdatesCats
    = MutableLiveData<ArrayList<PastPromotionalCategoryModel>>()

    val promoUpdatesCats :LiveData<ArrayList<PastPromotionalCategoryModel>>
    get() = _promoUpdatesCats

    init {
        fetchPromoCats()
    }

    fun fetchPromoCats(){
        viewModelScope.launch {
            val catUi =NowfloatsApiRepository.getCategoriesUI()
            _promoUpdatesCats.postValue(catUi.asPastPromotionalCategoryModels().toArrayList())
        }
    }

    fun getPastUpdatesListV6(fpId: String?, clientId: String, postType:Int?, tagListRequest: TagListRequest): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getPastUpdatesListV6(fpId = fpId, clientId = clientId, postType = postType, tagRequest = tagListRequest).toLiveData()
    }
}