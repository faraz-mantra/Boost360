package com.onboarding.nowfloats.ui.channel

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.framework.extensions.refreshLayout
import com.framework.views.customViews.CustomButton
import com.framework.views.customViews.CustomCardView
import com.framework.views.customViews.CustomTextView
import java.lang.ref.WeakReference

class ChannelConfirmDialogAnimator {

    private val animationDelay = 70L
    private val largeLogoAnimationDuration = 500L
    private val logoFadeInDuration = 120L
    private val titleFadeInDuration = 120L
    private val descFadeInDuration = 120L
    private val confirmFadeInDuration = 120L
    private val accelerateInterpolator = AccelerateInterpolator()

    private var imageRiyaLarge: WeakReference<LinearLayoutCompat?>? = null
    private var imageRiya: WeakReference<CustomCardView?>? = null

    private var title: WeakReference<CustomTextView?>? = null
    private var desc: WeakReference<CustomTextView?>? = null
    private var confirm: WeakReference<CustomButton?>? = null

    fun setViews(
            imageRiyaLarge: LinearLayoutCompat?, imageRiya: CustomCardView?,
            title: CustomTextView?, desc: CustomTextView?, confirm: CustomButton?
    ) {
        this.imageRiyaLarge = WeakReference(imageRiyaLarge)
        this.imageRiya = WeakReference(imageRiya)
        this.title = WeakReference(title)
        this.desc = WeakReference(desc)
        this.confirm = WeakReference(confirm)
    }

    fun startAnimation() {
        val imageRiyaParams =
                imageRiya?.get()?.layoutParams as? ConstraintLayout.LayoutParams ?: return
        val imageRiyaLargeParams =
                imageRiyaLarge?.get()?.layoutParams as? FrameLayout.LayoutParams ?: return

        val (heightAnimator, widthAnimator) =
                largeLogoSizeAnimators(imageRiyaParams, imageRiyaLargeParams)

        val largeLogoPositionAnimators = largeLogoPositionAnimators(imageRiyaLargeParams)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(
                listOf(heightAnimator, widthAnimator, largeLogoPositionAnimators)
        )
        animatorSet.interpolator = accelerateInterpolator
        animatorSet.duration = largeLogoAnimationDuration
        animatorSet.startDelay = animationDelay
        animatorSet.start()
    }

    private fun largeLogoPositionAnimators(imageRiyaLargeParams: FrameLayout.LayoutParams): ValueAnimator {
        val marginTopAnimator = ValueAnimator.ofInt((imageRiyaLargeParams.height) / 2, 0)
        marginTopAnimator.addUpdateListener {
            imageRiyaLargeParams.topMargin = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
            if (imageRiyaLargeParams.topMargin == 0) fadeInSmallLogoCard()
        }
        return marginTopAnimator
    }

    private fun fadeInSmallLogoCard() {
        imageRiya?.get()?.animate()?.alpha(1f)?.setDuration(logoFadeInDuration)?.start()
        imageRiyaLarge?.get()?.animate()?.setStartDelay(logoFadeInDuration)?.alpha(0f)?.start()

        title?.get()?.animate()?.alpha(1f)?.setStartDelay(logoFadeInDuration)
                ?.setDuration(titleFadeInDuration)?.start()
        desc?.get()?.animate()?.alpha(1f)?.setStartDelay(logoFadeInDuration + titleFadeInDuration)
                ?.setDuration(descFadeInDuration)?.start()
        confirm?.get()?.animate()?.alpha(1f)
                ?.setStartDelay(logoFadeInDuration + titleFadeInDuration + descFadeInDuration)
                ?.setDuration(confirmFadeInDuration)?.start()
    }

    private fun largeLogoSizeAnimators(imageRiyaParams: ConstraintLayout.LayoutParams, imageRiyaLargeParams: FrameLayout.LayoutParams
    ): Pair<ValueAnimator, ValueAnimator> {
        val heightAnimator = ValueAnimator.ofInt((imageRiyaLarge?.get()?.measuredHeight?.minus(20))
                ?: 0,
                imageRiyaParams.height - 20)

        heightAnimator.addUpdateListener {
            imageRiyaLargeParams.height = it.animatedValue as Int
//            imageRiyaLarge?.get()?.radius = imageRiyaLargeParams.height.div(2).toFloat()
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }

        val widthAnimator = ValueAnimator.ofInt((imageRiyaLarge?.get()?.measuredWidth?.minus(20))
                ?: 0,
                imageRiyaParams.width - 20)
        widthAnimator.addUpdateListener {
            imageRiyaLargeParams.width = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }
        return Pair(heightAnimator, widthAnimator)
    }
}
