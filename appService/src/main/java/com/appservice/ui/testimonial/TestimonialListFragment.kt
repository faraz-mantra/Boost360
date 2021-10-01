package com.appservice.ui.testimonial

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import org.json.JSONObject
import java.util.ArrayList

class TestimonialListFragment : BaseTestimonialFragment<FragmentTestimonialListBinding>(), AppOnZeroCaseClicked {

  private var isEdit: Boolean = false
  private var headerToken = "59c89bbb5d64370a04c9aea1"
  private var testimonialType = "testimonials"
  private val allTestimonialType = listOf("testimonials", "testimonial", "guestreviews")
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
    hitApi(viewModel?.getWebActionList(webTemplateId, fpTag), R.string.error_getting_web_action)
  }

  override fun onSuccess(it: BaseResponse) {
    when (it.taskcode) {
      TaskCode.GET_WEB_ACTION_TESTIMONIAL.ordinal -> {
        val data = (it as? TestimonialWebActionResponse)
        if (data?.webActions.isNullOrEmpty().not()) {
          loopBreak@ for (action in data?.webActions!!) {
            for (type in allTestimonialType) {
              if (action?.name.equals(type, ignoreCase = true)) {
                testimonialType = action?.name!!
                break@loopBreak
              }
            }
          }
        }
        data?.token?.let { headerToken = it }
        val query = JSONObject()
        query.put("WebsiteId", fpTag)
        hitApi(
          viewModel?.getTestimonialsList(headerToken, testimonialType, query, 0, 10000),
          R.string.error_getting_web_action
        )
      }
      TaskCode.GET_TESTIMONIAL.ordinal -> {
        val response = (it as? TestimonialDataResponse)
        if (response?.data.isNullOrEmpty().not()) {
//          binding?.emptyLayout?.gone()
          binding?.rvTestimonial?.visible()
          setTestimonialAdapter(response?.data!!)
        } else {
          binding?.rvTestimonial?.gone()
//          binding?.emptyLayout?.visible()
         

        }
      }
    }
  }

  private fun setTestimonialAdapter(data: ArrayList<TestimonialData>) {

  }

  override fun onFailure(it: BaseResponse) {

  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_add, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_add -> {
        addTestimonial()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun addTestimonial() {
    val bundle: Bundle = Bundle.EMPTY
    startTestimonialFragmentActivity(
      FragmentType.TESTIMONIAL_ADD_EDIT_FRAGMENT,
      bundle,
      clearTop = false,
      isResult = true
    )
  }

  override fun primaryButtonClicked() {
    addTestimonial()
  }

  override fun secondaryButtonClicked() {
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }


}