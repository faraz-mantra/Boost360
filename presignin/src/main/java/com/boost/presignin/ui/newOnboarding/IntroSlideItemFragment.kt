package com.boost.presignin.ui.newOnboarding

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.databinding.ItemIntroNewSlidesBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.framework.models.BaseViewModel

class IntroSlideItemFragment : AppBaseFragment<ItemIntroNewSlidesBinding, BaseViewModel>() {

  var onNext: (pos: Int?) -> Unit? = { _ -> }

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
      playAnimation()
    }
    binding?.tvIntroTitle?.text = introSlideItem.title
    binding?.lottieAnimationIntro?.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {
        if (introSlideItem.isLottieRepeat) onNext(position)
      }

      override fun onAnimationEnd(animation: Animator?) {
        if (!introSlideItem.isLottieRepeat) binding?.lottieAnimationIntro?.cancelAnimation()
      }

      override fun onAnimationCancel(animation: Animator?) {
        onNext(position)
      }

      override fun onAnimationStart(animation: Animator?) {
      }

    })
  }

  override fun onResume() {
    super.onResume()
    //binding?.lottieAnimationIntro?.playAnimation()
    Log.i("scsdbcj", "skjdbsjd")
  }
}