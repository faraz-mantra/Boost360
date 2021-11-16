package com.boost.upgrades.interfaces

import com.framework.upgradeDB.model.*

interface RemoveItemsListener {
  fun addItemToCart(item: CartModel)
  fun removeItemFromCart(item: CartModel)
}