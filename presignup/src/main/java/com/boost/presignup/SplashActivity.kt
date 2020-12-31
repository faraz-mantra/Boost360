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
import com.framework.utils.AppsFlyerUtils
import com.onboarding.nowfloats.managers.NavigatorManager
import com.smartlook.sdk.smartlook.Smartlook
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {

    var isUserLoggedIn = false
    var isSignUpComplete = false
    private var deepLinkViewType = ""
    private var deepLinkFpId = ""
    private var deepLinkFpTag = ""
    private var deepLinkDay = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (intent != null) {
            val uri = intent.data ?: null
            if (uri == null) {
                if (uri.toString().contains("onelink", true)) {
                    if (AppsFlyerUtils.sAttributionData.containsKey(DynamicLinkParams.viewType)) {
                        deepLinkViewType = AppsFlyerUtils.sAttributionData.get(DynamicLinkParams.viewType) ?: ""
                        deepLinkFpId = AppsFlyerUtils.sAttributionData.get(DynamicLinkParams.fpId) ?: ""
                        deepLinkFpTag = AppsFlyerUtils.sAttributionData.get(DynamicLinkParams.fpTag) ?: ""
                        deepLinkDay = AppsFlyerUtils.sAttributionData.get(DynamicLinkParams.day) ?: ""
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
            }
        }
        val pref: SharedPreferences = this.getSharedPreferences("nowfloatsPrefs", 0)
        isUserLoggedIn = pref.getBoolean("IsUserLoggedIn", false)
        isSignUpComplete = pref.getBoolean("IsSignUpComplete", false)

        if (isUserLoggedIn) {
            val profileId = pref.getString("user_profile_id", null)
            isUserLoggedIn = profileId != null && profileId.trim().isNotEmpty()
        }
        Smartlook.setupAndStartRecording("2976fda0fc620d3186eca19884bd55ac125b56e4")
        onCreateView()
    }

    private fun onCreateView() {
        if (isUserLoggedIn && deepLinkViewType.isNotEmpty()) {
            val intent = Intent(applicationContext, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkFpId", deepLinkFpId)
            intent.putExtra("deepLinkFpTag", deepLinkFpTag)
            intent.putExtra("deepLinkDay", deepLinkDay)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
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
//        val intent = Intent(applicationContext, Class.forName("com.dashboard.controller.DashboardActivity"))
//        startActivity(intent)
//        finish()
                when {
                    isUserLoggedIn -> {
                        val intent = Intent(applicationContext, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
                        startActivity(intent)
                        finish()
                    }
                    isSignUpComplete -> {
                        NavigatorManager.startActivities(this@SplashActivity)
                        finish()
                    }
                    else -> {
                        val mainIntent = Intent(applicationContext, PreSignUpActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
        animation_view.playAnimation()
    }

    private fun hashGeneration() { // Add code to print out the key hash
        try {
            val info: PackageInfo = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("BST_KeyHash:", ">>>>" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    override fun onDestroy() {
        animation_view?.cancelAnimation()
        super.onDestroy()
    }
}
