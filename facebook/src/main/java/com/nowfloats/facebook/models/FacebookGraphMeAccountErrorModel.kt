package com.nowfloats.facebook.models

data class FacebookGraphMeAccountErrorModel(
    val error: String? = null,
    val type: String? = null,
    val code: Int? = null,
    val fbtrace_id: String? = null
)