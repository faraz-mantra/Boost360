package dev.patrickgold.florisboard.customization.model.response.moreAction

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MoreActionResponse(
  @SerializedName("data")
  var `data`: ArrayList<MoreActionTypeResponse>? = null
) : BaseResponse(), Serializable {

  fun getDataMoreDataTYpe(fpExperienceCode: String): MoreActionTypeResponse? {
    return data?.firstOrNull { it.type.equals(fpExperienceCode, true) }
  }
}

data class MoreActionTypeResponse(
  @SerializedName("items")
  var items: ArrayList<MoreData>? = null,
  @SerializedName("type")
  var type: String? = null
) : BaseResponse(), Serializable