package com.inventoryorder.model.badges

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName

data class OrderCountResponse(
	@field:SerializedName("Data")
	var data: Int? = null
):BaseResponse()
