package com.appservice.staffs.model

import com.appservice.ui.catalog.common.AppointmentModel
import com.google.gson.annotations.SerializedName

data class StaffTimingAddUpdateRequest(

    @field:SerializedName("StaffId", alternate = ["staffId"])
    val staffId: String? = null,
    @field:SerializedName("WorkTimings", alternate = ["workTimings", "Timings"])
    val workTimings: List<AppointmentModel?>? = null,
)

