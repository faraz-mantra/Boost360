package com.inventoryorder.holders

import com.framework.extensions.gone
import com.framework.extensions.isVisible
import com.framework.extensions.visible
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemFaqBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.ui.tutorials.model.ContentsItem

class FaqViewHolder(binding: ItemFaqBinding) : AppBaseRecyclerViewHolder<ItemFaqBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val contentsItem = item as ContentsItem
    binding.ctvQuestion.text = contentsItem.question
    binding.ctvAnswer.text = contentsItem.answer
    binding.ctvQuestionNo.text = "Q${position + 1}."
    binding.root.setOnClickListener {
      when (binding.ctvAnswer.isVisible()) {
        true -> {
          binding.civExpand.setImageResource(R.drawable.ic_arrow_up)
          binding.ctvAnswer.gone()
        }
        else -> {
          binding.civExpand.setImageResource(R.drawable.ic_arrow_down_orange)
          binding.ctvAnswer.visible()

        }
      }
    }
  }
}