package com.boost.upgrades.interfaces

import com.boost.upgrades.data.model.CartModel

interface CartFragmentListener {

  fun deleteCartAddonsItem(itemID: String)
  fun showBundleDetails(itemID: String)
  fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int)
}