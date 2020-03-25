package com.boost.upgrades.interfaces

import com.boost.upgrades.data.model.Cart

interface CartFragmentListener {

    fun deleteCartAddonsItem(itemID: String)
}