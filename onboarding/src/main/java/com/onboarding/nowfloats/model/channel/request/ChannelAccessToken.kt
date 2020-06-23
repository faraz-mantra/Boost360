package com.onboarding.nowfloats.model.channel.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

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
    //TODO url
    var profilePicture: String? = null


) : Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString())

  enum class AccessTokenType {
    facebookpage, facebookshop, googlemybusiness, twitter, googlesearch, googlemap
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

fun ChannelAccessToken.isLinked(): Boolean {
  return this.userAccessTokenKey != null && this.userAccountId != null
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