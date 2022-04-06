package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.model.testimonial.AddTestimonialImageRequest
import com.appservice.model.testimonial.AddUpdateTestimonialRequest
import com.appservice.model.testimonial.ListTestimonialRequest
import com.appservice.rest.repository.NowfloatsApiRepository
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class TestimonialViewModel : BaseViewModel() {

  fun getTestimonialList(request: ListTestimonialRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getTestimonialList(request).toLiveData()
  }

  fun getTestimonialDetail(testimonialId: String?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getTestimonialDetail(testimonialId).toLiveData()
  }

  fun addTestimonial(request: AddUpdateTestimonialRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.addTestimonial(request).toLiveData()
  }

  fun updateTestimonial(request: AddUpdateTestimonialRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.updateTestimonial(request).toLiveData()
  }

  fun updateTestimonialImage(request: AddTestimonialImageRequest?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.updateTestimonialImage(request).toLiveData()
  }

  fun deleteTestimonial(testimonialId: String?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.deleteTestimonial(testimonialId).toLiveData()
  }

  fun deleteTestimonialImage(testimonialId: String?): LiveData<BaseResponse> {
    return NowfloatsApiRepository.deleteTestimonialImage(testimonialId).toLiveData()
  }
}
