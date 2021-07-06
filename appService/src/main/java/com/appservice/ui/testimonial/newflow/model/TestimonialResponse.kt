package com.appservice.ui.testimonial.newflow.model

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TestimonialResponse(

	@field:SerializedName("StatusCode")
	var statusCode: Int? = null,

	@field:SerializedName("Result")
	var result: DataItem? = null
): BaseResponse(),Serializable

