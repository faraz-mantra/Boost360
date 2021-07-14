package com.dashboard.extension

import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.dashboard.AppDashboardApplication.Companion.instance
import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject

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
