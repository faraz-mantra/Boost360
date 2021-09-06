package dev.patrickgold.florisboard.customization.model.response.shareUser

import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import dev.patrickgold.florisboard.customization.model.response.shareUser.UserDetail
import java.io.Serializable

data class ShareUserDetailResponse(
  @SerializedName("data")
  var `data`: ArrayList<UserDetail>? = null
):BaseResponse(),Serializable