package com.boost.presignup

import android.animation.Animator
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.boost.presignup.utils.DynamicLinkParams
import com.boost.presignup.utils.FirebaseDynamicLinksManager
import com.framework.analytics.SentryController
import com.framework.base.FRAGMENT_TYPE
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.featureNewOnBoardingFlowEnable
import com.framework.pref.UserSessionManager
import com.framework.utils.AppsFlyerUtils
import com.framework.utils.InAppUpdateUtil
import com.framework.utils.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

  var session: UserSessionManager? = null
  private var deepLinkViewType = ""
  private var deepLinkFpId = ""
  private var deepLinkFpTag = ""
  private var deepLinkDay = ""
  private val INAPP_UPDATE_REQUEST_CODE = 120
  lateinit var inAppUpdateUtil: InAppUpdateUtil

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    FirebaseRemoteConfigUtil.initRemoteConfigData(this)
    inAppUpdateUtil = InAppUpdateUtil(this)
    session = UserSessionManager(this)
    val uri = intent.data
    if (uri != null) {
      if (uri.toString().contains("onelink", true)) {
        if (AppsFlyerUtils.sAttributionData.containsKey(DynamicLinkParams.viewType.name)) {
          deepLinkViewType = AppsFlyerUtils.sAttributionData[DynamicLinkParams.viewType.name] ?: ""
          deepLinkFpId = AppsFlyerUtils.sAttributionData[DynamicLinkParams.fpId.name] ?: ""
          deepLinkFpTag = AppsFlyerUtils.sAttributionData[DynamicLinkParams.fpTag.name] ?: ""
          deepLinkDay = AppsFlyerUtils.sAttributionData[DynamicLinkParams.day.name] ?: ""
        }
      } else {
        val deepHashMap = FirebaseDynamicLinksManager().getURILinkParams(uri)
        if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
          deepLinkViewType = deepHashMap[DynamicLinkParams.viewType] ?: ""
          deepLinkFpId = deepHashMap[DynamicLinkParams.fpId] ?: ""
          deepLinkFpTag = deepHashMap[DynamicLinkParams.fpTag] ?: ""
          deepLinkDay = deepHashMap[DynamicLinkParams.day] ?: ""
        }
      }
    } else if (intent.extras?.containsKey("url") == true) {
      val url = intent.extras?.getString("url")
      if (url.isNullOrEmpty().not() && session?.isUserLoggedIn == true) {
        try {
          val intent = Intent(applicationContext, Class.forName("com.dashboard.controller.DashboardActivity"))
          intent.putExtra("url", url)
          startActivity(intent)
          overridePendingTransition(0, 0)
          this.finish()
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
    onCreateView()
  }

  private fun checkForUpdate() {
    FirebaseRemoteConfigUtil.remoteConfig?.fetchAndActivate()?.addOnCompleteListener {
      inAppUpdateUtil.checkForUpdate(INAPP_UPDATE_REQUEST_CODE, onComplete = {
        gotToNextScreen()
      })
    }
  }

  private fun onCreateView() {
    if (session?.isUserLoggedIn == true && deepLinkViewType.isNotEmpty()) {
      try {
        val intent = Intent(applicationContext, Class.forName("com.boost.presignin.ui.LoaderActivity"))
        intent.putExtra("deepLinkViewType", deepLinkViewType)
        intent.putExtra("deepLinkFpId", deepLinkFpId)
        intent.putExtra("deepLinkFpTag", deepLinkFpTag)
        intent.putExtra("deepLinkDay", deepLinkDay)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
      } catch (e: Exception) {
        e.printStackTrace()
      }
    } else
      initLottieAnimation()
  }

  private fun initLottieAnimation() {
    animation_view.setAnimation(R.raw.boost_lottie2)
    animation_view.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {
      }

      override fun onAnimationEnd(animation: Animator?) {
        animation_view?.cancelAnimation()
        checkForUpdate()
      }

      override fun onAnimationCancel(animation: Animator?) {
      }

      override fun onAnimationStart(animation: Animator?) {
      }

    })
    animation_view.playAnimation()
  }

  fun gotToNextScreen(){
    when {
      (session?.isUserLoggedIn == true) -> splashLoader()
      (session?.isUserSignUpComplete == true) -> startNewSignUpSuccess()
      else -> startNewSignIn()
    }
  }

  private fun splashLoader() {
    try {
      val intent = Intent(applicationContext, Class.forName("com.boost.presignin.ui.LoaderActivity"))
      startActivity(intent)
      finish()
    } catch (e: Exception) {
      e.printStackTrace()
      finish()
    }
  }

  private fun startNewSignUpSuccess() {
    try {
      if (featureNewOnBoardingFlowEnable()) {
        val intent = Intent(applicationContext, Class.forName("com.boost.presignin.ui.newOnboarding.NewOnBoardingContainerActivity"))
        intent.putExtra(FRAGMENT_TYPE, "LOADING_ANIMATION_DASHBOARD_FRAGMENT")
        startActivity(intent)
      }else {
        val intent = Intent(applicationContext, Class.forName("com.boost.presignin.ui.registration.RegistrationActivity"))
        intent.putExtra(FRAGMENT_TYPE, 101)
        startActivity(intent)
      }
      finish()
    } catch (e: Exception) {
      e.printStackTrace()
      finish()
    }
  }

  /**
   * FRAGMENT_TYPE : 0 stands for New screen Enter Phone Number
   *               : 1 stands for New Intro Slide Show Fragment
   * */
  private fun startNewSignIn() {
    try {
      val intent : Intent = if (featureNewOnBoardingFlowEnable()) {
        val fragmentType = if (UserSessionManager(this).hasUserLoggedInOnce) "ENTER_PHONE_FRAGMENT" else "INTRO_SLIDE_SHOW_FRAGMENT"
        Intent(applicationContext, Class.forName("com.boost.presignin.ui.newOnboarding.NewOnBoardingContainerActivity")).
        putExtra(FRAGMENT_TYPE, fragmentType) // 0 stands for New screen Enter Phone Number ordinal from FragmentType in Presignin constants
      }else Intent(applicationContext, Class.forName("com.boost.presignin.ui.intro.IntroActivity"))
      startActivity(intent)
      finish()
    } catch (e: Exception) {
      e.printStackTrace()
      finish()
    }
  }

  override fun onDestroy() {
    animation_view?.cancelAnimation()
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == INAPP_UPDATE_REQUEST_CODE) {
      when (resultCode) {
        RESULT_OK ->{
          gotToNextScreen()
        }
        RESULT_CANCELED ->{
          gotToNextScreen()
        }
        ActivityResult.RESULT_IN_APP_UPDATE_FAILED ->{
          showToast("App update failed")
          gotToNextScreen()
        }
      }


    }
  }


  override fun onStop() {
    super.onStop()
    inAppUpdateUtil.removeInstallStateUpdateListener()
  }

}
