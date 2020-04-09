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
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : AppCompatActivity() {

    var isUserLoggedIn = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initLottieAnimation()
        val pref: SharedPreferences = this.getSharedPreferences( "nowfloatsPrefs", 0)
        isUserLoggedIn = pref.getBoolean("IsUserLoggedIn", false)

        if(isUserLoggedIn){
            val profileId = pref.getString("user_profile_id", null)
            isUserLoggedIn = profileId != null && profileId.trim().isNotEmpty()
        }

        if(BuildConfig.DEBUG){
            hashGeneration()
        }
    }

    private fun initLottieAnimation() {
        animation_view.setAnimation(R.raw.boost_lottie2)
        animation_view.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
//                Log.d("onAnimationRepeat", "")
            }

            override fun onAnimationEnd(animation: Animator?) {
//                Log.d("onAnimationEnd", "")

                if(isUserLoggedIn){
                    val intent = Intent(applicationContext, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
                    startActivity(intent)
                    finish()
                } else {
                    val mainIntent = Intent(applicationContext, PreSignUpActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
//                Log.d("onAnimationCancel", "")
            }

            override fun onAnimationStart(animation: Animator?) {
//                Log.d("onAnimationStart", "")
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
}
