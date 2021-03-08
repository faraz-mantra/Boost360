package com.appservice.appointment.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class DeliveryDetailsResponse(


		@field:SerializedName("StatusCode")
		var statusCode: Int? = null,

		@field:SerializedName("Result")
		var result: DResult? = null,
) : BaseResponse()

data class DResult(

		@field:SerializedName("IsPickupAllowed")
		var isPickupAllowed: Boolean? = null,

		@field:SerializedName("IsBusinessLocationPickupAllowed")
		var isBusinessLocationPickupAllowed: Boolean? = null,

		@field:SerializedName("IsWarehousePickupAllowed")
		var isWarehousePickupAllowed: Boolean? = null,

		@field:SerializedName("IsHomeDeliveryAllowed")
		var isHomeDeliveryAllowed: Boolean? = null,

		@field:SerializedName("FlatDeliveryCharge")
		var flatDeliveryCharge: Double? = null,

		@field:SerializedName("FloatingPointId")
		var floatingPointId: String? = null,
)

data class DError(

		@field:SerializedName("ErrorList")
		var errorList: ErrorList? = null,

		@field:SerializedName("ErrorCode")
		var errorCode: Any? = null,
)

data class DErrorList(
		var any: Any? = null,
)
