package com.appservice.model.staffModel

import com.appservice.ui.catalog.common.AppointmentModel
import com.google.gson.annotations.SerializedName

data class StaffTimingAddUpdateRequest(

  @field:SerializedName("staffId", alternate = ["StaffId"])
  val staffId: String? = null,
  @field:SerializedName("workTimings", alternate = ["WorkTimings", "Timings"])
  val workTimings: ArrayList<AppointmentModel>? = null,
)

