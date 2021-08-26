package dev.patrickgold.florisboard.customization.model.response.shareUser

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDetail(
  @SerializedName("message")
  var message: String? = null,
  @SerializedName("type")
  var type: String? = null
):Serializable