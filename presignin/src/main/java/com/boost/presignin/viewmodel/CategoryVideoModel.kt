package com.boost.presignin.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.boost.presignin.rest.repository.CategoryRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class CategoryVideoModel : BaseViewModel() {
    fun getCategories(context: Context): LiveData<BaseResponse> {
        return CategoryRepository.getCategories(context).toLiveData()
    }
}