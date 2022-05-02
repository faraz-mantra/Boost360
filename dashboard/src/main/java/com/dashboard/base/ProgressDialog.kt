package com.dashboard.base

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.dashboard.R
import com.dashboard.databinding.DashboardProgressDialogBinding
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class ProgressDialog : BaseDialogFragment<DashboardProgressDialogBinding, BaseViewModel>() {
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
    return R.layout.dashboard_progress_dialog
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
      hideProgress()
      if (this.isVisible.not()) show(manager, ProgressDialog::class.java.simpleName)
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
    }
  }

}