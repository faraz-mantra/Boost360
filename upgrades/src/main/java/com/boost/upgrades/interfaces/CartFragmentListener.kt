package com.boost.upgrades.interfaces

interface CartFragmentListener {

    fun deleteCartAddonsItem(itemID: String)

    fun showBundleDetails(itemID: String)
}