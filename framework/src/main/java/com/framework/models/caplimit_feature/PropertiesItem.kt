package com.framework.models.caplimit_feature

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PropertiesItem(
  @SerializedName("Key")
  var key: String? = null,
  @SerializedName("Value")
  var value: String? = null
) : Serializable {

  enum class KeyType {
    LATESTUPDATES, PRODUCT_CATALOUGE, CUSTOM_PAGE, IMAGE
  }

  fun getValueN(): Int? {
    return value?.toIntOrNull()
  }
}