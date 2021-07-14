package com.appservice.ui.testimonial

import androidx.databinding.ViewDataBinding
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.PreferenceConstant
import com.appservice.viewmodel.TestimonialViewModel
import com.framework.exceptions.IllegalFragmentTypeException

open class BaseTestimonialFragment<binding : ViewDataBinding> :
  AppBaseFragment<binding, TestimonialViewModel>() {


  val fpId: String?
    get() {
      return pref?.getString(PreferenceConstant.KEY_FP_ID, "")
    }

  val fpTag: String?
    get() {
      return pref?.getString(PreferenceConstant.GET_FP_DETAILS_TAG, "")
    }

  val experienceCode: String?
    get() {
      return pref?.getString(PreferenceConstant.GET_FP_EXPERIENCE_CODE, "")
    }

  val webTemplateId: String?
    get() {
      return pref?.getString(PreferenceConstant.GET_FP_WEBTEMPLATE_ID, "")
    }

  override fun getLayout(): Int {
    return when (this) {
      is TestimonialListFragment -> R.layout.fragment_testimonial_list
      is TestimonialAddEditFragment -> R.layout.fragment_testimonial_add_edit
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun getViewModelClass(): Class<TestimonialViewModel> {
    return TestimonialViewModel::class.java
  }
}