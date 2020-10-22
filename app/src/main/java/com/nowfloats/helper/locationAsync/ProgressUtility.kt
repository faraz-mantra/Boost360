package com.nowfloats.helper.locationAsync

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.thinksity.R

object ProgressUtility {

  var popupWindow: Dialog? = null


  fun showProgress(context: Context) {
    try {
      if (!(context as Activity).isFinishing) {
        val layout: View = LayoutInflater.from(context).inflate(R.layout.popup_loading, null)
        popupWindow = Dialog(context, android.R.style.Theme_Translucent)
        popupWindow?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        popupWindow?.setContentView(layout)
        popupWindow?.setCancelable(false)
        if (!context.isFinishing) popupWindow?.show()
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun hideProgress() {
    try {
      if (popupWindow != null && popupWindow?.isShowing == true) popupWindow?.dismiss()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

}
