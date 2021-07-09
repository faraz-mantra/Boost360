package com.dashboard.model.live.drScore

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DrScoreUiDataResponse(
  @SerializedName("data")
  var `data`: ArrayList<DrScoreUiData> = ArrayList()
):BaseResponse(),Serializable