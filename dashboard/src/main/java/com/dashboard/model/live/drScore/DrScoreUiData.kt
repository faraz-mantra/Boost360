package com.dashboard.model.live.drScore

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DrScoreUiData(
  @SerializedName("desc")
  var desc: String = "",
  @SerializedName("id")
  var id: String = "",
  @SerializedName("title")
  var title: String = ""
) : BaseResponse(), Serializable {

  fun getDescValue(): String {
    return if (desc.isNotEmpty()) desc else title
  }
}