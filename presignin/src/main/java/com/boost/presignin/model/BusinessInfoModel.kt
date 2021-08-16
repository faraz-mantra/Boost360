package com.boost.presignin.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessInfoModel(
  @SerializedName("userEmail")
  var userEmail: String? = null,
  @SerializedName("userMobile")
  var userMobile: String? = null,
  @SerializedName("userName")
  var userName: String? = "",
  @SerializedName("userPassword")
  val userPassword: String? = "",
) : Serializable
