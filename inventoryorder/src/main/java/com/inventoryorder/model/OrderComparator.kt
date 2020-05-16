package com.inventoryorder.model

import com.framework.utils.DateUtils
import com.inventoryorder.model.ordersdetails.OrderItem
import java.text.SimpleDateFormat

internal class OrderComparator : Comparator<OrderItem?> {
  var dateFormat: SimpleDateFormat = SimpleDateFormat(DateUtils.FORMAT_SERVER_DATE)
  override fun compare(lhs: OrderItem?, rhs: OrderItem?): Int {
    return dateFormat.parse(lhs?.CreatedOn).compareTo(dateFormat.parse(rhs?.CreatedOn))
  }
}