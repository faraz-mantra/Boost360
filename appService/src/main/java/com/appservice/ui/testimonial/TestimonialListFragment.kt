package com.appservice.ui.testimonial

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentTestimonialListBinding
import com.framework.base.BaseResponse
import com.framework.views.zero.old.AppOnZeroCaseClicked

class TestimonialListFragment : BaseTestimonialFragment<FragmentTestimonialListBinding>(), AppOnZeroCaseClicked {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): TestimonialListFragment {
      val fragment = TestimonialListFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
  }

  override fun onSuccess(it: BaseResponse) {
    when (it.taskcode) {
    }
  }

  override fun onFailure(it: BaseResponse) {

  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }


  private fun addTestimonial() {
    val bundle: Bundle = Bundle.EMPTY
    startTestimonialFragmentActivity(FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT, bundle, clearTop = false, isResult = true)
  }

  override fun primaryButtonClicked() {
    addTestimonial()
  }

  override fun secondaryButtonClicked() {
    Toast.makeText(activity, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }
}