package com.inventoryorder.ui.order

import android.os.Bundle
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding

class InventoryOrderDetailFragment : BaseOrderFragment<FragmentInventoryOrderDetailBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InventoryOrderDetailFragment {
      val fragment = InventoryOrderDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

}