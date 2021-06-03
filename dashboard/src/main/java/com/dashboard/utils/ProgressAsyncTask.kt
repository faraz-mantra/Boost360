@file:Suppress("DEPRECATION")

package com.dashboard.utils

import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

class ProgressAsyncTask(activity: AppCompatActivity) : AsyncTask<Void?, Void?, Void?>() {

  var progressDialog = ProgressDialog(activity)

  override fun onPreExecute() {
    progressDialog.setMessage("Loading. Please wait...")
    progressDialog.setCancelable(false)
    progressDialog.show()
  }

  override fun doInBackground(vararg params: Void?): Void? {
    return try {
      Thread.sleep(1000)
      null
    } catch (e: InterruptedException) {
      null
    }
  }

  override fun onPostExecute(result: Void?) {
    super.onPostExecute(result)
    try {
      progressDialog.hide()
    } catch (e: InterruptedException) {
      e.printStackTrace()
    }
  }
}