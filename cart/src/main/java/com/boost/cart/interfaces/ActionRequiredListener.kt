package com.boost.cart.interfaces

import com.boost.dbcenterapi.upgradeDB.model.CartModel

interface ActionRequiredListener {
    fun actionClick(get: CartModel)
    fun actionClickVmn(get: CartModel)
    fun actionClickDomain(get: CartModel)
}