package com.nowfloats.facebook.models.userPages

data class FacebookGraphUserPagesErrorModel(
  val error: String? = null,
  val type: String? = null,
  val code: Int? = null,
  val fbtrace_id: String? = null
)