package com.dashboard.controller.ui.dialog

import androidx.annotation.RawRes
import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.databinding.DialogDashboardProgressBinding
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class ProgressDashboardDialog :
  BaseDialogFragment<DialogDashboardProgressBinding, BaseViewModel>() {

  private var title: String? = null
  private var lottyFile: Int? = null

  companion object {
    @JvmStatic
    fun newInstance(): ProgressDashboardDialog {
      return ProgressDashboardDialog()
    }
  }

  fun setData(@RawRes lottyFile: Int, title: String) {
    this.lottyFile = lottyFile
    this.title = title
  }

  override fun getLayout(): Int {
    return R.layout.dialog_dashboard_progress
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getTheme(): Int {
    return R.style.MaterialDialogTheme
  }

  override fun onCreateView() {
    title?.let { binding?.title?.setText(it) }
    binding?.gifAnim?.apply {
      lottyFile?.let { gifResource = it }
      play()
    }
  }

  override fun getWidth(): Int {
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

  override fun onDestroy() {
    super.onDestroy()
    binding?.gifAnim?.apply { pause() }
  }
}