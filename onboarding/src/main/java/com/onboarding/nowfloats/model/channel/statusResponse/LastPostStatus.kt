package com.onboarding.nowfloats.model.channel.statusResponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LastPostStatus(
  @SerializedName("created_at")
  var createdAt: String? = null,
  @SerializedName("dx_id")
  var dxId: String? = null,
  @SerializedName("errors")
  var errors: List<Error>? = null,
  @SerializedName("operation")
  var operation: String? = null,
  @SerializedName("ref_id")
  var refId: String? = null,
  @SerializedName("status")
  var status: String? = null,
) : Serializable