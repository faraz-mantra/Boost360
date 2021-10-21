package com.appservice.model.aptsetting

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class DeliverySetup(

	@field:SerializedName("IsPickupAllowed")
	var isPickupAllowed: Boolean? = null,

	@field:SerializedName("IsBusinessLocationPickupAllowed")
	var isBusinessLocationPickupAllowed: Boolean? = null,

	@field:SerializedName("IsWarehousePickupAllowed")
	var isWarehousePickupAllowed: Boolean? = null,

	@field:SerializedName("IsHomeDeliveryAllowed")
	var isHomeDeliveryAllowed: Boolean? = null,

	@field:SerializedName("FlatDeliveryCharge")
	var flatDeliveryCharge: String? = null,

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("FloatingPointId")
	var floatingPointId: String? = null
):BaseResponse()
