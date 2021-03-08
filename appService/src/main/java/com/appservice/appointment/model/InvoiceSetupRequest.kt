package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName

data class InvoiceSetupRequest(

	@field:SerializedName("PanDetails")
	val panDetails: PanDetails? = null,

	@field:SerializedName("GSTDetails")
	val gSTDetails: GSTDetails? = null,

	@field:SerializedName("TanDetails")
	val tanDetails: TanDetails? = null,

	@field:SerializedName("ClientId")
	val clientId: String? = null,

	@field:SerializedName("FloatingPointId")
	val floatingPointId: String? = null
)

data class GSTDetails(

	@field:SerializedName("GSTIN")
	val gSTIN: String? = null,

	@field:SerializedName("BusinessName")
	val businessName: String? = null,

	@field:SerializedName("FileType")
	val fileType: String? = null,

	@field:SerializedName("DocumentContent")
	val documentContent: String? = null,

	@field:SerializedName("DocumentName")
	val documentName: String? = null
)

data class PanDetails(

	@field:SerializedName("Number")
	val number: String? = null,

	@field:SerializedName("FileType")
	val fileType: String? = null,

	@field:SerializedName("DocumentContent")
	val documentContent: String? = null,

	@field:SerializedName("Name")
	val name: String? = null,

	@field:SerializedName("DocumentName")
	val documentName: String? = null
)

data class TanDetails(

	@field:SerializedName("Number")
	val number: String? = null,

	@field:SerializedName("FileType")
	val fileType: String? = null,

	@field:SerializedName("DocumentContent")
	val documentContent: String? = null,

	@field:SerializedName("DocumentName")
	val documentName: String? = null
)
