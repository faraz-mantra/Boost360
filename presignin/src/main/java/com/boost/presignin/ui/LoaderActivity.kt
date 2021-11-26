package com.boost.presignin.ui

import android.app.AlertDialog
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseActivity
import com.boost.presignin.databinding.ActivityLoaderBinding
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.intro.IntroActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.analytics.SentryController
import com.framework.analytics.UserExperiorController
import com.framework.extensions.observeOnce
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.firebaseUtils.firestore.FirestoreManager
import com.framework.pref.*
import com.framework.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class LoaderActivity : AppBaseActivity<ActivityLoaderBinding, LoginSignUpViewModel>() {

  private lateinit var session: UserSessionManager
  private var deepLink: String? = null
  private var deepLinkViewType: String? = null
  private var deepLinkFpId: String? = null
  private var deepLinkFpTag: String? = null
  private var deepLinkDay: String? = null

  override fun getLayout(): Int {
    return R.layout.activity_loader
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(this)
  }

  override fun onResume() {
    super.onResume()
    handleApis()
  }

  private fun handleApis() {
    if (NetworkUtils.isNetworkConnected()) {
      if (!session.isLoginCheck) {
        signUpStart()
      } else {
        val bundle = intent.extras
        if (intent != null && intent.getStringExtra("from") != null) {
          deepLink = intent.getStringExtra("url")
        }
        if (bundle != null) {
          deepLinkViewType = bundle.getString("deepLinkViewType")
          deepLinkFpId = bundle.getString("deepLinkFpId")
          deepLinkFpTag = bundle.getString("deepLinkFpTag")
          deepLinkDay = bundle.getString("deepLinkDay")
        }
        initLottieAnimation()
        storeFpDetails()
      }
    } else {
      binding?.preDashboardAnimation?.pauseAnimation()
      snackbarNoInternet()
    }
  }

  private fun storeFpDetails() {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel.getFpDetails(session.fPID ?: "", map).observeOnce(this, { it1 ->
      val response = it1 as? UserFpDetailsResponse
      if (it1.isSuccess() && response != null) {
        ProcessFPDetails(session).storeFPDetails(response)
        setFPDetailsToSentry(session)
        setFPDetailsToUserExperior(session)
        FirestoreManager.initData(session.fpTag ?: "", session.fPID ?: "", clientId)
        FirebaseRemoteConfigUtil.initRemoteConfigData(this)
        startService()
        if (
          deepLinkViewType != null && deepLinkViewType.equals("CART_FRAGMENT", ignoreCase = true)
          && deepLinkFpId != null && deepLinkFpId!!.trim { it <= ' ' } == session.fPID?.trim { it <= ' ' }
        ) {
          initiateAddonMarketplace()
        } else {
          if (deepLinkViewType != null && deepLinkViewType.equals("CART_FRAGMENT", ignoreCase = true)) showAlertDialog()
          else goHomePage()
        }
      } else {
        binding?.preDashboardAnimation?.pauseAnimation()
        snackBarUnableToGetFp()
      }
    })
  }

  private fun setFPDetailsToSentry(session: UserSessionManager) {
    SentryController.setUser(session)
  }

  private fun setFPDetailsToUserExperior(session: UserSessionManager) {
    UserExperiorController.setUserAttr(session)
  }

  private fun snackBarUnableToGetFp() {
    val view = findViewById<View>(android.R.id.content)
    val snackbar = Snackbar
      .make(view, getString(R.string.error_getting_fp_detail), Snackbar.LENGTH_INDEFINITE)
      .setAction(getString(R.string.retry)) { handleApis() }
    snackbar.show()
  }

  private fun showAlertDialog() {
    val str = String.format(resources.getString(R.string.error_right_fptag), deepLinkFpTag)
    AlertDialog.Builder(ContextThemeWrapper(this, R.style.CustomAlertDialogTheme))
      .setMessage(str)
      .setCancelable(false)
      .setPositiveButton(R.string.ok) { dialog, i ->
        dialog.dismiss()
        goHomePage()
      }.show()
  }


  private fun initiateAddonMarketplace() {
    val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
    intent.putExtra("expCode", session.fP_AppExperienceCode)
    intent.putExtra("fpName", session.fPName)
    intent.putExtra("fpid", session.fPID?.trim { it <= ' ' })
    intent.putExtra("isDeepLink", true)
    intent.putExtra("deepLinkViewType", deepLinkViewType)
    intent.putExtra("deepLinkDay", deepLinkDay)
    intent.putExtra("fpTag", session.fpTag)
    intent.putExtra("accountType", session.getFPDetails("GET_FP_DETAILS_CATEGORY"))
    val storeList = ArrayList(session.getStoreWidgets() ?: arrayListOf())
    intent.putStringArrayListExtra("userPurchsedWidgets", storeList)
    if (session.userProfileEmail != null) {
      intent.putExtra("email", session.userProfileEmail)
    } else {
      intent.putExtra("email", "ria@nowfloats.com")
    }
    if (session.userPrimaryMobile != null) {
      intent.putExtra("mobileNo", session.userPrimaryMobile)
    } else {
      intent.putExtra("mobileNo", "9160004303")
    }
    intent.putExtra("profileUrl", session.fPLogo)
    startActivity(intent)
    overridePendingTransition(0, 0)
    finish()
  }

  private fun goHomePage() {
    try {
      val i = Intent(this, Class.forName("com.dashboard.controller.DashboardActivity"))
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
      if (deepLink != null) {
        if (!deepLink!!.contains("logout")) {
          i.putExtras(intent)
          startActivity(i)
          finish()
        } else session.logoutUser(this)
      } else {
        startActivity(i)
        finish()
      }
    } catch (e: ClassNotFoundException) {
      Log.e("Home Page", e.localizedMessage)
      session.logoutUser(this)
      SentryController.captureException(e)
      session.logoutUser(this)
    }
  }

  private fun startService() {
    startService(Intent(this, APIService::class.java))
  }

  private fun initLottieAnimation() {
    binding?.preDashboardAnimation?.setAnimation(R.raw.pre_dashboard_lottie)
    binding?.preDashboardAnimation?.repeatCount = LottieDrawable.INFINITE
    binding?.preDashboardAnimation?.playAnimation()
  }


  private fun signUpStart() {
    val webIntent = Intent(this, IntroActivity::class.java)
    webIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(webIntent)
    overridePendingTransition(0, 0)
    finish()
  }

  private fun snackbarNoInternet() {
    val view = findViewById<View>(android.R.id.content)
    val snackbar = Snackbar
      .make(view, getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
      .setAction(getString(R.string.settings)) {
        try {
          startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK; })
        } catch (e: Exception) {
          SentryController.captureException(e)
          Toast.makeText(this@LoaderActivity, "Unable to find network settings. Please do it manually from phone's settings", Toast.LENGTH_LONG).show()
          Log.e(TAG, "updateUiInternetNotAvailable: " + e.localizedMessage)
        }
      }
    snackbar.show()
  }

  override fun onDestroy() {
    super.onDestroy()
    binding?.preDashboardAnimation?.cancelAnimation()
  }
}