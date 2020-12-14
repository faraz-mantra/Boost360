package com.dashboard.holder

import android.view.View
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemContentSetupManageBinding
import com.dashboard.model.live.SiteMeterModel
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class ItemContentSetupHolder(binding: ItemContentSetupManageBinding) : AppBaseRecyclerViewHolder<ItemContentSetupManageBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? SiteMeterModel ?: return
    binding.txtTitle.text = data.Title
    binding.txtSubtitle.text = data.Desc
    binding.imgOkCircle.setImageResource(data.getIcon())
    if (data.status == true) {
      binding.imgArrowIcon.visible()
      binding.imgArrowGif.gone()
      binding.imgArrowGif.pause()
      getColor(R.color.warm_light)?.let { binding.txtTitle.setTextColor(it) }
    } else {
      binding.imgArrowIcon.gone()
      binding.imgArrowGif.visible()
      binding.imgArrowGif.play()
      getColor(R.color.colorAccent)?.let { binding.txtTitle.setTextColor(it) }
    }
    binding.view2.visibility = if (itemCount != null && position == (itemCount!! - 1)) View.INVISIBLE else View.VISIBLE
    binding.mainContent.setOnClickListener {
      if (data.status == false) listener?.onItemClick(position, data, RecyclerViewActionType.DIGITAL_SCORE_READINESS_CLICK.ordinal)
    }
  }
}

