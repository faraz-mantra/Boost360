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
import com.inventoryorder.ui.tutorials.model.VideoDescAppointmentMgmt
import java.io.*

class TutorialViewModel : BaseViewModel() {

  fun getLearnStaffResponse(): MutableLiveData<LearnAboutAppointmentMgmt> {
    val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.learn_about_staff)
    val jsonObj = jsonResourceReader.constructUsingGson(LearnAboutAppointmentMgmt::class.java)
    return MutableLiveData(jsonObj)
  }

  fun getFaqStaffResponse(): MutableLiveData<AppointmentfaqResponse> {
    val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.faq_staff)
    val jsonObj = jsonResourceReader.constructUsingGson(AppointmentfaqResponse::class.java)
    return MutableLiveData(jsonObj)
  }

  fun getVideoDetailStaff(): MutableLiveData<VideoDescAppointmentMgmt> {
    val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.video_detail_staff)
    val jsonObj = jsonResourceReader.constructUsingGson(VideoDescAppointmentMgmt::class.java)
    return MutableLiveData(jsonObj)
  }

  fun getTutorialsStaffList(): MutableLiveData<AllVideoResponse> {
    val jsonResourceReader = JSONResourceReader(BaseOrderApplication.instance.applicationContext.resources, R.raw.video_tutorials_staff)
    val jsonObj = jsonResourceReader.constructUsingGson(AllVideoResponse::class.java)
    return MutableLiveData(jsonObj)
  }

}

class JSONResourceReader(resources: Resources, id: Int) {

  private val jsonString: String

  fun <T> constructUsingGson(type: Class<T>?): T {
    val gson: Gson = GsonBuilder().create()
    return gson.fromJson(jsonString, type)
  }

  companion object {
    private val LOGTAG = JSONResourceReader::class.java.simpleName
  }

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