package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.framework.webengageconstant.CLICKED_ON_ADD_PRODUCT
import com.framework.webengageconstant.ORDERS
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentAddProductBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController

class AddProductFragment : BaseInventoryFragment<FragmentAddProductBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddProductFragment {
      val fragment = AddProductFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent(CLICKED_ON_ADD_PRODUCT, ORDERS, it) }

    setOnClickListener(binding?.tvProceed)
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
        binding?.tvProceed -> {
            startFragmentOrderActivity(FragmentType.BILLING_DETAIL, Bundle())
        }
    }
  }

}