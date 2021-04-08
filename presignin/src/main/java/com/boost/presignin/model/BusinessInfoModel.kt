package com.boost.presignin.model

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.utils.ValidationUtils
import java.net.HttpURLConnection
import java.net.URL

class BusinessInfoModel(
        val name:String,
        var businessName: String,
        var email: String,
        var number: String,
        var domainName: String? = null,
        var addressCity: String? = null,
) : Parcelable {


    constructor(source: Parcel) : this(
            source.readString()!!,
            source.readString()!!,
            source.readString()!!,
            source.readString()!!,
            source.readString(),
            source.readString(),
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(businessName)
        writeString(email)
        writeString(number)
        writeString(domainName)
      writeString(addressCity)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BusinessInfoModel> = object : Parcelable.Creator<BusinessInfoModel> {
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

//    fun clearAllDomain() {
//        businessName = null
//        addressCity = null
//        email = null
//        number = null
//        domainName = null
//    }
}