package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.account.AccountCreateRequest
import com.appservice.model.account.BankAccountDetailsN
import com.appservice.model.account.testimonial.addEdit.DeleteTestimonialRequest
import com.appservice.rest.repository.BoostKitDevRepository
import com.appservice.rest.repository.RazorRepository
import com.appservice.rest.repository.WithFloatRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData
import org.json.JSONObject

class TestimonialViewModel : BaseViewModel() {

  fun getWebActionList(themeID: String?, websiteId: String?): LiveData<BaseResponse> {
    return BoostKitDevRepository.getWebActionList(themeID, websiteId).toLiveData()
  }

  fun getTestimonialsList(token: String?,testimonialType: String?, query: JSONObject?, skip: Int, limit: Int): LiveData<BaseResponse> {
    return BoostKitDevRepository.getTestimonialsList(token,testimonialType, query, skip, limit).toLiveData()
  }

//  fun addTestimonials(token: String?, testimonialType: String?, body: AddTestimonialsData?): LiveData<BaseResponse> {
//    return BoostKitDevRepository.addTestimonials(token, testimonialType, body).toLiveData()
//  }
//
//  fun updateTestimonials(token: String?, testimonialType: String?, body: UpdateTestimonialsData?): LiveData<BaseResponse> {
//    return BoostKitDevRepository.updateTestimonials(token, testimonialType, body).toLiveData()
//  }

  fun deleteTestimonials(token: String?,testimonialType: String?, body: DeleteTestimonialRequest?): LiveData<BaseResponse> {
    return BoostKitDevRepository.deleteTestimonials(token,testimonialType, body).toLiveData()
  }
}
