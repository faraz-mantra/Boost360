package com.inventoryorder.model.apointmentData


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DoctorAppointmentResponse(
    @SerializedName("Data")
  var `data`: ArrayList<AptData>? = null,
    @SerializedName("Extra")
  var extra: Extra? = null
):BaseResponse(),Serializable