package com.onboarding.nowfloats.model.domain

import com.framework.base.BaseRequest

data class BusinessDomainSuggestRequest(
  val clientId: String? = null,
  val name: String? = null,
  val city: String? = null,
  val country: String? = "India",
  val category: String? = null
) : BaseRequest()