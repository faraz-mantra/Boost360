package com.festive.poster.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.response.GetTemplatesResponse
import com.festive.poster.reset.repo.NowFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import com.framework.utils.toArrayList
import kotlinx.coroutines.launch

class FestivePosterViewModel: BaseViewModel() {

    fun getTemplates(floatingPointId: String?,floatingPointTag: String?,tags:List<String>?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag,tags).toLiveData()
    }

    fun getTemplateConfig(floatingPointId: String?,floatingPointTag: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplateConfig(floatingPointId,floatingPointTag).toLiveData()
    }

    fun prepareTemplatePackList(floatingPointId: String?,floatingPointTag: String?,tags: List<PosterPackTagModel>?){
        val list = ArrayList<PosterPackModel>()
        viewModelScope.launch {
            tags?.forEach {

               val response =  NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag, arrayListOf(it.Tag))

              //  val posterPackModel = PosterPackModel(it,response.Result.Templates.toArrayList())

            }
        }
    }

}