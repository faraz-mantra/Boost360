package com.appservice.appointment.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class InvoiceSetupRequest(

		@field:SerializedName("PanDetails")
		var panDetails: PANDetails? = null,

		@field:SerializedName("GSTDetails")
		var gSTDetails: GSTDetails? = null,

		@field:SerializedName("TanDetails")
		var tanDetails: TANDetails? = null,

		@field:SerializedName("ClientId")
		var clientId: String? = null,

		@field:SerializedName("FloatingPointId")
		var floatingPointId: String? = null,
) : Serializable

data class GSTDetails(

		@field:SerializedName("GSTIN")
		var gSTIN: String? = null,

		@field:SerializedName("BusinessName")
		var businessName: String? = null,

		@field:SerializedName("FileType")
		var fileType: String? = null,

		@field:SerializedName("DocumentContent")
		var documentContent: String? = null,

		@field:SerializedName("DocumentName")
		var documentName: String? = null,
) : Serializable


