package com.boost.upgrades.interfaces

import com.boost.dbcenterapi.data.api_model.GetPurchaseOrder.Result

interface HistoryFragmentListener {
  fun viewHistoryItem(item: Result)
}