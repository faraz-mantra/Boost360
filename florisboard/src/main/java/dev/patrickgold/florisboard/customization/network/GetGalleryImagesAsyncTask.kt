package dev.patrickgold.florisboard.customization.network

import android.os.AsyncTask
import dev.patrickgold.florisboard.customization.util.Constants
import dev.patrickgold.florisboard.customization.util.Util
import org.json.JSONArray
import org.json.JSONObject

class GetGalleryImagesAsyncTask : AsyncTask<Void?, String?, ArrayList<String>?>() {

  interface GetGalleryImagesInterface {
    fun imagesReceived(listImage: ArrayList<String>)
  }

  private var clientIdWithQuotes = "\"" + Constants.clientId + "\""
  private var fpId: String? = null
  private var galleryInterface: GetGalleryImagesInterface? = null

  fun setGalleryInterfaceListener(galleryInterface: GetGalleryImagesInterface?, fpId: String?) {
    this.galleryInterface = galleryInterface
    this.fpId = fpId
  }

  override fun doInBackground(vararg params: Void?): ArrayList<String> {
    val result = ArrayList<String>()
    try {
      val response = Util.getDataFromServer(clientIdWithQuotes, Constants.HTTP_POST, Constants.LoadStoreURI.toString() + fpId, Constants.BG_SERVICE_CONTENT_TYPE_JSON)
      if (response.isNotEmpty()) {
        val store = JSONObject(response)
        if (response.contains("ImageUri")) Constants.storePrimaryImage = store.getString("ImageUri")
        if (response.contains("SecondaryImages")) {
          try {
            val array: JSONArray = store.getJSONArray("SecondaryImages")
            val len: Int = array.length()
            if (len != 0) {
              for (i in 0 until len) {
                result.add(0, array.getString(i))
              }
            }
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return result
  }

  override fun onPostExecute(listImage: ArrayList<String>?) {
    galleryInterface?.imagesReceived(listImage ?: ArrayList())
  }
}