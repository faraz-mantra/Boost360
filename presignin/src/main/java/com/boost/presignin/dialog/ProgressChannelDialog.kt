package com.boost.presignin.dialog

import androidx.fragment.app.FragmentManager
import com.boost.presignin.R
import com.boost.presignin.databinding.DialogChannelProgressBinding
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class ProgressChannelDialog : BaseDialogFragment<DialogChannelProgressBinding, BaseViewModel>() {

  private var title: CharSequence? = null

  companion object {
    @JvmStatic
    fun newInstance(): ProgressChannelDialog {
      return ProgressChannelDialog()
    }
  }

  fun setTitle(title: CharSequence) {
    this.title = title
  }

  override fun getLayout(): Int {
    return R.layout.dialog_channel_progress
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
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
    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: IllegalStateException) {
      e.printStackTrace()
    }
  }

}