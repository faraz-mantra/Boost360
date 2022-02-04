package com.dashboard.model.live.drScore

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DrScoreUiDataResponse(
  @SerializedName("data")
  var `data`: ArrayList<DrScoreUiDataItems> = ArrayList()
) : BaseResponse(), Serializable {

  fun getActionItem(expCode: String?): ArrayList<DrScoreUiData> {
    val dataDrScore = data.firstOrNull { it.getTypes().contains(expCode?:"") }
    return dataDrScore?.action_item ?: data.lastOrNull()?.action_item?: arrayListOf()
  }
}

data class DrScoreUiDataItems(
  @SerializedName("type")
  var type: String? = null,
  @SerializedName("action_item")
  var action_item: ArrayList<DrScoreUiData> = ArrayList()
) : BaseResponse(), Serializable {

  fun getTypes(): List<String> {
    return type?.split(",") ?: arrayListOf()
  }
}