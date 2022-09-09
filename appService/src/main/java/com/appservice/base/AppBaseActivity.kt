package com.appservice.base

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.framework.pref.Key_Preferences
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.WEB_VIEW_PAGE
import com.onboarding.nowfloats.ui.webview.WebViewActivity
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList
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
    hideProgress()
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

  override fun getNavigationIcon(): Drawable? {
    return ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
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

  fun changeTheme(color: Int, taskBarColor: Int) {
    getToolbar()?.setBackgroundColor(ContextCompat.getColor(this, color))
    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window?.statusBarColor = ContextCompat.getColor(this, taskBarColor)
  }

  open fun initiateBuyFromMarketplace(buyItemKey: String = "") {
    showProgress()
    val intent = Intent(this, Class.forName("com.boost.marketplace.ui.home.MarketPlaceActivity"))
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("fpid", session.fPID)
    intent.putExtra("fpTag", session.fpTag)
    intent.putExtra("accountType", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
    intent.putStringArrayListExtra("userPurchsedWidgets", session.getStoreWidgets()?.toArrayList())
    if (session.userPrimaryMobile != null) {
      intent.putExtra("email", session.userPrimaryMobile)
    } else {
      intent.putExtra("email", getString(R.string.ria_customer_mail))
    }
    if (session.userPrimaryMobile != null) {
      intent.putExtra("mobileNo", session.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
    }
    intent.putExtra("profileUrl", session.fPLogo)
    intent.putExtra("buyItemKey", buyItemKey)
    startActivity(intent)
    Handler().postDelayed({ hideProgress() }, 1000)
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

fun isStaffType(category_code: String?): Boolean {
  return (category_code.equals("DOC", true) || category_code.equals("HOS", true)).not()
}

fun addPlus91(userMobile: String?): String? {
  return when {
    userMobile?.contains("+91-") == true -> userMobile.replace("+91-", "+91")
    userMobile?.contains("+91") == false -> "+91$userMobile"
    else -> userMobile
  }
}

