package com.inventoryorder.ui.order

import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.PreferenceConstant
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.viewmodel.OrderCreateViewModel

open class BaseOrderFragment<binding : ViewDataBinding> : AppBaseFragment<binding, OrderCreateViewModel>() {

  protected val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, 0)
    }
  protected val userProfileId: String?
    get() {
      return preferenceData?.userProfileId
    }
  protected val clientId: String?
    get() {
      return preferenceData?.clientId
    }
  protected val fpId: String?
    get() {
      return preferenceData?.fpid
    }
  protected val auth: String?
    get() {
      return preferenceData?.authorization
    }

  protected var preferenceData: PreferenceData? = null

  override fun getLayout(): Int {
    return when (this) {
      is InventoryAllOrderFragment -> R.layout.fragment_inventory_all_order
      is InventoryOrderDetailFragment -> R.layout.fragment_inventory_order_detail
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
    }
  }
}