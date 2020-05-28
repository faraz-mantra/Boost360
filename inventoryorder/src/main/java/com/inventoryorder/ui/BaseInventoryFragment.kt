package com.inventoryorder.ui

import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.ui.booking.BookingDetailsFragment
import com.inventoryorder.ui.booking.BookingsFragment
import com.inventoryorder.ui.order.OrderDetailFragment
import com.inventoryorder.ui.order.OrdersFragment
import com.inventoryorder.viewmodel.OrderCreateViewModel

open class BaseInventoryFragment<binding : ViewDataBinding> : AppBaseFragment<binding, OrderCreateViewModel>() {

  protected val fpTag: String?
    get() {
      return preferenceData?.fpTag
    }
  protected val clientId: String?
    get() {
      return preferenceData?.clientId
    }
  protected val auth: String
    get() {
      return preferenceData?.authorization ?: ""
    }

  protected var preferenceData: PreferenceData? = null

  override fun getLayout(): Int {
    return when (this) {
      is OrdersFragment -> R.layout.fragment_inventory_all_order
      is OrderDetailFragment -> R.layout.fragment_inventory_order_detail
      is BookingsFragment -> R.layout.fragment_inventory_all_bookings
      is BookingDetailsFragment -> R.layout.fragment_inventory_booking_details
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
      is OrdersFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is OrderDetailFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is BookingsFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is BookingDetailsFragment -> inflater.inflate(R.menu.menu_share_button, menu)
    }
  }
}