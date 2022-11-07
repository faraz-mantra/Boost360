package com.appservice.model.testimonial

import com.framework.base.BaseRequest
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddTestimonialImageRequest(
  @SerializedName("profileImage")
  var profileImage: ProfileImage? = null,
  @SerializedName("testimonialId")
  var testimonialId: String? = null
) : BaseRequest(), Serializable