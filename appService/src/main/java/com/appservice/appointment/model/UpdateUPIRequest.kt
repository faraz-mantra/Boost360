package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class UpdateUPIRequest(

	@field:SerializedName("ClientId")
	val clientId: String? = null,

	@field:SerializedName("UPIId")
	val uPIId: String? = null,

	@field:SerializedName("FloatingPointId")
	val floatingPointId: String? = null
)
