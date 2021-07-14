package com.nowfloats.education.helper

import android.app.ProgressDialog
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nowfloats.util.BoostLog
import com.nowfloats.util.Constants

open class BaseFragment : Fragment() {

  private var progressDialog: ProgressDialog? = null

  fun showLoader(message: String?) {
    if (activity == null || !isAdded) return
    if (progressDialog == null) {
      progressDialog = ProgressDialog(activity)
      progressDialog?.setCanceledOnTouchOutside(false)
      progressDialog?.setCancelable(false)
    }
    progressDialog?.setMessage(message)
    progressDialog?.show()
  }

  fun hideLoader() {
    BoostLog.i(Constants.LogTag, "Logger hidden")
    if (progressDialog != null && progressDialog?.isShowing()!!) {
      progressDialog?.dismiss()
    }
  }

  fun showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
  }
}