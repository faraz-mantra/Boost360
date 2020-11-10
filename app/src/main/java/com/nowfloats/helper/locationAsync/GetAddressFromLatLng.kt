package com.nowfloats.helper.locationAsync

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import java.util.*

class GetAddressFromLatLng(private var contextRef: Context?, val listener: Listener) : AsyncTask<Double, Void, Bundle>() {

  override fun onPreExecute() {
    super.onPreExecute()
    contextRef?.let { ProgressUtility.showProgress(it) }
  }

  override fun doInBackground(vararg params: Double?): Bundle? {
    val addressBundle = Bundle()
    try {
      val latitude = params[0]
      val longitude = params[1]
      val addresses: ArrayList<Address?>
      val geoCoder = Geocoder(contextRef, Locale.getDefault())
      val sb = StringBuilder()
      //get location from lat long if address string is null
      addresses = geoCoder.getFromLocation(latitude!!, longitude!!, 1) as ArrayList<Address?>
      return if (addresses.size > 0) {
        val address = addresses[0]?.getAddressLine(0)
        if (address != null) addressBundle.putString("addressline1", address)
        sb.append(address).append(" ")
        val city = addresses[0]?.locality
        if (city != null) addressBundle.putString("city", city)
        sb.append(city).append(" ")
        val state = addresses[0]?.adminArea
        if (state != null) addressBundle.putString("state", state)
        sb.append(state).append(" ")
        val country = addresses[0]?.countryName
        if (country != null) addressBundle.putString("country", country)
        sb.append(country).append(" ")
        val postalCode = addresses[0]?.postalCode
        if (postalCode != null) addressBundle.putString("postalcode", postalCode)
        sb.append(postalCode).append(" ")
        addressBundle.putString("fulladdress", sb.toString())
        addressBundle
      } else {
        null
      }
    } catch (e: Exception) {
      e.printStackTrace()
      addressBundle.putBoolean("error", true)
      return addressBundle
    }
  }

  override fun onPostExecute(result: Bundle?) {
    super.onPostExecute(result)
    ProgressUtility.hideProgress()
    listener.getAddressResult(result)
  }

  interface Listener {
    fun getAddressResult(bundle: Bundle?)
  }
}