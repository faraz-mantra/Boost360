package com.nowfloats.education.service

import android.os.AsyncTask
import android.util.Log
import com.nowfloats.education.helper.Constants
import com.nowfloats.education.koindi.KoinBaseApplication
import com.nowfloats.education.model.ResponseImageModel
import com.nowfloats.education.model.UploadImageModel
import com.thinksity.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class UploadImageService(
  private val listener: UploadImageServiceListener,
  private val uploadImages: List<UploadImageModel>
) : AsyncTask<Void?, String?, String?>() {

  private val TAG = UploadImageService::class.java.simpleName
  private val responseImageUrlList = mutableListOf<ResponseImageModel>()

  override fun onPostExecute(result: String?) {
    when {
      !responseImageUrlList.isNullOrEmpty() -> {
        listener.onSuccess(responseImageUrlList)
      }
      else -> {
        listener.onFailed(KoinBaseApplication.instance.getString(R.string.error_occured_while_uploadin_images))
      }
    }
  }

  override fun doInBackground(vararg params: Void?): String? {
    return uploadFilesToServer(uploadImages)
  }

  private fun uploadFilesToServer(uploadImages: List<UploadImageModel>): String? {
    uploadImages.forEach {
      val file = File(it.filePath)
      try {
        val client = OkHttpClient()
        val `in`: InputStream = FileInputStream(file)
        var buf: ByteArray?
        buf = ByteArray(`in`.available())
        while (`in`.read(buf) != -1);

        val requestBody: RequestBody = MultipartBody.Builder()
          .setType(MultipartBody.FORM)
          .addFormDataPart(
            "file",
            it.fileName,
            RequestBody.create("image/*".toMediaTypeOrNull(), buf)
          )
          .build()

        val request = Request.Builder()
          .url(it.apiUrl)
          .post(requestBody)
          .addHeader("Authorization", Constants.AUTH_CODE)
          .build()

        val response = client.newCall(request).execute()

        `in`.close()
        buf = null
        when {
          response != null && response.code == 200 -> {
            val responseImageModel =
              ResponseImageModel(it.fileName, Objects.requireNonNull(response.body)!!.string())
            responseImageUrlList.add(responseImageModel)
          }
          else -> Log.e(TAG, response.message)
        }
      } catch (e: Exception) {
        e.printStackTrace()
        Log.e(TAG, e.message ?: "")
      }
    }
    return null
  }
}