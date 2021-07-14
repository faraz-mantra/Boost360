package com.inventoryorder.model.doctorsData

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Profileimage(
  @SerializedName("description")
  val description: String = "",
  @SerializedName("url")
  val url: String = ""
) : Serializable