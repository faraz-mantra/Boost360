package com.boost.upgrades.interfaces

import com.boost.upgrades.data.model.CartModel

interface RemoveItemsListener {
  fun addItemToCart(item: CartModel)
  fun removeItemFromCart(item: CartModel)
}