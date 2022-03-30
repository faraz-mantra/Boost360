package com.boost.presignin.ui.newOnboarding

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ItemIntroNewSlidesBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class IntroSlideItemFragment : AppBaseFragment<ItemIntroNewSlidesBinding, BaseViewModel>() {

  private var handler:Handler= Handler(Looper.getMainLooper())
  private var runnable:Runnable?=null
  private var textZoomed=false
  var onNext: (pos: Int?) -> Unit? = { _ -> }

  private val TAG = "IntroSlideItemFragment"
  private val introSlideItem by lazy {
    arguments?.getSerializable(IntentConstant.INTRO_SLIDE_ITEM.name) as IntroItemNew
  }

  private val position by lazy {
    arguments?.getInt(IntentConstant.POSITION.name)
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): IntroSlideItemFragment {
      val fragment = IntroSlideItemFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.item_intro_new_slides
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    val colorBg = ContextCompat.getColor(baseActivity, introSlideItem.slideBackgroundColor)
    binding?.relativeParentWrapperIntroItem?.setBackgroundColor(colorBg)
    binding?.lottieAnimationIntro?.setBackgroundColor(colorBg)

    binding?.lottieAnimationIntro?.apply {
      setAnimation(introSlideItem.lottieRawResource)
      repeatCount = if (!introSlideItem.isLottieRepeat) introSlideItem.count else LottieDrawable.INFINITE
      repeatMode = LottieDrawable.RESTART
    }

    binding?.tvIntroTitle?.text = introSlideItem.title
    runnable = Runnable {
      val frame = binding!!.lottieAnimationIntro.frame
      val maxFrame = binding!!.lottieAnimationIntro.maxFrame
      if (frame>=maxFrame-30&&textZoomed.not()&&position==0){
        textZoomed=true
        //textZoomAnim()
        Log.i(TAG, "onCreateView: ")
      }
      handler.postDelayed(runnable!!,100)

    }
    binding?.lottieAnimationIntro?.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {
        if (introSlideItem.isLottieRepeat) onNext(position)
      }

      override fun onAnimationEnd(animation: Animator?) {
        handler.removeCallbacks(runnable!!)
        if (!introSlideItem.isLottieRepeat) binding?.lottieAnimationIntro?.cancelAnimation()
      }

      override fun onAnimationCancel(animation: Animator?) {
        handler.removeCallbacks(runnable!!)
        if (position!=0){
          Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding?.lottieAnimationIntro?.progress=0F

            onNext(position)
          },2000)
        }else{
          binding?.lottieAnimationIntro?.progress=0F

          onNext(position)

        }


      }

      override fun onAnimationStart(animation: Animator?) {
        binding?.tvIntroTitle?.scaleX=1F
        binding?.tvIntroTitle?.scaleY=1F

        handler.removeCallbacks(runnable!!)

        textZoomed=false
        handler.postDelayed(runnable!!,100)

        Log.i("jbsjh", position.toString())
        if (position == 0) WebEngageController.trackEvent(PS_INTRO_SCREEN_START, START_INTRO_ANIMATION, NO_EVENT_VALUE)
      }

    })
  }

  fun textZoomAnim(){
    val startSize: Float = 1F // Size in pixels

    val endSize = 1.08F
    val animationDuration = 800L // Animation duration in ms


    val animator: ValueAnimator = ValueAnimator.ofFloat(startSize, endSize)
    animator.duration = animationDuration

    animator.addUpdateListener { valueAnimator ->
      val animatedValue = valueAnimator.animatedValue as Float
      binding?.tvIntroTitle?.scaleX=animatedValue
      binding?.tvIntroTitle?.scaleY=animatedValue


      if (animatedValue>=endSize){
        binding?.tvIntroTitle?.scaleX =1F
        binding?.tvIntroTitle?.scaleY =1F

      }
    }

    animator.start()
  }
  override fun onResume() {
    super.onResume()
    binding?.lottieAnimationIntro?.playAnimation()
    Log.i("scsdbcj", "skjdbsjd")
  }
}