package dev.patrickgold.florisboard.customization.network

import android.os.AsyncTask
import dev.patrickgold.florisboard.customization.util.Constants
import dev.patrickgold.florisboard.customization.util.Util
import org.json.JSONArray
import org.json.JSONObject

class GetGalleryImagesAsyncTask : AsyncTask<Void?, String?, String?>() {

    interface GetGalleryImagesInterface {
        fun imagesReceived()
    }

    private var clientIdWithQuotes = "\"" + Constants.clientId + "\""
    private var fpId: String? = null
    private var galleryInterface: GetGalleryImagesInterface? = null

    fun setGalleryInterfaceListener(galleryInterface: GetGalleryImagesInterface?, fpId: String?) {
        this.galleryInterface = galleryInterface
        this.fpId = fpId
    }

    override fun doInBackground(vararg params: Void?): String {
        var response = ""
        try {
            response = Util.getDataFromServer(clientIdWithQuotes,
                    Constants.HTTP_POST, Constants.LoadStoreURI.toString() + fpId,
                    Constants.BG_SERVICE_CONTENT_TYPE_JSON)
            if (response.isNotEmpty()) {
                Constants.hasStoreData = true
                val store = JSONObject(response)
                if (response.contains("ImageUri")) Constants.storePrimaryImage = store.getString("ImageUri")
                if (response.contains("SecondaryImages")) {
                    try {
                        val array: JSONArray = store.getJSONArray("SecondaryImages")
                        val len: Int = array.length()
                        Constants.storeSecondaryImages = arrayListOf()
                        if (len != 0) {
                            for (i in 0 until len) {
                                Constants.storeSecondaryImages.add(0, array.getString(i))
                            }
                            Constants.storeActualSecondaryImages = Constants.storeSecondaryImages
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    override fun onPostExecute(result: String?) {
        galleryInterface!!.imagesReceived()
    }
}