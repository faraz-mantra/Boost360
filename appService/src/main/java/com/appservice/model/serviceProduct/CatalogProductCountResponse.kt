package com.appservice.model.serviceProduct

import com.framework.base.BaseResponse
import java.io.Serializable

data class CatalogProductCountResponse(
  val Products: ArrayList<CatalogProduct>? = null,
  val TotalCount: Int? = null,
  val HasMoreItems: Boolean? = null
) : BaseResponse(),Serializable