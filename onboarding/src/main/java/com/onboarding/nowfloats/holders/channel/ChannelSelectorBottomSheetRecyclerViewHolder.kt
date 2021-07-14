package com.onboarding.nowfloats.holders.channel

import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.underlineText
import com.framework.extensions.visible
import com.framework.utils.ConversionUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewActionType
import com.onboarding.nowfloats.databinding.ItemChannelBottomSheetBinding
import com.onboarding.nowfloats.model.channel.*
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ChannelSelectorBottomSheetRecyclerViewHolder constructor(binding: ItemChannelBottomSheetBinding) :
  AppBaseRecyclerViewHolder<ItemChannelBottomSheetBinding>(binding) {

  private var model: ChannelModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ChannelModel
    setViews(model)
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding.card -> listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.CHANNEL_ITEM_CLICKED.ordinal
      )
      binding.learnMoreLink,
      binding.whyLinkClick -> listener?.onItemClick(
        adapterPosition,
        model,
        RecyclerViewActionType.CHANNEL_ITEM_WHY_CLICKED.ordinal
      )
    }
  }

  private fun setViews(model: ChannelModel?) {
    setClickListeners(binding.card, binding.whyLinkClick, binding.learnMoreLink)
    binding.title.text = model?.getName()
    setLinkAndPriority(model)
    setSelection(model)
    setImage(model)
  }

  private fun setLinkAndPriority(model: ChannelModel?) {
    binding.whyLink.underlineText(1, binding.whyLink.text.length - 2)
    binding.learnMoreLink.underlineText(0, binding.learnMoreLink.text.length - 1)

    if (model?.getPriority() != ChannelPriority.LEARN_MORE) {
      binding.learnMoreLink.gone()
      binding.whyLinkClick.visible()
      binding.whyText.text = model?.getPriority()?.name?.toLowerCase()?.capitalize()
    } else {
      binding.whyLinkClick.gone()
      binding.learnMoreLink.visible()
    }
  }

  private fun setSelection(model: ChannelModel?) {
    setCardSelection(model?.isSelected ?: false)
  }

  private fun setImage(model: ChannelModel?) {
    binding.image.setImageDrawable(model?.getDrawable(activity))
  }

  private fun setCardSelection(isSelected: Boolean) {
    if (isSelected) {
      getColor(R.color.aliceblue_10)?.let { binding.card.setCardBackgroundColor(it) }
      binding.card.cardElevation = 0F
      binding.check.setImageResource(takeIf { adapterPosition == 0 }?.let { R.drawable.ic_check_permanent }
        ?: R.drawable.ic_check_blue)
    } else {
      getColor(R.color.white)?.let { binding.card.setCardBackgroundColor(it) }
      binding.card.cardElevation = ConversionUtils.dp2px(1.0f).toFloat()
      binding.check.setImageResource(R.drawable.ic_unselected)
    }
  }
}