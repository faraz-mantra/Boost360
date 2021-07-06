package com.appservice.ui.testimonial.newflow.model

import com.google.gson.annotations.SerializedName

data class DeleteTestimonialRequestNew(

	@field:SerializedName("TestimonialId")
	var testimonialId: String? = null
)
