package com.boost.presignin.base

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.databinding.PresigninProgressDialogBinding
import com.framework.analytics.SentryController
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class ProgressDialog : BaseDialogFragment<PresigninProgressDialogBinding, BaseViewModel>() {
  private var title: CharSequence? = null

  companion object {
    @JvmStatic
    fun newInstance(): ProgressDialog {
      return ProgressDialog()
    }
  }

  fun setTitle(title: CharSequence) {
    this.title = title
  }

  override fun getLayout(): Int {
    return R.layout.presignin_progress_dialog
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
  }

  override fun onCreateView() {
    title?.let { binding?.title?.text = it }
  }

  override fun getWidth(): Int? {
    return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(32f)
  }

  fun showProgress(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, ProgressDialog::class.java.simpleName)
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
      SentryController.captureException(e)

    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
      SentryController.captureException(e)

    }
  }

}