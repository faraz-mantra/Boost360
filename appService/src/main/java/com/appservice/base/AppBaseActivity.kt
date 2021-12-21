package com.appservice.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.appservice.R
import com.appservice.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.WEB_VIEW_PAGE
import com.onboarding.nowfloats.ui.webview.WebViewActivity
import com.framework.pref.UserSessionManager
import com.onboarding.nowfloats.base.ProgressDialog

abstract class AppBaseActivity<Binding : ViewDataBinding, ViewModel : BaseViewModel> : BaseActivity<Binding, ViewModel>() {
  private var progressView: ProgressDialog? = null
  protected lateinit var session: UserSessionManager

  override fun onCreate(savedInstanceState: Bundle?) {
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    session = UserSessionManager(this)
    progressView = ProgressDialog.newInstance()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
  }

  open fun hideProgress() {
    progressView?.hideProgress()
  }

  open fun showProgress(title: String? = "Please wait...", cancelable: Boolean? = false) {
    title?.let { progressView?.setTitle(it) }
    cancelable?.let { progressView?.isCancelable = it }
    progressView?.showProgress(supportFragmentManager)
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

  fun getStaffType(category_code: String?): String {
    return when (category_code) {
      "DOC", "HOS" -> "DOCTORS"
      else -> "STAFF"
    }
  }

  fun changeTheme(color: Int, taskBarColor: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window?.statusBarColor = ContextCompat.getColor(this, taskBarColor)
  }
}

fun AppCompatActivity.startWebViewPageLoad(session: UserSessionManager?, url: String?) {
  try {
    WebEngageController.trackEvent(WEB_VIEW_PAGE, CLICK, url)
    val intent = Intent(this, WebViewActivity::class.java)
    intent.putExtra(com.onboarding.nowfloats.constant.IntentConstant.DOMAIN_URL.name, url)
    startActivity(intent)
  } catch (e: Exception) {
    e.printStackTrace()
    SentryController.captureException(e)
  }
}
