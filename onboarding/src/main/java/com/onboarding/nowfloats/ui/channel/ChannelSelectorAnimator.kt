package com.onboarding.nowfloats.ui.channel

import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.onboarding.nowfloats.extensions.fadeOut
import java.lang.ref.WeakReference

class ChannelSelectorAnimator {

  interface OnAnimationCompleteListener {
    fun onAnimationComplete() {}
  }

  private var motionLayout: WeakReference<MotionLayout?>? = null
  private var imageView: WeakReference<View?>? = null
  private var titleForeground: WeakReference<View?>? = null
  private var subTitleForeground: WeakReference<View?>? = null
  private var isAnimating: Boolean = true
  var listener: OnAnimationCompleteListener? = null

  fun setViews(
    motionLayout: MotionLayout?, imageView: View?,
    titleForeground: View?, subTitleForeground: View?
  ) {
    this.motionLayout = WeakReference(motionLayout)
    this.imageView = WeakReference(imageView)
    this.titleForeground = WeakReference(titleForeground)
    this.subTitleForeground = WeakReference(subTitleForeground)
  }

  fun startAnimation() {
    this.imageView?.get()?.fadeOut(200L)?.andThen(titleForeground?.get()?.fadeOut())
      ?.andThen(subTitleForeground?.get()?.fadeOut(200L))?.doOnComplete {
      isAnimating = false
      listener?.onAnimationComplete()
    }?.subscribe()
  }


  fun isAnimating(): Boolean {
    return isAnimating
  }
}