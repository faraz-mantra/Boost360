package com.festive.poster.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import com.festive.poster.reset.repo.NowFloatsRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class FestivePosterViewModel: BaseViewModel() {

    fun getTemplates(floatingPointId: String?,floatingPointTag: String?,tags:List<String>?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplates(floatingPointId,floatingPointTag,tags).toLiveData()
    }

    fun getTemplateConfig(floatingPointId: String?,floatingPointTag: String?): LiveData<BaseResponse> {
        return NowFloatsRepository.getTemplateConfig(floatingPointId,floatingPointTag).toLiveData()
    }

}