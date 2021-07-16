package com.dashboard.controller.ui.more.holder

import android.graphics.Color
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.RecyclerItemUsefulLinksBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.visible


class UsefulLinksHolder(binding: RecyclerItemUsefulLinksBinding) : AppBaseRecyclerViewHolder<RecyclerItemUsefulLinksBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? UsefulLinksItem
    binding.ctvHeading.text = data?.title
    binding.ctvSubheading.text = data?.subtitle
    if (data?.isActionButtonEnabled == true) {
      binding.ctvNeedsAction.visible()
      binding.ctvNeedsAction.text = data.actionBtn?.title
      if (data.actionBtn?.color.isNullOrEmpty().not()&&data.actionBtn?.textColor.isNullOrEmpty().not()){
      binding.ctvNeedsAction.setTextColor(Color.parseColor(data.actionBtn?.textColor))
      binding.ctvNeedsAction.setBackgroundColor(Color.parseColor(data.actionBtn?.color))}
    } else {
      binding.ctvNeedsAction.invisible()
    }
    binding.rivIcon.setImageResource(activity?.resources?.getIdentifier(data?.icon, "drawable", activity?.packageName)!!)

  }

}