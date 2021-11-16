package com.boost.cart.interfaces

import com.boost.cart.data.api_model.GetPurchaseOrder.Result

interface HistoryFragmentListener {
  fun viewHistoryItem(item: Result)
}