package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentInventoryOrderDetailBinding
import com.inventoryorder.model.InventoryOrderDetailsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter

class InventoryOrderDetailFragment : BaseOrderFragment<FragmentInventoryOrderDetailBinding>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): InventoryOrderDetailFragment {
      val fragment = InventoryOrderDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setAdapter()
  }

  private fun setAdapter() {
    binding?.recyclerViewOrderDetails?.post {
      val list = InventoryOrderDetailsModel().getOrderDetails()
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, list)
      binding?.recyclerViewOrderDetails?.adapter = adapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_share)
    item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
      showLongToast("Share")
    }
  }
}