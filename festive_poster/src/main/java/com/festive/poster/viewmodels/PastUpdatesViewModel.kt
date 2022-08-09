package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.asPastPromotionalCategoryModels
import com.festive.poster.models.promoModele.PastPromotionalCategoryModel
import com.festive.poster.models.promoModele.TagListRequest
import com.festive.poster.reset.repo.NowFloatsRepository
import com.festive.poster.reset.repo.WithFloatTwoRepository
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
            val catUi =NowFloatsRepository.getCategoriesUI()
            _promoUpdatesCats.postValue(catUi.asPastPromotionalCategoryModels().toArrayList())
        }
    }

    fun getPastUpdatesListV6(fpId: String?, clientId: String, postType:Int?, tagListRequest: TagListRequest): LiveData<BaseResponse> {
        return WithFloatTwoRepository.getPastUpdatesListV6(fpId = fpId, clientId = clientId, postType = postType, tagRequest = tagListRequest).toLiveData()
    }
}