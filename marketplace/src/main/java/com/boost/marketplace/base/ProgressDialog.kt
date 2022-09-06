package com.boost.marketplace.base

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.boost.marketplace.R
import com.boost.marketplace.databinding.MarketplaceProgressDialogBinding
import com.framework.analytics.SentryController
import com.framework.analytics.SentryController.captureException
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils

class ProgressDialog : BaseDialogFragment<MarketplaceProgressDialogBinding, BaseViewModel>() {
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
    return R.layout.marketplace_progress_dialog
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
      captureException(e)
    }
  }

  fun hideProgress() {
    try {
      if (isRemoving.not()) dismiss()
    } catch (e: Exception) {
      Log.e(ProgressDialog::class.java.name, e.localizedMessage ?: "")
      captureException(e)
    }
  }

}