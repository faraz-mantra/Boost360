package com.appservice.ui.testimonial

import androidx.databinding.ViewDataBinding
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.PreferenceConstant
import com.appservice.viewmodel.TestimonialViewModel
import com.framework.exceptions.IllegalFragmentTypeException

open class BaseTestimonialFragment<binding : ViewDataBinding> : AppBaseFragment<binding, TestimonialViewModel>() {

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