package com.boost.presignin.holder.common

import com.boost.presignin.databinding.ItemIntroNewSlidesBinding
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem

class IntroSlidesViewHolder constructor(binding: ItemIntroNewSlidesBinding) :
    AppBaseRecyclerViewHolder<ItemIntroNewSlidesBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val model = item as IntroItemNew

        binding.ivIntro.setImageResource(model.imageResource!!)
        binding.tvIntroTitle.text = model.title
    }
}