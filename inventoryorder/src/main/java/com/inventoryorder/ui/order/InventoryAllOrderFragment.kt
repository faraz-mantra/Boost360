package com.inventoryorder.ui.order

import android.os.Bundle
import com.inventoryorder.databinding.FragmentInventoryAllOrderBinding

class InventoryAllOrderFragment : BaseOrderFragment<FragmentInventoryAllOrderBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InventoryAllOrderFragment {
      val fragment = InventoryAllOrderFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

}