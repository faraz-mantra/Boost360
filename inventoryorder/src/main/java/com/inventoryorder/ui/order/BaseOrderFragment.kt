package com.inventoryorder.ui.order

import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.viewmodel.OrderCreateViewModel

open class BaseOrderFragment<binding : ViewDataBinding> : AppBaseFragment<binding, OrderCreateViewModel>() {

  protected val fpTag: String?
    get() {
      return preferenceData?.fpTag
    }
  protected val auth: String
    get() {
      return preferenceData?.authorization ?: ""
    }

  protected var preferenceData: PreferenceData? = null

  override fun getLayout(): Int {
    return when (this) {
      is InventoryAllOrderFragment -> R.layout.fragment_inventory_all_order
      is InventoryOrderDetailFragment -> R.layout.fragment_inventory_order_detail
      is InventoryBookingDetailsFragment -> R.layout.fragment_inventory_booking_details
      is InventoryAllBookingsFragment -> R.layout.fragment_inventory_all_bookings
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    preferenceData = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as? PreferenceData
  }

  override fun getViewModelClass(): Class<OrderCreateViewModel> {
    return OrderCreateViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    when (this) {
      is InventoryAllOrderFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is InventoryOrderDetailFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is InventoryBookingDetailsFragment -> inflater.inflate(R.menu.menu_share_button,menu)
      is InventoryAllBookingsFragment -> inflater.inflate(R.menu.menu_search_icon,menu)
    }
  }
}