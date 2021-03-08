package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class UploadMerchantSignature(

	@field:SerializedName("FileType")
	val fileType: String? = null,

	@field:SerializedName("DocumentContent")
	val documentContent: String? = null,

	@field:SerializedName("ClientId")
	val clientId: String? = null,

	@field:SerializedName("FloatingPointId")
	val floatingPointId: String? = null,

	@field:SerializedName("DocumentName")
	val documentName: String? = null
)
