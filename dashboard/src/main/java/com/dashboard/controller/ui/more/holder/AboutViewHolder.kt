package com.dashboard.controller.ui.more.holder

import android.content.res.ColorStateList
import android.graphics.Color
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.more.model.AboutAppSectionItem
import com.dashboard.controller.ui.more.model.UsefulLinksItem
import com.dashboard.databinding.ItemBoostAddOnsBinding
import com.dashboard.databinding.RecyclerItemAboutAppBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class AboutViewHolder(binding: RecyclerItemAboutAppBinding)
  :AppBaseRecyclerViewHolder<RecyclerItemAboutAppBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AboutAppSectionItem
    if (data?.view_type_2!=null){ binding.ctvViewType2.visible()
      binding.recyclerItemAbout.gone()
    }
      else{ binding.ctvViewType2.gone()
      binding.recyclerItemAbout.visible()
      binding.ctvHeading.text = data?.title
      binding.ctvSubtitle.text = data?.subtitle
      if (data?.view_type_2!=null){ binding.ctvViewType2.visible() }else binding.ctvViewType2.gone()
      val iconType = data?.icon?.let { AboutAppSectionItem.IconType.fromName(it) }
      iconType?.let { binding.civImage.setImageResource(iconType.icon) }
      binding.root.setOnClickListener { listener?.onItemClick(position,item,RecyclerViewActionType.ABOUT_VIEW_CLICK.ordinal) }
      if (data?.actionBtn!=null){
        binding.ctvNeedsAction.visible()
         binding.ctvNeedsAction.text = data.actionBtn?.title
          binding.ctvNeedsAction.setTextColor(Color.parseColor(data.actionBtn?.textColor))
          binding.ctvNeedsAction.backgroundTintList = ColorStateList.valueOf((Color.parseColor(data.actionBtn?.color)))
      }else{
        binding.ctvNeedsAction.gone()
      }
    }

  }

}
