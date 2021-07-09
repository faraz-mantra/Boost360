package com.onboarding.nowfloats.model.channel.request

import android.os.Parcel
import android.os.Parcelable

data class ChannelActionData(
    var active_whatsapp_number: String? = null
) : Parcelable {

  fun getNumberWithCode(): String? {
//   return if (active_whatsapp_number.isNullOrEmpty().not()) "+91${active_whatsapp_number}" else null
    return if (active_whatsapp_number.isNullOrEmpty().not()) active_whatsapp_number else null
  }

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
  return this.active_whatsapp_number.isNullOrEmpty().not()
}