package com.inventoryorder.model.weeklySchedule

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetDoctorWeeklySchedule(
    @SerializedName("Extra")
    val extra: Extra,
    @SerializedName("Data")
    val data: ArrayList<DataItem>?
):BaseResponse(),Serializable