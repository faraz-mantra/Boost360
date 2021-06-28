package com.boost.presignin.model.verification

import com.google.gson.annotations.SerializedName

data class RequestValidateEmail(

	@field:SerializedName("clientId")
	var clientId: String? = null,

	@field:SerializedName("email")
	var email: String? = null
)
