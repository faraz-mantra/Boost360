package com.framework.firebaseUtils.caplimit_feature

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PropertiesItem(
  @SerializedName("Key")
  var key: String? = null,
  @SerializedName("Value")
  var value: String? = null
) : Serializable {

  enum class KeyType {
    LIMIT
  }

  fun getValueN(): Int? {
    return if (value != null && (value == "-1" || value == "0")) null else value?.toIntOrNull()
  }
}