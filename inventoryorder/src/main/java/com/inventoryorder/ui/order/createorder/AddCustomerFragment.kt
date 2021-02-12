package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioGroup
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
    fpTag?.let { WebEngageController.trackEvent("Clicked on Add Customer", "ORDERS", it) }

    setOnClickListener(binding?.vwNext, binding?.textAddCustomerGstin, binding?.tvRemove)

    binding?.checkboxAddressSame?.setOnCheckedChangeListener { p0, isChecked ->
      binding?.lytShippingAddress?.visibility = if (isChecked) View.GONE else View.VISIBLE
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwNext -> {
        startFragmentOrderActivity(FragmentType.BILLING_DETAIL, Bundle())
      }

      binding?.textAddCustomerGstin -> {

        if (binding?.lytCustomerGstn?.visibility == View.GONE) {
          binding?.textAddCustomerGstin?.visibility = View.GONE
          binding?.lytCustomerGstn?.visibility = View.VISIBLE
        }
      }

      binding?.tvRemove -> {
        binding?.textAddCustomerGstin?.visibility = View.VISIBLE
        binding?.lytCustomerGstn?.visibility = View.GONE
      }
    }
  }
}