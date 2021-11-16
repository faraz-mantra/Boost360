package com.boost.cart.utils

import com.boost.cart.ui.home.CartFragment
import com.boost.cart.ui.details.DetailsFragment
import com.boost.cart.ui.freeaddons.FreeAddonsFragment


class Constants {
    companion object {
        var DETAILS_FRAGMENT: String = DetailsFragment::class.java.getName()
        var CART_FRAGMENT: String = CartFragment::class.java.getName()
        var FREEADDONS_FRAGMENT: String = FreeAddonsFragment::class.java.getName()
        const val ALERT_YES = 1
        const val ALERT_NO = 2
    }
}