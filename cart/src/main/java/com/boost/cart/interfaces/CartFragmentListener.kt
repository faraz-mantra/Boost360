package com.boost.cart.interfaces

import com.boost.dbcenterapi.upgradeDB.model.CartModel

interface CartFragmentListener {

  fun deleteCartAddonsItem(item: CartModel)
  fun showBundleDetails(itemID: String)
  fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int)
  fun featureDetailsPopup(domain: String)
  fun featureDetailsPopupvmn(vmn: String)
  fun editSelectedDomain(bundleItem: CartModel)
  fun editSelectedVmn(bundleItem: CartModel)
}