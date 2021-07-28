package com.onboarding.nowfloats.model.registration

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.utils.ValidationUtils
import java.net.HttpURLConnection
import java.net.URL

class BusinessInfoModel(
  var businessName: String? = null,
  var addressCity: String? = null,
  var email: String? = null,
  var number: String? = null,
  var domainName: String? = null
) : Parcelable {

  fun getNumberN(): String? {
    return if (number.isNullOrEmpty().not()) number else null
  }

  fun getEmailN(): String? {
    return if (email.isNullOrEmpty().not()) email else null
  }

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
    source.readString(),
    source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(businessName)
    writeString(addressCity)
    writeString(email)
    writeString(number)
    writeString(domainName)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<BusinessInfoModel> =
      object : Parcelable.Creator<BusinessInfoModel> {
        override fun createFromParcel(source: Parcel): BusinessInfoModel = BusinessInfoModel(source)
        override fun newArray(size: Int): Array<BusinessInfoModel?> = arrayOfNulls(size)
      }
  }

  fun getUrlStatusCode(url: String): LiveData<Int> {
    val code = MutableLiveData<Int>()
    Runnable {
      try {
        val siteURL = URL(url)
        val connection = siteURL.openConnection() as? HttpURLConnection
        connection?.requestMethod = "GET"
        connection?.connectTimeout = 3000
        connection?.connect()
        code.postValue(connection?.responseCode ?: -1)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
    return code
  }

  fun clearAllDomain() {
    businessName = null
    addressCity = null
    email = null
    number = null
    domainName = null
  }

  fun isAllExceptDomainEmpty(): Boolean {
    return businessName.isNullOrEmpty() && addressCity.isNullOrEmpty() && email.isNullOrEmpty()
        && number.isNullOrEmpty()
  }

  fun isDomainEmpty(): Boolean {
    return domainName.isNullOrEmpty()
  }
}