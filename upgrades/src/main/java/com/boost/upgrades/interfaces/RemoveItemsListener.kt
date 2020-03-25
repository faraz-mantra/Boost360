package com.boost.upgrades.interfaces

import com.boost.upgrades.data.model.Cart

interface RemoveItemsListener {
    fun addItemToCart(item: Cart)
    fun removeItemFromCart(item: Cart)
}