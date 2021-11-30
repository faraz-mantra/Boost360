package com.example.template.base

import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.example.template.R
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel

abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {

  //private var progressView: ProgressDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    //progressView = ProgressDialog.newInstance()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
  }


  override fun getToolbarBackgroundColor(): Int? {
    return ContextCompat.getColor(this, R.color.colorPrimary)
  }

  override fun getToolbarTitleColor(): Int? {
    return ContextCompat.getColor(this, R.color.white)
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

  protected open fun hideProgress() {
    //progressView?.hideProgress()
  }

  protected open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    //title?.let { progressView?.setTitle(it) }
    //cancelable?.let { progressView?.isCancelable = it }
    //progressView?.showProgress(supportFragmentManager)
  }

  fun changeTheme(color: Int, taskBarColor: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window?.statusBarColor = ContextCompat.getColor(this, taskBarColor)
    }
  }
}
