package com.onboarding.nowfloats.model.registration

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.models.BaseViewModel
import com.thedevelopercat.sonic.utils.ValidationUtils
import java.net.HttpURLConnection
import java.net.URL

class RegistrationViewModel(
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
    val CREATOR: Parcelable.Creator<RegistrationViewModel> = object : Parcelable.Creator<RegistrationViewModel> {
      override fun createFromParcel(source: Parcel): RegistrationViewModel = RegistrationViewModel(source)
      override fun newArray(size: Int): Array<RegistrationViewModel?> = arrayOfNulls(size)
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
}