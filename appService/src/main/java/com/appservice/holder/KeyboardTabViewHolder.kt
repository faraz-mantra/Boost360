package com.appservice.holder

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemKeyboardTabBinding
import com.appservice.model.keyboard.KeyboardActionItem
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class KeyboardTabViewHolder(binding: ItemKeyboardTabBinding) : AppBaseRecyclerViewHolder<ItemKeyboardTabBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? KeyboardActionItem) ?: return
    binding.title.text = data.title
    binding.desc.text = data.description
    setStatusView(data.getColorActive(), data.getEyeActive())
    binding.btnEye.setOnClickListener {
      if (data.isPremiumKeyAvailable() && data.isPremium().not()) return@setOnClickListener
      listener?.onItemClick(position, data, RecyclerViewActionType.KEYBOARD_TAB_CLICK.ordinal)
    }
  }

  private fun setStatusView(@ColorRes color: Int, @DrawableRes icEyeActive: Int) {
    getColor(color)?.let { binding.title.setTextColor(it) }
    getColor(color)?.let { binding.desc.setTextColor(it) }
    binding.btnEye.setImageResource(icEyeActive)
  }
}
