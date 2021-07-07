package com.appservice.ui.catalog

import com.appservice.ui.catalog.common.AppointmentModel
import com.google.gson.annotations.SerializedName

data class RequestWeeklyAppointment(

  @field:SerializedName("duration")
  val duration: Int? = null,

  @field:SerializedName("timings")
  val timings: List<AppointmentModel?>? = null,

  @field:SerializedName("serviceId")
  val serviceId: String? = null,
)

