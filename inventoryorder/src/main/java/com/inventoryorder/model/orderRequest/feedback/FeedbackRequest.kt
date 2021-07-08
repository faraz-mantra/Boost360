package com.inventoryorder.model.orderRequest.feedback

import com.framework.base.BaseRequest
import java.io.Serializable

data class FeedbackRequest(
  var OrderId: String? = null,
  var Message: String? = null,
) : BaseRequest(), Serializable