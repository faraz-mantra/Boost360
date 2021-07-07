package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.framework.utils.PreferencesUtils
import com.framework.utils.saveData
import com.framework.webengageconstant.CLICKED_ON_ORDERS_CREATION
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.PreferenceConstant
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
    fpTag?.let { WebEngageController.trackEvent(CLICKED_ON_ORDERS_CREATION, "ORDERS", it) }
    setOnClickListener(binding?.tvGetStarted, binding?.ivClose)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvGetStarted -> {
        PreferencesUtils.instance.saveData(PreferenceConstant.SHOW_CREATE_ORDER_WELCOME, true)
        val bundle = Bundle()
        bundle.putSerializable(
          IntentConstant.PREFERENCE_DATA.name,
          arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name)
        )
        startFragmentOrderActivity(FragmentType.ADD_PRODUCT, bundle, isResult = true)
        (context as? FragmentContainerOrderActivity)?.finish()
      }
      binding?.ivClose -> baseActivity.onNavPressed()
    }
  }
}