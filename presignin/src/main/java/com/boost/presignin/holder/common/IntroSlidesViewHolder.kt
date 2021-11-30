package com.boost.presignin.holder.common

import android.animation.Animator
import com.airbnb.lottie.LottieDrawable
import com.boost.presignin.databinding.ItemIntroNewSlidesBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.jakewharton.rxbinding2.view.RxMenuItem.visible

class IntroSlidesViewHolder constructor(binding: ItemIntroNewSlidesBinding) :
    AppBaseRecyclerViewHolder<ItemIntroNewSlidesBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val model = item as IntroItemNew

        binding.relativeParentWrapperIntroItem.setBackgroundColor(model.slideBackgroundColor!!)

        binding.ivIntro.apply {
            if (position == 1 /*|| position == 3*/) gone() else {
                setImageResource(model.imageResource!!)
                visible()
            }
        }
        binding.lottieAnimationIntro.apply {
            if (position == 1 /*|| position == 3*/) {
                binding.lottieAnimationIntro.setAnimation(model.lottieRawResource!!)
                binding.lottieAnimationIntro.repeatCount =
                    if (model.isLottieRepeat!!) LottieDrawable.INFINITE else 1
                binding.lottieAnimationIntro.repeatMode = LottieDrawable.RESTART
                binding.lottieAnimationIntro.playAnimation()
                visible()
            } else
                gone()
        }

        binding.tvIntroTitle.text = model.title

        binding.lottieAnimationIntro.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                binding.lottieAnimationIntro.cancelAnimation()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })
    }
}