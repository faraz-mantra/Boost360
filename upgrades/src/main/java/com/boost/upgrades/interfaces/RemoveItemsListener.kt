package com.boost.upgrades.interfaces

import com.boost.dbcenterapi.upgradeDB.model.*

interface RemoveItemsListener {
  fun addItemToCart(item: CartModel)
  fun removeItemFromCart(item: CartModel)
}