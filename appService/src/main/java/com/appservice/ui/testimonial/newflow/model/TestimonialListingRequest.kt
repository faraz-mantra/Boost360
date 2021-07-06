package com.appservice.ui.testimonial.newflow.model

import com.google.gson.annotations.SerializedName

data class TestimonialListingRequest(

	@field:SerializedName("FloatingPointTag")
	var floatingPointTag: String? = null,

	@field:SerializedName("Limit")
	var limit: Int? = 0,

	@field:SerializedName("FloatingPointId")
	var floatingPointId: String? = null,

	@field:SerializedName("Offset")
	var offset: Int? = 0
)
