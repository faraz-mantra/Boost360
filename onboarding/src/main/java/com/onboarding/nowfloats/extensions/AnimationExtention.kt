package com.onboarding.nowfloats.extensions

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.onboarding.nowfloats.BaseBoardingApplication.Companion.instance
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

fun View.fadeIn(duration: Long = 500L, alpha: Float = 1F, color: Int? = null): Completable {
  val animationSubject = CompletableSubject.create()
  return animationSubject.doOnSubscribe {
    ViewCompat.animate(this)
      .setDuration(duration)
      .alpha(alpha)
      .withEndAction {
        color?.let {
          this.setBackgroundColor(
            ResourcesCompat.getColor(
              instance.resources,
              color,
              instance.theme
            )
          )
        }
        animationSubject.onComplete()
      }
  }
}


fun View.fadeOut(duration: Long = 500L, alpha: Float = 0F, color: Int? = null): Completable {
  return this.fadeIn(duration, alpha, color)
}

fun fadeInTogether(first: View, second: View): Completable {
  return first.fadeIn().mergeWith(second.fadeIn())
}

fun tick(delaySecond: Int = 5): Completable {
  return Completable.timer(delaySecond.toLong(), TimeUnit.SECONDS)

}

fun View.playRightInAnimation() {
  val inFromRight: Animation = TranslateAnimation(
    Animation.RELATIVE_TO_PARENT, +1.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f
  )
  inFromRight.duration = 500
  inFromRight.interpolator = AccelerateInterpolator()
  this.startAnimation(inFromRight)
}

fun View.playLeftInAnimation() {
  val inFromLeft: Animation = TranslateAnimation(
    Animation.RELATIVE_TO_PARENT, -1.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f,
    Animation.RELATIVE_TO_PARENT, 0.0f
  )
  inFromLeft.duration = 500
  inFromLeft.interpolator = AccelerateInterpolator()
  this.startAnimation(inFromLeft)
}
