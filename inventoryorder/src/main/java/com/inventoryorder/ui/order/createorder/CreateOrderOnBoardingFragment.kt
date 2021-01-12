package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentOrderOnBoardingBinding
import com.inventoryorder.ui.BaseInventoryFragment
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

    setOnClickListener(binding?.tvGetStarted)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvGetStarted -> {
        startFragmentOrderActivity(FragmentType.ADD_CUSTOMER, Bundle())
      }
    }
  }
}