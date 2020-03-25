package com.onboarding.nowfloats.model.contactinfo

import android.os.Parcel
import android.os.Parcelable
import com.framework.models.BaseViewModel
import com.thedevelopercat.sonic.utils.ValidationUtils

class ContactInfo(
        var storeName: String? = null,
        var address: String? = null,
        var email: String? = null,
        var number: String? = null
) : BaseViewModel(), Parcelable {


  fun isEmailValid(): Boolean {
    return ValidationUtils.isEmailValid(email ?: "")
  }

  fun isNumberValid(): Boolean {
    return ValidationUtils.isMobileNumberValid(number ?: "")
  }

  constructor(source: Parcel) : this(
          source.readString(),
          source.readString(),
          source.readString(),
          source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(storeName)
    writeString(address)
    writeString(email)
    writeString(number)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<ContactInfo> = object : Parcelable.Creator<ContactInfo> {
      override fun createFromParcel(source: Parcel): ContactInfo = ContactInfo(source)
      override fun newArray(size: Int): Array<ContactInfo?> = arrayOfNulls(size)
    }
  }
}