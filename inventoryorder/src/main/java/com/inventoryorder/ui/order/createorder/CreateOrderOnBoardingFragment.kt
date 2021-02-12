package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentOrderOnBoardingBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController

class CreateOrderOnBoardingFragment : BaseInventoryFragment<FragmentOrderOnBoardingBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): CreateOrderOnBoardingFragment {
      val fragment = CreateOrderOnBoardingFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent("Clicked on Orders Creation", "ORDERS", it) }

    setOnClickListener(binding?.tvGetStarted, binding?.ivClose)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvGetStarted -> {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentOrderActivity(FragmentType.ADD_PRODUCT, bundle)
      }

      binding?.ivClose -> {
        (context as FragmentContainerOrderActivity).finish()
      }
    }
  }
}