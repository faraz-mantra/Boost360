package com.appservice.model.aptsetting

import com.framework.base.BaseRequest
import java.io.Serializable

data class GstSlabRequest(
  var ClientId: String? = null,
  var FloatingPointId: String? = null,
  var GSTSlab: Int? = null
) : Serializable, BaseRequest()