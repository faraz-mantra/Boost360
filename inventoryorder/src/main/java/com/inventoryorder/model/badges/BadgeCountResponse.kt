package com.inventoryorder.model.badges

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class BadgeCountResponse(

	@field:SerializedName("StatusCode")
	var statusCode: Int? = null,

	@field:SerializedName("Result")
	var result: Int? = null
):BaseResponse()

data class ErrorList(
	var any: Any? = null
)

data class Error(

	@field:SerializedName("ErrorList")
	var errorList: ErrorList? = null,

	@field:SerializedName("ErrorCode")
	var errorCode: Any? = null
)
