package com.boost.presignin.holder.common

import android.animation.Animator
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.ItemIntroNewSlidesBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.jakewharton.rxbinding2.view.RxMenuItem.visible

class IntroSlidesViewHolder constructor(binding: ItemIntroNewSlidesBinding) : AppBaseRecyclerViewHolder<ItemIntroNewSlidesBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model = item as? IntroItemNew ?: return
    val colorBg = ContextCompat.getColor(itemView.context, model.slideBackgroundColor)
    binding.relativeParentWrapperIntroItem.setBackgroundColor(colorBg)
    binding.lottieAnimationIntro.setBackgroundColor(colorBg)

    binding.lottieAnimationIntro.apply {
      setAnimation(model.lottieRawResource)
      repeatCount = if (!model.isLottieRepeat) 1 else LottieDrawable.INFINITE
      repeatMode = LottieDrawable.RESTART
      playAnimation()
    }
    binding.tvIntroTitle.text = model.title
    binding.lottieAnimationIntro.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {
        onItemClick(position, item)
      }

      override fun onAnimationEnd(animation: Animator?) {
        if (!model.isLottieRepeat) binding.lottieAnimationIntro.cancelAnimation()
        onItemClick(position, item)
      }

      override fun onAnimationCancel(animation: Animator?) {
      }

      override fun onAnimationStart(animation: Animator?) {
      }

    })
  }

  private fun onItemClick(position: Int, item: BaseRecyclerViewItem) {
    listener?.onItemClick(position, item, RecyclerViewActionType.INTRO_LOTTIE_ANIMATION_COMPLETE_INVOKE.ordinal)
  }
}