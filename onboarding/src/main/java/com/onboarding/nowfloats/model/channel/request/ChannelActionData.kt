package com.onboarding.nowfloats.model.channel.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ChannelActionData(
    var active_whatsapp_number: String? = null
) : Parcelable {
  constructor(parcel: Parcel) : this(parcel.readString())

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(active_whatsapp_number)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<ChannelActionData> {
    override fun createFromParcel(parcel: Parcel): ChannelActionData {
      return ChannelActionData(parcel)
    }

    override fun newArray(size: Int): Array<ChannelActionData?> {
      return arrayOfNulls(size)
    }
  }
}

fun ChannelActionData.isLinked(): Boolean {
  return this.active_whatsapp_number != null
}