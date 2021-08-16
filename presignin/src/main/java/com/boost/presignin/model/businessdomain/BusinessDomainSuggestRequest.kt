package com.boost.presignin.model.businessdomain

import com.framework.base.BaseRequest
import com.framework.base.BaseResponse

data class BusinessDomainSuggestRequest(
  val clientId: String? = null,
  val name: String? = null,
  val city: String? = null,
  val country: String? = "India",
  val category: String? = null
) : BaseRequest()