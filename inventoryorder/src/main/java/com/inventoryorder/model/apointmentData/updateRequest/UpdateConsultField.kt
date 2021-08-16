package com.inventoryorder.model.apointmentData.updateRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateConsultField(
  @SerializedName("$".plus("set"))
  var `set`: SetField? = null
) : Serializable