package com.inventoryorder.ui.order

import android.content.SharedPreferences
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.PreferenceConstant

open class BaseOrderFragment<binding : ViewDataBinding> : AppBaseFragment<binding, BaseViewModel>() {

  protected val pref: SharedPreferences?
    get() {
      return baseActivity.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, 0)
    }
  protected val userProfileId: String?
    get() {
      return pref?.getString(PreferenceConstant.USER_PROFILE_ID, "5e7dfd3d5a9ed3000146ca56")
    }
  protected val clientId: String?
    get() {
      return pref?.getString(PreferenceConstant.CLIENT_ID, "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21")
    }

  override fun getLayout(): Int {
    return when (this) {
      is InventoryAllOrderFragment -> R.layout.fragment_inventory_all_order
      is InventoryOrderDetailFragment -> R.layout.fragment_inventory_order_detail
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

}