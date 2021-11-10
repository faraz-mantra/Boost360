package com.framework.models.firestore.badges

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BadgesModel(
  @SerializedName("badgesType")
  var badgesType: String? = null,
  @SerializedName("clientId")
  var clientId: String? = null,
  @SerializedName("createdOn")
  var createdOn: String? = null,
  @SerializedName("deeplinks")
  var deeplinks: DeepLink? = null,
  @SerializedName("isEnabled")
  var isEnabled: Boolean? = null,
  @SerializedName("message")
  var message: Long? = null,
) : Serializable {

  fun getMessageN(): Int {
    return message?.toInt() ?: 0
  }

  fun getIsEnable(): Boolean {
    return isEnabled ?: false
  }

  fun getMessageText(): String {
    return if (getMessageN() > 9) "9+" else "${getMessageN()}"
  }


  enum class BadgesType {
    ENQUIRYBADGE,
    HOMEBADGE,
    MARKETINGBADGE,
    MARKETPLACEBADGE,
    MENUBADGE,
    WEBSITEBADGE;

    companion object {
      fun fromUrlCheck(url: String?): BadgesType? = values().firstOrNull { url?.contains(it.name) == true }
      fun fromName(name: String?): BadgesType? = values().firstOrNull { it.name == name }
    }
  }
}

data class DeepLink(
  @SerializedName("android")
  var android: String? = null,
  @SerializedName("ios")
  var ios: String? = null,
  @SerializedName("web")
  var web: String? = null
) : Serializable