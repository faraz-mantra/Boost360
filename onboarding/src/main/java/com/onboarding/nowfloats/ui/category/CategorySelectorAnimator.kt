package com.onboarding.nowfloats.ui.category

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.framework.extensions.refreshLayout
import com.framework.utils.ConversionUtils
import com.framework.views.customViews.CustomCardView
import com.framework.views.customViews.CustomTextView
import com.onboarding.nowfloats.BaseBoardingApplication.Companion.instance
import com.onboarding.nowfloats.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class CategorySelectorAnimator {

    interface OnAnimationCompleteListener{
        fun onLargeLogoAnimationComplete() {}
        fun onTitleAnimationComplete() {}
        fun onSubTitleAnimationComplete() {}
    }

    private var imageRiyaLarge: WeakReference<CustomCardView?>? = null
    private var imageRiya: WeakReference<CustomCardView?>? = null
    private var motionLayout: WeakReference<MotionLayout?>? = null
    private var toolbarTitle: WeakReference<CustomTextView?>? = null
    private var subTitleForeground: WeakReference<View?>? = null

    private val typingSpeed = 15L
    private val animationDelay = 100L
    private val largeLogoAnimationDuration = 700L
    private val contentFadeInDuration = 500L
    private val accelerateInterpolator = AccelerateInterpolator()
    private var isAnimating: Boolean = true

    private var titleTypeWriter: Disposable? = null

    private val title = instance.resources.getString(R.string.what_your_business_like)

    var listener: OnAnimationCompleteListener? = null

    fun setViews(imageRiyaLarge: CustomCardView?, imageRiya: CustomCardView?,
                              motionLayout: MotionLayout?, toolbarTitle: CustomTextView?,
                              subTitleForeground: View?) {
        this.imageRiyaLarge = WeakReference(imageRiyaLarge)
        this.imageRiya = WeakReference(imageRiya)
        this.motionLayout = WeakReference(motionLayout)
        this.toolbarTitle = WeakReference(toolbarTitle)
        this.subTitleForeground = WeakReference(subTitleForeground)
    }

    fun startAnimation(){
        isAnimating = animateLargeLogo()
        if (!isAnimating) {
            onLargeLogoAnimationCompleted()
        }
    }

    /**
     * return true if the animation have started else false that the animation cannot be performed
     */
    private fun animateLargeLogo(): Boolean {
        val imageRiyaLargeParams = (imageRiyaLarge?.get()?.layoutParams
                as? FrameLayout.LayoutParams) ?: return false

        val imageRiyaParams = (imageRiya?.get()?.layoutParams
                as? ConstraintLayout.LayoutParams) ?: return false

        val (heightAnimator, widthAnimator) =
                largeLogoSizeAnimators(imageRiyaParams, imageRiyaLargeParams)

        val (marginTopAnimator, marginStartAnimator) =
                largeLogoPositionAnimators(imageRiyaLargeParams)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(listOf(heightAnimator, widthAnimator,
                marginTopAnimator, marginStartAnimator))
        animatorSet.interpolator = accelerateInterpolator
        animatorSet.duration = largeLogoAnimationDuration
        animatorSet.startDelay = animationDelay
        animatorSet.start()

        val alphaAnimator = motionLayout?.get()?.animate()
        alphaAnimator?.alpha(1f)
        alphaAnimator?.duration = contentFadeInDuration
        alphaAnimator?.startDelay = largeLogoAnimationDuration + animationDelay
        alphaAnimator?.setUpdateListener {
            if (it.animatedFraction == 1f) {
                listener?.onLargeLogoAnimationComplete()
                onLargeLogoAnimationCompleted()
            }
        }
        alphaAnimator?.interpolator = accelerateInterpolator
        alphaAnimator?.start()
        return true
    }

    private fun onLargeLogoAnimationCompleted() {
        imageRiyaLarge?.get()?.alpha = 0f
        startTitleTypeWriter()
    }

    private fun largeLogoPositionAnimators(imageRiyaLargeParams: FrameLayout.LayoutParams): Pair<ValueAnimator, ValueAnimator> {
        val marginTopAnimator = ValueAnimator.ofInt(imageRiyaLargeParams.topMargin,
                ConversionUtils.dp2px(68f))
        marginTopAnimator.addUpdateListener {
            imageRiyaLargeParams.topMargin = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }

        val marginStartAnimator = ValueAnimator.ofInt(imageRiyaLargeParams.marginStart,
                ConversionUtils.dp2px(32f))
        marginStartAnimator.addUpdateListener {
            imageRiyaLargeParams.marginStart = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }
        return Pair(marginTopAnimator, marginStartAnimator)
    }

    private fun largeLogoSizeAnimators(imageRiyaParams: ConstraintLayout.LayoutParams, imageRiyaLargeParams: FrameLayout.LayoutParams): Pair<ValueAnimator, ValueAnimator> {
        val heightAnimator = ValueAnimator.ofInt(imageRiyaLarge?.get()?.measuredHeight
                ?: 0, imageRiyaParams.height)

        heightAnimator.addUpdateListener {
            imageRiyaLargeParams.height = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }

        val widthAnimator = ValueAnimator.ofInt(imageRiyaLarge?.get()?.measuredWidth
                ?: 0, imageRiyaParams.width)
        widthAnimator.addUpdateListener {
            imageRiyaLargeParams.width = it.animatedValue as Int
            imageRiyaLarge?.get()?.layoutParams = imageRiyaLargeParams
            imageRiyaLarge?.get()?.refreshLayout()
        }
        return Pair(heightAnimator, widthAnimator)
    }

    private fun startTitleTypeWriter() {
        titleTypeWriter = Observable.range(0, title.length)
                .concatMap { Observable.just(it).delay(typingSpeed, TimeUnit.MILLISECONDS) }
                .map { title[it].toString() }.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    listener?.onTitleAnimationComplete()
                    onTitleTypingAnimationCompleted()
                }
                .subscribe { char ->
                    toolbarTitle?.get()?.text = toolbarTitle?.get()?.text?.toString()?.plus(char)
                }
    }

    private fun onTitleTypingAnimationCompleted() {
        val subtitleAnimator = subTitleForeground?.get()?.animate()
        subtitleAnimator?.alpha(0f)
        subtitleAnimator?.duration = contentFadeInDuration
        subtitleAnimator?.setUpdateListener {
            if (it.animatedFraction == 1f) {
                isAnimating = false
                listener?.onSubTitleAnimationComplete()
            }
        }
        subtitleAnimator?.interpolator = accelerateInterpolator
        subtitleAnimator?.start()
    }

    fun isAnimating(): Boolean {
        return isAnimating
    }

    fun getObservables(): List<Disposable?> {
        return listOf(titleTypeWriter)
    }
}