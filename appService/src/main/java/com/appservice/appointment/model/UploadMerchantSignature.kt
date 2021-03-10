package com.appservice.appointment.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UploadMerchantSignature(

	@field:SerializedName("FileType")
	var fileType: String? = null,

	@field:SerializedName("DocumentContent")
	var documentContent: String? = null,

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("FloatingPointId")
	var floatingPointId: String? = null,

	@field:SerializedName("DocumentName")
	var documentName: String? = null
):BaseResponse(),Serializable
