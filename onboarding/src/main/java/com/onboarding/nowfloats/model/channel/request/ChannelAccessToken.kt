package com.onboarding.nowfloats.model.channel.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.onboarding.nowfloats.model.channel.ChannelTokenResponse

data class ChannelAccessToken(
  @SerializedName("Type")
  var type: String? = null,
  @SerializedName("UserAccessTokenKey")
  var userAccessTokenKey: String? = null,
  @SerializedName("UserAccessTokenSecret")
  var userAccessTokenSecret: String? = null,
  @SerializedName("UserAccountId")
  var userAccountId: String? = null,
  @SerializedName("UserAccountName")
  var userAccountName: String? = null,

  //TODO  for shop
  @SerializedName("PixelId")
  var pixelId: String? = null,
  @SerializedName("CatalogId")
  var catalogId: String? = null,
  @SerializedName("MerchantSettingsId")
  var merchantSettingsId: String? = null,

  //TODO google business
  var token_expiry: String? = null,
  var invalid: Boolean? = null,
  var token_response: ChannelTokenResponse? = null,
  var refresh_token: String? = null,
//    var account_name: String? = null,
//    var account_id: String? = null,
//    var location_id: String? = null,
  var LocationId: String? = null,
//    var location_name: String? = null,
  var LocationName: String? = null,
  var verified_location: Boolean? = null,

  //TODO url
  var profilePicture: String? = null,

  ) : Parcelable {
  constructor(parcel: Parcel) : this(
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString(),
    parcel.readString()
  )

  enum class AccessTokenType {
    facebookpage, facebookshop, twitter, googlemybusiness, googlesearch, googlemap, instagram
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(type)
    parcel.writeString(userAccessTokenKey)
    parcel.writeString(userAccessTokenSecret)
    parcel.writeString(userAccountId)
    parcel.writeString(userAccountName)
    parcel.writeString(profilePicture)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChannelAccessToken> {
    override fun createFromParcel(parcel: Parcel): ChannelAccessToken {
      return ChannelAccessToken(parcel)
    }

    override fun newArray(size: Int): Array<ChannelAccessToken?> {
      return arrayOfNulls(size)
    }
  }

}

fun ChannelAccessToken.isConnected(): Boolean {
  return this.userAccountId.isNullOrEmpty().not()
}

fun ChannelAccessToken.isLinked(): Boolean {
  return this.userAccessTokenKey.isNullOrEmpty().not() && this.userAccountId.isNullOrEmpty().not()
}

fun ChannelAccessToken.isLinkedGoogleBusiness(): Boolean {
  return this.userAccessTokenKey.isNullOrEmpty().not() && this.userAccountId.isNullOrEmpty()
    .not() && this.LocationId.isNullOrEmpty().not()
}

fun ChannelAccessToken.clear() {
  this.userAccessTokenKey = null
  this.userAccessTokenSecret = null
  this.userAccountId = null
  this.userAccountName = null
  this.profilePicture = null
}

fun ChannelAccessToken.getType(): ChannelAccessToken.AccessTokenType {
  return ChannelAccessToken.AccessTokenType.values().first { it.name.toLowerCase() == type }
}