package com.appservice.model.account.testimonial.addEdit

import com.framework.base.BaseRequest
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DeleteTestimonialRequest(
    @SerializedName("Query")
    @Expose
    private var query: String? = null,
    @SerializedName("UpdateValue")
    @Expose
    private val updateValue: String? = null,
    @SerializedName("Multi")
    @Expose
    private val multi: Boolean? = null
):BaseRequest(),Serializable