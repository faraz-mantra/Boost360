package com.boost.cart.interfaces

import com.boost.dbcenterapi.upgradeDB.model.CartModel

interface CartFragmentListener {

  fun deleteCartAddonsItem(item: CartModel)
  fun showBundleDetails(itemID: String)
  fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int)
}