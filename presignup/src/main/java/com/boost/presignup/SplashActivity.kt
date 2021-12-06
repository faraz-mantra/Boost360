package com.boost.presignup

import android.animation.Animator
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignup.utils.DynamicLinkParams
import com.boost.presignup.utils.FirebaseDynamicLinksManager
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.featureNewOnBoardingFlowEnable
import com.framework.pref.UserSessionManager
import com.framework.utils.AppsFlyerUtils
import com.onboarding.nowfloats.managers.NavigatorManager
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : AppCompatActivity() {

  var session: UserSessionManager? = null
  private var deepLinkViewType = ""
  private var deepLinkFpId = ""
  private var deepLinkFpTag = ""
  private var deepLinkDay = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
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

  private fun onCreateView() {
    FirebaseRemoteConfigUtil.initRemoteConfigData(this)
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
        when {
          (session?.isUserLoggedIn == true) -> splashLoader()
          (session?.isUserSignUpComplete == true) -> startNewSignUpSuccess()
          else -> startNewSignIn()
        }
      }

      override fun onAnimationCancel(animation: Animator?) {
      }

      override fun onAnimationStart(animation: Animator?) {
      }

    })
    animation_view.playAnimation()
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
      val intent = Intent(applicationContext, Class.forName("com.boost.presignin.ui.registration.RegistrationActivity"))
      intent.putExtra("FRAGMENT_TYPE", 101)
      startActivity(intent)
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
        Intent(applicationContext, Class.forName("com.boost.presignin.ui.newOnboarding.NewOnBoardingContainerActivity")).
        putExtra("FRAGMENT_TYPE", 1) // 0 stands for New screen Enter Phone Number ordinal from FragmentType in Presignin constants
      }else
        Intent(applicationContext, Class.forName("com.boost.presignin.ui.intro.IntroActivity"))
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
}
