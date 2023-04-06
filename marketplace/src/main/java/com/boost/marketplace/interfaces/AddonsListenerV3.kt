package com.boost.marketplace.interfaces

import androidx.recyclerview.widget.RecyclerView

interface AddonsListenerV3 {
    fun onPaidAddonsClicked(item: String)
    fun onAllRecyclerView(item: RecyclerView, endList: Boolean = false)
    fun onPackageItemClicked(name: String)
}