package com.inventoryorder.model.services.general

import com.framework.base.BaseResponse
import java.io.Serializable

data class GeneralServiceResponse(
  val Error: Any? = null,
  val Result: GeneralServiceData? = null,
  val StatusCode: Int? = null
) : BaseResponse(), Serializable