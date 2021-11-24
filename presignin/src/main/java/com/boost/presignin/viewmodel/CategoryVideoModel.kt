package com.boost.presignin.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import com.boost.presignin.rest.repository.BoostKitDevRepository
import com.boost.presignin.rest.repository.CategoryRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class CategoryVideoModel : BaseViewModel() {
  fun getCategories(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategories(context).toLiveData()
  }

  fun getCategoriesOv2(context: Context): LiveData<BaseResponse> {
    return CategoryRepository.getCategoriesOv2(context).toLiveData()
  }

  fun getCategoriesFromApi(): LiveData<BaseResponse>{
      return BoostKitDevRepository.getCategories().toLiveData()
  }
}