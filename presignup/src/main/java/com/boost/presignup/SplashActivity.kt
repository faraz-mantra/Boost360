package com.boost.presignup

import android.animation.Animator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initLottieAnimation()
    }

    private fun initLottieAnimation() {
        animation_view.setAnimation(R.raw.boost_lottie)
        animation_view.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                Log.d("onAnimationRepeat", "")
            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.d("onAnimationEnd", "")
                val mainIntent = Intent(applicationContext, PreSignUpActivity::class.java)
                startActivity(mainIntent)
                finish()
            }

            override fun onAnimationCancel(animation: Animator?) {
                Log.d("onAnimationCancel", "")
            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d("onAnimationStart", "")
            }

        })
        animation_view.playAnimation()
    }
}
