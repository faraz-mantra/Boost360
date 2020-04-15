package com.boost.upgrades.interfaces

import com.boost.upgrades.data.api_model.GetPurchaseOrder.Result

interface HistoryFragmentListener {
    fun viewHistoryItem(item: Result)
}