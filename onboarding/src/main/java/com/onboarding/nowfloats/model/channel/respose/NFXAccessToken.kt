package com.onboarding.nowfloats.model.channel.respose

import java.io.Serializable
import java.util.*

data class NFXAccessToken(
  val CatalogId: String? = null,
  val MerchantSettingsId: String? = null,
  val PixelId: String? = null,
  val Status: String? = null,
  val Type: String? = null,
  val UserAccessTokenKey: String? = null,
  val UserAccountId: String? = null,
  val UserAccountName: String? = null,
  val account_id: String? = null,
  val account_name: String? = null,
  val invalid: Boolean? = null,
  val location_id: String? = null,
  val location_name: String? = null,
  val refresh_token: String? = null,
  val token_expiry: String? = null,
  val token_response: TokenResponse? = null,
  val verified_location: Boolean? = null
) : Serializable {

  fun type(): String {
    return Type?.toLowerCase(Locale.ROOT) ?: ""
  }

  fun isValidType(): Boolean {
    return (UserAccountId.isNullOrEmpty() && UserAccessTokenKey.isNullOrEmpty()).not()
  }

  fun isValidTypeShop(): Boolean {
    return (UserAccountId.isNullOrEmpty() && MerchantSettingsId.isNullOrEmpty()).not()
  }
}