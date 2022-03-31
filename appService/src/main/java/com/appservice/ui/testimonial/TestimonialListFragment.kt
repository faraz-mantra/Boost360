package com.appservice.ui.testimonial

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.appservice.R
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentTestimonialListBinding
import com.appservice.model.account.testimonial.TestimonialData
import com.appservice.model.account.testimonial.TestimonialDataResponse
import com.appservice.model.account.testimonial.webActionList.TestimonialWebActionResponse
import com.appservice.rest.TaskCode
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.zero.old.AppOnZeroCaseClicked
import org.json.JSONObject
import java.util.*

class TestimonialListFragment : BaseTestimonialFragment<FragmentTestimonialListBinding>(), AppOnZeroCaseClicked {

  private val TAG = "TestimonialListFragment"

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
    Log.i(TAG, "onCreateView: ")
//    hitApi(viewModel?.getWebActionList(webTemplateId, fpTag), R.string.error_getting_web_action)
  }

  override fun onSuccess(it: BaseResponse) {
    when (it.taskcode) {
      TaskCode.GET_WEB_ACTION_TESTIMONIAL.ordinal -> {
        val data = (it as? TestimonialWebActionResponse)
//        hitApi(
//          viewModel?.getTestimonialsList(headerToken, testimonialType, query, 0, 10000),
//          R.string.error_getting_web_action
//        )
      }
      TaskCode.GET_TESTIMONIAL.ordinal -> {
        val response = (it as? TestimonialDataResponse)
      }
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