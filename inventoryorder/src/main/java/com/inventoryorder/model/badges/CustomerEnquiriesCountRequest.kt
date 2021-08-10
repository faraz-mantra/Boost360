package com.inventoryorder.model.badges

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName

data class CustomerEnquiriesCountRequest(

	@field:SerializedName("FpIds")
	var fpIds: List<String?>? = null,

	@field:SerializedName("StartDate")
	var startDate: String? = null,

	@field:SerializedName("ClientId")
	var clientId: String? = null,

	@field:SerializedName("EndDate")
	var endDate: String? = null
):BaseRequest()
