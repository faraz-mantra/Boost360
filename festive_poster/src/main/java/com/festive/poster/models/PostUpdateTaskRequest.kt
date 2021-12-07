package com.festive.poster.models

import com.framework.base.BaseRequest
import java.io.Serializable

data class PostUpdateTaskRequest(
  var clientId: String? = null,
  var message: String? = null,
  var isPictureMessage: Boolean? = null,
  var merchantId: String? = null,
  var parentId: String? = null,
  var sendToSubscribers: Boolean? = null,
  var socialParameters: String? = null
) : BaseRequest(), Serializable
