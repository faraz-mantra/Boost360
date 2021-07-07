package com.nowfloats.helper.locationAsync

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import java.util.*

class GetLatLngFromAddress(private val contextRef: Context?, val listener: Listener) :
  AsyncTask<String, Context, LatLng>() {

  override fun onPreExecute() {
    super.onPreExecute()
    contextRef?.let { ProgressUtility.showProgress(it) }
  }

  override fun doInBackground(vararg params: String?): LatLng {
    var latLng = LatLng(0.0, 0.0)
    try {
      val addresses: List<Address>?
      val geoCoder = Geocoder(contextRef, Locale.getDefault())
      //get location from lat long if address string is null
      addresses = geoCoder.getFromLocationName(params[0], 1)
      if (addresses.isNullOrEmpty().not()) {
        latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
      }
    } catch (ignored: Exception) {
    }
    return latLng
  }

  override fun onPostExecute(result: LatLng?) {
    super.onPostExecute(result)
    ProgressUtility.hideProgress()
    listener.getLatLngResult(result)
  }

  interface Listener {
    fun getLatLngResult(latLong: LatLng?)
  }
}