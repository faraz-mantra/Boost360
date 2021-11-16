package com.boost.upgrades.interfaces

import com.framework.upgradeDB.model.*

interface CartFragmentListener {

  fun deleteCartAddonsItem(itemID: String)
  fun showBundleDetails(itemID: String)
  fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int)
}