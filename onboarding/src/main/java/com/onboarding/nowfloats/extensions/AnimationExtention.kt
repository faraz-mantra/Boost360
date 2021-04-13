package com.onboarding.nowfloats.extensions

import android.view.View
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
            this.setBackgroundColor(ResourcesCompat.getColor(instance.resources, color, instance.theme))
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
