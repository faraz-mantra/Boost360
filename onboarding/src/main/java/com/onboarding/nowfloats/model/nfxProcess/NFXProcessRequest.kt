package com.onboarding.nowfloats.model.nfxProcess

import com.framework.base.BaseRequest

data class NFXProcessRequest(
  val nowfloats_client_id: String? = null,
  val nowfloats_id: String? = null,
  val social_data: SocialData? = null,
  val identifiers: List<String>? = null,
  val callback_url: String? = null,
  val operation: String? = "create",
  val filter: String? = "access_token",
  val boost_priority: Int? = 9
) : BaseRequest()