package com.boost.presignin.dialog

import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.databinding.FullScreenDialogBinding
import com.framework.analytics.SentryController
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class FullScreenProgressDialog : BaseDialogFragment<FullScreenDialogBinding, BaseViewModel>() {

  private var title: CharSequence? = null

  companion object {
    @JvmStatic
    fun newInstance(): FullScreenProgressDialog {
      return FullScreenProgressDialog()
    }
  }

  fun setTitle(title: CharSequence) {
    this.title = title
  }

  override fun getLayout(): Int {
    return R.layout.full_screen_dialog
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialThemeSignInFull
  }

  override fun onCreateView() {
    title?.let { binding?.title?.setText(it) }
  }

  override fun getWidth(): Int? {
    return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(32f)
  }

  fun showProgress(manager: FragmentManager) {
    try {
      if (this.isVisible.not()) show(manager, "")
    } catch (e: IllegalStateException) {
      e.printStackTrace()
      SentryController.captureException(e)
    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
      SentryController.captureException(e)

    }
  }

}