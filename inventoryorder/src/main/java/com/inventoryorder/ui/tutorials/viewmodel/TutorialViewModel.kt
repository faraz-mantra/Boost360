package com.inventoryorder.ui.tutorials.viewmodel

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.framework.models.BaseViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.inventoryorder.BaseOrderApplication
import com.inventoryorder.R
import com.inventoryorder.ui.tutorials.model.AllVideoResponse
import com.inventoryorder.ui.tutorials.model.AppointmentfaqResponse
import com.inventoryorder.ui.tutorials.model.LearnAboutAppointmentMgmt
import java.io.*

class TutorialViewModel : BaseViewModel() {
    fun getLearnAppointmentmgmtResponse(): MutableLiveData<LearnAboutAppointmentMgmt> {
        val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.learnaboutappointmentmgmt)
        val jsonObj = jsonResourceReader.constructUsingGson(LearnAboutAppointmentMgmt::class.java)
        return MutableLiveData(jsonObj)
    }

    fun getFaqResponse(): MutableLiveData<AppointmentfaqResponse> {
        val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.appointmentfaq)
        val jsonObj = jsonResourceReader.constructUsingGson(AppointmentfaqResponse::class.java)
        return MutableLiveData(jsonObj)
    }

    fun get(): MutableLiveData<AppointmentfaqResponse> {
        val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.appointmentfaq)
        val jsonObj = jsonResourceReader.constructUsingGson(AppointmentfaqResponse::class.java)
        return MutableLiveData(jsonObj)
    }

    fun getTutorialsList(): MutableLiveData<AllVideoResponse> {
        val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.appointmentmgmtvideotutorials)
        val jsonObj = jsonResourceReader.constructUsingGson(AllVideoResponse::class.java)
        return MutableLiveData(jsonObj)
    }


}

class JSONResourceReader(resources: Resources, id: Int) {
    // === [ Private Data Members ] ============================================
    // Our JSON, in string form.
    private val jsonString: String

    /**
     * Build an object from the specified JSON resource using Gson.
     *
     * @param type The type of the object to build.
     *
     * @return An object of type T, with member fields populated using Gson.
     */
    fun <T> constructUsingGson(type: Class<T>?): T {
        val gson: Gson = GsonBuilder().create()
        return gson.fromJson(jsonString, type)
    }

    companion object {
        private val LOGTAG = JSONResourceReader::class.java.simpleName
    }
    // === [ Public API ] ======================================================
    /**
     * Read from a resources file and create a [JSONResourceReader] object that will allow the creation of other
     * objects from this resource.
     *
     * @param resources An application [Resources] object.
     * @param id The id for the resource to load, typically held in the raw/ folder.
     */
    init {
        val resourceReader: InputStream = resources.openRawResource(id)
        val writer: Writer = StringWriter()
        try {
            val reader = BufferedReader(InputStreamReader(resourceReader, "UTF-8"))
            var line: String? = reader.readLine()
            while (line != null) {
                writer.write(line)
                line = reader.readLine()
            }
        } catch (e: Exception) {
            Log.e(LOGTAG, "Unhandled exception while using JSONResourceReader", e)
        } finally {
            try {
                resourceReader.close()
            } catch (e: Exception) {
                Log.e(LOGTAG, "Unhandled exception while using JSONResourceReader", e)
            }
        }
        jsonString = writer.toString()
    }
}