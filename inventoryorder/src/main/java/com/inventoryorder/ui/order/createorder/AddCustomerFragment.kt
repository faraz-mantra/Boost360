package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.framework.webengageconstant.CLICKED_ON_ADD_CUSTOMER
import com.framework.webengageconstant.ORDERS
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentAddCustomerBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController


class AddCustomerFragment : BaseInventoryFragment<FragmentAddCustomerBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddCustomerFragment {
      val fragment = AddCustomerFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent(CLICKED_ON_ADD_CUSTOMER, ORDERS, it) }

    setOnClickListener(binding?.vwNext)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwNext -> {
        startFragmentOrderActivity(FragmentType.ADD_PRODUCT, Bundle())
      }
    }
  }

}