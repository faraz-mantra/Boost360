package com.boost.presignin.viewmodel

import BusinessDomainRequest
import android.content.Context
import androidx.lifecycle.LiveData
import com.boost.presignin.model.business.BusinessCreateRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.rest.repository.*
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

  fun postCheckBusinessDomain(request: BusinessDomainRequest): LiveData<BaseResponse> {
    return BusinessDomainRepository.postCheckBusinessDomain(request).toLiveData()
  }

  fun createMerchantProfile(request: CreateProfileRequest?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.createUserProfile(request).toLiveData()
  }

  fun putCreateBusinessV6(profileId: String?, request: BusinessCreateRequest): LiveData<BaseResponse> {
    return BusinessCreateRepository.putCreateBusinessV6(profileId, request).toLiveData()
  }
}