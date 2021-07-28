package com.onboarding.nowfloats.base

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.databinding.ViewDataBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R

abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {

  private var progressView: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
    progressView = ProgressDialog.newInstance()
  }


  protected open fun hideProgress() {
    progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    progressView?.showProgress(supportFragmentManager)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    super.onBackPressed()
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
  }
}
