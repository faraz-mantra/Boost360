package com.dashboard.holder

import android.view.View
import com.dashboard.R
import com.dashboard.databinding.ItemContentSetupManageBinding
import com.dashboard.model.AllBusinessData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class ItemContentSetupHolder(binding: ItemContentSetupManageBinding) : AppBaseRecyclerViewHolder<ItemContentSetupManageBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AllBusinessData ?: return
    binding.txtTitle.text = data.title
    binding.txtSubtitle.text = data.subTitle
    data.icon1?.let { binding.imgOkCircle.setImageResource(it) }
    if (data.isDone == true) {
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
    binding.view2.visibility = if (data.isLast == true) View.INVISIBLE else View.VISIBLE
  }
}

